package com.github.onsdigitial.elastic.importer.models.page.staticpage.foi;


import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.BaseStaticPage;

/**
 * Created by bren on 04/06/15.
 */
public class FOI extends BaseStaticPage {
    @Override
    public PageType getType() {
        return PageType.static_foi;
    }
}
