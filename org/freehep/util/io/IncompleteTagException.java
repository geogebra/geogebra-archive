// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Exception for the TaggedInputStream. Signals that the inputstream contains
 * more bytes than the stream has read for this tag.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: IncompleteTagException.java,v 1.1 2008-02-25 21:17:43 murkle Exp $
 */
public class IncompleteTagException extends IOException {

    /**
     * 
     */
    private static final long serialVersionUID = -7808675150856818588L;

    private Tag tag;

    private byte[] rest;

    /**
     * Creates an Incomplete Tag Exception
     * 
     * @param tag incomplete tag
     * @param rest unused bytes
     */
    public IncompleteTagException(Tag tag, byte[] rest) {
        super("Tag " + tag + " contains " + rest.length + " unread bytes");
        this.tag = tag;
        this.rest = rest;
    }

    /**
     * @return tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * @return unused bytes
     */
    public byte[] getBytes() {
        return rest;
    }
}
