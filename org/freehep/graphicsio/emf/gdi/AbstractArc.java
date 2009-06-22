// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * @author Steffen Greiffenberg
 * @version $Id: AbstractArc.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public abstract class AbstractArc extends EMFTag {

    private Rectangle bounds;

    private Point start, end;

    protected AbstractArc(int id, int version, Rectangle bounds, Point start, Point end) {
        super(id, version);
        this.bounds = bounds;
        this.start = start;
        this.end = end;
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeRECTL(bounds);
        emf.writePOINTL(start);
        emf.writePOINTL(end);
    }

    public String toString() {
        return super.toString() +
            "\n  bounds: " + bounds +
            "\n  start: " + start +
            "\n  end: " + end;
    }

    /**
     * creates a shape based on bounds, start and end
     *
     * @param renderer EMFRenderer storing the drawing session data
     * @param arcType type of arc, e.g. {@link Arc2D#OPEN}
     * @return shape to render 
     */
    protected Shape getShape(EMFRenderer renderer, int arcType) {
        // normalize start and end point to a circle
        double nx0 = start.getX() / bounds.getWidth();

        // double ny0 = arc.getStart().y / arc.getBounds().height;
        double nx1 = end.getX() / bounds.getWidth();

        // double ny1 = arc.getEnd().y / arc.getBounds().height;
        // calculate angle of start point
        double alpha0, alpha1;
        if (renderer.getArcDirection() == EMFConstants.AD_CLOCKWISE) {
            alpha0 = Math.acos(nx0);
            alpha1 = Math.acos(nx1);
        } else {
            alpha0 = Math.acos(nx1);
            alpha1 = Math.acos(nx0);
        }

        return new Arc2D.Double(
            start.getX(),
            start.getY(),
            bounds.getWidth(),
            bounds.getHeight(),
            alpha0,
            alpha1 - alpha0,
            arcType);
    }
}