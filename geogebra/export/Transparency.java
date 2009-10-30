package geogebra.export;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Transparency {
	
	/**
	 * Replaces color in img by transparent pixels.
	 */
	public static void makeColorTransparent(BufferedImage img, final Color color) {
		// the color we are looking for... Alpha bits are set to opaque
		int markerRGB = color.getRGB() | 0xFF000000;
			    
	    // Get all the pixels
	    int w = img.getWidth(null);
	    int h = img.getHeight(null);
	    for (int x = 0; x < w; x++) {
	    	for (int y = 0; y < h; y++) {
	    		// get pixel
	    	    int rgb = img.getRGB(x, y);
	    	    
	    	    // make pixel transparent
	    	    if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
	    	    	rgb =  0x00FFFFFF & rgb;
				}
	    	    
	    	    // set pixel
	    	    img.setRGB(x, y, rgb);
	    	}
	    }
	    
	}
}