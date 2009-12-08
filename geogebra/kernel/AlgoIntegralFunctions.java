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


/**
 * Area between two functions (GeoFunction) f(x) and g(x) over an interval [a, b].
 * The value equals Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] - Integral[g(x), a, b]
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralFunctions extends AlgoElement {


	private static final long serialVersionUID = 1L;
	private GeoFunction f, g; // input
	private NumberValue a, b; //input
	private GeoElement ageo, bgeo;
	private GeoNumeric n; // output n = integral(f(x) - g(x), x, a, b)   

	private GeoNumeric intF, intG;						

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
		
		// helper algorithms for integral f and g		
		AlgoIntegralDefinite algoInt = new AlgoIntegralDefinite(cons, f, a, b);
		cons.removeFromConstructionList(algoInt);
		intF = algoInt.getIntegral();
		
		algoInt = new AlgoIntegralDefinite(cons, g, a, b);
		cons.removeFromConstructionList(algoInt);
		intG = algoInt.getIntegral();
		
		// output: intF - intG
		n = new GeoNumeric(cons);				
				
		setInputOutput(); // for AlgoElement		
		compute();
		n.setLabel(label);
	}
	
	protected String getClassName() {
		return "AlgoIntegralFunctions";
	}

	// for AlgoElement
	protected void setInputOutput() {
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
	
	protected final void compute() {	
		if (!f.isDefined() || !g.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}
		
		// Integral[f(x) - g(x), a, b] = Integral[f(x), a, b] - Integral[g(x), a, b]
		n.setValue(intF.getValue() - intG.getValue());		
	}


	final public String toString() {
		return getCommandDescription();
	}
	

}
