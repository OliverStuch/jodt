package org.jodt.property.implementation;

import java.util.Collection;
import java.util.Set;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 * Container für ReflectiveProperties TODO: typsicher machen
 */
public class ReflectivePropertySet<T> extends DefaultCompositePropertySet<T> {

    public ReflectivePropertySet(Property property, Set<CompositeProperty<?>> propertySet, CompositeProperty parent) {
        super(property, propertySet, parent);
    }

    @Override
    public boolean add(CompositeProperty e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends CompositeProperty<?>> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void replace(CompositeProperty oldProperty, CompositeProperty newProperty) {
        InternalCompositeProperty iProperty = (InternalCompositeProperty) oldProperty;
        InternalCompositeProperty iNewProperty = (InternalCompositeProperty) newProperty;
        iProperty.setValue(newProperty.value());
        iProperty.properties(iNewProperty.properties());
    }

    public CompositeProperty replace(CompositeProperty oldProperty, Object newValue) {
        // TODO sanity checks (newValue must be type compatible...)
        if (oldProperty.value() == null) {
            CompositeProperty newProperty = propertyFactory.createCompositeProperty(newValue, oldProperty.name(), parent);
            replace(oldProperty, newProperty);
            return oldProperty;
        }
        return null;
    }
}
