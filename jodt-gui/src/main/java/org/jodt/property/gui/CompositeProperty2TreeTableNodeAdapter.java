package org.jodt.property.gui;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;

/**
 * Diese Klasse adaptiert ein CompositeProperty-Objekt für die TreeTable. Es ist
 * als Property ansprechbar.
 *
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class CompositeProperty2TreeTableNodeAdapter<T> extends AbstractMutableTreeTableNode implements MutableTreeTablePropertyNode<T> {

    public CompositeProperty2TreeTableNodeAdapter(CompositeProperty<T> compositeProperty) {
        this.compositeProperty = compositeProperty;
        setUserObject(compositeProperty); // ein bischen redundant
        if (compositeProperty.hasProperties()) {
            for (CompositeProperty<?> compositePropertyChild : compositeProperty) {
                MutableTreeTablePropertyNode<?> childNode = create(compositePropertyChild);
                add(childNode);
            }
        }
    }

    public <P> MutableTreeTablePropertyNode<P> create(Property<P> property) {
        if (property instanceof CompositeProperty) {
            return new CompositeProperty2TreeTableNodeAdapter<P>((CompositeProperty) property);
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
                return displayName();
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
    @Override
    public String description() {
        return compositeProperty.description();
    }

    /**
     * {@inheritDoc Property#name()}
     */
    @Override
    public String name() {
        return compositeProperty.name();
    }

    @Override
    public String displayName() {
        return compositeProperty.displayName();
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
