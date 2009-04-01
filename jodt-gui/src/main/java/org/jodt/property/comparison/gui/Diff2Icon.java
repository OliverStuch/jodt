package org.jodt.property.comparison.gui;

import javax.swing.ImageIcon;

import org.jodt.property.comparison.implementation.Additional;
import org.jodt.property.comparison.implementation.Missing;
import org.jodt.property.comparison.implementation.ReferenceDiff;
import org.jodt.property.comparison.implementation.SubDiff;
import org.jodt.property.comparison.implementation.ValueDiff;
import org.jodt.util.Registry;


/**
 * @author Oliver Stuch
 */

public class Diff2Icon extends Registry<ImageIcon> {
    public Diff2Icon() {
        register(Missing.class, new ImageIcon(CompositeComparisonTreeTable.class.getResource("missing.png")));
        register(Additional.class, new ImageIcon(CompositeComparisonTreeTable.class.getResource("additional.png")));
        register(ReferenceDiff.class, new ImageIcon(CompositeComparisonTreeTable.class.getResource("referenceChanged.png")));
        register(ValueDiff.class, new ImageIcon(CompositeComparisonTreeTable.class.getResource("diff.png")));
        register(SubDiff.class, new ImageIcon(CompositeComparisonTreeTable.class.getResource("subDiffs.png")));
        // register(NoDiff.class,new ImageIcon(CompositeComparisonTreeTable.class.getResource("noDiff.png")));
    }
}
