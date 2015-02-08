package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.jodt.property.Property;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class PropertyUtil {

    public static String name(Property property) {
        return property != null ? property.name() : null;
    }

    public static String displayName(Property property) {
        return property != null ? property.displayName() : null;
    }

    public static Class type(Property property) {
        return property != null ? property.type() : null;
    }

    public static Object value(Property propery) {
        return propery != null ? propery.value() : null;
    }

    public static Collection<Annotation> annotations(Property property) {
        return property != null ? property.annotations() : null;
    }

    public static boolean isAnnotationPresent(Property property, Class<?> annotationClass) {
        if (property != null) {
            for (Annotation annotation : annotations(property)) {
                if (annotationClass.isAssignableFrom(annotation.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

}
