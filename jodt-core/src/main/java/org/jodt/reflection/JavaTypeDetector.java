package org.jodt.reflection;

import java.util.regex.Pattern;

/**
 * @author Oliver Stuch
 */

public class JavaTypeDetector {
    public static boolean isJavaValueType(Class clazz) {
        if (clazz == null) {
            return true;
        }

        if (clazz.isPrimitive()) {
            return true;
        }

        if (isPrimitiveWrapper(clazz)) {
            return true;
        }

        if (isPrimitiveArray(clazz)) {
            return true;
        }
        return false;
    }

    private static boolean isPrimitiveWrapper(Class clazz) {
        if (!(clazz.equals(Integer.class) || clazz.equals(String.class) || clazz.equals(Double.class) || clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Boolean.class))) {
            return false;
        }
        return true;
    }


    public static boolean isJavaType(String classname) {
        if (classname == null) {
            return true;
        }

        java.util.regex.Matcher matcher = excludePattern.matcher(classname);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isJavaType(Class clazz) {
        if (clazz == null) {
            return true;
        }
        if (isJavaType(clazz.getName())) {
            return true;
        }
        if (clazz.isPrimitive()) {
            return true;
        }
        if (isPrimitiveArray(clazz)) {
            return true;
        }
        return false;
    }

    public static boolean isPrimitiveArray(Class clazz) {
        if (!clazz.isArray()) {
            return false;
        }
        String arrayElementClassName = clazz.getName().substring(1);
        if (arrayElementClassName.startsWith("L")) {
            return false;
        }
        switch (arrayElementClassName.charAt(0)) {
        case 'V': // "void";
        case 'B': // "byte";
        case 'C':
            // className = "char";
            // break;
        case 'S':
            // className = "short";
            // break;
        case 'I':
            // className = "int";
            // break;
        case 'J':
            // className = "long";
            // break;
        case 'F':
            // className = "float";
            // break;
        case 'D':
            // className = "double";
            // break;
        case 'Z':
            // className = "boolean";
            // break;
        case 'T':
            // className = "T";
            // break;
            return true;
        default:
            return false;
        }
    }
    private static final Pattern excludePattern = Pattern.compile( "^(\\[L)*java.*");
}
