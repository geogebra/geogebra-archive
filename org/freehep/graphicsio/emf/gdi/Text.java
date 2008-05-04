// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF Text
 * 
 * @author Mark Donszelmann
 * @version $Id: Text.java,v 1.3 2008-05-04 12:18:15 murkle Exp $
 */
public abstract class Text implements EMFConstants {

    Point pos;

    String string;

    int options;

    Rectangle bounds;

    int[] widths;

    protected Text(Point pos, String string, int options, Rectangle bounds, int[] widths) {
        this.pos = pos;
        this.string = string;
        this.options = options;
        this.bounds = bounds;
        this.widths = widths;
    }

    public Point getPos() {
        return pos;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getString() {
        return string;
    }

    public abstract void write(EMFOutputStream emf) throws IOException;
}
