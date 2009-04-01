package org.jodt.util;

public interface Filter<T> {
    boolean filter(T t);
    boolean include(T t);
    boolean exclude(T t);
}
