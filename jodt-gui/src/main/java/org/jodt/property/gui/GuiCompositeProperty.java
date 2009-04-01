package org.jodt.property.gui;

import org.jodt.property.CompositeProperty;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public interface GuiCompositeProperty extends CompositeProperty{
    /**
     * Überschrift über die Namen der Properties
     */
    String nameColumnName();

    /**
     * Überschrift über die Werte der Properties
     */
    String valueColumnName();
}
