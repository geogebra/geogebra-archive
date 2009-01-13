/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoSimplify extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output     
    
    private StringBuffer sb = new StringBuffer();
   
    public AlgoSimplify(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoSimplify";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    

        // MathPiper version       
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

        /*
		String functionIn = f.getFunction().
		getExpression().getCASstring(ExpressionNode.STRING_TYPE_MathPiper, false);*/
		//Application.debug(functionIn);

	    
	    sb.setLength(0);
        sb.append("Simplify(");
        sb.append(functionIn);
        sb.append(")");
		String functionOut = kernel.evaluateMathPiperRaw(sb.toString());
		
		//Application.debug("Factorize input:"+functionIn);
		//Application.debug("Factorize output:"+functionOut);
		
		boolean MathPiperError=false;
		
		if (functionOut.length()==0) MathPiperError=true; // MathPiper error
		
		if (functionOut.length()>7)
			if (functionOut.startsWith("Simplify(") || // MathPiper error
				functionOut.startsWith("Undefined") || // MathPiper error/bug eg Simplify(0.00000000000000001)
				functionOut.startsWith("FWatom(") )  // MathPiper oddity??
				MathPiperError=true;
			

		if (MathPiperError) // MathPiper error
		{
			g.set(f); // set to input ie leave unchanged
		}
		else
		{
			g.set(kernel.getAlgebraProcessor().evaluateToFunction(functionOut));					
		}
		
		g.setDefined(true);	
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
