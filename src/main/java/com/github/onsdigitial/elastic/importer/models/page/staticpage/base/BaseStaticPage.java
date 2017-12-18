package com.github.onsdigitial.elastic.importer.models.page.staticpage.base;



import com.github.onsdigitial.elastic.importer.models.page.base.Page;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DownloadSection;
import com.github.onsdigitial.elastic.importer.models.partial.Link;

import java.util.List;

/**
 * Created by bren on 29/06/15.
 */
public abstract class BaseStaticPage extends Page {

    private List<DownloadSection> downloads;

    /**
     *Body in markdown format
     */
    private List<String> markdown;

    /**
     * Optional external links
     */
    private List<Link> links;

    public List<String> getMarkdown() {
        return markdown;
    }

    public void setMarkdown(List<String> markdown) {
        this.markdown = markdown;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<DownloadSection> getDownloads() {
        return downloads;
    }

    public void setDownloads(List<DownloadSection> downloads) {
        this.downloads = downloads;
    }
}
