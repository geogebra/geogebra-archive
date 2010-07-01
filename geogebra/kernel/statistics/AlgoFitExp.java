package geogebra.kernel.statistics;

/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;

/**
 * Fits an a*e^(b*x) to a list of pints. Adapted from AlgoFitLine and
 * AlgoPolynomialFromCoordinates (Borcherds)
 * 
 * @author Hans-Petter Ulven
 * @version 24.04.08
 */
public class AlgoFitExp extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList geolist; // input
	private GeoFunction geofunction; // output
	private RegressionMath regMath;

	public AlgoFitExp(Construction cons, String label, GeoList geolist) {
		super(cons);

		regMath = kernel.getRegressionMath();

		this.geolist = geolist;
		geofunction = new GeoFunction(cons);
		setInputOutput();
		compute();
		geofunction.setLabel(label);
	}// Constructor

	public String getClassName() {
		return "AlgoFitExp";
	}

	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geolist;
		output = new GeoElement[1];
		output[0] = geofunction;
		setDependencies();
	}// setInputOutput()

	public GeoFunction getFitExp() {
		return geofunction;
	}

	protected final void compute() {
		int size = geolist.size();
		boolean regok = true;
		double a, b;
		if (!geolist.isDefined() || (size < 2)) { // 24.04.08:2
			geofunction.setUndefined();
			return;
		} else {
			regok = regMath.doExp(geolist);
			if (regok) {
				a = regMath.getP1();
				b = regMath.getP2();
				MyDouble A = new MyDouble(kernel, a);
				MyDouble B = new MyDouble(kernel, b);
				// 24.04.08: not: MyDouble E=new MyDouble(kernel,Math.E);
				FunctionVariable X = new FunctionVariable(kernel);
				ExpressionValue expr = new ExpressionNode(kernel, B,
						ExpressionNode.MULTIPLY, X);
				expr = new ExpressionNode(kernel, expr, ExpressionNode.EXP,
						null); // 24.04.08: changed 2.71..to "e" with the null
								// trick!
				ExpressionNode node = new ExpressionNode(kernel, A,
						ExpressionNode.MULTIPLY, expr);
				Function f = new Function(node, X);
				geofunction.setFunction(f);
				geofunction.setDefined(true);
			} else {
				geofunction.setUndefined();
				return;
			}// if error in regression
		}// if error in parameters
	}// compute()

}// class AlgoFitExp