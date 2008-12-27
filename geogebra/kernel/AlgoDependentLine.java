/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentLine.java
 *
 * Created on 29. Oktober 2001
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Polynomial;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Equation equation;
    private ExpressionValue [] ev = new ExpressionValue[3];  // input
    private GeoLine g;     // output                 
        
    /** Creates new AlgoDependentLine */
    public AlgoDependentLine(Construction cons, String label, Equation equ) {        
    	super(cons);
        equation = equ;                              
        Polynomial lhs = equ.getNormalForm();
        
        ev[0] = lhs.getCoefficient("x");        
   		ev[1] = lhs.getCoefficient("y");        
   		ev[2] = lhs.getConstantCoefficient(); 
   		
   		// check coefficients
        for (int i=0; i<3; i++) {
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate();
            
            // check that coefficients are numbers
            ((NumberValue) ev[i].evaluate()).getDouble();    
        }
        
        g = new GeoLine(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number        
        compute();      
        g.setLabel(label);        
    }   
    
	protected String getClassName() {
		return "AlgoDependentLine";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = equation.getGeoElementVariables();         
        
        output = new GeoElement[1];        
        output[0] = g;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {  
    	try {
	        g.x = ((NumberValue) ev[0].evaluate()).getDouble();
	        g.y = ((NumberValue) ev[1].evaluate()).getDouble();
	        g.z = ((NumberValue) ev[2].evaluate()).getDouble();   
	    } catch (Throwable e) {
			g.setUndefined();
		}
    }   
          
    final public String toString() { 
        return equation.toString();
    } 
    
}
