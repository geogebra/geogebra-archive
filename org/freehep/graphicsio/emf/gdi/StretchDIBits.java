// Copyright 2002-2007, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Locale;

import org.freehep.graphicsio.ImageConstants;
import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFImageLoader;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFRenderer;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.util.io.NoCloseOutputStream;

/**
 * StretchDIBits TAG. Encoded as plain RGB rather than the not-yet-working PNG
 * format. The BI_code for BI_PNG and BI_JPG seems to be missing from the
 * WINGDI.H file of visual C++.
 * 
 * @author Mark Donszelmann
 * @version $Id: StretchDIBits.java,v 1.4 2008-08-06 19:23:24 murkle Exp $
 */
public class StretchDIBits extends EMFTag implements EMFConstants {

    public final static int size = 80;

    private Rectangle bounds;

    private int x, y, width, height;

    private int xSrc, ySrc, widthSrc, heightSrc;

    private int usage, dwROP;

    private Color bkg;

    private BitmapInfo bmi;

    private BufferedImage image;

    public StretchDIBits() {
        super(81, 1);
    }

    public StretchDIBits(Rectangle bounds, int x, int y, int width, int height,
            BufferedImage image, Color bkg) {
        this();
        this.bounds = bounds;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xSrc = 0;
        this.ySrc = 0;
        this.widthSrc = image.getWidth();
        this.heightSrc = image.getHeight();
        this.usage = DIB_RGB_COLORS;
        this.dwROP = SRCCOPY;

        this.bkg = bkg;
        this.image = image;
        this.bmi = null;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len)
            throws IOException {

        StretchDIBits tag = new StretchDIBits();
        tag.bounds = emf.readRECTL(); // 16
        tag.x = emf.readLONG(); // 20
        tag.y = emf.readLONG(); // 24
        tag.xSrc = emf.readLONG(); // 28
        tag.ySrc = emf.readLONG(); // 32
        tag.width = emf.readLONG(); // 36
        tag.height = emf.readLONG(); // 40
        // ignored
        emf.readDWORD(); // 44
        emf.readDWORD(); // 48
        emf.readDWORD(); // 52
        emf.readDWORD(); // 56

        tag.usage = emf.readDWORD(); // 60
        tag.dwROP = emf.readDWORD(); // 64
        tag.widthSrc = emf.readLONG(); // 68
        tag.heightSrc = emf.readLONG(); // 72

        // FIXME: this size can differ and can be placed somewhere else
        tag.bmi = new BitmapInfo(emf);

        tag.image = EMFImageLoader.readImage(
            tag.bmi.getHeader(),
            tag.width,
            tag.height,
            emf,
            len - 72 - BitmapInfoHeader.size, null);

        return tag;
    }

    public void write(int tagID, EMFOutputStream emf) throws IOException {
        emf.writeRECTL(bounds);
        emf.writeLONG(x);
        emf.writeLONG(y);
        emf.writeLONG(xSrc);
        emf.writeLONG(ySrc);
        emf.writeLONG(widthSrc);
        emf.writeLONG(heightSrc);
        emf.writeDWORD(size); // bmi follows this record immediately
        emf.writeDWORD(BitmapInfoHeader.size);
        emf.writeDWORD(size + BitmapInfoHeader.size); // bitmap follows bmi

        // write image and calculate size
        emf.pushBuffer();
        int encode;
        // plain
        encode = BI_RGB;

        ImageGraphics2D.writeImage(
            (RenderedImage) image,
            ImageConstants.RAW.toLowerCase(Locale.US),
            ImageGraphics2D.getRAWProperties(bkg, "BGR"),
            new NoCloseOutputStream(emf));
        // emf.writeImage(image, bkg, "BGR", 1);
        // png
        // encode = BI_PNG;
        // ImageGraphics2D.writeImage(image, "png", new Properties(), new
        // NoCloseOutputStream(emf));
        // jpg
        // encode = BI_JPEG;
        // ImageGraphics2D.writeImage(image, "jpg", new Properties(), new
        // NoCloseOutputStream(emf));
        int length = emf.popBuffer();

        emf.writeDWORD(length);
        emf.writeDWORD(usage);
        emf.writeDWORD(dwROP);
        emf.writeLONG(width);
        emf.writeLONG(height);

        BitmapInfoHeader header = new BitmapInfoHeader(widthSrc, heightSrc, 24,
                encode, length, 0, 0, 0, 0);
        bmi = new BitmapInfo(header);
        bmi.write(emf);

        emf.append();
    }

    public String toString() {
        return super.toString() +
            "\n  bounds: " + bounds +
            "\n  x, y, w, h: " + x + " " + y + " " + width + " " + height +
            "\n  xSrc, ySrc, widthSrc, heightSrc: " + xSrc + " "
                + ySrc + " " + widthSrc + " " + heightSrc +
            "\n  usage: " + usage +
            "\n  dwROP: " + dwROP +
            "\n  bkg: " + bkg +
            "\n" + bmi.toString();
    }

    /**
     * displays the tag using the renderer
     *
     * @param renderer EMFRenderer storing the drawing session data
     */
    public void render(EMFRenderer renderer) {
        // The StretchDIBits function copies the color data for a rectangle of pixels in a
        // DIB to the specified destination rectangle. If the destination rectangle is larger
        // than the source rectangle, this function stretches the rows and columns of color
        // data to fit the destination rectangle. If the destination rectangle is smaller
        // than the source rectangle, this function compresses the rows and columns by using
        // the specified raster operation.
        if (image != null) {
            renderer.drawImage(image, x, y, widthSrc, heightSrc);
        }
    }
}
