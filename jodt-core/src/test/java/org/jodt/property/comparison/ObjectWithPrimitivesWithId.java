package org.jodt.property.comparison;

import java.io.Serializable;

import org.jodt.property.comparison.IdentityResolver;

/**
 * @author Oliver Stuch
 */

public class ObjectWithPrimitivesWithId extends ObjectWithPrimitives implements Serializable {
    public ObjectWithPrimitivesWithId(int id) {
        this.id = new Long(id);
    }

    public Long id;

    static class IDResolver implements IdentityResolver<ObjectWithPrimitivesWithId> {
        public Long getID(ObjectWithPrimitivesWithId t) {
            return t.id;
        }
    }
}