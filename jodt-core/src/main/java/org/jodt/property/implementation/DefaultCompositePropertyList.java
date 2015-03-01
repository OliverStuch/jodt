package org.jodt.property.implementation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.jodt.property.CompositeProperty;
import org.jodt.property.CompositePropertyList;
import org.jodt.property.Property;
import org.jodt.property.PropertyTool;

/**
 * Diese Klasse wird erzeugt, wenn DefaultCompositePropertyFactory auf eine
 * Liste von Objekten trifft.
 *
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class DefaultCompositePropertyList<T> extends DelegatingCompositeProperty<T> implements CompositePropertyList<T> {

    /**
     * @param listAsProperty repräsentiert die Liste als Property. Wenn die
     * Liste z.B. in einem Objekt als Attributwert vorhanden ist, ist der
     * property.name gleich dem Attribut-Namen.
     * @param propertyList Der Inhalt der Liste als Liste von CompositeProperty
     */
    public DefaultCompositePropertyList(Property<T> listAsProperty, List<CompositeProperty<?>> propertyList, PropertyTool propertyTool) {
        super(listAsProperty, null, propertyTool);
        this.properties = propertyList;
    }

    public DefaultCompositePropertyList(Property<T> listAsProperty, List<CompositeProperty<?>> propertyList, CompositeProperty<?> parent, PropertyTool propertyTool) {
        super(listAsProperty, parent, propertyTool);
        this.properties = propertyList;
    }

    // InternalCompositeProperty
    public Collection<CompositeProperty<?>> properties() {
        return properties;
    }

    public void properties(Collection<CompositeProperty<?>> properties) {
        this.properties = (List<CompositeProperty<?>>) properties;
    }

    public CompositeProperty replace(CompositeProperty oldProperty, Object newObject) {
        CompositeProperty newProperty = createProperty(oldProperty, newObject);
        replace(oldProperty, newProperty);
        return newProperty;
    }

    public void replace(CompositeProperty property, CompositeProperty newProperty) {
        if (properties.contains(property)) {
            set(properties.indexOf(property), newProperty);
        } else {
            add(newProperty);
        }
    }

    // List-Delegates
    public boolean add(CompositeProperty e) {
        return properties.add(e);
    }

    public void add(int index, CompositeProperty element) {
        properties.add(index, element);
    }

    public boolean addAll(Collection<? extends CompositeProperty<?>> c) {
        return properties.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends CompositeProperty<?>> c) {
        return properties.addAll(index, c);
    }

    public void clear() {
        properties.clear();
    }

    public boolean contains(Object o) {
        return properties.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return properties.containsAll(c);
    }

    public CompositeProperty get(int index) {
        return properties.get(index);
    }

    public int indexOf(Object o) {
        return properties.indexOf(o);
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    public Iterator<CompositeProperty<?>> iterator() {
        return properties.iterator();
    }

    public int lastIndexOf(Object o) {
        return properties.lastIndexOf(o);
    }

    public ListIterator<CompositeProperty<?>> listIterator() {
        return properties.listIterator();
    }

    public ListIterator<CompositeProperty<?>> listIterator(int index) {
        return properties.listIterator(index);
    }

    public CompositeProperty remove(int index) {
        return properties.remove(index);
    }

    public boolean remove(Object o) {
        return properties.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return properties.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return properties.retainAll(c);
    }

    public CompositeProperty set(int index, CompositeProperty<?> element) {
        List underlyingList = (List) value();
        underlyingList.set(index, element.value());
        return properties.set(index, element);
    }

    public int size() {
        return properties.size();
    }

    public List<CompositeProperty<?>> subList(int fromIndex, int toIndex) {
        return properties.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return properties.toArray();
    }

    public <P> P[] toArray(P[] a) {
        return properties.toArray(a);
    }

    @Override
    public String toString() {
        return "DCPL{" + super.toString() + "properties=" + PropertyUtil.toString(properties, ", ") + '}';
    }

    private List<CompositeProperty<?>> properties;
}
