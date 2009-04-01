package org.jodt.property.comparison.implementation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.jodt.property.comparison.IndexMapper;


/**
 * Teilweise Implementierung des List-Interfaces.
 * 
 * @author Oliver Stuch
 */
public class MappedList<T> implements List<T> {
    public MappedList(List<T> delegate, IndexMapper mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    public boolean add(T e) {
        mapper.addMapping(size(), size());
        return delegate.add(e);
    }

    public boolean addAll(Collection<? extends T> c) {
        for (T t : c) {
            if (add(t) == false) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        mapper.clear();
        delegate.clear();
    }

    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * 
     * @return Der übegebene index wird per internem Mapping auf einen anderen Index gemapped mit dem aus der dem Konstruktur übergebenen Liste per get(int) der Rückgabewert geholt wird.
     *         Gibt es zu dem übergebenen Index keinen gemapped Index, wird null zurückgegeben.
     */
    public T get(int index) {
        int mappedIndex = mapper.map(index);
        if (mappedIndex < 0) {
            return null;
        }
        return delegate.get(mappedIndex);
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public int indexOf(Object o) {
        return mapper.map(delegate.indexOf(o));
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Iterator<T> iterator() {
        return new MappedListIterator();
    }

    private class MappedListIterator implements Iterator<T> {
        int nextIndex = 0;

        public boolean hasNext() {
            return size() > nextIndex;
        }

        public T next() {
            return get(nextIndex++);
        }

        public void remove() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

    }

    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    public ListIterator<T> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public T set(int index, T element) {
        int mappedIndex = mapper.map(index);
        if (mappedIndex < 0) { // not yet mapped
            throw new IndexOutOfBoundsException();
        }
        return delegate.set(mappedIndex, element);
    }

    public int size() {
        return mapper.size();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    private IndexMapper mapper;

    private List<T> delegate;

    private static Logger logger = Logger.getLogger(MappedList.class);
}
