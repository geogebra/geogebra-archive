// Copyright 2003-2007, FreeHEP.
package org.freehep.graphicsio;

/**
 * 
 * @author Mark Donszelmann
 * @author Steffen Greiffenberg
 * @version $Id: ImageConstants.java,v 1.1 2008-02-25 21:17:36 murkle Exp $
 */
public class ImageConstants {

    private ImageConstants() {
    }

    /**
     * general purpose data compression / decompression
     */
    public static final String ZLIB = "ZLIB";

    /**
     * image format "raw"
     */
    public static final String RAW = "RAW";

    /**
     * image format "portable network graphics"
     */
    public static final String PNG = "PNG";

    /**
     * image format "joint photographic experts group"
     */
    public static final String JPG = "JPG";

    /**
     * image format "joint photographic experts group"
     */
    public static final String JPEG = "JPEG";

    /**
     * image format "graphics interchange format "
     */
    public static final String GIF = "GIF";

    /**
     * image format "portable pixmap"
     */
    public static final String PPM = "PPM";

    /**
     * image format "bitmap"
     */
    public static final String BMP = "BMP";

    /**
     * image format "windows bitmap"
     */
    public static final String WBMP = "WBMP";

    /**
     * image format "enhanced metafile"
     */
    public static final String EMF = "EMF";

    /**
     * image format "java"
     */
    public static final String JAVA = "JAVA";

    /**
     * image format "scalable vector graphic"
     */
    public static final String SVG = "SVG";

    /**
     * image format "shockwave flash"
     */
    public static final String SWF = "SWF";

    /**
     * image / document format "portable document format"
     */
    public static final String PDF = "PDF";

    /**
     * image / document format "postscript"
     */
    public static final String PS = "PS";

    /**
     * key for {@link org.freehep.util.UserProperties} that stores image format
     */
    public static final String WRITE_IMAGES_AS = "WriteImagesAs";

    /**
     * key for {@link org.freehep.util.UserProperties} that
     * stores a {@link java.awt.Dimension} for image size
     */
    public static final String IMAGE_SIZE = "ImageSize";

    /**
     * value for {@link org.freehep.util.UserProperties} with key
     * IMAGE_SIZE (alternative for a certain dimension to choose
     * cmallest image size)
     */
    public static final String SMALLEST = "Smallest Size";

    /**
     * used for ASCII 85 encoding using a {@link org.freehep.util.io.ASCII85OutputStream}
     * @see org.freehep.graphicsio.ImageGraphics2D#toByteArray(java.awt.image.RenderedImage, String, String, java.util.Properties)
     */
    public final static String ENCODING_ASCII85 = "ASCII85";

    /**
     * zip encoding used for converting Images to byte[]
     * @see org.freehep.graphicsio.ImageGraphics2D#toByteArray(java.awt.image.RenderedImage, String, String, java.util.Properties)
     */
    public final static String ENCODING_FLATE = "Flate";

    /**
     * used for compressed ASCII 85 encoding using a {@link org.freehep.util.io.ASCII85OutputStream}
     * @see org.freehep.graphicsio.ImageGraphics2D#toByteArray(java.awt.image.RenderedImage, String, String, java.util.Properties)
     */
    public final static String ENCODING_FLATE_ASCII85 = ENCODING_FLATE + "-" + ENCODING_ASCII85;

    /**
     * Discrete Cosine Transform for JPEG uses an ASCII85OutputStream
     * @see org.freehep.graphicsio.ImageGraphics2D#toByteArray(java.awt.image.RenderedImage, String, String, java.util.Properties)
     */
    public final static String ENCODING_DCT = "DCT";

    /**
     * RGB ColorModel used by org.freehep.graphicsio.pdf.PDFStream and
     * org.freehep.graphicsio.ps.PSGraphics2D
     */
    public static final String COLOR_MODEL_RGB = "RGB";

    /**
     * Alpha (?) ColorModel used by
     * org.freehep.graphicsio.swf.DefineBitsJPEG3
     */
    public static final String COLOR_MODEL_A = "A";

    /**
     * ColorModel used by
     * org.freehep.graphicsio.swf.DefineBitsLossless
     */
    public static final String COLOR_MODEL_ARGB = "*ARGB";

}
