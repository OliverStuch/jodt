package org.jodt.property;

import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public interface PropertyToolConfiguration {

    // -------------------- Identities ------------------------------------ //
    Comparable getID(Object object);

    boolean hasIdentityResolver(Class clazz);

    Comparable resolveId(Object object);

    void registerIdResolver(Class<?> clazz, IdentityResolver<?> identityResolver);

    Registry<? extends IdentityResolver> getIdentityResolverRegistry();

    /**
     * 
     * @param property
     * @return guaranteed to be not null
     */
    Comparable resolveId(Property property);

    // -------------------- End: Identities ------------------------------------ //
    // -------------------- NonTerminal ------------------------------------ //
    // -------------------- Abfragen NonTerminal ------------------------------------ //
    boolean isPrimitive(Object object, Class type);

    boolean isTerminal(Object object);

    boolean isNonTerminal(Object object);

    // -------------------- Konfiguration NonTerminal ------------------------------------ //
    void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy);

    void registerNonTerminalType(Class nonTerminalType);

    void registerTerminalType(Class terminalType);

    // -------------------- Ende NonTerminal ------------------------------------ //
    void registerIgnoreType(Class toBeIgnored);

    boolean isIgnored(Class type);

    boolean isIgnored(String attributeName);

    void registerIgnoreAttributeName(String attributeName);

    void registerAttributeNameMapping(String attributeName, String displayName);

    String renderAttributeName(String attributeName);

    void globalIdentityResolverFactory(IdentityResolverFactory identityResolverFactory);

    void registerGlobalAttributeNameRenderer(ToStringRenderer toStringRenderer);

    void registerGlobalPropertyActor(String attributeName, PropertyActor actor);

    void registerGlobalPropertyActor(Class attributeClass, PropertyActor actor);

    PropertyActor getPropertyActor(String attributeName);

    PropertyActor getPropertyActor(Class attributeClass);
}
