/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

import java.util.Iterator;
import java.util.TreeSet;


/**
 * Creates a residual plot. 
 * 
 * Input: list of points (x,y)
 * Input: regression function 
 * Output: list of residual points (x, y - yPredicted)
 *  
 * @author G.Sturr
 */

public class AlgoResidualPlot extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoFunctionable function;
	private GeoList outputList; //output	
	private int size;

	AlgoResidualPlot(Construction cons, String label, GeoList inputList, GeoFunctionable function2) {
		super(cons);
		this.inputList = inputList;
		this.function = function2;       
		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	public String getClassName() {
		return "AlgoResidualPlot";
	}

	protected void setInputOutput(){
		input = new GeoElement[2];
		input[0] = inputList;
		input[1] = function.toGeoElement();

		output = new GeoElement[1];
		output[0] = outputList;
		setDependencies(); // done by AlgoElement
	}

	GeoList getResult() {
		return outputList;
	}

	protected final void compute() {

		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			outputList.setUndefined();
			return;
		} 

		outputList.setDefined(true);
		outputList.clear();
		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		
		GeoFunction funGeo = function.getGeoFunction();
    	
		double x,y, r;

		for (int i = 0 ; i < size ; i++) {
			GeoElement p = inputList.get(i);
			if (p.isGeoPoint()) {
				x = ((GeoPoint)p).getInhomX();
				y = ((GeoPoint)p).getInhomY();
				r = y - funGeo.evaluate(x); 
				outputList.add(new GeoPoint(cons, null, x, r, 1.0));
			} else {
				outputList.setUndefined();
				return;
			}
		}	
		cons.setSuppressLabelCreation(suppressLabelCreation);
	}  

}
