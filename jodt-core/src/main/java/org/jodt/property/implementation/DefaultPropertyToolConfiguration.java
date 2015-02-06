package org.jodt.property.implementation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jodt.property.IdentityResolver;
import org.jodt.property.IdentityResolverFactory;
import org.jodt.property.NonTerminalStrategy;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.reflection.JavaTypeDetector;
import org.jodt.util.Registry;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultPropertyToolConfiguration implements PropertyToolConfiguration {

    // -------------------- Identities ------------------------------------ //
    public Long getID(Object object) {
        return idResolverRegistry.getImplementation(object.getClass()).getID(object);
    }

    public boolean hasIdentityResolver(Class clazz) {
        return idResolverRegistry.getImplementation(clazz) != null;
    }

    public void register(Class<?> clazz, IdentityResolver<?> identityResolver) {
        idResolverRegistry.setImplementation(clazz, identityResolver);

        registerNonTerminalType(clazz);
    }

    public Registry<? extends IdentityResolver> getIdentityResolverRegistry() {
        return idResolverRegistry;
    }

    public Long resolveId(Property property) {
        Object idHolder = property.value();

        if (property instanceof ReflectivePropertySet || property instanceof ReflectiveProperty) {
            idHolder = property.name();
        } else if (DelegatingCompositeProperty.class.isAssignableFrom(property.getClass())) {
            DelegatingCompositeProperty delegatingCompositeProperty = (DelegatingCompositeProperty) property;
            if (delegatingCompositeProperty.parent() instanceof ReflectivePropertySet<?>) {
                idHolder = property.name();
            }
        }

        if (idHolder == null) {
            return null;
        } else {
            IdentityResolver identityResolver = (IdentityResolver) idResolverRegistry.getImplementation(idHolder.getClass());
            if (identityResolver == null && globalIdentityResolverFactory != null) {
                identityResolver = globalIdentityResolverFactory.create(idHolder.getClass());
            }
            if (identityResolver == null) {
                identityResolver = new HashCodeIdentityResolver();
            }
            return identityResolver.getID(idHolder);
        }
    }

    private Registry<IdentityResolver> idResolverRegistry = new Registry<IdentityResolver>();

    // -------------------- End: Identities ------------------------------------ //
    // -------------------- isNonTerminal ------------------------------------ //
    public void registerNonTerminalType(Class referenceType) {
        if (JavaTypeDetector.isJavaType(referenceType)) {
            if (!hasIdentityResolver(referenceType)) {
                throw new IllegalArgumentException("Java Types (" + referenceType.toString()
                        + ") are not allowed unless a IdentityResolver is registered for that type");
            }
        }
        nonTerminalTypes.setImplementation(referenceType, isNonTerminalType);
    }

    public boolean isTerminal(Class type) {
        return !(isNonTerminal(type) || Collection.class.isAssignableFrom(type) || type.isArray());
    }

    public boolean isTerminal(Object object) {
        if (object == null) {
            return false;
        }
        return !(isNonTerminal(object) || Collection.class.isAssignableFrom(object.getClass()) || object.getClass().isArray());

    }

    // TODO ... eigentlich !isNonTerminal. 2015: Verstehe ich nicht!
    public boolean isPrimitive(Object object, Class type) {
        if (object == null || type.isPrimitive() || Number.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    public boolean isNonTerminal(Object object) {
        if (object == null) {
            return false;
        }
        if (nonTerminalTypes.getImplementation(object.getClass()) != null) {
            return true;
        }
        if (globalNonTerminalStrategy != null) {
            return globalNonTerminalStrategy.isNonTerminal(object);
        }
        return false;
    }

    public boolean isNonTerminal(Class type) {
        if (nonTerminalTypes.getImplementation(type) != null) {
            return true;
        }
        if (globalNonTerminalStrategy != null) {
            return globalNonTerminalStrategy.isNonTerminal(type);
        }
        return false;
    }

    public void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy) {
        this.globalNonTerminalStrategy = nonTerminalStrategy;
    }

    public void registerIgnoreType(Class toBeIgnored) {
        ignoreTypes.register(toBeIgnored, ignoreType);
    }

    public boolean isIgnored(Class type) {
        return ignoreTypes.getImplementation(type) != null;
    }

    public void registerIgnoreAttributeName(String ignoreAttributeName) {
        ignoreAttributeNames.add(ignoreAttributeName);
    }

    public boolean isIgnored(String attributeName) {
        return ignoreAttributeNames.contains(attributeName);
    }

    public void registerAttributeNameMapping(String attributeName, String displayName) {
        attributeNameToStringRenderer.put(attributeName, displayName);
    }

    public String renderAttributeName(String attributeName) {
        String displayName = attributeNameToStringRenderer.get(attributeName);
        return displayName != null ? displayName : attributeName;
    }

    public void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory) {
        globalIdentityResolverFactory = identityResolverFactory;
    }

    // public void set(NonTerminalStrategy nonTerminalStrategy) {
    // this.globalNonTerminalStrategy = nonTerminalStrategy;
    // }
    private static class IsNonTerminalType {
    }

    private static class IsIgnoreType {
    }

    private static IsNonTerminalType isNonTerminalType = new IsNonTerminalType();
    private static IsIgnoreType ignoreType = new IsIgnoreType();
    private Registry<IsNonTerminalType> nonTerminalTypes = new Registry<IsNonTerminalType>();
    private Registry<IsIgnoreType> ignoreTypes = new Registry<IsIgnoreType>();
    private NonTerminalStrategy globalNonTerminalStrategy;
    private Set<String> ignoreAttributeNames = new HashSet();
    private Map<String, String> attributeNameToStringRenderer = new HashMap();
    private IdentityResolverFactory globalIdentityResolverFactory;

    // -------------------- END isNonTerminal ------------------------------------ //
}
