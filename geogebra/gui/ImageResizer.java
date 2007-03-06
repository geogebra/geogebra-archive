package geogebra.gui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageResizer {


	/**
	 * Resizes the image to the given size.
	 * 
	 * @param srcImg
	 *            the image to be resized
	 * @return resized image
	 */
	public static BufferedImage resizeImage(BufferedImage image, 
			int width,
			int height) {		
		
	    // draw original image to resizedImage object and
	    // scale it to the new size on-the-fly
	    BufferedImage resizedImage = new BufferedImage(width, 
	    			height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	      RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    graphics2D.drawImage(image, 0, 0, width, height, null);
			
		return resizedImage;
	}
}