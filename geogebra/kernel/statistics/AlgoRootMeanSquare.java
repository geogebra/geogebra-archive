/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.statistics;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.arithmetic.NumberValue;




/**
 * Returns the harmonic mean for a list of numbers
 */

public class AlgoRootMeanSquare extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoNumeric result; //output	
	private int size;
	private double sum;



	public AlgoRootMeanSquare(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;
		result = new GeoNumeric(cons);

		setInputOutput();
		compute();
		result.setLabel(label);
	}

	public String getClassName() {
		return "AlgoRootMeanSquare";
	}

	protected void setInputOutput(){
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0,result);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	protected final void compute() {

		//==========================
		// validation
		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			result.setUndefined();
			return;
		} 


		//==========================
		// compute result

		sum = 0;

		// load input value array from  geoList
		for (int i=0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				double d = ((NumberValue) geo).getDouble();
				sum += d*d;	
			} else {
				result.setUndefined();
				return;
			}    		    		
		}   

		result.setValue(Math.sqrt(sum/size));
	}


}
