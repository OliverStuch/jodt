package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.DiffType;

public class AbstractDiff implements DiffType{

    public AbstractDiff(String description) {
        this.description = description;
    }

    public Object compareObject() {
        return compareObject;
    }

    void compareObject(Object compareObject) {
        this.compareObject = compareObject;
    }

    public Object referenceObject() {
        return referenceObject;
    }

    void referenceObject(Object referenceObject) {
        this.referenceObject = referenceObject;
    }

    public String toString() {
        return this.description;
    }

    private Object compareObject;
    private Object referenceObject;
    private String description;
}
