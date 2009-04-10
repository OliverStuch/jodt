package org.jodt.property.comparison.implementation;

import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.Test_CompareTool;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.testng.annotations.Test;



/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
@Test
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

}
