package org.freehep.graphicsio.pdf;

/**
 * Specifies a PDFName object.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFName.java,v 1.1 2008-02-25 21:17:18 murkle Exp $
 */
public class PDFName implements PDFConstants {

    private String name;

    PDFName(String name) {
        this.name = name;
    }

    public String toString() {
        return "/" + name;
    }
}