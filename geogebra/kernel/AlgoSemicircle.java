/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Semicircle defined by two points A and B (start and end point).
 */
public class AlgoSemicircle extends AlgoElement {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint A, B;	// input
	private GeoConicPart conicPart; // output
	
	private GeoPoint M; // midpoint of AB
	private GeoConic conic;

	/**
	 * Creates new semicircle algoritm
	 * @param cons construction
	 * @param label label for the semicircle
	 * @param A first endpoint
	 * @param B second endpoint
	 */
    AlgoSemicircle(Construction cons, String label,
    		GeoPoint A, GeoPoint B) {
    	this(cons, A, B);
    	conicPart.setLabel(label);
    }
    
    /**
	 * Creates new unlabeled semicircle algoritm
	 * @param cons construction
	 * @param A first endpoint
	 * @param B second endpoint
	 */
    public AlgoSemicircle(Construction cons, 
    		GeoPoint A, GeoPoint B) {
        super(cons);        
        this.A = A;
        this.B = B; 

        // helper algo to get midpoint
        AlgoMidpoint algom = 
        	new AlgoMidpoint(cons, A, B);
        cons.removeFromConstructionList(algom);		
        M = algom.getPoint();                

        // helper algo to get circle
        AlgoCircleTwoPoints algo = 
        	new AlgoCircleTwoPoints(cons, M, B);
        cons.removeFromConstructionList(algo);		
        conic = algo.getCircle(); 
        
        conicPart = new GeoConicPart(cons, GeoConicPart.CONIC_PART_ARC);
        conicPart.addPointOnConic(A);
        conicPart.addPointOnConic(B);
        
        setInputOutput(); // for AlgoElement      
        compute();               
    }    	
    
	public String getClassName() {
		return "AlgoSemicircle";
	}

    // for AlgoElement
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;      
        input[1] = B;    

        setOutputLength(1);
        setOutput(0,conicPart);

        setDependencies();
    }
    
	/**
	 * Returns the semicercle
	 * @return the semicircle
	 */
    public GeoConicPart getSemicircle() {
    	return conicPart;
    }
    
    /**
     * Returns first endpoint
     * @return first endpoint
     */
    GeoPoint getA() {
    	return A;
    }
    
    /**
     * Returns second endpoint
     * @return second endpoint
     */
    GeoPoint getB() {
    	return B;
    }    
    
    /**
     * Returns the full circle
     * @return full circle
     */
    public GeoConic getConic() {
    	return conic;
    }
    
    protected void compute() {
    	if (!conic.isDefined()) {
    		conicPart.setUndefined();
    		return;
    	}
    	
    	double alpha = Math.atan2(B.inhomY - A.inhomY, B.inhomX - A.inhomX);
    	double beta = alpha + Math.PI;
    	
    	conicPart.set(conic); 
    	conicPart.setParameters(alpha, beta, true);
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("SemicircleThroughAandB",A.getLabel(),B.getLabel());
    }
}
