// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ResizePalette TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ResizePalette.java,v 1.3 2008-05-04 12:19:53 murkle Exp $
 */
public class ResizePalette extends EMFTag {

    private int index, entries;

    public ResizePalette() {
        super(51, 1);
    }

    public ResizePalette(int index, int entries) {
        this();
        this.index = index;
        this.entries = entries;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ResizePalette(emf.readDWORD(), emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
        emf.writeDWORD(entries);
    }

    public String toString() {
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index) +
            "\n  entries: " + entries;
    }
}
