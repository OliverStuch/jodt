package org.jodt.property.comparison;

/**
 * @author Oliver Stuch
 */
public interface IgnorePropertyStrategy {
    boolean ignoreProperty(Class propertyOwnerType, String propertyName, Class propertyType);
}
