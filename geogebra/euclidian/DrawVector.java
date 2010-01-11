/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class DrawVector extends Drawable implements Previewable {
   
    private GeoVector v;
    private GeoPoint P;
    
    boolean isVisible, labelVisible;
    private boolean traceDrawingNeeded = false;
           	          
    private Line2D.Double line;               
    private double [] coordsA = new double[2];
	private double [] coordsB = new double[2];   
	private double [] coordsV = new double[2]; 
    private GeneralPath gp; // for arrow   
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
		
		//start point in real world coords
		P = v.getStartPoint();            		                            
        if (P != null && !P.isInfinite()) {
        	P.getInhomCoords(coordsA);
        } else {
            coordsA[0] = 0;
           	coordsA[1] = 0;			
        }       
        
        // vector
        coordsV[0] = v.x;
        coordsV[1] = v.y;
         
		// end point 
        coordsB[0] = coordsA[0] + coordsV[0];
        coordsB[1] = coordsA[1] + coordsV[1];
        
        // set line and arrow of vector and converts all coords to screen
		setArrow(v.lineThickness);
        
		// label position
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();       
			// note that coordsV was normalized in setArrow()
			xLabel = (int) ((coordsA[0] + coordsB[0])/ 2.0 + coordsV[1]);
			yLabel = (int) ((coordsA[1] + coordsB[1])/ 2.0 - coordsV[0]);
			addLabelOffset();   
		}    
		
		if (v == view.getEuclidianController().recordObject)
		    recordToSpreadsheet(v);

		
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

    /**
     * Sets the line and arrow of the vector.
     */
    private void setArrow(float lineThickness) {
    	// screen coords of start and end point of vector
    	boolean onscreenA = view.toScreenCoords(coordsA);
		boolean onscreenB = view.toScreenCoords(coordsB);
        coordsV[0] = coordsB[0] - coordsA[0];
        coordsV[1] = coordsB[1] - coordsA[1];
    	
	      // calculate endpoint F at base of arrow
		  double factor = 12.0 + lineThickness;
		  double length = GeoVec2D.length(coordsV);
		  if (length > 0.0) {
			coordsV[0] = (coordsV[0] * factor) / length; 
			coordsV[1] = (coordsV[1] * factor) / length;
		  }
		  double [] coordsF = new double[2];
		  coordsF[0] = coordsB[0] - coordsV[0];
		  coordsF[1] = coordsB[1] - coordsV[1];
		  
        // set clipped line
		if (line == null) line = new Line2D.Double();
		if (onscreenA && onscreenB) {
			// A and B on screen
			line.setLine(coordsA[0], coordsA[1], coordsF[0], coordsF[1]);
		} else {
			// A or B off screen
			// clip at screen, that's important for huge coordinates
			Point2D.Double [] clippedPoints = 
				Clipping.getClipped(coordsA[0], coordsA[1], coordsF[0], coordsF[1], 0, view.width, 0, view.height);
			if (clippedPoints == null) {
				isVisible = false;	
			} else {
				line.setLine(clippedPoints[0].x, clippedPoints[0].y, clippedPoints[1].x, clippedPoints[1].y);
			}
		}
		
		// add triangle if end point on screen
		  if (gp == null) 
			 gp = new GeneralPath();
		  else 
			gp.reset();
		if (onscreenB && length > 0) {
			  coordsV[0] /= 4.0;
			  coordsV[1] /= 4.0;  
			  
			  gp.moveTo((float) coordsB[0], (float) coordsB[1]); // end point
			  gp.lineTo((float) (coordsF[0] - coordsV[1]), (float)(coordsF[1] + coordsV[0]));
			  gp.lineTo((float)(coordsF[0] + coordsV[1]), (float)(coordsF[1] - coordsV[0]));
			  gp.closePath();	
		}
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
			P.getInhomCoords(coordsA);
			coordsB[0] = coordsA[0];
			coordsB[1] = coordsA[1];
			setArrow(1);                              			                                            
		}
	}
    
	Point2D.Double endPoint = new Point2D.Double();
	
	final public void updateMousePos(int x, int y) {		
		if (isVisible) {
			double xRW = view.toRealWorldCoordX(x);
			double yRW = view.toRealWorldCoordY(y);
			
			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1 && view.getEuclidianController().altDown) {
				GeoPoint p = (GeoPoint)points.get(0);
				double px = p.inhomX;
				double py = p.inhomY;
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt((py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));
				
				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15; 
				
				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);
				
				endPoint.x = xRW;
				endPoint.y = yRW;
				view.getEuclidianController().setLineEndPoint(endPoint);
			}
			else
				view.getEuclidianController().setLineEndPoint(null);
  
			// set start and end point in real world coords
			GeoPoint P = (GeoPoint) points.get(0);	
			P.getInhomCoords(coordsA);
			coordsB[0] = xRW;
			coordsB[1] = yRW;
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
        return isVisible && 
        		(line.intersects(x-3, y-3, 6, 6) || gp.intersects(x-3, y-3, 6, 6));
    }
	
	final public boolean isInside(Rectangle rect) {  
    	return isVisible && rect.contains(line.getBounds());   
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
