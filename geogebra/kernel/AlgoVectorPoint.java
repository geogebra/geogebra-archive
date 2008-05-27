/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVectorPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 * Vector v = P - (0, 0)
 * @author  Markus
 * @version 
 */
public class AlgoVectorPoint extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoPoint P;   // input
    private GeoVector  v;     // output                    
    
    AlgoVectorPoint(Construction cons, String label, GeoPoint P) {
        super(cons);
        this.P = P;
        
        // create new vector
        v = new GeoVector(cons);                
        setInputOutput();
                        
        compute();        
        v.setLabel(label);
    }           
    
    protected String getClassName() {
        return "AlgoVectorPoint";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = P;
        
        output = new GeoElement[1];        
        output[0] = v;        
        setDependencies(); // done by AlgoElement
    }           
    
    GeoVector getVector() { return v; }
    public GeoPoint getP() { return P; }    
    
    // calc vector OP   
    protected final void compute() {                
        if (P.isFinite()) {                    
            v.x = P.inhomX;
            v.y = P.inhomY;        
            v.z = 0.0;
        } else {
            v.setUndefined();
        }
    }       
    
}
