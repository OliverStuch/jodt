package org.jodt.util.gui.treetable;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultParentUpdater extends AbstractParentUpdater {

    private DefaultJXTreeTable.DefaultJXTreeTableModel dttm;

    public DefaultParentUpdater(DefaultJXTreeTable.DefaultJXTreeTableModel dttm) {
        this.dttm = dttm;
    }

    protected void childrenChanged(TreePath treePath) {
        if (treePath.getParentPath() != null) {
            dttm.firePathChanged(treePath);
            childrenChanged(treePath.getParentPath());
        }
    }

    @Override
    protected void parentChanged(TreeModelEvent e) {
        TreeTableNode ttn1 = (TreeTableNode) e.getChildren()[0];
        if (ttn1.getChildCount() != 0) {
            TreeTableNode ttn2 = (TreeTableNode) ttn1.getChildAt(0);
            if (ttn2.getChildCount() == 0) {
                dttm.firePathChanged(new TreePath(dttm.getPathToRoot(ttn2)));
            }
        }
    }

}