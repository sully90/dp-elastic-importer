package com.github.onsdigitial.elastic.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.elasticutils.client.bulk.configuration.BulkProcessorConfiguration;
import com.github.onsdigital.elasticutils.client.bulk.options.BulkProcessingOptions;
import com.github.onsdigital.elasticutils.client.http.SimpleRestClient;
import com.github.onsdigital.elasticutils.util.ElasticSearchHelper;
import com.github.onsdigitial.elastic.importer.elasticsearch.OpenNlpSearchClient;
import com.github.onsdigitial.elastic.importer.models.Article;
import com.github.onsdigitial.elastic.importer.models.Page;
import com.github.onsdigitial.elastic.importer.util.Configuration;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private static final String INDEX = "test";
    private List<Page> articles;

    private final OpenNlpSearchClient<Page> searchClient;

    private static final List<String> allowedStrings = new ArrayList<String>() {{
        add("articles");
//            add("bulletins");
    }};

    private final String dataDirectory;

    public App() {
        this.dataDirectory = Configuration.getProperty("data.directory");

        SimpleRestClient client = ElasticSearchHelper.getRestClientWithTimeout("localhost", 9200,
                10000, 200000, 200000);
        this.searchClient = new OpenNlpSearchClient<>(client, getConfiguration());
        this.articles = new ArrayList<>();
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
            String indexName = this.getIndexFromDirectoryName(directory);
            LOGGER.info("Index: " + indexName + " File: " + fileName);
            try {
                Article article = MAPPER.readValue(file, Article.class);
                this.articles.add(article);
            } catch (IOException e) {
                e.printStackTrace();
                return;
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

    public void index() {
        this.searchClient.bulk(INDEX, this.articles);
    }

    public void run() throws IOException {
        // Wipe the index and reindex
        Settings settings = ElasticSearchHelper.loadSettingsFromFile("/search/", "index-config.yml");
        Map<String, Object> mapping = ElasticSearchHelper.loadMappingFromFile("/search", "default-document-mapping.json");

        this.searchClient.dropIndex(INDEX);
        this.searchClient.createIndex(INDEX, settings, mapping);
        this.indexDirectory(this.dataDirectory);
    }

    public synchronized boolean awaitClose(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return this.searchClient.awaitClose(timeout, timeUnit);
    }

    @Override
    public void close() throws Exception {
        searchClient.shutdown();
    }

    private static BulkProcessorConfiguration getConfiguration() {
        BulkProcessorConfiguration bulkProcessorConfiguration = new BulkProcessorConfiguration(BulkProcessingOptions.builder()
                .setBulkActions(10)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(4)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build());
        return bulkProcessorConfiguration;
    }

    public static void main(String[] args) {
        try (App app = new App()) {
            app.run();
            app.index();
            app.awaitClose(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
