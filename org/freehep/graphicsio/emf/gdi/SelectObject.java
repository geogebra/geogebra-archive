// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SelectObject TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SelectObject.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class SelectObject extends EMFTag {

    private int index;

    public SelectObject() {
        super(37, 1);
    }

    public SelectObject(int index) {
        this();
        this.index = index;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SelectObject(emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
    }

    public String toString() {
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index);
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        GDIObject gdiObject;

        if (index < 0) {
            gdiObject = StockObjects.getStockObject(index);
        } else {
            gdiObject = renderer.getGDIObject(index);
        }

        if (gdiObject != null) {
            // render that object
            gdiObject.render(renderer);
        } else {
            logger.warning("gdi object with index " + index + " not found");
        }
    }
}
