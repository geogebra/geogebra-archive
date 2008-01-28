/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.algebra.parser.Parser;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;

/**
 * Try to expand the given function to a polynomial.
 * 
 * @author Markus Hohenwarter
 */
public class AlgoPolynomialFromFunction extends AlgoElement {

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
    
    protected String getClassName() {
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

//  ON CHANGE: similar code is in AlgoTaylorSeries
    final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        // get numeric string for function
        //String function = f.getFunction().getExpression().getJSCLString(false);        
		String function = f.getFunction().
			getExpression().getCASstring(ExpressionNode.STRING_TYPE_JASYMCA, false);
    	
        // expand expression and get polynomial coefficients
        String [] strCoeffs = kernel.getPolynomialCoeffs(function, "x");
        if (strCoeffs == null) {
        	 g.setDefined(false);
        	 return;
        }
        
        //  build polynomial 
        
        // Michael Borcherds 2008-23-01 BEGIN
        // moved shared code into AlgoPolynomialFromCoordinates.buildPolyFunctionExpression()
        
        int n=strCoeffs.length;
        double coeffs[] = new double[n];
  	    for (int k = strCoeffs.length-1; k >= 0 ; k--) {
  	        coeffs[k] = evaluateToDouble(strCoeffs[k]);  	 	 
			 if (Double.isNaN(coeffs[k]) || Double.isInfinite(coeffs[k])) {
				 g.setUndefined();
				 return;
			 }
		 }
        
   		Function polyFun = AlgoPolynomialFromCoordinates.buildPolyFunctionExpression(kernel,coeffs);

   		if (polyFun==null)
   		{
   		    g.setUndefined();
       	    return;			   			   			
   		}
        
        /*
        
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
		Function polyFun = new Function(poly, fVar);	*/
        // Michael Borcherds 2008-23-01 END
   		
   		
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
