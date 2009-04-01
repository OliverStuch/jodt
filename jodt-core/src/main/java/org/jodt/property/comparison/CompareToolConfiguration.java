package org.jodt.property.comparison;

import org.jodt.property.PropertyToolConfiguration;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */
public interface CompareToolConfiguration extends PropertyToolConfiguration {

    // -------------------- analysePropertiesOfDifferentNonTerminalObjects ------------------------------------ //
    /**
     * Bei true werden alle Properties (und deren Properties) von object untersucht, auch wenn ein ReferenceChange, Additional, Missing oder ValueDiff vorliegt.
     * 
     * Default: false.
     */
    boolean analysePropertiesOfDifferentNonTerminalObjects(Object object);

    /**
     * Registriert eine Klasse, bei der bei diff/compare die Properties auch dann verglichen werden sollen, wenn es sich um unterschiedliche objekte handelt. Die Klasse wird zugleich als
     * Nicht-Terminal registriert.
     */
    void registerAnalysePropertiesOfDifferentNonTerminalObjects(Class clazz);

    // -------------------- End: analysePropertiesOfDifferentNonTerminalObjects ------------------------------------ //

    // -------------------- ignorePropertiesOfNonTerminalObject ------------------------------------ //
    /**
     * @return true: Wird ein Objekt referenziert, sollen beim vergleich die properties (also auch diffs unter ihnen) ignoriert werden, wenn die beiden referenzierten objekte laut ID gleich
     *         sind. Sind die IDs unterschiedlich, soll nur ein ReferenceChange-Diff notiert werden. Beispiel: Zuständigkeiten unterscheiden sich, nur weil im Zuständigen Gericht sich eine
     *         Telefonnummer geändert hat. Interessieren tut aus Sicht der Zuständigkeit aber nur, wenn ein anderes Gericht referenziert wird.
     */
    boolean ignoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Object object);

    public void registerIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Class ignoreType);
    
    public void deregisterIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Class ignoreType);

    // -------------------- End: ignorePropertiesOfNonTerminalObject ------------------------------------ //

    // -------------------- ignoreObjectButAnalyseItsNonTerminalProperties ------------------------------------ //

    boolean ignoreObjectButAnalyseItsNonTerminalProperties(Object object);

    void registerIgnoreObjectButAnalyseItsNonTerminalProperties(Class clazz);

    void setGlobalIgnoreObjectButAnalyseItsNonTerminalPropertiesStrategy(IgnoreStrategy ignoreStrategy);

    void removeGlobalIgnoreObjectButAnalyseItsNonTerminalPropertiesStrategy();

    // -------------------- End: ignoreObjectButAnalyseItsNonTerminalProperties ------------------------------------ //

    // -------------------- ignorePropertyStrategy ------------------------------------ //
    /**
     * frühere ExcludePropertyStrategy Bei true wird eine Property, die durch die Eigenschaften propertyOwnerType, propertyName und propertyName, komplett ignoriert. Betrifft also auch die
     * Properties der Property.
     */
    boolean ignoreProperty(Class propertyOwnerType, String propertyName, Class propertyType);

    void set(IgnorePropertyStrategy ignorePropertyStrategy);

    void removeIgnorePropertyStrategy();

    // -------------------- End: ignorePropertyStrategy ------------------------------------ //

}
