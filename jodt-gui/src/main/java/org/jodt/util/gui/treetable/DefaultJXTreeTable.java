package org.jodt.util.gui.treetable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.UIAction;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.FormatStringValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jdesktop.swingx.util.Utilities;
import org.jodt.property.CompositeProperty;
import org.jodt.property.gui.PropertyNode;
import org.jodt.property.gui.editors.DoubleEditor;
import org.jodt.property.gui.editors.FloatEditor;
import org.jodt.property.gui.editors.IntegerEditor;
import org.jodt.property.gui.editors.LongEditor;
import org.jodt.property.gui.editors.StringEditor;
import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;
import org.jodt.util.gui.Factory;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultJXTreeTable extends JXTreeTable {

    public DefaultJXTreeTable() {
        rendererRegistry = new Registry();
        toStringRendererRegistry = new Registry();
        editorRegistry = new Registry();
        editorFactory = new Factory();
        notEditableRegistry = new Registry<TableCellEditor>();
        init();
    }

    private void init() {
        setupToStringRendererRegistry();
        setupRendererRegistry();
        setupEditors();
        setupActions();
        setupAccelerators();
        putClientProperty("JTree.lineStyle", "Angled");
        setupIcons();
        setupHighlightning();
    }

    private void setupHighlightning() {
        setHighlighters(HighlighterFactory.createSimpleStriping());
        addHighlighter(new ShadingColorHighlighter(new HighlightPredicate.ColumnHighlightPredicate(0)));
        addHighlighter(new ShadingColorHighlighter(HighlightPredicate.ROLLOVER_ROW));
        setRolloverEnabled(true);
    }

    private void setupIcons() {
        setExpandedIcon(new ImageIcon(DefaultJXTreeTable.class.getResource("tree_expanded3.png")));
        setCollapsedIcon(new ImageIcon(DefaultJXTreeTable.class.getResource("tree_collapsed3.png")));
        setLeafIcon(new ImageIcon(DefaultJXTreeTable.class.getResource("tree_leaf.png")));
        setClosedIcon(new ImageIcon(DefaultJXTreeTable.class.getResource("tree_closed.png")));
        setOpenIcon(new ImageIcon(DefaultJXTreeTable.class.getResource("tree_open.png")));
    }

    private void setupActions() {
        ActionMap map = getActionMap();
        map.put(ACTION_EXPAND, new Actions(ACTION_EXPAND));
        // map.put(ACTION_SELECT_CURRENT_LEVEL, new Actions(ACTION_SELECT_CURRENT_LEVEL));
    }

    private class Actions extends UIAction {

        Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            if (ACTION_EXPAND.equals(getName())) {
                expandCompletePath(getSelectedRow());
            } else if (ACTION_SELECT_CURRENT_LEVEL.equals(getName())) {
                selectCurrentLevel();
            }
        }
    }

    private void setupAccelerators() {
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getAccelerator("F"), "find");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getAccelerator("C"), "collapse-all");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getAccelerator("E"), "expand-all");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getAccelerator("X"), ACTION_EXPAND);
        // getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(getAccelerator("S"), ACTION_SELECT_CURRENT_LEVEL);
    }

    private KeyStroke getAccelerator(String accelerator) {
        String findMnemonic = accelerator;
        KeyStroke stroke = Utilities.stringToKey("D-" + findMnemonic);
        if (stroke == null) {
            stroke = KeyStroke.getKeyStroke("control " + accelerator);
        }
        return stroke;
    }

    /**
     * add all toStringRenderer from param toStringRendererRegistry
     */
    public void addToStringRenderer(Registry<ToStringRenderer> toStringRendererRegistry) {
        this.toStringRendererRegistry.addAll(toStringRendererRegistry);
    }

    private void setupToStringRendererRegistry() {
        toStringRendererRegistry.setImplementation(String.class, new ToStringRenderer() {
            public String render2String(Object object) {
                String string = (String) object;
                return string;
            }
        });
    }

    // Übernehmen der JXTreeTable renderer
    private void setupRendererRegistry() {
        Set<Class> keys = defaultRenderersByColumnClass.keySet();
        for (Class clazz : keys) {
            setRenderer(clazz, (TableCellRenderer) defaultRenderersByColumnClass.get(clazz));
        }
        setRenderer(Object.class, new DefaultTableRenderer(new ToStringRenderer2StringValue(toStringRendererRegistry)));
        setRenderer(Number.class, new DefaultTableRenderer(FormatStringValue.NUMBER_TO_STRING, JLabel.LEFT));
        setRenderer(Date.class, new DefaultTableRenderer(new ToStringRenderer2StringValue(toStringRendererRegistry)));
        // rendererRegistry.setImplementation(Integer.class, new net.stuch.yag.util.gui.property.NumberRenderer());

        // setDefaultRenderer(Number.class, new DefaultTableRenderer(
        // FormatStringValue.NUMBER_TO_STRING, JLabel.RIGHT));
        // setDefaultRenderer(Date.class, new DefaultTableRenderer(
        // FormatStringValue.DATE_TO_STRING));
        // // use the same center aligned default for Image/Icon
        // TableCellRenderer renderer = new DefaultTableRenderer(
        // new MappedValue(StringValue.EMPTY, IconValue.ICON),
        // JLabel.CENTER);
        // setDefaultRenderer(Icon.class, renderer);
        // setDefaultRenderer(ImageIcon.class, renderer);
        // // use a ButtonProvider for booleans
        // setDefaultRenderer(Boolean.class, new DefaultTableRenderer(
        // new ButtonProvider()));
        setRenderer(Collection.class, new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Collection collection = (Collection) value;
                Iterator iterator = collection.iterator();
                Vector vector = new Vector(collection.size());
                while (iterator.hasNext()) {
                    Object nextObject = iterator.next();
                    ToStringRenderer toStringRenderer = null;
                    if (nextObject != null) {
                        toStringRenderer = getToStringRenderer(nextObject.getClass());
                    }

                    if (toStringRenderer != null) {
                        vector.add(toStringRenderer.render2String(nextObject));
                    } else {
                        vector.add(nextObject);
                    }
                }
//                String displayString = "#" + collection.size() + ": " + vector;
                String displayString = "Anzahl Elemente:" + collection.size();
                TableCellRenderer stringRenderer = getDefaultRenderer(String.class);
                return stringRenderer.getTableCellRendererComponent(table, displayString, isSelected, hasFocus, row, column);
            }
        });

        nullValueRenderer = new DefaultTableRenderer(new StringValue() {

            public String getString(Object value) {
                return "---";
            }
        });
    }

    private void setupEditors() {

        editorFactory.register(Long.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return new LongEditor();
            }
        });

        editorFactory.register(Float.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return new FloatEditor();
            }
        });

        editorFactory.register(Integer.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return new IntegerEditor();
            }
        });

        editorFactory.register(Double.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return new DoubleEditor();
            }
        });

        editorFactory.register(String.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return new StringEditor();
            }
        });

        editorFactory.register(Object.class, new Factory.Creator<TableCellEditor>() {
            public TableCellEditor create() {
                return null;
            }
        });

        // setupEditorRegistry
        // first copy swingx editors
        Set<Class> keys = defaultEditorsByColumnClass.keySet();
        for (Class clazz : keys) {
            editorRegistry.setImplementation(clazz, (TableCellEditor) defaultEditorsByColumnClass.get(clazz));
        }
        // then replace some editors not working in treetables
        editorRegistry.setImplementation(Float.class, editorFactory.create(Float.class));
        editorRegistry.setImplementation(Integer.class, editorFactory.create(Integer.class));
        editorRegistry.setImplementation(Double.class, editorFactory.create(Double.class));
        editorRegistry.setImplementation(String.class, editorFactory.create(String.class));
        editorRegistry.setImplementation(Object.class, editorFactory.create(Object.class));
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        Object object2render = getModel().getValueAt(convertRowIndexToModel(row), convertColumnIndexToModel(column));
        if (isHierarchical(column)) {
            return super.getCellRenderer(row, column);
        } else {
            if (object2render == null) {
                // return (TableCellRenderer) rendererRegistry.getImplementation(Object.class);
                return nullValueRenderer;
            }
            return (TableCellRenderer) rendererRegistry.getImplementation(object2render.getClass());
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        TableCellEditor result = null;
        Object object2edit = getModel().getValueAt(convertRowIndexToModel(row), convertColumnIndexToModel(column));
        if (object2edit == null) {
            result = (TableCellEditor) editorRegistry.getImplementation(null);
            if (result == null) {
                logger.debug("no editor registered for NULL");
            }
        } else {
            result = (TableCellEditor) editorRegistry.getImplementation(object2edit.getClass());
        }
        return result;
    }

    public TableCellRenderer getHierachicalCellRenderer() {
        // return super.getCellRenderer(convertRowIndexToModel(0), convertRowIndexToModel(0));
        return super.getCellRenderer((0), (0));
    }

    public void setRenderer(Class clazz, TableCellRenderer tableCellRenderer) {
        rendererRegistry.setImplementation(clazz, tableCellRenderer);
    }

    public void setEditor(Class clazz, TableCellEditor tableCellEditor) {
        editorRegistry.setImplementation(clazz, tableCellEditor);
    }

    public void setEditorCreator(Class clazz, Factory.Creator<TableCellEditor> editorCreator) {
        editorFactory.register(clazz, editorCreator);
    }

    public void setNotEditable(Class clazz) {
        DefaultJXTreeTableModel ttm = (DefaultJXTreeTableModel) getTreeTableModel();
        ttm.notEditableRegistry.setImplementation(clazz, new NotEditableEditor());
    }

    private class NotEditableEditor extends AbstractCellEditor implements TableCellEditor {

        public boolean isCellEditable(EventObject anEvent) {
            return false;
        }

        public Object getCellEditorValue() {
            throw new UnsupportedOperationException("not editable");
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            throw new UnsupportedOperationException("not editable");
        }

    }

    public void setToStringRenderer(Class clazz, ToStringRenderer toStringRenderer) {
        toStringRendererRegistry.setImplementation(clazz, toStringRenderer);
    }

    public ToStringRenderer getToStringRenderer(Class<?> clazz) {
        return (ToStringRenderer) toStringRendererRegistry.getImplementation(clazz);
    }

    public Registry<TableCellEditor> getEditorRegitry() {
        return editorRegistry;
    }

    public Factory<TableCellEditor> getEditorFactory() {
        return editorFactory;
    }

    /**
     * TODO Oliver: removen auch aus dem Model
     */
    public void removeSelectedToplevelObjects() {
        DefaultJXTreeTableModel ttm = (DefaultJXTreeTableModel) getTreeTableModel();
        List result = new ArrayList();
        int[] selectedRows = getSelectedRows();
        for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
            TreePath nodePath = getPathForRow(selectedRows[rowIndex]);
            TreeTableNode childOfRoot = (TreeTableNode) nodePath.getPathComponent(1);
            ttm.removeNodeFromParent((MutableTreeTableNode) childOfRoot);
        }
    }

    public boolean isToplevel(int row) {
        TreePath treePath = getPathForRow(row);
        TreeTableNode node = (TreeTableNode) treePath.getLastPathComponent();
        if (node.getParent() != null) {
            if (node.getParent().getParent() == null) {
                return true;
            }
        }
        return false;
    }

    public List getSelectedToplevelObjects() {
        DefaultJXTreeTableModel ttm = (DefaultJXTreeTableModel) getTreeTableModel();
        List result = new ArrayList();
        int[] selectedRows = getSelectedRows();
        for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
            TreePath nodePath = getPathForRow(selectedRows[rowIndex]);
            TreeTableNode childOfRoot = (TreeTableNode) nodePath.getPathComponent(1);
            result.add(childOfRoot.getUserObject());
        }
        return result;
    }

    public TreePath[] getSelectionPaths() {
        int[] selectedRows = getSelectedRows();
        TreePath[] result = new TreePath[selectedRows.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = getPathForRow(i);
        }
        return result;
    }

    private int expandCompletePath(int rowStart) {
        int rowCountBefore = getRowCount();
        expandRow(rowStart);
        int rowCountAfter = getRowCount();
        int numRowsExpanded = 0;
        int numChildrenRowsExpanded = 0;
        if (rowCountAfter == rowCountBefore) { // war schon expandiert
            TreePath treePath = getPathForRow(rowStart);
            if (treePath != null) {
                TreeTableNode ttn = (TreeTableNode) treePath.getLastPathComponent();
                for (; numRowsExpanded < ttn.getChildCount(); numRowsExpanded++) {
                    numChildrenRowsExpanded += expandCompletePath(1 + rowStart + numRowsExpanded + numChildrenRowsExpanded);
                }
            }
        } else {
            for (; numRowsExpanded < rowCountAfter - rowCountBefore; numRowsExpanded++) {
                numChildrenRowsExpanded += expandCompletePath(1 + rowStart + numRowsExpanded + numChildrenRowsExpanded);
            }
        }
        return numRowsExpanded + numChildrenRowsExpanded;
    }

    /**
     * @deprecated tut noch nicht
     */
    public void selectCurrentLevel() {
        // start by getting current selection state
        TreePath[] selectedPaths = getSelectionPaths();
        if (selectedPaths == null) {
            return;
        }
        // continue by extracting paths that are under different immediate parents
        List<TreePath> uniqueParentPaths = new ArrayList<TreePath>();
        // array is ok here, there aren't usually that many paths selected.
        List uniqueParents = new ArrayList();
        for (TreePath selectedPath : selectedPaths) {
            TreePath parentPath = selectedPath.getParentPath();
            if (parentPath == null) // root hasn't got a parent path
            {
                continue;
            }
            Object parent = parentPath.getLastPathComponent();
            // filter out unique parents and add their TreePaths to collection
            if (!uniqueParents.contains(parent)) {
                uniqueParents.add(parent);
                uniqueParentPaths.add(parentPath);
            }
        }
        // loop through the uniqueParentPaths and create new treepaths by adding the childs of the parents
        // to existing path.
        List<TreePath> paths = new ArrayList<TreePath>();
        TreeTableModel model = getTreeTableModel();
        ListSelectionModel listSelectionModel = getSelectionModel();
        for (TreePath uniqueParentPath : uniqueParentPaths) {
            Object parent = uniqueParentPath.getLastPathComponent();
            // using the model to get the 'childs'
            int childCount = model.getChildCount(parent);
            for (int i = 0; i < childCount; i++) {
                paths.add(uniqueParentPath.pathByAddingChild(model.getChild(parent, i)));
            }
        }
        // finally select all the 'nodes' that were found.
        getTreeSelectionModel().setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
    }

    /**
     * @supplierRole toStringRenderer
     */
    private Registry<ToStringRenderer> toStringRendererRegistry;
    /**
     * @supplierRole cellRenderer
     */
    private Registry<TableCellRenderer> rendererRegistry;
    /**
     * @supplierRole cellEditors
     */
    private Registry<TableCellEditor> editorRegistry;
    private Factory<TableCellEditor> editorFactory;
    protected Registry<TableCellEditor> notEditableRegistry;
    private TableCellRenderer nullValueRenderer;

    private static final Logger logger = Logger.getLogger(DefaultJXTreeTable.class);
    private static final String ACTION_EXPAND = "expand";
    private static final String ACTION_SELECT_CURRENT_LEVEL = "select-current-level";

    // inner classes
    public class DefaultJXTreeTableModel extends DefaultTreeTableModel {

        private String[] columnNames;
        private Registry<TableCellEditor> notEditableRegistry;

        public DefaultJXTreeTableModel(TreeTableNode root, String... columnNames) {
            super(root);
            this.columnNames = columnNames;
        }

        public void setValue(Object value, PropertyNode node) {
            if (!isValidTreeTableNode(node)) {
                throw new IllegalArgumentException("node must be a valid node managed by this model");
            }
            CompositeProperty newProperty = node.value(value);
            if (newProperty == null) {
                modelSupport.firePathChanged(new TreePath(getPathToRoot(node)));
            } else {
                // replace(node, new CompositeProperty2TreeTableNodeAdapter(newProperty));
                replace(node, node.create(newProperty));
            }
        }

        public void replace(MutableTreeTableNode node, MutableTreeTableNode newNode) {
            MutableTreeTableNode parent = (MutableTreeTableNode) node.getParent();
            int indexInParent = parent.getIndex(node);
            removeNodeFromParent(node);
            insertNodeInto(newNode, parent, indexInParent);
        }

        // @Deprecated
        // public void setValue(Object value, PropertyTreeTableNode node) {
        // if (!isValidTreeTableNode(node)) {
        // throw new IllegalArgumentException("node must be a valid node managed by this model");
        // }
        //
        // node.value(value);
        // modelSupport.firePathChanged(new TreePath(getPathToRoot(node)));
        //
        // }
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(Object node, int column) {
            if (!isValidTreeTableNode(node)) {
                throw new IllegalArgumentException("node must be a valid node managed by this model");
            }

            if (column < 0) {// || column >= getColumnCount()) {
                throw new IllegalArgumentException("column must be a valid index");
            }

            TreeTableNode ttn = (TreeTableNode) node;

            // if (column >= ttn.getColumnCount()) {
            // return null;
            // }
            return ttn.getValueAt(column);
        }

        /**
         * copy from JXTreeTable
         */
        @Override
        public boolean isCellEditable(Object node, int column) {
            if (super.isCellEditable(node, column)) {
                // nochmal genauer draufschauen
                TreeTableNode ttn = (TreeTableNode) node;
                do {
                    Object value = ttn.getValueAt(column);
                    if (value != null) {
                        TableCellEditor editor = notEditableRegistry.getImplementation(value.getClass());
                        if (editor != null && !editor.isCellEditable(null)) {
                            return false;
                        }
                    }
                    ttn = ttn.getParent();
                } while (ttn != null);
                return true;
            } else {
                return false;
            }
        }

        /**
         * copy from JXTreeTable
         */
        private boolean isValidTreeTableNode(Object node) {
            boolean result = false;

            if (node instanceof TreeTableNode) {
                TreeTableNode ttn = (TreeTableNode) node;

                while (!result && ttn != null) {
                    result = ttn == root;

                    ttn = ttn.getParent();
                }
            }

            return result;
        }

        /**
         * Diese Methode wird ...bitte festhalten... per reflection von JXTree
         * aus geholt und aufgerufen!
         */
        public String convertValueToText(Object object) {
            ToStringRenderer toStringRenderer = (ToStringRenderer) toStringRendererRegistry.getImplementation(object.getClass());
            if (toStringRenderer != null) {
                return toStringRenderer.render2String(object);
            } else {
                return object.toString();
            }

        }

        // public int getDiffColumn(int row) {
        // TreeTableNode rootNode = getRoot();
        //
        // if (ObjectComparison2TreeTableNodeAdapter.class.isAssignableFrom(rootNode.getClass())) {
        // return 1;
        // } else if (Diff2TreeTableNodeAdapter.class.isAssignableFrom(rootNode.getClass())) {
        // return 0;
        // } else {
        // throw new RuntimeException(rootNode.getClass() + " not supported");
        // }
        // }
        public void fireTableDataChanged() {
            modelSupport.fireStructureChanged();
        }

        public void firePathChanged(TreePath parentPath) {
            modelSupport.firePathChanged(parentPath);
        }

        public void setNotEditable(Registry<TableCellEditor> notEditableRegistry) {
            this.notEditableRegistry = notEditableRegistry;
        }

    }

    private static class ToStringRenderer2StringValue implements StringValue {

        private Registry<ToStringRenderer> toStringRendererRegistry;

        public ToStringRenderer2StringValue(Registry<ToStringRenderer> toStringRendererRegistry) {
            this.toStringRendererRegistry = toStringRendererRegistry;
        }

        public String getString(Object value) {
            if (value == null) {
                return null;
            }
            ToStringRenderer renderer = toStringRendererRegistry.getImplementation(value.getClass());
            if (renderer == null) {
                return value + "";
            }
            String result = renderer.render2String(value);
            return result;
        }
    }

}
