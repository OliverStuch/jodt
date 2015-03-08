package org.jodt.property.implementation;

import java.util.Comparator;
import org.apache.log4j.Logger;
import org.jodt.property.Property;
import org.jodt.property.PropertyToolConfiguration;

public class PropertyValueComparator implements Comparator<Property> {

    private final PropertyToolConfiguration propertyToolConfiguration;

    public PropertyValueComparator(PropertyToolConfiguration propertyToolConfiguration) {
        this.propertyToolConfiguration = propertyToolConfiguration;
    }

    public int compare(Property o1, Property o2) {
//        logger.debug((o1 != null ? o1.value() : o1) + " vs " + (o2 != null ? o2.value() : o2));
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1.value() == o2.value()) {
            return 0;
        }
        if (o1.value() == null) {
            return -1;
        }
        Comparable o1id = propertyToolConfiguration.resolveId(o1);
        Comparable o2id = propertyToolConfiguration.resolveId(o2);
        return o1id.compareTo(o2id);
    }

    private static final Logger logger = Logger.getLogger(PropertyValueComparator.class);
}
