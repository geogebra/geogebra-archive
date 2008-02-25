// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetTextColor TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetTextColor.java,v 1.1 2008-02-25 21:17:01 murkle Exp $
 */
public class SetTextColor extends EMFTag {

    private Color color;

    public SetTextColor() {
        super(24, 1);
    }

    public SetTextColor(Color color) {
        this();
        this.color = color;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetTextColor(emf.readCOLORREF());
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
        renderer.setTextColor(color);
    }
}
