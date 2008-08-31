/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;

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
    
	protected String getClassName() {
		return "AlgoDependentNumber";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        output = new GeoElement[1];        
        output[0] = number;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoNumeric getNumber() { return number; }
    
    ExpressionNode getExpression() { return root; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {    
    	try {
    		NumberValue nv = (NumberValue) root.evaluate();
	        number.setValue(nv.getDouble());
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
