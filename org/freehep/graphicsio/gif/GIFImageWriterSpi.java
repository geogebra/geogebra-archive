// Copyright 2003-2006, FreeHEP
package org.freehep.graphicsio.gif;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * 
 * @version $Id: GIFImageWriterSpi.java,v 1.1 2008-02-25 21:17:44 murkle Exp $
 */
public class GIFImageWriterSpi extends ImageWriterSpi {

    public GIFImageWriterSpi() {
        super("FreeHEP Java Libraries, http://java.freehep.org/", "1.0",
                new String[] { "gif" }, new String[] { "gif" },
                new String[] { "image/gif", "image/x-gif" },
                "org.freehep.graphicsio.gif.GIFImageWriter",
                STANDARD_OUTPUT_TYPE, null, false, null, null, null, null,
                false, null, null, null, null);
    }

    public String getDescription(Locale locale) {
        return "FreeHEP Graphics Interchange Format";
    }

    public ImageWriter createWriterInstance(Object extension)
            throws IOException {
        return new GIFImageWriter(this);
    }

    public boolean canEncodeImage(ImageTypeSpecifier type) {
        return true;
    }
}