package com.github.onsdigitial.elastic.importer.base;


import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by bren on 03/08/15.
 */
public class Content implements Cloneable {

    @Override
    public Object clone() throws CloneNotSupportedException {
        return clone(this);
    }

    /**
     * Clones given object and returns a new copy
     *
     * @param o   object to be cloned
     * @param <O> copy of given object
     * @return
     */
    public static <O extends Cloneable> O clone(O o) {
        Cloneable cloneable = o;
        return ObjectUtils.clone(o);
    }
}
