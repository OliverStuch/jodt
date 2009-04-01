package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.DiffType;

public class AbstractDiff implements DiffType{

    public AbstractDiff(String string) {
        this.string = string;
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
        return this.string;
    }

    private Object compareObject;
    private Object referenceObject;
    private String string;
}
