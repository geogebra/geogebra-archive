// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetBkColor TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetBkColor.java,v 1.1 2008-02-25 21:17:07 murkle Exp $
 */
public class SetBkColor extends EMFTag {

    private Color color;

    public SetBkColor() {
        super(25, 1);
    }

    public SetBkColor(Color color) {
        this();
        this.color = color;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetBkColor(emf.readCOLORREF());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeCOLORREF(color);
    }

    public String toString() {
        return super.toString() + "\n  color: " + color;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // This function fills the gaps between styled lines drawn using a
        // pen created by the CreatePen function; it does not fill the gaps
        // between styled lines drawn using a pen created by the ExtCreatePen
        // function. The SetBKColor function also sets the background colors
        // for TextOut and ExtTextOut.

        // If the background mode is OPAQUE, the background color is used to
        // fill gaps between styled lines, gaps between hatched lines in brushes,
        // and character cells. The background color is also used when converting
        // bitmaps from color to monochrome and vice versa.

        // TODO: affects TextOut and ExtTextOut, CreatePen
    }
}
