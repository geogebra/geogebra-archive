/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentConic.java
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
public class AlgoDependentConic extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Equation equation;
    private ExpressionValue [] ev = new ExpressionValue[6];  // input
    private GeoConic conic;     // output                 
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentConic(Construction cons,String label, Equation equ) {  
       	super(cons);
        equation = equ;                
        Polynomial lhs = equ.getNormalForm();
        
        ev[0] = lhs.getCoefficient("xx");       
        ev[1] = lhs.getCoefficient("xy");       
        ev[2] = lhs.getCoefficient("yy");        
        ev[3] = lhs.getCoefficient("x");        
        ev[4] = lhs.getCoefficient("y");                
        ev[5] = lhs.getConstantCoefficient();          
        // find constant parts of input and evaluate them right now
        for (int i=0; i<6; i++) {
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate();
        }
        
        conic = new GeoConic(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number        
        compute();      
        conic.setLabel(label);                
    }   
    
	protected String getClassName() {
		return "AlgoDependentConic";
	}
    
    // for AlgoElement
    void setInputOutput() {
        input = equation.getGeoElementVariables();  
        
        output = new GeoElement[1];        
        output[0] = conic;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoConic getConic() { return conic; }
    
    // calc the current value of the arithmetic tree
    final void compute() {   
    	try {
	        conic.setCoeffs( 
	                ((NumberValue) ev[0].evaluate()).getDouble(),
	                ((NumberValue) ev[1].evaluate()).getDouble(),
	                ((NumberValue) ev[2].evaluate()).getDouble(),        
	                ((NumberValue) ev[3].evaluate()).getDouble(),        
	                ((NumberValue) ev[4].evaluate()).getDouble(),        
	                ((NumberValue) ev[5].evaluate()).getDouble()
	                );
	    } catch (Exception e) {
			conic.setUndefined();
		}
    }   

    public final String toString() {
        return equation.toString();
    }           
}
