package org.jodt.property.comparison.implementation;

import java.util.ArrayList;
import java.util.List;

import org.jodt.property.comparison.IndexMapper;
import org.jodt.reflection.PrivilegedReflectionUtil;


/**
 * @author Oliver Stuch
 */

public class DefaultIndexMapper implements IndexMapper {

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new DefaultIndexMapper((List<Integer>) PrivilegedReflectionUtil.cloneUsingCloneMethod(indexMapping));
    }

    public DefaultIndexMapper(List<Integer> indexMapping) {
        this.indexMapping = indexMapping;
    }

    public DefaultIndexMapper() {
        indexMapping = new ArrayList<Integer>();
    }

    public DefaultIndexMapper(int initialSize) {
        indexMapping = new ArrayList<Integer>(initialSize);
    }

    public int map(int index) {
        return indexMapping.get(index);
    }

    public int size() {
        return indexMapping.size();
    }

    public void addMapping(int index, int mappedIndex) {
        indexMapping.add(index, mappedIndex);
    }

    public boolean isMapped(int index) {
        return indexMapping.contains(index);
    }

    public void clear() {
        indexMapping.clear();
    }

    public String toString() {
        return "mapping: " + indexMapping;
    }

    private List<Integer> indexMapping;

}
