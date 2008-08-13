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
public class AlgoFactor extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output         
   
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
                
        
        // Yacas version
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_YACAS);

        /*
		String functionIn = f.getFunction().
		getExpression().getCASstring(ExpressionNode.STRING_TYPE_YACAS, false);*/

		String functionOut = kernel.evaluateYACASRaw("Factor("+functionIn+")");
		
		//Application.debug("Factorize input:"+functionIn);
		//Application.debug("Factorize output:"+functionOut);
		
		boolean yacasError=false;
		
		if (functionOut.length()==0) yacasError=true; // Yacas error
		
		if (functionOut.length()>7)
			if (functionOut.substring(0,7).equals("Factor(")) // Yacas error
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
