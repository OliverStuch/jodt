package org.jodt.property.comparison.gui;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jodt.property.comparison.ComparisonStrategy;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.implementation.Diff2ToStringRenderer;
import org.jodt.property.gui.CompositePropertyTreeTable;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;
import org.jodt.util.gui.treetable.DefaultParentUpdater;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class CompositeComparisonTreeTable extends DefaultJXTreeTable {

    private Object comparativeObject;
    private Object referenceObject;
    private String comparativeObjectName;
    private String referenceObjectName;
    private ComparisonStrategy comparisonStrategy;

    protected ComparisonStrategy comparisonStrategy() {
        return this.comparisonStrategy;
    }

    /**
     * @param object
     *            Das in einer PropertyTreeTable darzustellende Objekt
     * @param objectName
     *            Name des Objekts (momentan ohne Funktion/Bedeutung)
     */
    public CompositeComparisonTreeTable(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName,
            ComparisonStrategy comparisonStrategy) {
        this.comparisonStrategy = comparisonStrategy;
        setTreeCellRenderer(new DiffIconTreeCellRenderer(this));
        addToStringRenderer(new Diff2ToStringRenderer());
        set(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName);
    }

    // TODO unschön!?
    public void update() {
        set(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName);
    }

    public void set(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName) {
        this.comparativeObject = comparativeObject;
        this.comparativeObjectName = comparativeObjectName;
        this.referenceObject = referenceObject;
        this.referenceObjectName = referenceObjectName;
        setTreeTableModel(createTreeTableModel(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName));
    }

    protected TreeTableModel createTreeTableModel(Object comparativeObject, String comparativeObjectName, Object referenceObject, String referenceObjectName) {
        CompositeComparison compositeComparison = comparisonStrategy.createComparison(comparativeObject, comparativeObjectName, referenceObject, referenceObjectName);
        DefaultJXTreeTable.DefaultJXTreeTableModel dttm = new DefaultJXTreeTableModel(new CompositeComparison2TreeTableNodeAdapter(compositeComparison), new String[] {
                "Attribut oder Index", comparativeObjectName, referenceObjectName });
        dttm.addTreeModelListener(new DefaultParentUpdater(dttm));
        dttm.setNotEditable(notEditableRegistry);

        return dttm;
    }

    private static Logger logger = Logger.getLogger(CompositePropertyTreeTable.class);

}
