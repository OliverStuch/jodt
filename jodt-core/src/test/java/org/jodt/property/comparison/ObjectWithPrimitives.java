package org.jodt.property.comparison;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class ObjectWithPrimitives {
    final static int INITIAL_INTEGER_VALUE = 10;
    final static String INITIAL_STRING = "Eine kleine Nachtmusik";
    int primitiveInt = 1;
    Integer integer = new Integer(INITIAL_INTEGER_VALUE);
    String string = INITIAL_STRING;
    String nullString = null;
}
