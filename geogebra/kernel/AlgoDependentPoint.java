/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoDependentPoint.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.VectorValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentPoint extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoPoint P;     // output         
    
    private GeoVec2D temp;
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentPoint(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;        
        
        P = new GeoPoint(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        P.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoDependentPoint";
	}
	
    // for AlgoElement
    void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        output = new GeoElement[1];        
        output[0] = P;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint getPoint() { return P; }
    
    // calc the current value of the arithmetic tree
    final void compute() {   
    	try {
	        temp = ((VectorValue) root.evaluate()).getVector();
	        if (Double.isInfinite(temp.x) || Double.isInfinite(temp.y)) {
	        	P.setUndefined();
	        } else {
				P.setCoords( temp.x, temp.y, 1.0); 
	        }		
    	} catch (Exception e) {
	    	P.setUndefined();
	    }
    }   
    
    final public String toString() {              
        return root.toString();
    }
}
