// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.UIManager;


/**
 *
 * @author Mark Donszelmann
 * @version $Id: JTriStateMenuItem.java,v 1.1 2008-02-25 21:17:47 murkle Exp $
 */
public class JTriStateMenuItem extends JCheckBoxMenuItem implements TriState {

    static {
        UIManager.getDefaults().put("TriStateMenuItemUI", "org.freehep.plaf.metal.MetalTriStateMenuItemUI");
    }   
    
    public JTriStateMenuItem () {
        this(null, null, 0);
    }

    public JTriStateMenuItem(Icon icon) {
        this(null, icon, 0);
    }
    
    public JTriStateMenuItem(Icon icon, int selected) {
        this(null, icon, selected);
    }
    
    public JTriStateMenuItem (String text) {
        this(text, null, 0);
    }

    public JTriStateMenuItem (String text, int selected) {
        this(text, null, selected);
    }

    public JTriStateMenuItem(String text, Icon icon) {
        this(text, icon, 0);
    }

    public JTriStateMenuItem (String text, Icon icon, int selected) {
        super(text, icon, false);
        setModel(new TriStateModel());
        setTriState(selected);
    }
    
    public int getTriState() {
        return ((TriStateModel)getModel()).getTriState();
    }
    
    public void setTriState(int state) {
        ((TriStateModel)getModel()).setTriState(state);
    }

    public void setTriState(boolean state) {
        setTriState((state) ? 1 : 0);
    }

    public String getUIClassID() {
        return "TriStateMenuItemUI";
    }
}
  
