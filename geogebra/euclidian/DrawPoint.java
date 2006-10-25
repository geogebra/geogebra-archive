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
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;


/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawPoint extends Drawable {
	 	
	private  int SELECTION_OFFSET;
	       
    private GeoPoint P;    
    
	private int diameter, selDiameter, pointSize;
    boolean isVisible, labelVisible;   
	private Ellipse2D.Double circle, circleSel; // for dot and selection
	private Line2D.Double line1, line2;// for cross	
    private double [] coords = new double[2];
    
    private static BasicStroke borderStroke = EuclidianView.getDefaultStroke();
    private static BasicStroke [] crossStrokes = new BasicStroke[10];
    
    /** Creates new DrawPoint */
    public DrawPoint(EuclidianView view, GeoPoint P) {      
    	this.view = view;          
        this.P = P;
        geo = P;
        
        crossStrokes[1] = new BasicStroke(1f);

        update();
    }
    
    final public void update() {       
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();
        		
        // compute lower left corner of bounding box
        P.getInhomCoords(coords);     
        
        // point outside screen?
        if (coords[0] > view.xmax || coords[0] < view.xmin
        	|| coords[1] > view.ymax || coords[1] < view.ymin)  
        {
        	isVisible = false;
        	return;	
        }
        
        // convert to screen
		view.toScreenCoords(coords);						
        
        if (pointSize != P.pointSize) {
        	pointSize = P.pointSize;
			diameter = 2 * pointSize;
			//selDiameter =  diameter + SELECTION_DIAMETER_ADD;
			SELECTION_OFFSET = pointSize / 2 + 1;
			selDiameter =  diameter + 2 * SELECTION_OFFSET ;			
        }        
         		
        double xUL = coords[0] - pointSize;
        double yUL = coords[1] - pointSize;   
        
        switch (view.pointStyle) {	       		        
        	case EuclidianView.POINT_STYLE_CROSS:        		
        	    double xR = coords[0] + pointSize;        		
        		double yB = coords[1] + pointSize;
        		
        		if (line1 == null) {
        			line1 = new Line2D.Double();
        			line2 = new Line2D.Double();
        		}        		
        		line1.setLine(xUL, yUL, xR, yB);
        		line2.setLine(xUL, yB, xR, yUL);
        		
        		if (crossStrokes[pointSize] == null)
        			crossStrokes[pointSize] = new BasicStroke(pointSize/2f); 
        		break;
        		        	
        	case EuclidianView.POINT_STYLE_CIRCLE:
        		if (crossStrokes[pointSize] == null)
        			crossStrokes[pointSize] = new BasicStroke(pointSize/2f); 
        	
        	// case EuclidianView.POINT_STYLE_DOT:
        	default:			        
		        if (circle == null) {
		        	circle = new Ellipse2D.Double();
		        	circleSel = new Ellipse2D.Double();
		        }		        
		        
		        circle.setFrame(xUL, yUL, diameter, diameter); 
		        circleSel.setFrame(xUL - SELECTION_OFFSET, 
						yUL - SELECTION_OFFSET, selDiameter, selDiameter);
        }                                 
		
		// draw trace
		if (P.trace) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}
		
		if (labelVisible) {      
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int)  Math.round(yUL - pointSize);    
			addLabelOffset();           
		}    
    }

    final public void draw(Graphics2D g2) {   
        if (isVisible) { 
            switch (view.pointStyle) {
            	case EuclidianView.POINT_STYLE_CROSS:
            		 if (geo.doHighlighting()) {           
            		 	g2.setPaint(geo.selColor);		
        				g2.fill(circle);
                    }
                    
             		// draw cross like: X     
                    g2.setPaint(geo.objColor);
                    g2.setStroke(crossStrokes[pointSize]);            
                    g2.draw(line1);                              
                    g2.draw(line2);             		
            		break;
            		
            	case EuclidianView.POINT_STYLE_CIRCLE:
            		// draw a circle
            		if (geo.doHighlighting()) {
        				g2.setPaint(geo.selColor);		
        				g2.fill(circleSel);       
                    } 		
        			g2.setPaint(geo.objColor);	
        			g2.setStroke(crossStrokes[pointSize]);
        			g2.draw(circle);  										                                                               		
           		break;
            	
           		// case EuclidianView.POINT_STYLE_CIRCLE:
            	default:
            		// draw a dot
            		if (geo.doHighlighting()) {
        				g2.setPaint(geo.selColor);		
        				g2.fill(circleSel);       
                    } 		
        			g2.setPaint(geo.objColor);	
        			g2.fill(circle);  										           
                    
                    // black stroke        	
                    g2.setPaint(Color.black);
                    g2.setStroke(borderStroke);
        			g2.draw(circle);          			
            }    		        	
                  

            // label   
            if (labelVisible) {
				g2.setFont(view.fontPoint);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);                   
            }                         
        }
    }
    
    final void drawTrace(Graphics2D g2) {
		g2.setPaint(geo.objColor);
		g2.fill(circle);	
    }

    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return circleSel.contains(x, y);        
    }
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


