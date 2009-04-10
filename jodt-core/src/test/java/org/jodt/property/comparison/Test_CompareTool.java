package org.jodt.property.comparison;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jodt.property.PropertyToolConfiguration;
import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.implementation.Additional;
import org.jodt.property.comparison.implementation.CompareStrategy;
import org.jodt.property.comparison.implementation.DiffStrategy;
import org.jodt.property.comparison.implementation.Missing;
import org.jodt.property.comparison.implementation.NoDiff;
import org.jodt.property.comparison.implementation.ReferenceDiff;
import org.jodt.property.comparison.implementation.SubDiff;
import org.jodt.property.comparison.implementation.ValueDiff;
import org.jodt.property.implementation.HashCodeIdentityResolver;
import org.jodt.property.implementation.PackageNonTerminalStrategy;

import junit.framework.TestCase;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public abstract class Test_CompareTool extends TestCase {
    public abstract CompareTool createCompareTool(String packageFilter);

    public Test_CompareTool(String text) {
        super(text);
    }

    private interface Asserts {
        CompositeComparison<?> assert_NoDiffNoPropertyDiff(Object o1, Object o2, int numToplevelChildren);

        void assert_DiffNotDeterminable(Object o1, Object o2);

        void assert_Additional_Missing(Object o);

        void assert_ValueDiff(Object o1, Object o2);

        void assert_ReferenceDiff(Object o1, Object o2);

        CompareToolConfiguration configureCompareTool();
        
        void assert_HasOneValueDiff(Object o1, Object o2, Object value1, Object value2);
    }

    private class AbstractAsserts {

        private ComparisonStrategy comparisonStrategy;

        public AbstractAsserts(ComparisonStrategy comparisonStrategy) {
            this.comparisonStrategy = comparisonStrategy;
        }

        public CompareToolConfiguration configureCompareTool() {
            return comparisonStrategy.configure();
        }

        public void assert_HasOneValueDiff(Object o1, Object o2, Object value1, Object value2) {
            CompositeComparison<ObjectWithNonTerminalReferences> comparison = comparisonStrategy.createComparison(o1, "nonTerminalObject1", o2, "nonTerminalObject2");
            assertNotNull(comparison);
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(comparison.hasDiffsOnPropertyLevel());
            assertEquals(1, comparison.childDiffCount());
            List<CompositeComparison<?>> childCC = comparison.recursiveChildrenWithDiffsOnObjectLevel(comparison);
            assertEquals(1, childCC.size());
            assertEquals(ValueDiff.class, childCC.get(0).diff().getClass());
            ValueDiff valueDiff = (ValueDiff) childCC.get(0).diff();
            assertEquals(value1, valueDiff.compareObject());
            assertEquals(value2, valueDiff.referenceObject());
        }

        public void assert_DiffNotDeterminable(Object o1, Object o2) {
            CompositeComparison<?> comparison = comparisonStrategy.createComparison(o1, "nonTerminalObject1", o2, "nonTerminalObject2");
            assertNull(comparison.diff()); // vielleicht ein bischen bitter, aber an dieser Stelle kann man leider keinen Diff bestimmen.
        }

        public void assert_ReferenceDiff(Object compareObject, Object referenceObject) {
            {
                CompositeComparison<?> comparison = comparisonStrategy.createComparison(compareObject, " ", referenceObject, " ");
                assertNotNull(comparison);
                assertEquals(ReferenceDiff.class, comparison.diff().getClass());
                ReferenceDiff diff = (ReferenceDiff) comparison.diff();
                assertEquals(diff.compareObject(), compareObject);
                assertEquals(diff.referenceObject(), referenceObject);
                assertFalse(comparison.hasChildren());
            }
        }

        public void assert_ValueDiff(Object o1, Object o2) {
            CompositeComparison<?> comparison = comparisonStrategy.createComparison(o1, " ", o2, " ");
            assertNotNull(comparison);
            assertTrue(comparison.hasDiffsOnObjectLevel());
            assertTrue(!comparison.hasDiffsOnPropertyLevel());
            assertEquals(ValueDiff.class, comparison.diff().getClass());
            ValueDiff diff = (ValueDiff) comparison.diff();
            assertEquals(diff.compareObject(), o1);
            assertEquals(diff.referenceObject(), o2);
            assertFalse(comparison.hasChildren());
        }

        protected CompositeComparison<?> assert_NoDiff(Object compareObject, Object referenceObject) {
            {
                CompositeComparison<?> comparison = comparisonStrategy.createComparison(compareObject, " ", referenceObject, " ");
                assertNotNull(comparison);
                assertTrue(!comparison.hasDiffsOnObjectLevel());
                assertTrue(!comparison.hasDiffsOnPropertyLevel());
                assertEquals(NoDiff.class, comparison.diff().getClass());
                NoDiff diff = (NoDiff) comparison.diff();
                assertEquals(compareObject, diff.compareObject());
                assertEquals(referenceObject, diff.referenceObject());
                return comparison;
            }
        }

        public CompositeComparison<?> assert_NoDiffNoPropertyDiff(Object compareObject, Object referenceObject, int numToplevelChildren) {
            CompositeComparison<?> comparison = assert_NoDiff(compareObject, referenceObject);
            assertEquals(0, comparison.childDiffCount());
            assertEquals(numToplevelChildren, comparison.childCount());
            return comparison;
        }

        public void assert_Additional_Missing(Object object) {
            {
                CompositeComparison<?> comparison = comparisonStrategy.createComparison(object, " ", null, "null value");
                assertNotNull(comparison);
                assertEquals(0, comparison.size()); // no children
                assertEquals(Additional.class, comparison.diff().getClass());
                Additional diff = (Additional) comparison.diff();
                assertEquals(diff.additionalObject(), object);
                assertFalse(comparison.hasChildren());
            }

            {
                CompositeComparison<?> comparison = comparisonStrategy.createComparison(null, "null value", object, " ");
                assertNotNull(comparison);
                assertEquals(0, comparison.size()); // no children!
                assertTrue(comparison.diff() instanceof Missing);
                Missing diff = (Missing) comparison.diff();
                assertEquals(diff.missingObject(), object);
                assertFalse(comparison.hasChildren());
            }
        }
    }

    private class CompareModeAsserts extends AbstractAsserts implements Asserts {
        public CompareModeAsserts(CompareTool compareTool) {
            super(new CompareStrategy(compareTool));
        }
    }

    private class DiffModeAsserts extends AbstractAsserts implements Asserts {
        public DiffModeAsserts(CompareTool compareTool) {
            super(new DiffStrategy(compareTool));
        }
    }

    /**
     * Teste das Verhalten beim Vergleich von 2 null: Erwarte NoDiff
     */
  @Test
    public void test_objectLevelCompareNulls() {
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        // compare mode
        test_objectLevelCompareNulls(new CompareModeAsserts(compareTool), 0);
        test_objectLevelCompareNulls(new DiffModeAsserts(compareTool), 0);
    }

    private void test_objectLevelCompareNulls(Asserts asserts, int numToplevelChildren) {
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        asserts.assert_NoDiffNoPropertyDiff(null, null, numToplevelChildren);
    }

    /**
     * Teste das Verhalten beim Vergleich EINER null mit einem Object.: Erwarte Additional bzw. Missing
     */
  @Test
    public void test_objectLevelCompareNullWithObject() {
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        test_objectLevelCompareNullWithObject(new CompareModeAsserts(compareTool));
        test_objectLevelCompareNullWithObject(new DiffModeAsserts(compareTool));
    }

    /**
     * Teste das Verhalten beim Vergleich EINER null mit einem Object.: Erwarte Additional bzw. Missing
     */
    private void test_objectLevelCompareNullWithObject(Asserts asserts) {
        Object terminalObject = new String();
        asserts.assert_Additional_Missing(terminalObject);
        Object nonTerminalObject = new ObjectWithPrimitives(); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
        asserts.assert_Additional_Missing(nonTerminalObject);
    }

  @Test
    public void test_objectLevelCompareNonNullObjects() {
        test_objectLevelCompareNonNullObjects(new CompareModeAsserts(createCompareTool(NON_TERMINAL_PACKAGE)));
        test_objectLevelCompareNonNullObjects(new DiffModeAsserts(createCompareTool(NON_TERMINAL_PACKAGE)));
    }

    /**
     * Teste das Verhalten beim Vergleich zweier nicht-Null Objekte: Erwarte ReferenceDiff bzw. ValueDiff
     */
    private void test_objectLevelCompareNonNullObjects(Asserts asserts) {

        {
            Object terminalObject1 = new String();
            Object terminalObject2 = new String();
            asserts.assert_NoDiffNoPropertyDiff(terminalObject1, terminalObject2, 0); // Vergleich von Primitiven => auch im CompareMode keine children
            terminalObject1 = "Hoho, draußen vom Walde komme ich her!";
            asserts.assert_ValueDiff(terminalObject1, terminalObject2);
        }

        {
            Object nonTerminalObject1 = new ObjectWithPrimitives(); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
            Object nonTerminalObject2 = new ObjectWithPrimitives(); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
            asserts.assert_DiffNotDeterminable(nonTerminalObject1, nonTerminalObject2);// vielleicht ein bischen bitter, aber an dieser Stelle kann man leider keinen Diff bestimmen.
            // erst wenn ein IdResolver registriert ist, können diffs bestimmt werden:
            asserts.configureCompareTool().register(ObjectWithPrimitives.class, new HashCodeIdentityResolver());
            asserts.assert_ReferenceDiff(nonTerminalObject1, nonTerminalObject2);
            // Wenn nun nonTerminalObject1 geändert wird, muss nach wie vor der ReferenceDiff kommen
            // dies gilt solange analysePropertiesOfDifferentNonTerminalObjects == false
            ((ObjectWithPrimitives) nonTerminalObject1).primitiveInt++;
            asserts.assert_ReferenceDiff(nonTerminalObject1, nonTerminalObject2); // Wenn ein ReferenceDiff vorliegt, gibt es auch im CompareMode keine children
            // es sei denn, man schaltet das ein
            // asserts.configureCompareTool().registerAnalysePropertiesOfDifferentNonTerminalObjects(true);
        }

        { // ein ähnlicher Test, nur dass die ID über einen IDResolver festgestellt wird
            Object nonTerminalObject1 = new ObjectWithPrimitivesWithId(1); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
            Object nonTerminalObject2 = new ObjectWithPrimitivesWithId(1); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
            asserts.configureCompareTool().register(ObjectWithPrimitivesWithId.class, new ObjectWithPrimitivesWithId.IDResolver());
            int numToplevelChildren = 5;
            if (asserts instanceof DiffModeAsserts) {
                numToplevelChildren = 0; // im DiffMode (beim Aufruf der diff-Operation) soll es nur für Unterschiede children geben
            }
            CompositeComparison<?> comparison = asserts.assert_NoDiffNoPropertyDiff(nonTerminalObject1, nonTerminalObject2, numToplevelChildren);
            nonTerminalObject2 = new ObjectWithPrimitivesWithId(2);
            asserts.assert_ReferenceDiff(nonTerminalObject1, nonTerminalObject2);
        }
    }

    // compareMode
  @Test
    public void test_propertyLevelCompareSetsOfNonTerminalObjects() {
        Set set1OfNonTerminalObjects = new HashSet();
        Set set2OfNonTerminalObjects = new HashSet();
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        ObjectWithPrimitivesWithId nonTerminalObject1 = new ObjectWithPrimitivesWithId(1);
        ObjectWithPrimitivesWithId nonTerminalObject2 = new ObjectWithPrimitivesWithId(1);
        set1OfNonTerminalObjects.add(nonTerminalObject1);
        set2OfNonTerminalObjects.add(nonTerminalObject2);
        {
            CompositeComparison<?> comparison = compareTool.compare(set1OfNonTerminalObjects, " ", set2OfNonTerminalObjects, " ");
            assert_NullDiffNoPropertyDiff(comparison, set1OfNonTerminalObjects, set2OfNonTerminalObjects);
            assertEquals(1, comparison.childCount());
        }
        nonTerminalObject2.id = new Long(2);
        {
            CompositeComparison comparison = compareTool.compare(set1OfNonTerminalObjects, "set1", set2OfNonTerminalObjects, "set2");
            assertNotNull(comparison);
            assertEquals(1, comparison.childCount());
            assertEquals(1, comparison.childDiffCount());
            assertEquals(SubDiff.class, comparison.diff().getClass());
            CompositeComparison<?> innerComparison = (CompositeComparison) comparison.get(0);
            assertNotNull(innerComparison);
            assertEquals(5, innerComparison.childCount());
            assertEquals(1, innerComparison.childDiffCount());
            for (CompositeComparison<?> compositeComparison : innerComparison) {

                if (compositeComparison.childDiffCount() != 0) {
                    ValueDiff diff = (ValueDiff) compositeComparison.diff();
                    assertEquals(1, diff.compareObject());
                    assertEquals(2, diff.referenceObject());
                }

            }

        }
        compareTool.configure().register(ObjectWithPrimitivesWithId.class, new ObjectWithPrimitivesWithId.IDResolver());
        {
            CompositeComparison comparison = compareTool.compare(set1OfNonTerminalObjects, "set1", set2OfNonTerminalObjects, "set2");
            assertNotNull(comparison);
            assertEquals(1, comparison.childCount());
            assertEquals(1, comparison.childDiffCount());
            CompositeComparison<?> innerComparison = (CompositeComparison) comparison.get(0);
            assertNotNull(innerComparison);
            ReferenceDiff diff = (ReferenceDiff) innerComparison.diff();
            assertEquals(nonTerminalObject1, diff.compareObject());
            assertEquals(nonTerminalObject2, diff.referenceObject());
        }
    }

    // compareMode
  @Test
    public void test_propertyLevelCompareSetsOfTerminalObjects() {
        Set set1OfTerminalObjects = new HashSet();
        Set set2OfTerminalObjects = new HashSet();
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        {
            CompositeComparison<?> comparison = compareTool.compare(set1OfTerminalObjects, " ", set2OfTerminalObjects, " ");
            assert_NullDiffNoPropertyDiff(comparison, set1OfTerminalObjects, set2OfTerminalObjects);
            assertEquals(0, comparison.childCount());
        }
        set1OfTerminalObjects.add(new Integer(1));
        set2OfTerminalObjects.add(new Integer(1));
        {
            CompositeComparison<?> comparison = compareTool.compare(set1OfTerminalObjects, " ", set2OfTerminalObjects, " ");
            assert_NullDiffNoPropertyDiff(comparison, set1OfTerminalObjects, set2OfTerminalObjects);
            assertEquals(1, comparison.childCount());
        }

        set1OfTerminalObjects.add(new Integer(2));
        set2OfTerminalObjects.add(new Integer(2));
        {
            CompositeComparison<?> comparison = compareTool.compare(set1OfTerminalObjects, " ", set2OfTerminalObjects, " ");
            assert_NullDiffNoPropertyDiff(comparison, set1OfTerminalObjects, set2OfTerminalObjects);
            assertEquals(2, comparison.childCount());
        }

        set1OfTerminalObjects.add(new Integer(3));
        {
            CompositeComparison comparison = compareTool.compare(set1OfTerminalObjects, " ", set2OfTerminalObjects, " ");
            assertNotNull(comparison);
            assertEquals(3, comparison.childCount());
            assertEquals(1, comparison.childDiffCount());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            assertEquals(1, childCC.size());
            Additional additional = (Additional) childCC.get(0).diff();
        }
    }

    // compareMode
  @Test
    public void test_propertyLevelCompareListsOfTerminalObjects() {
        List list1OfTerminalObjects = new ArrayList();
        List list2OfTerminalObjects = new ArrayList();
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        {
            CompositeComparison<?> comparison = compareTool.compare(list1OfTerminalObjects, " ", list2OfTerminalObjects, " ");
            assert_NullDiffNoPropertyDiff(comparison, list1OfTerminalObjects, list2OfTerminalObjects);
            assertEquals(0, comparison.childCount());
        }
        list1OfTerminalObjects.add(new Integer(1));
        list2OfTerminalObjects.add(new Integer(1));
        {
            CompositeComparison comparison = compareTool.compare(list1OfTerminalObjects, "", list2OfTerminalObjects, "");
            assert_NullDiffNoPropertyDiff(comparison, list1OfTerminalObjects, list2OfTerminalObjects);
            assertEquals(1, comparison.childCount());
        }

        list1OfTerminalObjects.add(new Integer(2));
        list2OfTerminalObjects.add(new Integer(2));
        {
            CompositeComparison comparison = compareTool.compare(list1OfTerminalObjects, "", list2OfTerminalObjects, "");
            assert_NullDiffNoPropertyDiff(comparison, list1OfTerminalObjects, list2OfTerminalObjects);
            assertEquals(2, comparison.childCount()); // compareMode => 2 children, obwohl keine Diffs vorliegen
        }
        {
            CompositeComparison comparison = compareTool.diff(list1OfTerminalObjects, "", list2OfTerminalObjects, "");
            assert_NullDiffNoPropertyDiff(comparison, list1OfTerminalObjects, list2OfTerminalObjects);
            assertEquals(0, comparison.childCount()); // diffMode => 0 children, da keine Diffs vorliegen
        }
        list1OfTerminalObjects.add(new Integer(3));
        {
            CompositeComparison comparison = compareTool.compare(list1OfTerminalObjects, " ", list2OfTerminalObjects, " ");
            assertNotNull(comparison);
            assertEquals(3, comparison.childCount());
            assertEquals(1, comparison.childDiffCount());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            assertEquals(1, childCC.size());
            Additional additional = (Additional) childCC.get(0).diff();
        }

        {
            CompositeComparison comparison = compareTool.diff(list1OfTerminalObjects, " ", list2OfTerminalObjects, " ");
            assertNotNull(comparison);
            assertEquals(1, comparison.childCount());
            assertEquals(1, comparison.childDiffCount());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            assertEquals(1, childCC.size());
            Additional additional = (Additional) childCC.get(0).diff();
        }

    }

  @Test
    public void test_objectLevelCompareTerminalWithNonterminal() {
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        Object terminalObject = new String();
        Object nonTerminalObject = new ObjectWithPrimitives(); // non terminal durch PackageNonTerminalStrategy("net.stuch.*")
        assert_IllegalArgumentException(compareTool, terminalObject, nonTerminalObject);
        assert_IllegalArgumentException(compareTool, nonTerminalObject, terminalObject);
    }

    private void assert_IllegalArgumentException(CompareTool compareTool, Object compareObject, Object referenceObject) {
        try {
            compareTool.compare(compareObject, " ", referenceObject, " ");
            assertTrue("Vergleich zwischen Terminal und nonTerminal nicht erlaubt", false);
        } catch (IllegalArgumentException e) {
            assertTrue(true); // Exception muss fliegen, da ein Vergleich zwischen Terminal und nonTerminal nicht erlaubt ist.
        }
    }

    // propertyuntersuchung trotz diff

    private void assert_Additional_Missing(CompareTool compareTool, Object object) {
        {
            CompositeComparison<?> comparison = compareTool.compare(object, " ", null, "null value");
            assertNotNull(comparison);
            assertEquals(0, comparison.size()); // no children
            assertEquals(Additional.class, comparison.diff().getClass());
            Additional diff = (Additional) comparison.diff();
            assertEquals(diff.additionalObject(), object);
            assertFalse(comparison.hasChildren());
        }

        {
            CompositeComparison<?> comparison = compareTool.compare(null, "null value", object, " ");
            assertNotNull(comparison);
            assertEquals(0, comparison.size()); // no children!
            assertTrue(comparison.diff() instanceof Missing);
            Missing diff = (Missing) comparison.diff();
            assertEquals(diff.missingObject(), object);
            assertFalse(comparison.hasChildren());
        }
    }

    private CompositeComparison<?> assert_NoDiffNoPropertyDiff(CompareTool compareTool, Object compareObject, Object referenceObject) {
        {
            CompositeComparison<?> comparison = assert_NoDiff(compareTool, compareObject, referenceObject);
            assertEquals(0, comparison.childDiffCount());
            return comparison;
        }
    }

    private void assert_NullDiffNoPropertyDiff(CompositeComparison<?> comparison, Object compareObject, Object referenceObject) {
        {
            assertNotNull(comparison);
            assertEquals(null, comparison.diff());
            assertEquals(0, comparison.childDiffCount());
        }
    }

    private CompositeComparison<?> assert_NoDiff(CompareTool compareTool, Object compareObject, Object referenceObject) {
        {
            CompositeComparison<?> comparison = compareTool.compare(compareObject, " ", referenceObject, " ");
            assertNotNull(comparison);
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(!comparison.hasDiffsOnPropertyLevel());
            assertEquals(NoDiff.class, comparison.diff().getClass());
            NoDiff diff = (NoDiff) comparison.diff();
            assertEquals(compareObject, diff.compareObject());
            assertEquals(referenceObject, diff.referenceObject());
            return comparison;
        }
    }

    private void assert_ValueDiff(CompareTool compareTool, Object compareObject, Object referenceObject) {
        {
            CompositeComparison<?> comparison = compareTool.compare(compareObject, " ", referenceObject, " ");
            assertNotNull(comparison);
            assertTrue(comparison.hasDiffsOnObjectLevel());
            assertTrue(!comparison.hasDiffsOnPropertyLevel());
            assertEquals(ValueDiff.class, comparison.diff().getClass());
            ValueDiff diff = (ValueDiff) comparison.diff();
            assertEquals(diff.compareObject(), compareObject);
            assertEquals(diff.referenceObject(), referenceObject);
            assertFalse(comparison.hasChildren());
        }
    }

    private void assert_ReferenceDiff(CompareTool compareTool, Object compareObject, Object referenceObject) {
        {
            CompositeComparison<?> comparison = compareTool.compare(compareObject, " ", referenceObject, " ");
            assertNotNull(comparison);
            assertEquals(ReferenceDiff.class, comparison.diff().getClass());
            ReferenceDiff diff = (ReferenceDiff) comparison.diff();
            assertEquals(diff.compareObject(), compareObject);
            assertEquals(diff.referenceObject(), referenceObject);
            assertFalse(comparison.hasChildren());
        }
    }

    private void assert_objectLevelNullDiff(CompareTool compareTool, Object o1, Object o2) {
        CompositeComparison<?> comparison = compareTool.compare(o1, "o1", o2, "o2");
        assertTrue(!comparison.hasDiffsOnObjectLevel());
        assertTrue(!comparison.hasDiffsOnPropertyLevel());
        assertEquals(null, comparison.diff()); // In diesem Fall kann kein Diff auf der Ebene festgestellt werden (auch nicht NoDiff)
    }

  @Test
    public void test_propertyLevelCompareObjectWithPrimitives() {
        ObjectWithPrimitives o1 = new ObjectWithPrimitivesWithId(1);
        ObjectWithPrimitives o2 = new ObjectWithPrimitivesWithId(1);

        CompareTool compareTool = createCompareTool("");

        assert_ValueDiff(compareTool, o1, o2);

        compareTool.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy(NON_TERMINAL_PACKAGE));

        assert_objectLevelNullDiff(compareTool, o1, o2);

        compareTool.configure().register(ObjectWithPrimitivesWithId.class, new ObjectWithPrimitivesWithId.IDResolver());

        assert_NoDiff(compareTool, o1, o2);

        o1.primitiveInt++;
        {
            CompositeComparison<?> comparison = compareTool.compare(o1, " ", o2, " ");
            assertNotNull(comparison);
            assertEquals(1, comparison.childDiffCount());
            assertEquals(SubDiff.class, comparison.diff().getClass());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            ValueDiff primitiveIntDiff = (ValueDiff) childCC.get(0).diff();
            assertEquals(2, primitiveIntDiff.compareObject());
            assertEquals(1, primitiveIntDiff.referenceObject());
        }
        o2.primitiveInt--;
        {
            CompositeComparison<?> comparison = compareTool.compare(o1, " ", o2, " ");
            assertNotNull(comparison);
            assertEquals(1, comparison.childDiffCount());
            assertEquals(SubDiff.class, comparison.diff().getClass());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            ValueDiff primitiveIntDiff = (ValueDiff) childCC.get(0).diff();
            assertEquals(2, primitiveIntDiff.compareObject());
            assertEquals(0, primitiveIntDiff.referenceObject());
        }
        o1.string = "andererString";
        {
            CompositeComparison<ObjectWithPrimitives> comparison = compareTool.compare(o1, "o1", o2, "o2");
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(comparison.hasDiffsOnPropertyLevel());
            assertEquals(2, comparison.childDiffCount());
            assertEquals(SubDiff.class, comparison.diff().getClass());
            List<CompositeComparison<?>> childCC = comparison.childrenWithDiffsOnObjectLevel();
            ValueDiff primitiveIntDiff = (ValueDiff) childCC.get(1).diff();
            assertEquals(2, primitiveIntDiff.compareObject());
            assertEquals(0, primitiveIntDiff.referenceObject());
            ValueDiff stringDiff = (ValueDiff) childCC.get(0).diff();
            assertEquals(o1.string, stringDiff.compareObject());
            assertEquals(o2.string, stringDiff.referenceObject());
        }
    }

  @Test
    public void test_propertyLevelCompareObjectWithNonTerminalReferences() {
        ObjectWithNonTerminalReferences o1 = new ObjectWithNonTerminalReferences();
        ObjectWithNonTerminalReferences o2 = new ObjectWithNonTerminalReferences();

        CompareTool compareTool = createCompareTool("");

        assert_ValueDiff(compareTool, o1, o2); // o1 und o2 werden noch nicht als Non-Terminals erkannt

        compareTool.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy(NON_TERMINAL_PACKAGE));

        assert_objectLevelNullDiff(compareTool, o1, o2); // wegen fehlendem IDResolver kann nicht festgestellt werden, ob ein Diff vorliegt

        compareTool.configure().register(ObjectWithNonTerminalReferences.class, new ObjectWithNonTerminalReferences.IDResolver());
        o1.id = new Long(1);
        o2.id = new Long(1);

        assert_NoDiff(compareTool, o1, o2); // gleiche ID + gleiche Daten => kein Diff

        // Teste, ob folgender ValueDiff gefunden wird:
        final int DIFF_INTEGER_VALUE = 1;
        o1.objectWithPrimitives1.integer = new Integer(DIFF_INTEGER_VALUE);
        {
            CompositeComparison<ObjectWithNonTerminalReferences> comparison = compareTool.compare(o1, "o1", o2, "o2");
            assertNotNull(comparison);
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(comparison.hasDiffsOnPropertyLevel());
            assertEquals(1, comparison.childDiffCount());
            List<CompositeComparison<?>> childCC = comparison.recursiveChildrenWithDiffsOnObjectLevel(comparison);
            assertEquals(1, childCC.size());
            assertEquals(ValueDiff.class, childCC.get(0).diff().getClass());
            ValueDiff valueDiff = (ValueDiff) childCC.get(0).diff();
            assertEquals(DIFF_INTEGER_VALUE, valueDiff.compareObject());
            assertEquals(ObjectWithPrimitives.INITIAL_INTEGER_VALUE, valueDiff.referenceObject());
        }
        // Teste, ob die ValueDiffs nicht mehr registriert werden, wenn das Feature "IgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges"
        // für ObjectWithPrimitives aktiviert wird
        compareTool.configure().registerIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(ObjectWithPrimitives.class);
        {
            CompositeComparison<ObjectWithNonTerminalReferences> comparison = compareTool.compare(o1, "o1", o2, "o2");
            assertNotNull(comparison);
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(!comparison.hasDiffsOnPropertyLevel());
            assertEquals(0, comparison.childDiffCount());
        }

        // Teste, ob die ValueDiffs nicht mehr registriert werden, wenn das Feature "IgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges"
        // für ObjectWithNonTerminalReferences aktiviert wird
        compareTool.configure().deregisterIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(ObjectWithPrimitives.class);
        compareTool.configure().registerIgnoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(ObjectWithNonTerminalReferences.class);
        {
            CompositeComparison<ObjectWithNonTerminalReferences> comparison = compareTool.compare(o1, "o1", o2, "o2");
            assertNotNull(comparison);
            assertTrue(!comparison.hasDiffsOnObjectLevel());
            assertTrue(!comparison.hasDiffsOnPropertyLevel());
            assertEquals(0, comparison.childDiffCount());
        }
    }

  @Test
    public void test_ignorePropertyDiffs() {
        test_ignorePropertyDiffs(new CompareModeAsserts(createCompareTool(NON_TERMINAL_PACKAGE)));
        test_ignorePropertyDiffs(new DiffModeAsserts(createCompareTool(NON_TERMINAL_PACKAGE)));
    }

    private void test_ignorePropertyDiffs(Asserts asserts) {
        ObjectWithNonTerminalReferences2 o1 = new ObjectWithNonTerminalReferences2();
        ObjectWithNonTerminalReferences2 o2 = new ObjectWithNonTerminalReferences2();

        final int DIFF_INTEGER_VALUE = 1;
        o1.objectWithPrimitives1.integer = new Integer(DIFF_INTEGER_VALUE);

        asserts.assert_HasOneValueDiff(o1, o2, DIFF_INTEGER_VALUE, ObjectWithPrimitives.INITIAL_INTEGER_VALUE);
        
        o1.objectWithPrimitivesIgnorePropertyDiffs.integer = new Integer(DIFF_INTEGER_VALUE); // zweites Diff hinzufügen, die aber ignoriert werden soll
        
        asserts.assert_HasOneValueDiff(o1, o2, DIFF_INTEGER_VALUE, ObjectWithPrimitives.INITIAL_INTEGER_VALUE);
        
    }

    protected static final String NON_TERMINAL_PACKAGE = "org.jodt.*";

}
