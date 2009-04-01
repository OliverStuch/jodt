package org.jodt.property.comparison;

import java.util.List;

/**
 * @author Oliver Stuch
 */

public interface CompositeMerge<T> extends Merge<T>,PropertyMerge<T>, List<CompositeMerge<?>>  {
    

    /**
     * get parent
     */
    public CompositeMerge<?> parent();
    /**
     * set parent
     */
    public void parent(CompositeMerge<?> parent);
    /**
     * get children
     */
    public  List<CompositeMerge<?>> children();
   
}
