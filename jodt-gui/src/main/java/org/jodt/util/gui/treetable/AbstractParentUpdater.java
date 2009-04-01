package org.jodt.util.gui.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public abstract class AbstractParentUpdater implements TreeModelListener {

    public void treeNodesChanged(TreeModelEvent e) {
        childrenChanged(e.getTreePath());
//        parentChanged(e);
    }

    protected abstract void parentChanged(TreeModelEvent e);

    protected abstract void childrenChanged(TreePath treePath);

    public void treeNodesInserted(TreeModelEvent e) {
        childrenChanged(e.getTreePath());
    }

    public void treeNodesRemoved(TreeModelEvent e) {
        // do nothing;
    }

    public void treeStructureChanged(TreeModelEvent e) {
        // do nothing
    }

}