package com.github.onsdigitial.elastic.importer.models.page.staticpage.qmi;



import com.github.onsdigitial.elastic.importer.models.page.base.PageType;
import com.github.onsdigitial.elastic.importer.models.page.staticpage.base.BaseStaticPage;
import com.github.onsdigitial.elastic.importer.models.partial.Link;

import java.util.List;

/**
 * Created by bren on 04/06/15.
 */
public class QMI extends BaseStaticPage {

    private List<Link> relatedDocuments;
    private List<Link> relatedDatasets;

    @Override
    public PageType getType() {
        return PageType.static_qmi;
    }

    public List<Link> getRelatedDocuments() {
        return relatedDocuments;
    }

    public void setRelatedDocuments(List<Link> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }

    public List<Link> getRelatedDatasets() {
        return relatedDatasets;
    }

    public void setRelatedDatasets(List<Link> relatedDatasets) {
        this.relatedDatasets = relatedDatasets;
    }
}
