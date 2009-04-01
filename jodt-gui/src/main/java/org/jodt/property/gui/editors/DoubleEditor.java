package org.jodt.property.gui.editors;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;



public class DoubleEditor extends RevertableEditor {
    NumberFormat doubleFormat;
    private Double minimum;
    private Double maximum;
    private boolean DEBUG = false;
    
    public DoubleEditor() {
        this(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleEditor(double min, double max) {
        super();
        formattedTextField = (JFormattedTextField)getComponent();
        minimum = new Double(min);
        maximum = new Double(max);
        //Set up the editor for the double cells.
        doubleFormat = new DecimalFormat();
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);
        doubleFormatter.setMinimum(minimum);
        doubleFormatter.setMaximum(maximum);
        formattedTextField.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter));
        formattedTextField.setValue(minimum);
    }

    //Override to ensure that the value remains an Doble.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if (o instanceof Double) {
            return o;
        } else if (o instanceof Number) {
            return new Double(((Number)o).doubleValue());
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Double");
            }
            try {
                return doubleFormat.parseObject(o.toString());
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
