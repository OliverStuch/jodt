package org.jodt.property.comparison;

import org.jodt.property.CompositePropertyList;

/**
 * @author Oliver Stuch
 */

public interface PropertyComparison<T> {
    /**
     * Repräsentation des CompareObjekts zum index-weisen Vergleich der Properties mit dem ReferenceObjekt
     */
    public CompositePropertyList<T> getCompareObjectAsIndexMappedCompositePropertyList();

    public CompositePropertyList<T> getReferenceObjectAsIndexMappedCompositePropertyList();

    public boolean hasDiffsOnPropertyLevel();

    public void childDiffCount(int childDiffCount);

    public int childDiffCount();

    public int childCount();

    public boolean hasChildren();

}
