package org.jodt.property.implementation;

import java.util.Collection;

import org.jodt.property.CompositeProperty;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

interface InternalCompositeProperty<T> {
    Collection<CompositeProperty<?>> properties();

    void properties(Collection<CompositeProperty<?>> properties);

    void parent(CompositeProperty<?> parent);

    CompositeProperty<?> parent();

    /**
     * just the setter
     */
    void setValue(T newValue);
}
