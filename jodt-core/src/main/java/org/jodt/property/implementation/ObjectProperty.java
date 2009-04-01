package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;


/**
 * Repräsentiert ein Object (ohne dessen Attribute) als Property.
 * Verträgt auch null
 * @author Oliver Stuch  (oliver@stuch.net) 
 */
public class ObjectProperty<T> extends AbstractProperty<T> implements Property<T> {

    private T underlyingObject;
    private String name;
    private String description;

    public ObjectProperty(T object, String name, String description) {
        this(object, name);
        this.description = description();
    }

    public ObjectProperty(T object, String name) {
        this.underlyingObject = object;
        this.name = name;
    }

    public String description() {
        return description;
    }

    public String name() {
        return name;
    }

    public Class type() {
        return  underlyingObject != null ? underlyingObject.getClass() : null;
    }
    
    public Collection<Annotation> annotations() {
        return  underlyingObject != null ? Arrays.asList(underlyingObject.getClass().getAnnotations()) : new ArrayList();
    }

    public T value() {
        return underlyingObject;
    }

    public CompositeProperty value(T value) {
        // TODO os: supported?
        underlyingObject = value;
        return null;
    }



}