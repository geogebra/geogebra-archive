// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * @author Mark Donszelmann
 * @version $Id: TaggedOutput.java,v 1.1 2008-02-25 21:17:43 murkle Exp $
 */
public interface TaggedOutput {

    /**
     * Write a tag.
     * 
     * @param tag tag to write
     * @throws IOException if write fails
     */
    public void writeTag(Tag tag) throws IOException;

    /**
     * Close the stream
     * 
     * @throws IOException if close fails
     */
    public void close() throws IOException;
}
