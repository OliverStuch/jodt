package org.jodt.property.comparison.implementation;


import org.jodt.property.PropertyToolConfiguration;
import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.ComparisonStrategy;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.CompositeMerge;


/**
 * @author Oliver Stuch
 */

public class CompareStrategy implements ComparisonStrategy {


    private CompareTool compareTool;

    public CompareStrategy(CompareTool compareTool) {
        this.compareTool = compareTool;
    }

    public CompositeComparison createComparison(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName) {
        return compareTool.compare(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName);
    }

    public <T> CompositeMerge<T> addMergeObject(CompositeComparison<T> compositeComparison) {
        return compareTool.addMergeObject(compositeComparison);
    }

    public PropertyToolConfiguration configure() {
        return compareTool.configure();
    }

}