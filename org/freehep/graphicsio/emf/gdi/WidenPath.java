// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * WidenPath TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: WidenPath.java,v 1.4 2009-06-22 02:18:17 hohenwarter Exp $
 */
public class WidenPath extends EMFTag {

    public WidenPath() {
        super(66, 1);
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
        GeneralPath currentPath = renderer.getPath();
        Stroke currentPenStroke = renderer.getPenStroke();
        // The WidenPath function redefines the current path as the area
        // that would be painted if the path were stroked using the pen
        // currently selected into the given device context.
        if (currentPath != null && currentPenStroke != null) {
            GeneralPath newPath = new GeneralPath(
                renderer.getWindingRule());
            newPath.append(currentPenStroke.createStrokedShape(currentPath), false);
            renderer.setPath(newPath);
        }
    }
}
