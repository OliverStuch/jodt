package org.jodt.property.gui.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Dieser Editor ermöglicht generell, auf den vorherigen Wert eines bereits editierten Wertes zurückzugehen (revert)
 */
public abstract class RevertableEditor extends DefaultCellEditor {

    public RevertableEditor() {
        super(new JFormattedTextField());
        formattedTextField = (JFormattedTextField) getComponent();
        formattedTextField.setHorizontalAlignment(JTextField.LEADING);
        formattedTextField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        formattedTextField.setCaretColor(new Color(0,200,0));
        // React when the user presses Enter while the editor is
        // active. (Tab is handled as specified by
        // JFormattedTextField's focusLostBehavior property.)
        formattedTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
        formattedTextField.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!formattedTextField.isEditValid()) { // The text is invalid.
                    if (userSaysRevert()) { // reverted
                        formattedTextField.postActionEvent(); // inform the editor
                    }
                } else
                    try { // The text is valid,
                        formattedTextField.commitEdit(); // so use it.
                        formattedTextField.postActionEvent(); // stop editing
                    } catch (java.text.ParseException exc) {
                    }
            }
        });
    }

    // Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        ftf.setValue(value);
        return ftf;
    }

    // Override to check whether the edit is valid,
    // setting the value if it is and complaining if
    // it isn't. If it's OK for the editor to go
    // away, we need to invoke the superclass's version
    // of this method so that everything gets cleaned up.
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        if (ftf.isEditValid()) {
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) {
            }

        } else { // text is invalid
            if (!userSaysRevert()) { // user wants to edit
                return false; // don't let the editor go away
            }
        }
        return super.stopCellEditing();
    }

    /**
     * Lets the user know that the text they entered is bad. Returns true if the user elects to revert to the last good value. Otherwise, returns false, indicating that the user wants to
     * continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        formattedTextField.selectAll();
        Object[] options = { "Edit", "Revert" };
        int answer = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(formattedTextField), getErrorText() + "You can either continue editing "
                + "or revert to the last valid value.", "Invalid Text Entered", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);

        if (answer == 1) { // Revert!
            formattedTextField.setValue(formattedTextField.getValue());
            return true;
        }
        return false;
    }

    JFormattedTextField formattedTextField;
    public abstract String getErrorText();
}
