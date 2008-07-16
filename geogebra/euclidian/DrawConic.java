/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawConic.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package geogebra.euclidian;

import geogebra.kernel.AlgoCircleThreePoints;
import geogebra.kernel.AlgoCircleTwoPoints;
import geogebra.kernel.AlgoEllipseFociPoint;
import geogebra.kernel.AlgoHyperbolaFociPoint;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
final public class DrawConic extends Drawable implements Previewable {        
    
    // plotpoints per quadrant for hyperbola
    private static final int PLOT_POINTS = 32;
	static final int MAX_PLOT_POINTS = 300;
    // maximum of pixels for a standard circle radius
    // bigger circles are drawn via Arc2D
    private static final double BIG_CIRCLE_RADIUS = 600;    
           
    private GeoConic conic;
    
    private boolean isVisible, labelVisible;
    private int type;
            
    private double [] labelCoords = new double[2];      
    
    // CONIC_SINGLE_POINT
    boolean firstPoint = true;
    private GeoPoint  point;
    private DrawPoint drawPoint;
    
    // CONIC_INTERSECTING_LINES
    boolean firstLines = true;
    private GeoLine [] lines;
    private DrawLine [] drawLines;
    
    // CONIC_CIRCLE
    boolean firstCircle = true;
    private GeoVec2D midpoint;    
    private Arc2D.Double arc;   
    private GeneralPath arcFiller, gp;
    private RectangularShape circle;
    double  mx, my, radius, yradius, angSt, angEnd;    
    
    // for ellipse, hyperbola, parabola
    private AffineTransform conicTransform, 
                            transform = new AffineTransform();   
    private Shape shape;     
    
    // CONIC_ELLIPSE    
    boolean firstEllipse = true;
    private double [] halfAxes;      
    private Ellipse2D.Double ellipse;    
    
    // CONIC_PARABOLA   
    boolean firstParabola = true;
    private double x0, y0;
    private int i, k2;
    private GeoVec2D vertex;
    private QuadCurve2D.Double parabola;    
    private double [] parpoints = new double[6];        
    
    // CONIC_HYPERBOLA   
    boolean firstHyperbola = true;    
    private double a,b, tsq, step, t, denom;
    private double x, y;
    private int index0, index1, n, points;
    private Polyline hypLeft, hypRight;    
    private boolean hypLeftOnScreen, hypRightOnScreen;      
    
    // preview of circle (two points or three points)
	private ArrayList prevPoints;      
	private GeoPoint [] previewTempPoints;  
	private int previewMode, neededPrevPoints;
    
    /** Creates new DrawVector */
    public DrawConic(EuclidianView view, GeoConic c) {
    	this.view = view;
        initConic(c);
        update();
    }
    
    private void initConic(GeoConic c) {
    	conic = c;
        geo = c;
                
        vertex = c.getTranslationVector(); // vertex                            
        midpoint = vertex;
        halfAxes = c.getHalfAxes();
        conicTransform = c.getAffineTransform();                              
    }
    
	/**
	 * Creates a new DrawConic for preview of a circle 
	 */
	DrawConic(EuclidianView view, int mode, ArrayList points) {
		this.view = view; 
		prevPoints = points;
		previewMode = mode;	
		
		Construction cons = view.getKernel().getConstruction();
		neededPrevPoints = mode == EuclidianView.MODE_CIRCLE_TWO_POINTS ?
							1 : 2;
		previewTempPoints = new GeoPoint[neededPrevPoints+1];
		for (int i=0; i < previewTempPoints.length; i++) {
			previewTempPoints[i] = new GeoPoint(cons);			
		}
		
		initPreview();
	} 

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        labelVisible = geo.isLabelVisible();
       
        updateStrokes(conic);          
        type = conic.getType();               
        
