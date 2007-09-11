/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package geogebra.euclidian;

import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawRay extends Drawable
implements Previewable {
   
    private GeoLine ray;
    private GeoPoint A;
     
    boolean isVisible, labelVisible;
    private ArrayList points;
    
    private Line2D.Double line = new Line2D.Double();               
    private double [] a = new double[2];
	private double [] v = new double[2];
     
    /** Creates new DrawSegment */
    public DrawRay(EuclidianView view, GeoLine ray) {
    	this.view = view;
    	this.ray = ray;
    	geo = ray;
    	    	
    	
        update();
    }
    
	/**
	 * Creates a new DrawSegment for preview.     
	 */
	DrawRay(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
		update(true);
	}
	
	void update(boolean showLabel) {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = showLabel && geo.isLabelVisible();       
			updateStrokes(ray);
			
			A = ray.getStartPoint();
			
			// calc start point of ray in screen coords
			a[0] = A.inhomX;
			a[1] = A.inhomY;
			view.toScreenCoords(a);

			// calc direction vector of ray in screen coords
			v[0] = ray.y * view.xscale;
			v[1] = ray.x * view.yscale;
			
			setClippedLine();
			
			 // line on screen?		
    		if (!line.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
    			return;
    		}
			
			// draw trace
			if (ray.trace) {
				isTracing = true;
				Graphics2D g2 = view.getBackgroundGraphics();
				if (g2 != null) drawTrace(g2);
			} else {
				if (isTracing) {
					isTracing = false;
					view.updateBackground();
				}
			}					           
                                   
            // label position
            // use unit perpendicular vector to move away from line
            if (labelVisible) {
				labelDesc = geo.getLabelDescription();
				
				double nx = v[0];
				double ny = -v[1];
				double length = GeoVec2D.length(nx, ny);
				double unit;
				if (length > 0.0) {
					unit = 16d / length;        		    		   				           					
				} else {
					nx = 0.0;
					ny = 1.0;
					unit = 16d;
				}				   
				xLabel = (int) (a[0] + v[0]/2.0 + nx * unit);
				yLabel = (int) (a[1] + v[1]/2.0 + ny * unit);	  
				addLabelOffset();        
            }		                                                
        }
    }
    
    private void setClippedLine() {
		// calc clip point C = a + lambda * v
		double lambda;
		if (Math.abs(v[0]) > Math.abs(v[1])) {
			if (v[0] > 0) // RIGHT
				lambda = (view.width - a[0]) / v[0];
			else // LEFT
				lambda = - a[0] / v[0];
		} else {
			if (v[1] > 0) // BOTTOM
				lambda = (view.height - a[1]) / v[1];
			else 
				lambda = -a[1] / v[1];
		}

		if (lambda < 0) { // ray is completely out of screen
			isVisible = false;
			return;
		}

		line.setLine( a[0], a[1],  a[0] + lambda * v[0], a[1] + lambda * v[1]);		  
    }
    
    final public void draw(Graphics2D g2) {
        if (isVisible) {			
            if (geo.doHighlighting()) {
                g2.setPaint(ray.selColor);
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(ray.objColor);             
            g2.setStroke(objStroke);            
			g2.draw(line);            
                        
            if (labelVisible) {
				g2.setPaint(ray.labelColor);
				g2.setFont(view.fontLine);
				drawLabel(g2);
            }			
        }
    }
    
    final public void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.objColor);
		g2.setStroke(objStroke);  
		g2.draw(line);
    }
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			A = (GeoPoint) points.get(0);						   			
			A.getInhomCoords(a);			                        
			view.toScreenCoords(a);						
			line.setLine(a[0], a[1], a[0], a[1]);                                   			                                            
		}
	}
	
	final public void updateMousePos(int x, int y) {		
		if (isVisible) { 				
			a[0] = A.inhomX;
			a[1] = A.inhomY;
			view.toScreenCoords(a);
			v[0] = x - a[0];
			v[1] = y - a[1];
			setClippedLine();                                   			                                            
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {			            
			g2.setPaint(ConstructionDefaults.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(line);                        		
		}
	}
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {        
        return line.intersects(x-3, y-3, 6, 6);        
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
