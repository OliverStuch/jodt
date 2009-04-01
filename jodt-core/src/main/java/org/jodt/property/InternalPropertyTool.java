package org.jodt.property;

import java.util.Collection;
import java.util.List;

/**
 * @author Oliver Stuch  (oliver@stuch.net)
 */
public interface InternalPropertyTool extends PropertyTool {
    /**
     * Rekursive
     */
    <T> CompositeProperty<T> createCompositeProperty(T object, String name, CompositeProperty<?> parent);

    /**
     * Bringe properties in eine Liste, sortiert nach dem Namen der Property
     */
    List<CompositeProperty> createPropertyList(Collection<CompositeProperty> properties);

}
