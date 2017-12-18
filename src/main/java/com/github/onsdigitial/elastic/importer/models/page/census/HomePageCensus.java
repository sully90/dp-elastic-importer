package com.github.onsdigitial.elastic.importer.models.page.census;

import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.taxonomy.base.TaxonomyNode;
import com.github.onsdigitial.elastic.importer.models.partial.markdown.MarkdownSection;

import java.util.List;

/**
 * Created by bren on 14/01/16.
 */
public class HomePageCensus extends TaxonomyNode {

    private MarkdownSection intro = new MarkdownSection();
    private List<HomeSectionCensus> sections;
    private List<Image> images;

    @Override
    public PageType getType() {
        return PageType.home_page_census;
    }

    public MarkdownSection getIntro() {
        return intro;
    }

    public void setIntro(MarkdownSection intro) {
        this.intro = intro;
    }

    public List<HomeSectionCensus> getSections() {
        return sections;
    }

    public void setSections(List<HomeSectionCensus> sections) {
        this.sections = sections;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
