// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * SaveDC TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SaveDC.java,v 1.3 2008-05-04 12:17:56 murkle Exp $
 */
public class SaveDC extends EMFTag {

    public SaveDC() {
        super(33, 1);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return this;
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        renderer.saveDC();
    }
}
