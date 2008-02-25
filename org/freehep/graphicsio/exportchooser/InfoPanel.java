// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

import javax.swing.JLabel;

import org.freehep.swing.layout.TableLayout;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: InfoPanel.java,v 1.1 2008-02-25 21:18:00 murkle Exp $
 */
public class InfoPanel extends OptionPanel {
    public InfoPanel(Properties options, String rootKey, String[] keys) {
        super("Info");

        for (int i = 0; i < keys.length; i++) {
            add(TableLayout.LEFT, new JLabel(keys[i]));
            add(TableLayout.RIGHT, new OptionTextField(options, rootKey + "."
                    + keys[i], 40));
        }
    }
}
