// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * ExtCreatePen TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtCreatePen.java,v 1.1 2008-02-25 21:17:07 murkle Exp $
 */
public class ExtCreatePen extends EMFTag {

    private int index;

    private ExtLogPen pen;

    public ExtCreatePen() {
        super(95, 1);
    }

    public ExtCreatePen(int index, ExtLogPen pen) {
        this();
        this.index = index;
        this.pen = pen;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        int index = emf.readDWORD();
        /* int bmiOffset = */ emf.readDWORD();
        /* int bmiSize = */ emf.readDWORD();
        /* int brushOffset = */ emf.readDWORD();
        /* int brushSize = */ emf.readDWORD();
        return new ExtCreatePen(index, new ExtLogPen(emf));
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(index);
        emf.writeDWORD(0); // offset to bmi
        emf.writeDWORD(0); // size of bmi
        emf.writeDWORD(0); // offset to brush bitmap
        emf.writeDWORD(0); // size of brush bitmap
        pen.write(emf);
    }

    public String toString() {
        return super.toString() +
            "\n  index: 0x" + Integer.toHexString(index) +
            "\n" + pen.toString();
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.storeGDIObject(index, pen);
    }
}
