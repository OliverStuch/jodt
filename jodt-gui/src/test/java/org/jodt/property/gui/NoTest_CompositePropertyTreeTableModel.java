package org.jodt.property.gui;

import java.util.Collection;
import java.util.Iterator;

import org.jodt.property.AbstractTest_CompositeProperty;
import org.jodt.property.CompositeProperty;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;
import org.jodt.util.gui.treetable.DefaultJXTreeTable;
import org.jodt.util.gui.treetable.DefaultJXTreeTable.DefaultJXTreeTableModel;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */

public class NoTest_CompositePropertyTreeTableModel extends AbstractTest_CompositeProperty {
    private CompositePropertyTreeTable compositePropertyTreeTable;
    
    
    /**
     * erzeuge neues Testobjekt innerhalb eines CompositePropertyTreeTable
     */
    public CompositeProperty createCompositeProperty(Object object, String name) {
        compositePropertyTreeTable = new CompositePropertyTreeTable(object, name, pcf);
        return new CompositePropertyTreeTable2CompositePropertyAdapter(compositePropertyTreeTable);
    }

    public CompositeProperty createCompositeProperty(Object value, String name, CompositeProperty parent) {
//        compositePropertyTreeTable.getTreeTableNode(parent);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    @Override
    public PropertyToolConfiguration configure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static class CompositePropertyTreeTable2CompositePropertyAdapter implements CompositeProperty {

        private CompositePropertyTreeTable compositePropertyTreeTable;
        private DefaultJXTreeTable.DefaultJXTreeTableModel dttm;
        private CompositeProperty2TreeTableNodeAdapter root;

        public CompositePropertyTreeTable2CompositePropertyAdapter(CompositePropertyTreeTable compositePropertyTreeTable) {
            this.compositePropertyTreeTable = compositePropertyTreeTable;
            this.dttm = (DefaultJXTreeTableModel) compositePropertyTreeTable.getTreeTableModel();
            this.root = (CompositeProperty2TreeTableNodeAdapter) dttm.getRoot();
        }

        @Override
        public CompositeProperty find(String name) {
            Iterator<CompositeProperty> iterator = iterator();
            while (iterator.hasNext()) {
                CompositeProperty compositeProperty = (CompositeProperty) iterator.next();
                if (compositeProperty.name().equals(name)) {
                    return compositeProperty;
                }
            }
            return null;
        }

        public CompositeProperty find(Object value) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean hasProperties() {
            return !root.isLeaf();
        }

        public void replace(CompositeProperty oldProperty, CompositeProperty newProperty) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public CompositeProperty replace(CompositeProperty oldProperty, Object newValue) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public String description() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public String name() {
            return root.name();
        }

        public Class type() {
            return root.type();
        }

        public Object value() {
            return root.value();
        }

        public CompositeProperty value(Object value) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean add(CompositeProperty e) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }


        public void clear() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean contains(Object o) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }


        public boolean isEmpty() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Iterator<CompositeProperty> iterator() {
            return new Iterator<CompositeProperty>() {
                private int index = 0;

                public boolean hasNext() {
                    return index < root.getChildCount();
                }

                public CompositeProperty next() {
                    return (CompositeProperty) root.getChildAt(index++).getUserObject();
                }

                public void remove() {
                    // TODO Auto-generated method stub
                    throw new UnsupportedOperationException();
                }
            };
        }

        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }


        public int size() {
            return root.getChildCount();
        }

        public Object[] toArray() {
            Object[] result = new Object[root.getChildCount()];
            int i = 0;
            Iterator<CompositeProperty> iterator = iterator();
            while (iterator.hasNext()) {
                result[i++] = iterator.next();
            }
            return result;
        }

        public boolean add(Object e) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection c) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection c) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection c) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Object[] toArray(Object[] a) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public Collection annotations() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        @Override
        public String displayName() {
           return root.displayName();
        }

    }


    public <T> Property<T> createProperty(T object, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }



    public <T> CompositeProperty<T> createShallowCompositeProperty(T object, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }


    public <T> CompositeProperty<T> createOneLevelRecursiveCompositeProperty(T object, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

 }
