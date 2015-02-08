package org.jodt.property;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author Oliver Stuch  (oliver@stuch.net)
 */

public interface Property<T> {
    
    Collection<Annotation> annotations();

    String name();
    
    String displayName();

    Class<T> type();

    /**
     * @return null means: no description available
     */
    String description();

    T value();

    /**
     * @return Eine durch Setzen des Wertes neu entstandene Property oder null (falls keine neue erzeugt wurde)
     */
    CompositeProperty<T> value(T value);

}
