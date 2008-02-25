// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing;



/**
 * @author Mark Donszelmann
 * @version $Id: TriState.java,v 1.1 2008-02-25 21:17:46 murkle Exp $
 */
public interface TriState {

    public int getTriState(); 
    
    public void setTriState(int state);
    public void setTriState(boolean state);    
}
  
