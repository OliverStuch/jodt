package org.jodt.property.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jodt.property.CompositeProperty;
import org.jodt.property.InternalPropertyTool;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.reflection.ReflectionUtil;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */

public class DefaultPropertyTool implements InternalPropertyTool {

    public DefaultPropertyTool(boolean ignoreStaticFields) {
        this.ignoreStaticFields = ignoreStaticFields;
        this.configuration = new DefaultPropertyToolConfiguration();
    }

    public DefaultPropertyTool() {
        this(true);
    }

    // PropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name) {
        analyzedProperties = new HashMap();
        reflectiveProperties = new DoubleKeyMap<Object, Field, ReflectiveProperty>();
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new RecursiveStrategy());
    }

    // InternalPropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name, CompositeProperty<?> parent) {
        analyzedProperties = new HashMap();
        reflectiveProperties = new DoubleKeyMap<Object, Field, ReflectiveProperty>();
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), parent, new RecursiveStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name) {
        analyzedProperties = new HashMap();
        reflectiveProperties = new DoubleKeyMap<Object, Field, ReflectiveProperty>();
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new ShallowStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name) {
        analyzedProperties = new HashMap();
        reflectiveProperties = new DoubleKeyMap<Object, Field, ReflectiveProperty>();
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new OneLevelRecursiveStrategy());
    }

    // TODO Map, Array, SortedSet
    private CompositeProperty recursiveCreateCompositeProperty(Object object, Class type, String name, PropertyProvider propertyProvider, CompositeProperty parent,
            RecursionStrategy recursionStrategy) {
        CompositeProperty alreadyAnalyzed = analyzedProperties.get(propertyProvider.provide());
        if (alreadyAnalyzed != null) {
            return alreadyAnalyzed;
        }
        if (this.configuration.isPrimitive(object, type)) {
            // create CompositePropery for "primitive"
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), parent);
            analyzedProperties.put(propertyProvider.provide(), result);
            return result;
        } else if (Set.class.isAssignableFrom(type)) {
            Set objectAsSet = (Set) object;
            Set<CompositeProperty> propertySet = new HashSet();
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), propertySet, parent);
            analyzedProperties.put(propertyProvider.provide(), result);
            recursionStrategy.addElements(propertySet, objectAsSet, result);
            return result;
        } else if (List.class.isAssignableFrom(type)) {
            List objectAsList = (List) object;
            List<CompositeProperty<?>> propertyList = new ArrayList();
            CompositeProperty result = new DefaultCompositePropertyList(propertyProvider.provide(), propertyList, parent);
            analyzedProperties.put(propertyProvider.provide(), result);
            recursionStrategy.addElements(propertyList, objectAsList, result);
            return result;
        } else { // kein special => reflection
            Set<Property> objectAsReflectivePropertySet = createReflectivePropertySet(object); // object as reflectivePropertySet
            Set<CompositeProperty> propertySet = new HashSet();
            ReflectivePropertySet result = new ReflectivePropertySet(propertyProvider.provide(), propertySet, parent);
            // Kann kein normales PropertySet sein, weil man keine Attribute aus einer
            // Klasse entfernen oder adden kann
            // obwohl das ein interessantes feature wäre ;-)
            analyzedProperties.put(propertyProvider.provide(), result);
            recursionStrategy.addElements(propertySet, objectAsReflectivePropertySet, result);
            return result;
        }
    }

    interface RecursionStrategy {

        void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result);

        void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result);

        void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result);

    }

    class OneLevelRecursiveStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                propertySet.add(recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new PropertyProvider(property), result, shallowStrategy));
            }
        }

        public void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                propertyList.add(recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new PropertyProvider(elementOfList, name), result, shallowStrategy));
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null;
                propertySet.add(recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new PropertyProvider(elementOfSet, name), result, shallowStrategy));
            }
        }

        private RecursionStrategy shallowStrategy = new ShallowStrategy();
    }

    class RecursiveStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null;
                propertySet.add(recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new PropertyProvider(elementOfSet, name), result, this));
            }
        }

        public void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                propertyList.add(recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new PropertyProvider(elementOfList, name), result, this));
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                propertySet.add(recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new PropertyProvider(property), result, this));
            }
        }

    }

    class ShallowStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result) {
            // do nothing
        }

        public void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result) {
            // do nothing
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            // do nothing
        }

    }

    private Class type(Object object) {
        return object != null ? object.getClass() : null;
    }

    private static class PropertyProvider<T> {
        private Property<T> property;

        public PropertyProvider(Property<T> property) {
            this.property = property;
        }

        public PropertyProvider(Object object, String name) {
            this.property = new ObjectProperty(object, name);
        }

        public Property<T> provide() {
            return property;
        }
    }

    /**
     * @param object
     *            must be != null
     */
    private Set<Property> createReflectivePropertySet(Object object) {
        Set<Property> properties = new HashSet<Property>();
        Class<?> clazz;
        List<Field> declaredFields;
        // if (object == null) {
        // declaredFields = new ArrayList();
        // clazz = null;
        // } else {
        clazz = object.getClass();
        if (ignoreStaticFields) {
            declaredFields = ReflectionUtil.getAllNonStaticProperties(clazz);
        } else {
            declaredFields = ReflectionUtil.getAllProperties(clazz);
        }

        // }

        for (Field field : declaredFields) {

            ReflectiveProperty reflectiveProperty;

            reflectiveProperty = reflectiveProperties.get(object, field);
            if (reflectiveProperty == null) {
                reflectiveProperty = new ReflectiveProperty(field, object);
                reflectiveProperties.put(object, field, reflectiveProperty);
            }
            properties.add(reflectiveProperty);
        }
        return properties;
    }

    private DoubleKeyMap<Object, Field, ReflectiveProperty> reflectiveProperties;

    private static class DoubleKeyMap<K1, K2, V> {
        public DoubleKeyMap() {
            dkm = new HashMap();
        }

        public V get(K1 key1, K2 key2) {
            Map<K2, V> key1resolvedMap = dkm.get(key1);
            if (key1resolvedMap == null) {
                return null;
            }
            return key1resolvedMap.get(key2);
        }
        
        public void put(K1 key1, K2 key2, V value) {
            Map<K2, V> key1resolvedMap = dkm.get(key1);
            if (key1resolvedMap == null) {
                key1resolvedMap = new HashMap();
                dkm.put(key1, key1resolvedMap);
            }
            key1resolvedMap.put(key2, value);
        }

        private Map<K1, Map<K2, V>> dkm;
    }

    public List<CompositeProperty> createPropertyList(Collection<CompositeProperty> properties) {
        List<CompositeProperty> propertyList = new ArrayList(properties);
        Collections.sort(propertyList, new Comparator<CompositeProperty>() {
            public int compare(CompositeProperty o1, CompositeProperty o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        return propertyList;
    }

    private RecursionStrategy strategy;
    private boolean ignoreStaticFields;
    private PropertyToolConfiguration configuration;
    private Map<Property, CompositeProperty> analyzedProperties = new HashMap();
}
