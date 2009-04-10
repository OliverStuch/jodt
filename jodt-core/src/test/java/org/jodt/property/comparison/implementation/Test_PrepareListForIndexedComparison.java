package org.jodt.property.comparison.implementation;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Test_PrepareListForIndexedComparison {
    private static final String TEST_NAME = "Test_PrepareListForIndexedComparison";

    @DataProvider(name = TEST_NAME)
    public Object[][] createData1() {
        return new Integer[][][] {
        // 
                // 1. identische Listen
                { { 1, 2, 3 }, { 1, 2, 3 }, // input
                        { 1, 2, 3 }, { 1, 2, 3 } }, // expected result
                        
                // 2. der zweiten Liste fehlt das letzte Element
                { { 1, 2, 3 }, { 1, 2 }, // input
                        { 1, 2, 3 }, { 1, 2, null } }, // expected result
                        
                // 3. der ersten Liste fehlt das letzte Element
                { { 1, 2, 3 }, { 1, 2, 3, 4 }, // input
                        { 1, 2, 3, null }, { 1, 2, 3, 4 } }, // expected result
                        
                // 4. beiden Listen fehlt das jeweils andere Element
                { { 1, 2, 3 }, { 1, 2, 4 }, // input
                        { 1, 2, 3, null }, { 1, 2, null, 4 } }, // expected result
                        
                // 5. der ersten Liste fehlt hinten ein Element (1), der zweiten Liste fehlt vorne eine Element (1)
                { { 1, 2, 3 }, { 2, 3, 1 }, // input
                        { 1, 2, 3, null }, { null, 2, 3, 1 } }, // expected result
                        
                // 6. der ersten Liste fehlen hinten zwei Elemente, der zweiten Liste fehlen vorne zwei Elemente
                { { 1, 2, 3 }, { 3, 2, 1 }, // input
                        { 1, 2, 3, null, null }, { null, null, 3, 2, 1 } }, // expected result
        };
    }

    /**
     * Test, ob das compareTool Listen richtig "auffüllt"
     */
    @Test(dataProvider = TEST_NAME)
    public void test_PrepareListForIndexedComparison(Integer[] inputList1, Integer[] inputList2, Integer[] expectedResult1, Integer[] expectedResult2) {
        // verbrate Zeit
        List list1 = Arrays.asList(inputList1);
        List list2 = Arrays.asList(inputList2);
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        DefaultCompareTool defaultCompareTool = (DefaultCompareTool) compareTool;
        List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
        List result1 = result.get(0);
        List result2 = result.get(1);
        assertEquals(expectedResult1.length, result1.size());
        assertEquals(expectedResult2.length, result2.size());
        for (int i = 0; i < result1.size(); i++) {
            assertEquals(expectedResult1[i], result1.get(i));
        }
        for (int i = 0; i < result2.size(); i++) {
            assertEquals(expectedResult2[i], result2.get(i));
        }
    }

    private CompareTool createCompareTool(String packagePattern) {
        CompareToolConfiguration configuration = new DefaultCompareToolConfiguration();
        configuration.globalNonTerminalStrategy(new PackageNonTerminalStrategy(packagePattern));
        CompareTool compareTool = new DefaultCompareTool(configuration);

        return compareTool;
    }

    protected static final String NON_TERMINAL_PACKAGE = "org.jodt.*";
}
