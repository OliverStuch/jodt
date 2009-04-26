package org.jodt.property.gui;

import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

public abstract class AbstractMultipleParentMutableTreeTableNode extends AbstractMutableTreeTableNode {

    @Override
    public void insert(MutableTreeTableNode child, int index) {
        if (!allowsChildren)
            throw new IllegalStateException("this node cannot accept children");
        children.add(index, child);
        if (child.getParent() != this)
            child.setParent(this);
    }

    @Override
    public void remove(int index) {
        ((MutableTreeTableNode) children.remove(index)).setParent(null);
    }

    @Override
    public void remove(MutableTreeTableNode node) {
        children.remove(node);
        node.setParent(null);
    }

    @Override
    public void removeFromParent() {
        parent.remove(this);
    }

    @Override
    public void setParent(MutableTreeTableNode newParent) {
        if (newParent != null && newParent.getAllowsChildren()) {
            if (parent != null && parent.getIndex(this) != -1)
                parent.remove(this);
        } else if (newParent != null)
            throw new IllegalArgumentException("newParent does not allow children");
        parent = newParent;
        if (parent != null && parent.getIndex(this) == -1)
            parent.insert(this, parent.getChildCount());
    }

    @Override
    public TreeTableNode getParent() {
        return parent;
    }
}
