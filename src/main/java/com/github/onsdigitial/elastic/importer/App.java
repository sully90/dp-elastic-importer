package com.github.onsdigitial.elastic.importer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.elasticutils.client.Host;
import com.github.onsdigital.elasticutils.client.bulk.configuration.BulkProcessorConfiguration;
import com.github.onsdigital.elasticutils.client.bulk.options.BulkProcessingOptions;
import com.github.onsdigital.elasticutils.client.generic.ElasticSearchClient;
import com.github.onsdigital.elasticutils.client.generic.RestSearchClient;
import com.github.onsdigital.elasticutils.client.generic.TransportSearchClient;
import com.github.onsdigital.elasticutils.client.http.SimpleRestClient;
import com.github.onsdigital.elasticutils.client.type.DefaultDocumentTypes;
import com.github.onsdigital.elasticutils.util.ElasticSearchHelper;
import com.github.onsdigitial.elastic.importer.elasticsearch.OpenNlpSearchClient;
import com.github.onsdigitial.elastic.importer.models.page.adhoc.AdHoc;
import com.github.onsdigitial.elastic.importer.models.page.base.Page;
import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.census.HomePageCensus;
import com.github.onsdigitial.elastic.importer.models.page.compendium.CompendiumChapter;
import com.github.onsdigitial.elastic.importer.models.page.compendium.CompendiumData;
import com.github.onsdigitial.elastic.importer.models.page.compendium.CompendiumLandingPage;
import com.github.onsdigitial.elastic.importer.models.page.home.HomePage;
import com.github.onsdigitial.elastic.importer.models.page.release.Release;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.StaticArticle;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.StaticLandingPage;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.StaticPage;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.foi.FOI;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.qmi.QMI;
import com.github.onsdigitial.elastic.importer.models.page.statistics.data.DataSlice;
import com.github.onsdigitial.elastic.importer.models.page.statistics.data.timeseries.TimeSeries;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.Dataset;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DatasetLandingPage;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.ReferenceTables;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.TimeSeriesDataset;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.article.Article;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.article.ArticleDownload;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.bulletin.Bulletin;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.chart.Chart;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.equation.Equation;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.image.Image;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.table.Table;
import com.github.onsdigitial.elastic.importer.models.page.taxonomy.ProductPage;
import com.github.onsdigitial.elastic.importer.models.page.taxonomy.TaxonomyLandingPage;
import com.github.onsdigitial.elastic.importer.models.page.visualisation.Visualisation;
import com.github.onsdigitial.elastic.importer.util.FileScanner;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author sullid (David Sullivan) on 18/12/2017
 * @project dp-elastic-importer
 */
public class App {

    private static final ObjectMapper MAPPER;
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final FileScanner fileScanner;
    private final Host host = Host.LOCALHOST;

    private final String indexName;
    private List<Page> pages;

    private int commitCount = 100;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public App() {
        String zebedeeRoot = System.getenv("zebedee_root");
        String dataDirectory = String.format("%s/zebedee/master/", zebedeeRoot);

        this.fileScanner = new FileScanner(dataDirectory);
        this.indexName = "ons_test";
        this.pages = new ArrayList<>();
    }

    public String getIndexName() {
        return indexName;
    }

    private Page asClass(String fileString, Class<? extends Page> returnClass) throws IOException {
        return MAPPER.readValue(fileString, returnClass);
    }

