package org.jodt.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jodt.property.implementation.DefaultPropertyTool;

/**
 * @author Oliver Stuch
 */

abstract public class AbstractTest_CompositeProperty extends TestCase implements InternalPropertyTool {

    public void test_creatingSpecialProperties() {

        {
            CompositeProperty propertyCollection = createCompositeProperty(null, "root"); // NULL
            assertNotNull(propertyCollection);
            assertNull(propertyCollection.value());
            assertNull(propertyCollection.type());
            assertEquals("root", propertyCollection.name());
            assertTrue(!propertyCollection.hasProperties());
        }
        {
            List toplevelList = new ArrayList();
            toplevelList.add("string");
            CompositeProperty<List> propertyCollection = createCompositeProperty(toplevelList, "toplevelList"); // NULL
            assertNotNull(propertyCollection);
            assertEquals(toplevelList, propertyCollection.value());
            assertEquals(ArrayList.class, propertyCollection.type()); // In der Tat NICHT List.class !!!
            assertEquals("toplevelList", propertyCollection.name());
            assertFalse(!propertyCollection.hasProperties());
            assertEquals(1, propertyCollection.size());

            CompositeProperty listEntry = (CompositeProperty) propertyCollection.iterator().next();
            assertEquals("string", listEntry.value());
            assertEquals(String.class, listEntry.type());
            assertEquals("0", listEntry.name());
            assertTrue(!listEntry.hasProperties());
        }
        {
            // TODO Set
        }
        {
            // TODO Map
        }

    }

    public void test_creatingStaticAttributeHandling() {

    }

    public void test_creating() {
        CompositePropertyTestClass testObject = new CompositePropertyTestClass();
        CompositeProperty rootCompositeProperty = createCompositeProperty(testObject, "root");
        assertEquals(testObject, rootCompositeProperty.value());
        assertEquals(testObject, rootCompositeProperty.value());
        assertEquals("root", rootCompositeProperty.name());
        assertFalse(!rootCompositeProperty.hasProperties());
        assertEquals(5, rootCompositeProperty.size());
        List<CompositeProperty> propertyList = createPropertyList(rootCompositeProperty);

        Iterator<CompositeProperty> propertyIterator = propertyList.iterator();
        {
            CompositeProperty property = propertyIterator.next();
            assertEquals("innerPropertyCollectionTestClass", property.name());
            assertTrue(!property.hasProperties()); // attribut ist NULL
        }
        {
            CompositeProperty property = propertyIterator.next();
            assertEquals("innerPropertyCollectionTestClassFilled", property.name());

            assert_InnerPropertyCollectionTestClass(property, testObject);
        }
        {
            CompositeProperty property = propertyIterator.next();
            assertEquals("myInt", property.name());
            assertTrue(!property.hasProperties());
        }

        {
            CompositeProperty property = propertyIterator.next();
            assertEquals("mySet", property.name());
            assertFalse(!property.hasProperties());
            assertEquals(testObject.mySet.size(), property.size());

            Iterator<CompositeProperty> subPropertyIterator = property.iterator();
            Found found = new Found();
            {
                CompositeProperty compositeProperty = subPropertyIterator.next();
                assertTrue(testObject.mySet.contains(compositeProperty.value()));
                assert_damnSet(compositeProperty, found, testObject);
            }

            {
                CompositeProperty compositeProperty = subPropertyIterator.next();
                assertTrue(testObject.mySet.contains(compositeProperty.value()));
                assert_damnSet(compositeProperty, found, testObject);
            }
            {
                CompositeProperty compositeProperty = subPropertyIterator.next();
                assertTrue(testObject.mySet.contains(compositeProperty.value()));
                assert_damnSet(compositeProperty, found, testObject);
            }
            assertTrue(found.float11_found);
            assertTrue(found.floatNeg11_found);
            assertTrue(found.setFound);
        }
        {
            CompositeProperty property = propertyIterator.next();
            assertEquals("myString", property.name());
            assertEquals("myStringValue", property.value());
            assertTrue(!property.hasProperties());
        }
    }

    private void assert_damnSet(CompositeProperty compositeProperty, Found found, CompositePropertyTestClass testObject) {
        if (Float.class.equals(compositeProperty.type())) {
            assertTrue(!compositeProperty.hasProperties());
            if (compositeProperty.value().equals(1.1f)) {
                found.float11_found = true;
            } else if (compositeProperty.value().equals(-1.1f)) {
                found.floatNeg11_found = true;
            } else {
                fail("wrong float found " + compositeProperty.value());
            }
        } else if (InnerCompositePropertyTestClass.class.equals(compositeProperty.type())) {
            found.setFound = true;
            assertTrue(testObject.mySet.contains(compositeProperty.value()));
            assertEquals(null, compositeProperty.name());
            assert_InnerPropertyCollectionTestClass(compositeProperty, testObject);
        } else {
            fail("wrong property found: " + compositeProperty);
        }
    }

