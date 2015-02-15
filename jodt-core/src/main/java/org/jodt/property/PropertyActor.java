package org.jodt.property;


/**
 * @author Oliver Stuch
 */

public interface PropertyActor<T> {
    /**
     * 
     * @param property to act on
     * @return true if property was modified
     */
    boolean actOn(Property<T> property);
}
