/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
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
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * Draws graph of parametric curves and functions
 * @author  Markus Hohenwarter
 */
public class DrawParametricCurve extends Drawable {	
	
	// maximum distance between two plot points in pixels
	private static final int MAX_PIXEL_DISTANCE = 3; // pixels		
	
	// maximum angle between two line segments
	private static final double MAX_BEND = 0.08; // = tan(5 degrees)

	// maximum number of iterations (max number of plot points = 2^MAX_DEPTH)
	private static final int MAX_DEPTH = 25;

	// clipping area around screen: pixel-width of border on every side 	
	private static final int SCREEN_CLIP_BORDER = 200; 
	
	// if the curve is undefined at both endpoints, we break
	// the parameter interval up into smaller intervals
	private static final int DESPERATE_MODE_INTERVALS = 100;		
   
    private ParametricCurve curve;        
	private GeneralPath gp = new GeneralPath();
    private boolean isVisible, labelVisible;   
    private static int pointsCount = 0;
   
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
    	pointsCount = 0;  
    	Point labelPoint = null;				   
    	
		// Curves with undefined start and end points
		// are broken into more intervals    	
		// check first and last point of curve
		GeoVec2D p1 = curve.evaluateCurve(t1);
		GeoVec2D p2 = curve.evaluateCurve(t2);	
		boolean p1def = p1.isDefined();
		boolean p2def = p2.isDefined();
			
