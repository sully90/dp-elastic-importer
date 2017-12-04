package com.github.onsdigitial.elastic.importer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public abstract class Page {

    private List<Object> pdfTable;
    private List<Section> sections;
    private List<Section> accordion;
    private List<Object> relatedData;
    private List<Object> relatedDocuments;
    private List<UriContent> charts;
    private List<UriContent> tables;
    private List<UriContent> images;
    private List<Object> links;
    private List<Object> relatedMethodology;
    private String type;
    private String uri;
    private Map<String, Object> description;
    private String metaDescription;
    private boolean nationalStatistic;
    private Contact contact;
    private String releaseDate;
    private String edition;
    private String unit;
    private String preUnit;
    private String source;
    private List<Object> equations;
    private List<Object> alerts;
    private List<Object> relatedMethodologyArticle;
    private List<Object> versions;
    private List<Object> topics;

    protected Page() {

    }

    public List<Object> getPdfTable() {
        return pdfTable;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Section> getAccordion() {
        return accordion;
    }

    public List<Object> getRelatedData() {
        return relatedData;
    }

    public List<Object> getRelatedDocuments() {
        return relatedDocuments;
    }

    public List<UriContent> getCharts() {
        return charts;
    }

    public List<UriContent> getTables() {
        return tables;
    }

    public List<UriContent> getImages() {
        return images;
    }

    public List<Object> getLinks() {
        return links;
    }

    public List<Object> getRelatedMethodology() {
        return relatedMethodology;
    }

    public String getType() {
        return type;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, Object> getDescription() {
        return description;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public boolean isNationalStatistic() {
        return nationalStatistic;
    }

    public Contact getContact() {
        return contact;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getEdition() {
        return edition;
    }

    public String getUnit() {
        return unit;
    }

    public String getPreUnit() {
        return preUnit;
    }

    public String getSource() {
        return source;
    }

    public List<Object> getEquations() {
        return equations;
    }

    public List<Object> getAlerts() {
        return alerts;
    }

    public List<Object> getRelatedMethodologyArticle() {
        return relatedMethodologyArticle;
    }

    public List<Object> getVersions() {
        return versions;
    }

    public List<Object> getTopics() {
        return topics;
    }
}
