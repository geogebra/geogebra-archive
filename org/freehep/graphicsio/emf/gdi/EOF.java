// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * Rectangle TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: EOF.java,v 1.1 2008-02-25 21:17:05 murkle Exp $
 */
public class EOF extends EMFTag {

    public EOF() {
        super(14, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        /* int[] bytes = */ emf.readUnsignedByte(len);
        return new EOF();
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(0); // # of palette entries
        emf.writeDWORD(0x10); // offset for palette
        // ... palette
        emf.writeDWORD(0x14); // offset to start of record
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // do nothing
    }
}
