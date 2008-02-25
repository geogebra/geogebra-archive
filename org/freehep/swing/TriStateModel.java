// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing;

import javax.swing.JToggleButton;


/**
 *
 * @author Mark Donszelmann
 * @version $Id: TriStateModel.java,v 1.1 2008-02-25 21:17:47 murkle Exp $
 */
public class TriStateModel extends JToggleButton.ToggleButtonModel {
    
    boolean otherState;

    public TriStateModel () {
        super();
    }
    
    public int getTriState() {
        if (super.isSelected()) {
            if (!otherState) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }
    
    public void setTriState(int state) {
        setSelected((state == 0) ? false : true);
        otherState = (state == 1) ? true : false;
    }

    public void setSelected(boolean state) {
        otherState = state;
        super.setSelected(state);
    }
    
}
  
