package org.jodt.property;

import org.jodt.property.comparison.IdentityResolver;
import org.jodt.property.comparison.NonTerminalStrategy;
import org.jodt.util.Registry;


/**
 * @author Oliver Stuch
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
    // TODO ... eigentlich !isNonTerminal
    boolean isPrimitive(Object object, Class type);

    boolean isTerminal(Object compareObject);

    boolean isTerminal(Class type);

    // -------------------- Konfiguration NonTerminal ------------------------------------ //

    void globalNonTerminalStrategy(NonTerminalStrategy nonTerminalStrategy);

    void registerNonTerminalType(Class referenceType);
    // -------------------- Ende NonTerminal ------------------------------------ //
}
