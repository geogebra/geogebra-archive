/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
    // for dot and selection
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Ellipse2D.Double circleSel = new Ellipse2D.Double();
	private Line2D.Double line1, line2;// for cross	
    
    private static BasicStroke borderStroke = EuclidianView.getDefaultStroke();
    private static BasicStroke [] crossStrokes = new BasicStroke[10];
    
    /** Creates new DrawPoint */
    public DrawPoint(EuclidianView view, GeoPoint P) {      
    	this.view = view;          
        this.P = P;
        geo = P;
        
        //crossStrokes[1] = new BasicStroke(1f);

        update();
    }
    
    final public void update() {       
        isVisible = geo.isEuclidianVisible();       				 
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();
        		
        // compute lower left corner of bounding box
	    double [] coords = new double[2];
        P.getInhomCoords(coords);                    
        
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

    	
    	// Florian Sonner 2008-07-17
    	int pointStyle = P.getPointStyle();
    	
        switch (pointStyle) {	       		        
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
        		break;
        		
        	// case EuclidianView.POINT_STYLE_DOT:
        	//default:			        		        			        		    
        }    
        
        // circle might be needed at least for tracing
        circle.setFrame(xUL, yUL, diameter, diameter);
        
        // selection area
        circleSel.setFrame(xUL - SELECTION_OFFSET, 
				yUL - SELECTION_OFFSET, selDiameter, selDiameter);
		
        if (P.spreadsheetTrace) recordToSpreadsheet(P); 
        
		if (P == view.getEuclidianController().recordObject)
		    recordToSpreadsheet(P);

        
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
		
		// point outside screen?
        if (coords[0] > view.width || coords[0] < 0
        	|| coords[1] > view.height || coords[1] < 0)  
        {
        	isVisible = false;        		
        }
    }

    final public void draw(Graphics2D g2) {   
        if (isVisible) { 
        	if (geo.doHighlighting()) {           
    		 	g2.setPaint(geo.getSelColor());		
    		 	g2.fill(circleSel);  
            }
        	
        	// Florian Sonner 2008-07-17
        	int pointStyle = P.getPointStyle();
        	
            switch (pointStyle) {
            	case EuclidianView.POINT_STYLE_CROSS:            		                     
             		// draw cross like: X     
                    g2.setPaint(geo.getObjectColor());
                    g2.setStroke(crossStrokes[pointSize]);            
                    g2.draw(line1);                              
                    g2.draw(line2);             		
            		break;
            		
            	case EuclidianView.POINT_STYLE_CIRCLE:
            		// draw a circle            		
        			g2.setPaint(geo.getObjectColor());	
        			g2.setStroke(crossStrokes[pointSize]);
        			g2.draw(circle);  										                                                               		
           		break;
            	
           		// case EuclidianView.POINT_STYLE_CIRCLE:
            	default:
            		// draw a dot            			
        			g2.setPaint(geo.getObjectColor());	
        			g2.fill(circle);  										           
                    
                    // black stroke        	
                    g2.setPaint(Color.black);
                    g2.setStroke(borderStroke);
        			g2.draw(circle);          			
            }    		        	
                  

            // label   
            if (labelVisible) {
				g2.setFont(view.fontPoint);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);			
            }                         
        }
    }
    
    final void drawTrace(Graphics2D g2) {
    	g2.setPaint(geo.getObjectColor());
    	
    	
    	// Florian Sonner 2008-07-17
    	int pointStyle = P.getPointStyle();
    	
		switch (pointStyle) {
	     	case EuclidianView.POINT_STYLE_CIRCLE:
	 			g2.setStroke(crossStrokes[pointSize]);
	 			g2.draw(circle);  										                                                               		
	    		break;
	     	
	     	case EuclidianView.POINT_STYLE_CROSS:
	     	default: // case EuclidianView.POINT_STYLE_CIRCLE:	     		
	 			g2.fill(circle);  										           	     	
	     }    		       
    }

    /**
     * was this object clicked at? (mouse pointer
     * location (x,y) in screen coords)
     */
    final public boolean hit(int x, int y) {
        return circleSel.contains(x, y);        
    }
    
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(circleSel.getBounds());  
    }
    
    /**
	 * Returns the bounding box of this DrawPoint in screen coordinates.	 
	 */
	final public Rectangle getBounds() {				
		// return selection circle's bounding box
		if (!geo.isEuclidianVisible())
			return null;
		else 
			return circleSel.getBounds();		
	}
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


