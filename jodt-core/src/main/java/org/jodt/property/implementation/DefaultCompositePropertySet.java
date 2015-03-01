package org.jodt.property.implementation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jodt.property.CompositeProperty;
import org.jodt.property.CompositePropertySet;
import org.jodt.property.Property;
import org.jodt.property.PropertyTool;

/**
 * Diese Klasse wird erzeugt, wenn DefaultCompositePropertyFactory auf ein Set
 * von Objekten trifft
 *
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultCompositePropertySet<T> extends DelegatingCompositeProperty<T> implements CompositePropertySet<T> {

//    public PropertySet(Object object, Set<CompositeProperty> properties, String name, CompositeProperty parent) {
//        this(new ObjectProperty(object, name), parent);
//        this.properties = properties;
//    }
    public DefaultCompositePropertySet(Property property, CompositeProperty parent, PropertyTool propertyTool) {
        super(property, parent, propertyTool);
        this.subProperties = new HashSet();
    }

    public DefaultCompositePropertySet(Property property, Set<CompositeProperty<?>> subProperties, CompositeProperty parent, PropertyTool propertyTool) {
        this(property, parent, propertyTool);
        this.subProperties = subProperties;
    }

    public void replace(CompositeProperty oldProperty, CompositeProperty newProperty) {
        remove(oldProperty);
        add(newProperty);
    }

    public CompositeProperty replace(CompositeProperty oldProperty, Object newObject) {
        CompositeProperty newProperty = createProperty(oldProperty, newObject);
        replace(oldProperty, newProperty);
        return newProperty;
    }

    public Collection<CompositeProperty<?>> properties() {
        return subProperties;
    }

    public void properties(Collection<CompositeProperty<?>> properties) {
        this.subProperties = (Set<CompositeProperty<?>>) properties;
    }

    // Set-Delegates
    public boolean add(CompositeProperty property) {
        Set underlyingSet = (Set) value();
        underlyingSet.add(property.value());
        return subProperties.add(property);
    }

    public boolean addAll(Collection<? extends CompositeProperty<?>> c) {
        return subProperties.addAll(c);
    }

    public void clear() {
        subProperties.clear();
    }

    public boolean contains(Object o) {
        return subProperties.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return subProperties.containsAll(c);
    }

    public boolean isEmpty() {
        return subProperties.isEmpty();
    }

    public Iterator<CompositeProperty<?>> iterator() {
        return subProperties.iterator();
    }

    public boolean remove(Object o) {
        CompositeProperty compositeProperty = (CompositeProperty) o;
        Set underlyingSet = (Set) value();
        underlyingSet.remove(compositeProperty.value());
        return subProperties.remove(compositeProperty);
    }

    public boolean removeAll(Collection<?> c) {
        return subProperties.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return subProperties.retainAll(c);
    }

    public int size() {
        return subProperties.size();
    }

    public Object[] toArray() {
        return subProperties.toArray();
    }

    public <P> P[] toArray(P[] a) {
        return subProperties.toArray(a);
    }

    @Override
    public String toString() {
        return "DCPS{" + super.toString() + "subProperties=" + PropertyUtil.toString(subProperties, ", ") + '}';
    }

    private Set<CompositeProperty<?>> subProperties;

}
