package org.jodt.property.gui.editors;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;



public class FloatEditor extends RevertableEditor {
    NumberFormat format;
    private Float minimum;
    private Float maximum;
    private boolean DEBUG = false;
    
    public FloatEditor() {
        this(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public FloatEditor(float min, float max) {
        super();
        formattedTextField = (JFormattedTextField)getComponent();
        minimum = new Float(min);
        maximum = new Float(max);
        //Set up the editor for the double cells.
        format = new DecimalFormat();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setFormat(format);
        formatter.setMinimum(minimum);
        formatter.setMaximum(maximum);
        formattedTextField.setFormatterFactory(new DefaultFormatterFactory(formatter));
        formattedTextField.setValue(minimum);
    }

    //Override to ensure that the value remains an Float.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if (o instanceof Float) {
            return o;
        } else if (o instanceof Number) {
            return new Float(((Number)o).floatValue());
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Float");
            }
            try {
                return format.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
    
    public String getErrorText() {
        return "The value must be an floating point value between " + minimum + " and " + maximum + ".\n";
    }
}
