/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoPoint;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;


/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawImage extends Drawable {
	 	      
    private GeoImage geoImage;    
    boolean isVisible;       
    private Image image;
    
    boolean absoluteLocation;
    private AlphaComposite alphaComp;
    private float alpha; 
    private boolean isInBackground = false;    
    private AffineTransform at, atInverse, tempAT;
    private int screenX, screenY;
        
    public DrawImage(EuclidianView view, GeoImage geoImage) {      
    	this.view = view;          
        this.geoImage = geoImage;
        geo = geoImage;
        
        // temp
        at = new AffineTransform();
        tempAT = new AffineTransform();
        
        selStroke = new MyBasicStroke(1.5f);

        update();
    }
    
    final public void update() {         	
        isVisible = geo.isEuclidianVisible();      
			 
        if (!isVisible) return;
        
        if (geo.alphaValue != alpha) {
        	alpha = geo.alphaValue;
        	alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);                
    	}
        
        image = geoImage.getImage();             
        int width = image.getWidth(null);
        int height = image.getHeight(null); 
        absoluteLocation = geoImage.isAbsoluteScreenLocActive();
  
        // ABSOLUTE SCREEN POSITION
        if (absoluteLocation) {
        	screenX = geoImage.getAbsoluteScreenLocX();
        	screenY = geoImage.getAbsoluteScreenLocY() - height;
        	labelRectangle.setBounds(screenX, screenY, width, height);    
        }
        
        // RELATIVE SCREEN POSITION
        else {	                                      
	        GeoPoint A = geoImage.getCorner(0);
	        GeoPoint B = geoImage.getCorner(1);
	        GeoPoint D = geoImage.getCorner(2);
	        
	        double ax = 0;
	        double ay = 0;
	        if (A != null) {
	        	if (!A.isDefined()) {
	        		isVisible = false;
	        		return;
	        	}
	        	ax = A.inhomX;
	        	ay = A.inhomY;
	        }
	        
	        // set transform according to corners                
	        at.setTransform(view.coordTransform); // last transform: real world -> screen   
	        at.translate(ax, ay); // translate to first corner A
	        
	        if (B == null) {
	        	// we only have corner A 
	        	if (D == null) {        
	        		// use original pixel width and heigt of image
			        at.scale(view.invXscale, -view.invXscale); 		        
		    	} 
	        	// we have corners A and D 
	        	else {        
	        		if (!D.isDefined()) {
		        		isVisible = false;
		        		return;
		        	}
	        		// rotate to coord system (-ADn, AD)
	        		double ADx = D.inhomX - ax;
	        		double ADy = D.inhomY - ay;
	        		tempAT.setTransform(ADy, -ADx, ADx, ADy, 0, 0);
	        		at.concatenate(tempAT);
	        		
	        		// scale height of image to 1
	        		double yscale = 1.0 / height;
	        		at.scale(yscale, -yscale);        		
	        	}
	        } 
	        else { 
	        	if (!B.isDefined()) {
	        		isVisible = false;
	        		return;
	        	}
	        	
	        	// we have corners A and B
	        	if (D == null) { 
	        		// rotate to coord system (AB, ABn)
	        		double ABx = B.inhomX - ax;
	        		double ABy = B.inhomY - ay;
	        		tempAT.setTransform(ABx, ABy, -ABy, ABx, 0, 0);
	        		at.concatenate(tempAT);
	        		
	        		// scale width of image to 1
	        		double xscale = 1.0 / width;
	        		at.scale(xscale, -xscale);        		
	        	}        	
	        	else { // we have corners A, B and D
	        		if (!D.isDefined()) {
		        		isVisible = false;
		        		return;
		        	}
	        		
	        		// shear to coord system (AB, AD)
	        		double ABx = B.inhomX - ax;
	        		double ABy = B.inhomY - ay;
	        		double ADx = D.inhomX - ax;
	        		double ADy = D.inhomY - ay;
	        		tempAT.setTransform(ABx, ABy, ADx, ADy, 0, 0);
	        		at.concatenate(tempAT);
	        		
	        		// scale width and height of image to 1        		
	        		at.scale(1.0/width, -1.0/height);        		
	        	}        	
	        }
	        
	        // move image up so that A becomes lower left corner
		    at.translate(0, -height); 
		    labelRectangle.setBounds(0, 0, width, height);	
		    
		    try { 
		    	// for hit testing
		    	atInverse = at.createInverse();		            	
		    } catch (NoninvertibleTransformException e) {
		    	isVisible = false;
		    	return;
		    }	    
        }
	    	    
	    if (isInBackground != geoImage.isInBackground()) {	
	    	isInBackground = !isInBackground;
	    	if (isInBackground) {
				view.addBackgroundImage(this);
	    	} else {
	    		view.removeBackgroundImage(this);
	    		view.updateBackgroundImage();
	    	}			
	    }
	    
	    if (isInBackground) 
	    	view.updateBackgroundImage();	    	    	    	
    }

    final public void draw(Graphics2D g2) {   	   
        if (isVisible) {  
        	Composite oldComp = g2.getComposite();
        	if (alpha != 1f) {
        		g2.setComposite(alphaComp);                
        	}
        	
        	if (absoluteLocation) {        	 
        		g2.drawImage(image, screenX, screenY, null);   
        		if (!isInBackground && geo.doHighlighting()) {
    				//  draw rectangle around image
    				g2.setStroke(selStroke);
    				g2.setPaint(Color.lightGray);		
    				g2.draw(labelRectangle);         
    			}     		
        	} else {
        		AffineTransform oldAT = g2.getTransform();         
            	g2.transform(at);       			
            	g2.drawImage(image, 0, 0, null); 
     			if (!isInBackground && geo.doHighlighting()) {
    				// draw rectangle around image
     				g2.setStroke(selStroke);
    				g2.setPaint(Color.lightGray);		
    				g2.draw(labelRectangle);        
    			} 
            	g2.setTransform(oldAT);            	
        	}  
        	
        	g2.setComposite(oldComp);
        }
    }
    
    boolean isInBackground() {
    	return geoImage.isInBackground();
    }
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
    	if (!isVisible || geoImage.isInBackground()) return false;
    	
    	hitCoords[0] = x;
    	hitCoords[1] = y;
    	
    	// convert screen to image coordinate system    
    	if (!geoImage.isAbsoluteScreenLocActive()) {
    		atInverse.transform(hitCoords, 0, hitCoords, 0, 1);
    	}    	    	
    	return labelRectangle.contains(hitCoords[0], hitCoords[1]);						     
    }
    private double [] hitCoords = new double[2];
    
    /**
     * Returns false     
     */ 
	public boolean hitLabel(int x, int y) {
		return false;
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }   

}


