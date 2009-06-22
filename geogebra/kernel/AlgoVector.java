/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoVector.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel;



/**
 * Vector between two points P and Q.
 * 
 * @author  Markus
 * @version 
 */
public class AlgoVector extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoPoint P, Q;   // input
    private GeoVector  v;     // output     
        
    /** Creates new AlgoVector */  
    AlgoVector(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;         
        
        // create new vector
        v = new GeoVector(cons);   
        try {     
        	if (P.isLabelSet())
        		v.setStartPoint(P);
            else {
            	GeoPoint startPoint = new GeoPoint(P);
            	startPoint.set(P);
            	v.setStartPoint(startPoint);
            }        		
        } catch (CircularDefinitionException e) {}
                 
        setInputOutput();
        
        // compute vector PQ        
        compute();                          
        v.setLabel(label);
    }           
    
    protected String getClassName() {
        return "AlgoVector";
    }
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
        
        output = new GeoElement[1];        
        output[0] = v;        
        setDependencies(); // done by AlgoElement
    }           
    
    GeoVector getVector() { return v; }
    public GeoPoint getP() { return P; }
    public GeoPoint getQ() { return Q; }
    
    // calc the vector between P and Q    
    protected final void compute() {
        if (P.isFinite() && Q.isFinite()) {        
            v.x = Q.inhomX - P.inhomX;
            v.y = Q.inhomY - P.inhomY;             
            v.z = 0.0;
            
            // update position of unlabeled startpoint
            GeoPoint startPoint = v.getStartPoint();
            if (!startPoint.isLabelSet())
        		startPoint.set(P);            
        } else {
            v.setUndefined();
        }
    }       
    
}
