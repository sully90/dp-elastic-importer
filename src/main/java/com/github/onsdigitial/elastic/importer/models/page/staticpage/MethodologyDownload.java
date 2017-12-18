package com.github.onsdigitial.elastic.importer.models.page.staticpage;



import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.BaseStaticPage;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DownloadSection;
import com.github.onsdigitial.elastic.importer.models.partial.Alert;
import com.github.onsdigitial.elastic.importer.models.partial.Link;

import java.util.List;

public class MethodologyDownload extends BaseStaticPage {

    private List<Link> relatedDocuments;
    private List<Link> relatedDatasets;
    private List<DownloadSection> pdfDownloads;

    private List<Alert> alerts;

    public List<Link> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(List<Link> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public List<Link> getRelatedDatasets() {
        return relatedDatasets;
    }

    public void setRelatedDatasets(List<Link> relatedDatasets) {
        this.relatedDatasets = relatedDatasets;
    }

    public List<DownloadSection> getPdfDownloads() {
        return pdfDownloads;
    }

    public void setPdfDownloads(List<DownloadSection> pdfDownloads) {
        this.pdfDownloads = pdfDownloads;
    }

    public List<Alert> getAlerts() { return alerts; }

    public void setAlerts(List<Alert> alerts) { this.alerts = alerts; }

    @Override
    public PageType getType() {
        return PageType.static_methodology_download;
    }
}
