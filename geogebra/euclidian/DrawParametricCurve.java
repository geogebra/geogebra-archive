/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.Function;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Draws graph of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawParametricCurve extends Drawable {
	
	// TODO: check discontinuities on the borders
	
	// maximum angle between two line segments
	private static final float MAX_TAN_ANGLE = 0.07f;
	
	// in pixel
	private static final float MAX_STEP = 8;
	private static final float INIT_MIN_STEP = 0.001f;
	private static final float STEP_INCREMENT = 0.5f;
	private static final float STEP_DIVISOR = 2f;
	private static final float MAX_SLOPE_BEND = 0.07f;
   
    GeoFunction f;
    
	GeneralPath gp = new GeneralPath();
    boolean isVisible, labelVisible;
   
    public DrawParametricCurve(EuclidianView view, GeoFunction f) {
    	this.view = view;
        this.f = f;
        geo = f;
        update();
    }
    
    // for DrawIntegral
    DrawParametricCurve() {
    }
    
    private boolean checkAngle(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
    	double vx = p1.x - p2.x;
    	double vy = p1.y - p2.y;
    	double wx = p3.x - p2.x;
    	double wy = p3.y - p2.y;
    	
    	// |v| * |w| * sin(alpha) = det(v, w)
    	// cos(alpha) = v . w / (|v| * |w|)
    	// tan(alpha) = sin(alpha) / cos(alpha)
    	// => tan(alpha) = det(v, w) / v . w    	    	
    	double det = vx * wy - vy * wx;
    	double prod = vx * wx + vy * wy;    	    
    	double value = Math.atan2(det, prod);   
    	return value < MAX_TAN_ANGLE;
    }

    final public void update() {				   
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(f);
		
		
		double a = view.xmin;
		double b = view.xmax;
		
		// is function limited to interval?
		Function fun = f.getFunction();
		if (fun != null && fun.interval) {
			a = Math.max(a, fun.a);
			b = Math.min(b, fun.b);
		}
		
		gp.reset();
		Point p = plotFunction(f, a, b, view, gp, labelVisible, true);		

		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
		}
		
		if (labelVisible) {
			xLabel = p.x;
			yLabel = p.y;
			labelDesc = geo.getLabelDescription();
			addLabelOffset();
		}
		
		// draw trace
		if (f.trace) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null) drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}
    }
    
    /**
     * Draws the graph of f between a and b to gp. (Note: a < b) 
     * @param f
     * @param a: left x value in real wold coords
     * @param b: right x value in real wold coords
     * @param view
     * @param gp: generalpath the can be drawn afterwards
     * @param calcLabelPos: whether label position should be calculated and returned
     * @param moveToAllowed: whether moveTo() may be used for gp
     * @return label position as Point     
     */
	final public static Point plotFunction(GeoFunction f,
								double aRW, double bRW, EuclidianView view, 
								GeneralPath gp, boolean calcLabelPos, 
								boolean moveToAllowed) {	
									
			int sign = (aRW <= bRW) ? 1 : -1;									
    										    		
			double xRW, yRW;
			float MIN_STEP = INIT_MIN_STEP;
			float y, yPrev, slope, slopePrev;
			float x, xPrev, step, xLabel = 0, yLabel = 0;
			boolean onScreen;
			boolean onScreenPrev;
			boolean valid; 
			boolean validPrev;
			boolean continous;       
			boolean needLabelPos = calcLabelPos;     		         		  						
		
			// init values						
			int b = view.toScreenCoordX(bRW);
			step = sign * 2;
			y = 0;
			onScreen = false;
			valid = false;			
			
			// FIRST POINT
			xRW = aRW;
			x = view.toScreenCoordX(xRW);
			// evaluate function									
			yRW = f.evaluate(xRW);		
			y = (float) (view.yZero - yRW * view.yscale);	
						
			// check if result is valid
			if (Float.isNaN(y)) {
				valid = false;
				onScreen = false;															
			} 
			else {
				// standard case: valid result
				// convert to screen coords, result on screen?
				valid = true;
				if (y < 0) {
					onScreen = false;
					y = -1;
				} else if (y > view.height) {
					onScreen = false;
					y = view.height + 1;
				}
				else {
					onScreen = true;
				}
			}											
													
			// draw FIRST POINT									
			if (onScreen) { // valid and on screen																		
				if (moveToAllowed) {
					gp.moveTo(x, y); // move to first point
				} else {
					gp.lineTo(x, y); // line to first point				
				}
			} else { // not on screen
				if (valid) {
					if (moveToAllowed) {
						gp.moveTo(x, y);																	
					} else {
						gp.lineTo(x, y); // inside screen -> outside screen											
					}				
				}							 							
			}
				
			//	store first point values
			validPrev = valid;
			onScreenPrev = onScreen;				 
			xPrev = x;
			yPrev = y;			
			slopePrev = 0;
			slope = 0;
									
			
			int COUNTER = 0;
			
			// plotting loop: x from a to b
			// (x, y) in screen coorrds
			// (xRW, yRW) in real world coords					 											
			for (x += step; 
				  (sign == 1 && x < b) || (sign == -1 && x > b); 
			      x += step) 
			{	
				//System.out.println("step: " + step);
				
				// avoid too many plot loopings
				COUNTER++;
				//if (COUNTER > 5000) MIN_STEP = 1;
																	 			
				// evaluate function
				xRW = view.toRealWorldCoordX(x);						
				yRW = f.evaluate(xRW);			
				y = (float) (view.yZero - yRW * view.yscale);
				continous = true;
					
				// check if result is valid
				if (Float.isNaN(y)) 
				{
					// step from valid to invalid
					if (validPrev && Math.abs(step) > MIN_STEP) {
						//	try again with smaller step
						x = xPrev;			
						step /= STEP_DIVISOR;
						//System.out.println("valid -> invalid: step: " + step);
						//System.out.println("x = " + x + " step = " + step);																				 
						continue;
					} else {
						step = sign * Math.min(MAX_STEP, 
								Math.abs(step) + STEP_INCREMENT);
					}
					valid = false;
					onScreen = false;
				} 
				else {
					// step from invalid to valid
					if (!validPrev && Math.abs(step) > MIN_STEP) {
						//	try again with smaller step
						x = xPrev;			
						step /= STEP_DIVISOR;
						//System.out.println("invalid -> valid: step: " + step);
						//System.out.println("\ndy = " + Math.abs(y - yPrev));
						//System.out.println("x = " + x + " step = " + step);																			 
						continue;
					} 
		
					
					// standard case: valid result
					// convert to screen coords, result on screen?
					valid = true;
					if (y < 0) {
						onScreen = false;
						y = -1;
					} else if (y > view.height) {
						onScreen = false;
						y = view.height + 1;
					}
					else {
						onScreen = true;
					}														
					
					// avoid big slope changes (bends)
					// if the bend is too big, turn down step																			
					slope = (y - yPrev) / (x - xPrev);
					double absSlope = Math.abs(slope);
					
					continous = !validPrev ||
						Math.abs((slope - slopePrev)) <
						Math.max(1, absSlope) *  MAX_SLOPE_BEND;
					if (continous) {							
						step = sign * Math.min(MAX_STEP, 
								Math.abs(step) + STEP_INCREMENT);	
					} else {							
						if (Math.abs(step) > MIN_STEP) {
							//	try again with smaller step
							x = xPrev;			
							step /= STEP_DIVISOR;
							//System.out.println("not continous: step: " + step);
							//System.out.println("slope: " + slope + ", x: " + x);
							//System.out.println("slopePrev: " + slopePrev);
							//System.out.println("\ndy = " + Math.abs(y - yPrev));
							//System.out.println("x = " + x + " step = " + step);																		 
							continue;
						}
					} 						
				}
												
				// line drawing									
				if (onScreen) { // valid and on screen
					if (needLabelPos) {
						xLabel = x + 10;
						if (xLabel < 20) xLabel = 5;					
					
						yLabel = (int) (y + 15);		
						if (yLabel < 40) 
							yLabel = 15;
						else if (yLabel > view.height - 30) 
							yLabel = view.height - 5;
						needLabelPos = false;
					}
				
					if (onScreenPrev) {	
						if (continous || !moveToAllowed)
							gp.lineTo(x, y); // inside screen -> inside screen
						else
							gp.moveTo(x, y); // inside screen -> inside screen
					} else {
						if (validPrev && continous || !moveToAllowed) {						
							gp.lineTo(x, y); // outside screen -> inside screen						
						} else {
							gp.moveTo(x, y); // invalid -> inside screen
						}
					}					
				} else { // not on screen
					if (valid) {
						if (onScreenPrev && continous || !moveToAllowed) {						
							gp.lineTo(x, y); // inside screen -> outside screen						
						} else {
							gp.moveTo(x, y); // outside screen -> outside screen											
						}
					} 
					//else not valid: do nothing					
				}						
				
				//	store previous values
				validPrev = valid;
				onScreenPrev = onScreen;				 
				xPrev = x;
				yPrev = y;					
				slopePrev = slope;
			} // for loop			
			
			// LAST POINT			
			x = b;
			xRW = bRW;
			// evaluate function									
			yRW = f.evaluate(xRW);			
			y = (float) (view.yZero - yRW * view.yscale);	
											
			// check if LAST POINT is valid
			if (Float.isNaN(y)) {
				valid = false;
				onScreen = false;															
			} 
			else {
				// standard case: valid result
				// convert to screen coords, result on screen?
				valid = true;
				if (y < 0) {
					onScreen = false;
					y = -1;
				} else if (y > view.height) {
					onScreen = false;
					y = view.height + 1;
				}
				else {
					onScreen = true;
				}
			}																				
													
			// draw LAST POINT																				
			if (onScreen) { // valid and on screen			
				if (onScreenPrev) {		
					gp.lineTo(x, y); // inside screen -> inside screen
				} else {
					if (validPrev) {						
						gp.lineTo(x, y); // outside screen -> inside screen						
					} else if (moveToAllowed) {
						gp.moveTo(x, y); // invalid -> inside screen
					}
				}					
			} else { // not on screen				
				if (valid) {
					if (onScreenPrev || !moveToAllowed) {						
						gp.lineTo(x, y); // inside screen -> outside screen						
					} 
				} 					
			}		
			
			//System.out.println("plot loop: " + COUNTER);
										
			if (calcLabelPos) 
				return new Point((int) xLabel, (int) yLabel);
			else
				return null;
	}        
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {
        	try {
	            if (geo.doHighlighting()) {
	                g2.setPaint(f.selColor);
	                g2.setStroke(selStroke);            
	                g2.draw(gp);           
	            } 
        	} catch (Exception e) {
        		System.err.println(e.getMessage());
        	}
            
			try {
            	g2.setPaint(f.objColor);
				g2.setStroke(objStroke);                                   
				g2.draw(gp);     
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);
            }        
        }
    }
    
	final void drawTrace(Graphics2D g2) {
	   try {
		   g2.setPaint(f.objColor);
		   g2.setStroke(objStroke);                                   
			g2.draw(gp);    
	   } catch (Exception e) {
		   System.err.println(e.getMessage());
	   }    							 
	}
    
	final public boolean hit(int x,int y) {  
    	if (!isVisible) return false;
    	double rwX = view.toRealWorldCoordX(x);
    	double rwY = view.toRealWorldCoordY(y);
    	double maxError = 7 * view.invYscale; // pixel
    	double yVal =  f.evaluate(rwX);  	
        return Math.abs(yVal - rwY) <= maxError;      
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
