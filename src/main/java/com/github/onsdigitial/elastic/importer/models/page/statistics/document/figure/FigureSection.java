package com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure;



import com.github.onsdigitial.elastic.importer.base.Content;

import java.net.URI;

/**
 * Common properties of figures (charts and tables)
 */
public class FigureSection extends Content {
    private String title;
    private String filename;
    private URI uri;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
