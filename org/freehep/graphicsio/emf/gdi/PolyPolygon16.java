// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyPolygon16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyPolygon16.java,v 1.3 2008-05-04 12:18:36 murkle Exp $
 */
public class PolyPolygon16 extends AbstractPolyPolygon {

    private int numberOfPolys;

    public PolyPolygon16() {
        super(91, 1, null, null, null);
    }

    public PolyPolygon16(
        Rectangle bounds,
        int numberOfPolys,
        int[] numberOfPoints,
        Point[][] points) {

        super(91, 1, bounds, numberOfPoints,  points);
        this.numberOfPolys = numberOfPolys;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        Rectangle bounds = emf.readRECTL();
        int np = emf.readDWORD();
        /* int totalNumberOfPoints = */ emf.readDWORD();
        int[] pc = new int[np];
        Point[][] points = new Point[np][];

        for (int i = 0; i < np; i++) {
            pc[i] = emf.readDWORD();
            points[i] = new Point[pc[i]];
        }

        for (int i = 0; i < np; i++) {
            points[i] = emf.readPOINTS(pc[i]);
        }

        return new PolyPolygon16(bounds, np, pc, points);
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        int[] numberOfPoints = getNumberOfPoints();
        Point[][] points = getPoints();

        emf.writeRECTL(getBounds());
        emf.writeDWORD(numberOfPolys);
        int c = 0;
        for (int i = 0; i < numberOfPolys; i++) {
            c += getNumberOfPoints()[i];
        }
        emf.writeDWORD(c);
        for (int i = 0; i < numberOfPolys; i++) {
            emf.writeDWORD(numberOfPoints[i]);
        }
        for (int i = 0; i < numberOfPolys; i++) {
            emf.writePOINTS(numberOfPoints[i], points[i]);
        }
    }
}
