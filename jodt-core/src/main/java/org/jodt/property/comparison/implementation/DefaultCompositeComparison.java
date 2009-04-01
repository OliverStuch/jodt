package org.jodt.property.comparison.implementation;

import java.util.ArrayList;
import java.util.List;

import org.jodt.property.CompositePropertyList;
import org.jodt.property.Property;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.DiffType;
import org.jodt.property.comparison.implementation.DefaultCompareTool.PropertyListPair;


/**
 * @author Oliver Stuch
 */

public class DefaultCompositeComparison<T> extends ArrayList<CompositeComparison<?>> implements CompositeComparison<T> {

    public DefaultCompositeComparison(PropertyListPair identityMappedPropertyListPair, CompositeComparison<T> parent) {
        this.identityMappedPropertyListPair = identityMappedPropertyListPair;
        this.parent = parent;
    }

    // Comparison
    public void diff(DiffType diff) {
        this.diff = diff;
    }

    // Comparison
    public DiffType diff() {
        return diff;
    }

    // Comparison
    public boolean hasDiffsOnObjectLevel() {
        return diff != null && !(diff instanceof NoDiff) && !(diff instanceof SubDiff);
    }

    public boolean hasDiffsOnPropertyLevel() {
        return childDiffCount > 0 || (diff != null && diff instanceof SubDiff);
    }

    // PropertyComparison
    public Property<T> getCompareProperty() {
        return getCompareObjectAsIndexMappedCompositePropertyList();
    }

    // PropertyComparison
    public Property<T> getReferenceProperty() {
        return getReferenceObjectAsIndexMappedCompositePropertyList();
    }

    // PropertyComparison
    public CompositePropertyList<T> getCompareObjectAsIndexMappedCompositePropertyList() {
        return identityMappedPropertyListPair.compareProperties;
    }

    // PropertyComparison
    public CompositePropertyList<T> getReferenceObjectAsIndexMappedCompositePropertyList() {
        return identityMappedPropertyListPair.referenceProperties;
    }

    // CompositeComparison
    public int childCount() {
        return size();
    }

    // CompositeComparison
    public boolean hasChildren() {
        return size() != 0;
    }

    // CompositeComparison
    public List<CompositeComparison<?>> children() {
        return this;
    }

    // CompositeComparison
    // TODO slowwwww..
    public List<CompositeComparison<?>> childrenWithDiffsOnObjectLevel() {
        List<CompositeComparison<?>> result = new ArrayList();
        for (CompositeComparison<?> compositeComparison : this) {
            if (compositeComparison.hasDiffsOnObjectLevel()) {
                result.add(compositeComparison);
            }
        }
        return result;
    }

    // CompositeComparison
    // TODO slowwwww..
    public List<CompositeComparison<?>> recursiveChildrenWithDiffsOnObjectLevel(CompositeComparison<?> comparison) {
        List<CompositeComparison<?>> result = new ArrayList();
        for (CompositeComparison<?> child : comparison) {
            result.addAll(child.childrenWithDiffsOnObjectLevel());
            recursiveChildrenWithDiffsOnObjectLevel(child);
        }
        return result;
    }

    // CompositeComparison
    public CompositeComparison<?> parent() {
        return parent;
    }

    // CompositeComparison
    public void parent(CompositeComparison<?> parent) {
        this.parent = parent;
    }

    // CompositeComparison
    public void childDiffCount(int childDiffCount) {
        this.childDiffCount = childDiffCount;
    }

    // CompositeComparison
    public int childDiffCount() {
        return this.childDiffCount;
    }

    public String name() {
        String compareName = getCompareProperty().name();
        String referenceName = getReferenceProperty().name();
        if (compareName == null) {
            compareName = " ";
        } else if (referenceName == null) {
            referenceName = " ";
        }

        if (compareName.equals(referenceName)) {
            return compareName;
        } else {
            return compareName + " / " + referenceName;
        }
    }

    public String toString() {
        return name() + " TYPE: " + diff() + ", CHILDREN: " + size() + ", COMPARE: " + this.identityMappedPropertyListPair.compareProperties.toString()
                + ", REFERENCE: " + this.identityMappedPropertyListPair.referenceProperties.toString();
    }

    private PropertyListPair<T> identityMappedPropertyListPair;
    private CompositeComparison<?> parent;
    private DiffType diff;
    private int childDiffCount;

}
