package org.jodt.property.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jodt.property.PropertyTool;
import org.jodt.property.implementation.DefaultPropertyTool;
import org.jodt.property.implementation.PackageNonTerminalStrategy;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch (oliver@stuch.net)
 */
public class Demo {

    public static List<Person> createLieferung() {
        System.setProperty("sun.swing.enableImprovedDragGesture", "true");

        Adress koe = new Adress("Königsallee", "1a");
        Adress hell = new Adress("Hellersbegstr", "10");
        Adress breit = new Adress("Breitestr", "109");
        Woman mary = new Woman(new PersonIdentificationNumber(1), "Mary");
        mary.string2string.put("firstKey", "firstValue");
        mary.string2string.put("secondKey", "secondValue");
        mary.listOfAdresses.add(hell);
        mary.listOfAdresses.add(koe);
        Man fred = new Man(new PersonIdentificationNumber(2), "Fred");
        fred.listOfAdresses.add(breit);
        Woman mariesMother = new Woman(new PersonIdentificationNumber(3), "Maries Mother");
        mariesMother.listOfAdresses.add(koe);
        Man mariesFather = new Man(new PersonIdentificationNumber(4), "Maries Father");
        mariesFather.listOfAdresses.add(koe);
        mariesMother.husband=mariesFather;
        mariesFather.wife=mariesMother;
        mary.father = mariesFather;
        mary.mother = mariesMother;
        mariesMother.petName.put("littleChild", mary);

        Man john = new Man(new PersonIdentificationNumber(6), "John");
        john.age=32;
        mary.listOfAdresses.add(koe);
        List<Person> personenListe = new ArrayList();
        personenListe.add(mary);
        personenListe.add(fred);
        personenListe.add(john);
        return personenListe;
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

    public static class Person implements Serializable {

        public Person() {
        }

        public Person(PersonIdentificationNumber idnumber, String name) {
            this.idnumber = idnumber;
            this.name = name;
        }

        public String toString() {
            String objectToString = super.toString();
            int indexOfA = objectToString.indexOf("@");
            return name + objectToString.substring(indexOfA, objectToString.length());
        }
        public int age;
        public PersonIdentificationNumber idnumber;
        public String name;
        public List<Adress> listOfAdresses = new ArrayList();
        public Set<String> setOfNicknames = new HashSet();
        public Set<Adress> setOfAdresses = new HashSet();
        public Map<String, Person> petName = new HashMap();
        public Man father = null;
        public Woman mother = null;
        public Map<String, String> string2string = new HashMap();
    }

    public static class Adress implements Serializable {

        public Adress() {
        }

        public Adress(String streetname, String number) {
            this.streetname = streetname;
            this.hausnummer = number;
        }

        public String toString() {
            return streetname + " " + hausnummer;
        }

        public String streetname;
        public String hausnummer;

    }

    public static class Woman extends Person {

        public Woman() {

        }

        public Woman(PersonIdentificationNumber amtsgerichtNummer, String name) {
            super(amtsgerichtNummer, name);
        }

        public Man husband;

    }

    public static class Man extends Person {

        public Man(PersonIdentificationNumber personID, String name) {
            super(personID, name);
        }
        public Woman wife;
    }

    public static class PersonIdentificationNumber implements Serializable {

        public Long nummer;

        public PersonIdentificationNumber() {
        }

        public PersonIdentificationNumber(int i) {
            nummer = new Long(i);
        }

        public String toString() {
            return nummer + "";
        }
    }
}
