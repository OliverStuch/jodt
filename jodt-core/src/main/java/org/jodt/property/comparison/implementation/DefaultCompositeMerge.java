package org.jodt.property.comparison.implementation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jodt.property.CompositeProperty;
import org.jodt.property.CompositePropertyList;
import org.jodt.property.Property;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.CompositeMerge;
import org.jodt.property.comparison.DiffType;


/**
 * @author Oliver Stuch
 */

public class DefaultCompositeMerge<T> extends ArrayList<CompositeMerge<?>> implements CompositeMerge<T> {

    public DefaultCompositeMerge(CompositeComparison<T> compositeComparison) {
        this.delegateCompositeComparison = compositeComparison;
        this.mergeProperty = this.delegateCompositeComparison.getCompareProperty(); // TODO clone
        this.mergeObjectAsIndexMappedCompositePropertyList = this.delegateCompositeComparison.getCompareObjectAsIndexMappedCompositePropertyList(); // TODO clone
        for (CompositeComparison<?> childCompositeComparison : compositeComparison) {
            CompositeMerge<?> cm = new DefaultCompositeMerge(childCompositeComparison);
            cm.parent(this);
            add(cm);
        }
    }

    // Comparison
    public void diff(DiffType diffType) {
        delegateCompositeComparison.diff(diffType);
    }

    // Comparison
    public DiffType diff() {
        return delegateCompositeComparison.diff();
    }

    // Comparison
    public boolean hasDiffsOnObjectLevel() {
        return delegateCompositeComparison.hasDiffsOnObjectLevel();
    }

    // PropertyComparison
    public int childCount() {
        return delegateCompositeComparison.childCount();
    }

    // PropertyComparison
    public void childDiffCount(int childDiffCount) {
        delegateCompositeComparison.childDiffCount(childDiffCount);
    }

    // PropertyComparison
    public int childDiffCount() {
        return delegateCompositeComparison.childDiffCount();
    }

    // PropertyComparison
    public boolean hasChildren() {
        return delegateCompositeComparison.hasChildren();
    }

    // PropertyComparison
    public boolean hasDiffsOnPropertyLevel() {
        return delegateCompositeComparison.hasDiffsOnPropertyLevel();
    }

    // PropertyComparison
    public Property<T> getCompareProperty() {
        return delegateCompositeComparison.getCompareProperty();
    }

    // PropertyComparison
    public Property<T> getReferenceProperty() {
        return delegateCompositeComparison.getReferenceProperty();
    }

    // PropertyComparison
    public CompositePropertyList<T> getCompareObjectAsIndexMappedCompositePropertyList() {
        return delegateCompositeComparison.getCompareObjectAsIndexMappedCompositePropertyList();
    }

    // PropertyComparison
    public CompositePropertyList<T> getReferenceObjectAsIndexMappedCompositePropertyList() {
        return delegateCompositeComparison.getReferenceObjectAsIndexMappedCompositePropertyList();
    }

    // PropertyMerge
    public CompositePropertyList<T> getMergeObjectAsIndexMappedCompositePropertyList() {
        return mergeObjectAsIndexMappedCompositePropertyList;
    }

    // CompositeMerge
    public CompositeMerge<?> parent() {
        return parent;
    }

    // CompositeMerge
    public void parent(CompositeMerge<?> parent) {
        this.parent = parent;
    }

    // CompositeMerge
    public List<CompositeMerge<?>> children() {
        return this;
    }

    // Merge
    public Property<T> getMergeProperty() {
        return mergeProperty;
    }

    // Property
    public String name() {
        return mergeProperty.name();
    }

    // Property
    public String description() {
        return mergeProperty.description();
    }

    // Property
    public Class<T> type() {
        return mergeProperty.type();
    }

    public Collection<Annotation> annotations() {
        return mergeProperty.annotations();
    }

    // Property
    public T value() {
        return mergeProperty.value();
    }

    // Property
    public CompositeProperty<T> value(T value) {
        return mergeProperty.value(value);
    }

    public String toString() {
        return "merge: " + delegateCompositeComparison.toString() + " -> " + mergeProperty.toString();
    }

    private CompositeComparison<T> delegateCompositeComparison;
    private CompositeMerge<?> parent;
    private Property<T> mergeProperty;
    private CompositePropertyList<T> mergeObjectAsIndexMappedCompositePropertyList;

}
