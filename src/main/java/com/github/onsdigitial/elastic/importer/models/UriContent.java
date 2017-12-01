package com.github.onsdigitial.elastic.importer.models;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public class UriContent {

    private String uri;
    private String title;
    private String filename;

    public UriContent(String uri, String title, String filename) {
        this.uri = uri;
        this.title = title;
        this.filename = filename;
    }

    private UriContent() {
        // For jackson
    }

    public String getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }
}
