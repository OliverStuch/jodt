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
import org.jodt.property.comparison.implementation.CompareStrategy;
import org.jodt.property.comparison.implementation.DefaultCompareTool;
import org.jodt.property.gui.Demo;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.jodt.util.ToStringRenderer;


/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class DemoComparison extends Demo {
    public static class PersonIdentifier implements IdentityResolver<Person> {

        public Long getID(Person t) {
            return t.idnumber.nummer;
        }

    }

    public static void modify(List<Person> l1, List<Person> l2) {
        Woman mary2 = (Woman) l2.get(0);
        Man fred2 = (Man)l2.get(1);
        mary2.husband = fred2;
        fred2.listOfAdresses.add(mary2.listOfAdresses.get(0));
        fred2.petName.put("darling", mary2);
        mary2.mother.petName.remove("littleChild");
        Man john2 = (Man)l2.get(2);
        mary2.mother.husband=john2;
        john2.wife=mary2.mother;
        john2.age=33;
        Man mariesFather = mary2.father;
        mariesFather.wife=null;
                
    }

    public static void main(String[] args) {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        List<Person> lieferung1 = createLieferung();
        List<Person> lieferung2 = createLieferung();
        modify(lieferung1, lieferung2);
        CompareTool ct = new DefaultCompareTool();
        ct.configure().registerAnalysePropertiesOfDifferentNonTerminalObjects(Person.class);
        ct.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy("org.jodt.*"));
        ct.configure().registerIdResolver(Person.class, new PersonIdentifier());
        // GUI
        final CompositeComparisonTreeTable lieferungTable = new CompositeComparisonTreeTable(lieferung1, "Test", lieferung2, "Test2", new CompareStrategy(ct));
        // lieferungTable.setNotEditable(Mahngericht.class);
        // lieferungTable.setEditable(false);
        lieferungTable.setEditable(true);
        lieferungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lieferungTable.setToStringRenderer(Adress.class, new ToStringRenderer<Adress>() {
            public String render2String(Adress t) {
                return t.streetname + " " + t.hausnummer;
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
