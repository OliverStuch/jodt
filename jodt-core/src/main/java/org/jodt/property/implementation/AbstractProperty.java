package org.jodt.property.implementation;

import org.jodt.property.Property;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */

abstract public class AbstractProperty<T> implements Property<T> {
    public String toString() {
        return name() + ": " + type() + " = " +value();
    }
}
