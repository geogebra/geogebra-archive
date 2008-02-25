// Copyright 2001, FreeHEP.
package org.freehep.util.io;

import java.io.IOException;

/**
 * Tag to hold the data for an Undefined Tag for the TaggedIn/OutputStreams. The
 * data is read in and written as the number of bytes is known.
 * 
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: UndefinedTag.java,v 1.1 2008-02-25 21:17:40 murkle Exp $
 */
public class UndefinedTag extends Tag {

    private int[] bytes;

    /**
     * Create Undefined Tag with 0 length.
     */
    public UndefinedTag() {
        this(DEFAULT_TAG, new int[0]);
    }
    
    /**
     * Create Undefined Tag.
     * 
     * @param tagID undefined tagID
     * @param bytes bytes that follow the undefined tag
     */
    public UndefinedTag(int tagID, int[] bytes) {
        super(tagID, 3);
        this.bytes = bytes;
    }

    public int getTagType() {
        return 0;
    }

    public Tag read(int tagID, TaggedInputStream input, int len)
            throws IOException {

        int[] bytes = input.readUnsignedByte(len);
        UndefinedTag tag = new UndefinedTag(tagID, bytes);
        return tag;
    }

    public void write(int tagID, TaggedOutputStream output) throws IOException {

        output.writeUnsignedByte(bytes);
    }

    public String toString() {
        return ("UNDEFINED TAG: " + getTag() + " length: " + bytes.length);
    }
}
