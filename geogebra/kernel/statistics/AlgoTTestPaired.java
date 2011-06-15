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
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 * Performs a paired t-test.
 * 
 * 
 * @author G. Sturr
 */
public class AlgoTTestPaired extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geoList0, geoList1; //input
	private GeoText tail; //input
	private GeoNumeric  result;     // output   
	private TTestImpl tTestImpl;
	private double[] val0, val1;

	public AlgoTTestPaired(Construction cons, String label, GeoList geoList0, GeoList geoList1, GeoText tail) {
		super(cons);
		this.geoList0 = geoList0;
		this.geoList1 = geoList1;
		this.tail = tail;
		result = new GeoNumeric(cons); 
		setInputOutput(); // for AlgoElement

		compute();      
		result.setLabel(label);
	}


	public String getClassName() {
		return "AlgoTTestPaired";
	}

	protected void setInputOutput(){

		input = new GeoElement[3];
		input[0] = geoList0;
		input[1] = geoList1;
		input[2] = tail;

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

		int size= geoList0.size();
		if(!geoList1.isDefined() || geoList1.size() != size){
			result.setUndefined();	
			return;			
		}

		// create number value arrays
		val0 = new double[size];
		val1 = new double[size];
		GeoElement geo0, geo1;
		NumberValue num0, num1;

		for (int i=0; i < size; i++) {
			geo0 = geoList0.get(i);
			geo1 = geoList1.get(i);
			if (geo0.isNumberValue() && geo1.isNumberValue()) {
				num0 = (NumberValue) geo0;
				num1 = (NumberValue) geo1;
				val0[i] = num0.getDouble();
				val1[i] = num1.getDouble();

			} else {
				result.setUndefined();
				return;
			}    		    		
		}   

		try {
			if(tTestImpl == null)
				tTestImpl = new TTestImpl();

			p = tTestImpl.pairedTTest(val0, val1);
			testStat = tTestImpl.pairedT(val0, val1);
			result.setValue(adjustedPValue(p, testStat));


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

}
