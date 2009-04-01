package org.jodt.property.comparison.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;


import org.jdesktop.swingx.JXTreeTable;
import org.jodt.property.comparison.Comparison;
import org.jodt.property.comparison.DiffType;
import org.jodt.property.comparison.implementation.NoDiff;
import org.jodt.property.gui.MutableTreeTablePropertyNode;
import org.jodt.util.ToStringRenderer;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;

/**
 * @author Oliver Stuch
 */

public class DiffIconTreeCellRenderer implements TreeCellRenderer {
    private Diff2Icon diff2icon;
    private DefaultJXTreeTable defaultJXTreeTable;

    DiffIconTreeCellRenderer(DefaultJXTreeTable defaultJXTreeTable) {
        this.defaultJXTreeTable = defaultJXTreeTable;
        this.diff2icon = new Diff2Icon();
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

        MutableTreeTablePropertyNode comparisonNode = (MutableTreeTablePropertyNode) value;
        Comparison compositeComparison = (Comparison) comparisonNode.getUserObject();
        DiffType diff = compositeComparison.diff();
        if (diff == null) {
            diff = new NoDiff(null, null);
        }
        ToStringRenderer toStringRenderer = defaultJXTreeTable.getToStringRenderer(diff.getClass());
        label.setText(comparisonNode.name());// compositeComparison.getCompareObjectAsIndexMappedCompositePropertyList().name());
        label.setIcon(diff2icon.getImplementation(diff.getClass()));
        return label;
    }
}
