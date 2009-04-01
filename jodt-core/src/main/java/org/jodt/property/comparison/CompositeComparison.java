package org.jodt.property.comparison;

import java.util.List;

/**
 * Repräsentiert den hierarchischen Vergleich zweier Objekte (CompareObjekt und ReferenceObjekt). Die zu vergleichenden Objekte müssen als Liste repräsentiert werden, damit man sie Property
 * für Property vergleichen kann.
 * 
 * Ein CompositeComparison kann eine Menge (Collection) von Children (Leafs) enthalten.
 * 
 * @author Oliver Stuch (oliver@stuch.net)
 * 
 */
// Collection oder List ?
public interface CompositeComparison<T> extends Comparison<T>, PropertyComparison<T>, List<CompositeComparison<?>> {

    /**
     * get parent
     */
    public CompositeComparison<?> parent();

    /**
     * set parent
     */
    public void parent(CompositeComparison<?> parent);

    /**
     * get children
     */
    public List<CompositeComparison<?>> children();

    /**
     * get children with diff
     */
    public List<CompositeComparison<?>> childrenWithDiffsOnObjectLevel();

    public List<CompositeComparison<?>> recursiveChildrenWithDiffsOnObjectLevel(CompositeComparison<?> comparison);

}
