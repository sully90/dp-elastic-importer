package com.github.onsdigitial.elastic.importer.models.page.adhoc;


import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.BaseStaticPage;

/**
 * Created by bren on 04/06/15.
 */
public class AdHoc extends BaseStaticPage {

    @Override
    public PageType getType() {
        return PageType.static_adhoc;
    }

}
