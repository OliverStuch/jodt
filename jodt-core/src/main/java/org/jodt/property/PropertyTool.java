package org.jodt.property;

import java.util.Collection;
import java.util.List;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public interface PropertyTool {

    /**
     * Erzeuge aus object ein rekursives Property-Objekt (= CompositeProperty)
     * mit Namen name. Diese Operation eignet sich für "toplevel-Objekte", da
     * hier kein Name automatisch bestimmt werden kann.
     *
     * @return maybe null!
     */
    <T> CompositeProperty<T> createCompositeProperty(T object, String name);

    /**
     * Rekursive
     */
    <T> CompositeProperty<T> createCompositeProperty(T object, String name, CompositeProperty<?> parent);

    /**
     * Bringe properties in eine Liste, sortiert nach dem Namen der Property
     */
    List<CompositeProperty> createPropertyList(Collection<CompositeProperty> properties);

    /**
     * @return maybe null!
     */
    <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name);

    /**
     * @return maybe null!
     */
    <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name);

    PropertyToolConfiguration configure();

}
