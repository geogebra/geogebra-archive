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
import geogebra.main.Application;

import java.util.ArrayList;

import org.apache.commons.math.stat.inference.OneWayAnovaImpl;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a one way ANOVA test.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoANOVA extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private GeoList  result;     // output   

	private ArrayList<double[]> categoryData;
	private double p, testStat;
	private OneWayAnovaImpl anovaImpl;


	public AlgoANOVA(Construction cons, String label, GeoList geoList) {
		super(cons);
		this.geoList = geoList;
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	public String getClassName() {
		return "AlgoANOVA";
	}

	protected void setInputOutput(){

		input = new GeoElement[1];
		input[0] = geoList;

		output = new GeoElement[1];
		output[0] = result;
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}


	protected final void compute() {

		int size = geoList.size();
		System.out.println(geoList.toOutputValueString());
		// exit if less than two data lists
		if (size < 2) {
			result.setUndefined();	
			return;		
		}

		// exit if data lists are not defined or have less than two values
		for(int index = 0; index < size; index++){

			if(!geoList.get(index).isDefined() 
					|| !geoList.get(index).isGeoList()
					|| ((GeoList)geoList.get(index)).size() < 2){
				result.setUndefined();
				return;			
			}
		}

		// create an array list of data arrays
		if(categoryData == null){
			categoryData = new ArrayList<double[]>(); 
		}else{
			categoryData.clear();
		}


		// load the data arrays from the input GeoList
		GeoList list;
		for (int index=0; index < size; index++){

			list = (GeoList) geoList.get(index);
			double[] val = new double[list.size()];

			for (int i=0; i < list.size(); i++){
				GeoElement geo = list.get(i);
				if (geo.isNumberValue()) {
					NumberValue num = (NumberValue) geo;
					val[i] = num.getDouble();
				} else {
					result.setUndefined();
					return;
				}    		 
			}
			categoryData.add(val);
		}


		try {

			// get the test statistic and p value
			if(anovaImpl == null)
				anovaImpl = new OneWayAnovaImpl();
			p = anovaImpl.anovaPValue(categoryData);
			testStat = anovaImpl.anovaFValue(categoryData);

			// put these results into the output list
			result.clear();
			result.add(new GeoNumeric(cons, p));
			result.add(new GeoNumeric(cons,testStat));


		} catch (Exception e) {
			result.setUndefined();
			e.printStackTrace();
		}

	}


}
