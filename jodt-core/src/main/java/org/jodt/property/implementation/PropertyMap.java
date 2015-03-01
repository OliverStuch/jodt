package org.jodt.property.implementation;

import java.util.Collection;
import java.util.Iterator;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.PropertyTool;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class PropertyMap<T> extends DelegatingCompositeProperty<T> {

    public PropertyMap(Property delegate, CompositeProperty parent, PropertyTool propertyTool) {
        super(delegate, parent, propertyTool);
        // TODO Auto-generated constructor stub
    }

    public Collection<CompositeProperty<?>> properties() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void properties(Collection<CompositeProperty<?>> properties) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void replace(CompositeProperty oldProperty, CompositeProperty newProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CompositeProperty replace(CompositeProperty oldProperty, Object newValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean add(CompositeProperty<?> e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends CompositeProperty<?>> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void clear() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<CompositeProperty<?>> iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public int size() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public <P> P[] toArray(P[] a) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String displayName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "PropertyMap{" + super.toString()+ '}';
    }
    
    
}