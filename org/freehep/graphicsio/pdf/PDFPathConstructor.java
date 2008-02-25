// Copyright 2002 FreeHEP.
package org.freehep.graphicsio.pdf;

import java.io.IOException;

import org.freehep.graphicsio.QuadToCubicPathConstructor;

/**
 * @author Mark Donszelmann
 * @version $Id: PDFPathConstructor.java,v 1.1 2008-02-25 21:17:17 murkle Exp $
 */
public class PDFPathConstructor extends QuadToCubicPathConstructor {
    private PDFStream stream;

    public PDFPathConstructor(PDFStream stream) {
        super();
        this.stream = stream;
    }

    public void move(double x, double y) throws IOException {
        stream.move(x, y);
        super.move(x, y);
    }

    public void line(double x, double y) throws IOException {
        stream.line(x, y);
        super.line(x, y);
    }

    public void cubic(double x1, double y1, double x2, double y2, double x3,
            double y3) throws IOException {
        stream.cubic(x1, y1, x2, y2, x3, y3);
        super.cubic(x1, y1, x2, y2, x3, y3);
    }

    public void closePath(double x0, double y0) throws IOException {
        stream.closePath();
        super.closePath(x0, y0);
    }
}
