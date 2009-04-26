package org.jodt.property.comparison.gui;


import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jodt.property.comparison.ComparisonStrategy;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.CompositeMerge;
import org.jodt.property.gui.ContextMenuFactory;
import org.jodt.property.gui.PropertyNode;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;
import org.jodt.util.gui.treetable.DefaultParentUpdater;

/**
 * @author Oliver Stuch
 * @deprecated very alpha
 */

public class MergeTreeTable extends CompositeComparisonTreeTable {

    public MergeTreeTable(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName,
            ComparisonStrategy comparisonStrategy) {
        super(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName, comparisonStrategy);
        new ContextMenuFactory().setupContextMenu(this);
    }

    protected TreeTableModel createTreeTableModel(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName) {
        CompositeComparison compositeComparison = comparisonStrategy().createComparison(comparativeObject, comparativeObjectName, referenceObject,
                referenceObjectName);
        CompositeMerge compositeMerge = comparisonStrategy().addMergeObject(compositeComparison);
        Map<Object, PropertyNode> userObject2Node = new HashMap();
        DefaultJXTreeTable.DefaultJXTreeTableModel dttm = new DefaultJXTreeTableModel(new CompositeMerge2TreeTableNodeAdapter(compositeMerge, userObject2Node), userObject2Node ,new String[] {
                "Attribut oder Index", "comparative", "reference", "merge" });
        dttm.addTreeModelListener(new DefaultParentUpdater(dttm));
        dttm.setNotEditable(notEditableRegistry);

        return dttm;
    }
}
