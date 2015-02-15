package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.PropertyDescription;
import org.jodt.reflection.PrivilegedReflectionUtil;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class ReflectiveProperty<T> implements Property<T> {

    private Field field;
    private Object fieldOwner;
    private String displayName;

    public ReflectiveProperty(Field field, Object fieldOwner, String displayName) {
        this.field = field;
        this.fieldOwner = fieldOwner;
        this.displayName = displayName;
    }

    public String description() {
        PropertyDescription property = field.getAnnotation(PropertyDescription.class);
        return property != null ? property.description() : "no description available";
    }

    public String name() {
        return field.getName();
    }

    public String displayName() {
        return displayName;
    }

    public Class type() {
        return field.getType();
    }

    public T value() {
        try {
            return (T) PrivilegedReflectionUtil.getValue(fieldOwner, field);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    public CompositeProperty value(Object value) {
        try {
            PrivilegedReflectionUtil.setValueViaSetter(fieldOwner, field, value);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            try {
                PrivilegedReflectionUtil.setValue(fieldOwner, field, value);
            } catch (IllegalArgumentException e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            } catch (IllegalAccessException e1) {
                // TODO Auto-generated catch block
                throw new RuntimeException(e1);
            }
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
        return null;
    }

    public Collection<Annotation> annotations() {
        Collection<Annotation> result = Arrays.asList(field.getDeclaredAnnotations());
        return result;
    }

    @Override
    public String toString() {
        return "ReflectiveProperty{" + "field=" + field + ", fieldOwner=" + fieldOwner + ", displayName=" + displayName + '}';
    }

}
