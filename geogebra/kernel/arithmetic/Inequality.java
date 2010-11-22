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
import geogebra.kernel.AlgoDependentFunction;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;

/**
 * stores left and right hand side of an inequality as Expressions
 */
public class Inequality {

	/** can be used e.g. by PointIn, but cannot be drawn */
	public static final int INEQUALITY_INVALID = 0;

	/** x > f(y) */
	public static final int INEQUALITY_PARAMETRIC_X = 1;
	/** y > f(x) */
	public static final int INEQUALITY_PARAMETRIC_Y = 2;
	/** f(x,y) >0, degree of f greater than 2 */
	public static final int INEQUALITY_IMPLICIT = 3;
	/** f(x,y) >0, f is quadratic */
	public static final int INEQUALITY_CONIC = 4;

	/** inequality with one variable */
	public static final int INEQUALITY_1VAR_X = 5;
	/** inequality with one variable, called y */
	public static final int INEQUALITY_1VAR_Y = 6;

	private int op = ExpressionNode.LESS;
	private int type;
	private GeoImplicitPoly impBorder;
	private GeoConic conicBorder;
	private GeoFunction funBorder;
	private GeoElement border;
	private Kernel kernel;
	private boolean isAboveBorder;
	private ExpressionNode normal;
	private FunctionVariable[] fv;
	private MyDouble coef;
	private GeoPoint[] zeros;
	private FunctionalNVar function;

