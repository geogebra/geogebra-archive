// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF GradientTriangle
 * 
 * @author Mark Donszelmann
 * @version $Id: GradientTriangle.java,v 1.3 2008-05-04 12:18:47 murkle Exp $
 */
public class GradientTriangle extends Gradient {

    private int vertex1, vertex2, vertex3;

    public GradientTriangle(int vertex1, int vertex2, int vertex3) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.vertex3 = vertex3;
    }

    public GradientTriangle(EMFInputStream emf) throws IOException {
        vertex1 = emf.readULONG();
        vertex2 = emf.readULONG();
        vertex3 = emf.readULONG();
    }

    public void write(EMFOutputStream emf) throws IOException {
        emf.writeULONG(vertex1);
        emf.writeULONG(vertex2);
        emf.writeULONG(vertex3);
    }

    public String toString() {
        return "  GradientTriangle: " + vertex1 + ", " + vertex2 + ", "
                + vertex3;
    }
}
