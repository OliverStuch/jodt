package org.jodt.property.gui;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.util.gui.treetable.AddChildrenOnDemandNode;

/**
 * Diese Klasse adaptiert ein CompositeProperty-Objekt für die TreeTable. Es ist als Property ansprechbar.
 * 
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class CompositeProperty2TreeTableNodeAdapter<T> extends AbstractMultipleParentMutableTreeTableNode implements MutableTreeTablePropertyNode<T>, AddChildrenOnDemandNode {

    public CompositeProperty2TreeTableNodeAdapter(CompositeProperty<T> compositeProperty, Map<Object, PropertyNode> userObject2Node) {
        this(compositeProperty);
        // addRecursivlyChildren(userObject2Node);
        addChildren();
    }

    /**
     * Construct without children
     */
    private CompositeProperty2TreeTableNodeAdapter(CompositeProperty compositeProperty) {
        this.compositeProperty = compositeProperty;
        setUserObject(compositeProperty); // ein bischen redundant
    }

    // Lazy
    private boolean childrenFilled = false;
    // Lazy
    @Override
    public TreeTableNode getChildAt(int i) {
        if (!childrenFilled) {
            addChildren();
        }
        return super.getChildAt(i);
    }

    // Lazy
    @Override
    public Enumeration children() {
        if (!childrenFilled) {
            addChildren();
        }
        return super.children();
    }

    // Lazy
    @Override
    public boolean isLeaf() {
        return !compositeProperty.hasProperties();
    }

    public void addChildren() {
        if (compositeProperty.hasProperties()) {
            for (CompositeProperty<?> compositePropertyChild : compositeProperty) {
                CompositeProperty2TreeTableNodeAdapter newChildNode = new CompositeProperty2TreeTableNodeAdapter<T>(compositePropertyChild);
                add(newChildNode);
            }
        }
        childrenFilled = true;
    }

// kann man wohl nicht brauchen...
    private void addRecursivlyChildren(Map<Object, PropertyNode> userObject2Node) {
        if (compositeProperty.hasProperties()) {
            for (CompositeProperty<?> compositePropertyChild : compositeProperty) {
                PropertyNode<?> childNode = userObject2Node.get(compositePropertyChild);
                if (childNode == null) {
                    CompositeProperty2TreeTableNodeAdapter newChildNode = new CompositeProperty2TreeTableNodeAdapter<T>(compositePropertyChild);
                    userObject2Node.put(compositePropertyChild, newChildNode);
                    newChildNode.addRecursivlyChildren(userObject2Node);
                    childNode = newChildNode;
                }
                add(childNode);
            }
        }
    }

     /**
     * PropertyNode
     */
    public <P> MutableTreeTablePropertyNode<P> create(Property<P> property, Map<Object, PropertyNode> userObject2Node) {
        if (property instanceof CompositeProperty) {
            return new CompositeProperty2TreeTableNodeAdapter<P>((CompositeProperty) property, userObject2Node);
        } else {
            throw new IllegalArgumentException("CompositeProperty2TreeTableNodeAdapter.create must receive a CompositeProperty. Got " + property.getClass());
        }
    }

    // MutableTreeTableNode
    public int getColumnCount() {
        return 2;
    }

    // MutableTreeTableNode
    public Object getValueAt(int column) {
        switch (column) {
        case NAME_COLUMN:
            return name();
        case VALUE_COLUMN:
            return value();
        default:
            return null;
        }
    }

    // MutableTreeTableNode
    public void setValueAt(Object value, int column) {
        switch (column) {
        case VALUE_COLUMN:
            value((T) value);
        default:
            return;
        }
    }

    // MutableTreeTableNode
    public boolean isEditable(int column) {
        switch (column) {
        case VALUE_COLUMN:
            return true;
        default:
            return false;
        }
    }

    public Property<T> getProperty() {
        return compositeProperty;
    }

    /**
     * {@inheritDoc Property#description()}
     */
    public String description() {
        return compositeProperty.description();
    }

    /**
     * {@inheritDoc Property#name()}
     */
    public String name() {
        return compositeProperty.name();
    }

    /**
     * {@inheritDoc Property#type()}
     */
    public Class type() {
        return compositeProperty.type();
    }

    public Collection<Annotation> annotations() {
        return compositeProperty.annotations();
    }

    public T value() {
        return (T) compositeProperty.value();
    }

    // Property
    public CompositeProperty<T> value(T value) {
        CompositeProperty<T> newProperty = compositeProperty.value(value);
        if (newProperty != null) {
            compositeProperty = newProperty;
            setUserObject(newProperty);
        }
        return newProperty;
    }

    private CompositeProperty<T> compositeProperty;
    private static Logger logger = Logger.getLogger(CompositeProperty2TreeTableNodeAdapter.class);

    private static final int NAME_COLUMN = 0;
    private static final int VALUE_COLUMN = 1;
}
