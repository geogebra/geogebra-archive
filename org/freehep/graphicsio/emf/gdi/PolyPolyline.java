// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyPolyline TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyPolyline.java,v 1.3 2008-05-04 12:19:41 murkle Exp $
 */
public class PolyPolyline extends AbstractPolyPolyline {

    private int start, end;

    public PolyPolyline() {
        super(7, 1, null, null, null);
    }

    public PolyPolyline(
        Rectangle bounds,
        int start,
        int end,
        int[] numberOfPoints,
        Point[][] points) {

        super(7, 1, bounds, numberOfPoints, points);

        this.start = start;
        this.end = Math.min(end, numberOfPoints.length - 1);
    }

    public EMFTag read(
        int tagID,
        EMFInputStream emf,
        int len)
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
            points[i] = emf.readPOINTL(pc[i]);
        }
        return new PolyPolyline(bounds, 0, np - 1, pc, points);
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        int[] numberOfPoints = getNumberOfPoints();
        Point[][] points = getPoints();

        emf.writeRECTL(getBounds());
        emf.writeDWORD(end - start + 1);
        int c = 0;
        for (int i = start; i < end + 1; i++) {
            c += numberOfPoints[i];
        }
        emf.writeDWORD(c);
        for (int i = start; i < end + 1; i++) {
            emf.writeDWORD(numberOfPoints[i]);
        }
        for (int i = start; i < end + 1; i++) {
            emf.writePOINTL(numberOfPoints[i], points[i]);
        }
    }
}
