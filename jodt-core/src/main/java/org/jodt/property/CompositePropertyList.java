package org.jodt.property;

import java.util.List;

/**
 * @author Oliver Stuch  (oliver@stuch.net)
 */
public interface CompositePropertyList<T> extends CompositeProperty<T>, List<CompositeProperty<?>> {

}
