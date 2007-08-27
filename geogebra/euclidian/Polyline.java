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
 * Polyline.java
 *
 * Created on 16. November 2001, 09:26
 */

package geogebra.euclidian;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

/**
 *
 * @author  markus
 * @version 
 */
public class Polyline {   
    
    
    int n;
        
    private int max_capacity = DrawConic.MAX_PLOT_POINTS;
	double [] x, y; 	
    private GeneralPath gp = new GeneralPath();
    
    /** Creates new Polyline for n vertices */
    public Polyline(int n) {
           setNumberOfPoints(n);  
    }
    
    public void setPoints(int n, double [] x, double [] y) {
        this.n = n;
        this.x = x;
        this.y = y;
    }
    
    void setNumberOfPoints(int n) {  			
			this.n = n;
			if (n > max_capacity || x == null) {			
				max_capacity = n;
				x = new double[max_capacity]; 
				y = new double[max_capacity];		    							    
			} 		    			
    }
    
    /** builds a general path of a polyline from points (x[0], y[0]) 
     * to (x[n-1], y[n-1]) 
     */    
    final public void buildGeneralPath() {
		boolean firstSet = false;                       
        
        gp.reset();                       
        for (int i=0; i < n; i++) {             	           	 	
    		if ( Math.abs(x[i]) < Float.MAX_VALUE &&
				 Math.abs(y[i]) < Float.MAX_VALUE ) {    			 
				if (firstSet) {
					gp.lineTo((float) x[i], (float) y[i]);					
				} else {					
					gp.moveTo((float) x[i], (float) y[i]);	 // starting point
					firstSet = true;	
				}		
    		}
        }
    }  
    
    /** transforms the general path of this polyline */    
    public void transform(AffineTransform at) {
        gp.transform(at);           
    }     
    
    final public void draw(Graphics2D g2) {            	
     	Drawable.drawGeneralPath(gp, g2);
    }
    
    final public void fill(Graphics2D g2) {
    	Drawable.fillGeneralPath(gp, g2);
    }
    
    final public boolean intersects(double x, double y, double w, double h) {
    	return gp.intersects(x, y, w, h);
    }
    
    final public boolean contains(double x, double y, double w, double h) {
    	return gp.contains(x, y, w, h);
    }
    
    final public Shape createStrokedShape(Stroke stroke) {
    	return stroke.createStrokedShape(gp);
    }

}
