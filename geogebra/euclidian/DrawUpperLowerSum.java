/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Draws upper / lower sum of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawUpperLowerSum extends Drawable {
   
    private GeoNumeric sum;
    private NumberValue a, b; // interval borders

    boolean isVisible, labelVisible;     
    private AlgoFunctionAreaSums algo;   
    private GeneralPathClipped gp;
    private double [] coords = new double[2];
    private boolean trapeziums;
    private boolean histogram;
    private boolean boxplot, barchartFreqs;
   
    public DrawUpperLowerSum(EuclidianView view, GeoNumeric n) {
    	this.view = view; 	
    	sum = n;
		geo = n;
		
		n.setDrawable(true);
    	
    	algo = (AlgoFunctionAreaSums) n.getParentAlgorithm();    	
		this.trapeziums = algo.useTrapeziums();
		this.histogram = algo.isHistogram();
		this.boxplot = algo.isBoxPlot();
		this.barchartFreqs = algo.getType() == AlgoFunctionAreaSums.TYPE_BARCHART_FREQUENCY_TABLE;
        a = algo.getA();
        b = algo.getB();    
        update();
    }

    final public void update() {				   
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(sum);
		
		if (gp == null)
			gp = new GeneralPathClipped(view);
		
		if (boxplot)
		{
			updateBoxPlot();
			return;
		}
		
		if (barchartFreqs || histogram)
		{
			updateBarChart();
			return;
		}
		
		// init gp
		gp.reset();
		double aRW = a.getDouble();
		double bRW = b.getDouble();

		double ax = view.toScreenCoordXd(aRW);
		double bx = view.toScreenCoordXd(bRW);
		double y0 = view.yZero;
				
		// plot upper/lower sum rectangles
		int N = algo.getIntervals();		
		double [] leftBorder = algo.getLeftBorders();
		double [] yval = algo.getValues();
		
		// first point 						
		double x = ax;
		double y = y0;		
		gp.moveTo(x, y);					
		for (int i=0; i < N; i++) {
			coords[0] = leftBorder[i];						
			coords[1] = yval[i];
			view.toScreenCoords(coords);
			
			/* removed - so that getBounds() works
			// avoid too big y values
			if (coords[1] < 0 && !trapeziums) {
				coords[1] = -1;
			} else if (coords[1] > view.height && !trapeziums) {
				coords[1] = view.height + 1;
			}*/
			
			x = (double) coords[0];			
			
			if (trapeziums) gp.lineTo(x, coords[1]); // top
			else gp.lineTo(x, y); // top
			
			gp.lineTo(x, y0); // RHS
			gp.moveTo(x, y);
			y = (double) coords[1];
			gp.moveTo(x, y0);
			gp.lineTo(x, y);			 								
		} 	
		if (trapeziums)
		{
			coords[0] = leftBorder[N];						
			coords[1] = yval[N];
			view.toScreenCoords(coords);
			gp.lineTo(bx, (double) coords[1]); // last bar: top
		}
		else
		{
			gp.lineTo(bx, y); // last bar: top
		}
		
		if (histogram) gp.moveTo(bx, y0);
		else gp.lineTo(bx, y0);// last bar: right
		
		gp.lineTo(ax, y0);// all bars, along bottom		
		
		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		

		if (labelVisible) {
			xLabel = (int) Math.round((ax + bx) / 2) - 6;
			yLabel = (int) view.yZero - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
    }

    private void updateBoxPlot()
    {
		// init gp
		gp.reset();
		double yOff = a.getDouble();
		double yScale = b.getDouble();
	
		// plot upper/lower sum rectangles
		double [] leftBorder = algo.getLeftBorders();
		
		coords[0] = leftBorder[0];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[0];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[0];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[1];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[3];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = 0 + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[4];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[2];						
		coords[1] = yScale + yOff;
		view.toScreenCoords(coords);
		gp.moveTo((double)coords[0], (double)coords[1]);
			
		coords[0] = leftBorder[2];						
		coords[1] = -yScale + yOff;
		view.toScreenCoords(coords);
		gp.lineTo((double)coords[0], (double)coords[1]);
			

		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		

		if (labelVisible) {
			xLabel = (int)coords[0];
			yLabel = (int)coords[1] - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
    

    }
    private void updateBarChart() {
		gp.reset();
		double base = (double) view.yZero;
				
		int N = algo.getIntervals();		
		double [] leftBorder = algo.getLeftBorders();
		double [] yval = algo.getValues();
		
		gp.moveTo(view.toScreenCoordXd(leftBorder[0]), base);	
		
		for (int i = 0; i < N - 1; i++) {
			
			double x0 = view.toScreenCoordXd(leftBorder[i]);	
			double height = view.toScreenCoordYd(yval[i]);
			double x1 = view.toScreenCoordXd(leftBorder[i + 1]);
			
			gp.lineTo(x0, height); // up
			gp.lineTo(x1, height); // along
			gp.lineTo(x1, base); // down
			
		} 	
		
		gp.lineTo(view.toScreenCoordXd(leftBorder[0]), base);

		
		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
        	// don't return here to make sure that getBounds() works for offscreen points too
		}		

		if (labelVisible) {
			xLabel = (view.toScreenCoordX(leftBorder[0]) + view.toScreenCoordX(leftBorder[N-1])) / 2 - 6;
			yLabel = (int) view.yZero - view.fontSize;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}

    }

    final public void draw(Graphics2D g2) {
        if (isVisible) {
        	try {
	            if (geo.doHighlighting()) {
	                g2.setPaint(sum.getSelColor());
	                g2.setStroke(selStroke);            
	                g2.draw(gp);           
	            } 
        	} catch (Exception e) {
        		Application.debug(e.getMessage());
        	}
            
        	if (sum.isHatchingEnabled()) {
        		// use decoStroke as it is always full (not dashed/dotted etc)
                HatchingHandler.setHatching(g2, decoStroke, geo.getObjectColor(), sum.alphaValue, geo.getHatchingDistance(), geo.getHatchingAngle());
                g2.fill(gp);
        		
        	} else        	
        	if (sum.getFillColor().getAlpha() > 0) {
				try {
	            	g2.setPaint(sum.getFillColor());                                  
					g2.fill(gp);    
				} catch (Exception e) {
					Application.debug(e.getMessage());
				}   
        	}
			
			try {
				if (geo.lineThickness > 0) {
					g2.setPaint(sum.getObjectColor());
					g2.setStroke(objStroke);                                   
					g2.draw(gp);   
				}
			} catch (Exception e) {
				Application.debug(e.getMessage());
			}    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
            }        
        }
    }
    
	final public boolean hit(int x,int y) {  
	       return gp != null && (gp.contains(x, y) || gp.intersects(x-3, y-3, 6, 6));        
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
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		else 
			return (Rectangle) gp.getBounds();	
	}
}