    private class Found {
        private boolean float11_found;
        private boolean floatNeg11_found;
        private boolean setFound;
    }

    private void assert_InnerPropertyCollectionTestClass(CompositeProperty property, CompositePropertyTestClass propertyCollectionTestClass) {

        assertEquals(InnerCompositePropertyTestClass.class, property.type());
        assertFalse(!property.hasProperties());

        assertEquals(InnerCompositePropertyTestClass.class.getDeclaredFields().length, property.size());

        List<CompositeProperty> listForEasierAsserting = createPropertyList(property);

        Iterator<CompositeProperty> subPropertyIterator = listForEasierAsserting.iterator();

        {
            CompositeProperty subproperty = subPropertyIterator.next();
            assertEquals("myInt2", subproperty.name());
            assertEquals(int.class, subproperty.type());
            assertEquals(1, subproperty.value());
            assertTrue(!subproperty.hasProperties());
        }
        {
            CompositeProperty subproperty = subPropertyIterator.next();
            assertEquals("myList", subproperty.name());
            assertEquals(List.class, subproperty.type());
            assertFalse(!subproperty.hasProperties());
            assertEquals(propertyCollectionTestClass.innerPropertyCollectionTestClassFilled.myList, subproperty.value());
            Iterator<CompositeProperty> listPropertiesIterator = subproperty.iterator();
            for (Object listObject : propertyCollectionTestClass.innerPropertyCollectionTestClassFilled.myList) {
                assertEquals(listObject, listPropertiesIterator.next().value());
            }
        }

    }

    /**
     * Diese Klasse zeigt, was geht und was nicht.
     */
    public static class CompositePropertyTestClass {
        private InnerCompositePropertyTestClass innerPropertyCollectionTestClass;
        private InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();
        private int myInt;
        private Set mySet;
        private String myString = "myStringValue";

        public CompositePropertyTestClass() {
            mySet = new HashSet();
            mySet.add(new Float(1.1));
            mySet.add(new Float(-1.1));
            mySet.add(new InnerCompositePropertyTestClass());
        }

    }

    public static class InnerCompositePropertyTestClass {
        private int myInt2 = 1;
        private List myList;

        public InnerCompositePropertyTestClass() {
            myList = new ArrayList();
            myList.add(1);
        }
    }

