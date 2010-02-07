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
import geogebra.main.Application;
/**
 * Try to expand the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoCoefficients extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoCoefficients(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoCoefficients";
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
                
        
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

	    sb.setLength(0);
        sb.append("Degree(");
        sb.append(functionIn);
        sb.append(")");
		String functionOut = kernel.evaluateMathPiper(sb.toString());
		int order = Integer.parseInt(functionOut);	
		
		//Application.debug("order = "+order);

	    sb.setLength(0);
	    sb.append("Coef(");
        sb.append(functionIn);
        sb.append(",x,0 .. ");
        sb.append(order);
        sb.append(")");
		functionOut = kernel.evaluateMathPiper(sb.toString());
		
		//Application.debug("Coefficients input:"+sb.toString());
		//Application.debug("Coefficients output:"+functionOut);
		
		boolean yacasError=false;
		
		if (functionOut == null || functionOut.length()==0) yacasError=true; // Yacas error
		
		else if (functionOut.length()>6)
			if (functionOut.startsWith("Coef(") || // Yacas error
			    functionOut.startsWith("FWatom(") )  // Yacas oddity??
				yacasError=true;
			

		if (yacasError) // Yacas error
		{
			g.setUndefined(); 
		}
		else
		{
			g.set(kernel.getAlgebraProcessor().evaluateToList(functionOut));					
		}
		
		g.setDefined(true);	
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
