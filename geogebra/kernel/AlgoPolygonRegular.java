/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
    private boolean isIniting = true;
    
    /**
     * Creates a new regular polygon algorithm
     * @param cons
     * @param labels: labels[0] for polygon, then labels for segments and then for points
     * @param A
     * @param B
     * @param num
     */
    AlgoPolygonRegular(Construction cons, String [] labels, GeoPoint A, GeoPoint B, NumberValue num) {
        super(cons);
        this.A = A;
        this.B = B;
        this.num = num;  
        
        // temp center point of regular polygon
        centerPoint = new GeoPoint(cons);
        rotAngle = new MyDouble(kernel);   
               
        // output
        points = new GeoPoint[0];
        poly = new GeoPolygon(cons, points);
                     
        // for AlgoElement
        setInputOutput(); 
        
        // compute poly
        compute();      
                                                                
        poly.initLabels(labels);   
        isIniting = false;
    }   
        
    protected String getClassName() {
        return "AlgoPolygonRegular";
    }        
    
    // for AlgoElement
    protected void setInputOutput() {
    	input = new GeoElement[3];
		input[0] = A;
		input[1] = B;
		input[2] = num.toGeoElement();    	
		// set dependencies
        for (int i = 0; i < input.length; i++) {
            input[i].addAlgorithm(this);
        }
        cons.addToAlgorithmList(this);

        // setOutput(); done in compute

        // parent of output
        poly.setParentAlgorithm(this);       
        cons.addToAlgorithmList(this); 
    }        

    private void setOutput() {    
    	if (points == null) return;
    	
    	// size = poly + points (without A, B) + segments
    	GeoSegment [] segments = poly.getSegments();
    	GeoPoint [] points = poly.getPoints();
        int size = 1 + segments.length + points.length - 2; 
       
        output = new GeoElement[size];   
        int k = 0;
        output[k] = poly;                                  
              
        for (int i=0; i < segments.length; i++) {
            output[++k] = segments[i];
        }    
        
        for (int i=2; i < points.length; i++) {
            output[++k] = points[i];            
        }                
        
        
//    	System.out.println("*** OUTPUT ****************");
//        for (int i=0; i < output.length; i++) {
//			System.out.println(" " + i + ": " + output[i].getLongDescription());		     	        	     	
//		} 
//    	System.out.println("*****************");
        
    }
    
    GeoPolygon getPoly() { return poly; }    
     
    /**
     * Computes points of regular polygon
     */
    protected final void compute() {      
    	// check points and number
    	double nd = num.getDouble();
    	if (Double.isNaN(nd)) nd = 2;
    	
    	// get integer number of vertices n
    	int n = Math.max(2, (int) Math.round( nd ));
    	
    	// if number of points changed, we need to update the
    	// points array and the output array
    	int oldPointNumber = points.length;
    	if (n != oldPointNumber) {
    		updatePointsArray(n);
    		poly.setPoints(points);
    		setOutput();
    	}
    	
    	// check if regular polygon is defined
    	if (n < 3 || !A.isDefined() || !B.isDefined()) {
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
    	
    	// update new points and segments 
    	if (n != oldPointNumber) {
    		GeoSegment [] segments = poly.getSegments();
    		   	   
			for (int i=Math.max(2, oldPointNumber); i < points.length; i++) {            	
				if (!points[i].isLabelSet())
					points[i].setLabel(null);        		           	
			}
    		
            for (int i=0; i < segments.length; i++) {
            	segments[i].getParentAlgorithm().update();   
            	if (!segments[i].isLabelSet())
            		segments[i].setLabel(null);            	
            }
    	}    	    	
    }         
    
    /**
     * Ensures that the pointList holds n points.
     * @param n
     */
    private void updatePointsArray(int n) {
    	GeoPoint [] oldPoints = points;	
    	int oldPointsLength = oldPoints == null ? 0 : oldPoints.length;    	    	
		if (oldPointsLength < 2) {
			// init old points array with first two points A and B
			oldPoints = new GeoPoint[2];
			oldPoints[0] = A;
			oldPoints[1] = B;
			oldPointsLength = 2;
		}
		
		// new points
		points = new GeoPoint[n];
        
		// reuse old points
        for (int i=0; i < oldPointsLength; i++) {
        	if (i < points.length) {
        		// reuse old point
        		points[i] = oldPoints[i];	
        	} else {
        		removePoint(oldPoints[i]);  
        	}        		        	
		}
        
        // create new points if needed
        for (int i=oldPointsLength; i < points.length; i++) {
			GeoPoint newPoint = new GeoPoint(cons);
			newPoint.setCoords(0,0,1); // set defined
			newPoint.setParentAlgorithm(this);
			points[i] = newPoint;						 	        	     
		}    
    }
    
    private void removePoint(GeoPoint oldPoint) {
    	// remove old point                     	
		oldPoint.setParentAlgorithm(null);
		
		// remove dependent segment algorithm that are part of this polygon
		// to make sure we don't remove the polygon as well
		ArrayList list = oldPoint.getAlogrithmList();
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
		
		oldPoint.getAlogrithmList().clear();
		// remove point
		oldPoint.doRemove(); 
    }
    
    
    /**
     * Calls doRemove() for all output objects of this
     * algorithm except for keepGeo.
     */
    void removeOutputExcept(GeoElement keepGeo) {
    	for (int i=0; i < output.length; i++) {
            GeoElement geo = output[i];
            if (geo != keepGeo) {
            	if (geo.isGeoPoint())
            		removePoint((GeoPoint) geo);
            	else 
            		geo.doRemove();
            }            	
        }
    }
       
}
