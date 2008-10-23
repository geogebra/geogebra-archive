/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.util;

import geogebra.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.util.Hashtable;

import javax.swing.ImageIcon;

/**
 * An ImageManager provides methods for loading images and icons for a JFrame. 
 * To save memory every image and icon will be loaded only once. 
 * @author Markus Hohenwarter
 */
public class ImageManager {
		
	private Hashtable iconTable = new Hashtable();
	private Hashtable internalImageTable = new Hashtable();
	private Hashtable externalImageTable = new Hashtable();
	
	
	private Toolkit toolKit;
	private MediaTracker tracker;
		
	/**
	 * Creates a new ImageManager for the given JFrame.	 
	 */		
 	public ImageManager(Component comp) { 		 	
 		toolKit = Toolkit.getDefaultToolkit();
 		tracker = new MediaTracker(comp);
 	}
  
  	/**
  	 * Gets the icon specified by fileName.
  	 * @return icon for fileName or null
  	 */  
	public ImageIcon getImageIcon(String fileName) {
		return getImageIcon(fileName, null);
	}
	
	// if borderColor == null no border is added
	public ImageIcon getImageIcon(String fileName, Color borderColor) {
		ImageIcon icon = null;		
		Object ob = iconTable.get(fileName);
		if (ob != null) {
			// icon already loaded
			icon = (ImageIcon) ob;
		}
		else {
			 // load the icon		
			 Image im = getImageResource(fileName);			 
			 if (im != null) {			 	 
			 	 icon = new ImageIcon(addBorder(im, borderColor));
			 	 iconTable.put(fileName, icon);				 	 		 	
			 }  
		}		                        		
		return icon;
	}
	
	// draw a line around the image
	public static Image addBorder(Image im, Color borderColor) {
		if (borderColor == null) return im;
		
		BufferedImage bim = toBufferedImage(im);
		Graphics g = bim.getGraphics();					
		
		g.setColor(borderColor);
		g.drawRect(0, 0, bim.getWidth()-1, bim.getHeight()-1);		 	
		return bim;		
	}
	
	/**
	 * Gets the image specified by fileName.
	 * @return image for fileName or null
	 */  
	public Image getInternalImage(String fileName) {		
		Image img = null;		
		Object ob = internalImageTable.get(fileName);
		if (ob != null) {
			// image already loaded
			img = (Image) ob;
		}
		else {
			 // load the image from disk		          
			 img = getImageResource(fileName);			 	 
			 if (img != null) {				 
				 internalImageTable.put(fileName, img);
			 }    			 
		}		  		                     
		return img;
	}
	
	public void addExternalImage(String fileName, BufferedImage img) {
		if (fileName != null && img != null) {
			externalImageTable.put(fileName, img);
		}			
	}	
	
	public BufferedImage getExternalImage(String fileName) {		
		return (BufferedImage) externalImageTable.get(fileName);
	}
	
	/*
	private class FileNamePair {
		File file;
		String name;
		
		FileNamePair(File file, String name) {
			this.file = file;
			this.name = name;
		}
	}*/
	
	private Image getImageResource(String name) {
		 Image img = null;
		 try {
		    java.net.URL url = ImageManager.class.getResource(name);	
		    if (url != null) {		   
				img = toolKit.getImage(url);	
				tracker.addImage(img, 0);
				try {
				   tracker.waitForAll();
				} catch (InterruptedException e) {
				   Application.debug("Interrupted while loading Image: " + name);
				}
				tracker.removeImage(img);
			}			   
		 } catch (Exception e) {
		 	Application.debug(e.toString());
		 }
		 if (img == null) Application.debug("Image " + name + " not found");
		 return img;
	}	  

//	 This method returns a buffered image with the contents of an image
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage)
			return (BufferedImage)image;
    
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
    
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);
    
        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
    
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
    
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
    
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
    
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
    
        return bimage;
    }
	
	 // This method returns true if the specified image has transparent pixels
    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }
    
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
         PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
    
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
    
    
}
