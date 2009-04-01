package org.jodt.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * a.k.a. The "ObjectMolester"
 * <p>
 * This class is used to access a method or field of an object no matter what the access modifier of the method or field. The syntax for accessing fields and methods is out of the ordinary
 * because this class uses reflection to peel away protection.
 * <p>
 * Here is an example of using this to access a private member. <code>resolveName</code> is a private method of <code>Class</code>.
 * 
 * <pre>
 * Class c = Class.class;
 * System.out.println(PrivilegedAccessor.invokeMethod(c, &quot;resolveName&quot;, &quot;/net/iss/common/PrivilegeAccessor&quot;));
 * </pre>
 * 
 * @author Charlie Hubbard (chubbard@iss.net)
 * @author Prashant Dhokte (pdhokte@iss.net)
 */

public class PrivilegedReflectionUtil {

    public static void setValue(Object instance, String fieldName, Object newValue) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        setValue(instance, getField(instance.getClass(), fieldName), newValue);
    }

    public static void setValue(Object instance, Field field, Object newValue) throws IllegalArgumentException, IllegalAccessException {
        boolean access = field.isAccessible();
        field.setAccessible(true);
        field.set(instance, newValue);
        field.setAccessible(access);
    }

    private static String capitalize(String input) {
        String firstChar = input.substring(0, 1);
        return firstChar.toUpperCase() + input.substring(1);
    }

    public static void setValueViaSetter(Object instance, Field field, Object newValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName = "set" + capitalize(field.getName());
        invokeMethod(instance, methodName, newValue);
    }

    /**
     * Gets the value of the named field and returns it as an object.
     * 
     * @param instance
     *            the object instance
     * @param fieldName
     *            the name of the field
     * @return an object representing the value of the field
     */
    public static Object getValue(Object instance, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = getField(instance.getClass(), fieldName);
        return getValue(instance, field);
    }

    /**
     * Gets the value of the givem field and returns it as an object.
     * 
     * @param instance
     *            the object instance
     * @param field
     *            the field
     * @return an object representing the value of the field
     */
    public static Object getValue(Object instance, Field field) throws IllegalAccessException, NoSuchFieldException {
        boolean access = field.isAccessible();
        field.setAccessible(true);
        Object result = field.get(instance);
        field.setAccessible(access);
        return result;
    }

    /**
     * Calls a method on the given object instance without argument.
     * 
     * @param instance
     *            the object instance
     * @param method
     *            the method to invoke
     * @param arg
     *            the argument to pass to the method
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object[])
     */
    public static Object invokeMethod(Object instance, Method method) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(instance, method, (Object[]) null);
    }

    /**
     * Calls a method on the given object instance without argument.
     * 
     * @param instance
     *            the object instance
     * @param methodName
     *            the name of the method to invoke
     * @param arg
     *            the argument to pass to the method
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object[])
     */
    public static Object invokeMethod(Object instance, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(instance, methodName, (Object[]) null);
    }

    /**
     * Calls a method on the given object instance with the given argument.
     * 
     * @param instance
     *            the object instance
     * @param method
     *            the method to invoke
     * @param arg
     *            the argument to pass to the method
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object[])
     * @return result of the invoked method
     */
    public static Object invokeMethod(Object instance, Method method, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = new Object[1];
        args[0] = arg;
        return invokeMethod(instance, method, args);
    }

    /**
     * Calls a method on the given object instance with the given argument.
     * 
     * @param instance
     *            the object instance
     * @param methodName
     *            the name of the method to invoke
     * @param arg
     *            the argument to pass to the method
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object[])
     */
    public static Object invokeMethod(Object instance, String methodName, Object arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object[] args = new Object[1];
        args[0] = arg;
        return invokeMethod(instance, methodName, args);
    }

    /**
     * Calls a method on the given object instance with the given arguments.
     * 
     * @param instance
     *            the object instance
     * @param method
     *            the method to invoke
     * @param args
     *            an array of objects to pass as arguments
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object)
     * @return result of the invoked method
     */
    public static Object invokeMethod(Object instance, Method accessMethod, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        boolean access = accessMethod.isAccessible();
        accessMethod.setAccessible(true);
        Object result = accessMethod.invoke(instance, args);
        accessMethod.setAccessible(access);
        return result;
    }

    public static Object newInstance(Class clazz) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Constructor defaultConstructor = clazz.getDeclaredConstructor(new Class[] {});
        boolean access = defaultConstructor.isAccessible();
        defaultConstructor.setAccessible(true);
        // Object result = defaultConstructor.newInstance(); since java5 ...
        Object result = defaultConstructor.newInstance(new Object[] {});
        defaultConstructor.setAccessible(access);
        return result;
    }

    /**
     * Calls a method on the given object instance with the given arguments.
     * 
     * @param instance
     *            the object instance
     * @param methodName
     *            the name of the method to invoke
     * @param args
     *            an array of objects to pass as arguments
     * @see PrivilegedReflectionUtil#invokeMethod(Object,String,Object)
     */
    public static Object invokeMethod(Object instance, String methodName, Object[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class[] classTypes = generateClassTypes(args);
        Method accessMethod = getMethod(instance.getClass(), methodName, classTypes);
        return invokeMethod(instance, accessMethod, args);
    }

    private static Class[] generateClassTypes(Object[] args) {
        Class[] classTypes = null;
        if (args != null) {
            classTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null)
                    classTypes[i] = args[i].getClass();
            }
        }
        return classTypes;
    }

    /**
     * Return the named method with a method signature matching classTypes from the given class.
     */
    private static Method getMethod(Class thisClass, String methodName, Class[] classTypes) throws NoSuchMethodException {
        if (thisClass == null)
            throw new NoSuchMethodException("Invalid method : " + methodName);
        try {
            return thisClass.getDeclaredMethod(methodName, classTypes);
        } catch (NoSuchMethodException e) {
            return getMethod(thisClass.getSuperclass(), methodName, classTypes);
        }
    }

    /**
     * Return the named field from the given class.
     */
    private static Field getField(Class thisClass, String fieldName) throws NoSuchFieldException {
        if (thisClass == null)
            throw new NoSuchFieldException("Invalid field : " + fieldName);
        try {
            // $ANALYSIS-IGNORE
            return thisClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getField(thisClass.getSuperclass(), fieldName);
        }
    }

    public static boolean isEqual(Object object1, Object object2) {
        if ((object1 == null && object2 != null) || (object1 != null && object2 == null)) {
            return false;
        }

        boolean access1 = false;
        boolean access2 = false;
        Field field2 = null;
        Field field1 = null;

        if (object1 != null && object2 != null) {
            try {
                Class klasse1 = object1.getClass();
                Class klasse2 = object2.getClass();
                if (!klasse1.equals(klasse2)) {
                    return false;
                }

                while (klasse1 != null) {
                    Field[] fields = klasse1.getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        field1 = fields[i];
                        String name = field1.getName();
                        // $ANALYSIS-IGNORE
                        field2 = klasse2.getDeclaredField(name);

                        access1 = field1.isAccessible();
                        field1.setAccessible(true);
                        access2 = field2.isAccessible();
                        field2.setAccessible(true);

                        Object value1 = field1.get(object1);
                        Object value2 = field2.get(object2);

                        if ((value1 != null && value2 == null) || (value1 == null && value2 != null)) {
                            return false;
                        }
                        if (value1 != null && value2 != null && !value1.equals(value2)) {
                            return false;
                        }
                    } // for
                    klasse1 = klasse1.getSuperclass();
                    klasse2 = klasse2.getSuperclass();
                } // while
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException(ex);
            } catch (SecurityException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            } finally {
                field1.setAccessible(access1);
                field2.setAccessible(access2);
            }
        }
        return true;
    }

    /**
     * equalize class and superclasses and all referenced objects
     */
    public static void equalizeDeep(Object original, Object target) {
        throw new UnsupportedOperationException();
    }

    /**
     * equalize class and superclasses
     */
    public static void equalizeShallow(Object original, Object target) {
        if (original.getClass().equals(target.getClass())) {
            Class clazz = original.getClass();
            while (clazz != null) {
                equalizeDeclaredFields(clazz, original, target);
                clazz = clazz.getSuperclass();
            }
        } else {
            throw new RuntimeException("cannot equalizeShallow different classes: " + original.getClass() + ", " + target.getClass());
        }
    }

    /**
     * equalize class without superclasses
     */
    // frontend for private static void equalize(Class clazz, Object original, Object newValue)
    public static void equalizeDeclaredFields(Object original, Object target) {
        if (original.getClass().equals(target.getClass())) {
            Class originalClazz = original.getClass();
            equalizeDeclaredFields(originalClazz, original, target);
            originalClazz = originalClazz.getSuperclass();
        } else {
          throw  new RuntimeException("cannot equalize different classes: " + original.getClass() + ", " + target.getClass());
        }
    }

    /**
     * equalize class without superclasses
     */
    private static void equalizeDeclaredFields(Class clazz, Object original, Object target) {
        try {
            Field[] fields = clazz.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                boolean accessible = fields[i].isAccessible();
                fields[i].setAccessible(true);

                try {
                    Object originalFieldValue = fields[i].get(original);
                    fields[i].set(target, originalFieldValue);
                } catch (IllegalAccessException e) {
                    // hier kommt man hin, wenn man auf ein static final attribut getroffen ist
                }

                fields[i].setAccessible(accessible);
            }
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object cloneShallow(Object source) {

        try {
            Object clone = newInstance(source.getClass());
            equalizeShallow(source, clone);
            return clone;
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    /**
     * Clones an object by calling the (protected) clone()-Method
     */

    public static Object cloneUsingCloneMethod(Object source) {
        try {
            return invokeMethod(source, "clone");
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    
    private static final Logger logger = Logger.getLogger(PrivilegedReflectionUtil.class);

}
