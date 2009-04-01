package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.ComparisonStrategy;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.CompositeMerge;


/**
 * @author Oliver Stuch
 */
public class DiffStrategy implements ComparisonStrategy {

    private CompareTool compareTool;

    public DiffStrategy(CompareTool compareTool) {
        this.compareTool = compareTool;
    }

    public CompositeComparison createComparison(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName) {
        return compareTool.diff(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName);
    }

    public <T> CompositeMerge<T> addMergeObject(CompositeComparison<T> compositeComparison) {
        return compareTool.addMergeObject(compositeComparison);
    }
    
    public CompareToolConfiguration configure() {
        return compareTool.configure();
    }

}