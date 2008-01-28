/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * DrawPoint.java
 *
 * Created on 11. Oktober 2001, 23:59
 */

package geogebra.euclidian3D;

import geogebra.kernel.GeoElement;
import geogebra.kernel3D.GeoPoint3D;
//import geogebra.euclidian.*;

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
public final class DrawPoint3D extends Drawable3D {
	 	
	private  int SELECTION_OFFSET;
	       
    private GeoPoint3D P;    
    
	private int diameter, selDiameter, pointSize;
    boolean isVisible, labelVisible;   
    // for dot and selection
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Ellipse2D.Double circleSel = new Ellipse2D.Double();
	private Line2D.Double line1, line2;// for cross	
    private double [] coords = new double[3];
    
    private static BasicStroke borderStroke = EuclidianView3D.getDefaultStroke();
    private static BasicStroke [] crossStrokes = new BasicStroke[10];
    
    /** Creates new DrawPoint */
    public DrawPoint3D(EuclidianView3D view3D, GeoPoint3D P) {      
    	this.view3D = view3D;          
        this.P = P;
        geo = P;
        
        crossStrokes[1] = new BasicStroke(1f);

        update();
    }
    
    final public void update() {       
        //TODO isVisible = geo.isEuclidianVisible();  
    	isVisible=true;
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();
        		
        // compute lower left corner of bounding box
        P.getInhomCoords(coords);     
        
        // TODO point outside screen?
        /*
        if (coords[0] > view3D.getXmax() || coords[0] < view3D.getXmin()
        	|| coords[1] > view3D.getYmax() || coords[1] < view3D.getYmin())  
        {
        	isVisible = false;
        	return;	
        }*/
        
        // convert to screen
		view3D.toScreenCoords3D(coords);						
        
        if (pointSize != P.pointSize) {
        	pointSize = P.pointSize;
			diameter = 2 * pointSize;
			//selDiameter =  diameter + SELECTION_DIAMETER_ADD;
			SELECTION_OFFSET = pointSize / 2 + 1;
			selDiameter =  diameter + 2 * SELECTION_OFFSET ;			
        }        
         		
        double xUL = coords[0] - pointSize;
        double yUL = coords[1] - pointSize;   
        
        switch (view3D.getPointStyle()) {	       		        
        	case EuclidianView3D.POINT_STYLE_CROSS:        		
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
        		        	
        	case EuclidianView3D.POINT_STYLE_CIRCLE:
        		if (crossStrokes[pointSize] == null)
        			crossStrokes[pointSize] = new BasicStroke(pointSize/2f); 
        		break;
        		
        	// case Euclidianview3D.POINT_STYLE_DOT:
        	//default:			        		        			        		    
        }    
        
        // circle might be needed at least for tracing
        circle.setFrame(xUL, yUL, diameter, diameter);
        
        // selection area
        circleSel.setFrame(xUL - SELECTION_OFFSET, 
				yUL - SELECTION_OFFSET, selDiameter, selDiameter);
		
		// draw trace
		if (P.trace) {
			isTracing = true;
			Graphics2D g2 = view3D.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view3D.updateBackground();
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
        	if (geo.doHighlighting()) {           
    		 	g2.setPaint(geo.selColor);		
    		 	g2.fill(circleSel);  
            }
        	        	
            switch (view3D.getPointStyle()) {
            	case EuclidianView3D.POINT_STYLE_CROSS:            		                     
             		// draw cross like: X     
                    g2.setPaint(geo.objColor);
                    g2.setStroke(crossStrokes[pointSize]);            
                    g2.draw(line1);                              
                    g2.draw(line2);             		
            		break;
            		
            	case EuclidianView3D.POINT_STYLE_CIRCLE:
            		// draw a circle            		
        			g2.setPaint(geo.objColor);	
        			g2.setStroke(crossStrokes[pointSize]);
        			g2.draw(circle);  										                                                               		
           		break;
            	
           		// case Euclidianview3D.POINT_STYLE_CIRCLE:
            	default:
            		// draw a dot            			
        			g2.setPaint(geo.objColor);	
        			g2.fill(circle);  										           
                    
                    // black stroke        	
                    g2.setPaint(Color.black);
                    g2.setStroke(borderStroke);
        			g2.draw(circle);          			
            }    		        	
                  

            // label   
            if (labelVisible) {
				g2.setFont(view3D.fontPoint);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);			
            }                         
        }
    }
    
    final void drawTrace(Graphics2D g2) {
    	g2.setPaint(geo.objColor);
    	
		switch (view3D.getPointStyle()) {
	     	case EuclidianView3D.POINT_STYLE_CIRCLE:
	 			g2.setStroke(crossStrokes[pointSize]);
	 			g2.draw(circle);  										                                                               		
	    		break;
	     	
	     	case EuclidianView3D.POINT_STYLE_CROSS:
	     	default: // case Euclidianview3D.POINT_STYLE_CIRCLE:	     		
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
    
    final public GeoElement getGeoElement() {
        return geo;
    }    
    
    final public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    } 

}


