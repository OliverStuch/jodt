package org.jodt.property.comparison.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jodt.property.CompositeProperty;
import org.jodt.property.CompositePropertyList;
import org.jodt.property.CompositePropertySet;
import org.jodt.property.Property;
import org.jodt.property.PropertyTool;
import org.jodt.property.comparison.CompareTool;
import org.jodt.property.comparison.CompareToolConfiguration;
import org.jodt.property.comparison.CompositeComparison;
import org.jodt.property.comparison.CompositeMerge;
import org.jodt.property.comparison.DiffType;
import org.jodt.property.comparison.IgnorePropertyDiffs;
import org.jodt.property.comparison.IndexMapper;
import org.jodt.property.implementation.DefaultCompositePropertyList;
import org.jodt.property.implementation.DefaultPropertyTool;
import org.jodt.property.implementation.PropertyUtil;

public class DefaultCompareTool implements CompareTool {

    /**
     * uses DefaultCompareToolConfiguration
     */
    public DefaultCompareTool() {
        this(new DefaultCompareToolConfiguration());
    }

    public DefaultCompareTool(CompareToolConfiguration compareToolConfiguration) {
        this.compareToolConfiguration = compareToolConfiguration;
        this.propertyTool = new DefaultPropertyTool(true, compareToolConfiguration);
    }

    public <T> CompositeComparison<T> diff(T comparativeObject, String comparativeName, T referenceObject, String referenceName) {
        diffMode = true;
        CompositeComparison<T> result = recursiveAnalysis(comparativeObject, comparativeName, referenceObject, referenceName, null);
        calculateDiffcountRecursivly(result);
        return result;
    }

    public <T> CompositeComparison<T> compare(T comparativeObject, String comparativeName, T referenceObject, String referenceName) {
        diffMode = false;
        CompositeComparison<T> result = recursiveAnalysis(comparativeObject, comparativeName, referenceObject, referenceName, null);
        calculateDiffcountRecursivly(result);
        return result;
    }

