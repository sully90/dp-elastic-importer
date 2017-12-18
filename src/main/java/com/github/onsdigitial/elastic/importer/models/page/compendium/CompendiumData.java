package com.github.onsdigitial.elastic.importer.models.page.compendium;


import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.statistics.dataset.DatasetLandingPage;
import com.github.onsdigitial.elastic.importer.models.partial.Link;

/**
 * Created by bren on 06/07/15.
 */
public class CompendiumData extends DatasetLandingPage {

    private Link parent;

    @Override
    public PageType getType() {
        return PageType.compendium_data;
    }

    public Link getParent() {
        return parent;
    }

    public void setParent(Link parent) {
        this.parent = parent;
    }
}
