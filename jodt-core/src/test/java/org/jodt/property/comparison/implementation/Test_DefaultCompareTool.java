package org.jodt.property.comparison.implementation;

import java.util.ArrayList;
import java.util.List;

import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.Test_CompareTool;
import org.jodt.property.implementation.PackageNonTerminalStrategy;


/**
 * @author Oliver Stuch
 */

public class Test_DefaultCompareTool extends Test_CompareTool {

    public Test_DefaultCompareTool(String text) {
        super(text);
        // TODO Auto-generated constructor stub
    }

    public CompareTool createCompareTool(String packagePattern) {
        CompareToolConfiguration configuration = new DefaultCompareToolConfiguration();
        configuration.globalNonTerminalStrategy(new PackageNonTerminalStrategy(packagePattern));
        CompareTool compareTool = new DefaultCompareTool(configuration);

        return compareTool;
    }

    public void test_prepareListsForIndexedComparison() {
        CompareTool compareTool = createCompareTool(NON_TERMINAL_PACKAGE);
        DefaultCompareTool defaultCompareTool = (DefaultCompareTool) compareTool;

        {
            List list1 = new ArrayList();
            list1.add(new Integer(1));
            list1.add(new Integer(2));
            list1.add(new Integer(3));

            List list2 = new ArrayList();
            list2.add(new Integer(1));
            list2.add(new Integer(2));
            { // 1. Liste hat hinten ein Element mehr
                List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
                assertEquals(2, result.size());
                List resultlist1 = result.get(0);
                List resultlist2 = result.get(1);
                assertEquals(3, resultlist1.size());
                assertEquals(3, resultlist2.size());
                assertEquals(resultlist1.get(0), resultlist2.get(0));
                assertEquals(resultlist1.get(1), resultlist2.get(1));
                assertEquals(resultlist1.get(2), new Integer(3));
                assertEquals(resultlist2.get(2), null);
            }

            list2.add(new Integer(3));
            {
                List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
                assertEquals(2, result.size());
                List resultlist1 = result.get(0);
                List resultlist2 = result.get(1);
                assertEquals(3, resultlist1.size());
                assertEquals(3, resultlist2.size());
                assertEquals(resultlist1.get(0), resultlist2.get(0));
                assertEquals(resultlist1.get(1), resultlist2.get(1));
                assertEquals(resultlist1.get(2), resultlist2.get(2));
            }
            list2.add(new Integer(4));
            { // 2. Liste hat hinten ein Element mehr
                List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
                assertEquals(2, result.size());
                List resultlist1 = result.get(0);
                List resultlist2 = result.get(1);
                assertEquals(4, resultlist1.size());
                assertEquals(4, resultlist2.size());
                assertEquals(resultlist1.get(0), resultlist2.get(0));
                assertEquals(resultlist1.get(1), resultlist2.get(1));
                assertEquals(resultlist1.get(2), resultlist2.get(2));
                assertEquals(resultlist2.get(3), new Integer(4));
                assertEquals(resultlist1.get(3), null);
            }
        }
        { // zweite Liste hat vorne ein Element mehr
            List list2 = new ArrayList();
            list2.add(new Integer(1));
            list2.add(new Integer(2));

            List list1 = new ArrayList();
            list1.add(new Integer(2));
            {
                List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
                assertEquals(2, result.size());
                List resultlist1 = result.get(0);
                List resultlist2 = result.get(1);
                assertEquals(2, resultlist1.size());
                assertEquals(2, resultlist2.size());
                assertEquals(null, resultlist1.get(0));
                assertEquals(new Integer(1), resultlist2.get(0));
                assertEquals(resultlist1.get(1), resultlist2.get(1));
            }
        }
        
        { //  erste Liste hat vorne ein Element mehr
            List list1 = new ArrayList();
            list1.add(new Integer(1));
            list1.add(new Integer(2));

            List list2 = new ArrayList();
            list2.add(new Integer(2));
            {
                List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
                assertEquals(2, result.size());
                List resultlist1 = result.get(0);
                List resultlist2 = result.get(1);
                assertEquals(2, resultlist1.size());
                assertEquals(2, resultlist2.size());
                assertEquals(null, resultlist2.get(0));
                assertEquals(new Integer(1), resultlist1.get(0));
                assertEquals(resultlist1.get(1), resultlist2.get(1));
            }
        }
        {
            List list1 = new ArrayList();
            list1.add(new Integer(1));
            list1.add(new Integer(2));
            list1.add(new Integer(3));

            List list2 = new ArrayList();
            list2.add(new Integer(3));
            list2.add(new Integer(2));
            list2.add(new Integer(1));

            List<MappedList> result = defaultCompareTool.prepareListsForIndexedComparison(list1, list2);
            assertEquals(2, result.size());
            List resultlist1 = result.get(0);
            List resultlist2 = result.get(1);
            assertEquals(5, resultlist1.size());
            assertEquals(5, resultlist2.size());
            assertEquals(new Integer(1), resultlist1.get(0));
            assertEquals(null, resultlist2.get(0));
            assertEquals(new Integer(2), resultlist1.get(1));
            assertEquals(null, resultlist2.get(1));
            assertEquals(resultlist1.get(2), resultlist2.get(2));
            assertEquals(null, resultlist1.get(3));
            assertEquals(new Integer(2), resultlist2.get(3));
            assertEquals(null, resultlist1.get(4));
            assertEquals(new Integer(1), resultlist2.get(4));
        }

    }
}
