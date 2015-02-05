package org.jodt.property.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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

    public DefaultPropertyTool(boolean ignoreStaticFields, PropertyToolConfiguration propertyToolConfiguration) {
        this.ignoreStaticFields = ignoreStaticFields;
        this.configuration = propertyToolConfiguration;
    }

    public DefaultPropertyTool(boolean ignoreStaticFields) {
        this(ignoreStaticFields, new DefaultPropertyToolConfiguration());
    }

    public DefaultPropertyTool() {
        this(true);
    }

    // PropertyTool
    public <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new ShallowStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new OneLevelRecursiveStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), null, new RecursiveStrategy());
    }

    // InternalPropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name, CompositeProperty<?> parent) {
        return recursiveCreateCompositeProperty(object, type(object), name, new PropertyProvider(object, name), parent, new RecursiveStrategy());
    }

    // TODO Map, Array, SortedSet
    private CompositeProperty recursiveCreateCompositeProperty(Object object, Class type, String name, PropertyProvider propertyProvider,
            CompositeProperty parent, Strategy strategy) {
        if (configuration.isIgnored(type)) {
            return null;
        }
        if(configuration.isIgnored(name)){
            return null;
        }
        if (this.configuration.isPrimitive(object, type)) {
            // create CompositePropery for "primitive"
            return new DefaultCompositePropertySet(propertyProvider.provide(), parent);
        } else if (Set.class.isAssignableFrom(type)) {
            Set objectAsSet = (Set) object;
            Set<CompositeProperty> propertySet = new HashSet();
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), propertySet, parent);
            strategy.addElements(propertySet, objectAsSet, result);
            return result;
        } else if (List.class.isAssignableFrom(type)) {
            List objectAsList = (List) object;
            List<CompositeProperty<?>> propertyList = new ArrayList();
            CompositeProperty result = new DefaultCompositePropertyList(propertyProvider.provide(), propertyList, parent);
            strategy.addElements(propertyList, objectAsList, result);
            return result;
        } else { // kein special => reflection
            Set<Property> objectAsReflectivePropertySet = createReflectivePropertySet(object); // object as reflectivePropertySet
            Set<CompositeProperty> propertySet = new HashSet();
            ReflectivePropertySet result = new ReflectivePropertySet(propertyProvider.provide(), propertySet, parent);
            // Kann kein normales PropertySet sein, weil man keine Attribute aus einer
            // Klasse entfernen oder adden kann
            // obwohl das ein interessantes feature wäre ;-)
            strategy.addElements(propertySet, objectAsReflectivePropertySet, result);

            return result;

        }
    }

    interface Strategy {

        void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result);

        void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result);

        void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result);

    }

    class OneLevelRecursiveStrategy implements Strategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new PropertyProvider(property), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
                }
            }
        }

        public void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new PropertyProvider(elementOfList, name), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertyList.add(newCompositeProperty);
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new PropertyProvider(elementOfSet, name), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
                }
            }
        }
        private Strategy shallowStrategy = new ShallowStrategy();
    }

    class RecursiveStrategy implements Strategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<?> objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new PropertyProvider(elementOfSet, name), result, this);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
                }
            }
        }

        public void addElements(List<CompositeProperty<?>> propertyList, List<?> objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new PropertyProvider(elementOfList, name), result, this);
                if (newCompositeProperty != null) {
                    propertyList.add(newCompositeProperty);
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new PropertyProvider(property), result, this);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
                }
            }
        }

    }

    class ShallowStrategy implements Strategy {

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
     * @param object must be != null
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
            ReflectiveProperty reflectiveProperty = new ReflectiveProperty(field, object);
            properties.add(reflectiveProperty);
        }
        return properties;
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

    private Strategy strategy;
    private boolean ignoreStaticFields;
    private PropertyToolConfiguration configuration;

}
