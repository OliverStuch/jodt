package org.jodt.property.comparison;

/**
 * @author Oliver Stuch
 */

public interface IdentityResolver<T> {
    Long getID(T t);

}
