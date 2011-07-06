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

import java.util.Arrays;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;


/**
 * Creates a Normal Quantile Plot. 
 * 
 * Input: list of unsorted raw numeric data 
 * Output: list containing 
 * (1) points forming a normal quantile plot for the raw data and
 * (2) a linear function for the qq line. 
 * 
 * Point ordering: 
 * x-coords = data values
 * y-coords = expected z-scores
 * 
 * 
 * The algorithm follows the description given by: 
 * http://en.wikipedia.org/wiki/Normal_probability_plot
 * http://www.itl.nist.gov/div898/handbook/eda/section3/normprpl.htm
 * 
 * @author G.Sturr
 */

public class AlgoNormalQuantilePlot extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; //input
	private GeoList outputList; //output	
	private int size;
	private double[] zValues;
	private double[] sortedData;

	AlgoNormalQuantilePlot(Construction cons, String label, GeoList inputList) {
		super(cons);
		this.inputList = inputList;

		outputList = new GeoList(cons);

		setInputOutput();
		compute();
		outputList.setLabel(label);
	}

	public String getClassName() {
		return "AlgoNormalQuantilePlot";
	}

	protected void setInputOutput(){
		input = new GeoElement[1];
		input[0] = inputList;

		setOutputLength(1);
		setOutput(0,outputList);
		setDependencies(); // done by AlgoElement
	}

	GeoList getResult() {
		return outputList;
	}

	private void calculateZValues(int n){

		zValues = new double[n];    	
		NormalDistributionImpl normalDist = new NormalDistributionImpl(0, 1);
		double x;

		try {
			x = 1 - Math.pow(0.5, 1.0/n);
			zValues[0] = normalDist.inverseCumulativeProbability(x);

			for(int i = 2; i<n; i++){
				x = (i - 0.3175)/(n + 0.365);
				zValues[i-1] = normalDist.inverseCumulativeProbability(x);
			}

			x = Math.pow(0.5, 1.0/n);
			zValues[n-1] = normalDist.inverseCumulativeProbability(x); 

		} catch (MathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getQQLineText(){

		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < sortedData.length; i++) {
			stats.addValue(sortedData[i]);
		}
		double sd = stats.getStandardDeviation();
		double mean = stats.getMean();
		double min = stats.getMin();
		double max = stats.getMax();

		// qq line: y = (1/sd)x - mean/sd 
		String text = "Function[ 1/" + sd + " x - " + mean/sd + "," + min + "," + max + "]";

		return text;
	}




	protected final void compute() {

		// validate
		size = inputList.size();
		if (!inputList.isDefined() ||  size == 0) {
			outputList.setUndefined();
			return;
		} 

		// convert geoList to sorted array of double
		sortedData = new double[size];
		for (int i=0; i < size; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isNumberValue()) {
				NumberValue num = (NumberValue) geo;
				sortedData[i] = num.getDouble();

			} else {
				outputList.setUndefined();
				return;
			}    		    		
		}   
		Arrays.sort(sortedData);


		// create the z values
		calculateZValues(size);

		
		// prepare output list. Pre-existing geos will be recycled, 
		// but extra geos are removed when outputList is too long
		outputList.setDefined(true);
		for (int i = outputList.size() - 1; i >= size; i--) {
			GeoElement extraGeo = outputList.get(i);
			extraGeo.remove();
			outputList.remove(extraGeo);
			
		}	
		int oldListSize = outputList.size();
		

		// iterate through the sorted data and create the normal quantile points 

		boolean suppressLabelCreation = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		for(int i = 0; i<sortedData.length; i++) {
			if(i<oldListSize)
				((GeoPoint)outputList.get(i)).setCoords(sortedData[i], zValues[i], 1.0);
			else
				outputList.add(new GeoPoint(cons, null, sortedData[i], zValues[i], 1.0));
		}      

		// create qq line and add it to the list
		GeoElement[] geos = kernel.getAlgebraProcessor()
		.processAlgebraCommandNoExceptions(getQQLineText(), false);
		geos[0].setEuclidianVisible(true);
		outputList.add(geos[0]);

		cons.setSuppressLabelCreation(suppressLabelCreation);

	}

}
