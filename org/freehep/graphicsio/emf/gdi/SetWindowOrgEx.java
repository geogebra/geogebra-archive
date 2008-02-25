// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetWindowOrgEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetWindowOrgEx.java,v 1.1 2008-02-25 21:17:10 murkle Exp $
 */
public class SetWindowOrgEx extends EMFTag {

    private Point point;

    public SetWindowOrgEx() {
        super(10, 1);
    }

    public SetWindowOrgEx(Point point) {
        this();
        this.point = point;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetWindowOrgEx(emf.readPOINTL());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writePOINTL(point);
    }

    public String toString() {
        return super.toString() + "\n  point: " + point;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // The SetWindowOrgEx function specifies which window point maps to
        // the window origin (0,0).
        renderer.setWindowOrigin(point);
        renderer.resetTransformation();
    }
}
