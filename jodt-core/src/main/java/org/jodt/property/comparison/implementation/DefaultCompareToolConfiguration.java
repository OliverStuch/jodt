package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.IgnorePropertyStrategy;
import org.jodt.property.comparison.IgnoreStrategy;
import org.jodt.property.implementation.DefaultPropertyToolConfiguration;
import org.jodt.util.Registry;


/**
 * Defaults:<br>
 * keine globalNonTerminalStrategy <br>
 * diffMode == false <br>
 * 
 */
public class DefaultCompareToolConfiguration extends DefaultPropertyToolConfiguration implements CompareToolConfiguration {

    public boolean analysePropertiesOfDifferentNonTerminalObjects(Object object) {
        if (object == null) {
            return false;
        }
        if (analysePropertiesOfNonTerminalTypes.getImplementation(object.getClass()) != null) {
            return true;
        } else {
            return false;
        }
    }
    

    public void registerAnalysePropertiesOfDifferentNonTerminalObjects(Class clazz) {
        registerNonTerminalType(clazz);
        analysePropertiesOfNonTerminalTypes.setImplementation(clazz, isIgnoreType);
    }
    private Registry<IsIgnoreType> analysePropertiesOfNonTerminalTypes = new Registry<IsIgnoreType>();



    private boolean diffModeActive;

    public void diffMode(boolean b) {
        this.diffModeActive = b;
    }
    public boolean diffMode() {
        return diffModeActive;
    }

    public boolean ignoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Object object) {
        if (object == null) {
            return false;
        }
 
        if (!isNonTerminal(object)) {
            return false;
        }

        if (ignorePropertiesOfNonTerminalTypes.getImplementation(object.getClass()) != null) {
            return true;
        }

        return false;
    }
    
    public void registerIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Class ignoreType) {
        ignorePropertiesOfNonTerminalTypes.setImplementation(ignoreType, isIgnoreType);
    }

    public void deregisterIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(Class ignoreType) {
        ignorePropertiesOfNonTerminalTypes.setImplementation(ignoreType, null);
    }

    private Registry<IsIgnoreType> ignorePropertiesOfNonTerminalTypes = new Registry<IsIgnoreType>();

    // -------------------- ignorePropertyStrategy ------------------------------------ //
    public boolean ignoreProperty(Class propertyOwnerType, String propertyName, Class propertyType) {
        if (ignorePropertyStrategy != null) {
            return ignorePropertyStrategy.ignoreProperty(propertyOwnerType, propertyName, propertyType);
        } else {
            return false;
        }
    }

    public void set(IgnorePropertyStrategy ignorePropertyStrategy) {
        this.ignorePropertyStrategy = ignorePropertyStrategy;
    }

    public void removeIgnorePropertyStrategy() {
        this.ignorePropertyStrategy = null;
    }

    private IgnorePropertyStrategy ignorePropertyStrategy;

    // -------------------- End: ignorePropertyStrategy ------------------------------------ //

    // -------------------- ignoreObjectButAnalyseItsNonTerminalProperties ------------------------------------ //

    public boolean ignoreObjectButAnalyseItsNonTerminalProperties(Object object) {
        if (object == null) {
            return false;
        }

        if (ignoreObjectButAnalyseItsNonTerminalProperties.getImplementation(object.getClass()) != null) {
            return true;
        } else {
            if (globalIgnoreStrategy != null) {
                return globalIgnoreStrategy.ignoreObjectButAnalyseItsNonTerminalProperties(object);
            } else {
                return false;
            }
        }
    }

    public void registerIgnoreObjectButAnalyseItsNonTerminalProperties(Class clazz) {
        ignoreObjectButAnalyseItsNonTerminalProperties.setImplementation(clazz, isIgnoreType);
    }

    public void setGlobalIgnoreObjectButAnalyseItsNonTerminalPropertiesStrategy(IgnoreStrategy ignoreStrategy) {
        this.globalIgnoreStrategy = ignoreStrategy;
    }

    public void removeGlobalIgnoreObjectButAnalyseItsNonTerminalPropertiesStrategy() {
        globalIgnoreStrategy = null;
    }

    private static class IsIgnoreType {
    }

    private static IsIgnoreType isIgnoreType = new IsIgnoreType();
    private Registry<IsIgnoreType> ignoreObjectButAnalyseItsNonTerminalProperties = new Registry<IsIgnoreType>();
    private IgnoreStrategy globalIgnoreStrategy;
    // -------------------------------------------------------- //
}
