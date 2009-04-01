package org.jodt.property.comparison.implementation;



/**
 * @author Oliver Stuch
 */

public class ValueDiff extends AbstractDiff {

    public ValueDiff(Object compareObject, Object referenceObject) {
        super("value diff");
        compareObject(compareObject);
        referenceObject(referenceObject);
    }

}
