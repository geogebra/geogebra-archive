// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import org.freehep.graphicsio.emf.EMFRenderer;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Parent class for a group of PolyLines. Childs are
 * rendered as not closed polygons.
 *
 * @author Steffen Greiffenberg
 * @version $Id: AbstractPolyPolyline.java,v 1.1 2008-02-25 21:17:00 murkle Exp $§
 */
public abstract class AbstractPolyPolyline extends AbstractPolyPolygon {

    protected AbstractPolyPolyline(
        int id,
        int version,
        Rectangle bounds,
        int[] numberOfPoints,
        Point[][] points) {

        super(id, version, bounds, numberOfPoints, points);
    }

    /**
     * displays the tag using the renderer. The default behavior
     * is not to close the polygons and not to fill them.
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        render(renderer, false);
    }
}
