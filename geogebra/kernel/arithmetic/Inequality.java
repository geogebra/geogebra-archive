/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.kernel.arithmetic;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.Kernel;

/**
 * stores left and right hand side of an inequality as Expressions
 */
public class Inequality {

	/** x > f(y) */
	public static final int INEQUALITY_PARAMETRIC_X = 0;
	/** y > f(x) */
	public static final int INEQUALITY_PARAMETRIC_Y = 1;
	/** f(x,y) >0, degree of f greater than 2 */
	public static final int INEQUALITY_IMPLICIT = 2;
	/** f(x,y) >0, f is quadratic */
	public static final int INEQUALITY_CONIC = 3;
	/** can be used e.g. by PointIn, but cannot be drawn */
	public static final int INEQUALITY_INVALID = 4;
	
	private int op = ExpressionNode.LESS;
	private int type;
	private GeoImplicitPoly impBorder;
	private GeoConic conicBorder;
	private GeoFunction funBorder;
	private GeoElement border;
	private Kernel kernel;
	private boolean isAboveBorder;

	/**
	 * check whether ExpressionNodes are evaluable to instances of Polynomial or
	 * NumberValue and build an Inequality out of them
	 * 
	 * @param kernel
	 * @param lhs
	 * @param rhs
	 * @param op
	 * @param fv
	 */
	public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs,
			int op, FunctionVariable[] fv) {

		this.op = op;
		this.kernel = kernel;
		ExpressionNode n = null;

		if (op == ExpressionNode.GREATER || op == ExpressionNode.GREATER_EQUAL) {
			n = new ExpressionNode(kernel, lhs, ExpressionNode.MINUS, rhs);

		} else {
			n = new ExpressionNode(kernel, rhs, ExpressionNode.MINUS, lhs);
		}
		Double coefY = n.getCoefficient(fv[1]);
		Double coefX = n.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !Kernel.isZero(coefY) && !Double.isNaN(coefY)
				&& (coefX == null || Math.abs(coefX) < Math.abs(coefY))) {
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, n, ExpressionNode.DIVIDE, new MyDouble(kernel,
							-coefY)), ExpressionNode.PLUS, fv[1]);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !Kernel.isZero(coefX)
				&& !Double.isNaN(coefX)) {
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, n, ExpressionNode.DIVIDE, new MyDouble(kernel,
							-coefX)), ExpressionNode.PLUS, fv[0]);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = INEQUALITY_PARAMETRIC_X;
		} else {
			
			GeoElement newBorder = kernel.getAlgebraProcessor().evaluateToGeoElement(n.toString() + "=0",false);
				if(newBorder.isGeoImplicitPoly()){
					type = INEQUALITY_IMPLICIT;
					impBorder = (GeoImplicitPoly)newBorder;
					border = impBorder;					
				}
				if(newBorder.isGeoConic()){
					type = INEQUALITY_CONIC;
					conicBorder = (GeoConic)newBorder;
					border = conicBorder;
					isAboveBorder=true;
				}						
		}
		if (type == INEQUALITY_PARAMETRIC_X || type == INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction((Function) fun.deepCopy(kernel));
			if (type == INEQUALITY_PARAMETRIC_X) {
				funBorder.swapEval();
			}
			border = funBorder;
		}
		if (isStrict())
			border.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		else
			border.setLineType(EuclidianView.LINE_TYPE_FULL);
	}

	/**
	 * @return implicit border
	 */
	public GeoImplicitPoly getImpBorder() {
		return impBorder;
	}

	final public String toString() {
		return "inequality";
	}

	/**
	 * @return true if strict
	 */
	public boolean isStrict() {
		return (op == ExpressionNode.GREATER || op == ExpressionNode.LESS);
	}

	/**
	 * @return border for parametric equations
	 */
	public GeoFunction getFunBorder() {
		return funBorder;
	}

	/**
	 * Returns true for parametric ineqs like y>border(x), false for y<border(x)
	 * (for PARAMETRIC_X vars are swapped)
	 * 
	 * @return true for parametric ineqs like y>border(x), false for y<border(x)
	 * 
	 */
	public boolean isAboveBorder() {
		return isAboveBorder;
	}

	/**
	 * Returns type of ineq
	 * 
	 * @return one of {@link #INEQUALITY_IMPLICIT}
	 *         {@link #INEQUALITY_PARAMETRIC_X} {@link #INEQUALITY_PARAMETRIC_Y}
	 */
	public int getType() {
		return type;
	}

	
	/**
	 * @return the conicBorder
	 */
	public GeoConic getConicBorder() {
		return conicBorder;
	}

} // end of class Equation
