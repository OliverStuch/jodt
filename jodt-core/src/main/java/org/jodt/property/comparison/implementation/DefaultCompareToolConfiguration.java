package org.jodt.property.comparison.implementation;

import org.jodt.property.Equalator;
import org.jodt.property.IdentityResolver;
import org.jodt.property.IdentityResolverFactory;
import org.jodt.property.NonTerminalStrategy;
import org.jodt.property.Property;
import org.jodt.property.PropertyActor;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.IgnorePropertyStrategy;
import org.jodt.property.comparison.IgnoreStrategy;
import org.jodt.property.implementation.DefaultPropertyToolConfiguration;
import org.jodt.property.implementation.Reference;
import org.jodt.property.implementation.Reference.ReferenceEqualator;
import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;

public class DefaultCompareToolConfiguration implements CompareToolConfiguration {

    private PropertyToolConfiguration propertyToolConfiguration;

    public DefaultCompareToolConfiguration() {
        propertyToolConfiguration = new DefaultPropertyToolConfiguration();
    }

    public DefaultCompareToolConfiguration(PropertyToolConfiguration propertyToolConfiguration) {
        this.propertyToolConfiguration = propertyToolConfiguration;
        registerTerminalTypeEqualator(Reference.class, new ReferenceEqualator());
    }

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

    /**
     * PropertyToolConfiguration
     */
    public Comparable getID(Object object) {
        return propertyToolConfiguration.getID(object);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean hasIdentityResolver(Class clazz) {
        return propertyToolConfiguration.hasIdentityResolver(clazz);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerIdResolver(Class<?> clazz, IdentityResolver<?> identityResolver) {
        propertyToolConfiguration.registerIdResolver(clazz, identityResolver);
    }

    /**
     * PropertyToolConfiguration
     */
    public Registry<? extends IdentityResolver> getIdentityResolverRegistry() {
        return propertyToolConfiguration.getIdentityResolverRegistry();
    }

    /**
     * PropertyToolConfiguration
     */
    public Comparable resolveId(Property property) {
        return propertyToolConfiguration.resolveId(property);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isPrimitive(Object object, Class type) {
        return propertyToolConfiguration.isPrimitive(object, type);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isTerminal(Object compareObject) {
        return propertyToolConfiguration.isTerminal(compareObject);
    }

    /**
     * PropertyToolConfiguration
     */
    public void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy) {
        propertyToolConfiguration.globalNonTerminalStrategy(nonTerminalStrategy);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerNonTerminalType(Class referenceType) {
        propertyToolConfiguration.registerNonTerminalType(referenceType);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerIgnoreType(Class toBeIgnored) {
        propertyToolConfiguration.registerIgnoreType(toBeIgnored);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isIgnored(Class type) {
        return propertyToolConfiguration.isIgnored(type);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isIgnored(String attributeName) {
        return propertyToolConfiguration.isIgnored(attributeName);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerIgnoreAttributeName(String attribbuteName) {
        propertyToolConfiguration.registerIgnoreAttributeName(attribbuteName);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isNonTerminal(Object object) {
        return propertyToolConfiguration.isNonTerminal(object);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerAttributeNameMapping(String attributeName, String displayName) {
        propertyToolConfiguration.registerAttributeNameMapping(attributeName, displayName);
    }

    /**
     * PropertyToolConfiguration
     */
    public String renderAttributeName(String attributeName) {
        return propertyToolConfiguration.renderAttributeName(attributeName);
    }

    public void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory) {
        propertyToolConfiguration.globalIdentityResolverFactory(identityResolverFactory);
    }

    public void registerTerminalType(Class terminalClass) {
        propertyToolConfiguration.registerTerminalType(terminalClass);
    }

    public void registerGlobalAttributeNameRenderer(ToStringRenderer toStringRenderer) {
        propertyToolConfiguration.registerGlobalAttributeNameRenderer(toStringRenderer);
    }

    public void registerGlobalPropertyActor(String attributeName, PropertyActor actor) {
        propertyToolConfiguration.registerGlobalPropertyActor(attributeName, actor);
    }

    public void registerGlobalPropertyActor(Class attributeClass, PropertyActor actor) {
        propertyToolConfiguration.registerGlobalPropertyActor(attributeClass, actor);
    }

    public PropertyActor getPropertyActor(String attributeName) {
        return propertyToolConfiguration.getPropertyActor(attributeName);
    }

    public PropertyActor getPropertyActor(Class attributeClass) {
        return propertyToolConfiguration.getPropertyActor(attributeClass);
    }

    public Comparable resolveId(Object object) {
        return propertyToolConfiguration.resolveId(object);
    }

    public Equalator getTerminalTypeEqualator(Class type) {
        return terminalTypeEqualator.getImplementation(type);
    }

    public void registerTerminalTypeEqualator(Class type, Equalator equalator) {
        terminalTypeEqualator.register(type, equalator);
    }

    private static class IsIgnoreType {
    }

    private static IsIgnoreType isIgnoreType = new IsIgnoreType();
    private Registry<IsIgnoreType> ignoreObjectButAnalyseItsNonTerminalProperties = new Registry<IsIgnoreType>();
    private IgnoreStrategy globalIgnoreStrategy;
    private Registry<Equalator> terminalTypeEqualator = new Registry<Equalator>();
    // -------------------------------------------------------- //
}
