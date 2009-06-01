package org.jodt.util.gui.treetable;

import java.util.Enumeration;

import org.jdesktop.swingx.treetable.TreeTableNode;

public interface AddChildrenOnDemandNode {
    public void addChildren();
    
    // from TreeTableNode
    public TreeTableNode getChildAt(int i) ;
    // from TreeTableNode
    public Enumeration children();
    // from TreeNode
    public boolean isLeaf();

}
