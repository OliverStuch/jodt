package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.DiffType;

/**
 * @author Oliver Stuch
 */
public class Additional implements DiffType {

    private Object additionalObject;

    public Additional(Object additionalObject) {
        this.additionalObject = additionalObject;
    }

    public Object additionalObject() {
        return additionalObject;
    }

    @Override
    public String toString() {
        return "additional";
    }

}
