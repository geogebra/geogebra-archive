// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SetBrushOrgEx TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SetBrushOrgEx.java,v 1.1 2008-02-25 21:17:11 murkle Exp $
 */
public class SetBrushOrgEx extends EMFTag {

    private Point point;

    public SetBrushOrgEx() {
        super(13, 1);
    }

    public SetBrushOrgEx(Point point) {
        this();
        this.point = point;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SetBrushOrgEx(emf.readPOINTL());
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
        // The SetBrushOrgEx function sets the brush origin that GDI assigns to
        // the next (only to the next!) brush an application selects into the specified
        // device context.
        renderer.setBrushOrigin(point);
    }
}
