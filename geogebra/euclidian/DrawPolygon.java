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

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawPolygon extends Drawable
implements Previewable {
   
    private GeoPolygon poly;            
    boolean isVisible, labelVisible;
    
    private GeneralPath gp = new GeneralPath();
	private GeneralPath gpPreview = new GeneralPath();
    private double [] coords = new double[2];
	private ArrayList points;              
      
    public DrawPolygon(EuclidianView view, GeoPolygon poly) {
		this.view = view; 
		this.poly = poly;		
		geo = poly;

		update();
    }
    
    /**
     * Creates a new DrawPolygon for preview.     
     */
	DrawPolygon(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = geo.isLabelVisible();       
			updateStrokes(poly);
			
            // build general path for this polygon       
			gp.reset();
            GeoPoint [] points = poly.getPoints();
			
			// first point
			points[0].getInhomCoords(coords);
			view.toScreenCoords(coords);			
            gp.moveTo((float) coords[0], (float) coords[1]);   
			
			// for centroid calculation (needed for label pos)
			double xsum = coords[0];
			double ysum = coords[1];
            
            for (int i=1; i < points.length; i++) {
				points[i].getInhomCoords(coords);
				view.toScreenCoords(coords);	
				if (labelVisible) {
					xsum += coords[0];
					ysum += coords[1];
				}			
            	gp.lineTo((float) coords[0], (float) coords[1]);                  	
            }
            
        	gp.closePath(); 	
        	
        	 // polygon on screen?		
    		if (!gp.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
    			return;
    		}
        	
			if (labelVisible) {
				labelDesc = geo.getLabelDescription();            				
				xLabel = (int) (xsum / points.length);
				yLabel = (int) (ysum / points.length);
				addLabelOffset();                                       
			}               
        }
    }
        
	final public void draw(Graphics2D g2) {
        if (isVisible) {
        	if (poly.alphaValue > 0.0f) {
				g2.setPaint(poly.fillColor);                       
				g2.fill(gp);  				
        	}
					  
            if (geo.doHighlighting()) {
                g2.setPaint(poly.selColor);
                g2.setStroke(selStroke);            
                g2.draw(gp);                
            }            
            
            if (labelVisible) {
				g2.setPaint(poly.labelColor);
				g2.setFont(view.fontPoint);
				drawLabel(g2);
            }			
        }
    }
    
	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;
		
		if (isVisible) { 			 
			// build general path for this polygon       
			gpPreview.reset();
			
			// first point
			GeoPoint p = (GeoPoint) points.get(0);
			p.getInhomCoords(coords);
			view.toScreenCoords(coords);			
			gpPreview.moveTo((float) coords[0], (float) coords[1]);   
			
			// rest of points		
			for (int i=1; i < size; i++) {
				p = (GeoPoint) points.get(i);
				p.getInhomCoords(coords);
				view.toScreenCoords(coords);	
				gpPreview.lineTo((float) coords[0], (float) coords[1]);                  	
			}    
			gp = gpPreview;									              
		}	
	}
	
	final public void updateMousePos(int x, int y) {
		if (isVisible) { 	
			gp = (GeneralPath) gpPreview.clone();
			gp.lineTo(x, y);
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
    	if (isVisible) {
			g2.setPaint(ConstructionDefaults.colPreviewFill);                       
			g2.fill(gp);  			
		  			            						
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(gp);
    	}		            	
    }
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {		
       return gp.contains(x, y) || gp.intersects(x-2, y-2, 4, 4);        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(gp.getBounds());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
}
