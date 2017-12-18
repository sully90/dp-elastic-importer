package com.github.onsdigitial.elastic.importer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.onsdigital.elasticutils.client.Host;
import com.github.onsdigital.elasticutils.client.bulk.configuration.BulkProcessorConfiguration;
import com.github.onsdigital.elasticutils.client.generic.ElasticSearchClient;
import com.github.onsdigital.elasticutils.client.generic.TransportSearchClient;
import com.github.onsdigital.elasticutils.util.ElasticSearchHelper;
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
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sullid (David Sullivan) on 18/12/2017
 * @project dp-elastic-importer
 */
public class App {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final FileScanner fileScanner;
    private final Host host = Host.LOCALHOST;

    private List<Page> pages;

    public App() {
        String zebedeeRoot = System.getenv("zebedee_root");
        String dataDirectory = String.format("%s/zebedee/master/", zebedeeRoot);

        this.fileScanner = new FileScanner(dataDirectory);
        this.pages = new ArrayList<>();
    }

    private Page asClass(File file, Class<? extends Page> returnClass) throws IOException {
        return MAPPER.readValue(file, returnClass);
    }

    public Page fromFile(File file) throws IOException {
        Object obj = MAPPER.readValue(file, Object.class);

        if (obj instanceof Map) {
            Map<String, Object> objectMap = (Map<String, Object>) obj;
            if (objectMap.containsKey("type") && objectMap.get("type") instanceof String) {
                String type = (String) objectMap.get("type");
                PageType pageType = PageType.forType(type);
                if (pageType != null) {
                    switch (pageType) {
                        case home_page:
                            return asClass(file, HomePage.class);
                        case home_page_census:
                            return asClass(file, HomePageCensus.class);
                        case taxonomy_landing_page:
                            return asClass(file, TaxonomyLandingPage.class);
                        case product_page:
                            return asClass(file, ProductPage.class);
                        case bulletin:
                            return asClass(file, Bulletin.class);
                        case article:
                            return asClass(file, Article.class);
                        case article_download:
                            return asClass(file, ArticleDownload.class);
                        case timeseries:
                            return asClass(file, TimeSeries.class);
                        case data_slice:
                            return asClass(file, DataSlice.class);
                        case compendium_landing_page:
                            return asClass(file, CompendiumLandingPage.class);
                        case compendium_chapter:
                            return asClass(file, CompendiumChapter.class);
                        case compendium_data:
                            return asClass(file, CompendiumData.class);
                        case static_landing_page:
                            return asClass(file, StaticLandingPage.class);
                        case static_article:
                            return asClass(file, StaticArticle.class);
                        case static_page:
                            break;
//                        return asClass(file, StaticPage.class);
                        case static_qmi:
                            return asClass(file, QMI.class);
                        case static_foi:
                            return asClass(file, FOI.class);
                        case static_adhoc:
                            return asClass(file, AdHoc.class);
                        case dataset:
                            return asClass(file, Dataset.class);
                        case dataset_landing_page:
                            return asClass(file, DatasetLandingPage.class);
                        case timeseries_dataset:
                            return asClass(file, TimeSeriesDataset.class);
                        case release:
                            return asClass(file, Release.class);
                        case reference_tables:
                            return asClass(file, ReferenceTables.class);
                        case chart:
                            return asClass(file, Chart.class);
                        case table:
                            return asClass(file, Table.class);
                        case image:
                            return asClass(file, Image.class);
                        case visualisation:
                            return asClass(file, Visualisation.class);
                        case equation:
                            return asClass(file, Equation.class);
                    }
                }
            }
        }
        return null;
    }

    public void index() {
        try (ElasticSearchClient<Page> searchClient = getClient(this.host)) {
            List<Page> pages = this.getPages();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public List<Page> getPages() {
        if (pages.size() == 0) {
            List<File> files = null;
            try {
                files = fileScanner.getFiles();
            } catch (IOException e) {
                LOGGER.error("Unable to retrieve page files.", e);
            }

            for (File file : files) {
                Page page = null;
                try {
                    page = fromFile(file);
                } catch (IOException e) {
                    LOGGER.error(String.format("Error parsing file: ", file.getAbsolutePath()), e);
                    System.exit(1);
                }
                if (page != null) {
                    pages.add(page);
                }
            }
        }
        return this.pages;
    }

    private static ElasticSearchClient<Page> getClient(Host host) throws UnknownHostException {
        TransportClient transportClient = ElasticSearchHelper.getTransportClient(host);
        BulkProcessorConfiguration configuration = ElasticSearchHelper.getDefaultBulkProcessorConfiguration();
        return new TransportSearchClient<>(transportClient, configuration);
    }

    public static void main(String[] args) {
        App app = new App();
        List<Page> pages = app.getPages();
        System.out.println(pages.size());
    }

}
