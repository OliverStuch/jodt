package org.jodt.property.comparison.gui;

import org.jodt.property.comparison.CompositeComparison;

public class CompositeComparison2TreeNodeAdapterHelper<T> {

    private CompositeComparison<T> compositeComparison;

    public CompositeComparison2TreeNodeAdapterHelper(CompositeComparison<T> compositeComparison) {
        this.compositeComparison = compositeComparison;
    }

    public int getColumnCount() {
        return 2;
    }

    public Object getValueAt(int column) {
        switch (column) {
        case NAME_COLUMN:
            return compositeComparison.name();
        case COMPARE_COLUMN:
            return compositeComparison.getCompareObjectAsIndexMappedCompositePropertyList().value();
        case REFERENCE_COLUMN:
            return compositeComparison.getReferenceObjectAsIndexMappedCompositePropertyList().value();
        default:
            return null;
        }
    }

    private static final int NAME_COLUMN = 0;
    private static final int COMPARE_COLUMN = 1;
    private static final int REFERENCE_COLUMN = 2;

    public String name() {
        return compositeComparison.name();
    }
}
