package com.github.onsdigitial.elastic.importer.models.partial;

import com.github.onsdigitial.elastic.importer.base.Content;

import java.util.Date;

public class Alert extends Content {
    private Date date;
    private String markdown;
    private AlertType type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }
}
