// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyPolyline16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyPolyline16.java,v 1.3 2008-05-04 12:19:36 murkle Exp $
 */
public class PolyPolyline16 extends AbstractPolyPolyline {

    private int numberOfPolys;

    public PolyPolyline16() {
        super(90, 1, null, null, null);
    }

    public PolyPolyline16(
        Rectangle bounds,
        int numberOfPolys,
        int[] numberOfPoints,
        Point[][] points) {

        super(90, 1, bounds, numberOfPoints, points);
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
        return new PolyPolyline16(bounds, np, pc, points);
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        int[] numberOfPoints = getNumberOfPoints();
        Point[][] points = getPoints();
        
        emf.writeRECTL(getBounds());
        emf.writeDWORD(numberOfPolys);
        int c = 0;
        for (int i = 0; i < numberOfPolys; i++) {
            c += numberOfPoints[i];
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
