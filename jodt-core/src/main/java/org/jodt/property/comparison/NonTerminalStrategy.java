package org.jodt.property.comparison;

/**
 * @author Oliver Stuch
 */
public interface NonTerminalStrategy {
    boolean isNonTerminal(Object object);
    boolean isNonTerminal(Class type);
}
