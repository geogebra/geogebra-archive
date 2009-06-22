// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * FillPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: FillPath.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class FillPath extends EMFTag {

    private Rectangle bounds;

    public FillPath() {
        super(62, 1);
    }

    public FillPath(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new FillPath(emf.readRECTL());
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
        GeneralPath currentPath = renderer.getPath();
        // fills the current path
        if (currentPath != null) {
            renderer.fillShape(currentPath);
            renderer.setPath(null);
        }
    }
}
