package org.jodt.property;

import java.util.Set;

/**
 * @author Oliver Stuch  (oliver@stuch.net)
 */
public interface CompositePropertySet<T> extends CompositeProperty<T>, Set<CompositeProperty<?>> {

}
