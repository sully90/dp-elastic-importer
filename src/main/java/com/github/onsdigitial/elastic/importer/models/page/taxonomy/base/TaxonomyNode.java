package com.github.onsdigitial.elastic.importer.models.page.taxonomy.base;


import com.github.onsdigitial.elastic.importer.models.page.base.Page;

/**
 * Created by bren on 04/06/15.
 * <p>
 * Represents a taxonomy node
 *
 * @author david
 * @author bren
 */
public abstract class TaxonomyNode extends Page implements Comparable<TaxonomyNode>  {

    private Integer index;

    @Override
    public int compareTo(TaxonomyNode o) {
        if (this.index == null) {
            return -1;
        }
        return Integer.compare(this.index, o.index);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
