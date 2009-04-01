package org.jodt.property.comparison.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.gui.DemoComparison.GerichtIdentifier;
import org.jodt.property.comparison.implementation.DefaultCompareTool;
import org.jodt.property.comparison.implementation.DiffStrategy;
import org.jodt.property.gui.Demo;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch
 */

public class DemoDiff extends Demo{
    public static void main(String[] args) {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        // GUI
        CompareTool ct = new DefaultCompareTool();
        List<Amtsgericht> lieferung1 = createLieferung();
        List<Amtsgericht> lieferung2 = createLieferung();
        DemoComparison.modify(lieferung1, lieferung2);

        ct.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy("org.jodt.*"));
        ct.configure().register(Gericht.class, new GerichtIdentifier());
        final CompositeComparisonTreeTable lieferungTable = new CompositeComparisonTreeTable(lieferung1, "Test", lieferung2, "Test2", new DiffStrategy(ct));
        // lieferungTable.setNotEditable(Mahngericht.class);
        // lieferungTable.setEditable(false);
        lieferungTable.setEditable(true);
//        lieferungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lieferungTable.setToStringRenderer(Adresse.class, new ToStringRenderer<Adresse>() {
            public String render2String(Adresse t) {
                return t.strasse + " " + t.hausnummer;
            }
        });

        JFrame frame = new JFrame("test");
        Container contentPane = frame.getContentPane();
        JPanel demoPanel = new JPanel(new BorderLayout());

        demoPanel.add(new JScrollPane(lieferungTable), BorderLayout.CENTER);
        demoPanel.add(new JLabel("Lieferung"), BorderLayout.NORTH);

        contentPane.add(demoPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
