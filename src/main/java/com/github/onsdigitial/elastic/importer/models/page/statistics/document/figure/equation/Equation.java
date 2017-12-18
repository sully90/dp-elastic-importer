package com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.equation;


import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.figure.FigureBase;

public class Equation extends FigureBase {

    private String filename;
    private String content;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public PageType getType() {
        return PageType.equation;
    }
}
