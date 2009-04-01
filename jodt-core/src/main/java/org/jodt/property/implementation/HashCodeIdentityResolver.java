package org.jodt.property.implementation;

import org.jodt.property.IdentityResolver;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */

public class HashCodeIdentityResolver implements IdentityResolver<Object> {

    public Long getID(Object object) {
        return new Long(object.hashCode());
    }



}
