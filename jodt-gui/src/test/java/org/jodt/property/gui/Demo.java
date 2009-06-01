package org.jodt.property.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.jodt.property.comparison.IgnorePropertyDiffs;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class Demo {

    public static List<Amtsgericht> createLieferung() {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        Adress koe = new Adress("Königsallee", "1a", "Düsseldorf");
        Adress hell = new Adress("Hellersbegstr", "10", "Neuss");
        Adress breit = new Adress("Breitestr", "109", "Köln");
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
        Adress koeNeu = new Adress("Königsweg", "2a", "Düsseldorf");
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

    static public Set<Person> createSmallWorld() {
        Set<Person> result = new HashSet();
        Person jack = new Person("Jack", Gender.MALE, new Adress("Bourbon Street", "1", "Springfield"));
        Person jill = new Person("Jill", Gender.FEMALE, new Adress("Springfield Road", "2", "Jtown"));
        jill.addFriend(jack);
        jack.addFriend(jill);
        result.add(jill);
        result.add(jack);
        return result;
    }

    static public Set<Person> createSmallWorldWithVariations(Set<Person> persons) {
        Set<Person> result = (Set<Person>) SerializationUtils.clone((Serializable) persons);
        for (Person person : result) {
            person.removeFriends();
        }
        return result;
    }

    public static void main(String[] args) {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        // GUI
//        final CompositePropertyTreeTable lieferungTable = new CompositePropertyTreeTable(createSmallWorld(), "Test");
        final CompositePropertyTreeTable lieferungTable = new CompositePropertyTreeTable(createLieferung(), "Test");
        // lieferungTable.setNotEditable(Mahngericht.class);
        // lieferungTable.setEditable(false);
        // lieferungTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

    public static class Gericht implements Serializable {

        public Gericht() {
        }

        public Gericht(Gerichtsnummer amtsgerichtNummer, String name) {
            nummer = amtsgerichtNummer;
            this.name = name;
            setOfStrings.add("some info");
            setOfStrings.add("another info");
            setOfAdressen.add(new Adress("Wo bin", "ich", "hier"));
        }

        public String toString() {
            String objectToString = super.toString();
            int indexOfA = objectToString.indexOf("@");
            return name + objectToString.substring(indexOfA, objectToString.length()) + " #adressen: " + (adressen != null ? adressen.size() + "" : "---");
        }

        public Gerichtsnummer nummer;
        public String name;
        List<Adress> adressen = new ArrayList<Adress>();
        public Set<String> setOfStrings = new HashSet();
        Set<Adress> setOfAdressen = new HashSet();
        @IgnorePropertyDiffs
        public Gericht übergeordnetesGericht = null;
    }

    enum Gender {
        MALE, FEMALE
    }

    public static class Person implements Serializable {
        public Person(String name, Gender gender, Adress adress) {
            this.name = name;
            this.gender = gender;
            this.adresses.add(adress);
        }

        public String toString() {
            return name;
        }
        
        public void removeFriends() {
            friends.clear();
        }

        public void addFriend(Person person) {
            friends.add(person);
        }

        private String name;
        private Gender gender;
        private List<Adress> adresses = new ArrayList();
        private Set<Person> friends = new HashSet();
        private Map<Person, String> nickNames;
        private int age;
        private Date birth;
    }

    public static class Adress implements Serializable {
        public Adress() {
        }

        public Adress(String strasse, String nummer, String city) {
            this.city = city;
            this.street = strasse;
            this.number = nummer;
        }

        public String toString() {
            return street + " " + number;
        }

        private String city;
        public String street;
        public String number;

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
