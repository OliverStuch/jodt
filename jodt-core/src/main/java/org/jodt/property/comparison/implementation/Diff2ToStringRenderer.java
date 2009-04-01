package org.jodt.property.comparison.implementation;

import org.jodt.util.Registry;
import org.jodt.util.ToStringRenderer;

/**
 * @author Oliver Stuch
 */

public class Diff2ToStringRenderer extends Registry<ToStringRenderer> {
    public Diff2ToStringRenderer() {
        register(NoDiff.class, new ToStringRenderer<NoDiff>() {
            public String render2String(NoDiff diff) {
                return "no diff";
            }
        });
        register(ReferenceDiff.class, new ToStringRenderer<ReferenceDiff>() {
            public String render2String(ReferenceDiff diff) {
                return "reference diff";
            }
        });
        register(Additional.class, new ToStringRenderer<Additional>() {
            public String render2String(Additional diff) {
                return "additional";
            }
        });
        register(Missing.class, new ToStringRenderer<Missing>() {
            public String render2String(Missing diff) {
                return "missing";
            }
        });
        register(ValueDiff.class, new ToStringRenderer<ValueDiff>() {
            public String render2String(ValueDiff diff) {
                return "value diff";
            }
        });
    }
}
