package org.jodt.property;

/**
 * @author Oliver Stuch  (oliver@stuch.net)
 */
public interface PropertyTool {
    

    /**
     * Erzeuge aus object ein rekursives Property-Objekt (= CompositeProperty) mit Namen name. 
     * Diese Operation eignet sich für "toplevel-Objekte", da hier kein Name automatisch bestimmt werden kann. 
     */
    <T> CompositeProperty<T> createCompositeProperty(T object, String name);
    <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name);
     <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name);


}
