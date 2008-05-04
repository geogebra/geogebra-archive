// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ExtFloodFill TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtFloodFill.java,v 1.3 2008-05-04 12:19:52 murkle Exp $
 */
public class ExtFloodFill extends EMFTag implements EMFConstants {

    private Point start;

    private Color color;

    private int mode;

    public ExtFloodFill() {
        super(53, 1);
    }

    public ExtFloodFill(Point start, Color color, int mode) {
        this();
        this.start = start;
        this.color = color;
        this.mode = mode;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ExtFloodFill(
            emf.readPOINTL(),
            emf.readCOLORREF(),
            emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writePOINTL(start);
        emf.writeCOLORREF(color);
        emf.writeDWORD(mode);
    }

    public String toString() {
        return super.toString() +
            "\n  start: " + start +
            "\n  color: " + color +
            "\n  mode: " + mode;
    }
}
