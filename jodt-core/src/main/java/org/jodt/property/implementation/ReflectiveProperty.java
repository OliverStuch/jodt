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
    private String renderedFieldName;

    public ReflectiveProperty(Field field, Object fieldOwner, String renderedFieldName) {
        this.field = field;
        this.fieldOwner = fieldOwner;
        this.renderedFieldName = renderedFieldName;
    }

    public String description() {
        PropertyDescription property = field.getAnnotation(PropertyDescription.class);
        return property != null ? property.description() : "no description available";
    }

    public String name() {
//        return field.getName();
        return renderedFieldName;
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

    public String toString() {
        return field.toString();
    }

    public Collection<Annotation> annotations() {
        Collection<Annotation> result = Arrays.asList(field.getDeclaredAnnotations());
        return result;
    }

}
