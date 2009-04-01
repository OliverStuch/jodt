package org.jodt.property.comparison;

import org.jodt.property.CompositePropertyList;

/**
 * @author Oliver Stuch
 */

public interface PropertyMerge<T> extends PropertyComparison<T> {
    public CompositePropertyList<T> getMergeObjectAsIndexMappedCompositePropertyList();
}
