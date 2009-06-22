// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * SelectClipPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: SelectClipPath.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class SelectClipPath extends AbstractClipPath {

    public SelectClipPath() {
        super(67, 1, EMFConstants.RGN_AND);
    }

    public SelectClipPath(int mode) {
        super(67, 1, mode);
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new SelectClipPath(emf.readDWORD());
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeDWORD(getMode());
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        render(renderer, renderer.getPath());
    }
}
