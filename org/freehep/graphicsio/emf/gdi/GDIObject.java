package org.freehep.graphicsio.emf.gdi;

import org.freehep.graphicsio.emf.EMFRenderer;

/**
 * A GDIObject uses a {@link org.freehep.graphicsio.emf.EMFRenderer}
 * to render itself to a Graphics2D object.
 *
 * @author Steffen Greiffenberg
 * @version $Id: GDIObject.java,v 1.3 2008-05-04 12:19:20 murkle Exp $
 */
public interface GDIObject {

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer);
}
