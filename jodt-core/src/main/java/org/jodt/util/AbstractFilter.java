package org.jodt.util;


abstract public class AbstractFilter<T> implements Filter<T> {

    public boolean filter(T t) {
        return include(t) || exclude(t);
    }

}
