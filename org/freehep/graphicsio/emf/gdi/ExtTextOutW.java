// Copyright 2002-2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Rectangle;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ExtTextOutW TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtTextOutW.java,v 1.1 2008-02-25 21:17:10 murkle Exp $
 */
public class ExtTextOutW extends AbstractExtTextOut implements EMFConstants {

    private TextW text;

    public ExtTextOutW() {
        super(84, 1, null, 0, 1, 1);
    }

    public ExtTextOutW(
        Rectangle bounds,
        int mode,
        float xScale,
        float yScale,
        TextW text) {

        super(84, 1, bounds, mode, xScale, yScale);
        this.text = text;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        return new ExtTextOutW(
            emf.readRECTL(),
            emf.readDWORD(),
            emf.readFLOAT(),
            emf.readFLOAT(),
            TextW.read(emf));
    }

    public Text getText() {
        return text;
    }
}
