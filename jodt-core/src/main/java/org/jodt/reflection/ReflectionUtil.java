package org.jodt.reflection;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jodt.property.NoProperty;

/**
 * @author Oliver Stuch
 */
public class ReflectionUtil extends JavaTypeDetector {

    public static interface SizedIterator extends Iterator {

        int size();
    }

    private static class ArrayIterator implements SizedIterator {

        private Object[] elements;
        private int index;

        private ArrayIterator(Object[] objects) {
            this.elements = objects;
        }

        public boolean hasNext() {
            return index < elements.length;
        }

        public Object next() {
            return this.elements[index++];
        }

        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public int size() {
            return elements.length;
        }
    }

    private static class PrimitveArrayIterator implements SizedIterator {

        private Object array;
        private int length;
        private int index;

        private PrimitveArrayIterator(Object array) {
            this.array = array;
            this.length = Array.getLength(array);
        }

        public boolean hasNext() {
            return index < length;
        }

        public Object next() {
            return Array.get(array, index++);
        }

        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public int size() {
            return length;

        }
    }

    private static class NullIterator implements SizedIterator {

        public boolean hasNext() {
            return false;
        }

        public Object next() {
            throw new UnsupportedOperationException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return 0;
        }
    }

    public static class OneElementIterator implements SizedIterator {

        private Object element;
        private boolean nextCalled;

        private OneElementIterator(Object object) {
            this.element = object;
        }

        public boolean hasNext() {
            if (nextCalled) {
                return false;
            } else {
                return true;
            }
        }

        public Object next() {
            this.nextCalled = true;

            return this.element;
        }

        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public int size() {
            return 1;
        }
    }

    public static class CollectionIterator implements SizedIterator {

        private Collection collection;
        private Iterator iterator;

        public CollectionIterator(Collection collection) {
            this.collection = collection;
            this.iterator = collection.iterator();
        }

        public int size() {
            return this.collection.size();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return iterator.next();
        }

        public void remove() {
            iterator.remove();
        }

    }

    public static SizedIterator getElementIterator(Object object) {
        if (object == null) {
            return new NullIterator();
        }

        Class[] implementedInterfaces = object.getClass().getInterfaces();
        String[] implementedInterfacesNames = new String[implementedInterfaces.length];

        for (int i = 0; i < implementedInterfaces.length; i++) {
            implementedInterfacesNames[i] = implementedInterfaces[i].getName();
        }

        if (Collection.class.isAssignableFrom(object.getClass())) {
            return new CollectionIterator((Collection) object);
        } else if (Map.class.isAssignableFrom(object.getClass())) {
            Collection componentElements = ((Map) object).values();
            return new CollectionIterator(componentElements);
        } else if (object.getClass().isArray()) {
            if (object.getClass().getComponentType().isPrimitive()) {
                return new PrimitveArrayIterator(object);
            } else {
                return new ArrayIterator((Object[]) object);
            }
        } else {
            return new OneElementIterator(object);
        }
    }

    private static boolean contains(Object requiredClass, Object[] clazzes) {
        boolean requiredClassFound = false;

        for (int i = 0; i < clazzes.length; i++) {
            if (clazzes[i].equals(requiredClass)) {
                requiredClassFound = true;
            }
        }

        return requiredClassFound;
    }

    private static boolean contains(Object[] requiredClasses, Object[] clazzes) {
        boolean requiredClassFound = false;

        for (int i = 0; i < requiredClasses.length; i++) {
            if (contains(requiredClasses[i], clazzes)) {
                requiredClassFound = true;
            }
        }

        return requiredClassFound;
    }

