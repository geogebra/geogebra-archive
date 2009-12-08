/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
public class AlgoFactor extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoFactor(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoFactor";
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
                
        
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

	    
	    // old code, put back when Factor[2 x³ - 18 x] works in MathPiper
	    
	    sb.setLength(0);
	    // added Rationalize to cope with eg Factor[9.1 x^7 - 32 x^6 + 48 x^5 - 40 x^4 + 20 x³ - 6 x² + x ]
        sb.append("Factor(Rationalize(");
        sb.append(functionIn);
        sb.append("))");
		String functionOut = kernel.evaluateMathPiper(sb.toString());
		
		//Application.debug("Factorize input:"+functionIn);
		//Application.debug("Factorize output:"+functionOut);
		
		boolean yacasError=false;
		
		if (functionOut == null || functionOut.length()==0) yacasError=true; // Yacas error
		
		else if (functionOut.length()>7)
			if (functionOut.startsWith("Factor(") || // Yacas error
			    functionOut.startsWith("FWatom(") )  // Yacas oddity??
				yacasError=true;
			

		if (yacasError) // Yacas error
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
