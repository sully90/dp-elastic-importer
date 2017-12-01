package com.github.onsdigitial.elastic.importer.models;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public class Section {

    private String title;
    private String markdown;

    public Section(String title, String markdown) {
        this.title = title;
        this.markdown = markdown;
    }

    private Section() {
        // For jackson
    }

    public String getTitle() {
        return title;
    }

    public String getMarkdown() {
        return markdown;
    }
}
