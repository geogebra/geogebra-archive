// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;

import javax.swing.JRadioButton;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionRadioButton.java,v 1.1 2008-02-25 21:17:59 murkle Exp $
 */
public class OptionRadioButton extends JRadioButton implements Options {
    protected boolean initialState;

    protected String key;

    public OptionRadioButton(Properties options, String key, String text) {
        super(text, new Boolean(options.getProperty(key, "false"))
                .booleanValue());
        this.key = key;
        initialState = isSelected();
    }

    public boolean applyChangedOptions(Properties options) {
        if (isSelected() != initialState) {
            options.setProperty(key, Boolean.toString(isSelected()));
            return true;
        }
        return false;
    }

    /**
     * Enables (otherwise disables) the supplied component if this radiobutton
     * is checked. Can be called for multiple components.
     */
    public void enables(final Component c) {
        if (c.isEnabled()) {
            c.setEnabled(isSelected());

            addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    c.setEnabled(isSelected());
                }
            });
        }
    }

    /**
     * Shows (otherwise hides) the supplied component if this radiobutton is
     * checked. Can be called for multiple components.
     */
    public void shows(final Component c) {
        c.setVisible(isSelected());

        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                c.setVisible(isSelected());
            }
        });
    }
}
