package org.jodt.property.gui.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

/**
 * @author oliver@stuch.net
 */
public class StringEditor extends DefaultCellEditor {

    public StringEditor() {
        super(new JTextField());
        JTextField inputField = (JTextField) getComponent();
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
            }
        });
    }

}
