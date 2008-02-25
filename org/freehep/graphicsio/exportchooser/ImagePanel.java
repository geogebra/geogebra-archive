// Copyright 2003, FreeHEP.
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;

import org.freehep.graphicsio.ImageConstants;
import org.freehep.swing.layout.TableLayout;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: ImagePanel.java,v 1.1 2008-02-25 21:17:59 murkle Exp $
 */
public class ImagePanel extends OptionPanel {
    public ImagePanel(Properties options, String rootKey, String[] formats) {
        super("Images");

        add(TableLayout.FULL, new JLabel("Write Images as"));
        ButtonGroup group = new ButtonGroup();
        OptionRadioButton imageType[] = new OptionRadioButton[formats.length];
        for (int i = 0; i < formats.length; i++) {
            imageType[i] = new OptionRadioButton(options, rootKey + "."
                    + ImageConstants.WRITE_IMAGES_AS, formats[i]);
            add(TableLayout.FULL, imageType[i]);
            group.add(imageType[i]);
            // add(TableLayout.RIGHT, new OptionTextField(options,
            // rootKey+"."+keys[i], 40));
        }
    }
}
