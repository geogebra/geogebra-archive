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

import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.Parametric2D;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Draws graph of a GeoFunction
 * @author  Markus Hohenwarter
 */
public class DrawParametricCurve extends Drawable {
	
	// TODO: check discontinuities on the borders
	
	// maximum distance between two plot points in pixels
	private static final int MAX_X_DISTANCE = 2; // pixels
	private static final int MAX_Y_DISTANCE = 2; // pixels

	// maximum number of plot points = 2^MAX_DEPTH
	private static final int MAX_DEPTH = 18; 

	// minimum depth of iterations to be reached
	// this ensures that closed curves are also drawn	
	private static final int MIN_POINTS = 10; // 
   
    private GeoCurveCartesian curve;    
	GeneralPath gp = new GeneralPath();
    boolean isVisible, labelVisible;   
   
    public DrawParametricCurve(EuclidianView view, GeoCurveCartesian curve) {
    	this.view = view;
        this.curve = curve;
        geo = curve;
        update();
    }
        
    /*
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
    }*/

    final public void update() {				   
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
		labelVisible = geo.isLabelVisible();            
		updateStrokes(curve);
						
		gp.reset();		
		Point p = plotCurve(curve, 
				curve.getMinParameter(), 
				curve.getMaxParameter(), 
				view, gp, labelVisible, true);		

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
		if (curve.getTrace()) {
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
     * Draws a parametric curve (x(t), y(t)) for t in [t1, t2]. 
     * @param gp: generalpath the can be drawn afterwards
     * @param calcLabelPos: whether label position should be calculated and returned
     * @param moveToAllowed: whether moveTo() may be used for gp
     * @return label position as Point
     * @author Markus Hohenwarter, based on an algorithm by John Gillam     
     */
	final public static Point plotCurve(Parametric2D curve,
								double t1, double t2, EuclidianView view, 
								GeneralPath gp,
								boolean calcLabelPos, 
								boolean moveToAllowed) {   
		
		boolean needLabelPos = calcLabelPos;
		Point labelPoint = null;
    	
		// this algorithm by John Gillam avoids multiple
		// evaluations of the curve for the same parameter value t
		// see an explanation of this algorithm at the end of this method
		double x0,y0,x,y,t;
		boolean onScreen, valid, prevValid;
		double [] eval = new double[2];
		
		int dyadicStack[]=new int[20];
		int depthStack[]=new int[20];
		double xStack[]=new double[20];
		double yStack[]=new double[20];
		boolean onScreenStack[]=new boolean[20];
		boolean validStack[]=new boolean[20];
		double divisors[]=new double[20];
		divisors[0]=t2-t1;
		for (int i=1;i<20;divisors[i]=divisors[i-1]/2,i++)
			;
		int i=1;
		dyadicStack[0]=1;
		depthStack[0]=0;

		curve.evaluate(t1, eval);
		view.toScreenCoords(eval);
		x0=eval[0]; // xEval(t1);
		y0=eval[1]; // yEval(t1);
		
		// with a GeneralPath moveTo(sx0,sy0) , the "screen" point
		if (moveToAllowed) 
			gp.moveTo((float) x0, (float) y0);
		else
			gp.lineTo((float) x0, (float) y0);			

		curve.evaluate(t2, eval);
		onScreenStack[0] = onScreen = view.toClippedScreenCoords(eval);
		xStack[0] = x = eval[0]; // xEval(t2);
		yStack[0] = y = eval[1]; // yEval(t2);	
		validStack[0] = valid = !(Double.isNaN(x) || Double.isNaN(y));		
		prevValid = valid;
		
		int top=1;
		int depth=0;
		int pointsCount = 1;
		do {
			boolean distNotOK = true;							
			while ( depth < MAX_DEPTH 
//					// TODO:think about this
					// && (valid || prevValid) 
					&&
					( 												
						(distNotOK = (Math.abs(x-x0) >= MAX_X_DISTANCE || Math.abs(y-y0) >= MAX_Y_DISTANCE))
						|| 
						pointsCount <= MIN_POINTS
					)
				  )				
			{
				// push stacks
				dyadicStack[top]=i; 
				depthStack[top]=depth;
				onScreenStack[top]=onScreen;
				validStack[top]=valid;
				xStack[top]=x; 
				yStack[top++]=y;
				i<<=1; i--;
				depth++;				
				t=t1+i*divisors[depth];  // t=t1+(t2-t1)*(i/2^depth)										
				
				// evaluate curve for parameter t
				curve.evaluate(t, eval);
				onScreen = view.toClippedScreenCoords(eval);
				x=eval[0]; // xEval(t);
				y=eval[1]; // yEval(t);
				valid = !(Double.isNaN(x) || Double.isNaN(y));	
				
				// TODO: remove
				if (!valid)
					System.out.println("not valid: " + t);
			}
			
			// drawLine(x0,y0,x,y);  // or with a GeneralPath lineTo(sx,sy)
			if (moveToAllowed && distNotOK) 
				gp.moveTo((float) x, (float) y);
			else
				gp.lineTo((float) x, (float) y);					
			prevValid = valid;	
			pointsCount++;			
			
			// calculate label position
			if (needLabelPos && onScreen) {
				double xLabel = x + 10;
				if (xLabel < 20) xLabel = 5;					
			
				double yLabel = y + 15;		
				if (yLabel < 40) 
					yLabel = 15;
				else if (yLabel > view.height - 30) 
					yLabel = view.height - 5;
			
				labelPoint = new Point((int) xLabel, (int) yLabel);
				needLabelPos = false;											
			}
									
			x0=x; y0=y;
			
			/*
			 * Here's the real utility of the algorithm: Now pop stack and go to
			 * right; notice the corresponding dyadic value when we go to right is
			 * 2*i/(2^(d+1) = i/2^d !! So we've already calculated the corresponding
			 * x and y values when we pushed.
			 */
		    y=yStack[--top]; 
		    x=xStack[top];
		    onScreen=onScreenStack[top];
		    valid=validStack[top];
		    depth=depthStack[top]+1; // pop stack and go to right
		    i=dyadicStack[top]<<1;
		} while (top !=0);
				
		// TODO: remove
		System.out.println("CURVE points = " + pointsCount);
		
		return labelPoint;		
		
		/*
		 * Explanation by John Gillam
		 * 
		 
		 Let X(t)=(x(t),y(t)) be a function defined on [a,b].  
		 Make a uniform partition t0=a<t1<t2...<tn=b of [a,b].  Assume we've already plotted X(ti).  
		 Choose j>i and see if it's acceptable for the next point to be X(tj).  If not, "back up" and try another j.  
		 Unfortunately, this probably leads to multiple evaluations of the function X at the same point.  
		 The following discussion presents an algorithm which basically does this "back up" but multiple 
		 function evaluations at the same point are avoided.

		 The actual algorithm always has n a power of 2, namely 2 to the maxDepth.  
		 In Math Okay, the user sets maxDepth in the drawing quality under Max lines.  

		    The following first creates a complete binary tree of height h(3)
		    Then the following algorithm visits all the leaves of the tree
		    (2^h leaves): (easy induction on height h).  Furthermore,
		    the maximum stack size is clearly h+1:
		
		    Node root=create(1,3),p;
		    p=root;
		    Node[] stack=new Node[20];
		    int top=0;
		    stack[top++]=p;
		    do {
		      while (p.left!=null) {
		        stack[top++]=p;
		        p=(Node)p.left;
		      }
		      System.out.print(" "+p.n); // visit p
		      p=stack[--top];
		      p=(Node)p.right;
		    } while (top!=0);
		
		    Now the following code "clearly" generates as t values all the
		    dyadic rationals with denominator 2^n, with n==maxDepth
		
		    int dyadicStack[]=new int[20];
		    int depthStack[]=new int[20];
		    double divisors[]=new double[20];
		    divisors[0]=1;
		    for (int i=1;i<20;divisors[i]=divisors[i-1]/2,i++)
		      ;
		    int top=0,maxDepth=5; // of course 5 is just a test
		    double t;
		    int i=1;
		    dyadicStack[0]=1;
		    depthStack[0]=0;
		    top=1;
		    int depth=0;
		    do {
		      while (depth<maxDepth) {
		        dyadicStack[top]=i; depthStack[top++]=depth;
		        i<<=1; i--;
		        depth++;
		      }
		      t=i*divisors[maxDepth]; //  a visit of dyadic rational t
		      depth=depthStack[--top]+1; // pop stack and go to right
		      i=dyadicStack[top]<<1;
		    } while (top !=0);
		
		Finally, here is code which draws a curve (continuous) x(t),y(t) for
		t1<=t<=t2; xEval and yEval evaluate x and y at t; maxXDistance and maxYDistance are set somewhere.  
		Let P0=(x0,y0) be "previous point" and P=(x,y) the "current point".  The while loop that goes down the tree
		has condition of form: depth<maxDepth && !acceptable(P0,P); i.e., go on
		down the tree when it is unacceptable to draw the line from P0 to P.
		
		  double x0,y0,x,y,t;
		  int dyadicStack[]=new int[20];
		  int depthStack[]=new int[20];
		  double xStack[]=new double[20];
		  double yStack[]=new double[20];
		  double divisors[]=new double[20];
		  divisors[0]=t2-t1;
		  for (int i=1;i<20;divisors[i]=divisors[i-1]/2,i++)
		    ;
		  int i=1;
		  dyadicStack[0]=1;
		  depthStack[0]=0;
		  x0=xEval(t1);
		  y0=yEval(t1);
		  xStack[0]=x=xEval(t2); yStack[0]=y=yEval(t2);
		  top=1;
		  int depth=0;
		  // with a GeneralPath moveTo(sx0,sy0) , the "screen" point
		  do {
		    while (depth<maxDepth &&
		          (abs(x-x0)>=maxXDistance || abs(y-y0)>=maxYDistance)) {
		      dyadicStack[top]=i; depthStack[top]=depth;
		      xStack[top]=x; yStack[top++]=y;
		      i<<=1; i--;
		      depth++;
		      t=t1+i*divisors[depth];  // t=t1+(t2-t1)*(i/2^depth)
		      x=xEval(t); y=yEval(t);
		    }
		    drawLine(x0,y0,x,y);  // or with a GeneralPath lineTo(sx,sy)
		    // above is call to user written function
		    x0=x; y0=y;
			//  Here's the real utility of the algorithm:
			//Now pop stack and go to right; notice the corresponding dyadic value when we go to right is 2*i/(2^(d+1) = i/2^d !! So we've already
			//calculated the corresponding x and y values when we pushed.
		    y=yStack[--top]; x=xStack[top]
		    depth=depthStack[top]+1; // pop stack and go to right
		    i=dyadicStack[top]<<1;
		  } while (top !=0);
		
		Notice the lines drawn from (x0,y0) to (x,y) always satisfy |x-x0|<maxXDistance 
		and |y-y0|<maxYDistance or the minimum mesh 1/2^maxDepth has been reached.  
		Also the maximum number of evaluations of functions x and y is 2^maxDepth.  
	    All this pushing and popping looks expensive, but compared to function evaluation and rendering, it's trivial.
		
		For the special case of y=f(x), of course x(t)==t and y(t)=f(t).  In this special case, you  still 
		have to worry about discontinuities of f, and in particular vertical asymptopes.  So you need to adjust the Boolean acceptable.		 		 
		 */
		
    }
    
    /*
     * Draws the graph of f between a and b to gp. (Note: a < b) 
     * @param f
     * @param a: left x value in real wold coords
     * @param b: right x value in real wold coords
     * @param view
     * @param gp: generalpath the can be drawn afterwards
     * @param calcLabelPos: whether label position should be calculated and returned
     * @param moveToAllowed: whether moveTo() may be used for gp
     * @return label position as Point     
     *
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
	} */       
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {         	
        	try {
	            if (geo.doHighlighting()) {
	                g2.setPaint(geo.selColor);
	                g2.setStroke(selStroke);            
	                g2.draw(gp);	                
	            } 
        	} catch (Exception e) {
        		System.err.println(e.getMessage());
        	}
            
			try {
            	g2.setPaint(geo.objColor);
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
		   g2.setPaint(geo.objColor);
		   g2.setStroke(objStroke);                                   
			g2.draw(gp);    
	   } catch (Exception e) {
		   System.err.println(e.getMessage());
	   }    							 
	}
    
	final public boolean hit(int x,int y) {  
    	if (!isVisible) 
    		return false;
    	return gp.intersects(x-2,y-2,4,4)
			&& !gp.contains(x-2,y-2,4,4);      
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
