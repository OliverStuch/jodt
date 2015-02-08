package org.jodt.util;

import java.util.Locale;

/**
 *
 * @author OliverStuch
 */
public class CapitalizeToStringRenderer implements ToStringRenderer<String> {

    @Override
    public String render2String(String t) {
        if (t == null) {
            return null;
        }
        switch (t.length()) {
            case 0:
                return t;
            case 1:
                return t.toUpperCase(Locale.GERMAN);
            default:
                return t.substring(0, 1).toUpperCase(Locale.GERMAN) + t.substring(1);
        }
    }

}
