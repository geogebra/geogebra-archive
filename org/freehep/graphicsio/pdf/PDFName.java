package org.freehep.graphicsio.pdf;

/**
 * Specifies a PDFName object.
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFName.java,v 1.3 2008-05-04 12:31:23 murkle Exp $
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