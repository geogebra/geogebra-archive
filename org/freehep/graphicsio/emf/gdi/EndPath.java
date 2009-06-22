// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * EndPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: EndPath.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class EndPath extends EMFTag {

    public EndPath() {
        super(60, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // TODO: fix EMFGraphics2D?
        // this happens only when EMF is created by EMFGraphics2D
        // there could be an open figure (created with LineTo, PolylineTo etc.)
        // that is not closed and therefore not written to the currentPath
        renderer.closeFigure();

        // The EndPath function closes a path bracket and selects the path
        // defined by the bracket into the specified device context.
        renderer.closePath();
    }
}
