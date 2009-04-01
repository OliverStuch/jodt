package org.jodt.property.comparison;

/**
 * Vergleicht zwei Objekte rekursiv.
 * @author Oliver Stuch  (oliver@stuch.net) 
 */
public interface CompareTool {
    <T> CompositeComparison<T> compare(T comparativeObject, String comparativeName, T referenceObject, String referenceName);

    <T> CompositeComparison<T> diff(T comparativeObject, String comparativeName, T referenceObject, String referenceName);

    <T> CompositeMerge<T> addMergeObject(CompositeComparison<T> compositeComparison);

    CompareToolConfiguration configure();

}
