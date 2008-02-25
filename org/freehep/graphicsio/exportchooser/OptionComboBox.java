// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionComboBox.java,v 1.1 2008-02-25 21:18:00 murkle Exp $
 */
public class OptionComboBox extends JComboBox implements Options {
    protected String initialSelectedItem;

    protected String key;

    public OptionComboBox(Properties options, String key, String[] values) {
        super(values);
        setSelectedItem(options.getProperty(key, values[0]));
        setEnabled(values.length > 1);
        this.key = key;
        initialSelectedItem = (String) getSelectedItem();
    }

    public boolean applyChangedOptions(Properties options) {
        if (!getSelectedItem().equals(initialSelectedItem)) {
            options.setProperty(key, (String) getSelectedItem());
            return true;
        }
        return false;
    }

    /**
     * Enables (otherwise disables) the supplied component if this item is
     * checked. Can be called for multiple components.
     */
    public void enables(final String item, final Component c) {
        if (c.isEnabled()) {
            c.setEnabled(getSelectedItem().equals(item));

            addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    c.setEnabled(getSelectedItem().equals(item));
                }
            });
        }
    }

    /**
     * Shows (otherwise hides) the supplied component if this item is selected.
     * Can be called for multiple components.
     */
    public void shows(final String item, final Component c) {
        c.setVisible(getSelectedItem().equals(item));

        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                c.setVisible(getSelectedItem().equals(item));
            }
        });
    }

    /**
     * Selects (or deselects) the supplied abstract button if this item is
     * selected. Can be called for multiple components and items.
     */
    public void selects(final String item, final AbstractButton c) {
        c.setSelected(getSelectedItem().equals(item));

        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                c.setSelected(getSelectedItem().equals(item));
            }
        });
    }
}
