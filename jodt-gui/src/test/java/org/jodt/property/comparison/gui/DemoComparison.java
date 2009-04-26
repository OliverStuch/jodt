package org.jodt.property.comparison.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jodt.property.IdentityResolver;
import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.gui.CompositeComparisonTreeTable;
import org.jodt.property.comparison.implementation.CompareStrategy;
import org.jodt.property.comparison.implementation.DefaultCompareTool;
import org.jodt.property.gui.Demo;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.jodt.util.ToStringRenderer;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class DemoComparison extends Demo {
    public static class GerichtIdentifier implements IdentityResolver<Gericht> {

        public Long getID(Gericht t) {
            return t.nummer.nummer;
        }

    }

    public static void modify(List<Amtsgericht> l1, List<Amtsgericht> l2) {
        l1.get(0).name = "neuer Name";
        l1.get(1).setOfStrings.add("neuer String");
        l2.get(1).setOfStrings.add("alter String");
        l2.get(2).übergeordnetesGericht=(Gericht) l2.get(1);
    }

    public static void main(String[] args) {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        List<Amtsgericht> lieferung1 = createLieferung();
        List<Amtsgericht> lieferung2 = createLieferung();
        modify(lieferung1, lieferung2);
        CompareTool ct = new DefaultCompareTool();
        ct.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy("org.jodt.*"));
        ct.configure().register(Gericht.class, new GerichtIdentifier());
        // GUI
        final CompositeComparisonTreeTable lieferungTable = new CompositeComparisonTreeTable(lieferung1, "Test", lieferung2, "Test2", new CompareStrategy(ct));
        // lieferungTable.setNotEditable(Mahngericht.class);
        // lieferungTable.setEditable(false);
        lieferungTable.setEditable(true);
        lieferungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lieferungTable.setToStringRenderer(Adress.class, new ToStringRenderer<Adress>() {
            public String render2String(Adress t) {
                return t.street + " " + t.number;
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
