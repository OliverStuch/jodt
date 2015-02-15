package org.jodt.property.implementation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public static String toString(Object[] objects, String delim) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                if (objects[i] != null) {
                    stringBuffer.append(objects[i].toString()).append(delim);
                }
            }
        }
        return stringBuffer.toString();
    }

    public static String toString(Object[] objects) {
        return toString(objects, " ");
    }

    private static String safeToString(Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return null;
        }
    }

    public static String toString(Collection collection) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        for (Object element : collection) {
            stringBuffer.append(safeToString(element)).append(" ");
        }
        return stringBuffer.toString();
    }

    public static String toString(Set set, String delim) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        for (Object element : set) {
            stringBuffer.append(safeToString(element)).append(delim);
        }
        return stringBuffer.toString();
    }

    public static String toString(Set set) {
        return toString(set, " ");
    }

    public static String toString(List list) {
        return toString(list, " ");
    }

    public static String toString(List list, String delim) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        for (Object element : list) {
            stringBuffer.append(safeToString(element)).append(delim);
        }
        return stringBuffer.toString();
    }

    public static String toString(Map map) {
        return toString(map, " ");
    }

    public static String toString(Map map, String delim) {
        StringBuilder stringBuffer = new StringBuilder("\n");
        for (Object key : map.keySet()) {
            Object element = map.get(key);
            stringBuffer.append(safeToString(key)).append(" -> ").append(safeToString(element)).append(delim);
        }
        return stringBuffer.toString();
    }

    public static <T> T deepClone(T template) {
        if (template == null) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(template);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
