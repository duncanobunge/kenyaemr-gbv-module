package org.openmrs.module.gbv.context;

import java.util.HashMap;

/**
 * Simple Context Model that an app can use to provide context when requesting extensions, etc
 * from the AppFrameworkService
 */
public class GbvAppContextModel extends HashMap<String, Object> {

    /**
     * @param key
     * @param value
     * @return a shallow copy, with key->value added
     */
    public GbvAppContextModel with(String key, Object value) {
        GbvAppContextModel clone = new GbvAppContextModel();
        clone.putAll(this);
        clone.put(key, value);
        return clone;
    }

    /**
     * @param key
     * @return a shallow copy, with 'key' removed
     */
    public GbvAppContextModel without(String key) {
        GbvAppContextModel clone = new GbvAppContextModel();
        clone.putAll(this);
        clone.remove(key);
        return clone;
    }

}
