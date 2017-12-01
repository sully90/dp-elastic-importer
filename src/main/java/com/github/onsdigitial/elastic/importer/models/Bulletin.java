package com.github.onsdigitial.elastic.importer.models;

import java.util.List;

/**
 * @author sullid (David Sullivan) on 01/12/2017
 * @project dp-elastic-importer
 */
public class Bulletin extends Page {

    private List<Object> relatedBulletins;
    private String headline1;
    private String headline2;
    private String headline3;

    protected Bulletin() {

    }

    public List<Object> getRelatedBulletins() {
        return relatedBulletins;
    }

    public String getHeadline1() {
        return headline1;
    }

    public String getHeadline2() {
        return headline2;
    }

    public String getHeadline3() {
        return headline3;
    }
}