    public Page fromFile(File file) throws IOException {
        String fileString = IOUtils.toString(new FileReader(file));
        Object obj;
        try {
             obj = MAPPER.readValue(fileString, Object.class);
        } catch (JsonParseException | JsonMappingException e) {
            LOGGER.warn(String.format("Error constructing object from json in file: %s", file.getAbsolutePath()), e);
            return null;
        }

        if (obj instanceof Map) {
            Map<String, Object> objectMap;
            try {
                objectMap = MAPPER.readValue(fileString, new TypeReference<Map<String, Object>>() {});
            } catch (JsonMappingException e) {
                LOGGER.warn(String.format("Error constructing objectMap from json in file: %s", file.getAbsolutePath()), e);
                return null;
            }

            if (objectMap.containsKey("type") && objectMap.get("type") instanceof String) {
                String type = (String) objectMap.get("type");
                PageType pageType = PageType.forType(type);
                if (pageType != null) {
                    switch (pageType) {
                        case home_page:
                            return asClass(fileString, HomePage.class);
                        case home_page_census:
                            return asClass(fileString, HomePageCensus.class);
                        case taxonomy_landing_page:
                            return asClass(fileString, TaxonomyLandingPage.class);
                        case product_page:
                            return asClass(fileString, ProductPage.class);
                        case bulletin:
                            return asClass(fileString, Bulletin.class);
                        case article:
                            return asClass(fileString, Article.class);
                        case article_download:
                            return asClass(fileString, ArticleDownload.class);
                        case timeseries:
                            return asClass(fileString, TimeSeries.class);
                        case data_slice:
                            return asClass(fileString, DataSlice.class);
                        case compendium_landing_page:
                            return asClass(fileString, CompendiumLandingPage.class);
                        case compendium_chapter:
                            return asClass(fileString, CompendiumChapter.class);
                        case compendium_data:
                            return asClass(fileString, CompendiumData.class);
                        case static_landing_page:
                            return asClass(fileString, StaticLandingPage.class);
                        case static_article:
                            return asClass(fileString, StaticArticle.class);
                        case static_page:
                            return asClass(fileString, StaticPage.class);
                        case static_qmi:
                            return asClass(fileString, QMI.class);
                        case static_foi:
                            return asClass(fileString, FOI.class);
                        case static_adhoc:
                            return asClass(fileString, AdHoc.class);
                        case dataset:
                            return asClass(fileString, Dataset.class);
                        case dataset_landing_page:
                            return asClass(fileString, DatasetLandingPage.class);
                        case timeseries_dataset:
                            return asClass(fileString, TimeSeriesDataset.class);
                        case release:
                            return asClass(fileString, Release.class);
                        case reference_tables:
                            return asClass(fileString, ReferenceTables.class);
                        case chart:
                            return asClass(fileString, Chart.class);
                        case table:
                            return asClass(fileString, Table.class);
                        case image:
                            return asClass(fileString, Image.class);
                        case visualisation:
                            return asClass(fileString, Visualisation.class);
                        case equation:
                            return asClass(fileString, Equation.class);
                        default:
                            LOGGER.warn("Unknown type: " + type);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds pages to the page list until we reach this.commitCount, at which time a bulk insert occurs
     * @param searchClient
     * @param file
     */
    private void indexPage(ElasticSearchClient<Page> searchClient, File file) {
        Page page = null;
        try {
            page = this.fromFile(file);

            if (page != null) {
                if (this.pages.size() >= this.commitCount) {
                    // Async bulk insert - use a copy of this.pages and then clear the list
                    searchClient.bulk(this.indexName, DefaultDocumentTypes.DOCUMENT, new ArrayList<>(this.pages).stream(),
                            XContentType.JSON, JsonInclude.Include.NON_NULL);
                    // Clear the list
                    this.pages.clear();
                } else {
                    this.pages.add(page);
                }
            }
        } catch (IOException e) {
            return;
        }
    }

    public void index() throws IOException {
        List<File> files = this.fileScanner.getFiles();
        try (ElasticSearchClient<Page> searchClient = getClientOpenNlpTcp(this.host)) {

            // Reset the index
            resetIndex(this.indexName, searchClient);

            Stream<File> fileStream = files.stream();
            fileStream.forEach(x -> this.indexPage(searchClient, x));

            searchClient.awaitClose(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void resetIndex(String indexName, ElasticSearchClient<Page> searchClient) throws IOException {
        LOGGER.info("Resetting index: " + indexName);
        if (searchClient.indexExists(indexName)) {
            searchClient.dropIndex(indexName);
        }
        Settings settings = ElasticSearchHelper.loadSettingsFromFile("/search/", "index-config.yml");
        Map<String, Object> mapping = ElasticSearchHelper.loadMappingFromFile("/search/", "default-document-mapping.json");
        searchClient.createIndex(indexName, DefaultDocumentTypes.DOCUMENT, settings, mapping);
    }

    private static ElasticSearchClient<Page> getClientOpenNlpTcp(Host host) throws UnknownHostException {
        TransportClient transportClient = ElasticSearchHelper.getTransportClient(host);
        return new OpenNlpSearchClient<>(transportClient, getConfiguration());
    }

    private static ElasticSearchClient<Page> getClientTcp(Host host) throws UnknownHostException {
        TransportClient transportClient = ElasticSearchHelper.getTransportClient(host);
        return new TransportSearchClient<>(transportClient, getConfiguration());
    }

    private static ElasticSearchClient<Page> getClientHttp(Host host) throws UnknownHostException {
        SimpleRestClient restClient = ElasticSearchHelper.getRestClient(host);
        return new RestSearchClient<>(restClient, getConfiguration());
    }

    private static BulkProcessorConfiguration getConfiguration() {
        BulkProcessorConfiguration bulkProcessorConfiguration = new BulkProcessorConfiguration(BulkProcessingOptions.builder()
                .setBulkActions(100)
//                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
//                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(8)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(1000), 5))
                .build());
        return bulkProcessorConfiguration;
    }

    public static void main(String[] args) {
        App app = new App();

        try {
            long startTime = System.currentTimeMillis();
            app.index();
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            System.out.format("Milli = %s, ( S_Start : %s, S_End : %s ) \n", duration, startTime, endTime );
            System.out.println("Human-Readable format : "+ millisToShortDHMS( duration ) );
        } catch (IOException e) {
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
