// Copyright 2006, FreeHEP
package org.freehep.util.export;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author duns
 * @version $Id: MimeTypes.java,v 1.1 2008-02-25 21:18:09 murkle Exp $
 */
public class MimeTypes {

    private static Set bitmaps;
    private static Set vectors;
    private static Set others;
    
    static {
        bitmaps = new HashSet();
        bitmaps.add("image/bmp");
        bitmaps.add("image/x-bmp");
        bitmaps.add("image/gif");
        bitmaps.add("image/x-gif");
        bitmaps.add("image/jpeg");
        bitmaps.add("image/jpeg2000");
        bitmaps.add("image/jp2");
        bitmaps.add("image/png");
        bitmaps.add("image/portable-anymap");
        bitmaps.add("image/x-portable-anymap");
        bitmaps.add("image/portable-bitmap");
        bitmaps.add("image/x-portable-bitmap");
        bitmaps.add("image/portable-graymap");
        bitmaps.add("image/x-portable-graymap");
        bitmaps.add("image/portable-pixmap");
        bitmaps.add("image/x-portable-pixmap");
        bitmaps.add("image/tiff");
        bitmaps.add("image/x-raw");
        bitmaps.add("image/x-windows-bmp");
        bitmaps.add("image/vnd.wap.wbmp");
        
        vectors = new HashSet();
        vectors.add("image/cgm");
        vectors.add("image/x-emf");
        vectors.add("image/tex");
        vectors.add("application/pdf");
        vectors.add("application/postscript");
        vectors.add("image/svg+xml");
        vectors.add("application/x-shockwave-flash");
        
        others = new HashSet();
        others.add("application/java");
    }
    
    private MimeTypes() {
    }
    
    public static boolean isBitmap(String mimeType) {
        return bitmaps.contains(mimeType);
    }
    
    public static boolean isVector(String mimeType) {
        return vectors.contains(mimeType);
    }
    
    public static boolean isOther(String mimeType) {
        return others.contains(mimeType) || (!isBitmap(mimeType) && !isVector(mimeType));
    }
    
}
