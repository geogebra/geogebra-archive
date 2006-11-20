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

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLocus;
import geogebra.util.MyPoint;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.LinkedList;


public final class DrawLocus extends Drawable {
	
 
	private GeoLocus locus;    	
    
    boolean isVisible, labelVisible;   
	private GeneralPath gp;	
	private double [] lastPointCoords;
	    
    public DrawLocus(EuclidianView view, GeoLocus locus) {      
    	this.view = view;          
        this.locus = locus;
        geo = locus;                          
        
        gp = new GeneralPath();     
        update();
    }
    
    final public void update() {    	
        isVisible = geo.isEuclidianVisible();   
        if (!isVisible) return;					        	          
        
		buildGeneralPath(locus.getMyPointList());
		
		 // line on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
		}
		
		updateStrokes(geo);
				
		labelVisible = geo.isLabelVisible();
		if (labelVisible) {								
			labelDesc = geo.getLabelDescription();			
			xLabel = (int) (lastPointCoords[0] - 5);
			yLabel = (int) (lastPointCoords[1] + 4 + view.fontSize);   
			addLabelOffset();           
		}
    }
    
    private void buildGeneralPath(LinkedList pointList) {    	
    	gp.reset(); 
    	
    	Iterator it = pointList.iterator();
    	MyPoint p;
    	double [] coords = new double[2];
    	while (it.hasNext()) {
    		p = (MyPoint) it.next();
    		coords[0] = p.x;
    		coords[1] = p.y;
    		view.toScreenCoords(coords);      		    		
    		
    		if ( coords[0] < Float.MAX_VALUE && 
    			  coords[1] < Float.MAX_VALUE) 
    		{
	    		if (p.lineTo) {
					gp.lineTo((float) coords[0], (float) coords[1]);					
				} else {					
					gp.moveTo((float) coords[0], (float) coords[1]);	   						
				}           	 	    		
    		}
        }
    	
    	lastPointCoords = coords;    	
    }      

    final public void draw(Graphics2D g2) {   
    	if (isVisible) {    			    	
            if (geo.doHighlighting()) {
                // draw locus              
                g2.setPaint(geo.selColor);
                g2.setStroke(selStroke);
                g2.draw(gp);
            }      
        	
            // draw locus         
            g2.setPaint(geo.objColor);
            g2.setStroke(objStroke);
            g2.draw(gp);
                        
            // label
            if (labelVisible) {
				g2.setFont(view.fontLine);
				g2.setColor(geo.labelColor);
				drawLabel(g2);
            }                        
        }
    }     
    	
    
   
    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return gp.intersects(x-2,y-2,4,4)
				&& !gp.contains(x-2,y-2,4,4);        
    }
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


