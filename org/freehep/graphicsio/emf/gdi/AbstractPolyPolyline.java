// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;

import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * Parent class for a group of PolyLines. Childs are
 * rendered as not closed polygons.
 *
 * @author Steffen Greiffenberg
 * @version $Id: AbstractPolyPolyline.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $§
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
