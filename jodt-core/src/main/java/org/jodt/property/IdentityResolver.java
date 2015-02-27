package org.jodt.property;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */

public interface IdentityResolver<T> {
    Comparable getID(T t);

}
