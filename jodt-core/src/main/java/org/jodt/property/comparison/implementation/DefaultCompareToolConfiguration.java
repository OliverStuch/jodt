package org.jodt.property.comparison.implementation;

import org.jodt.property.IdentityResolver;
import org.jodt.property.IdentityResolverFactory;
import org.jodt.property.NonTerminalStrategy;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.IgnorePropertyStrategy;
import org.jodt.property.comparison.IgnoreStrategy;
import org.jodt.property.implementation.DefaultPropertyToolConfiguration;
import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;

/**
 * Defaults:<br>
 * keine globalNonTerminalStrategy <br>
 * diffMode == false <br>
 *
 */
public class DefaultCompareToolConfiguration implements CompareToolConfiguration {

    private PropertyToolConfiguration delegate;

    public DefaultCompareToolConfiguration() {
        delegate = new DefaultPropertyToolConfiguration();
    }

    public DefaultCompareToolConfiguration(PropertyToolConfiguration propertyToolConfiguration) {
        delegate = propertyToolConfiguration;
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
    public Long getID(Object object) {
        return delegate.getID(object);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean hasIdentityResolver(Class clazz) {
        return delegate.hasIdentityResolver(clazz);
    }

    /**
     * PropertyToolConfiguration
     */
    public void register(Class<?> clazz, IdentityResolver<?> identityResolver) {
        delegate.register(clazz, identityResolver);
    }

    /**
     * PropertyToolConfiguration
     */
    public Registry<? extends IdentityResolver> getIdentityResolverRegistry() {
        return delegate.getIdentityResolverRegistry();
    }

    /**
     * PropertyToolConfiguration
     */
    public Long resolveId(Property property) {
        return delegate.resolveId(property);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isPrimitive(Object object, Class type) {
        return delegate.isPrimitive(object, type);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isTerminal(Object compareObject) {
        return delegate.isTerminal(compareObject);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isTerminal(Class type) {
        return delegate.isTerminal(type);
    }

    /**
     * PropertyToolConfiguration
     */
    public void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy) {
        delegate.globalNonTerminalStrategy(nonTerminalStrategy);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerNonTerminalType(Class referenceType) {
        delegate.registerNonTerminalType(referenceType);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerIgnoreType(Class toBeIgnored) {
        delegate.registerIgnoreType(toBeIgnored);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isIgnored(Class type) {
        return delegate.isIgnored(type);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isIgnored(String attributeName) {
        return delegate.isIgnored(attributeName);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerIgnoreAttributeName(String attribbuteName) {
        delegate.registerIgnoreAttributeName(attribbuteName);
    }

    /**
     * PropertyToolConfiguration
     */
    public boolean isNonTerminal(Object object) {
        return delegate.isNonTerminal(object);
    }

    /**
     * PropertyToolConfiguration
     */
    public void registerAttributeNameMapping(String attributeName, String displayName) {
        delegate.registerAttributeNameMapping(attributeName, displayName);
    }

    /**
     * PropertyToolConfiguration
     */
    public String renderAttributeName(String attributeName) {
        return delegate.renderAttributeName(attributeName);
    }

    public void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory) {
        delegate.globalIdentityResolverFactory(identityResolverFactory);
    }

    public void registerTerminalClass(Class terminalClass) {
        delegate.registerTerminalClass(terminalClass);
    }

    public void registerGlobalAttributeNameRenderer(ToStringRenderer toStringRenderer) {
        delegate.registerGlobalAttributeNameRenderer(toStringRenderer);
    }

    private static class IsIgnoreType {
    }

    private static IsIgnoreType isIgnoreType = new IsIgnoreType();
    private Registry<IsIgnoreType> ignoreObjectButAnalyseItsNonTerminalProperties = new Registry<IsIgnoreType>();
    private IgnoreStrategy globalIgnoreStrategy;
    // -------------------------------------------------------- //
}
