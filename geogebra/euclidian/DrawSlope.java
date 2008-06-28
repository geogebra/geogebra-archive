/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawSlope: draws the slope triangle for the slope of a line
 */

package geogebra.euclidian;

import geogebra.kernel.AlgoSlope;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class DrawSlope extends Drawable {
   
    private GeoNumeric slope;
    private GeoLine g;
     
    boolean isVisible, labelVisible;
    int xLabelHor, yLabelHor;
    String horLabel; // horizontal label, i.e. triangleSize    
    
    private double [] coords = new double[2];
    private GeneralPath gp = new GeneralPath();  
    private Kernel kernel;             
    
    public DrawSlope(EuclidianView view, GeoNumeric slope) {
        this.view = view;
        kernel = view.getKernel();
        this.slope = slope;
        geo = slope;
   
        slope.setDrawable(true);
        
        // get parent line
        g = ((AlgoSlope)slope.getParentAlgorithm()).getg();
        update();
    }

    final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (isVisible) {   
        	int slopeTriangleSize = slope.getSlopeTriangleSize();
            double rwHeight = slope.getValue() * slopeTriangleSize;
            double height =  view.yscale * rwHeight;
            if (Math.abs(height) > Float.MAX_VALUE) {
                isVisible = false;
                return;
            }
        
            // get point on line g
            g.getInhomPointOnLine(coords);              
            if (g.getStartPoint() == null) {
            	// get point on y-axis and line g
            	coords[0] = 0.0d;
            	coords[1] = -g.z / g.y;
            }
            view.toScreenCoords(coords);
            
            // draw slope triangle       
            float x = (float) coords[0];
            float y = (float) coords[1];
            float xright = (float) (x + view.xscale * slopeTriangleSize);
            gp.reset(); 
            gp.moveTo(x, y);
            gp.lineTo(xright, y);
            gp.lineTo(xright, y - (float) height);      
            
        	// gp on screen?		
    		if (!gp.intersects(0,0, view.width, view.height)) {				
    			isVisible = false;
    			return;
    		}		
                                   
            // label position
            labelVisible = geo.isLabelVisible();       
            if (labelVisible) {     
                if (slopeTriangleSize > 1) {    
                    StringBuffer sb = new StringBuffer();
                    switch (slope.getLabelMode()) {
                        case GeoElement.LABEL_NAME_VALUE:
                            sb.append(slopeTriangleSize);   
                            sb.append(' ');
                            sb.append(geo.getLabel());
                            sb.append(" = ");
                            sb.append(kernel.format(rwHeight));
                            break;
                            
                        case GeoElement.LABEL_VALUE:
                            sb.append(kernel.format(rwHeight));
                            break;
                            
                        default: //case GeoElement.LABEL_NAME:
                            sb.append(slopeTriangleSize);   
                            sb.append(' ');
                            sb.append(geo.getLabel());
                            break;
                    }
                    labelDesc = sb.toString();
                } else {
                    labelDesc = geo.getLabelDescription();      
                }     
                yLabel = (int) (y - height / 2.0f + 6);
                xLabel = (int) (xright) + 5;   
                addLabelOffset();    
                  
                // position off horizontal label (i.e. slopeTriangleSize)
                xLabelHor = (int) ((x + xright) /2.0);
                yLabelHor = (int) (y + view.fontSize + 2);
                StringBuffer sb = new StringBuffer();
                sb.append(slopeTriangleSize);
                horLabel = sb.toString();
            }               
            updateStrokes(slope);                                        
        }
    }
    
    final public void draw(Graphics2D g2) {
        if (isVisible) {        
            if (geo.alphaValue > 0.0f) {
                g2.setPaint(geo.getFillColor());                       
                g2.fill(gp);  
            }
                
            if (geo.doHighlighting()) {
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);            
                g2.draw(gp);       
            }    
            
            g2.setPaint(slope.getObjectColor());             
            g2.setStroke(objStroke);            
            g2.draw(gp);      
                        
            if (labelVisible) {
                g2.setPaint(slope.getLabelColor());
                g2.setFont(view.fontLine);
                drawLabel(g2);              
                g2.drawString(horLabel, xLabelHor, yLabelHor);
            }           
        }
    }
    
    final public boolean hit(int x,int y) {        
        return false;        
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
    
    /**
	 * Returns the bounding box of this Drawable in screen coordinates.	 
	 */
	final public Rectangle getBounds() {		
		if (!geo.isDefined())
			return null;
		else 
			return gp.getBounds();	
	}
    
}
