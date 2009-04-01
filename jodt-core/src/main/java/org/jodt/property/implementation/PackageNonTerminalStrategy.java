package org.jodt.property.implementation;

import org.jodt.property.NonTerminalStrategy;
import org.jodt.reflection.ReflectionUtil;
import org.jodt.util.PatternListFilter;


/**
 * Interpretiert nur Objekte aus definierten Packages als NonTerminal
 */
public class PackageNonTerminalStrategy implements NonTerminalStrategy {
    private PatternListFilter patternListFilter;

    public PackageNonTerminalStrategy(String packageFilter) {
        if (ReflectionUtil.isJavaType(packageFilter)) {
            throw new RuntimeException("java types not allowed to be recognized as non terminals");
        }
        patternListFilter = new PatternListFilter(packageFilter);
    }

    public boolean isNonTerminal(Object object) {
        return isNonTerminal(object.getClass());
    }

    public boolean isNonTerminal(Class clazz) {
        Package pack = clazz.getPackage();
        if (pack == null) { // z.B. bei int[]
            return false;
        }
        return patternListFilter.filter(pack.getName());
    }

}
