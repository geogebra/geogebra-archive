/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.euclidian;

import geogebra.kernel.AlgoSumUpperLower;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

/**
 * Draws upper / lower sum of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawUpperLowerSum extends Drawable {
   
    private GeoNumeric sum;
    private NumberValue a, b; // interval borders

    boolean isVisible, labelVisible;     
    private AlgoSumUpperLower algo;   
    private GeneralPath gp = new GeneralPath();
    private double [] coords = new double[2];
   
    public DrawUpperLowerSum(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	sum = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	algo = (AlgoSumUpperLower) n.getParentAlgorithm();    	
        a = algo.getA();
        b = algo.getB();    
        update();
    }

    final public void update() {				   
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(sum);
		
		// init gp
		gp.reset();
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		int ax = view.toScreenCoordX(aRW);
		int bx = view.toScreenCoordX(bRW);
		float y0 = (float) view.yZero;
				
		// plot upper/lower sum rectangles
		int N = algo.getIntervals();		
		double [] leftBorder = algo.getLeftBorders();
		double [] yval = algo.getValues();
		
		// first point 						
		float x = ax;
		float y = y0;		
		gp.moveTo(x, y);					
		for (int i=0; i < N; i++) {
			coords[0] = leftBorder[i];						
			coords[1] = yval[i];
			view.toScreenCoords(coords);
			
			// avoid too big y values
			if (coords[1] < 0) {
				coords[1] = -1;
			} else if (coords[1] > view.height) {
				coords[1] = view.height + 1;
			}
			
			x = (float) coords[0];			
			gp.lineTo(x, y);
			gp.lineTo(x, y0);
			gp.moveTo(x, y);
			y = (float) coords[1];
			gp.moveTo(x, y0);
			gp.lineTo(x, y);			 								
		} 	
		gp.lineTo(bx, y);
		gp.lineTo(bx, y0);
		gp.lineTo(ax, y0);		
		
		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
		}		

		if (labelVisible) {
			xLabel = (ax + bx) / 2 - 6;
			yLabel = (int) view.yZero - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
    }
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {
        	try {
	            if (geo.doHighlighting()) {
	                g2.setPaint(sum.selColor);
	                g2.setStroke(selStroke);            
	                g2.draw(gp);           
	            } 
        	} catch (Exception e) {
        		System.err.println(e.getMessage());
        	}
            
        	if (sum.fillColor.getAlpha() > 0) {
				try {
	            	g2.setPaint(sum.fillColor);                                  
					g2.fill(gp);    
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}   
        	}
			
			try {
				g2.setPaint(sum.objColor);
				g2.setStroke(objStroke);                                   
				g2.draw(gp);   
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);
            }        
        }
    }
    
	final public boolean hit(int x,int y) {  
    	return false;   
    }
	

	final public boolean isInside(Rectangle rect) {  
    	return false;   
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
