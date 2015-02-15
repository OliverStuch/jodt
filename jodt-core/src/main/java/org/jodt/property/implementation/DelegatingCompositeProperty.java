package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.jodt.property.CompositeProperty;
import org.jodt.property.InternalPropertyTool;
import org.jodt.property.Property;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
abstract public class DelegatingCompositeProperty<T> implements InternalCompositeProperty<T>, CompositeProperty<T> {

    public DelegatingCompositeProperty(Property<T> delegate, CompositeProperty<?> parent) {
        this.delegate = delegate;
        parent(parent);
    }

    public String description() {
        return delegate.description();
    }

    public String name() {
        return delegate.name();
    }

    public String displayName() {
        return delegate.displayName();
    }

    public Class type() {
        return delegate.type();
    }

    public Collection<Annotation> annotations() {
        return delegate.annotations();
    }

    public T value() {
        return delegate.value();
    }

    public void setValue(T value) {
        delegate.value(value);
    }

    public CompositeProperty<T> value(T value) {
        CompositeProperty<T> newProperty = parent.replace(this, value);
        setValue(value);
        return newProperty;
    }

    protected CompositeProperty createProperty(CompositeProperty oldProperty, Object value) {
        if (!hasProperties()) {
            throw new IllegalArgumentException("CompositeProperty " + this + " has no properties.");
        } else {
            if (!properties().contains(oldProperty)) {
                throw new IllegalArgumentException("property " + oldProperty + " not found.");
            } else {
                return propertyFactory.createCompositeProperty(value, oldProperty.name(), this);
            }
        }
    }

    public boolean hasProperties() {
        return (properties() != null && properties().size() != 0);
    }

    public CompositeProperty find(String name) {
        for (CompositeProperty compositeProperty : properties()) {
            if (name.equals(compositeProperty.name())) {
                return compositeProperty;
            }
        }
        return null; // nothing found
    }

    public CompositeProperty find(Object value) {
        for (CompositeProperty compositeProperty : properties()) {
            if (value.equals(compositeProperty.value())) {
                return compositeProperty;
            }
        }
        return null; // nothing found
    }

    public void parent(CompositeProperty parent) {
        this.parent = parent;
    }

    public CompositeProperty parent() {
        return this.parent;
    }

    @Override
    public String toString() {
        return "DelegatingCompositeProperty{" + "delegate=" + delegate + ", parent=" + parent + ", propertyFactory=" + propertyFactory + '}';
    }
    
    private Property<T> delegate;
    CompositeProperty parent;
    InternalPropertyTool propertyFactory = new DefaultPropertyTool();
}
