package org.jodt.property.comparison.implementation;



/**
 * @author Oliver Stuch
 */

public class ReferenceDiff extends AbstractDiff {

    public ReferenceDiff(Object compareObject, Object referenceObject) {
        super("reference diff");
        compareObject(compareObject);
        referenceObject(referenceObject);
    }

}
