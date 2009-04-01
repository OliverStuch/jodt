package org.jodt.property.comparison;

import org.jodt.property.Property;

/**
 * Ein Merge-Objekt erweitert ein Comparison-Objekt um die Fähigkeit, eine Kopie des comparativeObjects zu editieren. Anwendungsfall ist, dass man aus den beiden im
 * Comparison verglichenen Objekte ein neues Objekt erzeugen möchte.
 */
public interface Merge<T> extends Comparison<T> , Property<T>{
    public Property<T> getMergeProperty();

}
