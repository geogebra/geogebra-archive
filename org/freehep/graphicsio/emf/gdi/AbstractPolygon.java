// Copyright 2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFOutputStream;

import java.awt.Rectangle;
import java.awt.Point;
import java.io.IOException;

/**
 * @author Steffen Greiffenberg
 * @version $Id: AbstractPolygon.java,v 1.3 2008-05-04 12:19:55 murkle Exp $
 */
public abstract class AbstractPolygon extends EMFTag {

    private Rectangle bounds;

    private int numberOfPoints;

    private Point[] points;

    protected AbstractPolygon(int id, int version) {
        super(id, version);
    }

    protected AbstractPolygon(int id, int version, Rectangle bounds, int numberOfPoints, Point[] points) {
        super(id, version);
        this.bounds = bounds;
        this.numberOfPoints = numberOfPoints;
        this.points = points;
    }


    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeRECTL(bounds);
        emf.writeDWORD(numberOfPoints);
        emf.writePOINTL(numberOfPoints, points);
    }

    public String toString() {
        String result = super.toString() +
            "\n  bounds: " + bounds +
            "\n  #points: " + numberOfPoints;
        if (points != null) {
            result += "\n  points: ";
            for (int i = 0; i < points.length; i++) {
                result += "[" + points[i].x + "," + points[i].y + "]";
                if (i < points.length - 1) {
                    result += ", ";
                }
            }
        }
        return result;
    }

    protected Rectangle getBounds() {
        return bounds;
    }

    protected int getNumberOfPoints() {
        return numberOfPoints;
    }

    protected Point[] getPoints() {
        return points;
    }
}
