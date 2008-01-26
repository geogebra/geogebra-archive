/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;


/**
 * Area between two functions (GeoFunction) over an interval [a, b].
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralFunctions extends AlgoElement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f, g; // input
	private NumberValue a, b; //input
	private GeoElement ageo, bgeo;
	private GeoNumeric n; // output n = integral(f(x) - g(x), x, a, b)   

	private Function diffFunction;	
	private GeoFunction diffGeoFunction;			
	private AlgoIntegralDefinite algoInt;						

	public AlgoIntegralFunctions(Construction cons, String label, 
							GeoFunction f, GeoFunction g,
							NumberValue a, NumberValue b) {
		super(cons);
		this.f = f;
		this.g = g;		
		this.a = a;
		this.b = b;
		ageo = a.toGeoElement();		
		bgeo = b.toGeoElement();
		
		// use integral of differnce f - g
		diffFunction = new Function(kernel);
		diffGeoFunction = new GeoFunction(cons);
		algoInt = new AlgoIntegralDefinite(cons, diffGeoFunction, a, b);
		cons.removeFromConstructionList(algoInt);
		
		//		output
		n = new GeoNumeric(cons);				
				
		setInputOutput(); // for AlgoElement		
		compute();
		n.setLabel(label);
	}
	
	String getClassName() {
		return "AlgoIntegralFunctions";
	}

	// for AlgoElement
	void setInputOutput() {
		input = new GeoElement[4];
		input[0] = f;
		input[1] = g;
		input[2] = ageo;
		input[3] = bgeo;

		output = new GeoElement[1];
		output[0] = n;
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getIntegral() {
		return n;
	}
	
	public GeoFunction getF() {
		return f;
	}
	
	public GeoFunction getG() {
		return g;
	}	
	
	public NumberValue getA() {
		return a;
	}
	
	public NumberValue getB() {
		return b;
	}
	
	final void compute() {	
		if (!f.isDefined() || !g.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}
		
		// build difference function
		Function.difference(f.getFunction(), g.getFunction(), diffFunction);
		diffGeoFunction.setFunction(diffFunction);
		// calculate the integral of the difference function
		algoInt.compute();
		n.setValue(algoInt.getIntegralValue());		
	}


	final public String toString() {
		return getCommandDescription();
	}
	

}
