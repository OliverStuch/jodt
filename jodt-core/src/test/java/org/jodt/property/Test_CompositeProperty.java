package org.jodt.property;

/**
 * Dieser Test testet den Abstrakten Test mit den Implementiereungen der DefaultCompositePropertyFactory => ohne GUI
 * 
 * @author Oliver Stuch (oliver@stuch.net)
 */

public class Test_CompositeProperty extends AbstractTest_CompositeProperty {

    public <T> CompositeProperty<T> createCompositeProperty(T value, String name, CompositeProperty<?> parent) {
        return pcf.createCompositeProperty(value, name, parent);
    }

    public <T> CompositeProperty<T> createCompositeProperty(T object, String name) {
        return pcf.createCompositeProperty(object, name);
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
