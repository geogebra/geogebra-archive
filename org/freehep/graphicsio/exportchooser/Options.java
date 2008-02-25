// Copyright 2003, FreeHEP
package org.freehep.graphicsio.exportchooser;

import java.util.Properties;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: Options.java,v 1.1 2008-02-25 21:17:59 murkle Exp $
 */
public interface Options {

    /**
     * Sets all the changed options in the properties object.
     * 
     * @return true if any options were set
     */
    public boolean applyChangedOptions(Properties options);
}
