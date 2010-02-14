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
	protected GeoFunction f;
	protected NumberValue num; // input
    protected GeoNumeric outNum; // output       
    
    protected StringBuilder sb = new StringBuilder();
   
    public AlgoLimit(Construction cons, String label, GeoFunction f, NumberValue num) {
    	super(cons);
        this.f = f;            	
        this.num = num;
    	
        init(label);
    }
    
    private void init(String label) {
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

    // over-ridden in LimitAbove/Below
    protected void compute() {       
        if (!f.isDefined() || !input[1].isDefined()) {
        	outNum.setUndefined();
        	return;
        }    
                
        outNum.setValue(f.getLimit(num.getDouble(), 0));
		
    }
    
    final public String toString() {
    	return getCommandDescription();
    }
    

}
