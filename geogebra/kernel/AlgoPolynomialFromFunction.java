/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

import geogebra.algebra.parser.Parser;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;

/**
 * Try to expand the given function to a polynomial.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunction extends AlgoElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
    private Parser parser;
   

    public AlgoPolynomialFromFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        parser = new Parser(cons.getKernel(), cons);
        
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    String getClassName() {
        return "AlgoPolynomialFromFunction";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getPolynomial() {
        return g;
    }

//  ON CHANGE: similiar code is in AlgoTaylorSeries
    final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        // get numeric string for function
        //String function = f.getFunction().getExpression().getJSCLString(false);        
		String function = f.getFunction().getExpression().getJSCLString(false);
        
        // expand expression and get polynomial coefficients
        String [] strCoeffs = GeoGebraCAS.getPolynomialCoeffs(function);
        if (strCoeffs == null) {
        	 g.setDefined(false);
        	 return;
        }
        
        //  build polynomial 
        ExpressionNode poly = null; // expression for the expanded polynomial		
		FunctionVariable fVar = new FunctionVariable(kernel);	
		double coeff;
  	    for (int k = strCoeffs.length-1; k >= 0 ; k--) {
  	        coeff = evaluateToDouble(strCoeffs[k]);  	 	 
			 if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
					 g.setUndefined();
					 return;
			 }
			 else if (kernel.isZero(coeff)) 
			 	continue; // this part vanished
			 				
			boolean negativeCoeff = coeff < 0; 					
			
			// build the expression x^k
			ExpressionValue powerExp; 
			switch (k) {
			    case 0: 
			    	powerExp = null;
			    	break;
			    	
				case 1: 
					powerExp = fVar; 
					break;
					
				default: powerExp = 				
					 new ExpressionNode(kernel, 
							fVar,
							ExpressionNode.POWER, 
							new MyDouble(kernel, k));						
			}
					
			// build the expression 
			// (coeff) * x^k
			ExpressionValue partExp;
			MyDouble coeffMyDouble = null;
			if (kernel.isEqual(coeff, 1.0)) {
				if (powerExp == null)
					partExp = new MyDouble(kernel, 1.0);
				else
					partExp = powerExp;
			} else {
				coeffMyDouble = new MyDouble(kernel, coeff);
				if (powerExp == null)
					partExp = coeffMyDouble;
				else 
					partExp = new ExpressionNode(kernel, 
									coeffMyDouble, 
									ExpressionNode.MULTIPLY, 
									powerExp);
			}								
	
			 // add part to series
			if (poly == null) {
				poly = new ExpressionNode(kernel, partExp);
			} else {
				if (negativeCoeff) {
					if (coeffMyDouble != null)
						coeffMyDouble.set(-coeff); // change sign
					poly = new ExpressionNode(kernel, 
											poly, 
											ExpressionNode.MINUS, 
											partExp);
				} else {
					poly = new ExpressionNode(kernel, 
											poly, 
											ExpressionNode.PLUS, 
											partExp);
				}					
			}		
  	    }     
  	    
  	    // all coefficients were 0, we've got f(x) = 0
  	    if (poly == null) {
  	    	poly = new ExpressionNode(kernel, new MyDouble(kernel, 0));
  	    }  	       
    	
    	//  polynomial Function
		Function polyFun = new Function(poly, fVar);	
		g.setFunction(polyFun);			
		g.setDefined(true);	
		
		
    }
    
    private double evaluateToDouble(String str) {
		try {
			ValidExpression ve = parser.parse(str);
			ExpressionNode en = (ExpressionNode) ve;
			NumberValue nv = (NumberValue) en.evaluate();
			return nv.getDouble();
		} catch (Exception e) {
			return Double.NaN;
		} catch (Error e) {
			return Double.NaN;
		}
	}


    final public String toString() {
    	return getCommandDescription();
    }

}
