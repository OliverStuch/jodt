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
    public Long getID(Object object) {
        IdentityResolver identityResolver = idResolverRegistry.getImplementation(object.getClass());
        if (identityResolver == null) {
            return null;
        } else {
            return identityResolver.getID(object);
        }
    }

    public boolean hasIdentityResolver(Class clazz) {
        return findIdentityAndUpdateIdentityResolverRegistry(clazz, false) != null;
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

    public void registerTerminalClass(Class terminalClass) {
        terminalTypes.register(terminalClass, isTerminalType);
    }

    public boolean isTerminal(Class type) {
        if (terminalTypes.getImplementation(type) != null) {
            return true;
        }
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
        return (object == null || terminalTypes.getImplementation(type) != null || JavaTypeDetector.isJavaValueType(type));
//        if (object == null || type.isPrimitive() || Number.class.isAssignableFrom(type) || String.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)
//                || Boolean.class.isAssignableFrom(type)
//                || Character.class.isAssignableFrom(type)
//                || Byte.class.isAssignableFrom(type)
//                || Short.class.isAssignableFrom(type)
//                || Integer.class.isAssignableFrom(type)
//                || Long.class.isAssignableFrom(type)
//                || Float.class.isAssignableFrom(type)
//                || Double.class.isAssignableFrom(type)
//                || Void.class.isAssignableFrom(type)) {
//            return true;
//        }
//        return false;
    }

    public boolean isNonTerminal(Object object) {
        if (object == null) {
            return false;
        }
        return isNonTerminal(object.getClass());
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

    public void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory) {
        globalIdentityResolverFactory = identityResolverFactory;
    }

    public void registerGlobalAttributeNameRenderer(ToStringRenderer toStringRenderer) {
        globalAttributeNameRenderer = toStringRenderer;
    }

    public void registerGlobalPropertyActor(String attributeName, PropertyActor actor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void registerGlobalPropertyActor(Class attributeClass, PropertyActor actor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PropertyActor getPropertyActor(String attributeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PropertyActor getPropertyActor(Class attributeClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    private IdentityResolverFactory globalIdentityResolverFactory;
    private ToStringRenderer globalAttributeNameRenderer;
    // -------------------- END isNonTerminal ------------------------------------ //
}
