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

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.ArrayList;


/**
 * Algorithm for the Sequence[ expression of var, var, from-value, to-value, step ] command.
 * @author  Markus Hohenwarter
 */
public class AlgoSequence extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoElement expression; // input expression dependent on var
	private GeoNumeric var; // input: local variable
	private NumberValue  var_from, var_to, var_step;
	private GeoElement var_from_geo, var_to_geo, var_step_geo;
    private GeoList list; // output
        
    private double last_from = Double.MIN_VALUE, last_to = Double.MIN_VALUE, last_step = Double.MIN_VALUE;    
    private boolean expIsGeoFunction, funExpUnchanged, isEmpty;
    private ExpressionNode last_function_Expression;
    private AlgoElement expressionParentAlgo;
   
   
    /**
     * Creates a new algorithm to create a sequence of objects that form a list.
     * @param cons
     * @param labels: labels[0] for the list and the rest for the list items
     * @param expression
     * @param var
     * @param var_from
     * @param var_to
     * @param var_step
     */
    AlgoSequence(Construction cons, String label, GeoElement expression, GeoNumeric var, 
    		NumberValue var_from, NumberValue var_to, NumberValue var_step) {
        super(cons);
                
        this.expression = expression;
        this.var = var;
        this.var_from = var_from;
        var_from_geo = var_from.toGeoElement();
        this.var_to = var_to;
        var_to_geo = var_to.toGeoElement(); 
        this.var_step = var_step;          
        if (var_step != null)
        	var_step_geo = var_step.toGeoElement();
    	               
//    	System.out.println("expression: " + expression);
//    	System.out.println("  parent algo: " + expression.getParentAlgorithm());
//    //	System.out.println("  parent algo input is var?: " + (expression.getParentAlgorithm().getInput()[0] == var));        
//    	System.out.println("  variable: " + var);
    		
    	expressionParentAlgo = expression.getParentAlgorithm();
    	expIsGeoFunction = expression.isGeoFunction();    	      
    	funExpUnchanged = true;
    	
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
        input[2] = var_from_geo;
        input[3] = var_to_geo;
        if (len == 5)
        	input[4] = var_step_geo;  
          
        output = new GeoElement[1];
    	output[0] = list;
           
        setDependencies(); // done by AlgoElement          
    }
    
    /**
     * All list objects are output of this algorithm.
     * Whenever the number of these objects changes,
     * we have to call this method.    
     */    		
    private void updateOutputArray() {
    	int size = list.size();
    	output = new GeoElement[size+1];
    	output[0] = list;
    	
    	for (int i=0; i < size; i++) {
    		GeoElement geo = list.get(i);
    		geo.setParentAlgorithm(this);
    		output[i+1] = geo;    		
    	}    	
    }

    GeoList getList() {
        return list;
    }      
    
    final void compute() {    	
    	for (int i=1; i < input.length; i++) {
    		if (!input[i].isDefined()) {
       			list.setUndefined();
    			return;
    		}
    	}    	
    	list.setDefined(true);
    	
    	// create sequence for expression(var) by changing var according to the given range
    	double from = var_from.getDouble();
    	double to = var_to.getDouble();
    	double step = var_step == null ? 1 : var_step.getDouble();
    	
    	isEmpty = !((to - from) * step > -Kernel.MIN_PRECISION);    	
    	
    	// an update may be necessary because another variable in expression
    	// has changed. However, the range (from, to, step) may not have changed:
    	// in this case it is much more efficient not to create all objects
    	// for the list again, but just to set their new values
    	boolean setValuesOnly = 
    		(	from == last_from && 
    			to == last_to &&
    			step == last_step );
    	    	
    	if (expIsGeoFunction) {    	
    		// for GeoFunction: check if function expression has changed
    		funExpUnchanged = (last_function_Expression == ((GeoFunction) expression).getFunctionExpression());    		
    		setValuesOnly = setValuesOnly && funExpUnchanged;	    		
    	}    		    	    	    	
    	
    	if (setValuesOnly)
    		updateListItems(from, to, step);
    	else
    		createNewList(from, to, step);        	
    }         
    
    private void createNewList(double from, double to, double step) {    	    	    	
    	// clear list if defined  
    	int i=0;
    	int oldListSize = list.size();
    	
    	if (!isEmpty) {    		
    		// create the sequence    		    
    		double currentVal = from;   
	    	
			while ((step > 0 && currentVal <= to) || 
				   (step < 0 && currentVal >= to)) 
			{
				// only add new objects
				GeoElement listElement;								
				if (i < oldListSize) {		
					// we reuse existing list element					
					listElement = list.get(i);	
					
					if (!funExpUnchanged) {
						// if function expression changed, we need a new element
						listElement.setParentAlgorithm(null);
			    		listElement.doRemove(); 
	
			    		// replace old list element by a new one
						listElement = createNewListElement();
						list.set(i, listElement); 
					}					
				} else {
					// create new list element and add it to end of list
					listElement = createNewListElement();					
					// add this element to end of list
					list.add(listElement);
				}
				
				// set the value of our element
				setValue(listElement, currentVal);		
				
				currentVal += step;
				i++;
	    	}
    	}
    	
    	// if the old list was longer than the new one
    	// we need to remove the last elements
    	for (int k=oldListSize-1; k >= i ; k--) {
    		GeoElement listElement = list.get(k);
    		list.remove(k); // remove from list
    		listElement.setParentAlgorithm(null);
    		listElement.doRemove();    		
    	}		    
		
		// remember current values
    	last_from = from;
    	last_to = to;
    	last_step = step;
    	
    	// for GeoFunctions we have to make sure that the function hasn't changed
    	if (expIsGeoFunction) {    	
    		last_function_Expression = ((GeoFunction) expression).getFunctionExpression();
    	}
    	
    	// update output array
    	updateOutputArray();
    }        
    
    private GeoElement createNewListElement() {
    	GeoElement listElement = expression.copyInternal(cons);
		listElement.setParentAlgorithm(this);
		listElement.setConstructionDefaults();
		listElement.setUseVisualDefaults(false);
		
		if (expIsGeoFunction) {
			// functions point to the local variable var
			// so we have to replace every occurance of var
			// by a special local variable object
			GeoFunction f = (GeoFunction) listElement;
			ExpressionNode funExp = f.getFunctionExpression();				
			if (funExp != null) {
				MyDouble localVar = new MyDouble(kernel, var.getValue());	
				funExp.replace(var, localVar);	
				funExp.setLocalVar(localVar);
			}
		}		
		
		return listElement;
    }
    
    private void updateListItems(double from, double to, double step) {     	    
    	if (isEmpty) return;
    	    	
    	double currentVal = from;
    	int i=0;
    	
		while ((step > 0 && currentVal <= to) || 
			   (step < 0 && currentVal >= to)) 
		{			
			GeoElement listElement = list.get(i);
			setValue(listElement, currentVal);	   			    		

			currentVal += step;
			i++;
		}    	      
    }       
    
    /**
     * Sets copy to the current value of orig using the current variable value.
     */
    private void setValue(GeoElement listElement, double varVal) {  	
    	if (expIsGeoFunction) {
    		// set local variable of function to current var value
    		GeoFunction f = (GeoFunction) listElement;
    		ExpressionNode funExp = f.getFunctionExpression();
    		if (funExp != null) {
    			funExp.getLocalVar().set(varVal);	
    		}    				
		} else {
	    	// set local variable to given value
	    	var.setValue(varVal);
	    		    	   
	    	// update var's algorithms until we reach expression 
	    	if (expressionParentAlgo != null) {
	    		var.getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
			}
	    	
	    	// copy expression value to listElement
			listElement.set(expression);
		}		
    }   
    

    final public String toString() {
        return getCommandDescription();
    }
}
