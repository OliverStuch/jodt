package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.DiffType;

public class Additional implements DiffType {

    private Object additionalObject;

    public Additional(Object additionalObject) {
        this.additionalObject = additionalObject;
    }

    public Object additionalObject() {
        return additionalObject;
    }

}
