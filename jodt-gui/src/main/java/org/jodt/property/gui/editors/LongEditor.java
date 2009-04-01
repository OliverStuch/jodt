package org.jodt.property.gui.editors;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;


import org.apache.log4j.Logger;

public class LongEditor extends RevertableEditor {
    NumberFormat integerFormat;
    private Long minimum;
    private Long maximum;


    public LongEditor() {
        this(Long.MIN_VALUE, Integer.MAX_VALUE);
    }

    public LongEditor(long min, long max) {
        super();
        formattedTextField = (JFormattedTextField)getComponent();
        minimum = new Long(min);
        maximum = new Long(max);
        //Set up the editor for the integer cells.
        integerFormat = NumberFormat.getIntegerInstance();
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setFormat(integerFormat);
        intFormatter.setMinimum(minimum);
        intFormatter.setMaximum(maximum);
        formattedTextField.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        formattedTextField.setValue(minimum);
    }

    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if (o instanceof Long) {
            return o;
        } else if (o instanceof Number) {
            return new Integer(((Number)o).intValue());
        } else {
            try {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                logger.error("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
    
    public String getErrorText() {
        return "The value must be an integer between " + minimum + " and " + maximum + ".\n";
    }
    
   final private static Logger logger = Logger.getLogger(IntegerEditor.class);
}