    public static int size(Object object) {
        Class[] implementedInterfaces = object.getClass().getInterfaces();
        String[] implementedInterfacesNames = new String[implementedInterfaces.length];

        for (int i = 0; i < implementedInterfaces.length; i++) {
            implementedInterfacesNames[i] = implementedInterfaces[i].getName();
        }

        if (Collection.class.isAssignableFrom(object.getClass())) { // Ist

            // componente
            // eine
            // Collection?
            return ((Collection) object).size();
        } else if (Map.class.isAssignableFrom(object.getClass())) { // Ist

            // componente
            // eine
            // Mappe?
            Collection componentElements = ((Map) object).values();

            return componentElements.size();
        } else if (object.getClass().getName().startsWith("[")) {
            return ((Object[]) object).length;
        } else if (object instanceof String) {
            return ((object == null) || ((String) object).trim().equals("")) ? 0 : 1;
        }

        return (object != null) ? 1 : 0;
    }

    public interface Predicate {

        boolean evaluate(Object object);
    }

    public static List<Field> getAllProperties(Class delinquentClass) {
        List<Field> fields = getAllFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(NoProperty.class)) {
                result.add(field);
            }
        }
//        result.sort(new Comparator<Field>() {
//            public int compare(Field o1, Field o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
        return result;
    }

    public static List<Field> getAllNonStaticProperties(Class delinquentClass) {
        List<Field> fields = getAllFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(NoProperty.class)) {
                int modifiers = field.getModifiers();
                if (!Modifier.isStatic(modifiers)) {
                    result.add(field);
                }
            }
        }
//        result.sort(new Comparator<Field>() {
//            public int compare(Field o1, Field o2) {
//                return o1.getName().compareTo(o2.getName());
//            }
//        });
        return result;
    }

    /**
     * Get all fields (this class + superclasses) with the given annotation
     * present
     */
    public static List<Field> getAllAnnotatedFields(Class delinquentClass, Class annotation) {
        List<Field> fields = getAllFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                result.add(field);
            }
        }
        return result;
    }

    /**
     * Get all fields (this class + superclasses) with the given annotation
     * present
     */
    public static List<Field> getDeclaredAnnotatedFields(Class delinquentClass, Class annotation) {
        List<Field> fields = getDeclaredFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotation)) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<Field> getNonTransientFields(Class delinquentClass) {
        List<Field> fields = getAllFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isTransient(modifiers)) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<Field> getAllNonStaticFields(Class delinquentClass) {
        List<Field> fields = getAllFields(delinquentClass);
        List<Field> result = new ArrayList<Field>();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<Field> getAllFields(Class delinquentClass) {
        List<Field> result = new ArrayList<Field>();

        Field[] fields = delinquentClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            result.add(fields[i]);
        }

        Class superclass = delinquentClass.getSuperclass();

        if (superclass != null) {
            result.addAll(getAllFields(superclass));
        }

        return result;
    }

    public static List<Field> getDeclaredFields(Class delinquentClass) {
        List<Field> result = new ArrayList<Field>();

        Field[] fields = delinquentClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            result.add(fields[i]);
        }

        return result;
    }

    private static List getAllInterfaces(Class clazz) {
        List result = new ArrayList();

        Class[] foundInterfaces = clazz.getInterfaces();

        for (int i = 0; i < foundInterfaces.length; i++) {
            result.add(foundInterfaces[i]);
        }

        Class superclass = clazz.getSuperclass();

        if (superclass != null) {
            result.addAll(getAllInterfaces(superclass));
        }

        return result;
    }

    private static List getAllSuperclasses(Class clazz) {
        List result = new ArrayList();
        Class superclass = clazz.getSuperclass();

        if (superclass != null) {
            result.add(clazz.getSuperclass());
            result.addAll(getAllSuperclasses(superclass));
        }

        return result;
    }

    // public boolean genericEquals(Object object1, Object object2) {
    // MultilineObjectVisitor objectVisitor1 = new MultilineObjectVisitor();
    // MultilineObjectVisitor objectVisitor2 = new MultilineObjectVisitor();
    //
    // new ObjectExplorerNonRecursive(objectVisitor1).process(object1);
    // new ObjectExplorerNonRecursive(objectVisitor2).process(object2);
    //
    // return object1.toString().equals(objectVisitor2.toString());
    // }
}
