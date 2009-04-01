package org.jodt.property.implementation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class NullProperty implements Property {

    public String description() {
        return null;
    }

    public String name() {
        return null;
    }

    public Class type() {
        return null;
    }

    public Object value() {
        return null;
    }

    public CompositeProperty value(Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Annotation> annotations() {
        return new ArrayList();
    }

}
