package org.jodt.property.comparison.gui;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.gui.MutableTreeTablePropertyNode;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class CompositeComparison2TreeTableNodeAdapter<T> extends AbstractMutableTreeTableNode implements MutableTreeTablePropertyNode<T> {

    public CompositeComparison2TreeTableNodeAdapter(CompositeComparison<T> compositeComparison) {
        this.compositeComparison = compositeComparison;
        setUserObject(compositeComparison); // ein bischen redundant
        if (compositeComparison.hasChildren()) {
            for (CompositeComparison<?> compositeComparisonChild : compositeComparison) {
                MutableTreeTableNode childNode = new CompositeComparison2TreeTableNodeAdapter(compositeComparisonChild);
                add(childNode);
            }
        }
    }

    public int getColumnCount() {
        return 3;
    }

    // MutableTreeTableNode
    public void setValueAt(Object value, int column) {
        throw new UnsupportedOperationException();
    }

    // MutableTreeTableNode
    public boolean isEditable(int column) {
        return false;
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

    public <P> MutableTreeTablePropertyNode<P> create(Property<P> property) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String description() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String name() {
        return this.compositeComparison.getCompareObjectAsIndexMappedCompositePropertyList().name();
    }

    @Override
    public String displayName() {
        return this.compositeComparison.getCompareObjectAsIndexMappedCompositePropertyList().displayName();
    }

    public Class<T> type() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public T value() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public CompositeProperty<T> value(T value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Collection<Annotation> annotations() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Property<T> getProperty() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    private CompositeComparison<T> compositeComparison;
    protected static final int NAME_COLUMN = 0;
    private static final int COMPARE_COLUMN = 1;
    private static final int REFERENCE_COLUMN = 2;

}
