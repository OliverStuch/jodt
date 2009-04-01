package org.jodt.property.comparison;

/**
 * @author Oliver Stuch
 */
public interface IgnoreStrategy {
    /**
     * Wird ein Objekt dieser Klasse mit {@link PropertyUtilConfiguration#setGlobalIgnoreObjectButAnalyseItsNonTerminalPropertiesStrategy(IgnoreStrategy)} gesetzt, werden für alle Objekte,
     * bei denen diese Methode true zurückgibt, von {@link PropertyUtilConfiguration#ignoreObjectButAnalyseItsNonTerminalProperties(Object)} ebenfalls true zurückgegeben.
     */
    boolean ignoreObjectButAnalyseItsNonTerminalProperties(Object object);
}