        switch (type) {
            case GeoConic.CONIC_SINGLE_POINT:                
                updateSinglePoint();
                break;       
                
            case GeoConic.CONIC_INTERSECTING_LINES:  
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:
                updateLines();
                break;                             
                
            case GeoConic.CONIC_CIRCLE:                                               
                updateCircle();
                break;                                                    
                
            case GeoConic.CONIC_ELLIPSE:   
                updateEllipse();
                break;
                
            case GeoConic.CONIC_HYPERBOLA:    
                updateHyperbola();
                break;
                
            case GeoConic.CONIC_PARABOLA:
                updateParabola();
                break;                          
        }
        
        
        // on screen?
        switch (type) {	                          	            
	        case GeoConic.CONIC_CIRCLE:                                               
	        case GeoConic.CONIC_ELLIPSE:   
	        case GeoConic.CONIC_PARABOLA:	           
	        	// shape on screen?
	        	if (!shape.intersects(0,0, view.width, view.height)) {				
	    			isVisible = false;
	    			return;
	    		}
	        	break;
	            
	        case GeoConic.CONIC_HYPERBOLA:
	        	// hyperbola wings on screen?
	        	hypLeftOnScreen = hypLeft.intersects(0,0, view.width, view.height);
	        	hypRightOnScreen = hypRight.intersects(0,0, view.width, view.height);
	        	if (!hypLeftOnScreen && !hypRightOnScreen) {
	        		isVisible = false;
	    			return;
	        	}	            
	            break;            
        }
        
