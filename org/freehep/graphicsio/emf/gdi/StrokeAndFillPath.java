// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * StrokeAndFillPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: StrokeAndFillPath.java,v 1.1 2008-02-25 21:17:01 murkle Exp $
 */
public class StrokeAndFillPath extends EMFTag {

    private Rectangle bounds;

    public StrokeAndFillPath() {
        super(63, 1);
    }

    public StrokeAndFillPath(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new StrokeAndFillPath(emf.readRECTL());
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
            renderer.drawShape(currentPath);
            renderer.setPath(null);
        }
    }
}
