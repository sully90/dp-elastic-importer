package com.github.onsdigitial.elastic.importer.models.page.statistics.document.article;



import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DownloadSection;
import com.github.onsdigitial.elastic.importer.models.page.statistics.document.base.StatisticalDocument;
import com.github.onsdigitial.elastic.importer.models.partial.Link;

import java.util.List;

/**
 * Created by bren on 04/06/15.
 */
public class Article extends StatisticalDocument {

    /*Body*/
    private List<Link> relatedArticles;
    private List<DownloadSection> pdfTable;
    private Boolean isPrototypeArticle;


    public List<DownloadSection> getPdfTable() {
        return pdfTable;
    }

    public void setPdfTable(List<DownloadSection> pdfTable) {
        this.pdfTable = pdfTable;
    }

    @Override
    public PageType getType() {
        return PageType.article;
    }

    public void setRelatedArticles(List<Link> relatedArticles) {
        this.relatedArticles = relatedArticles;
    }

    public List<Link> getRelatedArticles() {
        return relatedArticles;
    }

    public Boolean getPrototypeArticle() {
        return isPrototypeArticle;
    }

    public void setPrototypeArticle(Boolean prototypeArticle) {
        isPrototypeArticle = prototypeArticle;
    }
}
