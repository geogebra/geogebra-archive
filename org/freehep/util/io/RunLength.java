// Copyright 2001, FreeHEP.
package org.freehep.util.io;

/**
 * Constants for the RunLength encoding.
 * 
 * @author Mark Donszelmann
 * @version $Id: RunLength.java,v 1.1 2008-02-25 21:17:40 murkle Exp $
 */
public interface RunLength {

    /**
     * Maximum run length
     */
    public static final int LENGTH = 128;

    /**
     * End of data code
     */
    public static final int EOD = 128;

}
