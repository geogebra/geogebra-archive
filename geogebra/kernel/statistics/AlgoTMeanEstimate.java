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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;

/**
 * Calculates a one sample t-confidence interval estimate of a mean.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTMeanEstimate extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private GeoNumeric geoLevel, geoMean, geoSD, geoN; //input
	private GeoList  result;     // output   

	private double[] val;
	private double level, mean, sd, n, width;
	private SummaryStatistics stats;
	private TDistributionImpl tDist;
	

	public AlgoTMeanEstimate(Construction cons, String label, GeoList geoList, GeoNumeric geoLevel) {
		super(cons);
		this.geoList = geoList;
		this.geoLevel = geoLevel;
		this.geoMean = null;
		this.geoSD = null;
		this.geoN = null;
		
		level = geoLevel.getDouble();
		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	public AlgoTMeanEstimate(Construction cons, String label, GeoNumeric geoMean, GeoNumeric geoSD, GeoNumeric geoN, GeoNumeric geoLevel) {
		super(cons);
		this.geoList = null;
		this.geoLevel = geoLevel;
		this.geoMean = geoMean;
		this.geoSD = geoSD;
		this.geoN = geoN;
		
		level = geoLevel.getDouble();
		mean = geoMean.getDouble();
		sd = geoSD.getDouble();
		n = geoN.getDouble();

		result = new GeoList(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	public String getClassName() {
		return "AlgoTEstimate";
	}

	protected void setInputOutput(){

		if(geoList != null){
			input = new GeoElement[2];
			input[0] = geoList;
			input[1] = geoLevel;

		}else{
			input = new GeoElement[4];
			input[0] = geoMean;
			input[1] = geoSD;
			input[2] = geoN;
			input[3] = geoLevel;	
		}

		output = new GeoElement[1];
		output[0] = result;
		setDependencies(); // done by AlgoElement
	}

	public GeoList getResult() {
		return result;
	}


	private double getConfidenceIntervalWidth(double sd, double n, double confLevel) throws MathException {
		tDist = new TDistributionImpl(n - 1);
		double a = tDist.inverseCumulativeProbability(1.0 - confLevel/2);
		return a * sd / Math.sqrt(n);
	}



	protected final void compute() {

		try 
		{

			// get statistics from sample data input
			if(input.length == 2){

				int size= geoList.size();
				if(!geoList.isDefined() || size < 2){
					result.setUndefined();	
					return;			
				}

				val = new double[size];
				for (int i=0; i < size; i++) {
					GeoElement geo = geoList.get(i);
					if (geo.isNumberValue()) {
						NumberValue num = (NumberValue) geo;
						val[i] = num.getDouble();

					} else {
						result.setUndefined();
						return;
					}    		    		
				}   

				stats = new SummaryStatistics();
				for (int i = 0; i < val.length; i++) {
					stats.addValue(val[i]);
				}

				n = stats.getN();
				sd = stats.getStandardDeviation();
				mean = stats.getMean();
			}


			// validate statistics
			if(level < 0 || level > 1 || sd < 0 || n < 1){
				result.setUndefined();
				return;
			}



			// get interval estimate 
			width = getConfidenceIntervalWidth(sd, n, level);
			result.clear();
			boolean oldSuppress = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			result.add(new GeoNumeric(cons, mean - width));
			result.add(new GeoNumeric(cons, mean + width));
			cons.setSuppressLabelCreation(oldSuppress);
			


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

}