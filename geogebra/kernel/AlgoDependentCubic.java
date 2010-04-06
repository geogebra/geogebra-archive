/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
 * @author  Michael, adapted from AlgoDependentConic
 * @version 
 */
public class AlgoDependentCubic extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Equation equation;
    private ExpressionValue [] ev = new ExpressionValue[16];  // input
    private GeoCubic cubic;     // output                 
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentCubic(Construction cons,String label, Equation equ) {  
       	super(cons, false); // don't add to construction list yet
        equation = equ;                
        Polynomial lhs = equ.getNormalForm();
        
        ev[0] = lhs.getCoefficient("xxxyyy");       
        ev[1] = lhs.getCoefficient("xxyyy");       
        ev[2] = lhs.getCoefficient("xyyy");        
        ev[3] = lhs.getCoefficient("yyy");        
        ev[4] = lhs.getCoefficient("xxxyy");       
        ev[5] = lhs.getCoefficient("xxyy");       
        ev[6] = lhs.getCoefficient("xyy");        
        ev[7] = lhs.getCoefficient("yy");        
        ev[8] = lhs.getCoefficient("xxxy");       
        ev[9] = lhs.getCoefficient("xxy");       
        ev[10] = lhs.getCoefficient("xy");        
        ev[11] = lhs.getCoefficient("y");        
        ev[12] = lhs.getCoefficient("xxx");       
        ev[13] = lhs.getCoefficient("xx");       
        ev[14] = lhs.getCoefficient("x");        
        ev[15] = lhs.getConstantCoefficient();        
         
       
        // check coefficients
        for (int i=0; i<16; i++) {
        	// find constant parts of input and evaluate them right now
            if (ev[i].isConstant()) ev[i] = ev[i].evaluate();
                 
            // check that coefficient is a number: this may throw an exception
            ExpressionValue eval = ev[i].evaluate();
            ((NumberValue) eval).getDouble();  
        }  
        
        // if we get here, all is ok: let's add this algorithm to the construction list
        cons.addToConstructionList(this, false);
        
        cubic = new GeoCubic(cons); 
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number        
        compute();      
        cubic.setLabel(label);                
    }   
    
	protected String getClassName() {
		return "AlgoDependentCubic";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = equation.getGeoElementVariables();  
        
        output = new GeoElement[1];        
        output[0] = cubic;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoCubic getCubic() { return cubic; }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {   
    	try {
	        cubic.setCoeffs( 
	                ((NumberValue) ev[0].evaluate()).getDouble(),
	                ((NumberValue) ev[1].evaluate()).getDouble(),
	                ((NumberValue) ev[2].evaluate()).getDouble(),        
	                ((NumberValue) ev[3].evaluate()).getDouble(),        
	                ((NumberValue) ev[4].evaluate()).getDouble(),        
	                ((NumberValue) ev[5].evaluate()).getDouble(),        
	                ((NumberValue) ev[6].evaluate()).getDouble(),        
	                ((NumberValue) ev[7].evaluate()).getDouble(),        
	                ((NumberValue) ev[8].evaluate()).getDouble(),        
	                ((NumberValue) ev[9].evaluate()).getDouble(),        
	                ((NumberValue) ev[10].evaluate()).getDouble(),        
	                ((NumberValue) ev[11].evaluate()).getDouble(),        
	                ((NumberValue) ev[12].evaluate()).getDouble(),        
	                ((NumberValue) ev[13].evaluate()).getDouble(),        
	                ((NumberValue) ev[14].evaluate()).getDouble(),        
	                ((NumberValue) ev[15].evaluate()).getDouble()
	                );
	    } catch (Throwable e) {
			cubic.setUndefined();
		}
    }   

    public final String toString() {
        return equation.toString();
    }           
}
