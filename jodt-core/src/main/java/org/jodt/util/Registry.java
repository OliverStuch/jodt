package org.jodt.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */
public class Registry<T> implements Serializable {
    public void register(Class<?> clazz, T implementation) {
        if (implementation != null) {
            implementationByClass.put(clazz, implementation);
        } else {
            implementationByClass.remove(clazz);
        }
    }

    public void setImplementation(Class<?> clazz, T implementation) {
        register(clazz, implementation);
    }

    /**
     * die Implemtierung sucht zuerst exakt für die angegebene Class registrierte Impl, danach ob es für ein von der Klasse implementiertes Interface eine registrierte Impl gibt ,danach wird
     * im Fall eines Primitive nach Impl für den Wrapper gesucht. Ist das alles erfolglos, wird rekursiv für die superklasse gesucht
     * 
     */
    public T getImplementation(Class<?> clazz) {
        T implementation = implementationByClass.get(clazz);
        if (implementation != null) {
            return implementation;
        }

        if (clazz == null) {
            return null;
        } else {
            Class[] interfaces = clazz.getInterfaces();

            for (Class interfaze : interfaces) {
                implementation = implementationByClass.get(interfaze);
                if (implementation != null) {
                    return implementation;
                }
            }

            // primitive wrapping

            if (clazz == int.class) {
                return getImplementation(Integer.class);
            }

            if (clazz == long.class) {
                return getImplementation(Long.class);
            }
            if (clazz == float.class) {
                return getImplementation(Float.class);
            }
            if (clazz == double.class) {
                return getImplementation(Double.class);
            }
            if (clazz == boolean.class) {
                return getImplementation(Boolean.class);
            }

            return getImplementation(clazz.getSuperclass());
        }
    }

    public void addAll(Registry<T> other) {
        for (Class clazz : other.implementationByClass.keySet()) {
            setImplementation(clazz, other.getImplementation(clazz));
        }
    }

    public String toString() {
        return implementationByClass.toString();
    }

    private Map<Class<?>, T> implementationByClass = new HashMap<Class<?>, T>();
}