		// draw trace
		if (conic.trace) {
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
			addLabelOffset();
        }
    }
    
    final private void updateSinglePoint() {
        if (firstPoint) {
            firstPoint = false;
            point = conic.getSinglePoint();
            drawPoint = new DrawPoint(view, point);                
            drawPoint.setGeoElement(conic);
            //drawPoint.font = view.fontConic;            
        }
        point.copyLabel(conic);	
        point.setObjColor(conic.getObjectColor());
        point.setLabelColor(conic.getLabelColor());
        point.pointSize = conic.lineThickness;
        drawPoint.update();   
    }
    
    final private void updateLines() {
        if (firstLines) {
            firstLines = false;
            lines = conic.getLines();
            drawLines = new DrawLine[2];
            drawLines[0] = new DrawLine(view, lines[0]);
            drawLines[1] = new DrawLine(view, lines[1]);                    
            drawLines[0].setGeoElement(conic);
            drawLines[1].setGeoElement(conic);
            //drawLines[0].font = view.fontConic;
            //drawLines[1].font = view.fontConic;            
        }
        for (i=0; i < 2; i++) {
			lines[i].copyLabel(conic);					
			lines[i].setObjColor(conic.getObjectColor());					
			lines[i].setLabelColor(conic.getLabelColor());	
			lines[i].lineThickness = conic.lineThickness;
			lines[i].lineType = conic.lineType;		
			drawLines[i].update();			
        }
           
    }
    
    final private void updateCircle() {
        if (firstCircle) {
            firstCircle = false;  
            arc = new Arc2D.Double();     
            if (ellipse == null) ellipse = new Ellipse2D.Double();
        }
        
        // calc screen pixel of radius                        
        radius =  halfAxes[0] * view.xscale;
        yradius =  halfAxes[1] * view.yscale; // radius scaled in y direction
                
        // if circle is very big, draw arc: this is very important
        // for graphical continuity
        if (radius < BIG_CIRCLE_RADIUS || yradius < BIG_CIRCLE_RADIUS) {              
            circle = ellipse;
            arcFiller = null;         
            // calc screen coords of midpoint
            mx =  midpoint.x * view.xscale + view.xZero;
            my = -midpoint.y * view.yscale + view.yZero;   
            ellipse.setFrame(mx-radius, my-yradius, 2.0*radius, 2.0*yradius);                                                      
        } else {            
        // special case: really big circle
        // draw arc according to midpoint position        	        	        	
        	// of the arc
        	mx =  midpoint.x * view.xscale + view.xZero;
            my = -midpoint.y * view.yscale + view.yZero;
        	
            angSt = Double.NaN;            
            // left 
            if (mx < 0.0) { 
                // top
                if (my < 0.0) {                
                   angSt  = -Math.acos(-mx / radius);                    
                   angEnd = -Math.asin(-my / yradius);	
                   i = 0;			                      
                }
                // bottom
                else if (my > view.height) {                    
                    angSt  =  Math.asin((my - view.height) / yradius);
                    angEnd =  Math.acos(-mx / radius);
					i = 2;    
                }
                // middle
                else {                    
                    angSt  = -Math.asin((view.height - my) / yradius);
                    angEnd =  Math.asin(my / yradius);
					i = 1;                    
                }                                
            }
            // right 
            else if (mx > view.width) {                 
                // top
                if (my < 0.0) {                    
                    angSt  = Math.PI + Math.asin(-my / yradius);
                    angEnd = Math.PI + Math.acos((mx - view.width) / radius);
					i = 6;
                }
                // bottom
                else if (my > view.height) {                    
                    angSt  = Math.PI - Math.acos((mx - view.width) / radius);
                    angEnd = Math.PI - Math.asin((my - view.height) / yradius);
					i = 4;
                }
                // middle
                else {                    
                    angSt  = Math.PI - Math.asin(my / yradius);
                    angEnd = Math.PI + Math.asin((view.height - my) / yradius);
					i = 5;
                }                                                
            }
            // top middle
            else if (my < 0.0) {                                 
                angSt  = Math.PI + Math.acos(mx / radius);
                angEnd = 2*Math.PI - Math.acos((view.width - mx) / radius);
				i = 7;
            }
            // bottom middle
            else if (my > view.height) {                                 
                angSt  = Math.acos((view.width - mx) / radius);
                angEnd = Math.PI - Math.acos(mx / radius);
				i = 3;
            }      
            // on screen (should not be needed)
            else {                
                angSt = 0.0d;
                angEnd = 2*Math.PI;                
            }            
            
            if (Double.isNaN(angSt) || Double.isNaN(angEnd)) {                
                 // to ensure drawing ...                
                angSt = 0.0d;
                angEnd = 2*Math.PI;
            }                                   

            // set arc
            circle = arc;   
            arc.setArc(mx - radius, my - yradius, 2.0*radius, 2.0*yradius,
                   Math.toDegrees(angSt), Math.toDegrees(angEnd - angSt), 
				   Arc2D.OPEN);
                    
            // set general path for filling the arc to screen borders
			if (conic.alphaValue > 0.0f) {
				if (gp == null) gp = new GeneralPath();
				else gp.reset();
				Point2D sp = arc.getStartPoint();
				Point2D ep = arc.getEndPoint();
				
				switch (i) { // case number
					case 0: // left top
						gp.moveTo(0,0);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());						
					break;
					
					case 1: // left middle
						gp.moveTo(0,view.height);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());
						gp.lineTo(0,0);						
					break;
					
					case 2: // left bottom
						gp.moveTo(0,view.height);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());												
					break;
					
					case 3: // middle bottom
						gp.moveTo(view.width, view.height);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());
						gp.lineTo(0, view.height);						
					break;

					case 4: // right bottom
						gp.moveTo(view.width, view.height);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());											
					break;

					case 5: // right middle
						gp.moveTo(view.width, 0);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());
						gp.lineTo(view.width, view.height);											
					break;

					case 6: // right top
						gp.moveTo(view.width, 0);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());																
					break;

					case 7: // top middle
						gp.moveTo(0, 0);
						gp.lineTo((float) sp.getX(), (float) sp.getY());
						gp.lineTo((float) ep.getX(), (float) ep.getY());
						gp.lineTo(view.width, 0);											
					break;
					
					default:
					gp = null;
				}
				//gp.
				arcFiller = gp;
			}                                                              
        }                                        
		shape = circle;  
        
        // set label position
        xLabel = (int) (mx - radius/2.0) ;                
        yLabel = (int) (my - yradius * 0.85) + 20;  
    }        
    
    final private void updateEllipse() {
        if (firstEllipse) {
            firstEllipse = false;
            if (ellipse == null) ellipse = new Ellipse2D.Double();               
        }
	       
		//	set transform
		transform.setTransform(view.coordTransform);
		transform.concatenate(conicTransform);  
        
        // set ellipse
        ellipse.setFrameFromCenter(0, 0, halfAxes[0], halfAxes[1]); 
        shape = transform.createTransformedShape(ellipse);                                     

        // set label coords
        labelCoords[0] = -halfAxes[0] / 2.0d;
        labelCoords[1] = halfAxes[1] * 0.85d - 20.0/view.yscale;                                
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];   
    }
    
    final private void updateHyperbola() {     
        if (firstHyperbola) {                                       
            firstHyperbola = false;
            points = PLOT_POINTS;
            hypRight = new Polyline(2 * points - 1); // right wing
            hypLeft   = new Polyline(2 * points - 1); // left wing                                     
        }                
	
        a = halfAxes[0];
		b = halfAxes[1];
			
		// draw hyperbola wing from x=a to x=x0                
		// the drawn hyperbola must be larger than the screen
		// get max distance from midpoint to screen edge
		x0 = Math.max(
			   Math.max( Math.abs(midpoint.x - view.xmin),
						 Math.abs(midpoint.x - view.xmax)),
			   Math.max( Math.abs(midpoint.y - view.ymin),
						 Math.abs(midpoint.y - view.ymax) )
			 );           
		// ensure that rotated hyperbola is fully on screen:   			         
		x0 *= 1.5;     
		//x0 += 2* (view.xmax - view.xmin);
	
		//		init step width
		if (x0 <= a) { // hyperbola is not visible on screen		
			isVisible = false;
			return;	
		}  
		
		// set number of plot points according to size of x0
		// add ten points per screen width
		n = PLOT_POINTS + 
		   (int) (Math.abs(x0 - a) / (view.xmax - view.xmin)) * 10;		
		
		if (points != n) {				
			points = Math.min(n, MAX_PLOT_POINTS); 			
			hypRight.setNumberOfPoints(2 * points -1);  
			hypLeft.setNumberOfPoints(2 * points -1);  
		}		
				
		 // hyperbola is visible on screen	
		 step = Math.sqrt((x0 - a) / (x0 + a)) / (points - 1);		               		    
		 
//		 System.out.println("n: " + n);
//		System.out.println("POINTS: " + points);
//		 System.out.println("x0   = " + x0);                
//		 System.out.println("a     = " + a);
//		 System.out.println("step = " + step + "\n");		

        // build Polyline of parametric hyperbola
        // hyp(t) = 1/(1-t�) {a(1+t�), 2bt}, 0 <= t < 1
        // this represents the first quadrant's wing of a hypberola                                                
        hypRight.x[points-1] = a;
        hypRight.y[points-1] = 0.0f;
        hypLeft.x[points-1] = -a;
        hypLeft.y[points-1] = 0.0f;
  
  		t = step;
  		i = 1;
        index0 = points;    // points ... 2*points - 2
        index1 = points-2;  // points-2 ... 0
        while (index1 >= 0) {			        	
            tsq   = t * t;
            denom = 1.0 - tsq;
            // calc coords of first quadrant
            x = (a * (1.0 + tsq) / denom);
            y = (2.0 * b * t / denom);                              
			
            // first quadrant
            hypRight.x[index0] =  x; 
            hypRight.y[index0] =  y;
            // second quadrant                  
            hypLeft.x[index0]  =   -x;
            hypLeft.y[index0]  =    y;                   
            // third quadrant
            hypLeft.x[index1]  = -x;
            hypLeft.y[index1]  = -y;
            // fourth quadrant
            hypRight.x[index1] = x;
            hypRight.y[index1] = -y;    
            
			index0++;
			index1--;
			i++;
			t = i * step;                                                            
        }                               

        // set transform for Graphics2D 
        transform.setTransform(view.coordTransform);
        transform.concatenate(conicTransform);
        
        // build general paths of hyperbola wings and transform them
		hypLeft.buildGeneralPath();
        hypRight.buildGeneralPath();
		hypLeft.transform(transform);
		hypRight.transform(transform);                                     

        // set label coords
        labelCoords[0] = 2.0 * a; 
        // point on curve: y = b * sqrt(3) minus 20 pixels
        labelCoords[1] = b * 1.7 - 20.0/view.yscale;  
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];
    }
    
    final private void updateParabola() {
        if (firstParabola) {                                      
            firstParabola = false;
            parabola = new QuadCurve2D.Double();                    
        }                
        // calc control points coords of parabola y� = 2 p x                
        x0 = Math.max( Math.abs(vertex.x - view.xmin),
                       		   Math.abs(vertex.x - view.xmax) );                                      
        x0 = Math.max(x0, Math.abs(vertex.y - view.ymin));
		x0 = Math.max(x0, Math.abs(vertex.y - view.ymax));
		
        /*
        x0 *= 2.0d;
        // y� = 2px
        y0 = Math.sqrt(2*c.p*x0);
        */
        
        // avoid sqrt by choosing x = k*p with         
        // i = 2*k is quadratic number
        // make parabola big enough: k*p >= 2*x0 -> 2*k >= 4*x0/p
        x0 = 4*x0/conic.p;
        i = 4; 
        k2 = 16;
        while (k2 < x0) {
            i += 2;
            k2 = i * i;
        }
        x0 = k2/2 * conic.p; // x = k*p
        y0 = i * conic.p;    // y = sqrt(2k p�) = i p                
        
		//	set transform
		transform.setTransform(view.coordTransform);
		transform.concatenate(conicTransform);  
         
        // setCurve(P0, P1, P2)    
        //parabola.setCurve(x0, y0, -x0, 0.0, x0, -y0);  
        //shape = transform.createTransformedShape(parabola);
        parpoints[0] = x0;
        parpoints[1] = y0;
        parpoints[2] = -x0;
        parpoints[3] = 0.0;
        parpoints[4] = x0;
        parpoints[5] = -y0;
        transform.transform(parpoints, 0, parpoints, 0, 3);
        parabola.setCurve(parpoints, 0);
        shape = parabola;                     

        // set label coords
        labelCoords[0] = 2 * conic.p; 
        // y = 2p minus 20 pixels
        labelCoords[1] = labelCoords[0] - 20.0/view.yscale;                                                         
        transform.transform(labelCoords, 0, labelCoords, 0, 1);
        xLabel = (int) labelCoords[0];
        yLabel = (int) labelCoords[1];
    }
        
    
	final public void draw(Graphics2D g2) {
        if (!isVisible) return;                
        
        g2.setColor(conic.getObjectColor());        		
        switch (type) {
            case GeoConic.CONIC_SINGLE_POINT:                         
                drawPoint.draw(g2);
                break;     
                
            case GeoConic.CONIC_INTERSECTING_LINES:
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:
                drawLines[0].draw(g2);
                drawLines[1].draw(g2);
                break;             
                
            case GeoConic.CONIC_CIRCLE:                                                                                 
            case GeoConic.CONIC_ELLIPSE:                                
			case GeoConic.CONIC_PARABOLA: 	
				if (conic.alphaValue > 0.0f) {
					g2.setColor(conic.getFillColor());
					g2.fill(shape);
					if (arcFiller != null) 
						Drawable.fillGeneralPath(arcFiller, g2);
				}			                                               
                if (geo.doHighlighting()) {
                    g2.setStroke(selStroke);
                    g2.setColor(conic.getSelColor());
                    g2.draw(shape);		
                }                  
                g2.setStroke(objStroke);
                g2.setColor(conic.getObjectColor());				
                g2.draw(shape);    
                if (labelVisible) {
					g2.setFont(view.fontConic); 
					g2.setColor(conic.getLabelColor());                   
					drawLabel(g2);                                                               
                }                
                break;            
            
           case GeoConic.CONIC_HYPERBOLA:               		          
				if (conic.alphaValue > 0.0f) {
					g2.setColor(conic.getFillColor());
					if (hypLeftOnScreen) hypLeft.fill(g2);                                                
					if (hypRightOnScreen) hypRight.fill(g2); 
				}	
				if (geo.doHighlighting()) {
					 g2.setStroke(selStroke);
					 g2.setColor(conic.getSelColor());					
					 if (hypLeftOnScreen) hypLeft.draw(g2);                                                
					 if (hypRightOnScreen) hypRight.draw(g2); 				
				 }  
				 g2.setStroke(objStroke);
				 g2.setColor(conic.getObjectColor());				 
				 if (hypLeftOnScreen) hypLeft.draw(g2);                                                
				 if (hypRightOnScreen) hypRight.draw(g2); 
				             
				 if (labelVisible) {
					 g2.setFont(view.fontConic); 
					 g2.setColor(conic.getLabelColor());                   
					 drawLabel(g2);                                                                     
				 }                            
                break;      
        }
    }
	
	/**
	 * Returns the bounding box of this Drawable in screen coordinates. 
	 * @return null when this Drawable is infinite or undefined	 
	 */
	final public Rectangle getBounds() {	
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		
		switch (type) {
	        case GeoConic.CONIC_SINGLE_POINT:                         
	            return drawPoint.getBounds();              
	                                         
	        case GeoConic.CONIC_CIRCLE:  
	        case GeoConic.CONIC_ELLIPSE:     
	        	return shape.getBounds();
	        	
	        default:
	        	return null;        
        }		
	}
    
	final public void drawTrace(Graphics2D g2) {             
        g2.setColor(conic.getObjectColor());
		switch (type) {
			case GeoConic.CONIC_SINGLE_POINT:                         
				drawPoint.drawTrace(g2);
				break;     
                
			case GeoConic.CONIC_INTERSECTING_LINES:
			case GeoConic.CONIC_DOUBLE_LINE: 
			case GeoConic.CONIC_PARALLEL_LINES:
				drawLines[0].drawTrace(g2);
				drawLines[1].drawTrace(g2);
				break;             
                
			case GeoConic.CONIC_CIRCLE:                                                                                 
			case GeoConic.CONIC_ELLIPSE:                                
			case GeoConic.CONIC_PARABOLA: 			                                                  
				g2.setStroke(objStroke);
				g2.setColor(conic.getObjectColor());				
				g2.draw(shape);    				            
				break;            
            
		   case GeoConic.CONIC_HYPERBOLA:     
				 g2.setStroke(objStroke);
				 g2.setColor(conic.getObjectColor());				 
				 hypLeft.draw(g2);                                                
				 hypRight.draw(g2); 				  
				break;      
		}
	}
    
	final public boolean hit(int x, int y) {             
        switch (type) {
            case GeoConic.CONIC_SINGLE_POINT:                         
                return drawPoint.hit(x, y);                                
                
            case GeoConic.CONIC_INTERSECTING_LINES:  
            case GeoConic.CONIC_DOUBLE_LINE: 
            case GeoConic.CONIC_PARALLEL_LINES:                
                return drawLines[0].hit(x, y) || drawLines[1].hit(x, y);
                                                
            case GeoConic.CONIC_CIRCLE:  
            case GeoConic.CONIC_ELLIPSE:
            case GeoConic.CONIC_PARABOLA:
            	if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(shape);
        		}    		
        		return strokedShape.intersects(x-3,y-3,6,6);            	
            	
            case GeoConic.CONIC_HYPERBOLA: 
            	if (strokedShape == null) {
        			strokedShape = hypLeft.createStrokedShape(objStroke);
        			strokedShape2 = hypRight.createStrokedShape(objStroke);
        		}    		
        		return strokedShape.intersects(x-3,y-3,6,6) || strokedShape2.intersects(x-3,y-3,6,6);  
            	
            	/*
            	if (tempPoint == null) {
	       			 tempPoint = new GeoPoint(conic.getConstruction());
	       		}	       		
	       		double rwX = view.toRealWorldCoordX(x);	       		
	       		double rwY = view.toRealWorldCoordY(y);	       		
	       		tempPoint.setCoords(rwX, rwY, 1.0);
	       		return conic.isOnPath(tempPoint, 0.09);
	       		*/                                                                                                          
        }        
        return false;
    }
	
	final public boolean isInside(Rectangle rect) {				
		switch (type) {
           case GeoConic.CONIC_SINGLE_POINT:                         
               return drawPoint.isInside(rect);                          
                                               
           case GeoConic.CONIC_CIRCLE:  
           case GeoConic.CONIC_ELLIPSE:                         	
        	   return rect != null && rect.contains(shape.getBounds());
		}        
		
	    return false;
	}

    public GeoElement getGeoElement() {
        return geo;
    }        
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
    
    private void initPreview() {
		//	init the conicPart for preview			
		Construction cons = previewTempPoints[0].getConstruction();		
		switch (previewMode) {
			case EuclidianView.MODE_CIRCLE_TWO_POINTS:			
				AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, 
						previewTempPoints[0], 
						previewTempPoints[1]);
				cons.removeFromConstructionList(algo);				
				initConic(algo.getCircle());
				break;
			
			case EuclidianView.MODE_CIRCLE_THREE_POINTS:
				AlgoCircleThreePoints algo2 = new AlgoCircleThreePoints(cons, 
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo2);				
				initConic(algo2.getCircle());
				break;		
				
			case EuclidianView.MODE_ELLIPSE_THREE_POINTS:
				AlgoEllipseFociPoint algo3 = new AlgoEllipseFociPoint(cons,
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo3);				
				initConic(algo3.getEllipse());
				break;												
				
			case EuclidianView.MODE_HYPERBOLA_THREE_POINTS:
				AlgoHyperbolaFociPoint algo4 = new AlgoHyperbolaFociPoint(cons,
						previewTempPoints[0], 
						previewTempPoints[1],
						previewTempPoints[2]);
				cons.removeFromConstructionList(algo4);				
				initConic(algo4.getHyperbola());
				break;												
		}		
		
		if (conic != null) 
			conic.setLabelVisible(false);		
	}
    
    // preview of circle with midpoint through a second point
	final public void updatePreview() {				
		isVisible = conic != null && prevPoints.size() == neededPrevPoints;
		if (isVisible) {
			for (int i=0; i < prevPoints.size(); i++) {
				previewTempPoints[i].setCoords((GeoPoint) prevPoints.get(i));					
			}						
			previewTempPoints[0].updateCascade();			
		}				                                            				
	}
	
	final public void updateMousePos(int x, int y) {		
		if (isVisible) {
			double xRW = view.toRealWorldCoordX(x);
			double yRW = view.toRealWorldCoordY(y);
			previewTempPoints[previewTempPoints.length-1].setCoords(xRW, yRW, 1.0);
			previewTempPoints[previewTempPoints.length-1].updateCascade();		
			update();
		}
	}
    
	final public void drawPreview(Graphics2D g2) {
		draw(g2); 
	}
	
	public void disposePreview() {	
		if (conic != null)
			conic.remove();
	}
}
