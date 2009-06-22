// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * Ellipse TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: Ellipse.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class Ellipse extends EMFTag {
    private Rectangle bounds;

    public Ellipse(Rectangle bounds) {
        this();
        this.bounds = bounds;
    }

    public Ellipse() {
        super(42, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new Ellipse(emf.readRECTL());
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
        // The Ellipse function draws an ellipse. The center of the ellipse
        // is the center of the specified bounding rectangle.
        // The ellipse is outlined by using the current pen and is filled by
        // using the current brush.
        // The current position is neither used nor updated by Ellipse.
        renderer.fillAndDrawOrAppend(new Ellipse2D.Double(
            bounds.getX(),
            bounds.getY(),
            bounds.getWidth(),
            bounds.getHeight()));
    }
}
