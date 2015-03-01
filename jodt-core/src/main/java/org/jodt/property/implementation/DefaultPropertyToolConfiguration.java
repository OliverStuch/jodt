package org.jodt.property.implementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jodt.property.IdentityResolver;
import org.jodt.property.IdentityResolverFactory;
import org.jodt.property.NonTerminalStrategy;
import org.jodt.property.Property;
import org.jodt.property.PropertyActor;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.reflection.JavaTypeDetector;
import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultPropertyToolConfiguration implements PropertyToolConfiguration {

    // -------------------- Identities ------------------------------------ //
    @Override
    public Comparable getID(Object object) {
        IdentityResolver identityResolver = idResolverRegistry.getImplementation(object.getClass());
        if (identityResolver == null) {
            return null;
        } else {
            return identityResolver.getID(object);
        }
    }

    @Override
    public boolean hasIdentityResolver(Class clazz) {
        return findIdentityAndUpdateIdentityResolverRegistry(clazz, false) != null;
    }

    @Override
    public void register(Class<?> clazz, IdentityResolver<?> identityResolver) {
        idResolverRegistry.setImplementation(clazz, identityResolver);

        registerNonTerminalType(clazz);
    }

    @Override
    public Registry<? extends IdentityResolver> getIdentityResolverRegistry() {
        return idResolverRegistry;
    }

    @Override
    public Comparable resolveId(Property property) {
        Object idHolder = property.value();

        if (property instanceof ReflectivePropertySet || property instanceof ReflectiveProperty) {
            if (property.name() != null) {
                idHolder = property.name();
            }
        } else if (DelegatingCompositeProperty.class.isAssignableFrom(property.getClass())) {
            DelegatingCompositeProperty delegatingCompositeProperty = (DelegatingCompositeProperty) property;
            if (delegatingCompositeProperty.parent() instanceof ReflectivePropertySet<?>) {
                idHolder = property.name();
            }
        }

        if (idHolder == null) {
            return null;
        } else {
            return findIdentityAndUpdateIdentityResolverRegistry(idHolder.getClass(), true).getID(idHolder); // solange parameter2=true, kommt nicht null
        }
    }

    /**
     *
     * @param object
     * @param useHashCodeIdentityResolver
     * @return NOT NULL, if useHashCodeIdentityResolver == true
     */
    private IdentityResolver findIdentityAndUpdateIdentityResolverRegistry(Class clazz, boolean useHashCodeIdentityResolver) {
        IdentityResolver identityResolver = (IdentityResolver) idResolverRegistry.getImplementation(clazz);
        if (identityResolver == null && globalIdentityResolverFactory != null) {
            identityResolver = globalIdentityResolverFactory.create(clazz);
            if (identityResolver != null) {
                idResolverRegistry.register(clazz, identityResolver);
            }
        }
        if (identityResolver == null && useHashCodeIdentityResolver) {
            identityResolver = new HashCodeIdentityResolver();
            idResolverRegistry.register(clazz, identityResolver);
        }
        return identityResolver;
    }

    // -------------------- End: Identities ------------------------------------ //
    // -------------------- isNonTerminal ------------------------------------ //
    @Override
    public boolean isPrimitive(Object object, Class type) {
        return (object == null || JavaTypeDetector.isJavaValueType(type));
    }

    @Override
    public void registerNonTerminalType(Class referenceType) {
        if (JavaTypeDetector.isJavaType(referenceType)) {
            if (!hasIdentityResolver(referenceType)) {
                throw new IllegalArgumentException("Java Types (" + referenceType.toString()
                        + ") are not allowed unless a IdentityResolver is registered for that type");
            }
        }
        nonTerminalTypes.setImplementation(referenceType, isNonTerminalType);
    }

    @Override
    public void registerTerminalType(Class terminalClass) {
        terminalTypes.register(terminalClass, isTerminalType);
    }

    @Override
    public boolean isTerminal(Object object) {
        if (object == null) {
            return true;
        }
        return !(isNonTerminal(object));
    }

    @Override
    public boolean isNonTerminal(Object object) {
        if (object == null) {
            return false;
        }
        return isNonTerminal(object.getClass());
    }

    private boolean isNonTerminal(Class type) {
        if (nonTerminalTypes.getImplementation(type) != null) {
            return true;
        }
        if (terminalTypes.getImplementation(type) != null) {
            return false;
        }
        if (Collection.class.isAssignableFrom(type) || type.isArray()) {
            return true;
        }
        if (globalNonTerminalStrategy != null) {
            return globalNonTerminalStrategy.isNonTerminal(type);
        }

        return false;
    }

    @Override
    public void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy) {
        this.globalNonTerminalStrategy = nonTerminalStrategy;
    }

    @Override
    public void registerIgnoreType(Class toBeIgnored) {
        ignoreTypes.register(toBeIgnored, ignoreType);
    }

    @Override
    public boolean isIgnored(Class type) {
        return ignoreTypes.getImplementation(type) != null;
    }

    @Override
    public void registerIgnoreAttributeName(String ignoreAttributeName) {
        ignoreAttributeNames.add(ignoreAttributeName);
    }

    @Override
    public boolean isIgnored(String attributeName) {
        return ignoreAttributeNames.contains(attributeName);
    }

    @Override
    public void registerAttributeNameMapping(String attributeName, String displayName) {
        attributeNameToStringRenderer.put(attributeName, displayName);
    }

    @Override
    public String renderAttributeName(String attributeName) {
        if (attributeName == null) {
            return null;
        }
        String displayName = attributeNameToStringRenderer.get(attributeName);
        if (displayName != null) {
            return displayName;
        } else {
            if (globalAttributeNameRenderer != null) {
                return globalAttributeNameRenderer.render2String(attributeName);
            } else {
                return attributeName;
            }
        }
    }

    @Override
    public void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory) {
        globalIdentityResolverFactory = identityResolverFactory;
    }

    @Override
    public void registerGlobalAttributeNameRenderer(ToStringRenderer toStringRenderer) {
        globalAttributeNameRenderer = toStringRenderer;
    }

    @Override
    public void registerGlobalPropertyActor(String attributeName, PropertyActor actor) {
        globalAttributeNamePropertyActor.put(attributeName, actor);
    }

    @Override
    public void registerGlobalPropertyActor(Class attributeClass, PropertyActor actor) {
        globalClassPropertyActor.register(attributeClass, actor);
    }

    @Override
    public PropertyActor getPropertyActor(String attributeName) {
        return globalAttributeNamePropertyActor.get(attributeName);
    }

    @Override
    public PropertyActor getPropertyActor(Class attributeClass) {
        return globalClassPropertyActor.getImplementation(attributeClass);
    }

    // public void set(NonTerminalStrategy nonTerminalStrategy) {
    // this.globalNonTerminalStrategy = nonTerminalStrategy;
    // }
    private static class IsNonTerminalType {
    }

    private static class IsTerminalType {
    }

    private static class IsIgnoreType {
    }

    private static IsNonTerminalType isNonTerminalType = new IsNonTerminalType();
    private static IsTerminalType isTerminalType = new IsTerminalType();
    private static IsIgnoreType ignoreType = new IsIgnoreType();
    private Registry<IsTerminalType> terminalTypes = new Registry<IsTerminalType>();
    private Registry<IsNonTerminalType> nonTerminalTypes = new Registry<IsNonTerminalType>();
    private Registry<IsIgnoreType> ignoreTypes = new Registry<IsIgnoreType>();
    private NonTerminalStrategy globalNonTerminalStrategy;
    private Set<String> ignoreAttributeNames = new HashSet();
    private Map<String, String> attributeNameToStringRenderer = new HashMap();
    private Registry<IdentityResolver> idResolverRegistry = new Registry<IdentityResolver>();
    private IdentityResolverFactory globalIdentityResolverFactory;
    private ToStringRenderer globalAttributeNameRenderer;
    private Registry<PropertyActor> globalClassPropertyActor = new Registry<PropertyActor>();
    private Map<String, PropertyActor> globalAttributeNamePropertyActor = new HashMap();
    // -------------------- END isNonTerminal ------------------------------------ //
}
