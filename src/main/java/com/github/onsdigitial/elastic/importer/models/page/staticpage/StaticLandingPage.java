package com.github.onsdigitial.elastic.importer.models.page.staticpage;



import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.BaseStaticPage;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.StaticPageSection;

import java.util.List;

/**
 * Created by bren on 29/06/15.
 * Landing page showing links to other static pages
 *
 */
public class StaticLandingPage extends BaseStaticPage {

    private String fileName;
    private List<StaticPageSection> sections;

    @Override
    public PageType getType() {
        return PageType.static_landing_page;
    }

    public List<StaticPageSection> getSections() {
        return sections;
    }

    public void setSections(List<StaticPageSection> sections) {
        this.sections = sections;
    }

    public String getFileName() {
        return fileName;
    }
}
