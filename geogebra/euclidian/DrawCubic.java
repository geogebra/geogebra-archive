/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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

import geogebra.kernel.EquationSolver;
import geogebra.kernel.GeoCubic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
final public class DrawCubic extends Drawable implements Previewable {        
    
    // plotpoints per quadrant for hyperbola
    private static final int PLOT_POINTS = 32;
	static final int MAX_PLOT_POINTS = 300;
    // maximum of pixels for a standard circle radius
    // bigger circles are drawn via Arc2D
    private static final double BIG_CIRCLE_RADIUS = 600;    
           
    private GeoCubic cubic;
    
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
    private GeneralPathClipped arcFiller, gp;
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
    private GeneralPathClipped hypLeft, hypRight;    
    private boolean hypLeftOnScreen, hypRightOnScreen;      
    
    // preview of circle (two points or three points)
	private ArrayList prevPoints, prevSegments, prevConics; 
	private GeoPoint [] previewTempPoints;  
	private GeoNumeric previewTempRadius;
	private int previewMode, neededPrevPoints;
	
    EquationSolver eqnSolver;


    
    /** Creates new DrawVector */
    public DrawCubic(EuclidianView view, GeoCubic c) {
    	this.view = view;
    	eqnSolver = view.getKernel().getConstruction().getEquationSolver();
        initCubic(c);
        update();
    }
    
    private void initCubic(GeoCubic c) {
    	cubic = c;
        geo = c;
                                         
    }
    

