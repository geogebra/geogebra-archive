/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Try to simplify the given function 
 * 
 * @author Michael Borcherds
 */
public class AlgoSimplify extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoFunction g; // output     
    
    protected StringBuilder sb = new StringBuilder();
   
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

    
	    String functionIn = f.getFormulaString(kernel.getCASPrintForm(), true);

		String functionOut = null;
		String CASString = getCASString(functionIn);
		try {
			functionOut = ((GeoGebraCAS)(kernel.getGeoGebraCAS())).processCASInput(CASString, true);
		} catch (Throwable e) {
			Application.debug(getClassName()+" error processing: "+CASString);
		}
		
		//Application.debug(getClassName()+" input:"+functionIn);
		//Application.debug(getClassName()+" output:"+functionOut);

		if (functionOut == null || functionOut.length() == 0) // CAS error
		{
			g.set(f); // set to input ie leave unchanged
		}
		else
		{
			try {
				g.set(kernel.getAlgebraProcessor().evaluateToFunction(functionOut));
			} catch (Exception e) {
				g.set(f); // set to input ie leave unchanged
				Application.debug(getClassName()+" error processing: "+functionOut);
			}
		}
		
		g.setDefined(true);	
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }
    
    // over-ridden by AlgoPartialFractions
    protected String getCASString(String functionIn) {
	    sb.setLength(0);
        sb.append("SimplifyFull(");
        sb.append(functionIn);
        sb.append(")");        
        return sb.toString();
    }


}
