// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SelectPalette TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SelectPalette.java,v 1.1 2008-02-25 21:17:10 murkle Exp $
 */
public class SelectPalette extends EMFTag {

    private int index;

    public SelectPalette() {
        super(48, 1);
    }

    public SelectPalette(int index) {
        this();
        this.index = index;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SelectPalette(emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
    }

    public String toString() {
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index);
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // The SelectPalette function selects the specified
        // logical palette into a device context.

        // TODO needs CreatePalette and CreatePalletteIndex to work
    }
}
