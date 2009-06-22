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
import geogebra.main.Application;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoFactor extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output        
    
    private StringBuffer sb = new StringBuffer();
   
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

	    
	    // new code
	    // workaround for MathPiper bug
	    // Factor[2 x³ - 18 x]
	    String factors[] = factors(functionIn).split(",");
	    
	    for (int i = 0 ; i < factors.length / 2 ; i++) {
	    	//Application.debug(factors[2 * i]+"     "+factors[2 * i + 1]);
	    	
	    	// strip leading brackets (two on first)
	    	factors[2 * i] = factors[2 * i].substring(1);
	    	if (i == 0) factors[2 * i] = factors[2 * i].substring(1);
	    	
	    	// try to factor each term (workaround for MathPiper bug eg Factor[2 x³ - 18 x]
	    	// don't want to call Factor[1/2] - MathPiper bug
	    	// eg Factor[x/2+1/2]
	    	if (factors[2 * i].indexOf("x") > -1)
	    		factors[2 * i] = factor(factors[2 * i]);
	    	
	    	// error
	    	if (factors[2 * i].equals("")) {
	    		g.set(f);
	    		g.setDefined(true);
	    		return;
	    	}
	    	
	    	factors[2 * i + 1] = factors[2 * i + 1].substring(0,factors[2 * i + 1].length() - 1);
	    	if (i == factors.length / 2 - 1) factors[2 * i + 1] = factors[2 * i + 1] = factors[2 * i + 1].substring(0,factors[2 * i + 1].length() - 1);
	    	
	    	//Application.debug(factors[2 * i]+"     "+factors[2 * i + 1]);
	    
	    }
	    
    	sb.setLength(0);
	    for (int i = 0 ; i < factors.length / 2 ; i++) {
	    	

	    	if (!factors[2 * i].equals("1")) {

	    		// if not first one, need a "*"
		    	if (sb.length() > 0)
		    		sb.append("*");

	    		
	    		sb.append("(");
		    	sb.append(factors[2 * i]);
		    	
		    	// don't need index 1
		    	if (!factors[2 * i + 1].equals("1")) {
			    	sb.append(")^");
			    	sb.append(factors[2 * i + 1]); // index
		    	} else {
			    	sb.append(")");		    		
		    	}
		    	
	    	}
	    	
	    	
	    }
	    
	    if (sb.length() == 0)
	    	g.set(f); // set to input ie leave unchanged
	    else
	    	g.set(kernel.getAlgebraProcessor().evaluateToFunction(sb.toString()));					
	
	
	    g.setDefined(true);	
	    
	    
	    /* old code, put back when Factor[2 x³ - 18 x] works in MathPiper
	    
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
		
		g.setDefined(true);	*/
		
    }
    
    private String factor(String exp) {
	    sb.setLength(0);
	    // added Rationalize to cope with eg Factor[9.1 x^7 - 32 x^6 + 48 x^5 - 40 x^4 + 20 x³ - 6 x² + x ]
	    // Simplify to make x + x^2 + 1 -> x^2 + x + 1
        sb.append("Simplify(Factor(");
        sb.append(exp);
        sb.append("))");
		String ret = kernel.evaluateMathPiper(sb.toString());
		
		if (ret.indexOf("Factor(") > -1 || ret.indexOf("FWatom(") > -1 || ret.indexOf("Abs(") > -1) {
			Application.debug("MathPiper error input: Simplify(Factor("+exp+"))   \noutput:"+ret);
			ret = "";
		}
		
		return ret;
   	
    }
    
    private String factors(String exp) {
	    sb.setLength(0);
	    // added Rationalize to cope with eg Factor[9.1 x^7 - 32 x^6 + 48 x^5 - 40 x^4 + 20 x³ - 6 x² + x ]
        sb.append("Factors(Rationalize(");
        sb.append(exp);
        sb.append("))");
		String ret = kernel.evaluateMathPiper(sb.toString());
		
		//if (ret.startsWith("Factor(") || ret.startsWith("FWatom("))
		//	ret = "";
		
		return ret;
   	
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
