/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.ParametricCurve;
import geogebra.kernel.roots.RealRootUtil;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Draws graphs of parametric curves and functions
 * @author  Markus Hohenwarter
 */
public class DrawParametricCurve extends Drawable {	
	
	// maximum distance between two plot points in pixels
	private static final int MAX_PIXEL_DISTANCE = 5; // pixels		
	private static final int HUGE_PIXEL_DISTANCE = 32; // pixels	
	
	// minimum distance between two curve plot points in pixels
	private static final double MIN_PIXEL_DISTANCE = 1; // pixels	
	
	// min distance between two evaluation positions in pixel
	// e.g. needed for sin(1/x) to keep the number of points down around 0
	private static final double MIN_X_PIXEL_DISTANCE = 0.05; // pixels
	
	// maximum angle between two line segments
	//private static final double MAX_BEND = 0.087488; // = tan(5 degrees)
	private static final double MAX_BEND = 0.17; // = tan(10 degrees)
	

	// maximum number of iterations (max number of plot points = 2^MAX_DEPTH)
	private static final int MAX_DEPTH = 32;
	
	// minimum and maximum number of positions evaluated at curve
	private static final int MIN_EVALUATIONS = 10;
	private static final int MAX_EVALUATIONS = 50000;	
	private static final int MAX_POINTS = 20000;
	private static long countEvaluations = 0;

	// clipping area around screen: pixel-width of border on every side 	
	private static final int SCREEN_CLIP_BORDER = 200; 
	
	// if the curve is undefined at both endpoints, we break
	// the parameter interval up into smaller intervals
	private static final int MAX_INTERVAL_DEPTH = 8;
	
	// the curve is broken into this many intervals to plot it
	private static final int SPLIT_INTERVALS = 30;
   
    private ParametricCurve curve;        
	private GeneralPath gp = new GeneralPath();
    private boolean isVisible, labelVisible;   
    private static int countPoints = 0;
   
