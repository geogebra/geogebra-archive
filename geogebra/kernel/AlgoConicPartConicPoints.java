/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;



/**
 * Arc or sector defined by a conic, start- and end-point.
 */
public class AlgoConicPartConicPoints extends AlgoConicPart {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GeoPoint startPoint, endPoint;
	
	// temp points
	private GeoPoint P, Q;	

    /**
     * Creates a new arc or sector algorithm.
     * The type is either GeoConicPart.CONIC_PART_ARC or 
     * GeoConicPart.CONIC_PART_ARC       
     */
    AlgoConicPartConicPoints(Construction cons, String label,
    		GeoConic circle, GeoPoint startPoint, GeoPoint endPoint,
    		int type) {
        super(cons, type);        
        conic = circle;
        this.startPoint = startPoint;
        this.endPoint = endPoint;                          
        
        // temp points
        P = new GeoPoint(cons, conic);
        Q = new GeoPoint(cons, conic);
        
        conicPart = new GeoConicPart(cons, type);

        setInputOutput(); // for AlgoElement      
        compute();
        
        conicPart.setLabel(label);
    }    	
    
    GeoPoint getStartPoint() {
    	return startPoint;
    }

    GeoPoint getEndPoint() {
    	return endPoint;
    }
    
    GeoConic getConic() {
    	return conic;
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = conic;      
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
    	conicPart.setParameters(P.getPathParameter().t, Q.getPathParameter().t, 
    			true);
    }
    
}
