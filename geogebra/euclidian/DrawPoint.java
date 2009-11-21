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

import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoIntersectConics;
import geogebra.kernel.AlgoIntersectLineConic;
import geogebra.kernel.AlgoIntersectLines;
import geogebra.kernel.AlgoIntersectSingle;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;


/**
 *
 * @author  Markus
 * @version 
 */
public final class DrawPoint extends Drawable {
	 	
	private  int HIGHLIGHT_OFFSET, SELECTION_OFFSET;
	
	private static int SELECTION_DIAMETER_MIN = 20;
	       
    private GeoPoint P;    
    
	private int diameter, hightlightDiameter, selDiameter, pointSize;
    boolean isVisible, labelVisible;   
    // for dot and selection
	private Ellipse2D.Double circle = new Ellipse2D.Double();
	private Ellipse2D.Double circleHighlight = new Ellipse2D.Double();
	private Ellipse2D.Double circleSel = new Ellipse2D.Double();
	private Line2D.Double line1, line2, line3, line4;// for cross
	GeneralPath gp = null;
    
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
    	
    	if (gp != null) gp.reset(); // stop trace being left when (filled diamond) point moved
    	
        isVisible = geo.isEuclidianVisible();       				 
    	// still needs updating if it's being traced to the spreadsheet
        if (!isVisible && !P.getSpreadsheetTrace()) return;
		labelVisible = geo.isLabelVisible();
        		
        // compute lower left corner of bounding box
	    double [] coords = new double[2];
        P.getInhomCoords(coords);                    
        
        // convert to screen
		view.toScreenCoords(coords);	
		
		// point outside screen?
        if (coords[0] > view.width + P.pointSize || coords[0] < -P.pointSize ||
        	coords[1] > view.height + P.pointSize || coords[1] < -P.pointSize)  
        {
        	isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
        }
        
        if (pointSize != P.pointSize) {
        	pointSize = P.pointSize;
			diameter = 2 * pointSize;
			HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			//HIGHLIGHT_OFFSET = pointSize / 2 + 1;
			hightlightDiameter =  diameter + 2 * HIGHLIGHT_OFFSET ;	
			
			selDiameter = hightlightDiameter;
			
			if (selDiameter < SELECTION_DIAMETER_MIN)
				selDiameter = SELECTION_DIAMETER_MIN;
			
			SELECTION_OFFSET = (selDiameter - diameter) / 2;

        }        
         		
        double xUL = coords[0] - pointSize;
        double yUL = coords[1] - pointSize;   

    	
    	// Florian Sonner 2008-07-17
    	int pointStyle = P.getPointStyle();
    	
    	if(pointStyle == -1)
    		pointStyle = view.pointStyle;
    	
    	double root3over2;
    	
        switch (pointStyle) {	       		        
    	case EuclidianView.POINT_STYLE_FILLED_DIAMOND:        		
    	    double xR = coords[0] + pointSize;        		
    		double yB = coords[1] + pointSize;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}        		
    		gp.moveTo((float)(xUL+xR)/2, (float)yUL);
    		gp.lineTo((float)xUL, (float)(yB + yUL)/2);
    		gp.lineTo((float)(xUL+xR)/2, (float)yB);
    		gp.lineTo((float)xR, (float)(yB + yUL)/2);
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:        		
    	case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:
    		
