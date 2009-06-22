// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SetROP2 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetROP2.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class SetROP2 extends EMFTag implements EMFConstants {

    private int mode;

    public SetROP2() {
        super(20, 1);
    }

    public SetROP2(int mode) {
        this();
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetROP2(emf.readDWORD());
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
        // The SetROP2 function sets the current foreground mix mode.
        // GDI uses the foreground mix mode to combine pens and interiors
        // of filled objects with the colors already on the screen. The
        // foreground mix mode defines how colors from the brush or pen
        // and the colors in the existing image are to be combined.
        renderer.setRop2(mode);
    }
}
