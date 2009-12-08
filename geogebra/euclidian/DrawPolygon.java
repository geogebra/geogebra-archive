/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;

import java.awt.Graphics2D;
import java.awt.Rectangle;
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
    
    private GeneralPathClipped gp;
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
			addPointsToPath(poly.getPoints());
			gp.closePath();
        	
        	 // polygon on screen?		
    		if (!gp.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
            	// don't return here to make sure that getBounds() works for offscreen points too
    		}             
        }
    }
	
	private void addPointsToPath(GeoPoint[] points) {
		if (gp == null)
			gp = new GeneralPathClipped(view);
		else
			gp.reset();
		
		// first point
		points[0].getInhomCoords(coords);
		view.toScreenCoords(coords);			
        gp.moveTo(coords[0], coords[1]);   
		
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
        	gp.lineTo(coords[0], coords[1]);                  	
        }

		if (labelVisible) {
			labelDesc = geo.getLabelDescription();  
			xLabel = (int) (xsum / points.length);
			yLabel = (int) (ysum / points.length);
			addLabelOffset();                                       
		}  
	}
        
	final public void draw(Graphics2D g2) {
        if (isVisible) {
        	if (poly.alphaValue > 0.0f) {
				g2.setPaint(poly.getFillColor());                       
				g2.fill(gp);  				
        	}        	        	
            	
            if (geo.doHighlighting()) {
                g2.setPaint(poly.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(gp);                
            }        
        	
            // polygons (e.g. in GeoLists) that don't have labeled segments
            // should also draw their border
            else if (!poly.wasInitLabelsCalled() && poly.lineThickness > 0) {
        		 g2.setPaint(poly.getObjectColor());
                 g2.setStroke(objStroke);            
                 g2.draw(gp);  
        	}
        	
                                  
            if (labelVisible) {
				g2.setPaint(poly.getLabelColor());
				g2.setFont(view.fontPoint);
				drawLabel(g2);
            }			
        }
    }
    
	final public void updatePreview() {
		int size = points.size();
		isVisible = size > 0;
		
		if (isVisible) { 		
			GeoPoint[] pointsArray = new GeoPoint[size];
			for (int i=0; i < size; i++) {
				pointsArray[i] = (GeoPoint) points.get(i);
			}
			addPointsToPath(pointsArray);								              
		}	
	}
	
	final public void updateMousePos(int x, int y) {
		if (isVisible) {
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
       return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));        
    }
	
    final public boolean isInside(Rectangle rect) {
    	return gp != null && rect.contains(gp.getBounds());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return gp.getBounds();	
	}

    
}
