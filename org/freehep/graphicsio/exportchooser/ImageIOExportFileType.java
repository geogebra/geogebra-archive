// Copyright 2003-2007 FreeHEP
package org.freehep.graphicsio.exportchooser;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.RegisterableService;
import javax.imageio.spi.ServiceRegistry;

import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.util.export.ExportFileType;
import org.freehep.util.export.ExportFileTypeRegistry;

/**
 * This class does not work, since the ExportFileTypeRegistry stores Objects by
 * class. If we automatically generate ImageFileTypes by ImageIO they end up
 * being all different objects from the same class. The Registry currently then
 * overwrites the first one with the second and so on. Sun Bug #Submitted.
 * 
 * @author Mark Donszelmann
 * @version $Id: ImageIOExportFileType.java,v 1.1 2008-02-25 21:17:59 murkle Exp $
 */
public class ImageIOExportFileType implements RegisterableService {

    /**
     * This constructor will construct register all image formats available in
     * ImageIO into ExportFileTypeRegistry. The ImageExportFileTypeRegistration
     * will deregister itself immediately.
     */
    public ImageIOExportFileType() {
        // empty, registry is not valid yet
    }

    public void onRegistration(ServiceRegistry registry, Class category) {
        // run over all ImageWriterSpis and store their formats Alphabetically
        IIORegistry imageRegistry = IIORegistry.getDefaultInstance();
        Iterator providers = imageRegistry.getServiceProviders(
                ImageWriterSpi.class, false);
    	ExportFileTypeRegistry exportRegistry = ExportFileTypeRegistry.getDefaultInstance(null);
        while (providers.hasNext()) {
            ImageWriterSpi writerSpi = (ImageWriterSpi) providers.next();
        	String[] formats = writerSpi.getFileSuffixes();
            if ((formats != null) && (formats[0] != null)) {
            	exportRegistry.add(new ImageExportFileType(writerSpi));
            } else {
                System.err.println(getClass() + ": Cannot register "
                        + writerSpi + " because it has no filesuffixes.");
            }
        }    
        
        /*
        // Look for the last ExportFileType so that these ImageExportFileTypes
        // are registered neatly behind that one.
        ExportFileType previous = null;
        Iterator exportTypes = registry.getServiceProviders(
                ExportFileType.class, true);
        while (exportTypes.hasNext()) {
            previous = (ExportFileType) exportTypes.next();
        }

        // run over all formats and book them as ExportFileTypes
        Iterator formats = formatSet.iterator();
        while (formats.hasNext()) {
            String format = (String) formats.next();
            ExportFileType export = ImageExportFileType.getInstance(format);
            if (export != null) {
                registry.registerServiceProvider(export, ExportFileType.class);
                if (previous != null) {
                    registry.unsetOrdering(ExportFileType.class, previous,
                            export);
                    registry.setOrdering(ExportFileType.class,
                            previous, export);
                    // System.out.println("Ordering set : "+result);
                }
                previous = export;
            } else {
                System.err.println(getClass() + ": Invalid format: " + format
                        + ".");
            }
        }
*/
        registry.deregisterServiceProvider(this, category);
    }

    public void onDeregistration(ServiceRegistry registry, Class category) {
    }

    public static void main(String[] args) throws Exception {

        System.out.println("WRITERS");
        IIORegistry imageRegistry = IIORegistry.getDefaultInstance();
        Iterator providers = imageRegistry.getServiceProviders(
                ImageWriterSpi.class, false);
        while (providers.hasNext()) {
            ImageWriterSpi writerSpi = (ImageWriterSpi) providers.next();
            System.out.println("   " + writerSpi);
            System.out.println("      " + writerSpi.getDescription(Locale.US));
            System.out.print("      ");
            String[] formats = writerSpi.getFileSuffixes();
            for (int i = 0; i < formats.length; i++) {
                System.out.print(formats[i] + ", ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("MIMETYPES");
        String[] formats = ImageIO.getWriterMIMETypes();
        for (int i = 0; i < formats.length; i++) {
            System.out.println("   " + formats[i]);
            ImageWriter writer = ImageGraphics2D
                    .getPreferredImageWriterForMIMEType(formats[i]);
            String[] suffixes = writer.getOriginatingProvider()
                    .getFileSuffixes();
            System.out.print("      ");
            for (int j = 0; j < suffixes.length; j++) {
                System.out.print(suffixes[j] + " ");
            }
            System.out.println();
            System.out.println("      " + writer);
        }

        System.out.println();

        System.out.println("READERS");
        providers = imageRegistry.getServiceProviders(ImageReaderSpi.class,
                false);
        while (providers.hasNext()) {
            System.out.println("   " + providers.next());
        }
        System.out.println();

        System.out.println("All ExportFileTypes");
        List exportFileTypes = ExportFileType.getExportFileTypes();
        Iterator iterator = exportFileTypes.iterator();
        while (iterator.hasNext()) {
        	ExportFileType type = (ExportFileType)iterator.next();
            System.out.println("   " + type);
        }
    }
}
