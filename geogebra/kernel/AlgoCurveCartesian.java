/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoCurveCartesian extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NumberValue xcoord, ycoord, from, to;  // input
    private GeoNumeric localVar;     // input
    private GeoCurveCartesian curve;  // output
        
    /** Creates new AlgoJoinPoints */
    public AlgoCurveCartesian(Construction cons, String label, 
			NumberValue xcoord, NumberValue ycoord, 
			GeoNumeric localVar, NumberValue from, NumberValue to)  {
    	super(cons);
    	
    	this.xcoord = xcoord;
    	this.ycoord = ycoord;
    	this.from = from;
    	this.to = to;
    	this.localVar = localVar;
        
    	// we need to create Function objects for the coord NumberValues,
		// so let's get the expressions of xcoord and ycoord and replace
		// the localVar by a functionVar		
		FunctionVariable funVar = new FunctionVariable(kernel);
		funVar.setVarString(localVar.label);
		ExpressionNode xExp = kernel.convertNumberValueToExpressionNode(xcoord);
		ExpressionNode yExp = kernel.convertNumberValueToExpressionNode(ycoord);	
		xExp.replace(localVar, funVar);
		yExp.replace(localVar, funVar);		
		Function funX = new Function(xExp, funVar);
		Function funY = new Function(yExp, funVar);
        
		// create the curve
		curve = new GeoCurveCartesian(cons, funX, funY);
       
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent number
        compute();      
        curve.setLabel(label);
    }   
    
	String getClassName() {
		return "AlgoCurveCartesian";
	}
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[5];
        input[0] = xcoord.toGeoElement();
        input[1] = ycoord.toGeoElement();
    	input[2] = localVar;
    	input[3] = from.toGeoElement();
    	input[4] = to.toGeoElement();    	
        
        output = new GeoElement[1];        
        output[0] = curve;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoCurveCartesian getCurve() { return curve; }        
    
    final void compute() {    
    	// the coord-functions don't have to be updated,
    	// so we only set the interval
    	curve.setInterval(from.getDouble(), to.getDouble());
    }   
    
    final public String toString() {
        return getCommandDescription();
    }
}
