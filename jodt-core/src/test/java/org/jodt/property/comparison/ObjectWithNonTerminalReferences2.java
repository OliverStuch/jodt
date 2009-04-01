package org.jodt.property.comparison;


import org.jodt.property.comparison.IgnorePropertyDiffs;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class ObjectWithNonTerminalReferences2 extends ObjectWithNonTerminalReferences {
    @IgnorePropertyDiffs
    public ObjectWithPrimitives objectWithPrimitivesIgnorePropertyDiffs = new ObjectWithPrimitives();
}
