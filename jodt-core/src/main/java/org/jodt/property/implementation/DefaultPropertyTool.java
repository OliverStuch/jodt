package org.jodt.property.implementation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
//import java.util.TreeSet;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.PropertyActor;
import org.jodt.property.PropertyTool;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.reflection.ReflectionUtil;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultPropertyTool implements PropertyTool {

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

    private String displayName(String name) {
        return configuration.renderAttributeName(name);
    }

    // PropertyTool
    public <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new ObjectPropertyProvider(object, name, displayName(name)), null, new ShallowStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new ObjectPropertyProvider(object, name, displayName(name)), null, new OneLevelRecursionStrategy());
    }

    // PropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name) {
        return recursiveCreateCompositeProperty(object, type(object), name, new ObjectPropertyProvider(object, name, displayName(name)), null, new FullRecursionStrategy());
    }

    // InternalPropertyTool
    public <T> CompositeProperty<T> createCompositeProperty(T object, String name, CompositeProperty<?> parent) {
        return recursiveCreateCompositeProperty(object, type(object), name, new ObjectPropertyProvider(object, name, displayName(name)), parent, new FullRecursionStrategy());
    }

    // TODO Map, Array, SortedSet
    /**
     *
     * @param object
     * @param type
     * @param name NICHT displayName: Hier wird z.B. das Ignorieren geregelt
     * @param propertyProvider
     * @param parent
     * @param recursionStrategy
     * @return
     */
    private CompositeProperty recursiveCreateCompositeProperty(Object object, Class type, String name, ObjectPropertyProvider propertyProvider,
            CompositeProperty parent, RecursionStrategy recursionStrategy) {
        if (configuration.isIgnored(type)) {
            return null;
        } else if (configuration.isIgnored(name)) {
            return null;
        } else if (configuration.isPrimitive(object, type)) {
            // create CompositePropery for "primitive"
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), parent, this);
            applyPropertyActor(result);
            return result;
        } else if (Set.class.isAssignableFrom(type)) {
            Set objectAsSet = (Set) object;
            Set<CompositeProperty> propertySet = new TreeSet(new PropertyValueComparator(this.configuration));
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), propertySet, parent, this);
            recursionStrategy.addElements(propertySet, objectAsSet, result);
            return result;
        } else if (List.class.isAssignableFrom(type)) {
            List objectAsList = (List) object;
            List<CompositeProperty> propertyList = new ArrayList();
            CompositeProperty result = new DefaultCompositePropertyList(propertyProvider.provide(), propertyList, parent, this);
            recursionStrategy.addElements(propertyList, objectAsList, result);
            return result;
        } else if (Map.class.isAssignableFrom(type)) {
            Map objectAsMap = (Map) object;
            Set<CompositeProperty> propertySet = new HashSet();
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), propertySet, parent, this);
            recursionStrategy.addElements(propertySet, objectAsMap, result);
            return result;
        } else if (configuration.isTerminal(object)) {
            CompositeProperty result = new DefaultCompositePropertySet(propertyProvider.provide(), parent, this);
            applyPropertyActor(result);
            return result;
        } else { // kein special => reflection
            Set<Property> objectAsReflectivePropertySet = createReflectivePropertySet(object); // object as reflectivePropertySet
            Set<CompositeProperty> propertySet = new TreeSet(new PropertyNameComparator());
            ReflectivePropertySet result = new ReflectivePropertySet(propertyProvider.provide(), propertySet, parent, this);
            // Kann kein normales PropertySet sein, weil man keine Attribute aus einer
            // Klasse entfernen oder adden kann
            // obwohl das ein interessantes feature wäre ;-)
            recursionStrategy.addElements(propertySet, objectAsReflectivePropertySet, result);
            applyPropertyActor(result);
            return result;

        }
    }

    private boolean applyPropertyActor(CompositeProperty compositeProperty) {
        PropertyActor classPropertyActor = configuration.getPropertyActor(compositeProperty.type());
        if (classPropertyActor != null) {
            return classPropertyActor.actOn(compositeProperty);
        }
        PropertyActor attributeNamePropertyActor = configuration.getPropertyActor(compositeProperty.name());
        if (attributeNamePropertyActor != null) {
            return attributeNamePropertyActor.actOn(compositeProperty);
        }
        return false;
    }

    interface RecursionStrategy {

        void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result);

        void addElements(List<CompositeProperty> propertyList, List objectAsList, CompositeProperty result);

        void addElements(Set<CompositeProperty> propertySet, Set objectAsSet, CompositeProperty result);

        void addElements(Set<CompositeProperty> propertySet, Map objectAsMap, CompositeProperty result);

    }

    class OneLevelRecursionStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new ObjectPropertyProvider(property), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertySet.size());
                }
            }
        }

        public void addElements(List<CompositeProperty> propertyList, List objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new ObjectPropertyProvider(elementOfList, name, displayName(name)), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertyList.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertyList.size());
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new ObjectPropertyProvider(elementOfSet, name, displayName(name)), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertySet.size());
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Map objectAsMap, CompositeProperty result) {
            for (Object keyObject : objectAsMap.keySet()) {
                Reference reference = new Reference(keyObject, objectAsMap.get(keyObject));
                String name = "->";
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(reference, type(reference), name, new ObjectPropertyProvider(reference, name, displayName(name)), result, shallowStrategy);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertySet.size());
                }
            }
        }
        private RecursionStrategy shallowStrategy = new ShallowStrategy();
    }

    class FullRecursionStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set objectAsSet, CompositeProperty result) {
            for (Object elementOfSet : objectAsSet) {
                String name = null; // TODO: Hier vielleicht IdResolver? Könnte Auswirkungen auf 
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfSet, type(elementOfSet), name, new ObjectPropertyProvider(elementOfSet, name, displayName(name)), result, this);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertySet.size());
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Map objectAsMap, CompositeProperty result) {
            for (Object keyObject : objectAsMap.keySet()) {
                Reference reference = new Reference(keyObject, objectAsMap.get(keyObject));
                String name = "->";
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(reference, type(reference), name, new ObjectPropertyProvider(reference, name, displayName(name)), result, this);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertySet.size());
                }
            }
        }

        public void addElements(List<CompositeProperty> propertyList, List objectAsList, CompositeProperty result) {
            for (int i = 0; i < objectAsList.size(); i++) {
                Object elementOfList = objectAsList.get(i);
                String name = "" + i;
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(elementOfList, type(elementOfList), name, new ObjectPropertyProvider(elementOfList, name, displayName(name)), result, this);
                if (newCompositeProperty != null) {
                    propertyList.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() + " size:" + propertyList.size());
                }
            }
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            for (Property property : objectAsReflectivePropertySet) {
                CompositeProperty newCompositeProperty = recursiveCreateCompositeProperty(property.value(), property.type(), property.name(), new ObjectPropertyProvider(property), result, this);
                if (newCompositeProperty != null) {
                    propertySet.add(newCompositeProperty);
//                    logger.debug("add " + newCompositeProperty.name() + ":" + newCompositeProperty.value() +" size:" + propertySet.size());
                }
            }
        }

    }

    class ShallowStrategy implements RecursionStrategy {

        public void addElements(Set<CompositeProperty> propertySet, Set objectAsSet, CompositeProperty result) {
            // do nothing
        }

        public void addElements(List<CompositeProperty> propertyList, List objectAsList, CompositeProperty result) {
            // do nothing
        }

        public void addElements(Set<CompositeProperty> propertySet, Set<Property> objectAsReflectivePropertySet, ReflectivePropertySet result) {
            // do nothing
        }

        public void addElements(Set<CompositeProperty> propertySet, Map objectAsMap, CompositeProperty result) {
            // do nothing
        }

    }

    private Class type(Object object) {
        return object != null ? object.getClass() : null;
    }

    private static class ObjectPropertyProvider<T> {

        private Property<T> property;

        public ObjectPropertyProvider(Property<T> property) {
            this.property = property;
        }

        public ObjectPropertyProvider(Object object, String name, String displayName) {
            this.property = new ObjectProperty(object, name, displayName);
        }

        public Property<T> provide() {
            return property;
        }
    }

    /**
     * @param object must be != null
     */
    private Set<Property> createReflectivePropertySet(Object object) {
        Set<Property> properties = new TreeSet<Property>(new PropertyNameComparator());
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
            ReflectiveProperty reflectiveProperty = new ReflectiveProperty(field, object, configuration.renderAttributeName(field.getName()));
            properties.add(reflectiveProperty);
        }
        return properties;
    }

    public List<CompositeProperty> createPropertyList(Collection<CompositeProperty> properties) {
        List<CompositeProperty> propertyList = new ArrayList(properties);
        Collections.sort(propertyList, new Comparator<CompositeProperty>() {
            public int compare(CompositeProperty o1, CompositeProperty o2) {
                return o1.name().compareTo(o2.name()); // sollte egal sein, ob name() oder displayName()
            }
        });
        return propertyList;
    }

    public PropertyToolConfiguration configure() {
        return configuration;
    }

    private boolean ignoreStaticFields;
    private PropertyToolConfiguration configuration;
    private static final Logger logger = Logger.getLogger(DefaultPropertyTool.class);
}
