// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing.plaf.metal;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;




/**
 *
 * @author Mark Donszelmann
 * @version $Id: MetalTriStateMenuItemUI.java,v 1.1 2008-02-25 21:18:32 murkle Exp $
 */
public class MetalTriStateMenuItemUI extends BasicCheckBoxMenuItemUI {
    
    public static ComponentUI createUI(JComponent b) {
        return new MetalTriStateMenuItemUI();
    }

    public String getPropertyPrefix() {
	    return "CheckBoxMenuItem";
    }

    public void installDefaults() {
       	super.installDefaults();
     	checkIcon = new CheckBoxMenuItemIcon();
    }
}
