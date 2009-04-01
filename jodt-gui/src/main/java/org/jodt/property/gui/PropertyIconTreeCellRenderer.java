package org.jodt.property.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;


import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jodt.util.ToStringRenderer;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class PropertyIconTreeCellRenderer implements TreeCellRenderer {

    private DefaultJXTreeTable defaultJXTreeTable;

    public PropertyIconTreeCellRenderer(DefaultJXTreeTable defaultJXTreeTable) {
        this.defaultJXTreeTable = defaultJXTreeTable;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = new JLabel();
        label.setForeground(Color.BLACK);

        if (selected) {
            Component vRenderPane = tree.getParent();
            if (vRenderPane != null) {
                JXTreeTable vTable = (JXTreeTable) vRenderPane.getParent();
                if (vTable != null) {
                    if (vTable.hasFocus()) {
                        label.setForeground(Color.WHITE);
                    }
                }
            }
        }

        TreeTableNode treeTableNode = (TreeTableNode) value;
        Object property = treeTableNode.getValueAt(0);

        String displayString;
        ToStringRenderer toStringRenderer = (property != null ? defaultJXTreeTable.getToStringRenderer(property.getClass()) : null);
        if (toStringRenderer != null) {
            displayString = toStringRenderer.render2String(property);
        } else {
            // logger.debug("no renderer found for " + property);
            displayString = " ";
        }

        label.setText(displayString);
        return label;
    }
}