		try {
			// both end points are defined
			if (p1def && p2def) {	
				if (tooCloseOnScreen(view, p1, p2)) {		
					// CLOSED CURVE
					labelPoint = plotClosedCurve(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed, p1);
				} 
				else {
					// STANDARD CASE
					labelPoint = plotInterval(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed);									
				}
			} 
			
			// one end point is defined
			else if (p1def || p2def) {
				// STANDARD CASE
				labelPoint = plotInterval(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed);									
			} 
			
			// no end point is defined
			else { 		
				// DESPERATE MODE: start and end point are undefined 
				labelPoint = plotDesperateMode(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed, p1, p2);
			}
		}
		catch (Exception e) {
			// curve was undefined in interval
			gp.reset();
			return plotDesperateMode(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed, p1, p2);
		}

		
									
//		System.out.println("*** CURVE plot: " + curve);
		//System.out.println("plot points: " + pointsCount );	
		
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
			double t1, double t2, EuclidianView view, 
			GeneralPath gp,
			boolean calcLabelPos, 
			boolean moveToAllowed,
			GeoVec2D p1) 
    {    	    
    	double splitParam = 0;
    	boolean tooClose = true;		
    	int tries = 0;        
    	while (tooClose && tries < 10) {
    		// random split parameter
    		double rand = 0.4 + Math.random() * 0.2; // [0.4, 0.6]
    		splitParam = t1 +  rand * (t2-t1);    		
    		GeoVec2D splitPoint = curve.evaluateCurve(splitParam);    	
    		tooClose = tooCloseOnScreen(view, p1, splitPoint);
    		tries++;
    	}
    	
    	if (tooClose) {
        	// we could not find a split parameter and give up
    		return null;    		
    	} else {
    		try {
	    		// we got a split parameter: plot [t1, splitParam] and [splitParam, t2]
	    		Point labelPoint1 = plotInterval(curve, t1, splitParam, view, gp, 
						calcLabelPos, moveToAllowed);		
	    		Point labelPoint2 = plotInterval(curve, splitParam, t2, view, gp, 
						calcLabelPos && labelPoint1 == null, moveToAllowed);	
	    		
	    		if (labelPoint1 == null)
	    			return labelPoint2;
	    		else
	    			return labelPoint1;
    		}
    		catch (Exception e) {
    			// curve was undefined in interval
    			gp.reset();
    			return plotDesperateMode(curve, t1, t2, view, gp, calcLabelPos, moveToAllowed, p1, curve.evaluateCurve(t2));
    		}
    	}    			    	
    }
    
    /**
     * Plots a curve where both endpoints are undefined. This is done
     * by splitting up the parameter interval into DESPERATE_MODE_INTERVALS many
     * intervals and plotting these intervals if at least one of their endpoints
     * is defined. 
     */
    private static Point plotDesperateMode(ParametricCurve curve,
			double t1, double t2, EuclidianView view, 
			GeneralPath gp,
			boolean calcLabelPos, 
			boolean moveToAllowed,
			GeoVec2D p1, GeoVec2D p2) 
    {
    	Point labelPoint = null;
    	
    	//  plot all intervals					
		double intervalWidth = (t2 - t1) / DESPERATE_MODE_INTERVALS;
		t2 = t1 + intervalWidth;
		for (int i=0; i < DESPERATE_MODE_INTERVALS; i++) {				
			p2 = curve.evaluateCurve(t2);

			// only plot this interval if at least one border is defined
			if (p1.isDefined() || p2.isDefined()) {	
				try {
					Point p = plotInterval(curve, t1, t2, view, gp, 
										calcLabelPos && labelPoint == null, moveToAllowed);									
					if (labelPoint == null)
						labelPoint = p;
				}
				catch (Exception e) {
					// curve was undefined in interval					
				}
			}
			
			t1 = t2;
			t2 = t1 + intervalWidth;	
			p1 = p2;
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
								double t1, double t2, EuclidianView view, 
								GeneralPath gp,
								boolean calcLabelPos, 
								boolean moveToAllowed) throws Exception { 		
		
		// check wether the curve is defined at the interval borders
		// if not, we try to find a valid domain
		double [] intervalX = RealRootUtil.getDefinedInterval(curve.getRealRootFunctionX(), t1, t2);
		double [] intervalY = RealRootUtil.getDefinedInterval(curve.getRealRootFunctionY(), t1, t2);
		double lowerBound = Math.max(intervalX[0], intervalY[0]);
		double upperBound = Math.min(intervalX[1], intervalY[1]);
		t1 = Double.isNaN(lowerBound) ? t1 : lowerBound;
		t2 = Double.isNaN(upperBound) ? t2 : upperBound;
		
		boolean moveToFirstPoint = moveToAllowed;			
		boolean needLabelPos = calcLabelPos;
		Point labelPoint = null;
		
    	
		// The following algorithm by John Gillam avoids multiple
		// evaluations of the curve for the same parameter value t
		// see an explanation of this algorithm at the end of this method.
		double x0,y0,x,y,t,moveX=0, moveY=0;		
		boolean onScreen, prevOnScreen, nextLineToNeedsMoveToFirst = false;		
		double [] eval = new double[2];
				
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

		// evaluate for t1
		curve.evaluateCurve(t1, eval);
		prevOnScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
		x0=eval[0]; // xEval(t1);
		y0=eval[1]; // yEval(t1);

		// invalid: desperate mode
		if (Double.isNaN(x0) || Double.isNaN(y0)) {
			throw new Exception("Curve undefined at t = " + t1);
		}						
		
		// with a GeneralPath moveTo(sx0,sy0) , the "screen" point			
		if (moveToFirstPoint) {	
			moveTo(gp, x0, y0);				
			moveToFirstPoint = false;
		} else {
			lineTo(gp, x0, y0);
		}			
		
		// evaluate for t2
		curve.evaluateCurve(t2, eval);
		onScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
		onScreenStack[0] = onScreen;
		xStack[0] = x = eval[0]; // xEval(t2);
		yStack[0] = y = eval[1]; // yEval(t2);	
		
		// invalid: desperate mode
		if (Double.isNaN(x) || Double.isNaN(y)) {
			throw new Exception("Curve undefined at t = " + t2);
		}	
		
		double slope = (y - y0) / (x - x0); 
		double prevSlope = slope;		
		
		int top=1;
		int depth=0;				
			                                 
		do {
			boolean distNotOK = true;							
			while ( depth < MAX_DEPTH &&  									
					( 										
						// distance from last point too large
						(distNotOK = ( Math.abs(x-x0) >= MAX_PIXEL_DISTANCE || 
									   Math.abs(y-y0) >= MAX_PIXEL_DISTANCE) )
					||
						// angle between line segments too large
						// tan(alpha) > MAX_BEND
						// where tan(alpha) = det(v,w)/v.w, here for slopes
						( Math.abs(slope - prevSlope) > MAX_BEND * Math.abs(1 + slope * prevSlope))
					 )
				  )					
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
				onScreen = view.toClippedScreenCoords(eval, SCREEN_CLIP_BORDER);
				x=eval[0]; // xEval(t);
				y=eval[1]; // yEval(t);
					
				// invalid: desperate mode
				if (Double.isNaN(x) || Double.isNaN(y)) {
					throw new Exception("Curve undefined at t = " + t);
				}
								
				slope = (y - y0) / (x - x0);				 
			}			
			
			// drawLine(x0,y0,x,y)
			// don't add points to gerneral path that are in the same pixel as the previous point			
										
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
				else if (distNotOK){
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
			slope = (y - y0) / (x - x0);		    
		} while (top !=0);					
		
		return labelPoint;	
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
			else
				gp.moveTo((float) x, (float) y);
			
			pointsCount++;
		}				
	 }
		
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
                g2.setPaint(geo.selColor);
                g2.setStroke(selStroke);            
                drawGeneralPath(gp, g2);		                
            } 
        	            
		    g2.setPaint(geo.objColor);		    
			g2.setStroke(objStroke);                                   
			drawGeneralPath(gp, g2);		    
			
            if (labelVisible) {
				g2.setFont(view.fontConic);
				g2.setPaint(geo.labelColor);
				drawLabel(g2);
            }        
        }
    }		
    
	final void drawTrace(Graphics2D g2) {	   
	   g2.setPaint(geo.objColor);	   
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
