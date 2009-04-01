package org.jodt.property.comparison;

/**
 * @author Oliver Stuch
 */
public interface IndexMapper {
    
    void addMapping(int index, int mappedIndex);
    
    boolean isMapped(int index);

    int map(int index);

    int size();

    final int MISSING = -1;

    void clear();

}