    public void test_Setting() {

        // Primitive setzen

        {
            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            CompositeProperty toBeSet = testObjectAsCompositeProperty.find("myString");
            toBeSet.value("newStringValue");
            assertEquals("newStringValue", toBeSet.value());
            assertEquals("newStringValue", testObject.myString);
        }
        // Primitive setzen in "tieferen" Objekten

        {
            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            CompositeProperty innerPropertyCollectionTestClassFilled = testObjectAsCompositeProperty.find("innerPropertyCollectionTestClassFilled");
            CompositeProperty toBeSet = innerPropertyCollectionTestClassFilled.find("myInt2");
            assertEquals(1, toBeSet.value());
            assertEquals(1, testObject.innerPropertyCollectionTestClassFilled.myInt2);

            toBeSet.value(2);// ACTION!

            assertEquals(2, toBeSet.value());
            assertEquals(2, testObject.innerPropertyCollectionTestClassFilled.myInt2);
        }

        // Primitive setzen in Sets
        {
            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            Object oldValue = new Float(1.1);
            Object newValue = new Float(2.2);
            CompositeProperty containingSetCompositeProperty = testObjectAsCompositeProperty.find("mySet");
            Set containingSet = (Set) containingSetCompositeProperty.value();
            assertFalse(containingSet.contains(newValue));
            assertTrue(containingSet.contains(oldValue));
            CompositeProperty toBeSet = containingSetCompositeProperty.find(oldValue);
            assertNotNull(toBeSet);
            CompositeProperty hasBeenSet = containingSetCompositeProperty.find(newValue);
            assertNull(hasBeenSet);
            assertEquals(oldValue, toBeSet.value());

            toBeSet.value(newValue); // ACTION!

            assertEquals(newValue, toBeSet.value());
            hasBeenSet = containingSetCompositeProperty.find(newValue);
            assertNotNull(hasBeenSet);
            containingSet = (Set) containingSetCompositeProperty.value();
            assertTrue(containingSet.contains(newValue));
            assertFalse(containingSet.contains(oldValue));
        }

        // Complexe setzen
        {

            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            CompositeProperty toBeSet = testObjectAsCompositeProperty.find("innerPropertyCollectionTestClass"); // ist null !!!
            assertNull(toBeSet.value());
            InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();

            toBeSet.value(innerPropertyCollectionTestClassFilled);// ACTION!

            assertEquals(innerPropertyCollectionTestClassFilled, toBeSet.value());
            CompositeProperty toBeSetMyInt = toBeSet.find("myInt2");
            assertEquals(1, toBeSetMyInt.value());
            Object newInteger = new Integer(2);

            toBeSetMyInt.value(newInteger);// ACTION!

            assertEquals(newInteger, toBeSetMyInt.value());
            assertEquals(newInteger, innerPropertyCollectionTestClassFilled.myInt2);

        }

        // Complexe adden zu Set und darin Attribute setzen

        {

            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            CompositeProperty setProperty = testObjectAsCompositeProperty.find("mySet");
            Set set = (Set) setProperty.value();
            assertNotNull(setProperty);
            InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();
            CompositeProperty innerPropertyCollectionTestClassFilledAsProperty = createCompositeProperty(innerPropertyCollectionTestClassFilled, null, setProperty);
            setProperty.add(innerPropertyCollectionTestClassFilledAsProperty);
            assertEquals(4, set.size());
            assertTrue(set.contains(innerPropertyCollectionTestClassFilled));
            assertEquals(4, setProperty.size());

            CompositeProperty toBeSetMyInt = innerPropertyCollectionTestClassFilledAsProperty.find("myInt2");
            assertEquals(1, toBeSetMyInt.value());
            assertEquals(1, innerPropertyCollectionTestClassFilled.myInt2);
            Object newInteger = new Integer(2);

            toBeSetMyInt.value(newInteger);// ACTION!

            assertEquals(newInteger, toBeSetMyInt.value());
            assertEquals(newInteger, innerPropertyCollectionTestClassFilled.myInt2);
            assertTrue(set.contains(innerPropertyCollectionTestClassFilled));
        }

    }

    public void test_Adding() {
        logger.error("not implemented");

    }

    public void test_Removing() {

    }

    public void test_Replacing() {

        CompositePropertyTestClass testObject = new CompositePropertyTestClass();
        CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

        {
            InnerCompositePropertyTestClass newInnerPropertyCollectionTestClass = new InnerCompositePropertyTestClass();
            newInnerPropertyCollectionTestClass.myInt2 = 2;
            assertNull(testObjectAsCompositeProperty.find(newInnerPropertyCollectionTestClass));
            CompositeProperty replacement = createCompositeProperty(newInnerPropertyCollectionTestClass, "innerPropertyCollectionTestClass");
            CompositeProperty toBeReplaced = testObjectAsCompositeProperty.find("innerPropertyCollectionTestClass");
            assertNull(testObject.innerPropertyCollectionTestClass);
            testObjectAsCompositeProperty.replace(toBeReplaced, replacement);
            CompositeProperty replacementFound = testObjectAsCompositeProperty.find(newInnerPropertyCollectionTestClass);
            assertEquals(replacement.find("myInt2").value(), replacementFound.find("myInt2").value());
            // assertEquals(replacement, replacementFound); KEINE Anforderung!
            assertNotNull(testObject.innerPropertyCollectionTestClass);
            assertEquals(2, testObject.innerPropertyCollectionTestClass.myInt2);
        }
        {
            CompositeProperty setProperty = testObjectAsCompositeProperty.find("mySet");
            assertNotNull(setProperty);
            CompositeProperty toBeReplaced = setProperty.find(new Float(-1.1));
            assertNotNull(toBeReplaced);
            Object newElement = new Float(2.2);
            setProperty.replace(toBeReplaced, createCompositeProperty(newElement, null));
            assertNull(setProperty.find(new Float(-1.1)));
            CompositeProperty replacement = setProperty.find(newElement);
            assertNotNull(replacement);
            Set setPropertyFound = (Set) setProperty.value();
            assertTrue(setPropertyFound.contains(newElement));
        }
    }



    public List<CompositeProperty> createPropertyList(Collection<CompositeProperty> propertySet) {
        return pcf.createPropertyList(propertySet);
    }

    protected InternalPropertyTool pcf = new DefaultPropertyTool(false);
    private final Logger logger = Logger.getLogger(AbstractTest_CompositeProperty.class);
}
