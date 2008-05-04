// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * EMF LogPen
 * 
 * @author Mark Donszelmann
 * @version $Id: LogPen.java,v 1.3 2008-05-04 12:17:35 murkle Exp $
 */
public class LogPen extends AbstractPen {

    private int penStyle;

    private int width;

    private Color color;

    public LogPen(int penStyle, int width, Color color) {
        this.penStyle = penStyle;
        this.width = width;
        this.color = color;
    }

    public LogPen(EMFInputStream emf) throws IOException {
        penStyle = emf.readDWORD();
        width = emf.readDWORD();
        /* int y = */ emf.readDWORD();
        color = emf.readCOLORREF();
    }

    public void write(EMFOutputStream emf) throws IOException {
        emf.writeDWORD(penStyle);
        emf.writeDWORD(width);
        emf.writeDWORD(0);
        emf.writeCOLORREF(color);
    }

    public String toString() {
        return "  LogPen\n" + "    penstyle: " + penStyle +
            "\n    width: " + width +
            "\n    color: " + color;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.setUseCreatePen(true);
        renderer.setPenPaint(color);
        renderer.setPenStroke(
            createStroke(renderer,  penStyle, null, width));
    }
}
