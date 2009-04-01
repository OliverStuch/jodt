package org.jodt.property.comparison;


import org.jodt.property.IdentityResolver;

/**
 * @author Oliver Stuch
 */

public class ObjectWithNonTerminalReferences {
    public Long id;
    public ObjectWithPrimitives objectWithPrimitives1 = new ObjectWithPrimitives();
    public ObjectWithPrimitives objectWithPrimitives2 = new ObjectWithPrimitives();

    static class IDResolver implements IdentityResolver<ObjectWithNonTerminalReferences> {
        public Long getID(ObjectWithNonTerminalReferences t) {
            return t.id;
        }
    }
}
