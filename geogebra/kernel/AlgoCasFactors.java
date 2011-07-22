/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoCasFactors extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoCasFactors(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoFactors";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    

	    sb.setLength(0);
	    // added Rationalize to cope with eg Factors[9.1 x^7 - 32 x^6 + 48 x^5 - 40 x^4 + 20 x^3 - 6 x^2 + x ]
//		String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
//	    sb.append("Factors((");
//        sb.append(functionIn);
//        sb.append("))");
//		String functionOut = kernel.evaluateMathPiper(sb.toString());
		
	    try {
		    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MPREDUCE, true);
		    sb.append("factorize(");
		    sb.append(functionIn);
		    sb.append(")");
			String functionOut = kernel.evaluateMPReduce(sb.toString());	    
			if (functionOut == null || functionOut.length()==0) {
				g.setUndefined(); 
			}
			else {
				// read result back into list
				g.set(kernel.getAlgebraProcessor().evaluateToList(functionOut));		
				//g.setDefined(true);
			}
	    } catch (Throwable th) {
	    	g.setUndefined();
	    }
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
