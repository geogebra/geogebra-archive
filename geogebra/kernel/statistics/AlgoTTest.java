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
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.NumberValue;

import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a one sample t-test of a mean.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTTest extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList; //input
	private GeoNumeric hypMean, mean, sd, n; //input
	private GeoText tail; //input
	private GeoNumeric  result;     // output   
	private TTestImpl tTestImpl;
	private double[] val;
	private double[] valY;

	public AlgoTTest(Construction cons, String label, GeoList geoList, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.geoList = geoList;
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = null;
		this.sd = null;
		this.n = null;
		result = new GeoNumeric(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}

	public AlgoTTest(Construction cons, String label, GeoNumeric mean, GeoNumeric sd, GeoNumeric n, GeoNumeric hypMean, GeoText tail) {
		super(cons);
		this.geoList = null;
		this.hypMean = hypMean;
		this.tail = tail;
		this.mean = mean;
		this.sd = sd;
		this.n = n;
		result = new GeoNumeric(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	public String getClassName() {
		return "AlgoTTest";
	}

	protected void setInputOutput(){

		if(geoList != null){
			input = new GeoElement[3];
			input[0] = geoList;
			input[1] = hypMean;
			input[2] = tail;
			
		}else{
			input = new GeoElement[5];
			input[0] = mean;
			input[1] = sd;
			input[2] = n;
			input[3] = hypMean;
			input[4] = tail;			
		}

		output = new GeoElement[1];
		output[0] = result;
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getResult() {
		return result;
	}

	
	private double adjustedPValue(double p, double testStatistic){

		// two sided test
		if(tail.getTextString().equals("!=")) 
			return p;

		// one sided test
		else if((tail.getTextString().equals(">") && testStatistic > 0)
				|| (tail.getTextString().equals("<") && testStatistic < 0))
			return p/2;
		else
			return 1 - p/2;
	}

	
	protected final void compute() {

		
		if(!(tail.getTextString().equals("<") 
				|| tail.getTextString().equals(">") 
				|| tail.getTextString().equals("!="))){
			result.setUndefined();
			return;
		}
		
		
		double p, testStat;
		
		
		// sample data input
		if(input.length == 3){
			
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
			
			try {
				if(tTestImpl == null)
					tTestImpl = new TTestImpl();
				
				 p = tTestImpl.tTest(hypMean.getDouble(), val);
				 testStat = tTestImpl.t(hypMean.getDouble(), val);
				result.setValue(adjustedPValue(p, testStat));
				
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}
			

		// sample statistics input 
		}else{
			        
			StatisticalSummaryValues sumStats = new StatisticalSummaryValues(
					mean.getDouble(), sd.getDouble()*sd.getDouble(), (long) n.getDouble(), -1,-1,-1);
			
			try {
				if(tTestImpl == null)
					tTestImpl = new TTestImpl();
				
				 p = tTestImpl.tTest(hypMean.getDouble(), sumStats);
				 testStat = tTestImpl.t(hypMean.getDouble(), sumStats);
				result.setValue(adjustedPValue(p, testStat));
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (MathException e) {
				e.printStackTrace();
			}
			
    	}
			
	}

}
