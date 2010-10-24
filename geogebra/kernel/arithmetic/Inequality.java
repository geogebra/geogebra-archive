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
import geogebra.kernel.GeoVec2D;
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
	private ExpressionNode normal;
	private FunctionVariable[] fv;
	private MyDouble coef;

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
		this.fv = fv; 

		if (op == ExpressionNode.GREATER || op == ExpressionNode.GREATER_EQUAL) {
			normal = new ExpressionNode(kernel, lhs, ExpressionNode.MINUS, rhs);

		} else {
			normal = new ExpressionNode(kernel, rhs, ExpressionNode.MINUS, lhs);
		}
		update();
	}

	private void update() {
		Double coefY = normal.getCoefficient(fv[1]);
		Double coefX = normal.getCoefficient(fv[0]);
		Function fun = null;
		if (coefY != null && !Kernel.isZero(coefY) && !Double.isNaN(coefY)
				&& (coefX == null || Math.abs(coefX) < Math.abs(coefY))) {
			coef = new MyDouble(kernel,-coefY);
			isAboveBorder = coefY > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, ExpressionNode.DIVIDE, coef), ExpressionNode.PLUS, fv[1]);
			m.simplifyLeafs();
			fun = new Function(m, fv[0]);
			type = INEQUALITY_PARAMETRIC_Y;
		} else if (coefX != null && !Kernel.isZero(coefX)
				&& !Double.isNaN(coefX)) {
			coef = new MyDouble(kernel,-coefX);
			isAboveBorder = coefX > 0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(
					kernel, normal, ExpressionNode.DIVIDE, coef), ExpressionNode.PLUS, fv[0]);
			m.simplifyLeafs();
			fun = new Function(m, fv[1]);
			type = INEQUALITY_PARAMETRIC_X;
		} else {
			
			GeoElement newBorder = kernel.getAlgebraProcessor().evaluateToGeoElement(normal.toString() + "=0",false);
				if(newBorder.isGeoImplicitPoly()){
					type = INEQUALITY_IMPLICIT;
					if(impBorder==null)
						impBorder = (GeoImplicitPoly)newBorder;
					else
						impBorder.set(newBorder);
					border = impBorder;					
				}
				if(newBorder.isGeoConic()){
					type = INEQUALITY_CONIC;
					if(impBorder == null)
						conicBorder = (GeoConic)newBorder;
					else
						conicBorder.set(newBorder);					
					border = conicBorder;
					GeoVec2D midpoint = conicBorder.getTranslationVector();
					ExpressionNode normalCopy = (ExpressionNode) normal.deepCopy(kernel);
					normalCopy.replace(fv[0], new MyDouble(kernel,midpoint.x));
					normalCopy.replace(fv[1], new MyDouble(kernel,midpoint.y));
					double valAtCenter = ((NumberValue)normalCopy.evaluate()).getDouble(); 						
					isAboveBorder = (valAtCenter < 0) ^ (conicBorder.getType() == GeoConic.CONIC_HYPERBOLA);
				}						
		}
		if (type == INEQUALITY_PARAMETRIC_X || type == INEQUALITY_PARAMETRIC_Y) {
			funBorder = new GeoFunction(kernel.getConstruction());
			funBorder.setFunction(fun);
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
	 * Updates the coefficient k in y<k*f(x) for parametric,
	 * for implicit runs full update.
	 */
	public void updateCoef(){
		double coefVal,otherVal;
		if(type == INEQUALITY_PARAMETRIC_Y){
			coefVal = normal.getCoefficient(fv[1]);
			otherVal = normal.getCoefficient(fv[0]);
			isAboveBorder = coefVal>0;
			if(coefVal == 0 || Math.abs(otherVal)>Math.abs(coefVal))
				update();
			else
				coef.set(-coefVal);
		}else if(type == INEQUALITY_PARAMETRIC_X){
			coefVal = normal.getCoefficient(fv[0]);
			otherVal = normal.getCoefficient(fv[1]);
			isAboveBorder = coefVal>0;
			if(coefVal == 0 || Math.abs(otherVal)>Math.abs(coefVal))
				update();
			else
				coef.set(-coefVal);
		}
		else update();
						
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
	 * @return border
	 */
	public GeoElement getBorder(){
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

} // end of class Equation
