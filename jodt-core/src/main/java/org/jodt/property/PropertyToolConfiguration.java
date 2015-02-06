package org.jodt.property;

import org.jodt.util.Registry;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public interface PropertyToolConfiguration {

    // -------------------- Identities ------------------------------------ //
    Long getID(Object object);

    boolean hasIdentityResolver(Class clazz);

    void register(Class<?> clazz, IdentityResolver<?> identityResolver);

    Registry<? extends IdentityResolver> getIdentityResolverRegistry();

    Long resolveId(Property property);

    // -------------------- End: Identities ------------------------------------ //
    // -------------------- NonTerminal ------------------------------------ //
    // -------------------- Abfragen NonTerminal ------------------------------------ //
    boolean isPrimitive(Object object, Class type);

    boolean isTerminal(Object compareObject);

    boolean isTerminal(Class type);

    boolean isNonTerminal(Object object);

    // -------------------- Konfiguration NonTerminal ------------------------------------ //
    void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy);

    void registerNonTerminalType(Class referenceType);
    // -------------------- Ende NonTerminal ------------------------------------ //

    void registerIgnoreType(Class toBeIgnored);

    boolean isIgnored(Class type);

    boolean isIgnored(String attributeName);

    void registerIgnoreAttributeName(String attributeName);

    void registerAttributeNameMapping(String attributeName, String displayName);

    String renderAttributeName(String attributeName);
}
