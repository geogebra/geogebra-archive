// Copyright 2002, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Listener to inform that Prompt of the PromptInputStream has been found.
 * 
 * @author Mark Donszelmann
 * @version $Id: PromptListener.java,v 1.1 2008-02-25 21:17:41 murkle Exp $
 */
public interface PromptListener {

    /**
     * Prompt was found, and can now be read.
     * 
     * @param route stream for reading prompt (and more)
     * @throws IOException if read fails
     */
    public void promptFound(RoutedInputStream.Route route) throws IOException;
}
