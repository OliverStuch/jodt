package org.jodt.property.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;


import org.jdesktop.swingx.JXTreeTable;
import org.jodt.property.Property;
import org.jodt.util.gui.treetable.DefaultJXTreeTable.DefaultJXTreeTableModel;

/**
 * @author Oliver Stuch  (oliver@stuch.net) 
 */

public class ContextMenuFactory {
    public void setupContextMenu(final JXTreeTable component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (component.isEditable()) {
                    // if (evt.isPopupTrigger()) {
                    if (evt.getButton() == 3) { // Das kranke Windows ...
                        // logger.debug(evt.getButton());
                        TreePath popupMenuTreePath = component.getPathForLocation(evt.getX(), evt.getY());
                        createDynamicContextMenu("you may...", popupMenuTreePath, (DefaultJXTreeTableModel) component.getTreeTableModel()).show(evt.getComponent(), evt.getX(), evt.getY());
                    }
                }
            }

            private JPopupMenu createDynamicContextMenu(String title, TreePath popupMenuTreePath, final DefaultJXTreeTableModel treeTableModel) {
                JPopupMenu result = new JPopupMenu(title);
                result.setBorder(new TitledBorder(title));
                int optionCount = 0;
                final MutableTreeTablePropertyNode node = (MutableTreeTablePropertyNode) popupMenuTreePath.getLastPathComponent();
                final Property property = node.getProperty();
                final MutableTreeTablePropertyNode parentNode = (MutableTreeTablePropertyNode) node.getParent();
//                final CompositeProperty parentProperty = parentNode.getProperty();
                // option: SET property of node with new instance

                try {
                    final Constructor defaultConstructor = property.type().getConstructor(new Class[] {});
                    if (defaultConstructor != null) {
                        JMenuItem menuItem = new JMenuItem("set node value (" + property.type().getName() + " created by default constructor)");
                        menuItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                try {
                                    treeTableModel.setValue(defaultConstructor.newInstance(), node);
//                                    treeTableModel.setValue(defaultConstructor.newInstance(), property, node, parentProperty, parentNode);
                                } catch (IllegalArgumentException e1) {
                                    // TODO Auto-generated catch block
                                    throw new RuntimeException(e1);
                                } catch (InstantiationException e1) {
                                    // TODO Auto-generated catch block
                                    throw new RuntimeException(e1);
                                } catch (IllegalAccessException e1) {
                                    // TODO Auto-generated catch block
                                    throw new RuntimeException(e1);
                                } catch (InvocationTargetException e1) {
                                    // TODO Auto-generated catch block
                                    throw new RuntimeException(e1);
                                }
                            }
                        });
                        optionCount++;
                        result.add(menuItem);
                    }
                } catch (SecurityException e) {
                    // defaultConstructor not accessible
                } catch (NoSuchMethodException e) {
                    // defaultConstructor not accessible
                }
                
                if (property.value() != null && !property.type().isPrimitive()) {

                    JMenuItem menuItem = new JMenuItem("set " + property.name() + " of type " + property.type().getName() + " to null");
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            treeTableModel.setValue(null, node);
                        }
                    });
                    optionCount++;
                    result.add(menuItem);
                }

                if (optionCount == 0) {
                    JMenuItem menuItem = new JMenuItem("sorry ... no options available");
                    result.add(menuItem);
                }
                return result;
            }
        });
    }

}
