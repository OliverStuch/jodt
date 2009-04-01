package org.jodt.property.comparison.implementation;



/**
 * @author Oliver Stuch
 */

public class NoDiff extends AbstractDiff  {

    public NoDiff(Object compareObject, Object referenceObject) {
        super("no diff");
        compareObject(compareObject);
        referenceObject(referenceObject);
    }

}
