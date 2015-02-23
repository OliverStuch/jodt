package org.jodt.property;

import java.util.Collection;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public interface CompositeProperty<T> extends Property<T>, Collection<CompositeProperty<?>> {
    public final static String PATH_SEPARATOR=".";

    /**
     * @return true, if this object has properties (e.g. is not a leaf in the
     * property tree)
     */
    boolean hasProperties();

    /**
     * Ersetze eine property des zugrunde liegenden Objekts durch newProperty.
     * Wenn property nicht existiert, wird newProperty geadded. Im Fall einer
     * List erfolgt die Ersetzung so, dass newProperty den Index von property
     * erhält.
     */
    void replace(CompositeProperty oldProperty, CompositeProperty newProperty);

    /**
     * wrapper für {@link #replace(CompositeProperty, CompositeProperty)}, nur
     * das ein Objekt übergeben wird und eine neue Property erzeugt wird.
     *
     * @return Die neu erzeugte Property
     */
    CompositeProperty replace(CompositeProperty oldProperty, Object newValue);

    /**
     * @return a property with name.equals(property.name())==true (in case of
     * uneindeutig : ein zufälliges) or null if no property with that name
     * available TODO: Was ist mit displayName?
     */
    Collection<CompositeProperty> findByName(String name);

    /**
     * @return a property with value.equals(property.value())==true (in case of
     * uneindeutig : ein zufälliges) or null if no property with that value
     * available
     */
    Collection<CompositeProperty> findByValue(Object value);
    
    CompositeProperty parent();
    
    String path();

}
