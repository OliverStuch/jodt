package org.jodt.property.comparison.gui;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.comparison.CompositeMerge;
import org.jodt.property.gui.MutableTreeTablePropertyNode;

/**
 *
 * Diese Klasse erlaubt es ein CompositeMerge-Objekt zu editieren. Vom Wesen her
 * ist es also ein CompositeProperty2TreeTableNodeAdapter, der zwei weitere
 * Spalten (compare und reference) hat, die beim Editieren LESEND benutzt
 * werden. Es muss beachtet werden, dass der
 * CompositeProperty2TreeTableNodeAdapter-Anteil mit einer gemappten (also auch
 * indizierten!) CompositeProperty betrieben werden muss. Das Einfügen von
 * Collection-Elementen muss auch im compare- und reference-objekt zu neuen
 * Einträgen führen. Hier legen ohnehin gemappte CompositeProperty vor.
 *
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class CompositeMerge2TreeTableNodeAdapter<T> extends AbstractMutableTreeTableNode implements MutableTreeTablePropertyNode<T> {

    public CompositeMerge2TreeTableNodeAdapter(CompositeMerge<T> compositeMerge) {
        this.compositeMerge = compositeMerge;
        setUserObject(compositeMerge); // ein bischen redundant
        if (compositeMerge.hasChildren()) {
            for (CompositeMerge<?> compositeMergeChild : compositeMerge) {
                MutableTreeTableNode childNode = create(compositeMergeChild);
                add(childNode);
            }
        }

    }

    public <P> MutableTreeTablePropertyNode<P> create(Property<P> property) {
        if (property instanceof CompositeMerge) {
            return new CompositeMerge2TreeTableNodeAdapter<P>((CompositeMerge) property);
        } else {
            throw new IllegalArgumentException("CompositeProperty2TreeTableNodeAdapter.create must receive a CompositeMerge. Got " + property.getClass());
        }
    }

    public int getColumnCount() {
        return 4;
    }

    public Object getValueAt(int column) {
        switch (column) {
            case NAME_COLUMN:
                return displayName();
            case VALUE_COLUMN:
                return value();
            case COMPARE_COLUMN:
                return compositeMerge.getCompareObjectAsIndexMappedCompositePropertyList().value();
            case REFERENCE_COLUMN:
                return compositeMerge.getReferenceObjectAsIndexMappedCompositePropertyList().value();
            default:
                return null;
        }
    }

    @Override
    public String description() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public String name() {
        return compositeMerge.name();
    }

    @Override
    public String displayName() {
        return compositeMerge.displayName();
    }

    public Class<T> type() {
        return compositeMerge.type();
    }

    public Collection<Annotation> annotations() {
        return compositeMerge.annotations();
    }

    public T value() {
        return compositeMerge.value();
    }

    public CompositeProperty<T> value(T value) {
        return compositeMerge.value(value);
    }

    public Property<T> getProperty() {
        return compositeMerge.getMergeProperty();
    }

    private CompositeMerge<T> compositeMerge;
    private static final int NAME_COLUMN = 0;
    private static final int VALUE_COLUMN = 3;
    private static final int COMPARE_COLUMN = 1;
    private static final int REFERENCE_COLUMN = 2;

}
