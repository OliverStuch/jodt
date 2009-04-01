package org.jodt.util.gui;

import org.apache.log4j.Logger;
import org.jodt.util.Registry;

public class Factory<T> {
    /**
     * @return A new object of Class clazz or null, if no object could be created
     */
    public T create(Class clazz) {
        Creator<T> creator = creatorRegistry.getImplementation(clazz);
        if (creator == null) {
            logger.warn("No creator found for " + clazz);
            creator = creatorRegistry.getImplementation(Object.class);
        }
        return creator.create();
        // return creator != null ? creator.create() : null;
    }

    public void register(Class clazz, Creator<T> creator) {
        creatorRegistry.setImplementation(clazz, creator);
    }

    public interface Creator<T> {
        public T create();
    }

    private Registry<Creator<T>> creatorRegistry = new Registry<Creator<T>>();
    private static Logger logger = Logger.getLogger(Factory.class);
}
