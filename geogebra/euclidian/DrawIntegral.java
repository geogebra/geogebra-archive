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

import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

/**
 * Draws definite Integral of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawIntegral extends Drawable {
   
    private GeoNumeric n;
    private GeoFunction f;
    private NumberValue a, b;
    
	private GeneralPath gp = new GeneralPath();
    boolean isVisible, labelVisible;
   
    public DrawIntegral(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	this.n = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	AlgoIntegralDefinite algo = (AlgoIntegralDefinite) n.getParentAlgorithm();
    	f = algo.getFunction();
        a = algo.getA();
        b = algo.getB();    
        update();
    }

    final public void update() {						   
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(n);
		
		// init gp
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		int ax = view.toScreenCoordX(aRW);
		int bx = view.toScreenCoordX(bRW);
		float y0 = (float) view.yZero;
				
		// plot definite integral
		gp.reset(); 				
		gp.moveTo(ax, y0); 
		DrawFunction.plotFunction(f, aRW, bRW, view, gp, false, false);
		gp.lineTo(bx, y0);
		gp.lineTo(ax, y0);			
		
//		 gp on screen?		
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
	                g2.setPaint(n.selColor);
	                g2.setStroke(selStroke);            
	                g2.draw(gp);        
	            } 
        	} catch (Exception e) {
        		System.err.println(e.getMessage());
        	}
            
        	// filling
        	if (n.alphaValue > 0f) {
				try {
	            	g2.setPaint(n.fillColor);                                  
					g2.fill(gp);    
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}    
        	}
			
			try {
				g2.setPaint(n.objColor);
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
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
