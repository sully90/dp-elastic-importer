package com.github.onsdigitial.elastic.importer.elasticsearch.indicies;

import com.github.onsdigitial.elastic.importer.elasticsearch.exceptions.NoSuchIndexException;

/**
 * @author sullid (David Sullivan) on 22/11/2017
 * @project dp-search-service
 */
public enum ElasticSearchIndex {
    TEST("test");

    private String indexName;

    ElasticSearchIndex(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public static ElasticSearchIndex forIndexName(String indexName) throws NoSuchIndexException {
        for (ElasticSearchIndex index : ElasticSearchIndex.values()) {
            if (index.getIndexName().equals(indexName)) {
                return index;
            }
        }
        throw new NoSuchIndexException(indexName);
    }
}
