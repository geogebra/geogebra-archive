// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * Rectangle TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: EMFRectangle.java,v 1.1 2008-02-25 21:17:07 murkle Exp $
 */
public class EMFRectangle extends EMFTag {

    private Rectangle bounds;

    public EMFRectangle() {
        super(43, 1);
    }

    public EMFRectangle(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new EMFRectangle(emf.readRECTL());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeRECTL(bounds);
    }

    public String toString() {
        return super.toString() + "\n  bounds: " + bounds;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.fillAndDrawOrAppend(bounds);
    }
}
