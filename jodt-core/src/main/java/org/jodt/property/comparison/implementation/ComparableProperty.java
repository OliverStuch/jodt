package org.jodt.property.comparison.implementation;

import java.util.Collection;
import java.util.Iterator;

import org.jodt.property.CompositeProperty;
import org.jodt.property.IdentityResolver;
import org.jodt.util.Registry;

public class ComparableProperty implements CompositeProperty, Comparable<ComparableProperty> {

    public ComparableProperty(CompositeProperty property, int originalIndex, Comparable id) {
        this.delegateCompositeProperty = property;
        this.id = id;
        this.originalIndex = originalIndex;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

    public int compareTo(ComparableProperty other) {
        Comparable otherID = other.id;
        if (this.id == null || otherID == null) {
            return 0;
        }
        return id.compareTo(otherID);
    }

    public CompositeProperty parent() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public String path() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String description() {
        return delegateCompositeProperty.description();
    }

    public String name() {
        return delegateCompositeProperty.name();
    }

    public String displayName() {
        return delegateCompositeProperty.displayName();
    }

    public Class type() {
        return delegateCompositeProperty.type();
    }

    public Collection annotations() {
        return delegateCompositeProperty.annotations();
    }

    public Object value() {
        return delegateCompositeProperty.value();
    }

    public CompositeProperty value(Object value) {
        return delegateCompositeProperty.value(value);
    }

    public boolean add(Object e) {
        return delegateCompositeProperty.add(e);
    }

    public boolean addAll(Collection c) {
        return delegateCompositeProperty.addAll(c);
    }

    public void clear() {
        delegateCompositeProperty.clear();
    }

    public boolean contains(Object o) {
        return delegateCompositeProperty.contains(o);
    }

    public boolean containsAll(Collection c) {
        return delegateCompositeProperty.containsAll(c);
    }

    public Collection<CompositeProperty> findByValue(Object value) {
        return delegateCompositeProperty.findByValue(value);
    }

    public Collection<CompositeProperty> findByName(String name) {
        return delegateCompositeProperty.findByName(name);
    }

    public boolean hasProperties() {
        return delegateCompositeProperty.hasProperties();
    }

    public boolean isEmpty() {
        return delegateCompositeProperty.isEmpty();
    }

    public Iterator iterator() {
        return delegateCompositeProperty.iterator();
    }

    public boolean remove(Object o) {
        return delegateCompositeProperty.remove(o);
    }

    public boolean removeAll(Collection c) {
        return delegateCompositeProperty.removeAll(c);
    }

    public void replace(CompositeProperty oldProperty, CompositeProperty newProperty) {
        delegateCompositeProperty.replace(oldProperty, newProperty);
    }

    public CompositeProperty replace(CompositeProperty oldProperty, Object newValue) {
        return delegateCompositeProperty.replace(oldProperty, newValue);
    }

    public boolean retainAll(Collection c) {
        return delegateCompositeProperty.retainAll(c);
    }

    public int size() {
        return delegateCompositeProperty.size();
    }

    public Object[] toArray() {
        return delegateCompositeProperty.toArray();
    }

    public Object[] toArray(Object[] a) {
        return delegateCompositeProperty.toArray(a);
    }

    @Override
    public String toString() {
        return "ComparableProperty{" + "identityResolverRegistry=" + identityResolverRegistry + ", originalIndex=" + originalIndex + ", id=" + id + ", delegateCompositeProperty=" + delegateCompositeProperty + '}';
    }

    private CompositeProperty delegateCompositeProperty;
    private Registry<? extends IdentityResolver> identityResolverRegistry;
    private int originalIndex;
    private Comparable id;

}