    private <T> int calculateDiffcountRecursivly(CompositeComparison<T> compositeComparison) {
        int childDiffCount = 0;
        if (compositeComparison.hasChildren()) {
            for (CompositeComparison<?> childCompositeComparison : compositeComparison) {
                childDiffCount += calculateDiffcountRecursivly(childCompositeComparison);
            }
            compositeComparison.childDiffCount(childDiffCount);
            return childDiffCount;
        } else {
            if (compositeComparison.diff() != null && !(compositeComparison.diff() instanceof NoDiff)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    // Unterscheidung diff und comparison
    private <T> CompositeComparison<T> recursiveAnalysis(Object comparativeObject, String comparativeName, Object referenceObject, String referenceName,
            CompositeComparison<T> parentAnalysis) {
        // ------------------------------------------------------ Analyse der Objekte ---------------------------------------------------------------------- //
        // erster Schritt, ist Ebene der Objekte einen Vergleich zu machen. Ist compareObject gegeben und referenceObjekt nicht, liegt bereits ein Diff vor ("Additional")
        PropertyListPair<T> identityMappedPropertyListPair = createIdentityMappedPropertyList(comparativeObject, comparativeName, referenceObject, referenceName);
        CompositeComparison<T> analysis = new DefaultCompositeComparison(identityMappedPropertyListPair, parentAnalysis);
        // analysis.diffMode(diffMode); ?????
        DiffType toplevelObjectDiff = null; // solange auf null, kann es kein toplevel (quick)-result geben
        if (!this.compareToolConfiguration.ignoreObjectButAnalyseItsNonTerminalProperties(comparativeObject)) {
            // Wenn das Objekt NICHT ignoriert werden soll (z.B. Version-Objekt würde hier ignoriert)
            toplevelObjectDiff = determineToplevelDiff(comparativeObject, referenceObject);
        }
        analysis.diff(toplevelObjectDiff);
        // toplevelObjectDiff kann hier also null sein, weil es nicht untersucht wurde oder weil kein Toplevel-Unterschied festgestellt werden konnte
        // im DiffMode gibt es an dieser Stelle eine Ausstiegsmöglichkeit: Es liegt auf Objekt-Ebene bereits ein Diff vor. Wenn analysePropertiesOfDifferentNonTerminalObjects false ist
        // ist analysis bereits das Ergebnis des rekursiven Vergleichs (die Properties der auf Toplevelebene unterschiedlichen Objekte werden nicht
        // weiter untersucht
        if (diffMode) {
            if (toplevelObjectDiff != null && !(toplevelObjectDiff instanceof NoDiff) // es gab also ein Diff
                    && !compareToolConfiguration.analysePropertiesOfDifferentNonTerminalObjects(comparativeObject)) {
                // für diff auf objekt-ebene die Möglichkeit , einen "inneren" (rekursiven) Vergleich zu machen, wenn
                // analysePropertiesOfDifferentNonTerminalObjects true ergibt
                return analysis;
            }
        }
        // ------------------------------------------------------ Analyse der Properties ---------------------------------------------------------------------- //
        // nun werden die Properties der beiden Objekte untersucht
        CompositePropertyList<T> mappedPropertyListCompare = identityMappedPropertyListPair.compareProperties;
        CompositePropertyList<T> mappedPropertyListReference = identityMappedPropertyListPair.referenceProperties;
        // Die folgenden IFs sind Bedingungen für die Fortsetzung der Analyse ...
        if (mappedPropertyListCompare.hasProperties() || mappedPropertyListReference.hasProperties()) { // es gibt sub-properties
            if ((toplevelObjectDiff != null && (toplevelObjectDiff instanceof Additional || toplevelObjectDiff instanceof Missing || toplevelObjectDiff instanceof ValueDiff))) {
                // Dies ist der Fall, dass zwei Objekte verglichen wurden, die zwar subproperties haben, aber als "terminal" betrachtet wurden
            } else {
                if (!(toplevelObjectDiff != null && toplevelObjectDiff instanceof ReferenceDiff && !compareToolConfiguration
                        .analysePropertiesOfDifferentNonTerminalObjects(comparativeObject))) { // war auch kein refdiff (value diff kann nicht sein, non terminal)
                    // wenn kein Toplevel-Unterschied gefunden wurde oder dennoch weiter analysiert werden soll
                    for (int propertyIndex = 0; propertyIndex < identityMappedPropertyListPair.getNumProperties(); propertyIndex++) {
                        boolean add2analysis = true;
                        Property<?> compareProperty = mappedPropertyListCompare.get(propertyIndex);
                        Property<?> referenceProperty = mappedPropertyListReference.get(propertyIndex);
                        if (!ignoreProperty(comparativeObject, compareProperty, referenceObject, referenceProperty)) { // Soll die Property ignoriert werden?
                            Object comparePropertyValue = PropertyUtil.value(compareProperty);
                            Object referencePropertyValue = PropertyUtil.value(referenceProperty);
                            if (!compareToolConfiguration.ignoreObjectButAnalyseItsNonTerminalProperties(comparativeObject)
                                    || !compareToolConfiguration.isTerminal(comparePropertyValue)) {
                                // Wenn ignoreObjectButAnalyseItsNonTerminalProperties für
                                // das compareObject gilt, darf nur weiter analysiert werden, wenn compareProperty non-terminal ist
                                if (compareToolConfiguration.ignoreAllDiffsOfNonTerminalPropertiesButReferenceChanges(comparativeObject)
                                        || PropertyUtil.isAnnotationPresent(compareProperty, IgnorePropertyDiffs.class)) {
                                    add2analysis = false;
                                    DiffType toplevelPropertyDiff = determineToplevelDiff(comparePropertyValue, referencePropertyValue);
                                    if (toplevelPropertyDiff != null && toplevelPropertyDiff instanceof ReferenceDiff) {
                                        add2analysis = true;
                                    }
                                }
                                if (add2analysis) {
                                    CompositeComparison<T> propertyAnalysis = recursiveAnalysis(comparePropertyValue, PropertyUtil.name(compareProperty), referencePropertyValue,
                                            PropertyUtil.name(referenceProperty), analysis);
                                    if (diffMode) {
                                        if (propertyAnalysis.hasDiffsOnObjectLevel() || propertyAnalysis.hasDiffsOnPropertyLevel()) {
                                            analysis.add(propertyAnalysis);
                                            if (toplevelObjectDiff instanceof ReferenceDiff) {// ReferenceDiff anzeigen, damit klar ist, dass die "tieferen" Diffs daher rühren, dass unterschiedliche Objekte verglichen werden!
                                                analysis.diff(toplevelObjectDiff);
                                            } else {
                                                analysis.diff(new SubDiff());
                                            }
                                        }
                                    } else { // CompareMode
                                        analysis.add(propertyAnalysis);
                                        if (propertyAnalysis.hasDiffsOnObjectLevel() || propertyAnalysis.hasDiffsOnPropertyLevel()) {
                                            if (toplevelObjectDiff instanceof ReferenceDiff) {// ReferenceDiff anzeigen, damit klar ist, dass die "tieferen" Diffs daher rühren, dass unterschiedliche Objekte verglichen werden!
                                                analysis.diff(toplevelObjectDiff);
                                            } else {
                                                analysis.diff(new SubDiff());
                                            }
                                        }
                                        // DiffType propertyDiff = determineToplevelDiff(comparePropertyValue, referencePropertyValue);
                                        // if (propertyDiff != null && !(propertyDiff instanceof NoDiff)) {
                                        // propertyAnalysis.diffType(propertyDiff);
                                        // }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return analysis;
    }

    private boolean ignoreProperty(Object compareObject, Property compareProperty, Object referenceObject, Property referenceProperty) {
        if (compareObject != null) {
            return this.compareToolConfiguration.ignoreProperty(compareObject.getClass(), PropertyUtil.name(compareProperty), PropertyUtil.type(compareProperty));
        } else if (referenceObject != null) {
            return this.compareToolConfiguration.ignoreProperty(referenceObject.getClass(), PropertyUtil.name(referenceProperty), PropertyUtil.type(referenceProperty));
        }
        return false;
    }

    private PropertyListPair createIdentityMappedPropertyList(Object compareObject, String comparativeName, Object referenceObject, String referenceName) {
        CompositeProperty<?> compareObjectAsPropertyCollection = this.propertyTool.createOneLevelRecursiveCompositeProperty(compareObject, comparativeName);
        CompositeProperty<?> referenceObjectAsPropertyCollection = this.propertyTool.createOneLevelRecursiveCompositeProperty(referenceObject, referenceName);
        if (CompositePropertySet.class.isAssignableFrom(compareObjectAsPropertyCollection.getClass())
                && CompositePropertySet.class.isAssignableFrom(referenceObjectAsPropertyCollection.getClass())) {
            return createIdentityMappedPropertyListFromSets((CompositePropertySet) compareObjectAsPropertyCollection, (CompositePropertySet) referenceObjectAsPropertyCollection);
        } else if (CompositePropertyList.class.isAssignableFrom(compareObjectAsPropertyCollection.getClass())
                && CompositePropertyList.class.isAssignableFrom(referenceObjectAsPropertyCollection.getClass())) {
            return createIdentityMappedPropertyListFromLists((CompositePropertyList) compareObjectAsPropertyCollection, (CompositePropertyList) referenceObjectAsPropertyCollection);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Bei Listen erfolgt der Vergleich per Index. Diese Methode macht also
     * nichts ausser dem Erzeugen des PropertyListPair aus den beiden
     * übergebenen Listen. Diese Methode dient nur der Performanceoptimierung
     */
    private <T> PropertyListPair createIdentityMappedPropertyListFromLists(CompositePropertyList<T> propertyList1, CompositePropertyList<T> propertyList2) {

        List<MappedList> preparedMappedLists = prepareListsForIndexedComparison(propertyList1, propertyList2, new Comparator<Property>() {
            public int compare(Property o1, Property o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        PropertyListPair result = new PropertyListPair();
        result.compareProperties = new DefaultCompositePropertyList(propertyList1, preparedMappedLists.get(0));
        result.referenceProperties = new DefaultCompositePropertyList(propertyList2, preparedMappedLists.get(1));
        return result;
    }

    /**
     * Wandelt zum Vergleich der Sets die beiden anhand der identität in listen
     * (-> sorted) um und "bläht" diese auf
     */
    private <T> PropertyListPair createIdentityMappedPropertyListFromSets(CompositePropertySet<T> propertySet1, CompositePropertySet<T> propertySet2) {
        // Umfüllen der Sets in Listen und sortieren der Listen
        List<Comparable> comparablePropertyList1 = new ArrayList();
        int i = 0;
        for (CompositeProperty property : propertySet1) {
            comparablePropertyList1.add(new ComparableProperty(property, i++, compareToolConfiguration.resolveId(property)));
        }
        Collections.sort(comparablePropertyList1);

        i = 0;
        List<Comparable> comparablePropertyList2 = new ArrayList();
        for (CompositeProperty property : propertySet2) {
            comparablePropertyList2.add(new ComparableProperty(property, i++, compareToolConfiguration.resolveId(property)));
        }
        Collections.sort(comparablePropertyList2);

        List<MappedList> preparedMappedLists = prepareListsForIndexedComparison(comparablePropertyList1, comparablePropertyList2);
        PropertyListPair result = new PropertyListPair();
        result.compareProperties = new DefaultCompositePropertyList(propertySet1, preparedMappedLists.get(0));
        result.referenceProperties = new DefaultCompositePropertyList(propertySet2, preparedMappedLists.get(1));
        return result;
    }

    static class PropertyListPair<T> {

        CompositePropertyList<T> compareProperties;
        CompositePropertyList<T> referenceProperties;

        public int getNumProperties() {
            assert (compareProperties.size() == referenceProperties.size());
            return compareProperties.size();
        }
    }

    private static class ComparableComparator implements Comparator {

        public int compare(Object o1, Object o2) {
            Comparable comparable1 = (Comparable) o1;
            Comparable comparable2 = (Comparable) o2;
            return comparable1.compareTo(comparable2);
        }

    }

    private static class ComparatorComparator implements Comparator {

        private Comparator comparator;

        public ComparatorComparator(Comparator comparator) {
            this.comparator = comparator;
        }

        public int compare(Object o1, Object o2) {
            return comparator.compare(o1, o2);
        }

        public boolean equals(Object object) {
            return comparator.equals(object);
        }

    }

    List<MappedList> prepareListsForIndexedComparison(List<Comparable> comparables1, List<Comparable> comparables2) {
        return internalPrepareListsForIndexedComparison(comparables1, comparables2, new ComparableComparator());
    }

    List<MappedList> prepareListsForIndexedComparison(List comparables1, List comparables2, Comparator comparator) {
        return internalPrepareListsForIndexedComparison(comparables1, comparables2, new ComparatorComparator(comparator));
    }

    /**
     * Bläht zwei Listen aufgrund von compareTo so auf, dass 2 Listen entstehen
     * die "paarweise" (Paar: aus jeder Liste mit gleichem Index ein Element
     * entnehmen) verglichen werden können.
     *
     * @return 2-elementige Liste;
     * result.get(0).get(i).compareTo(result.get(1).get(i)) == 0 XOR
     * result.get(0).get(i) == null XOR result.get(1).get(i) == null
     */
    private List<MappedList> internalPrepareListsForIndexedComparison(List<?> comparables1, List<?> comparables2, Comparator comparator) {
        Map<Object, Integer> object2index1 = createObject2IndexMapping(comparables1, comparator);
        Map<Object, Integer> object2index2 = createObject2IndexMapping(comparables2, comparator);

        int index1 = 0;
        int index2 = 0;

        IndexMapper indexMapper1 = new DefaultIndexMapper();
        IndexMapper indexMapper2 = new DefaultIndexMapper();

        int mappingIndex = 0;
        Object object1 = null;
        Object object2 = null;

        boolean object1ready = false;
        boolean object2ready = false;

        while ((object1ready || index1 != comparables1.size()) && (object2ready || index2 != comparables2.size())) {

            if (mappingIndex == 0) {
                object1 = comparables1.get(index1++);
                object1ready = true;
                object2 = comparables2.get(index2++);
                object2ready = true;
            }

            if (comparator.compare(object1, object2) < 0) { // liste1 (links) hat ein zusätzliches Element
                indexMapper1.addMapping(mappingIndex, object2index1.get(object1));
                object1ready = false;
                indexMapper2.addMapping(mappingIndex, IndexMapper.MISSING);
                mappingIndex++;
                if (index1 == comparables1.size()) {
                    break;
                }
                object1 = comparables1.get(index1++);
                object1ready = true;
            } else if (comparator.compare(object1, object2) > 0) { // liste2 (rechts) hat ein zusätzliches Element
                indexMapper1.addMapping(mappingIndex, IndexMapper.MISSING);
                indexMapper2.addMapping(mappingIndex, object2index2.get(object2));
                object2ready = false;
                mappingIndex++;
                if (index2 == comparables2.size()) {
                    break;
                }
                object2 = comparables2.get(index2++);
                object2ready = true;
            } else { // beide Listen enthalten das Element
                indexMapper1.addMapping(mappingIndex, object2index1.get(object1));
                object1ready = false;
                indexMapper2.addMapping(mappingIndex, object2index2.get(object2));
                object2ready = false;
                mappingIndex++;
                if (index1 == comparables1.size() || index2 == comparables2.size()) {
                    break;
                }
                object1 = comparables1.get(index1++);
                object1ready = true;
                object2 = comparables2.get(index2++);
                object2ready = true;
            }

        }

        // Restlistenverarbeitung
        while (object1ready || index1 != comparables1.size()) { // hat list1 (links) noch Elemente übgrig?
            if (!object1ready) {
                object1 = comparables1.get(index1++);
            }
            indexMapper1.addMapping(mappingIndex, object2index1.get(object1));
            object1ready = false;
            indexMapper2.addMapping(mappingIndex, IndexMapper.MISSING);
            mappingIndex++;
        }

        while (object2ready || index2 != comparables2.size()) { // hat liste2 (rechts) noch Elemente übgrig?
            if (!object2ready) {
                object2 = comparables2.get(index2++);
            }
            indexMapper1.addMapping(mappingIndex, IndexMapper.MISSING);
            indexMapper2.addMapping(mappingIndex, object2index2.get(object2));
            object2ready = false;
            mappingIndex++;
        }
        List<MappedList> result = new ArrayList();
        result.add(new MappedList(comparables1, indexMapper1));
        result.add(new MappedList(comparables2, indexMapper2));
        return result;
    }

    private Map<Object, Integer> createObject2IndexMapping(List objects, Comparator comparator) {
        Map<Object, Integer> result = new TreeMap(comparator);
        for (int i = 0; i < objects.size(); i++) {
            result.put(objects.get(i), new Integer(i));
        }
        return result;
    }

    /**
     * stellt Differenzen zwischen zwei Objekten fest. Rückgabewert null
     * bedeutet, dass eine kein TOP-Level-Diff vorliegt.<br>
     *
     * @return wenn compareObject gegeben, aber referenceObject
     * nicht:{@link Additional} <br>
     * wenn referenceObject gegeben, aber compareObject nicht:{@link Missing}
     * <br>
     * wenn compareObject KEIN Terminal ist und ein IdentityResolver definiert
     * ist und dieser für compareObject und referenceObject unterschiedliche IDs
     * ergibt: {@link ReferenceDiff} <br>
     * wenn compareObject ein Terminal ist und
     * compareObject.equals(referenceObject) == false: {@link ValueDiff} <br>
     * wenn compareObject ein Terminal ist und
     * compareObject.equals(referenceObject) == true : {@link NoDiff} <br>
     * sonst: null (z.B. wenn compareObject ein Terminal ist und KEIN
     * IdentityResolver registriert ist. Dann muss später per Rekursion
     * verglichen werden)
     */
    private <T> DiffType determineToplevelDiff(T compareObject, T referenceObject) {
        if (compareObject == null && referenceObject == null) {
            // return null; // NICHT NoDiff !!!!!!!!!!! TODO warum nicht NoDiff ???
            return new NoDiff(compareObject, referenceObject);
        }

        if (compareObject == null) {
            return new Missing(referenceObject);
        }

        if (referenceObject == null) {
            return new Additional(compareObject);
        }

        boolean compareIsTerminal = this.compareToolConfiguration.isTerminal(compareObject);
        if (compareIsTerminal != this.compareToolConfiguration.isTerminal(referenceObject)) {
            throw new IllegalArgumentException("compareObject is terminal: " + compareIsTerminal + ", but referenceObject is terminal: "
                    + this.compareToolConfiguration.isTerminal(compareObject) + "! Must be either both terminal or both non-terminal.");
        }
        if (!compareIsTerminal) {
            if (this.compareToolConfiguration.hasIdentityResolver(compareObject.getClass())) {
                Long id1 = this.compareToolConfiguration.getID(compareObject);
                Long referenceId = this.compareToolConfiguration.getID(referenceObject);
                if (id1 == null || referenceId == null) {
                    // Wenn es sich um ein Non-Terminal handelt, für das kein Id-Resolver benutzt werden soll, liegt auch kein Ref-Dif vor
                    // Idee: Verwende HashCodeIdentifier, wenn Objekte equals überschreiben.
                    return null; // kein Unterschied feststellbar und auch nicht NoDiff feststellbar
                }
                if (id1.equals(referenceId)) {
                    return new NoDiff(compareObject, referenceObject);
                } else {
                    ReferenceDiff referenceChangedDiff = new ReferenceDiff(compareObject, referenceObject);
                    return referenceChangedDiff;
                }
            } else {
                // Wenn es sich um ein Non-Terminal handelt, für das kein Id-Resolver benutzt werden soll, liegt auch kein Ref-Dif vor
                // Idee: Verwende HashCodeIdentifier, wenn Objekte equals überschreiben.
                return null; // kein Unterschied feststellbar und auch nicht NoDiff feststellbar
            }
        } else if (!compareObject.equals(referenceObject)) {
            return new ValueDiff(compareObject, referenceObject);
        } else {
            return new NoDiff(compareObject, referenceObject);
        }
    }

    public CompareToolConfiguration configure() {
        return compareToolConfiguration;
    }

    private CompareToolConfiguration compareToolConfiguration;
    private PropertyTool propertyTool;
    /**
     * DiffMode: Füge dem Comparison nur die Properties hinzu, die Unterschiede
     * aufweisen. Andernfalls werden Properties immer hinzugefügt
     * ("Comparemode");
     */
    boolean diffMode = false;

    public <T> CompositeMerge<T> addMergeObject(CompositeComparison<T> compositeComparison) {
        CompositeMerge<T> result = new DefaultCompositeMerge(compositeComparison);

        return result;
    }

}
