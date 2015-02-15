package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;

/**
 * Repräsentiert ein Object (ohne dessen Attribute) als Property. Verträgt auch
 * null
 *
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class ObjectProperty<T>  implements Property<T> {

    public ObjectProperty(T object, String name, String displayName, String description) {
        this(object, name, displayName);
        this.description = description;
    }

    public ObjectProperty(T object, String name, String displayName) {
        this.underlyingObject = object;
        this.name = name;
        this.displayName = displayName;
    }

    public String description() {
        return description;
    }

    public String name() {
        return name;
    }

    public String displayName() {
        return displayName;
    }

    public Class type() {
        return underlyingObject != null ? underlyingObject.getClass() : null;
    }

    public Collection<Annotation> annotations() {
        return underlyingObject != null ? Arrays.asList(underlyingObject.getClass().getAnnotations()) : new ArrayList();
    }

    public T value() {
        return underlyingObject;
    }

    public CompositeProperty value(T value) {
        // TODO os: supported?
        underlyingObject = value;
        return null;
    }

    @Override
    public String toString() {
        return "ObjectProperty{" +"underlyingObject=" + underlyingObject + ", name=" + name + ", displayName=" + displayName + ", description=" + description + '}';
    }

    private T underlyingObject;
    private String name;
    private String displayName;
    private String description;

}
