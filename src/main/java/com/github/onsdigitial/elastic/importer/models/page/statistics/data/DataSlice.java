package com.github.onsdigitial.elastic.importer.models.page.statistics.data;


import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.statistics.data.base.StatisticalData;

/**
 * Created by bren on 05/06/15.
 */
public class DataSlice extends StatisticalData {

    @Override
    public PageType getType() {
        return PageType.data_slice;
    }

}
