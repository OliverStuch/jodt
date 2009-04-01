package org.jodt.property;

/**
 * @author Oliver Stuch
 */
public interface NonTerminalStrategy {
    boolean isNonTerminal(Object object);
    boolean isNonTerminal(Class type);
}
