// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetBkMode TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetBkMode.java,v 1.1 2008-02-25 21:17:08 murkle Exp $
 */
public class SetBkMode extends EMFTag implements EMFConstants {

    private int mode;

    public SetBkMode() {
        super(18, 1);
    }

    public SetBkMode(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetBkMode(emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(mode);
    }

    public String toString() {
        return super.toString() + "\n  mode: " + mode;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // The SetBkMode function affects the line styles for lines drawn using a
        // pen created by the CreatePen function. SetBkMode does not affect lines
        // drawn using a pen created by the ExtCreatePen function.
        renderer.setBkMode(mode);
    }
}
