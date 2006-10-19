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
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;


/**
 * Algorithm for the Sequence[ expression of var, var, from-value, to-value, step ] command.
 * @author  Markus Hohenwarter
 */
public class AlgoSequence extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement expression; // input expression dependent on var
	private GeoNumeric var; // input: local variable
	private NumberValue  var_from, var_to, var_step;
    private GeoList list; // output      
    
    private AlgoElement algoExp; // parent algo of expression 
    private double last_from, last_to, last_step;
    private boolean expIsGeoFunction;
    private Function last_function;


    AlgoSequence(Construction cons, String label, GeoElement expression, GeoNumeric var, 
    		NumberValue var_from, NumberValue var_to, NumberValue var_step) {
        super(cons);
        this.expression = expression;
        this.var = var;
        this.var_from = var_from;
        this.var_to = var_to;
        this.var_step = var_step;
        
    	// parent algorithm of expression;
    	algoExp = expression.getParentAlgorithm();
    	expIsGeoFunction = expression.isGeoFunction();
    	
        list = new GeoList(cons);
        setInputOutput(); // for AlgoElement

        compute();
        list.setLabel(label);
    }

    String getClassName() {
        return "AlgoSequence";
    }

    // for AlgoElement
    void setInputOutput() {
    	int len = var_step == null ? 4 : 5;
        input = new GeoElement[len];
        input[0] = expression;
        input[1] = var;
        input[2] = var_from.toGeoElement();
        input[3] = var_to.toGeoElement();
        if (len == 5)
        	input[4] = var_step.toGeoElement();                      

        output = new GeoElement[1];
        output[0] = list;
        setDependencies(); // done by AlgoElement
    }

    GeoList getList() {
        return list;
    }
    
    final void compute() {    	
    	// create sequence for expression(var) by changing var according to the given range
    	double from = var_from.getDouble();
    	double to = var_to.getDouble();
    	double step = var_step == null ? 1 : var_step.getDouble();
    	
    	// an update may be necessary because another variable in expression
    	// has changed. However, the range (from, to, step) may not have changed:
    	// in this case it is much more efficient not to create all objects
    	// for the list again, but just to set their new values
    	boolean setValuesOnly =
    		(	from == last_from && 
    			to == last_to &&
    			step == last_step );
    	
    	// for GeoFunction: check if function tree has changed
    	if (expIsGeoFunction) {    		
    		setValuesOnly = last_function == ((GeoFunction) expression).getFunction();	
    	}    		    	    	    	
    	
    	if (setValuesOnly)
    		updateListItems(from, to, step);
    	else
    		createNewList(from, to, step);        				        	    	
    }       
    
    private void createNewList(double from, double to, double step) {
    	list.clear();
    	    	    	
    	// check if defined
    	boolean isDefined = ((to - from) * step > Kernel.MIN_PRECISION);
    	list.setDefined(isDefined);
    	if (!isDefined) return;
    	    	
    	double currentVal = from;    
		while ((step > 0 && currentVal <= to) || 
			   (step < 0 && currentVal >= to)) 
		{
			var.setValue(currentVal);
			if (algoExp != null) algoExp.update();

			// create a copy of the expression GeoElement 
			GeoElement copy = expression.copyInternal();     			
			setValue(copy, expression, currentVal);								
			copy.setVisualStyle(list);
			list.add(copy);
			
			currentVal += step;
    	}    	
    	
    	last_from = from;
    	last_to = to;
    	last_step = step;
    	
    	// for GeoFunctions we have to make sure that the function hasn't changed
    	if (expIsGeoFunction) {    	
    		last_function = ((GeoFunction) expression).getFunction();
    	}
    }        
    
    private void updateListItems(double from, double to, double step) {    	       	    	    	
    	double currentVal = from;
    	int i=0;
    	
		while ((step > 0 && currentVal <= to) || 
				   (step < 0 && currentVal >= to)) 
		{
			var.setValue(currentVal);
			if (algoExp != null) algoExp.update();
			GeoElement copy = list.get(i);
			setValue(copy, expression, currentVal);	   			    		
			
			currentVal += step;
			i++;
		}    	      
    }
    
    /**
     * Sets copy to the current value of orig using the current variable value.
     */
    private void setValue(GeoElement copy, GeoElement orig, double varVal) {
		if (expIsGeoFunction) {
	    	// function's point to the local variable var
			// so we have to replace every occurance of var
			// by the current value of var
			GeoFunction f = (GeoFunction) copy;
			MyDouble varValue = new MyDouble(kernel, varVal);
			f.getFunction().getExpression().replace(var, varValue);		
			
		} else {
	    	// all other objects are copies that can be set directly
			copy.set(orig);
		}		
    }
    

    final public String toString() {
        return getCommandDescription();
    }
}
