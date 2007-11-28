/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
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
	private GeoElement var_from_geo, var_to_geo, var_step_geo;
    private GeoList list; // output
        
    private double last_from = Double.MIN_VALUE, last_to = Double.MIN_VALUE, last_step = Double.MIN_VALUE;    
    private boolean expIsGeoFunction, isEmpty;
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
        	
    	expressionParentAlgo = expression.getParentAlgorithm();
    	expIsGeoFunction = expression.isGeoFunction();    	      
    	       
//    	System.out.println("expression: " + expression);
//    	System.out.println("  parent algo: " + expression.getParentAlgorithm());
//    //	System.out.println("  parent algo input is var?: " + (expression.getParentAlgorithm().getInput()[0] == var));        
//    	System.out.println("  variable: " + var);
//    	System.out.println("  expIsGeoFunction: " + expIsGeoFunction);
    	
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
    	    	
    	// setValues does not work for functions
    	setValuesOnly = setValuesOnly && !expIsGeoFunction;    	
    	
    	if (setValuesOnly)
    		updateListItems(from, to, step);
    	else
    		createNewList(from, to, step);        	
    }         
    
    private void createNewList(double from, double to, double step) {     
    	// clear list if defined  
    	int i=0;
    	int oldListSize = list.size();
    	list.clear();
    	
    	if (!isEmpty) {    		
    		//  needed capacity
        	int n = (int) Math.ceil((to - from)/step) + 1;
        	list.ensureCapacity(n);
    		
    		// create the sequence    		    
    		double currentVal = from;   
    		int cacheListSize = list.getCacheSize();
    		
			while ((step > 0 && currentVal <= to + Kernel.MIN_PRECISION) || 
				   (step < 0 && currentVal >= to - Kernel.MIN_PRECISION)) 
			{				
				// set local var value
				updateLocalVar(currentVal);	  
				
				// only add new objects
				GeoElement listElement = null;
				
				if (i < cacheListSize) {		
					// we reuse existing list element from cache				
					listElement = list.getCached(i);	
					
					if (expIsGeoFunction) {
						// for functions we always need a new element
						listElement.setParentAlgorithm(null);
			    		listElement.doRemove(); 
	
			    		// replace old list element by a new one
						listElement = createNewListElement();						
					}			
				} else {
					// create new list element
					listElement = createNewListElement();					
				}
				
				// set the value of our element
				list.add(listElement);								
 			    						
				// copy current expression value to listElement    
				if (!expIsGeoFunction)
					listElement.set(expression);	
				
				currentVal += step;
				i++;
	    	}
    	}
    	
    	// if the old list was longer than the new one
    	// we need to set some cached elements to undefined
    	for (int k=oldListSize-1; k >= i ; k--) {
    		GeoElement oldElement = list.getCached(k);
    		oldElement.setUndefined();    				
    	}		    
		
		// remember current values
    	last_from = from;
    	last_to = to;
    	last_step = step;
    }        
    
    private GeoElement createNewListElement() {
    	GeoElement listElement = expression.copyInternal(cons);
		listElement.setParentAlgorithm(this);
		listElement.setConstructionDefaults();
		listElement.setUseVisualDefaults(false);
		
		if (expIsGeoFunction) {
			// functions point to the local variable var
			// so we have to replace var and all dependent objects of var
			// by their current values
			GeoFunction f = (GeoFunction) listElement;
			ExpressionNode funExp = f.getFunctionExpression();				
			if (funExp != null) {
				funExp.replaceChildrenByValues(var);	
			}
		}		
		
		return listElement;
    }
    
    private void updateListItems(double from, double to, double step) {     	    
    	if (isEmpty) return;
    	    	
    	double currentVal = from;
    	int i=0;
    	
		while ((step > 0 && currentVal <= to + Kernel.MIN_PRECISION) || 
			   (step < 0 && currentVal >= to - Kernel.MIN_PRECISION)) 
		{			
			GeoElement listElement = list.get(i);
			
			// set local var value
			updateLocalVar(currentVal);	   			    		
			
			// copy expression value to listElement    	
			listElement.set(expression);
			
			currentVal += step;
			i++;
		}    	      
    }       
    
    /**
     * Sets value of the local loop variable of the sequence
     * and updates all it's dependencies until we reach the sequence algo.
     */
    private void updateLocalVar(double varVal) {
    	// set local variable to given value
    	var.setValue(varVal);
    		    	   
    	// update var's algorithms until we reach expression 
    	if (expressionParentAlgo != null) {
    		this.setStopUpdateCascade(true);
    		var.getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
    		this.setStopUpdateCascade(false);
		}
    }
    

    final public String toString() {
        return getCommandDescription();
    }
}