    		double direction = 1.0;
    		if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_NORTH)
    			direction = -1.0;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}        		
    		root3over2 = Math.sqrt(3.0) / 2.0;
    		gp.moveTo((float)coords[0], (float)(coords[1] + direction * pointSize));
    		gp.lineTo((float)(coords[0] + pointSize * root3over2), (float)(coords[1] - direction * pointSize/2));
    		gp.lineTo((float)(coords[0] - pointSize * root3over2), (float)(coords[1] - direction * pointSize/2));
    		gp.lineTo((float)coords[0], (float)(coords[1] + direction * pointSize));
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_TRIANGLE_EAST:        		
    	case EuclidianView.POINT_STYLE_TRIANGLE_WEST:
    		
    		direction = 1.0;
    		if (pointStyle == EuclidianView.POINT_STYLE_TRIANGLE_WEST)
    			direction = -1.0;
    		
    		if (gp == null) {
    			gp = new GeneralPath();
    		}     
    		root3over2 = Math.sqrt(3.0) / 2.0;   		
    		gp.moveTo((float)(coords[0] + direction * pointSize), (float)coords[1]);
    		gp.lineTo((float)(coords[0] - direction * pointSize/2), (float)(coords[1] + pointSize * root3over2));
    		gp.lineTo((float)(coords[0] - direction * pointSize/2), (float)(coords[1] - pointSize * root3over2));
    		gp.lineTo((float)(coords[0] + direction * pointSize), (float)coords[1]);
    		gp.closePath();
    		break;
    		
    	case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		if (line3 == null) {
    			line3 = new Line2D.Double();
    			line4 = new Line2D.Double();
    		}        		
    		line1.setLine((xUL+xR)/2, yUL, xUL, (yB + yUL)/2);
    		line2.setLine(xUL, (yB + yUL)/2, (xUL+xR)/2, yB);
    		line3.setLine((xUL+xR)/2, yB, xR, (yB + yUL)/2);
    		line4.setLine(xR, (yB + yUL)/2, (xUL+xR)/2, yUL);
    		break;
    		        	
    	case EuclidianView.POINT_STYLE_PLUS:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		line1.setLine((xUL+xR)/2, yUL, (xUL+xR)/2, yB);
    		line2.setLine(xUL, (yB + yUL)/2, xR, (yB + yUL)/2);
    		break;
    		        	
    	case EuclidianView.POINT_STYLE_CROSS:        		
    	    xR = coords[0] + pointSize;        		
    		yB = coords[1] + pointSize;
    		
    		if (line1 == null) {
    			line1 = new Line2D.Double();
    			line2 = new Line2D.Double();
    		}        		
    		line1.setLine(xUL, yUL, xR, yB);
    		line2.setLine(xUL, yB, xR, yUL); 
    		break;
    		        	
        	case EuclidianView.POINT_STYLE_CIRCLE:
        		break;
        		
        	// case EuclidianView.POINT_STYLE_DOT:
        	//default:			        		        			        		    
        }    
        
        // circle might be needed at least for tracing
        circle.setFrame(xUL, yUL, diameter, diameter);
        
        // selection area
        circleHighlight.setFrame(xUL - HIGHLIGHT_OFFSET, 
				yUL - HIGHLIGHT_OFFSET, hightlightDiameter, hightlightDiameter);
		
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
		
		if (isVisible && labelVisible) {      
			labelDesc = geo.getLabelDescription();
			xLabel = (int) Math.round(coords[0] + 4);
			yLabel = (int)  Math.round(yUL - pointSize);    
			addLabelOffset(true);
		}    				
    }
    
    private Drawable drawable;
    
    private void drawClippedSection(GeoElement geo, Graphics2D g2) {
		double [] coords = new double[2];
        P.getInhomCoords(coords);                    
		
        view.toScreenCoords(coords);
        
		Ellipse2D.Float circle = new Ellipse2D.Float((int)coords[0] - 30, (int)coords[1] - 30, 60, 60);
		g2.clip((Shape)circle);
   	
    	switch (geo.getGeoClassType()) {
    	case GeoElement.GEO_CLASS_LINE:
    		drawable = new DrawLine(view, (GeoLine)geo);
    		break;
    	case GeoElement.GEO_CLASS_SEGMENT:
    		drawable = new DrawSegment(view, (GeoSegment)geo);
    		break;
    	case GeoElement.GEO_CLASS_CONIC:
    		drawable = new DrawConic(view, (GeoConic)geo);
    		break;
    	case GeoElement.GEO_CLASS_CONICPART:
    		drawable = new DrawConicPart(view, (GeoConicPart)geo);
    		break;
    		
    		default:
    			drawable = null;
    			Application.debug("unsupported type for restriced drawing "+geo.getClass()+"");

    	}
   		
    	if (drawable != null) {
			geo.setEuclidianVisible(true);
			drawable.update();
			drawable.draw(g2);
			geo.setEuclidianVisible(false);
			g2.setClip(null);
    	}
    	
    }

    final public void draw(Graphics2D g2) {   
        if (isVisible) { 
        	if (geo.doHighlighting()) {           
    		 	g2.setPaint(geo.getSelColor());		
    		 	g2.fill(circleHighlight);  
            }
        	
        	
        	/* TODO: add option "show trimmed intersecting lines" and use this
        	AlgoElement algo = geo.getParentAlgorithm();
        	
        	if ( algo instanceof AlgoIntersectLines 
            		|| algo instanceof AlgoIntersectLineConic
            		|| algo instanceof AlgoIntersectSingle
    			|| algo instanceof AlgoIntersectConics){
        		GeoElement[] geos = algo.getInput();
        		drawClippedSection(geos[0],g2);
        		drawClippedSection(geos[1],g2);
        	} */
        	
        	// Florian Sonner 2008-07-17
        	int pointStyle = P.getPointStyle();
        	
        	if(pointStyle == -1)
        		pointStyle = view.pointStyle;
        	
            switch (pointStyle) {
        	case EuclidianView.POINT_STYLE_PLUS:            		                     
        	case EuclidianView.POINT_STYLE_CROSS:            		                     
         		// draw cross like: X or +     
                g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));            
                g2.draw(line1);                              
                g2.draw(line2);             		
        		break;
        		
        	case EuclidianView.POINT_STYLE_EMPTY_DIAMOND:            		                     
         		// draw diamond    
                g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));            
                g2.draw(line1);                              
                g2.draw(line2);             		
                g2.draw(line3);                              
                g2.draw(line4);             		
        		break;
        		
        	case EuclidianView.POINT_STYLE_FILLED_DIAMOND:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_NORTH:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_SOUTH:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_EAST:            		                     
        	case EuclidianView.POINT_STYLE_TRIANGLE_WEST:            		                     
         		// draw diamond    
                g2.setPaint(geo.getObjectColor());
                g2.setStroke(getCrossStroke(pointSize));  
                drawWithValueStrokePure(gp, g2);
				g2.fill(gp);    
        		break;
        		

        		
            	case EuclidianView.POINT_STYLE_CIRCLE:
            		// draw a circle            		
        			g2.setPaint(geo.getObjectColor());	
        			g2.setStroke(getCrossStroke(pointSize));
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
	 			g2.setStroke(getCrossStroke(pointSize));
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
    
    /*
     * pointSize can be more than 9 (set from JavaScript, SetPointSize[])
     */
    final private BasicStroke getCrossStroke(int pointSize) {
    	
    	if (pointSize > 9)
    		return new BasicStroke(pointSize/2f); 
    	
		if (crossStrokes[pointSize] == null)
			crossStrokes[pointSize] = new BasicStroke(pointSize/2f); 
		
		return crossStrokes[pointSize];

    }

}


