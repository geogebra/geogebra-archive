/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawVector.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class DrawVector extends Drawable implements Previewable {
   
    private GeoVector v;
    private GeoPoint P;
    
    private double x1, y1, x2, y2;    
    private double length, fx, fy, vx, vy, factor; 
    boolean isVisible, labelVisible;
    private boolean traceDrawingNeeded = false;
           	          
	private Line2D.Float line = new Line2D.Float();           	          
    private GeneralPath gp = new GeneralPath(); // for arrow   
    private double [] coords = new double[2];
    private ArrayList points;
    
    /** Creates new DrawVector */
    public DrawVector(EuclidianView view, GeoVector v) {
    	this.view = view;
		this.v = v;
		geo = v;
    			
		update();
    }
    
	DrawVector(EuclidianView view, ArrayList points) {
		this.view = view;
		this.points = points;
		updatePreview();
	}

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();    
        
		updateStrokes(v);
		
		//start point  
		P = v.getStartPoint();            		                            
        if (P != null && !P.isInfinite()) {
        	P.getInhomCoords(coords);
			x2 = coords[0];
			y2 = coords[1];
			view.toScreenCoords(coords);
        } else {
        	x2 = 0.0;
        	y2 = 0.0;
            coords[0] = view.xZero;
           	coords[1] = view.yZero;			
        }                  
        x1 = coords[0];
        y1 = coords[1];       
         
		// end point 
        coords[0] = x2 + v.x;
        coords[1] = y2 + v.y;
		view.toScreenCoords(coords);        
        x2 = coords[0];
        y2 = coords[1];
        
        setArrow(v.lineThickness);	// uses x1, y1, x2, y2
        
    	// line on screen?		
		if (!line.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
		}		
        
		// label position
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();        
			xLabel = (int) ((x1 + x2)/ 2.0 + vy);
			yLabel = (int) ((y1 + y2)/ 2.0 - vx);
			addLabelOffset();   
		}        		
		
		// draw trace
		// a vector is a Locateable and it might
		// happen that there are several update() calls
		// before the new trace should be drawn
		// so the actual drawing is moved to draw()
		traceDrawingNeeded = v.trace;		
		if (v.trace) {
			isTracing = true;			
		} else {
			if (isTracing) {				
				isTracing = false;
				view.updateBackground();
			}
		}								 	                                
    }

    
    private void setArrow(float lineThickness) {
		// arrow for endpoint
		  vx = x2 - x1;
		  vy = y2 - y1;
		  factor = 12.0 + lineThickness;
		  length = GeoVec2D.length(vx, vy);
		  if (length > 0.0) {
			vx = (vx * factor) / length; 
			vy = (vy * factor) / length;
		  }
		                           
		  // build arrow
		  fx = x2 - vx;
		  fy = y2 - vy;
		  line.setLine(x1, y1, fx, fy);
		  vx /= 4.0;
		  vy /= 4.0;                            
		  gp.reset();
		  gp.moveTo((float) x2, (float) y2); // end point
		  gp.lineTo((float) (fx - vy), (float)(fy + vx));
		  gp.lineTo((float)(fx + vy), (float)(fy - vx));
		  gp.closePath();	
    }
    
    public void draw(Graphics2D g2) {
        if (isVisible) {
        	if (traceDrawingNeeded) {
        		traceDrawingNeeded = false;
        		Graphics2D g2d = view.getBackgroundGraphics();
    			if (g2d != null) drawTrace(g2d);    			
        	}
        	
            if (geo.doHighlighting()) {
                g2.setPaint(v.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(v.getObjectColor());
			g2.setStroke(objStroke);  
			g2.draw(line);              
            g2.fill(gp);
                                              
            if (labelVisible) {
				g2.setFont(view.fontVector);
				g2.setPaint(v.getLabelColor());
				drawLabel(g2);
            }            
        }
    }
    
    
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(v.getObjectColor());
		g2.setStroke(objStroke);  
		g2.draw(line);  
		g2.fill(gp);       
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			GeoPoint P = (GeoPoint) points.get(0);	
			P.getInhomCoords(coords);
			view.toScreenCoords(coords);	
			x1 = coords[0];
			y1 = coords[1];							   								                        				
			line.setLine(x1, y1, x1, y1);                                   			                                            
		}
	}
    
	final public void updateMousePos(int x, int y) {		
		if (isVisible) {
			x2 = x;
			y2 = y;    		           		         
			line.setLine(x1, y1, x2, y2);        
			setArrow(1);
		}						    	                                 
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {		
			g2.setPaint(ConstructionDefaults.colPreview);
			g2.setStroke(objStroke);  
			g2.fill(gp);                                    
			g2.draw(line);                                    			      
		}
	}
	
	public void disposePreview() {		
	}
    
	final public boolean hit(int x,int y) {        
        return line.intersects(x-3, y-3, 6, 6) 
				|| gp.intersects(x-3, y-3, 6, 6);
    }
	
	final public boolean isInside(Rectangle rect) {  
    	return rect.contains(line.getBounds());   
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
			return line.getBounds();	
	}
}
