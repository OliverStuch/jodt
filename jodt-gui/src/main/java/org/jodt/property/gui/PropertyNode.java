package org.jodt.property.gui;


import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jodt.property.Property;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public interface PropertyNode<T> extends MutableTreeTableNode, Property<T> {
    
     <P> MutableTreeTablePropertyNode<P> create(Property<P> property);

}