package org.jodt.property.comparison.gui;

import java.awt.Component;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.jodt.property.comparison.DiffType;
import org.jodt.util.ToStringRenderer;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;


/**
 * @author Oliver Stuch
 */

public class DiffIconTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
    private Diff2Icon diff2icon;
    private DefaultJXTreeTable defaultJXTreeTable;

    DiffIconTableCellRenderer() {
        this.diff2icon = new Diff2Icon();
    }

    public Component getTableCellRendererComponent(DefaultJXTreeTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String displayString;
        ToStringRenderer toStringRenderer = table.getToStringRenderer(value.getClass());
        if (toStringRenderer != null) {
            displayString = toStringRenderer.render2String(value);
        } else {
            DiffType diff = (DiffType) value;
            displayString = diff.toString(); // TODO ... debug ausgabe ändern in spezielle Ausgabe
        }
        setIcon(diff2icon.getImplementation(value.getClass()));
        return super.getTableCellRendererComponent(table, displayString, isSelected, hasFocus, row, column);
    }

}
