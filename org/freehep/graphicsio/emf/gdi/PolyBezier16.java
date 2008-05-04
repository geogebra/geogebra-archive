// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * PolyBezier16 TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: PolyBezier16.java,v 1.3 2008-05-04 12:19:56 murkle Exp $
 */
public class PolyBezier16 extends PolyBezier {

    public PolyBezier16() {
        super(85, 1, null, 0, null);
    }

    public PolyBezier16(Rectangle bounds, int numberOfPoints, Point[] points) {
        super(85, 1, bounds, numberOfPoints, points);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        Rectangle r = emf.readRECTL();
        int n = emf.readDWORD();
        return new PolyBezier16(r, n, emf.readPOINTS(n));
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeRECTL(getBounds());
        emf.writeDWORD(getNumberOfPoints());
        emf.writePOINTS(getNumberOfPoints(), getPoints());
    }
}
