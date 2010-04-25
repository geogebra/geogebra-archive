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
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

/**
 *
 * @author  Michael
 * @version 
 */
final public class DrawCubic extends Drawable {        
    
    // plotpoints per quadrant for hyperbola
    private static final int PLOT_POINTS = 32;
	static final int MAX_PLOT_POINTS = 300;
    // maximum of pixels for a standard circle radius
    // bigger circles are drawn via Arc2D
    private static final double BIG_CIRCLE_RADIUS = 600;    
           
    private GeoCubic cubic;
    
    private boolean isVisible, labelVisible;
    
    Shape strokedShape3;
                  
	
    EquationSolver eqnSolver;

	GeneralPath[] gps = new GeneralPath[3];
    boolean[] needsMove = new boolean[3];
    
    /** Creates new DrawVector */
    public DrawCubic(EuclidianView view, GeoCubic c) {
    	this.view = view;
    	eqnSolver = view.getKernel().getConstruction().getEquationSolver();
		gps[0] = new GeneralPath();
		gps[1] = new GeneralPath();
		gps[2] = new GeneralPath();

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

        updateGP();
        
        
    	// shape on screen?
    	// Michael Borcherds: bugfix getBounds2D() added otherwise rotated parabolas not displayed sometimes
    	//if (arcFiller == null && !shape.getBounds2D().intersects(0,0, view.width, view.height)) {				
		//	isVisible = false;
		//	return;
    	//}
        
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
	

           

    
	final public void updateGP() {
        
		gps[0].reset();
		gps[1].reset();
		gps[2].reset();
        
		if (!isVisible) return;                

		
		//g2.setPaint(geo.getObjectColor());	
		//g2.setStroke(getCrossStroke(1));

    	double minx = view.getXmin();
    	double maxx = view.getXmax();
    	double miny = view.getYmin();
    	double maxy = view.getYmax();
    	
        double [] eqn = new double[4];
        double [] sol = new double[3];
        double [] oldSol = new double[3];

    	int lastN = 0;

        
        double[] coeffs = cubic.getCoeffs();

        double step = (maxx - minx) / MAX_PLOT_POINTS;
        // y^3 + 0y^2 +2xy -x^2 = 0
    	//eqn[0] = -x^2;
    	//eqn[1] = 2x;
    	//eqn[2] = 0;
    	//eqn[3] = 1;
        
        needsMove[0] = true;
        needsMove[1] = true;
        needsMove[2] = true;
        
        boolean checkAsymptote = true;
        
        int[] order = new int[3];
        double[] newSol = new double[3];
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
        	//Application.debug("eqn: "+eqn[3]+" "+eqn[2]+" "+eqn[1]+" "+eqn[0]);
        	int n = solveCubic(eqn, sol);
        	
        	/*
    		System.out.println(n+" solutions: x = ");       	
        	for (int i = 0 ; i < n ; i++) {
        		System.out.println(sol[i]+", ");
        	}
    		System.out.println("\n");    */    	
        	
        	switch (n) {
        	default: // 0
        		if (lastN == 2){
        			lineTo(view.toScreenCoordX(x - step * 0.2), view.toScreenCoordY((oldSol[0] + oldSol[1])/2), 0);
        			lineTo(view.toScreenCoordX(x - step * 0.2), view.toScreenCoordY((oldSol[0] + oldSol[1])/2), 1);
        		}
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
            	        
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[2]] + oldSol[order[1]])/2), order[1]);
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[2]] + oldSol[order[1]])/2), order[2]);

            	        
        			} else if (diff1 < diff2) {
           				//Application.debug("B"+x);
           			    newOrder[0] = order[1];
        				newOrder[1] = order[0];
        				newOrder[2] = order[2];
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[2]])/2), order[0]);
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[2]])/2), order[2]);
        			} else {
           				//Application.debug("C"+x);
           				newOrder[0] = order[2];
        				newOrder[1] = order[1];
        				newOrder[2] = order[0];
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[1]])/2), order[0]);
            			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((oldSol[order[0]] + oldSol[order[1]])/2), order[1]);
         			}
        			for (int i = 0 ; i < 3 ; i ++) {
        				order[i] = newOrder[i];
        			}
        			
        			//Application.debug("3 -> 1 x = "+x+": "+order[0]);//
        		} 

        		//Application.debug("drawing: "+view.toScreenCoordY(sol[0])+" "+order[0]);
    			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[0]), order[0]);
        		// offscreen test
    			// still draw first time otherwise get a gap
    			if (sol[0] < miny - 5 || sol[0] > maxy + 5)        		
        			needsMove[order[0]] = true;
        				
    			
    			needsMove[order[1]] = true;
    			needsMove[order[2]] = true;
    			lastN = 1;
        		break;
        	case 2: // should only happen for curves with 2 branches
        		// eg x^3y^2=3
        		//Application.debug("N=2");
        		if (lastN == 0){
        			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[0]+sol[1])/2), 0);
        			lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[0]+sol[1])/2), 1);
        		} else if (lastN == 3) {
        			// eg y^3 + y^2 - x^2 = 0 at x=0
        			//Application.debug(oldSol[order[0]]+" "+oldSol[order[1]]+" "+oldSol[order[2]]);
        			//Application.debug(Math.abs(oldSol[0] - oldSol[1]) +" "+ Math.abs(oldSol[1] - oldSol[2]));
        			if (Math.abs(oldSol[order[0]] - oldSol[order[1]]) < Math.abs(oldSol[order[1]] - oldSol[order[2]])) {
        				sol[2] = sol[1];
        				sol[1] = sol[0];
            			//Application.debug("fix1 "+sol[0]+" "+sol[1]+" "+sol[2]);
        			} else {
        				sol[2] = sol[1];
            			//Application.debug("fix2 "+sol[0]+" "+sol[1]+" "+sol[2]);
           		    }
                	for (int j = 0 ; j < 3 ; j++) 
            			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[j]), order[j]);
        			lastN = 3;
        			break;
        		}
        		lastN = 2; 
    			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[0]), 0);
    			lineTo(view.toScreenCoordX(x), view.toScreenCoordY(sol[1]), 1);
        		//Application.debug("2");
        		break;
        	case 3:
        		
        		if (lastN == 1) {
        			double diff0 = Math.abs(oldSol[order[0]] - sol[0]);
        			double diff1 = Math.abs(oldSol[order[0]] - sol[1]);
        			double diff2 = Math.abs(oldSol[order[0]] - sol[2]);
        			//Application.debug("diffs:"+diff0+" "+diff1+" "+diff2);
        			if (diff0 < diff1 && diff0 < diff2) {
        				//Application.debug("AA"+x);
        				if (Math.abs(view.toScreenCoordY(sol[1]) - view.toScreenCoordY((sol[2]))) < 20) {
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[2])/2), order[1]);
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[2])/2), order[2]);
        				}
            			newOrder[0] = order[0];
            	        newOrder[1] = order[1];
            	        newOrder[2] = order[2];        				
        			} else if (diff1 < diff2) {
        				//Application.debug("BB"+x);
        				if (Math.abs(view.toScreenCoordY(sol[1]) - view.toScreenCoordY((sol[2]))) < 20) {
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[2])/2), order[1]);
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[2])/2), order[2]);
        				}
        				newOrder[0] = order[1];
        				newOrder[1] = order[0];
        				newOrder[2] = order[2];
        			} else {
        				//Application.debug("CC"+x);
        				if (Math.abs(view.toScreenCoordY(sol[1]) - view.toScreenCoordY((sol[0]))) < 20) {
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[0])/2), order[2]);
        					lineTo(view.toScreenCoordX(x - step * 0.8), view.toScreenCoordY((sol[1]+sol[0])/2), order[1]);
        				}
        				newOrder[0] = order[2];
        				newOrder[1] = order[1];
        				newOrder[2] = order[0];
        			}
        			
        			for (int i = 0 ; i < 3 ; i ++) {
        				order[i] = newOrder[i];
        			}
        			//Application.debug("1 -> 3 x = "+x+": "+order[0]+" "+order[1]+" "+order[2]);
        			
        		} else if (lastN == 3) {
        			if (checkAsymptote && (Math.abs(oldSol[0] - sol[1]) < Math.abs(oldSol[1] - sol[1])) 
                     		 && (Math.abs(oldSol[1] - sol[2]) < Math.abs(oldSol[2] - sol[2]))) {
          				//Application.debug("asymptote at x = "+x+" order[0] = "+order[0]);
          				// draw asymptote to screen edge
          				//lineTo(view.toScreenCoordX(x - step), view.toScreenCoordY(100), order[1]);
               		
          				// start new path
          				needsMove[2] = true;			
          				

          				if (order[0] == 0) {
	           				newOrder[0] = 2;
	           				newOrder[1] = 0;
	           				newOrder[2] = 1;
          				} else
          				if (order[0] == 1) {
          					Application.debug("TODO A");
	           				//newOrder[0] = order[2];
	           				//newOrder[1] = order[0];
	           				//newOrder[2] = order[1];
          				} else
          				if (order[0] == 2) {
          					Application.debug("TODO B");
	           				//newOrder[0] = order[2];
	           				//newOrder[1] = order[0];
	           				//newOrder[2] = order[1];
          				}
          				
           			for (int i = 0 ; i < 3 ; i ++) {
           				order[i] = newOrder[i];
           			}
           			
           			checkAsymptote = false;
       			} else
        			if (checkAsymptote && (Math.abs(oldSol[2] - sol[1]) < Math.abs(oldSol[1] - sol[1])) 
                     		 && (Math.abs(oldSol[1] - sol[0]) < Math.abs(oldSol[0] - sol[0]))) {
          				//Application.debug("asymptote2 at x = "+x+" order[0] = "+order[0]);
          				// draw asymptote to screen edge
          				//lineTo(view.toScreenCoordX(x - step), view.toScreenCoordY(100), order[1]);
               		
          				// start new path
          				needsMove[0] = true;			
          				

          				if (order[0] == 0) {
	           				newOrder[0] = 1;
	           				newOrder[1] = 2;
	           				newOrder[2] = 0;
          				} else
          				if (order[0] == 1) {
          					Application.debug("TODO C");
	           				//newOrder[0] = 2;
	           				//newOrder[1] = 1;
	           				//newOrder[2] = 0;
          				} else
          				if (order[0] == 2) {
	           				newOrder[0] = 0;
	           				newOrder[1] = 2;
	           				newOrder[2] = 1;
          				}
          				
           			for (int i = 0 ; i < 3 ; i ++) {
           				order[i] = newOrder[i];
           			}
           			
           			checkAsymptote = false;
       			}
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
	}
	
	final public void draw(Graphics2D g2) {

		if (!isVisible) return; 

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
    	if (ret < 2) return ret;
    		
    	// sort solutions
    	if (sol[0] > sol[1]) {
    		temp = sol[0];
    		sol[0] = sol[1];
    		sol[1] = temp;
    	}
    	
    	if (ret == 2) return ret;
    	
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
   	
    	//if (sol[0] > sol[1]) Application.debug("greatera");
    	//if (sol[1] > sol[2]) Application.debug("greaterb");
    	
    	return ret;
    }
    

	/**
	 * Returns the bounding box of this Drawable in screen coordinates. 
	 * @return null when this Drawable is infinite or undefined	 
	 */
	final public Rectangle getBounds() {	
		return null;

	}
    
	final public void drawTrace(Graphics2D g2) {             
			                                                  
				g2.setStroke(objStroke);
				g2.setColor(cubic.getObjectColor());				
            	Drawable.drawWithValueStrokePure(gps[0], g2);
            	Drawable.drawWithValueStrokePure(gps[1], g2);
            	Drawable.drawWithValueStrokePure(gps[2], g2);
         

	}
    
	final public boolean hit(int x, int y) {             

            	if (strokedShape == null) {
        			strokedShape = objStroke.createStrokedShape(gps[0]);
        			strokedShape2 = objStroke.createStrokedShape(gps[1]);
        			strokedShape3 = objStroke.createStrokedShape(gps[2]);
        		}    		

		return strokedShape.intersects(x-3,y-3,6,6) || strokedShape2.intersects(x-3,y-3,6,6) || strokedShape3.intersects(x-3,y-3,6,6);
    }
	
	final public boolean isInside(Rectangle rect) {				
                     	
        	   return rect != null && rect.contains(gps[0].getBounds()) && rect.contains(gps[1].getBounds()) && rect.contains(gps[2].getBounds());
      
	
	}

    public GeoElement getGeoElement() {
        return geo;
    }        
    
    public void setGeoElement(GeoElement geo) {
        this.geo = geo;
    }
	

}
