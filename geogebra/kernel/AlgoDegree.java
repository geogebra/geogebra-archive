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
public class AlgoDegree extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoNumeric num; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoDegree(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        num = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        num.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoDegree";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return num;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	num.setUndefined();
        	return;
        }    
                
	    String functionIn = f.getFormulaString(kernel.getCurrentCAS(), true);

	    sb.setLength(0);
        sb.append("Degree(");
        sb.append(functionIn);
        sb.append(")");
		String functionOut;
		try {
			functionOut = kernel.evaluateGeoGebraCAS(sb.toString());
			num.setValue(Integer.parseInt(functionOut));	
		} catch (Throwable e) {
			e.printStackTrace();
			num.setUndefined();
		}
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
