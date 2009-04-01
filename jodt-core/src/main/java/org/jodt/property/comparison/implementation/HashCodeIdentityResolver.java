package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.IdentityResolver;


/**
 * @author Oliver Stuch
 */

public class HashCodeIdentityResolver implements IdentityResolver<Object> {

    public Long getID(Object object) {
        return new Long(object.hashCode());
    }



}