	/**
	 * check whether ExpressionNodes are evaluable to instances of Polynomial or
	 * NumberValue and build an Inequality out of them
	 * 
	 * @param kernel
	 * @param lhs
	 * @param rhs
	 * @param op
	 * @param fv
	 * @param function
	 */
	public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs,
			int op, FunctionVariable[] fv, FunctionalNVar function) {

		this.op = op;
		this.kernel = kernel;
		this.fv = fv;
		this.function = function;

		if (op == ExpressionNode.GREATER || op == ExpressionNode.GREATER_EQUAL) {
			normal = new ExpressionNode(kernel, lhs, ExpressionNode.MINUS, rhs);

		} else {
			normal = new ExpressionNode(kernel, rhs, ExpressionNode.MINUS, lhs);
		}
		update();
	}

	private void update() {
		if (fv.length == 1) {
			init1varFunction(0);
			if(!funBorder.isPolynomialFunction(false)){
				type = INEQUALITY_INVALID;
			}
			else if (fv[0].toString().equals("y")) {
				type = INEQUALITY_1VAR_Y;
			} else
				type = INEQUALITY_1VAR_X;

			return;
		}
		Double coefY = normal.getCoefficient(fv[1]);
		Double coefX = normal.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !Kernel.isZero(coefY) && !Double.isNaN(coefY)
				&& (coefX == null || Math.abs(coefX) < Math.abs(coefY))) {
			coef = new MyDouble(kernel, -coefY);
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, ExpressionNode.DIVIDE, coef),
					ExpressionNode.PLUS, fv[1]);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !Kernel.isZero(coefX)
				&& !Double.isNaN(coefX)) {
			coef = new MyDouble(kernel, -coefX);
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, ExpressionNode.DIVIDE, coef),
					ExpressionNode.PLUS, fv[0]);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = INEQUALITY_PARAMETRIC_X;
		} else if (coefX != null && Kernel.isZero(coefX) && coefY == null) {
			normal.replace(fv[0], new MyDouble(kernel, 0));
			init1varFunction(1);
			type = funBorder.isPolynomialFunction(false) ?				
				INEQUALITY_1VAR_Y:INEQUALITY_INVALID;
		} else if (coefY != null && Kernel.isZero(coefY) && coefX == null) {
			normal.replace(fv[1], new MyDouble(kernel, 0));
			init1varFunction(0);
			type = funBorder.isPolynomialFunction(false) ?				
					INEQUALITY_1VAR_X:INEQUALITY_INVALID;
		}

		else {

			GeoElement newBorder = kernel.getAlgebraProcessor()
					.evaluateToGeoElement(normal.toString() + "=0", false);
			if (newBorder == null)
				return;
			if (newBorder.isGeoImplicitPoly()) {
				type = INEQUALITY_IMPLICIT;
				if (impBorder == null)
					impBorder = (GeoImplicitPoly) newBorder;
				else
					impBorder.set(newBorder);
				border = impBorder;
			}
			if (newBorder.isGeoConic()) {
				type = INEQUALITY_CONIC;
				if (conicBorder == null)
					conicBorder = (GeoConic) newBorder;
				else
					conicBorder.set(newBorder);
				border = conicBorder;
				GeoVec2D midpoint = conicBorder.getTranslationVector();
				ExpressionNode normalCopy = (ExpressionNode) normal
						.deepCopy(kernel);
				double midX, midY;
				if (conicBorder.type == GeoConic.CONIC_INTERSECTING_LINES) {
					GeoLine[] lines = conicBorder.getLines();
					midX = midpoint.x + lines[0].x + lines[1].x;
					midY = midpoint.y + lines[0].y + lines[1].y;
				} else {
					midX = midpoint.x;
					midY = midpoint.y;
				}
				normalCopy.replace(fv[0], new MyDouble(kernel, midX));
				normalCopy.replace(fv[1], new MyDouble(kernel, midY));
				double valAtCenter = ((NumberValue) normalCopy.evaluate())
						.getDouble();
				isAboveBorder = (valAtCenter < 0)
						^ (conicBorder.getType() == GeoConic.CONIC_HYPERBOLA);
			}
		}
		if (type == INEQUALITY_PARAMETRIC_X || type == INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction(fun);
			if (type == INEQUALITY_PARAMETRIC_X) {
				funBorder.swapEval();
			}
		}
		if (funBorder != null)
			border = funBorder;
		if (isStrict()) {
			border.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		} else
			border.setLineType(EuclidianView.LINE_TYPE_FULL);
	}

	private void init1varFunction(int varIndex) {
		Construction cons = kernel.getConstruction();
		boolean supress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		if (normal.containsObjectType(GeoElement.class)) {
			AlgoDependentFunction df = new AlgoDependentFunction(cons, null,
					new Function(normal, fv[varIndex]));
			funBorder = df.getFunction();
			AlgoElement thisPar = ((GeoElement) function).getParentAlgorithm();
			if (thisPar != null)
				thisPar.setUpdateAfterAlgo(df);// TODO: make this work
		} else {
			funBorder = new GeoFunction(cons);
			funBorder.setFunction(new Function(normal, fv[varIndex]));
		}
		zeros = kernel.Root(null, funBorder);
		cons.setSuppressLabelCreation(supress);
		border = funBorder;
		if (isStrict()) {
			border.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		} else
			border.setLineType(EuclidianView.LINE_TYPE_FULL);

	}

	/**
	 * Updates the coefficient k in y<k*f(x) for parametric, for implicit runs
	 * full update.
	 */
	public void updateCoef() {
		Double coefVal = null, otherVal = null;
		if (type == INEQUALITY_PARAMETRIC_Y) {
			coefVal = normal.getCoefficient(fv[1]);
			otherVal = normal.getCoefficient(fv[0]);

		} else if (type == INEQUALITY_PARAMETRIC_X) {
			coefVal = normal.getCoefficient(fv[0]);
			otherVal = normal.getCoefficient(fv[1]);
		}
		if (coefVal == null || coefVal == 0
				|| (otherVal != null && Math.abs(otherVal) > Math.abs(coefVal)))
			update();
		else {
			isAboveBorder = coefVal > 0;
			coef.set(-coefVal);
		}

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
	 * Returns border, which can be function, conic or implicit polynomial
	 * 
	 * @return border
	 */
	public GeoElement getBorder() {
		return border;
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

	/**
	 * @return zero points for 1var ineqs
	 */
	public GeoPoint[] getZeros() {
		return zeros;
	}

} // end of class Equation
