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

    AlgoSemicircle(Construction cons, String label,
    		GeoPoint A, GeoPoint B) {
    	this(cons, A, B);
    	conicPart.setLabel(label);
    }
    
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
    
	protected String getClassName() {
		return "AlgoSemicircle";
	}

    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;      
        input[1] = B;    

        output = new GeoElement[1];
        output[0] = conicPart;

        setDependencies();
    }
    
    public GeoConicPart getSemicircle() {
    	return conicPart;
    }
    
    GeoPoint getA() {
    	return A;
    }
    
    GeoPoint getB() {
    	return B;
    }    
    
    void compute() {
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
        StringBuffer sb = new StringBuffer();

        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        sb.append(app.getPlain("SemicircleThroughAandB",A.getLabel(),B.getLabel()));
        
        /*
        if (!app.isReverseLanguage()) { 
            sb.append(app.getPlain("Semicircle"));
            sb.append(' ');
        }
        sb.append(app.getPlain("through"));
        sb.append(' ');
        sb.append(A.getLabel());
        sb.append(' ');
        sb.append(app.getPlain("and"));
        sb.append(' ');
        sb.append(B.getLabel());
        if (app.isReverseLanguage()) { 
            sb.append(' ');
            sb.append(app.getPlain("of"));
            sb.append(app.getPlain("Semicircle"));
        }*/
        


        return sb.toString();
    }
}
