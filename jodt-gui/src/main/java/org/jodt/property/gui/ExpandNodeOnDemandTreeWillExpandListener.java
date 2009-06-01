package org.jodt.property.gui;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

import org.jodt.util.gui.treetable.AddChildrenOnDemandNode;

public class ExpandNodeOnDemandTreeWillExpandListener implements TreeWillExpandListener {

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // do nothing!
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        // TODO Auto-generated method stub
        AddChildrenOnDemandNode nodeWhichWillBeExpanded = (AddChildrenOnDemandNode) event.getPath().getLastPathComponent();
        nodeWhichWillBeExpanded.addChildren();
    }

}
