package org.jodt.property.comparison;



/**
 * @author Oliver Stuch
 */
// TODO nach root
public interface ComparisonStrategy {

    CompositeComparison createComparison(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName);

    <T> CompositeMerge<T> addMergeObject(CompositeComparison<T> compositeComparison);
    CompareToolConfiguration configure();
}