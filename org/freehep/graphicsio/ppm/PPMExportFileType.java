// Copyright 2000-2006, FreeHEP.
package org.freehep.graphicsio.ppm;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;

import org.freehep.graphicsio.exportchooser.ImageExportFileType;

/**
 * @author Mark Donszelmann
 * @author Charles Loomis
 * @version $Id: PPMExportFileType.java,v 1.1 2008-02-25 21:18:23 murkle Exp $
 */
public class PPMExportFileType extends ImageExportFileType {

    static {
        try {
            Class clazz = Class
                    .forName("org.freehep.graphicsio.ppm.PPMImageWriterSpi");
            IIORegistry.getDefaultInstance().registerServiceProvider(
                    (ImageWriterSpi)clazz.newInstance(), ImageWriterSpi.class);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public PPMExportFileType() {
        super("ppm");
    }

}
