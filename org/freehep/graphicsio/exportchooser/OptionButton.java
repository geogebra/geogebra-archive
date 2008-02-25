// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: OptionButton.java,v 1.1 2008-02-25 21:18:00 murkle Exp $
 */
public class OptionButton extends JButton implements Options {

    protected String key;

    public OptionButton(Properties options, String key, String text,
            final JDialog dialog) {
        super(text);
        this.key = key;
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(true);
                dialog.dispose();
            }
        });
    }

    public boolean applyChangedOptions(Properties options) {
        return false;
    }

}
