package com.github.onsdigitial.elastic.importer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public class Article extends Page {

    private List<Object> relatedArticles;
    private List<Object> relatedMethodologyArticle;
    private List<Object> relatedDatasets;
    private List<Object> pdfDownloads;
    private boolean latestRelease;
    private String _abstract;
    private List<Object> equations;
    private List<Object> alerts;
    private List<Object> versions;
    private List<Object> topics;
    private List<Object> downloads;
    private List<Object> supplementaryFiles;
    private Section section;
    private List<Object> markdown;
    private List<Object> dateChanges;

    protected Article() {

    }

    public List<Object> getRelatedArticles() {
        return relatedArticles;
    }

    public List<Object> getRelatedMethodologyArticle() {
        return relatedMethodologyArticle;
    }

    public List<Object> getRelatedDatasets() {
        return relatedDatasets;
    }

    public List<Object> getPdfDownloads() {
        return pdfDownloads;
    }

    public boolean isLatestRelease() {
        return latestRelease;
    }

    @JsonProperty("_abstract")
    public String getAbstract() {
        return _abstract;
    }

    public List<Object> getEquations() {
        return equations;
    }

    public List<Object> getAlerts() {
        return alerts;
    }

    public List<Object> getVersions() {
        return versions;
    }

    public List<Object> getTopics() {
        return topics;
    }

    public List<Object> getDownloads() {
        return downloads;
    }

    public List<Object> getSupplementaryFiles() {
        return supplementaryFiles;
    }

    public Section getSection() {
        return section;
    }

    public List<Object> getMarkdown() {
        return markdown;
    }

    public List<Object> getDateChanges() {
        return dateChanges;
    }
}