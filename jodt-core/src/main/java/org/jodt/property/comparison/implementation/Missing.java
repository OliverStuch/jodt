package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.DiffType;

/**
 * @author Oliver Stuch
 */

public class Missing implements DiffType {

    private Object missingObject;

    public Missing(Object missingObject) {
        this.missingObject = missingObject;
    }

    public Object missingObject() {
        return missingObject;
    }
}
