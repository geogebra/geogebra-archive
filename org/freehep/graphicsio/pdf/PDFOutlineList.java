package org.freehep.graphicsio.pdf;

import java.io.IOException;

/**
 * Implements the Outline Dictionary (see Table 7.3).
 * <p>
 * 
 * @author Mark Donszelmann
 * @version $Id: PDFOutlineList.java,v 1.3 2008-05-04 12:31:08 murkle Exp $
 */
public class PDFOutlineList extends PDFDictionary {

    PDFOutlineList(PDF pdf, PDFByteWriter writer, PDFObject object,
            PDFRef first, PDFRef last) throws IOException {
        super(pdf, writer, object);
        entry("Type", pdf.name("Outlines"));
        entry("First", first);
        entry("Last", last);
    }

    public void setCount(int count) throws IOException {
        entry("Count", count);
    }
}
