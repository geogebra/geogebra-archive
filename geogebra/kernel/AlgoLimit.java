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
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
/**
 * Find a limit
 * 
 * @author Michael Borcherds
 */
public class AlgoLimit extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f;
	private NumberValue num; // input
    private GeoNumeric outNum; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoLimit(Construction cons, String label, GeoFunction f, NumberValue num) {
    	super(cons);
        this.f = f;            	
        this.num = num;
    	
        outNum = new GeoNumeric(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        outNum.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoLimit";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = f;
        input[1] = num.toGeoElement();

        output = new GeoElement[1];
        output[0] = outNum;
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getResult() {
        return outNum;
    }

    protected final void compute() {       
        if (!f.isDefined() || !input[1].isDefined()) {
        	outNum.setUndefined();
        	return;
        }    
                
        
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

	    sb.setLength(0);
        sb.append("Limit(x,");
        sb.append(num.getDouble()+"");
        sb.append(")");
        sb.append(functionIn);
        
 		String functionOut = kernel.evaluateMathPiper(sb.toString());
 		
		Application.debug("Limit input:"+sb.toString());
		Application.debug("Limit output:"+functionOut);
		
		boolean yacasError=false;
		
		if (functionOut == null || functionOut.length()==0) yacasError=true; // Yacas error
		
		else if (functionOut.length()>7)
			if (functionOut.startsWith("Limit(") || // Yacas error
			    functionOut.startsWith("FWatom(") )  // Yacas oddity??
				yacasError=true;
			

		if (yacasError) // Yacas error
		{
			outNum.setUndefined(); 
		}
		else
		{
			//outNum.set(kernel.getAlgebraProcessor().evaluateToNumeric(functionOut).toGeoElement());		
			//if (functionOut.equals("Infinity")) outNum.setValue(Double.POSITIVE_INFINITY);
			//else if (functionOut.equals("-Infinity")) outNum.setValue(Double.NEGATIVE_INFINITY);
			//else outNum.setValue(Integer.parseInt(functionOut));
			
			GeoGebraCAS cas = (GeoGebraCAS)(kernel.getGeoGebraCAS());
			try {
				ValidExpression ve = cas.parseGeoGebraCASInput(functionOut);
				
				ExpressionValue ev = ve.evaluate();
				
				if (ev.isNumberValue())
					outNum.setValue(((NumberValue)ev).getDouble());
				else if (ev.isGeoElement())
					outNum.set((GeoNumeric)ev);
				else {
					Application.debug("unhandled ExpressionValue type: "+ev.getClass());
					outNum.setValue(Double.NaN);
				}

			} catch (Throwable e) {
				e.printStackTrace();
				outNum.setValue(Double.NaN);
			}
		}
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
