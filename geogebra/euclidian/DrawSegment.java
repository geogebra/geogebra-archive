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
 * DrawSegment
 *
 * Created on 21. 8 . 2003
 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

//added by Lo�c
import geogebra.kernel.GeoSegment;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawSegment extends Drawable
implements Previewable {
   
    private GeoLine s;
    private GeoPoint A, B;
     
    private double nx, ny, unit;
    boolean isVisible, labelVisible;
    private ArrayList points;
    
    private Line2D.Float line = new Line2D.Float();               
    private double [] coordsA = new double[2];
	private double [] coordsB = new double[2];
    
    /** Creates new DrawSegment */
    public DrawSegment(EuclidianView view, GeoLine s) {
    	this.view = view;
    	this.s = s;
    	geo = s;
    	        
        update();
    }
    
	/**
	 * Creates a new DrawSegment for preview.     
	 */
	DrawSegment(EuclidianView view, ArrayList points) {
		this.view = view; 
		this.points = points;

		updatePreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) { 
			labelVisible = geo.isLabelVisible();       
			updateStrokes(s);
			
			A = s.getStartPoint();
	        B = s.getEndPoint();
			
            A.getInhomCoords(coordsA);
            B.getInhomCoords(coordsB);                         
			view.toScreenCoords(coordsA);
			view.toScreenCoords(coordsB);					
			line.setLine(coordsA[0], coordsA[1], coordsB[0], coordsB[1]);        

			// line on screen?		
    		if (!line.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
    			return;
    		}			
			
			// draw trace
			if (s.trace) {
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
				
				nx = coordsA[1] - coordsB[1]; 			
				ny = coordsB[0] - coordsA[0];
				double length = GeoVec2D.length(nx, ny);
				if (length > 0.0) {
					unit = 16d / length;        		    		   				           					
				} else {
					nx = 0.0;
					ny = 1.0;
					unit = 16d;
				}				   
				xLabel = (int) ((coordsA[0] + coordsB[0])/ 2.0 + nx * unit);
				yLabel = (int) ((coordsA[1] + coordsB[1])/ 2.0 + ny * unit);	  
				addLabelOffset();        
            }		                                                
        }
    }
   private void mark(Graphics2D g2){
		geogebra.kernel.GeoSegment seg=(GeoSegment)s;
		switch(seg.getDecorationType()){
			case GeoElement.DECORATION_SEGMENT_ONE_TICK:
		 		double midX=(coordsA[0]+coordsB[0])/2;
				double midY=(coordsA[1]+coordsB[1])/2;
	 			double angle=Math.PI/2-Math.atan2(coordsA[1]-coordsB[1],coordsB[0]-coordsA[0]);
		 		double cos=Math.cos(angle);
		 		double sin=Math.sin(angle);
	 			int x1=(int)(midX-4.5*cos+0.5);
		 		int x2=(int)(midX+4.5*cos+0.5);
		 		int y1=(int)(midY-4.5*sin+0.5);
		 		int y2=(int)(midY+4.5*sin+0.5);		 		
		 		g2.drawLine(x1,y1,x2,y2);
		 	break;
		 	case GeoElement.DECORATION_SEGMENT_TWO_TICKS:
		 		midX=(coordsA[0]+coordsB[0])/2;
		 		midY=(coordsA[1]+coordsB[1])/2;
		 		angle=Math.PI/2-Math.atan2(coordsA[1]-coordsB[1],coordsB[0]-coordsA[0]);
		 		cos=Math.cos(angle);
		 		sin=Math.sin(angle);
		 		x1=(int)(midX-2*sin-4.5*cos+0.5);
		 		x2=(int)(midX-2*sin+4.5*cos+0.5);
		 		y1=(int)(midY+2*cos-4.5*sin+0.5);
		 		y2=(int)(midY+2*cos+4.5*sin+0.5);
		 		g2.drawLine(x1,y1,x2,y2);
		 		x1=(int)(midX+2*sin-4.5*cos+0.5);
		 		x2=(int)(midX+2*sin+4.5*cos+0.5);
		 		y1=(int)(midY-2*cos-4.5*sin+0.5);
		 		y2=(int)(midY-2*cos+4.5*sin+0.5);
		 		g2.drawLine(x1,y1,x2,y2);
		 	break;
		 	case GeoElement.DECORATION_SEGMENT_THREE_TICKS:
		 		midX=(coordsA[0]+coordsB[0])/2;
		 		midY=(coordsA[1]+coordsB[1])/2;
		 		angle=Math.PI/2-Math.atan2(coordsA[1]-coordsB[1],coordsB[0]-coordsA[0]);
		 		cos=Math.cos(angle);
		 		sin=Math.sin(angle);
		 		x1=(int)(midX-4.5*cos+0.5);
		 		x2=(int)(midX+4.5*cos+0.5);
		 		y1=(int)(midY-4.5*sin+0.5);
		 		y2=(int)(midY+4.5*sin+0.5);
		 		g2.drawLine(x1,y1,x2,y2);
		 		x1=(int)(midX-4*sin-4.5*cos+0.5);
		 		x2=(int)(midX-4*sin+4.5*cos+0.5);
		 		y1=(int)(midY+4*cos-4.5*sin+0.5);
		 		y2=(int)(midY+4*cos+4.5*sin+0.5);
		 		g2.drawLine(x1,y1,x2,y2);
		 		x1=(int)(midX+4*sin-4.5*cos+0.5);
		 		x2=(int)(midX+4*sin+4.5*cos+0.5);
		 		y1=(int)(midY-4*cos-4.5*sin+0.5);
		 		y2=(int)(midY-4*cos+4.5*sin+0.5);
		 		g2.drawLine(x1,y1,x2,y2);
		 		break;
		 }
	}
	// Lo�c>
	final public void draw(Graphics2D g2) {
        if (isVisible) {		        	
            if (geo.doHighlighting()) {
                g2.setPaint(s.selColor);
                g2.setStroke(selStroke);            
                g2.draw(line);       
            }
            
            g2.setPaint(s.objColor);             
            g2.setStroke(objStroke);            
			g2.draw(line);
			//added by Lo�c
			mark(g2);
	        if (labelVisible) {
				g2.setPaint(s.labelColor);
				g2.setFont(view.fontLine);
				drawLabel(g2);
            }
        }
    }
    
	final void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.objColor);
		g2.setStroke(objStroke);  
		g2.draw(line);
	}
    
	final public void updatePreview() {		
		isVisible = points.size() == 1;
		if (isVisible) { 
			//	start point
			A = (GeoPoint) points.get(0);						   			
			A.getInhomCoords(coordsA);			                        
			view.toScreenCoords(coordsA);						
			line.setLine(coordsA[0], coordsA[1], coordsA[0], coordsA[1]);                                   			                                            
		}
	}
	
	final public void updateMousePos(int x, int y) {		
		if (isVisible) { 											
			line.setLine(coordsA[0], coordsA[1], x, y);                                   			                                            
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		if (isVisible) {			            
			g2.setPaint(EuclidianView.colPreview);             
			g2.setStroke(objStroke);            
			g2.draw(line);                        		
		}
	}
	
	public void disposePreview() {	
	}
    
	final public boolean hit(int x,int y) {        
        return line.intersects(x-2, y-2, 4, 4);        
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
}
