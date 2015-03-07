package org.jodt.property.implementation;

import org.jodt.property.Equalator;
import org.jodt.property.IdentityResolver;
import org.jodt.property.PropertyToolConfiguration;

/**
 *
 * @author os
 */
public class Reference {

    Object key;
    Object value;

    public Reference(Object key, Object value) {
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
    public static class ReferenceEqualator implements Equalator<Reference>{

        public boolean areEqual(Reference t1, Reference t2) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
