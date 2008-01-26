/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootFunction;

/**
 * Fast polynomial evaluation of Function
 */
public class PolyFunction implements RealRootFunction, RealRootDerivFunction {
	protected double [] coeffs;
	private int degree; 
	
	private PolyFunction derivative; 
	
	private double [] ret = new double[2]; // for value and derivative's value

	public PolyFunction(int degree) {
		this.degree = degree;
		coeffs = new double[degree + 1];
	}
	
	public PolyFunction(double [] c) {
		coeffs = new double[c.length];
		for (int i=0; i < c.length; i++) {
			coeffs[i] = c[i];
		}
		degree = coeffs.length - 1;
	}
	
	public PolyFunction(PolyFunction pf) {
		degree = pf.degree;
		coeffs = pf.getCoeffsCopy();
	}
	
	public double [] getCoeffs() {
		return coeffs;
	}
	
	public double [] getCoeffsCopy() {
		double [] ret = new double[coeffs.length];
		for (int i=0; i < coeffs.length; i++) {
			ret[i] = coeffs[i];
		}
		return ret;
	}
	
	/**
	 * Returns true. This method is overwritten by the subclass SymbolicPolyFunction.	 
	 */
	public boolean updateCoeffValues() {
		// nothing to do here, see SymbolicPolyFunction
		return true;
	}
	
	public int getDegree() {
		return degree;
	}
	
	final public PolyFunction getDerivative() {
		if (derivative == null) {
			derivative = buildDerivative();
		}
		return derivative;
	}
	
	private PolyFunction buildDerivative() {
		if (degree < 1)
			return new PolyFunction(0);
		
		// standard case
		PolyFunction deriv = new PolyFunction(degree - 1);
		for (int i=1; i <= degree; i++) {
			deriv.coeffs[i-1] = i * coeffs[i];
		}
		return deriv;
	}
	
	/**
	 * Evaluates polynomial and its derivative 
	 */		 
	final public double [] evaluateDerivFunc(double x){		
		ret[0] = coeffs[degree];
		ret[1] = 0;
		for (int i=degree-1; i >= 0; i--) {
			ret[1] = ret[1] * x + ret[0];
			ret[0] = ret[0] * x + coeffs[i];
		}
		return ret;
	}	
	
	/**
	 * Evaluates polynomial
	 */		 
	final public double evaluate(double x){		
		double p = coeffs[degree];
		for (int i=degree-1; i >= 0; i--) {
			p = p * x + coeffs[i];
		}
		return p;
	}	
	
	/**
	 * This routine evaluates this polynomial and its first order derivatives at x. 
	 * @param x
	 * @param order of highest derivative
	 * @return array a with polynomial value as a[0] and nd derivatives as a[1..order].
	 */
	final public double[] evaluateDerivatives(double x, int order) {
		double pd[] = new double[order+1];

		int nnd, j, i;
		double cnst = 1.0;
		pd[0] = coeffs[degree];
		for (j = 1; j <= order; j++)
			pd[j] = 0.0;
		for (i = degree - 1; i >= 0; i--) {
			nnd = (order < (degree - i) ? order : degree - i);
			for (j = nnd; j >= 1; j--)
				pd[j] = pd[j] * x + pd[j - 1];
			pd[0] = pd[0] * x + coeffs[i];
		}
		for (i = 2; i <= order; i++) { //After the first derivative, factorial constants come in.
			cnst *= i;
			pd[i] *= cnst;
		}
		return pd;
	}
	
/*
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(coeffs[0]);
		for (int i=1; i<coeffs.length; i++) {
			sb.append(" + ");
			sb.append(coeffs[i]);
			sb.append(" x^");
			sb.append(i);
		}
		return sb.toString();
	}
	*/
	
}
