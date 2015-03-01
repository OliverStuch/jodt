package org.jodt.property.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import org.apache.log4j.Logger;
import org.jodt.property.PropertyTool;
import org.jodt.property.comparison.IgnorePropertyDiffs;
import org.jodt.property.implementation.DefaultPropertyTool;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.jodt.util.ToStringRenderer;


/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class Demo {

    public static List<Amtsgericht> createLieferung() {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        Adresse koe = new Adresse("Königsallee", "1a");
        Adresse hell = new Adresse("Hellersbegstr", "10");
        Adresse breit = new Adresse("Breitestr", "109");
        Amtsgericht amtsgerichtD = new Amtsgericht(new Gerichtsnummer(1), "Amtsgericht Düsseldorf");
        Amtsgericht amtsgerichtK = new Amtsgericht(new Gerichtsnummer(3), "Amtsgericht Köln");
        amtsgerichtD.übergeordnetesGericht = amtsgerichtK;

        Amtsgericht amtsgerichtN = new Amtsgericht(new Gerichtsnummer(10), "Amtsgericht Neuss");
        Mahngericht mahngerichtD = new Mahngericht(new Gerichtsnummer(2), "Mahngericht Düsseldorf");
        Mahngericht mahngerichtN = new Mahngericht(new Gerichtsnummer(12), "Mahngericht Neuss");
        amtsgerichtN.übergeordnetesGericht = amtsgerichtD;

        mahngerichtD.adressen.add(koe);
        mahngerichtN.adressen.add(hell);

        amtsgerichtD.adressen.add(koe);
        amtsgerichtD.mahngericht = mahngerichtD;

        amtsgerichtN.adressen.add(hell);
        amtsgerichtN.mahngericht = mahngerichtN;

        amtsgerichtK.adressen.add(breit);
        // Adressänderung amtsgericht Düsseldorf

        Amtsgericht amtsgerichtDNeu = new Amtsgericht(new Gerichtsnummer(1), "Amtsgericht Düsseldorf");
        Adresse koeNeu = new Adresse("Königsweg", "2a");
        amtsgerichtDNeu.adressen.add(koeNeu);
        amtsgerichtDNeu.mahngericht = mahngerichtN;

        Amtsgericht amtsgerichtNNeu = new Amtsgericht(new Gerichtsnummer(10), "Amtsgericht Neuss");
        amtsgerichtNNeu.adressen.add(hell);
        amtsgerichtNNeu.mahngericht = mahngerichtD;
        List<Amtsgericht> lieferung = new ArrayList();
        lieferung.add(amtsgerichtD);
        lieferung.add(amtsgerichtK);
        lieferung.add(amtsgerichtN);
        
        return lieferung;
    }

    public static void main(String[] args) {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        PropertyTool propertyTool = new DefaultPropertyTool();
        propertyTool.configure().globalNonTerminalStrategy(new PackageNonTerminalStrategy("org.jodt.property.*"));
        final CompositePropertyTreeTable lieferungTable = new CompositePropertyTreeTable(createLieferung(), "Test", propertyTool);
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

    public static class Gericht implements Serializable {

        public Gericht() {
        }

        public Gericht(Gerichtsnummer amtsgerichtNummer, String name) {
            nummer = amtsgerichtNummer;
            this.name = name;
            setOfStrings.add("some info");
            setOfStrings.add("another info");
            setOfAdressen.add(new Adresse("Wo bin", "ich"));
        }

        public String toString() {
            String objectToString = super.toString();
            int indexOfA = objectToString.indexOf("@");
            return name + objectToString.substring(indexOfA, objectToString.length()) + " #adressen: " + (adressen != null ? adressen.size() + "" : "---");
        }

        public Gerichtsnummer nummer;
        public String name;
        List<Adresse> adressen = new ArrayList<Adresse>();
        public Set<String> setOfStrings = new HashSet();
        Set<Adresse> setOfAdressen = new HashSet();
        @IgnorePropertyDiffs
        public Gericht übergeordnetesGericht = null;
    }

    public static class Adresse implements Serializable {
        public Adresse() {
        }

        public Adresse(String strasse, String nummer) {
            this.strasse = strasse;
            this.hausnummer = nummer;
        }

        public String toString() {
            return strasse + " " + hausnummer;
        }

        public String strasse;
        public String hausnummer;

    }

    private static class Version {
        private Date date = new Date();
    }

    public static class Amtsgericht extends Gericht {

        public Amtsgericht() {

        }

        public Amtsgericht(Gerichtsnummer amtsgerichtNummer, String name) {
            super(amtsgerichtNummer, name);
        }

        Mahngericht mahngericht;
    }

    public static class Mahngericht extends Gericht {

        public Mahngericht() {
        }

        public Mahngericht(Gerichtsnummer amtsgerichtNummer, String name) {
            super(amtsgerichtNummer, name);
            // TODO Auto-generated constructor stub
        }

    }

    public static class Gerichtsnummer implements Serializable {

        public Long nummer;

        public Gerichtsnummer() {
        }

        public Gerichtsnummer(int i) {
            nummer = new Long(i);
        }

        public String toString() {
            return nummer + "";
        }
    }

    private static final Logger logger = Logger.getLogger(Demo.class);
}
