package org.jodt.property.gui;


import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jodt.property.Property;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */


public interface MutableTreeTablePropertyNode<T> extends MutableTreeTableNode, PropertyNode<T>{

    Property<T> getProperty();
}
