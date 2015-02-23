package org.jodt.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jodt.property.implementation.DefaultPropertyTool;

/**
 * @author Oliver Stuch
 */
abstract public class AbstractTest_CompositeProperty extends TestCase implements InternalPropertyTool {

    public void test_Path() {
        CompositePropertyTestClass testObject = new CompositePropertyTestClass();
        CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "test_Path");
        String pathTestObject = testObjectAsCompositeProperty.path();
        Assert.assertNotNull(pathTestObject);
        Collection<CompositeProperty> myStringAsCPs = testObjectAsCompositeProperty.findByName("myString");
        Assert.assertEquals(1, myStringAsCPs.size());
        for (CompositeProperty myStringAsCP : myStringAsCPs) {
            String myStringPath = myStringAsCP.path();
            Assert.assertNotNull(myStringPath);
            Assert.assertEquals("test_Path.myString", myStringPath);
        }
        Collection<CompositeProperty> innerPropertyCollectionTestClassAsCPs = testObjectAsCompositeProperty.findByName("innerPropertyCollectionTestClass");
        Assert.assertEquals(1, innerPropertyCollectionTestClassAsCPs.size());
        for (CompositeProperty innerPropertyCollectionTestClassAsCP : innerPropertyCollectionTestClassAsCPs) {
            Assert.assertNotNull(innerPropertyCollectionTestClassAsCP);
            Assert.assertNull(innerPropertyCollectionTestClassAsCP.value());
        }
        Collection<CompositeProperty> innerPropertyCollectionTestClassFilledAsCPs = testObjectAsCompositeProperty.findByName("innerPropertyCollectionTestClassFilled");
        Assert.assertEquals(1, innerPropertyCollectionTestClassFilledAsCPs.size());
        for (CompositeProperty innerPropertyCollectionTestClassFilledAsCP : innerPropertyCollectionTestClassFilledAsCPs) {
            Assert.assertNotNull(innerPropertyCollectionTestClassFilledAsCP);
        }
        {
            Collection<CompositeProperty> myInt2AsCPs = testObjectAsCompositeProperty.findByName("myInt2");
            Assert.assertEquals(2, myInt2AsCPs.size());
            for (CompositeProperty myInt2AsCP : myInt2AsCPs) {
                Assert.assertEquals(1, myInt2AsCP.value());
                System.out.println(myInt2AsCP.path());
            }
        }
        testObject.addInnerCompositePropertyTestClass();
        testObjectAsCompositeProperty = createCompositeProperty(testObject, "test_Path");
        {
            Collection<CompositeProperty> myInt2AsCPs = testObjectAsCompositeProperty.findByName("myInt2");
            Assert.assertEquals(3, myInt2AsCPs.size());
            for (CompositeProperty myInt2AsCP : myInt2AsCPs) {
                Assert.assertEquals(1, myInt2AsCP.value());
                System.out.println(myInt2AsCP.path());
            }
        }
    }

    public void test_PropertyActorClass() {
        CompositePropertyTestClass testObject = new CompositePropertyTestClass();
        pcf.configure().registerGlobalPropertyActor(String.class, new PropertyActor() {
            public boolean actOn(Property property) {
                property.value("test_PropertyActor");
                return true;
            }
        });
        CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "test_PropertyActor");
        Collection<CompositeProperty> myStringAsCPs = testObjectAsCompositeProperty.findByName("myString");
        Assert.assertEquals(1, myStringAsCPs.size());
        for (CompositeProperty myStringAsCP : myStringAsCPs) {
            assertNotNull(myStringAsCP);
            assertEquals("test_PropertyActor", myStringAsCP.value());
        }
    }

    public void test_PropertyActorName() {
        CompositePropertyTestClass testObject = new CompositePropertyTestClass();
        pcf.configure().registerGlobalPropertyActor("myString", new PropertyActor() {
            public boolean actOn(Property property) {
                property.value("test_PropertyActor");
                return true;
            }
        });
        CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "test_PropertyActor");
        Collection<CompositeProperty> myStringAsCPs = testObjectAsCompositeProperty.findByName("myString");
        Assert.assertEquals(1, myStringAsCPs.size());
        for (CompositeProperty myStringAsCP : myStringAsCPs) {
            assertNotNull(myStringAsCP);
            assertEquals("test_PropertyActor", myStringAsCP.value());
        }
    }

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

    public void test_Setting() {

        // Primitive setzen
        {
            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            Collection<CompositeProperty> toBeSets = testObjectAsCompositeProperty.findByName("myString");
            Assert.assertEquals(1, toBeSets.size());
            for (CompositeProperty toBeSet : toBeSets) {
                toBeSet.value("newStringValue");
                assertEquals("newStringValue", toBeSet.value());
                assertEquals("newStringValue", testObject.myString);
            }
        }
        // Primitive setzen in "tieferen" Objekten

        {
            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            Collection<CompositeProperty> innerPropertyCollectionTestClassFilleds = testObjectAsCompositeProperty.findByName("innerPropertyCollectionTestClassFilled");
            Assert.assertEquals(1, innerPropertyCollectionTestClassFilleds.size());
            CompositeProperty innerPropertyCollectionTestClassFilled = innerPropertyCollectionTestClassFilleds.iterator().next();
            Collection<CompositeProperty> toBeSets = innerPropertyCollectionTestClassFilled.findByName("myInt2");
            Assert.assertEquals(1, toBeSets.size());
            CompositeProperty toBeSet = toBeSets.iterator().next();

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
            Collection<CompositeProperty> containingSetCompositePropertys = testObjectAsCompositeProperty.findByName("mySet");
            Assert.assertEquals(1, containingSetCompositePropertys.size());
            CompositeProperty containingSetCompositeProperty = containingSetCompositePropertys.iterator().next();

            Set containingSet = (Set) containingSetCompositeProperty.value();
            assertFalse(containingSet.contains(newValue));
            assertTrue(containingSet.contains(oldValue));
            Collection<CompositeProperty> toBeSets = containingSetCompositeProperty.findByValue(oldValue);
            Assert.assertEquals(1, toBeSets.size());
            CompositeProperty toBeSet = toBeSets.iterator().next();
            assertNotNull(toBeSet);
            Collection<CompositeProperty> hasBeenSets = containingSetCompositeProperty.findByValue(newValue);
            Assert.assertEquals(0, hasBeenSets.size());
//            assertNull(hasBeenSet);
            assertEquals(oldValue, toBeSet.value());

            toBeSet.value(newValue); // ACTION!

            assertEquals(newValue, toBeSet.value());
            hasBeenSets = containingSetCompositeProperty.findByValue(newValue);
            Assert.assertEquals(1, hasBeenSets.size());
            CompositeProperty hasBeenSet = hasBeenSets.iterator().next();
            assertNotNull(hasBeenSet);
            containingSet = (Set) containingSetCompositeProperty.value();
            assertTrue(containingSet.contains(newValue));
            assertFalse(containingSet.contains(oldValue));
        }

        // Complexe setzen
        {

            CompositePropertyTestClass testObject = new CompositePropertyTestClass();
            CompositeProperty testObjectAsCompositeProperty = createCompositeProperty(testObject, "root");

            Collection<CompositeProperty> toBeSets = testObjectAsCompositeProperty.findByName("innerPropertyCollectionTestClass"); // ist null !!!
            Assert.assertEquals(1, toBeSets.size());
            CompositeProperty toBeSet = toBeSets.iterator().next();
            assertNull(toBeSet.value());
            InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();

            toBeSet.value(innerPropertyCollectionTestClassFilled);// ACTION!

            assertEquals(innerPropertyCollectionTestClassFilled, toBeSet.value());
            Collection<CompositeProperty> toBeSetMyInts = toBeSet.findByName("myInt2");
            Assert.assertEquals(1, toBeSetMyInts.size());
            CompositeProperty toBeSetMyInt = toBeSetMyInts.iterator().next();
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

            Collection<CompositeProperty> setPropertys = testObjectAsCompositeProperty.findByName("mySet");
            Assert.assertEquals(1, setPropertys.size());
            CompositeProperty setProperty = setPropertys.iterator().next();
            Set set = (Set) setProperty.value();
            assertNotNull(setProperty);
            InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();
            CompositeProperty innerPropertyCollectionTestClassFilledAsProperty = createCompositeProperty(innerPropertyCollectionTestClassFilled, null, setProperty);
            setProperty.add(innerPropertyCollectionTestClassFilledAsProperty);
            assertEquals(4, set.size());
            assertTrue(set.contains(innerPropertyCollectionTestClassFilled));
            assertEquals(4, setProperty.size());

            Collection<CompositeProperty> toBeSetMyInts = innerPropertyCollectionTestClassFilledAsProperty.findByName("myInt2");
            Assert.assertEquals(1, toBeSetMyInts.size());
            CompositeProperty toBeSetMyInt = toBeSetMyInts.iterator().next();
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
            assertTrue(testObjectAsCompositeProperty.findByValue(newInnerPropertyCollectionTestClass).isEmpty());
            CompositeProperty replacement = createCompositeProperty(newInnerPropertyCollectionTestClass, "innerPropertyCollectionTestClass");
            Collection<CompositeProperty> toBeReplaceds = testObjectAsCompositeProperty.findByName("innerPropertyCollectionTestClass");
            Assert.assertEquals(1, toBeReplaceds.size());
            CompositeProperty toBeReplaced = toBeReplaceds.iterator().next();

            assertNull(testObject.innerPropertyCollectionTestClass);
            testObjectAsCompositeProperty.replace(toBeReplaced, replacement);
            Collection<CompositeProperty> replacementFounds = testObjectAsCompositeProperty.findByValue(newInnerPropertyCollectionTestClass);
            Assert.assertEquals(1, replacementFounds.size());
            CompositeProperty replacementFound = replacementFounds.iterator().next();
            CompositeProperty replacementMyInt2 = (CompositeProperty) replacement.findByName("myInt2").iterator().next();
            CompositeProperty replacementFoundMyInt2 = (CompositeProperty) replacementFound.findByName("myInt2").iterator().next();
            assertEquals(replacementMyInt2.value(), replacementFoundMyInt2.value());
            // assertEquals(replacement, replacementFound); KEINE Anforderung!
            assertNotNull(testObject.innerPropertyCollectionTestClass);
            assertEquals(2, testObject.innerPropertyCollectionTestClass.myInt2);
        }
        {
            Collection<CompositeProperty> setPropertys = testObjectAsCompositeProperty.findByName("mySet");
            Assert.assertEquals(1, setPropertys.size());
            CompositeProperty setProperty = setPropertys.iterator().next();
            assertNotNull(setProperty);
            Collection<CompositeProperty> toBeReplaceds = setProperty.findByValue(new Float(-1.1));
            CompositeProperty toBeReplaced = toBeReplaceds.iterator().next();
            assertNotNull(toBeReplaced);
            Object newElement = new Float(2.2);
            setProperty.replace(toBeReplaced, createCompositeProperty(newElement, null));
            assertTrue(setProperty.findByValue(new Float(-1.1)).isEmpty());
            Collection<CompositeProperty> replacements = setProperty.findByValue(newElement);
            CompositeProperty replacement = replacements.iterator().next();
            assertNotNull(replacement);
            Set setPropertyFound = (Set) setProperty.value();
            assertTrue(setPropertyFound.contains(newElement));
        }
    }

    public List<CompositeProperty> createPropertyList(Collection<CompositeProperty> propertySet) {
        return pcf.createPropertyList(propertySet);
    }

    //
    // Diese Klasse zeigt, was geht und was nicht.
    //
    public static class CompositePropertyTestClass {

        private int myInt;
        private Set mySet;
        private String myString = "myStringValue";
        private InnerCompositePropertyTestClass innerPropertyCollectionTestClass;
        private InnerCompositePropertyTestClass innerPropertyCollectionTestClassFilled = new InnerCompositePropertyTestClass();

        public CompositePropertyTestClass() {
            mySet = new HashSet();
            mySet.add(new Float(1.1));
            mySet.add(new Float(-1.1));
            mySet.add(new InnerCompositePropertyTestClass());
        }

        public void addInnerCompositePropertyTestClass() {
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

    protected InternalPropertyTool pcf = new DefaultPropertyTool(false);
    private final Logger logger = Logger.getLogger(AbstractTest_CompositeProperty.class);
}
