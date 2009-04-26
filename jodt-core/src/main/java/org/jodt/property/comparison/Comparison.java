package org.jodt.property.comparison;

import org.jodt.property.Property;

/**
 * Repräsentiert den Vergleich zweier Objekte auf der Ebene der Properties der Objekte. Behandelt NICHT die Properties der Properties.
 * Ist insofern eine Property, als dass der Name gleich dem Namen der verglichenen Properties ist. Der Wert ist der Vergleich selber
 * @author Oliver Stuch  (oliver@stuch.net) 
 */
public interface Comparison<T>  {
    
    public Property<T> getCompareProperty();
    
    public Property<T> getReferenceProperty();

    public DiffType diff();
    public void diff(DiffType diffType);
    // TODO
    public boolean hasDiffsOnObjectLevel();
    
    public String name(); 

}
