package com.github.onsdigitial.elastic.importer.models.page.staticpage;



import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DownloadSection;

import java.util.List;

/**
 * Created by bren on 06/07/15.
 */
public class Methodology extends StaticArticle {
    private List<DownloadSection> pdfTable;

    protected Methodology() {
        super();
    }

    public List<DownloadSection> getPdfTable() {
        return pdfTable;
    }

    public void setPdfTable(List<DownloadSection> pdfTable) {
        this.pdfTable = pdfTable;
    }

    @Override
    public PageType getType() {
        return PageType.static_methodology;
    }
}