    public DrawParametricCurve(EuclidianView view, ParametricCurve curve) {
    	this.view = view;
        this.curve = curve;
        geo = curve.toGeoElement();        
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
        updateStrokes(geo);		 
		
		gp.reset();		
		
		Point labelPoint = plotCurve(curve,
					curve.getMinParameter(),
					curve.getMaxParameter(), 
					view, gp,
					labelVisible, true); 

		// gp on screen?		
		if (!gp.intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
		}
				
		if (labelPoint != null) {
			xLabel = labelPoint.x;
			yLabel = labelPoint.y;
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
     * @param gp: generalpath that can be drawn afterwards
     * @param calcLabelPos: whether label position should be calculated and returned
     * @param moveToAllowed: whether moveTo() may be used for gp
     * @return label position as Point
     * @author Markus Hohenwarter, based on an algorithm by John Gillam     
     */
    final public static Point plotCurve(ParametricCurve curve,
			double t1, double t2, EuclidianView view, 
			GeneralPath gp,
			boolean calcLabelPos, 
			boolean moveToAllowed) 
    {     	
    	countPoints = 0; 
    	countEvaluations = 0;
    	Point labelPoint = null;	    	
    	
		// Curves with undefined start and end points
		// are broken into more intervals    	
		// check first and last point of curve
		GeoVec2D p1 = curve.evaluateCurve(t1);
		GeoVec2D p2 = curve.evaluateCurve(t2);	
		boolean p1def = p1.isDefined();
		boolean p2def = p2.isDefined();
		
		// CLOSED CURVE
		if (p1def && p2def && tooCloseOnScreen(view, p1, p2)) {		
			labelPoint = plotClosedCurve(curve, t1, t2, 0, view, gp, calcLabelPos, moveToAllowed, p1);					
		} 
		
		// STANDARD CASE
		else {			
			labelPoint = splitAndPlotInterval(SPLIT_INTERVALS, curve, t1, t2, view, gp, calcLabelPos, moveToAllowed);    		
	 	   
			//labelPoint = plotInterval(curve, t1, t2, 0, view, gp, calcLabelPos, moveToAllowed);									
		} 
		
//		System.out.println("*** CURVE plot: " + curve);
//		System.out.println("   plot points: " + countPoints );	
//		System.out.println("   evaluations: " + countEvaluations );	
			
		return labelPoint;
    }
    
    /**
     * Returns whether the pixel-distance of p1 on p2 is smaller than MAX_PIXEL_DISTANCE.
     */
    private static boolean tooCloseOnScreen(EuclidianView view, GeoVec2D p1, GeoVec2D p2) {
    	// p1
		double [] coords1 = new double[2];
		coords1[0] = view.xZero + p1.x * view.xscale;
		coords1[1] = view.yZero - p1.y * view.yscale;
		
		//	p2
		double [] coords2 = new double[2];
		coords2[0] = view.xZero + p2.x * view.xscale;
		coords2[1] = view.yZero - p2.y * view.yscale;
		
		// CLOSED CURVE: endpoints too close together 
		return (Math.abs(coords1[0] - coords2[0]) <= MAX_PIXEL_DISTANCE &&
				Math.abs(coords1[1] - coords2[1]) <= MAX_PIXEL_DISTANCE); 
    }
    
    /**
     * Plots a curve where both endpoints are very close on screen. We 
     * randomly try to find a split parameter t_split in
     * interval [t1, t2] where c(t_split) is not close to the endpoints of the curve.
     */
    private static Point plotClosedCurve(ParametricCurve curve,
			double t1, double t2, int depth, EuclidianView view, 
			GeneralPath gp,
			boolean calcLabelPos, 
			boolean moveToAllowed,
			GeoVec2D p1) 
    {    	    
    	double splitParam = 0;
    	boolean tooClose = true;		
    	int tries = 0;        
    	while (tooClose && tries < 5) {
    		// random split parameter
    		double rand = 0.4 + Math.random() * 0.2; // [0.4, 0.6]
    		splitParam = t1 +  rand * (t2-t1);    		
    		GeoVec2D splitPoint = curve.evaluateCurve(splitParam);    	
    		tooClose = tooCloseOnScreen(view, p1, splitPoint);	
    		tries++;
    	}
    	
    	if (tooClose) {
        	// we could not find a split parameter, DESPERATE MODE:
    		// split interval into SPLIT_INTERVALS 
    		return splitAndPlotInterval(SPLIT_INTERVALS, curve, t1, t2, view, gp, calcLabelPos, moveToAllowed);    		
    	} 
    	else {
    		// we got a split parameter: plot [t1, splitParam] and [splitParam, t2]
    		Point labelPoint1 = plotInterval(curve, t1, splitParam, depth + 1, view, gp, 
					calcLabelPos, moveToAllowed);		
    		Point labelPoint2 = plotInterval(curve, splitParam, t2, depth + 1, view, gp, 
					calcLabelPos && labelPoint1 == null, moveToAllowed);	
    		
    		if (labelPoint1 != null)
    			return labelPoint1;
    		else
    			return labelPoint2;    		
    	}    			    	
    }
    
    /**
     * Plots a curve by splitting up the parameter interval into 
     * n intervals.
     */
    private static Point splitAndPlotInterval(int n, ParametricCurve curve,
			double t1, double t2, EuclidianView view, 
			GeneralPath gp,
			boolean calcLabelPos, 
			boolean moveToAllowed) 
    {
    	    
    //	System.out.println("split intervals: " + n);
    	
    	Point labelPoint = null;
    	
    	//  plot all intervals					
		double intervalWidth = (t2 - t1) / n;
		t2 = t1 + intervalWidth;
		for (int i=0; i < n; i++) {							
			Point p = plotInterval(curve, t1, t2, 1, view, gp, 
					calcLabelPos && labelPoint == null, moveToAllowed);									
			if (labelPoint == null)
				labelPoint = p;
						
			t1 = t2;
			t2 = t1 + intervalWidth;	
		}			
		return labelPoint;
    }
    
    /**
     * Draws a parametric curve (x(t), y(t)) for t in [t1, t2]. 
     * @param gp: generalpath that can be drawn afterwards
     * @param calcLabelPos: whether label position should be calculated and returned
     * @param moveToAllowed: whether moveTo() may be used for gp
     * @return label position as Point
     * @author Markus Hohenwarter, based on an algorithm by John Gillam     
     */
	 private static Point plotInterval(ParametricCurve curve,
								double t1, double t2, int intervalDepth, EuclidianView view, 
								GeneralPath gp,
								boolean calcLabelPos, 
								boolean moveToAllowed) 
	 { 		
		 	
	    // plot interval for t in [t1, t2]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [left, (left + right)/2] and [(left + right)/2, right]
		// see catch block
		 
		boolean moveToFirstPoint = moveToAllowed;			
		boolean needLabelPos = calcLabelPos;
		Point labelPoint = null;
		//double intervalLength = Math.abs(t1-t2);
		    	
		// The following algorithm by John Gillam avoids multiple
		// evaluations of the curve for the same parameter value t
		// see an explanation of this algorithm at the end of this method.
		double x0,y0,t,x,y,moveX=0, moveY=0;		
		boolean onScreen, prevOnScreen, nextLineToNeedsMoveToFirst = false;		
		double [] eval = new double[2];
						
		// evaluate for t1
		curve.evaluateCurve(t1, eval);				
		if (Double.isNaN(eval[0]) || Double.isNaN(eval[1])) {
			// System.out.println("Curve undefined at t = " + t1);			
			return plotProblemInterval(curve, t1, t2, 
					intervalDepth, view, gp, calcLabelPos, moveToAllowed, labelPoint, eval);
		}	
		
		prevOnScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
		x0=eval[0]; // xEval(t1);
		y0=eval[1]; // yEval(t1);										
		
		// with a GeneralPath moveTo(sx0,sy0) , the "screen" point			
		if (moveToFirstPoint) {	
			moveTo(gp, x0, y0);				
			moveToFirstPoint = false;
		} else {
			lineTo(gp, x0, y0);
		}			
		
		// evaluate for t2
		curve.evaluateCurve(t2, eval);
		if (Double.isNaN(eval[0]) || Double.isNaN(eval[1])) {
			// System.out.println("Curve undefined at t = " + t2);
			return plotProblemInterval(curve, t1, t2, 
					intervalDepth, view, gp, calcLabelPos, moveToAllowed, labelPoint, eval);
		}	
		
		int LENGTH = MAX_DEPTH + 1;
		int dyadicStack[]=new int[LENGTH];
		int depthStack[]=new int[LENGTH];
		double xStack[]=new double[LENGTH];
		double yStack[]=new double[LENGTH];
		boolean onScreenStack[]=new boolean[LENGTH];		
		double divisors[]=new double[LENGTH];
		divisors[0]=t2-t1;		
		for (int i=1; i < LENGTH; divisors[i] = divisors[i-1]/2, i++);
		int i=1;
		dyadicStack[0]=1;
		depthStack[0]=0;
		
		// for evaluation at t2
		onScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
		onScreenStack[0] = onScreen;
		xStack[0] = x = eval[0]; // xEval(t2);
		yStack[0] = y = eval[1]; // yEval(t2);									
		
		double ydiff = y - y0;
		double xdiff = x - x0;
		double slope = ydiff / xdiff; 
		double prevSlope = slope;		
		
		int top=1;
		int depth=0;
		t = t1;
		double left = t1;
				
		boolean distTooLarge;		
		boolean distVeryLarge;
		boolean angleTooLarge;
		
		do {		
			// distance from last point too large
			distTooLarge = checkDistance(xdiff, ydiff);
		
			// angle between line segments too large
			angleTooLarge = checkAngle(slope, prevSlope);
			
			// make sure we have at least MIN_EVALUATIONS points: counter < 10
			while ( 
					countEvaluations < MAX_EVALUATIONS && countPoints < MAX_POINTS && 
					depth < MAX_DEPTH &&  
					(distTooLarge || angleTooLarge || countEvaluations < MIN_EVALUATIONS ) ) 
			{										
				// push stacks
				dyadicStack[top]=i; 
				depthStack[top]=depth;
				onScreenStack[top]=onScreen;
				xStack[top]=x; 
				yStack[top++]=y;
				i<<=1; i--;
				depth++;				
				t=t1+i*divisors[depth];  // t=t1+(t2-t1)*(i/2^depth)										
				
				// evaluate curve for parameter t
				curve.evaluateCurve(t, eval);		
				countEvaluations++;
			
				if (Double.isNaN(eval[0]) || Double.isNaN(eval[1])) {
					// t gave us an undefined value
					// check if this is a singularity or if we need to split our entire interval
					
					// singularity: f(t+eps) and f(t-eps) are defined
					boolean singularity = false;
					curve.evaluateCurve(t + 0.001, eval);
					boolean defRight = !Double.isNaN(eval[1]);
					if (defRight) {
						curve.evaluateCurve(t - 0.001, eval);
						boolean defLeft = !Double.isNaN(eval[1]);
						if (defLeft) {
							// probably a SINGULARITY, so keep on going
							singularity = true;							
						}
					}
					
					// split interval: f(t+eps) or f(t-eps) not defined
					if (!singularity) {					
						// System.out.println("Curve undefined at t = " + t);					
						return plotProblemInterval(curve, left, t2, 
								intervalDepth, view, gp, calcLabelPos, moveToAllowed, labelPoint, eval);
					} else {						
						//System.out.println("singularity at: " + t);
					}
				}							
										
				onScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
				x=eval[0]; // xEval(t);
				y=eval[1]; // yEval(t);	
				xdiff = x - x0;
				ydiff = y - y0;
				slope = ydiff / xdiff;		
						
				// stop at very small pixel distance
				if (curve.isFunctionInX()) {
					// function in x,  e.g. important for sin(1/x)
					if (Math.abs(xdiff) < MIN_X_PIXEL_DISTANCE)
					{						
						break;
					}					
				} else {
					// cartesian curve
					if (Math.abs(xdiff) < MIN_PIXEL_DISTANCE && 
						Math.abs(ydiff) < MIN_PIXEL_DISTANCE) 
					{						
						distTooLarge = false;						
						break;
					}
				}				
				
				// distance from last point too large
				distTooLarge = checkDistance(xdiff, ydiff);
				
				// angle between line segments too large
				angleTooLarge = checkAngle(slope, prevSlope);							
			}											
			
			// drawLine(x0,y0,x,y)
			// don't add points to gerneral path that are in the same pixel as the previous point			
					
			// check if distance was very large
			distVeryLarge = distTooLarge && checkDistanceVeryLarge(xdiff, ydiff);
			
			// move to is possible
			if (moveToAllowed) {	
				// still need first point
				if (moveToFirstPoint) { 
					moveTo(gp, x, y);
					moveToFirstPoint = false;												
				}					
				// previous and this point not on screen
				else if (!onScreen &&  !prevOnScreen) {
					nextLineToNeedsMoveToFirst = true;
					moveX = x;
					moveY = y;
				} 
				// distance too big
				else if (distTooLarge && (angleTooLarge || distVeryLarge)){
					moveTo(gp, x, y);					
					nextLineToNeedsMoveToFirst = false;						
				}
				// everything ok: let's draw a line!
				else { 
					if (nextLineToNeedsMoveToFirst) {
						moveTo(gp, moveX, moveY);							
						nextLineToNeedsMoveToFirst = false;
					}
					
					lineTo(gp, x, y);					
				}	
			}	
			// move to is not allowed: we have to draw all lines
			else { 					
				// line to
				lineTo(gp, x, y);					
			}																
			
			// remember last point in general path
			x0=x; 
			y0=y;
			left = t;
			prevOnScreen = onScreen;							
			
			// remember first point on screen for label position 
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
																								
			/*
			 * Here's the real utility of the algorithm: Now pop stack and go to
			 * right; notice the corresponding dyadic value when we go to right is
			 * 2*i/(2^(d+1) = i/2^d !! So we've already calculated the corresponding
			 * x and y values when we pushed.
			 */			
		    y=yStack[--top]; 
		    x=xStack[top];
		    onScreen=onScreenStack[top];
		    depth=depthStack[top]+1; // pop stack and go to right
		    i=dyadicStack[top]<<1;		    		    
			prevSlope = slope;	
			xdiff = x - x0;
			ydiff = y - y0;
			slope = ydiff / xdiff;		
			t=t1+i*divisors[depth];
		} while (top !=0);	
				
		//System.out.println("curve evaluations: " + counter);
		
		return labelPoint;						
	 }	 
	 
	 private static boolean checkDistance(double xdiff, double ydiff) {		 	
			// distance from last point too large
			return ( Math.abs(xdiff) > MAX_PIXEL_DISTANCE || 
					 Math.abs(ydiff) > MAX_PIXEL_DISTANCE);			
	 }
	 
	 private static boolean checkDistanceVeryLarge(double xdiff, double ydiff) {		 	
			// distance from last point too large
			return ( Math.abs(xdiff) > HUGE_PIXEL_DISTANCE || 
					 Math.abs(ydiff) > HUGE_PIXEL_DISTANCE);			
	 }
	 
	 
	 private static boolean checkAngle(double slope, double prevSlope) {			
			// angle between line segments too large
			// tan(alpha) > MAX_BEND
			// where tan(alpha) = det(v,w)/v.w, here for slopes
			return Math.abs(slope - prevSlope) > 
									MAX_BEND * Math.abs(1 + slope * prevSlope);
	 }
	 
	 /**
	  * Plots an interval where f(t1) or f(t2) is undefined.
	  */
	 private static Point plotProblemInterval(ParametricCurve curve,
				double t1, double t2, int intervalDepth, EuclidianView view, 
				GeneralPath gp,
				boolean calcLabelPos, 
				boolean moveToAllowed, Point labelPoint, double [] eval) 
	 {		
		 		
		// stop recursion for too many intervals
		if (intervalDepth > MAX_INTERVAL_DEPTH) {	
			return labelPoint;
		} 
		 		 
		// plot interval for t in [t1, t2]
		// If we run into a problem, i.e. an undefined point f(t), we bisect
		// the interval and plot both intervals [t, (t+t2)/2] and [(t+t2)/2], t2]											
		double splitParam = (t1 + t2) / 2.0;	
					
		// look at the end points of the intervals [t1, (t1+t2)/2] and [(t1+t2)/2, t2]
		// and try to get a defined interval. This is important if we one of
		// both interval borders is defined and the other is undefined. In this
		// case we want to find a smaller interval where both borders are defined		
		
		// plot interval [t1, (t1+t2)/2]
		getDefinedInterval(curve, t1, splitParam, eval);			
		calcLabelPos = calcLabelPos && labelPoint == null;
 		Point labelPoint1 = plotInterval(curve, eval[0], eval[1], intervalDepth + 1, view, gp, calcLabelPos, moveToAllowed);	
 		
 		// plot interval [(t1+t2)/2, t2]
 		getDefinedInterval(curve, splitParam, t2, eval);	
 		calcLabelPos = calcLabelPos && labelPoint1 == null;
 		Point labelPoint2 = plotInterval(curve, eval[0], eval[1], intervalDepth + 1, view, gp, calcLabelPos, moveToAllowed);	
 		
 		if (labelPoint != null)
 			return labelPoint;
 		else if (labelPoint1 != null)
 			return labelPoint1;
 		else
 			return labelPoint2;
	 }
	 
	 /**
	  * Sets borders to a defined interval in [a, b] if possible.
	  */
	 private static void getDefinedInterval(ParametricCurve curve, double a, double b, double [] borders) {		  
			// check first and last point in interval
			curve.evaluateCurve(a, borders);
			boolean aDef = !Double.isNaN(borders[0]) && !Double.isNaN(borders[1]);
			curve.evaluateCurve(b, borders);
			boolean bDef = !Double.isNaN(borders[0]) && !Double.isNaN(borders[1]);

			// both end points defined
			if (aDef && bDef) {
				borders[0] = a;
				borders[1] = b;
			} 
			// one end point defined
			else if (aDef && !bDef || !aDef && bDef) {
				// check whether the curve is defined at the interval borders
				// if not, we try to find a valid domain
				double [] intervalX = RealRootUtil.getDefinedInterval(curve.getRealRootFunctionX(), a, b);
				double [] intervalY = RealRootUtil.getDefinedInterval(curve.getRealRootFunctionY(), a, b);
				double lowerBound = Math.max(intervalX[0], intervalY[0]);
				double upperBound = Math.min(intervalX[1], intervalY[1]);
				borders[0] = Double.isNaN(lowerBound) ? a : lowerBound;
				borders[1] = Double.isNaN(upperBound) ? b : upperBound;						
			}
			// no end point defined
			else {
				borders[0] = a;
				borders[1] = b;
			}
	 }
	 
	 /**
	  * Calls gp.moveTo(x, y) only if the current point
	  * is not already at this position.
	  */
	 public static void moveTo(GeneralPath gp, double x, double y) {		
		 drawTo(gp, x, y, false);
	 }
	 
	 /**
	  * Calls gp.lineTo(x, y) only if the current point
	  * is not already at this position.
	  */
	 public static void lineTo(GeneralPath gp, double x, double y) {			 		 
		 drawTo(gp, x, y, true);
	 }

	 /**
	  * Calls gp.lineTo(x, y) resp. gp.moveTo(x, y) only if the current point
	  * is not already at this position.
	  */
	 private static void drawTo(GeneralPath gp, double x, double y, boolean lineTo) {		
		Point2D point = gp.getCurrentPoint();
		
		// check pixel distance
		if (point == null ||
			Math.abs(point.getX() - x) > 1 ||
			Math.abs(point.getY() - y) > 1) 
		{
			if (lineTo)
				gp.lineTo((float) x, (float) y);				
			else {
				gp.moveTo((float) x, (float) y);
				
				
			}
				
			
			countPoints++;
		}				
	 }
	 
//	 private boolean distanceSmall(double x, double y) {
//		 Point2D point = gp.getCurrentPoint();		 
//		 return Math.abs(point.getX() - x) > 1 || 
//		 		Math.abs(point.getY() - y) > 1;
//	 }
		
	/*
	 * The algorithm in plotInterval() is based on an algorithm by John Gillam.
	 * Below you find his explanation.
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
    
    
	final public void draw(Graphics2D g2) {
        if (isVisible) {         	        	
            if (geo.doHighlighting()) {
                g2.setPaint(geo.getSelColor());
                g2.setStroke(selStroke);            
                drawGeneralPath(gp, g2);		                
            } 
        	            
		    g2.setPaint(geo.getObjectColor());		    
			g2.setStroke(objStroke);                                   
			drawGeneralPath(gp, g2);		    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
            }        
        }
    }		
    
	final void drawTrace(Graphics2D g2) {	   
	   g2.setPaint(geo.getObjectColor());	   
	   g2.setStroke(objStroke); 		   
	   drawGeneralPath(gp, g2);		   
	}		
    
	final public boolean hit(int x,int y) {  
    	if (isVisible) {
    		if (strokedShape == null) {
    			strokedShape = objStroke.createStrokedShape(gp);
    		}    		
    		return strokedShape.intersects(x-3,y-3,6,6);
    	} else
    		return false;
    	/*
    	return gp.intersects(x-3,y-3,6,6)
			&& !gp.contains(x-3,y-3,6,6);
			*/    
    }
	
    final public boolean isInside(Rectangle rect) {
    	return rect.contains(gp.getBounds());  
    }
    
    public GeoElement getGeoElement() {
        return geo;
    }    
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
}
