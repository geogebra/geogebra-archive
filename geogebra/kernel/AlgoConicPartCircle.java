/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;



/**
 * Circular arc or sector defined by the circle's center, one point
 * on the circle (start point) and another point (angle for end-point).
 */
public class AlgoConicPartCircle extends AlgoConicPart {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPoint center, startPoint, endPoint;	

	private GeoPoint P, Q;			
	
    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    AlgoConicPartCircle(Construction cons, String label,
    		 GeoPoint center, GeoPoint startPoint, GeoPoint endPoint,
    		int type) {
    	this(cons, center, startPoint, endPoint, type);
    	conicPart.setLabel(label);
    }
    
    public  AlgoConicPartCircle(Construction cons, 
  	   		 GeoPoint center, GeoPoint startPoint, GeoPoint endPoint, int type) {      
        super(cons, type);  
        this.center = center;
        this.startPoint = startPoint;
        this.endPoint = endPoint;   
                
        // create circle with center through startPoint        
        AlgoCircleTwoPoints algo = new AlgoCircleTwoPoints(cons, center, startPoint);
        cons.removeFromConstructionList(algo);
        conic = algo.getCircle();

        // temp Points
        P = new GeoPoint(cons);
        Q = new GeoPoint(cons);
        
        conicPart = new GeoConicPart(cons, type);
        conicPart.addPointOnConic(startPoint);
        
        setInputOutput(); // for AlgoElement      
        compute();               
    }    	
    
    GeoPoint getStartPoint() {
    	return startPoint;
    }
    
    GeoPoint getEndPoint() {
    	return endPoint;
    }
    
    GeoPoint getCenter() {
    	return center;
    }
    
	public String getClassName() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return "AlgoCircleArc";
			default:
				return "AlgoCircleSector";
		}		
	}
	
	public int getRelatedModeID() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return EuclidianConstants.MODE_CIRCLE_ARC_THREE_POINTS;
			default:
				return EuclidianConstants.MODE_CIRCLE_SECTOR_THREE_POINTS;
		}
	}

    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = center;      
        input[1] = startPoint;
        input[2] = endPoint;

        output = new GeoElement[1];
        output[0] = conicPart;

        setDependencies();
    }
    
    protected final void compute() {
    	// the temp points P and Q should lie on the conic
    	P.setCoords(startPoint);
    	conic.pointChanged(P);
    	
    	Q.setCoords(endPoint);
    	conic.pointChanged(Q);
    	
    	// now take the parameters from the temp points
    	conicPart.set(conic);    	    	    	    	
    	conicPart.setParameters(P.getPathParameter().t, Q.getPathParameter().t, true);
    }
    
}
