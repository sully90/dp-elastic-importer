package com.github.onsdigitial.elastic.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.elasticutils.client.bulk.configuration.BulkProcessorConfiguration;
import com.github.onsdigital.elasticutils.client.bulk.options.BulkProcessingOptions;
import com.github.onsdigital.elasticutils.util.ElasticSearchHelper;
import com.github.onsdigitial.elastic.importer.elasticsearch.OpenNlpSearchClient;
import com.github.onsdigitial.elastic.importer.models.Article;
import com.github.onsdigitial.elastic.importer.models.Bulletin;
import com.github.onsdigitial.elastic.importer.models.Page;
import com.github.onsdigitial.elastic.importer.util.Configuration;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sullid (David Sullivan) on 30/11/2017
 * @project dp-elastic-importer
 */
public class App implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String HOSTNAME = "localhost";
    private Map<String, List<Page>> pages;

    private final OpenNlpSearchClient<Page> searchClient;

    private static final List<String> allowedStrings = new ArrayList<String>() {{
            add("articles");
            add("bulletins");
    }};

    private final String dataDirectory;

    public App() throws UnknownHostException {
        this.dataDirectory = Configuration.getProperty("data.directory");

//        SimpleRestClient client = ElasticSearchHelper.getRestClientWithTimeout(HOSTNAME, PORT,
//                1000, 60000, 60000);
        TransportClient client = ElasticSearchHelper.getTransportClient(HOSTNAME, ElasticSearchHelper.DEFAULT_TCP_PORT);
        this.searchClient = new OpenNlpSearchClient<>(client, getConfiguration());
        this.pages = new HashMap<>();
    }

    private static BulkProcessorConfiguration getConfiguration() {
        BulkProcessorConfiguration bulkProcessorConfiguration = new BulkProcessorConfiguration(BulkProcessingOptions.builder()
                .setBulkActions(50)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(8)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(1000), 5))
                .build());
        return bulkProcessorConfiguration;
    }

    public File[] subDirectories() {
        return this.subDirectories(this.dataDirectory);
    }

    public File[] subDirectories(String currentDirectory) {
        return new File(currentDirectory).listFiles(File::isDirectory);
    }

    public void indexDirectory(String directory) {
        File[] subDirectories = this.subDirectories(directory);
        if (subDirectories.length > 0) {
            for (File subDirectory : subDirectories) {
                this.indexDirectory(subDirectory.getAbsolutePath());
            }
        } else {
            // We are at the root level, so process the json
            readAndIndexJson(directory);
        }
    }

    private void readAndIndexJson(String directory) {
        String fileName = new StringBuilder(directory).append("/").append("data.json").toString();
        File file = new File(fileName);
        boolean accept = false;
        if (file.isFile()) {
            for (String allowed : allowedStrings) {
                if (file.getAbsolutePath().contains(allowed)) {
                    accept = true;
                    break;
                }
            }
        }
        if (accept) {
//            String indexName = this.getIndexFromDirectoryName(directory);
            LOGGER.info("File: " + fileName);
            try {
                Map<String, Object> dataMap = MAPPER.readValue(file, Map.class);
                if (dataMap.containsKey("type") && dataMap.get("type") instanceof String) {
                    String type = (String) dataMap.get("type");
                    Page page = null;
                    switch (type) {
                        case "article":
                            page = MAPPER.readValue(file, Article.class);
                            break;
                        case "bulletin":
                            page = MAPPER.readValue(file, Bulletin.class);
                            break;
                        default:
                            LOGGER.debug("Unknown type: " + type);
                            break;
                    }
                    if (page != null) {
                        if (!this.pages.containsKey(type)) {
                            this.pages.put(type, new ArrayList<Page>());
                        }
                        this.pages.get(type).add(page);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    private String getIndexFromDirectoryName(String directory) {
        String extension = directory.replace(this.dataDirectory, "");
        String[] parts = extension.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (allowedStrings.contains(part)) {
                break;
            } else {
                sb.append(part);
                if (i < parts.length - 1 && !allowedStrings.contains(parts[i+1])) {
                    sb.append("_");
                }
            }
        }
        return sb.toString();
    }

    public void run() throws IOException {
        // Wipe the index and reindex
        Settings settings = ElasticSearchHelper.loadSettingsFromFile("/search/", "index-config.yml");
        Map<String, Object> mapping = ElasticSearchHelper.loadMappingFromFile("/search", "default-document-mapping.json");

        this.indexDirectory(this.dataDirectory);

        for (String index : this.pages.keySet()) {
            if (this.searchClient.indexExists(index)) {
                this.searchClient.dropIndex(index);
            }
            this.searchClient.createIndex(index, settings, mapping);
//            this.searchClient.createIndex(index, Settings.EMPTY, Collections.emptyMap());
        }
    }

    public void index() {
        for (String index : this.pages.keySet()) {
            LOGGER.info(String.format("Indexing %d documents into index: %s", this.pages.get(index).size(), index));
            this.searchClient.bulk(index, this.pages.get(index));
        }
    }

    public Map<String, List<Page>> getPages() {
        return pages;
    }

    public synchronized boolean awaitClose(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return this.searchClient.awaitClose(timeout, timeUnit);
    }

    @Override
    public void close() throws Exception {
        this.searchClient.shutdown();
    }

    public static void main(String[] args) {
        try (App app = new App()) {
            app.run();
            long startTime = System.currentTimeMillis();
            app.index();
            LOGGER.info("Bulk request complete. Awaiting close...");
            app.awaitClose(5, TimeUnit.MINUTES);
            LOGGER.info("Done");
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);
            System.out.format("Milli = %s, ( S_Start : %s, S_End : %s ) \n", duration, startTime, endTime );
            System.out.println("Human-Readable format : "+ millisToShortDHMS( duration ) );

            int numDocumentsIndexed = 0;
            Map<String, List<Page>> pages = app.getPages();
            for (String key : pages.keySet()) {
                numDocumentsIndexed += pages.get(key).size();
            }
            float timePerDocument = new Long(duration).floatValue() / (float) numDocumentsIndexed;
            System.out.println("Time per document : "+ timePerDocument / 1000.0f );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String millisToShortDHMS(long duration) {
        String res = "";    // java.util.concurrent.TimeUnit;
        long days       = TimeUnit.MILLISECONDS.toDays(duration);
        long hours      = TimeUnit.MILLISECONDS.toHours(duration) -
                TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes    = TimeUnit.MILLISECONDS.toMinutes(duration) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds    = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        long millis     = TimeUnit.MILLISECONDS.toMillis(duration) -
                TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(duration));

        if (days == 0)      res = String.format("%02d:%02d:%02d.%04d", hours, minutes, seconds, millis);
        else                res = String.format("%dd %02d:%02d:%02d.%04d", days, hours, minutes, seconds, millis);
        return res;
    }
}
