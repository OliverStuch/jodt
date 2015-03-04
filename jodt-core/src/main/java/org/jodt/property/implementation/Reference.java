package org.jodt.property.implementation;

import org.jodt.property.IdentityResolver;
import org.jodt.property.PropertyToolConfiguration;

/**
 *
 * @author os
 */
class Reference {

    Object key;
    Object value;

    Reference(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "->" + value;
    }

    static class ReferenceIdResolver implements IdentityResolver<Reference> {

        private PropertyToolConfiguration propertyToolConfiguration;

        ReferenceIdResolver(PropertyToolConfiguration propertyToolConfiguration) {
            this.propertyToolConfiguration = propertyToolConfiguration;
        }

        public Comparable getID(Reference t) {
            return propertyToolConfiguration.resolveId(t.key);
        }

    }
}
