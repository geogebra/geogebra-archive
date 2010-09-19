/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;


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
    private boolean expIsFunctionOrCurve,  isEmpty;
    private AlgoElement expressionParentAlgo;
    
    // we need to check that some Object[] reference didn't cause infinite update cycle
    private boolean updateRunning = false;
   
   
    /**
     * Creates a new algorithm to create a sequence of objects that form a list.
     * @param cons
     * @param label label for the list
     * @param expression
     * @param var
     * @param var_from
     * @param var_to
     * @param var_step
     */
    AlgoSequence(Construction cons, String label, GeoElement expression, GeoNumeric var, 
    		NumberValue var_from, NumberValue var_to, NumberValue var_step) {
              
        this(cons, expression, var, var_from, var_to, var_step);
        list.setLabel(label);        
    }

    /**
     * Creates a new algorithm to create a sequence of objects that form a list.
     * @param cons
     * @param expression
     * @param var
     * @param var_from
     * @param var_to
     * @param var_step
     */
    AlgoSequence(Construction cons, GeoElement expression, GeoNumeric var, 
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
    	expIsFunctionOrCurve = expression.isGeoFunction() || expression.isGeoCurveCartesian();    	      
    	 	
//    	Application.debug("expression: " + expression);
//   	Application.debug("  parent algo: " + expression.getParentAlgorithm());
//    //	Application.debug("  parent algo input is var?: " + (expression.getParentAlgorithm().getInput()[0] == var));        
//    	Application.debug("  variable: " + var);
//    	Application.debug("  expIsGeoFunction: " + expIsGeoFunction);
    	
    	list = new GeoList(cons);       
        setInputOutput(); // for AlgoElement
                            
        compute();
    
    }

    public String getClassName() {
        return "AlgoSequence";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	int len = var_step == null ? 4 : 5;
        input = new GeoElement[len];
        input[0] = expression;
        input[1] = var;        
        input[2] = var_from_geo;
        input[3] = var_to_geo;
        if (len == 5)
        	input[4] = var_step_geo;  
          
        setOutputLength(1);
    	setOutput(0,list);	   
        
    	setDependencies(); // done by AlgoElement
    }
    
    /**
     *  Returns contents of input array excluding var 
     *  (var is not input object, but must be in input array
     *  because of GetCommandDescription method).
     *  see ticket #72
     *  2010-05-13 null pointer error fixed
     *  @author Zbynek Konecny
     *  @version 2010-05-13
     */
    GeoElement[] getInputForUpdateSetPropagation() {
    	GeoElement[] realInput = new GeoElement[input.length-1];
    	realInput[0] = expression;
    	realInput[1] = var_from_geo;
    	realInput[2] = var_to_geo;
    	if(input.length==5)	{
    		realInput[3] = var_step_geo;
    	}
    	
    	return realInput;
    }

    /**
     * Returns list of all contained elements.
     * @return list of elements
     */
    GeoList getList() {
        return list;
    }      
    
    protected final void compute() {
    	if(updateRunning) return;
    	updateRunning = true;
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
    	setValuesOnly = setValuesOnly && !expIsFunctionOrCurve;    	
    	
    	// avoid label creation, might happen e.g. in
    	boolean oldSuppressLabels = cons.isSuppressLabelsActive();
    	cons.setSuppressLabelCreation(true);
    	
    	// update list
    	if (setValuesOnly)
    		updateListItems(from, to, step);
    	else
    		createNewList(from, to, step);
    	
    	// revert label creation setting
    	cons.setSuppressLabelCreation(oldSuppressLabels);
    	updateRunning = false;
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
    		
    		
			while ((step > 0 && currentVal <= to + Kernel.MIN_PRECISION) || 
				   (step < 0 && currentVal >= to - Kernel.MIN_PRECISION)) 
			{				
				
				// check we haven't run out of memory
				if (app.freeMemoryIsCritical()) {
					long mem = app.freeMemory();
					list.clearCache();
					kernel.initUndoInfo(); // clear all undo info
					Application.debug("AlgoSequence aborted: free memory reached "+mem);
					return;
				}
				
				// set local var value
				updateLocalVar(currentVal);
				
				
				addElement(i);
				
				currentVal += step;
				if (kernel.isInteger(currentVal)) {
					currentVal = Math.round(currentVal);
				}
				i++;
	    	}
    	}
    	
    	// if the old list was longer than the new one
    	// we need to set some cached elements to undefined
    	for (int k=oldListSize-1; k >= i ; k--) {
    		GeoElement oldElement = list.getCached(k);    		
    		oldElement.setUndefined(); 
    		oldElement.update();
    	}		    
		
		// remember current values
    	last_from = from;
    	last_to = to;
    	last_step = step;
    }   
    
    private void addElement(int i) {
    	// only add new objects
		GeoElement listElement = null;
		int cacheListSize = list.getCacheSize();
		if (i < cacheListSize) {		
			// we reuse existing list element from cache				
			listElement = list.getCached(i);	
			
			if (expIsFunctionOrCurve) {
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
		    						
		// copy current expression value to listElement    
		if (!expIsFunctionOrCurve) {
			listElement.set(expression);
			if(listElement instanceof GeoNumeric){
        		listElement.setDrawAlgorithm(expression.getParentAlgorithm().copy());
				listElement.setEuclidianVisible(true);
			}
		}
		
		// set the value of our element
		listElement.update();
		list.add(listElement);	
    }
    
    private GeoElement createNewListElement() {
    	GeoElement listElement = expression.copyInternal(cons);
    	listElement.setParentAlgorithm(this);
		listElement.setConstructionDefaults();		
		listElement.setUseVisualDefaults(false);
				
		// functions and curves use the local variable var
		// so we have to replace var and all dependent objects of var
		// by their current values
		if (expIsFunctionOrCurve) {
			// GeoFunction
			if (listElement.isGeoFunction()) {
				GeoFunction f = (GeoFunction) listElement;
				f.replaceChildrenByValues(var);
			}
			// GeoCurve
			else if (listElement.isGeoCurveCartesian()) {				
				GeoCurveCartesian curve = (GeoCurveCartesian) listElement;
				curve.replaceChildrenByValues(var);
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
			
			// check we haven't run out of memory
			if (app.freeMemoryIsCritical()) {
				long mem = app.freeMemory();
				list.clearCache();
				kernel.initUndoInfo(); // clear all undo info
				Application.debug("AlgoSequence aborted: free memory reached "+mem);
				return;
			}
			
			// set local var value
			updateLocalVar(currentVal);	   			    		
			
			// copy expression value to listElement    				
			listElement.set(expression);
			listElement.update();
			
			currentVal += step;
			if (kernel.isInteger(currentVal)) {
				currentVal = Math.round(currentVal);
			}
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
    		// update all dependent algorithms of the local variable var    		
    		this.setStopUpdateCascade(true);
    		var.getAlgoUpdateSet().updateAllUntil(expressionParentAlgo);
    		this.setStopUpdateCascade(false);   
    		expressionParentAlgo.update();
		}
    }
    

    final public String toString() {
        return getCommandDescription();
    }
}