	final public void update() {
        isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        labelVisible = geo.isLabelVisible();
       
        updateStrokes(cubic);          

       // updateCubic();
        
        shape = new GeneralPath();
           
    	// shape on screen?
    	// Michael Borcherds: bugfix getBounds2D() added otherwise rotated parabolas not displayed sometimes
    	if (arcFiller == null && !shape.getBounds2D().intersects(0,0, view.width, view.height)) {				
			isVisible = false;
			return;
    	}
        
		// draw trace
		if (cubic.trace) {
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
	
	GeneralPath[] gps = new GeneralPath[3];
    boolean[] needsMove = new boolean[3];
           

    
	final public void draw(Graphics2D g2) {
        //if (!isVisible) return;                
        
		gps[0] = new GeneralPath();
		gps[1] = new GeneralPath();
		gps[2] = new GeneralPath();

		
		g2.setPaint(geo.getObjectColor());	
		//g2.setStroke(getCrossStroke(1));

    	double minx = view.getXmin();
    	double maxx = view.getXmax();
    	double miny = view.getYmin();
    	double maxy = view.getYmax();
    	
        circle = new Ellipse2D.Double();
        
        double [] eqn = new double[4];
        double [] sol = new double[3];
        double [] oldSol = new double[3];
        
    	boolean usingTop = false;
    	
    	int lastN = 0;
    	
    	int usingGP = -1;
        
        double[] coeffs = cubic.getCoeffs();

        double step = (maxx - minx) / 300;
        // y^3 + 0y^2 +2xy -x^2 = 0
    	//eqn[0] = -x^2;
    	//eqn[1] = 2x;
    	//eqn[2] = 0;
    	//eqn[3] = 1;
        
        needsMove[0] = true;
        needsMove[1] = true;
        needsMove[2] = true;
        
        int[] order = new int[3];
        int[] newOrder = new int[3];
        order[0] = 0;
        order[1] = 1;
        order[2] = 2;
       
        for (double x = minx ; x <= maxx + step ; x+=step ) {
        	double x2 = x * x;
        	double x3 = x2 * x;
        	eqn[0] = coeffs[12] * x3 + coeffs[13] * x2 + coeffs[14] * x + coeffs[15];
        	eqn[1] = coeffs[8] * x3 + coeffs[9] * x2 + coeffs[10] * x + coeffs[11];
        	eqn[2] = coeffs[4] * x3 + coeffs[5] * x2 + coeffs[6] * x + coeffs[7];
        	eqn[3] = coeffs[0] * x3 + coeffs[1] * x2 + coeffs[2] * x + coeffs[3];
        	oldSol[order[0]] = sol[0];
        	oldSol[order[1]] = sol[1];
        	oldSol[order[2]] = sol[2];
        	int n = solveCubic(eqn, sol);
        	
        	switch (n) {
        	default: // 0
        		needsMove[0] = true;
        		needsMove[1] = true;
        		needsMove[2] = true;
        		lastN = 0;
        		break;
        	case 1:
        		
        		if (lastN == 3) {
        			double diff0 = Math.abs(sol[0] - oldSol[order[0]]);
        			double diff1 = Math.abs(sol[0] - oldSol[order[1]]);
        			double diff2 = Math.abs(sol[0] - oldSol[order[2]]);
        			//Application.debug("diffs:"+diff0+" "+diff1+" "+diff2);
        			if (diff0 < diff1 && diff0 < diff2) {
        				//Application.debug("A"+x);
            	        newOrder[0] = order[0];
            	        newOrder[1] = order[1];
            	        newOrder[2] = order[2];   
            	        
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[2]] + oldSol[order[1]])/2), order[1]);
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[2]] + oldSol[order[1]])/2), order[2]);

            	        
        			} else if (diff1 < diff2) {
           				//Application.debug("B"+x);
           			    newOrder[0] = order[1];
        				newOrder[1] = order[0];
        				newOrder[2] = order[2];
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[2]])/2), order[0]);
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[2]])/2), order[2]);
        			} else {
           				//Application.debug("C"+x);
           				newOrder[0] = order[2];
        				newOrder[1] = order[1];
        				newOrder[2] = order[0];
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[1]])/2), order[0]);
            			lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[1]])/2), order[1]);
         			}
        			for (int i = 0 ; i < 3 ; i ++) {
        				order[i] = newOrder[i];
        			}
        			
        			//Application.debug("3 -> 1 x = "+x+": "+order[0]+" "+order[1]+" "+order[2]);
        		} 

    			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[0]), order[0]);
        		// offscreen test
    			// still draw first time otherwise get a gap
    			if (sol[0] < miny - 5 || sol[0] > maxy + 5)        		
        			needsMove[order[0]] = true;
        				
    			
    			needsMove[order[1]] = true;
    			needsMove[order[2]] = true;
    			lastN = 1;
        		break;
        	case 2:
        		lastN = 2; 
        		Application.debug("2");
        		break;
        	case 3:
        		
        		if (lastN == 1) {
        			double diff0 = Math.abs(oldSol[0] - sol[order[0]]);
        			double diff1 = Math.abs(oldSol[0] - sol[order[1]]);
        			double diff2 = Math.abs(oldSol[0] - sol[order[2]]);
        			//Application.debug("diffs:"+diff0+" "+diff1+" "+diff2);
        			if (diff0 < diff1 && diff0 < diff2) {
        				//Application.debug("A"+x);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[1]] + sol[order[2]])/2), order[1]);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[1]] + sol[order[2]])/2), order[2]);
            	        newOrder[0] = order[0];
            	        newOrder[1] = order[1];
            	        newOrder[2] = order[2];        				
        			} else if (diff1 < diff2) {
        				//Application.debug("B"+x);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[1]] + sol[order[0]])/2), order[1]);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[1]] + sol[order[0]])/2), order[0]);
        				newOrder[0] = order[0];
        				newOrder[1] = order[1];
        				newOrder[2] = order[2];
        			} else {
        				//Application.debug("CC"+x);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[0]] + sol[order[2]])/2), order[0]);
            			//lineTo(view.toScreenCoordX(x - step / 2), view.toScreenCoordY((sol[order[0]] + sol[order[2]])/2), order[2]);
        				newOrder[0] = order[2];
        				newOrder[1] = order[1];
        				newOrder[2] = order[0];
        			}
        			
        			for (int i = 0 ; i < 3 ; i ++) {
        				order[i] = newOrder[i];
        			}
        			//Application.debug("1 -> 3 x = "+x+": "+order[0]+" "+order[1]+" "+order[2]);
        			
       		} else {
        	        //order[0] = 0;
        	        //order[1] = 1;
        	        //order[2] = 2;
        		}

        		
            	for (int j = 0 ; j < n ; j++) {
        			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[j]), order[j]);
            		// offscreen test
        			// still draw first time otherwise get a gap
            		if (sol[j] < miny - 5 || sol[j] > maxy + 5)
            			needsMove[order[j]] = true;

            	}
            	
            	lastN = 3;
       		
        		break;
        	}
        	
        	/*
        	for (int j = 0 ; j < n ; j++) {
        		if (sol[j] > miny && sol[j] < maxy) {
        	        circle.setFrame(view.toScreenCoordX(x), view.toScreenCoordY(sol[j]), 1, 1);       			
        			g2.draw(circle);  			
        		}
        	}*/
        	
        	//System.out.println(""+eqnSolver.solveCubic(eqn, sol));
        }

			                                               
                if (geo.doHighlighting()) {
                    g2.setStroke(selStroke);
                    g2.setColor(cubic.getSelColor());
                	Drawable.drawWithValueStrokePure(gps[0], g2);
                	Drawable.drawWithValueStrokePure(gps[1], g2);
                	Drawable.drawWithValueStrokePure(gps[2], g2);
                }                  
                g2.setStroke(objStroke);
                g2.setColor(cubic.getObjectColor());				
            	Drawable.drawWithValueStrokePure(gps[0], g2);
            	//g2.setColor(Color.red);
            	Drawable.drawWithValueStrokePure(gps[1], g2);
            	//g2.setColor(Color.green);
            	Drawable.drawWithValueStrokePure(gps[2], g2);
                if (labelVisible) {
					g2.setFont(view.fontConic); 
					g2.setColor(cubic.getLabelColor());                   
					drawLabel(g2);                                                               
                }                
       
          
    }
	
    private void lineTo(float x, float y, int i) {
    	
    	if (needsMove[i]) {
    		gps[i].moveTo(x, y);
    		needsMove[i] = false;
    	} else
    		gps[i].lineTo(x, y);
    	
    }
    
    private int solveCubic(double[] eqn, double[] sol) {
    	int ret = eqnSolver.solveCubic(eqn, sol);
    	double temp;
    	if (ret < 3) return ret;
    		
    	if (sol[0] > sol[1]) {
    		temp = sol[0];
    		sol[0] = sol[1];
    		sol[1] = temp;
    	}
    	
    	if (sol[1] > sol[2]) {
    		temp = sol[2];
    		sol[2] = sol[1];
    		sol[1] = temp;
    	}
    	
    	if (sol[0] > sol[1]) {
    		temp = sol[0];
    		sol[0] = sol[1];
    		sol[1] = temp;
    	}
   	
    	if (sol[0] > sol[1]) Application.debug("greatera");
    	if (sol[1] > sol[2]) Application.debug("greaterb");
    	
    	return ret;
    }
    

	/**
	 * Returns the bounding box of this Drawable in screen coordinates. 
	 * @return null when this Drawable is infinite or undefined	 
	 */
	final public Rectangle getBounds() {	
		if (!geo.isDefined() || !geo.isEuclidianVisible())
			return null;
		
 
	        	return shape.getBounds();

	}
    
	final public void drawTrace(Graphics2D g2) {             
			                                                  
				g2.setStroke(objStroke);
				g2.setColor(cubic.getObjectColor());				
				g2.draw(shape);    				            
         

	}
    
	final public boolean hit(int x, int y) {             

            	if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(shape);
        		}    		
    			if (cubic.alphaValue > 0.0f) 
    				return shape.intersects(x-3,y-3,6,6);  
    			else
    				return strokedShape.intersects(x-3,y-3,6,6);            	
    }
	
	final public boolean isInside(Rectangle rect) {				
                     	
        	   return rect != null && rect.contains(shape.getBounds());
      
	
	}

    public GeoElement getGeoElement() {
        return geo;
    }        
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
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
		if (cubic != null)
			cubic.remove();
	}

	public void updatePreview() {
		// TODO Auto-generated method stub
		
	}
}
