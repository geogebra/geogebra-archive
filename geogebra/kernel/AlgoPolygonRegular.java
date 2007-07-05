/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.ArrayList;


/**
 * Creates a regular Polygon for two points and the number of vertices.
 * 
 * @author  Markus Hohenwarter
 */
public class AlgoPolygonRegular extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint A, B;  // input
	private NumberValue num; // input
    private GeoPolygon poly;     // output
    
    private GeoPoint [] points;
    private GeoPoint centerPoint;	
    private MyDouble rotAngle;
    
    AlgoPolygonRegular(Construction cons, String [] labels, GeoPoint A, GeoPoint B, NumberValue num) {
        super(cons);
        this.A = A;
        this.B = B;
        this.num = num;                          
        
        // temp center point of regular polygon
        centerPoint = new GeoPoint(cons);
        rotAngle = new MyDouble(kernel);   
        
        // points array
        points = new GeoPoint[2];
        points[0] = A;
        points[1] = B;
             
        // output
        poly = new GeoPolygon(cons, points);
        
        // for AlgoElement
        setInputOutput(); 
        
        // compute poly
        compute();     
                                                  
        poly.initLabels(labels);
    }   
        
    String getClassName() {
        return "AlgoPolygonRegular";
    }        
    
    // for AlgoElement
    void setInputOutput() {
    	input = new GeoElement[3];
		input[0] = A;
		input[1] = B;
		input[2] = num.toGeoElement();    	
		// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        cons.addToAlgorithmList(this);
        
        setOutput();               
    }        

    private void setOutput() {     	
    	// size = poly + points (without A, B) + segments
    	GeoSegment [] segments = poly.getSegments();
    	GeoPoint [] points = poly.getPoints();
        int size = 1 + segments.length + points.length - 2; 
       
        output = new GeoElement[size];   
        int k = 0;
        output[k] = poly;   
        output[k].setParentAlgorithm(this);                               
              
        for (int i=0; i < segments.length; i++) {
            output[++k] = segments[i];
        }    
        
        for (int i=2; i < points.length; i++) {
            output[++k] = points[i];
            output[k].setParentAlgorithm(this);
        }
        
        // init labels
        for (int i=0; i < output.length; i++) {
        	if (!output[i].isLabelSet()) {
            	output[i].setLabel(null);	
            }
        }
    }
    
    GeoPolygon getPoly() { return poly; }    
     
    /**
     * Computes points of regular polygon
     */
    final void compute() {      
    	// check points and number
    	if (!A.isDefined() || !B.isDefined() || Double.isNaN(num.getDouble())) {
    		poly.setUndefined();
    		return;
    	}
    	
    	// get number of vertices n
    	int n = Math.max(2, (int) Math.round( num.getDouble() ));
    	
    	// make sure we have n points
    	boolean pointNumberChanged = updatePointsArray(n);
    	if (pointNumberChanged) {
    		poly.setPoints(points);
    	    setOutput();
    	}
    	
    	if (n < 3) {
    		poly.setPoints(new GeoPoint[0]);
    		poly.setUndefined();
    		return;
    	}
    	    	  	
    	// some temp values
    	double mx = (A.inhomX + B.inhomX) / 2; // midpoint of AB
    	double my = (A.inhomY + B.inhomY) / 2;
    	double alpha = Kernel.PI_2 / n; // center angle ACB
    	double beta = (Math.PI - alpha) / 2; // base angle CBA = BAC
    	
    	// normal vector of AB
    	double nx = A.inhomY - B.inhomY;
    	double ny = B.inhomX - A.inhomX;
    	
    	// center point of regular polygon
    	double tanBetaHalf = Math.tan(beta) / 2;
    	centerPoint.setCoords(mx + tanBetaHalf * nx,
    						  my + tanBetaHalf * ny,
    						  1.0);
    	
    	
    	
    	// now we have the center point of the polygon and
    	// the center angle alpha between two neighbouring points
    	// let's create the points by rotating A around the center point
    	for (int k=2; k < n; k++) {    		
    		// rotate point around center point
    		points[k].set(A); 
    		rotAngle.set(k * alpha);
    		points[k].rotate(rotAngle, centerPoint);      		
    	}
    	
    	// compute area of poly
    	poly.calcArea();    	
    }   
    
    // TODO: check this
    /*
    void update() {
        // compute output from input
        compute();
                
        // update points     
        for (int i = 0; i < pointList.size(); i++) {           
        	pointList.get(i).update();
        } 
        
        // update polygon
        poly.update();
    } */
    
    /**
     * Ensures that the pointList holds n points.
     * @param n
     * @return point number changed
     */
    private boolean updatePointsArray(int n) {
    	GeoPoint [] oldPoints = points;	
    	int oldPointsLength = oldPoints == null ? 0 : oldPoints.length;    	
    	if (oldPointsLength == n) return false;    	    	
    	
    	// new points
		points = new GeoPoint[n]; 
			
        for (int i=0; i < oldPointsLength; i++) {
        	if (i < points.length) {
        		// reuse old point
        		points[i] = oldPoints[i];	
        	} else {
        		// remove old point        		
        		oldPoints[i].setUndefined();       		
        	}        		        	
		}
        
        for (int i=0; i < oldPointsLength; i++) {
        	if (i < points.length) {
        		// reuse old point
        		points[i] = oldPoints[i];	
        	} else {
        		// remove old point                     	
        		oldPoints[i].setParentAlgorithm(null);
        		
        		// remove dependent segment algorithm that are part of this polygon
        		// to make sure we don't remove the polygon as well
        		ArrayList list = oldPoints[i].getAlogrithmList();
        		for (int k=0; k < list.size(); k++) {        			
        			AlgoElement algo = (AlgoElement) list.get(k);	
        			// make sure we don't remove the polygon as well
        			if (algo instanceof AlgoJoinPointsSegment &&
        				((AlgoJoinPointsSegment) algo).getPoly() == poly) 
        			{        				
        			} else {
        				algo.remove();
        			}
        		}
        		
        		oldPoints[i].getAlogrithmList().clear();
        		// remove point
        		oldPoints[i].doRemove();   
        	}        		        	
		}
        

        // create missing points
        for (int i=oldPointsLength; i < points.length; i++) {
			GeoPoint newPoint = new GeoPoint(cons);
			newPoint.setCoords(0,0,1); // set defined
			points[i] = newPoint;			     	        	     	
		}
        
    	// TODO: remove
        System.out.println("*** new points: ");
        for (int i=0; i < points.length; i++) {
			System.out.println(" " + i + ": " + points[i]);		     	        	     	
		}       
        
        return true;
    }
}
