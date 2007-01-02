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
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.MyDouble;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentNumber extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoNumeric number;     // output              
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentNumber(Construction cons, String label, ExpressionNode root, boolean isAngle) {
    	super(cons);
        this.root = root;  
        
        if (isAngle) {
            number = new GeoAngle(cons);
        } else {
            number = new GeoNumeric(cons); 
        }
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        number.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoDependentNumber";
	}
    
    // for AlgoElement
    void setInputOutput() {
        input = root.getGeoElementVariables();
        
        output = new GeoElement[1];        
        output[0] = number;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getNumber() { return number; }
    
    ExpressionNode getExpression() { return root; }
    
    // calc the current value of the arithmetic tree
    final void compute() {    
    	try {
	        number.setValue((MyDouble) root.evaluate());
	    } catch (Exception e) {
	    	number.setUndefined();
		}
    }   
    
    final public String toString() {
        // was defined as e.g.  r = 5a - 3b
        // return 5a - 3b
        return root.toString();
    }
}
