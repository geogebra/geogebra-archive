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
    private GeoList list; // output
    
    private String [] labels;
    private AlgoElement algoExp; // parent algo of expression 
    private double last_from, last_to, last_step;    
    private boolean expIsGeoFunction, isEmpty;
    private Function last_function;
    private ArrayList copyGeos = new ArrayList(50); // reuse copied geos, so remember them in a list

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
    AlgoSequence(Construction cons, String [] labels, GeoElement expression, GeoNumeric var, 
    		NumberValue var_from, NumberValue var_to, NumberValue var_step) {
        super(cons);
                
        this.labels = labels;
        this.expression = expression;
        this.var = var;
        this.var_from = var_from;
        this.var_to = var_to;
        this.var_step = var_step;      
        
    	// parent algorithm of expression;
    	algoExp = expression.getParentAlgorithm();
    	expIsGeoFunction = expression.isGeoFunction();    	
    	
    	// TODO: think about list type
        //list = new GeoList(cons, expression.getClass());
    	list = new GeoList(cons);
        
        setInputOutput(); // for AlgoElement
        
        // we set the list's label first        
        list.setLabel((labels != null) ? labels[0] : null);
        compute();                
        list.update();
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
    	    	    	
    	// check if defined    	
    	int listSize = list.size();
    	int copyGeosSize = copyGeos.size();
    	int i=0;
    	
    	if (!isEmpty) {    		
    		// create the sequence    		    
    		double currentVal = from;   
	    	
			while ((step > 0 && currentVal <= to) || 
				   (step < 0 && currentVal >= to)) 
			{
				var.setValue(currentVal);
				if (algoExp != null) algoExp.update();
	
				// we need a copy of the expression GeoElement
				GeoElement copy;
				if (i < copyGeosSize) {
					// take existing object
					copy = (GeoElement) copyGeos.get(i);
				} else {
					// create new object and add it to end of list
					copy = expression.copyInternal();	
					// TODO: add description like exp for i=20
					
					// visual properties
					copy.setVisualStyle(list);	
					copy.setLabelVisible(false);
					
					// remember this copy
					copyGeos.add(copy);
				}
				
				// now we have to make sure that this copy
				// is also part of our list
				if (i >= listSize) {
					/*
					// label object: maybe we got loaded labels
					if (labels != null && labels.length > i+1)
						copy.setLabel(labels[i+1]);	
					else 
						// use list's label to get an indexed label
						copy.setLabel(list.label);		
					*/
					
					// add to list	
					list.add(copy);
				}
				
				// set the value of our element
				setValue(copy, expression, currentVal);		
				
				currentVal += step;
				i++;
	    	}
    	}
		
		// if we did not use all exising copy geos 
		// we remove all unneeded objects starting
		// from the end of list until we find any parent object 
		boolean foundParent = false;
		for (int j=listSize-1; j >= i; j--) {			
			GeoElement geo = (GeoElement) list.get(j);
			if (!foundParent && geo.hasEmptyAlgoUpdateSet()) {
				// geo is not used in any algorithms: remove it
				geo.doRemove();
				list.remove(j);
			} 
			else {				
				// geo has children, so we need to keep it
				foundParent = true;
				geo.setUndefined();
				geo.update();
			}									
		}
		
		// remember current values
    	last_from = from;
    	last_to = to;
    	last_step = step;
    	
    	// for GeoFunctions we have to make sure that the function hasn't changed
    	if (expIsGeoFunction) {    	
    		last_function = ((GeoFunction) expression).getFunction();
    	}
    	
    	// update output array
    	updateOutputArray();
    }        
    
    private void updateListItems(double from, double to, double step) {     	    
    	if (isEmpty) return;
    	    	
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
