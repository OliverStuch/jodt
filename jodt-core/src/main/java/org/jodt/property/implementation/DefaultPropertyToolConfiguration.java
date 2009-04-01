package org.jodt.property.implementation;

import java.util.Collection;

import org.jodt.property.IdentityResolver;
import org.jodt.property.NonTerminalStrategy;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.reflection.JavaTypeDetector;
import org.jodt.util.Registry;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
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
            if (identityResolver == null) {
                identityResolver = new HashCodeIdentityResolver();
            }
            return identityResolver.getID(idHolder);
        }
    }

    private Registry<IdentityResolver> idResolverRegistry = new Registry<IdentityResolver>();

    // -------------------- End: Identities ------------------------------------ //


    // -------------------- isNonTerminal ------------------------------------ //

    public boolean isPrimitive(Object object, Class type) {
        if (object == null || type.isPrimitive() || Number.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

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

    // public void set(NonTerminalStrategy nonTerminalStrategy) {
    // this.globalNonTerminalStrategy = nonTerminalStrategy;
    // }

    private static class IsNonTerminalType {
    }

    private static IsNonTerminalType isNonTerminalType = new IsNonTerminalType();
    private Registry<IsNonTerminalType> nonTerminalTypes = new Registry<IsNonTerminalType>();
    private NonTerminalStrategy globalNonTerminalStrategy;

    // -------------------- END isNonTerminal ------------------------------------ //

}
