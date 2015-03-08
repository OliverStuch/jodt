package org.jodt.property.implementation;

import java.util.Comparator;
import org.apache.log4j.Logger;
import org.jodt.property.Property;

public class PropertyNameComparator implements Comparator<Property> {

    public int compare(Property o1, Property o2) {
//        logger.debug((o1 != null ? o1.name() : o1) + " vs " + (o2 != null ? o2.name() : o2) );
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.name() == o2.name()) {
            return 0;
        }
        if (o1.name() == null) {
            return -1;
        }
        return o1.name().compareTo(o2.name());
    }

    private static final Logger logger = Logger.getLogger(PropertyNameComparator.class);

}
