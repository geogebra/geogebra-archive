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
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;



/**
 * stores left and right hand side of an inequality as
 * Expressions
 */
public class Inequality {

	/** x > f(y) */
	public static final int INEQUALITY_PARAMETRIC_X = 0;
	/** y > f(x) */
	public static final int INEQUALITY_PARAMETRIC_Y = 1;
	/** f(x,y) >0 */
	public static final int INEQUALITY_IMPLICIT = 1;
	private int op = ExpressionNode.LESS;
	private int type;
	private GeoFunction border;
	private Kernel kernel;
	private boolean isAboveBorder;
  
    /** check whether ExpressionNodes are evaluable to instances of Polynomial
     * or NumberValue and build an Inequality out of them
     * @param kernel 
     * @param lhs 
     * @param rhs 
     * @param op 
     * @param fv 
     */
    public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs, int op,FunctionVariable[] fv) {
    	 	
    	this.op = op;
    	this.kernel = kernel;
    	ExpressionNode n = null;
		
		if(op == ExpressionNode.GREATER || op == ExpressionNode.GREATER_EQUAL){
			n = new ExpressionNode(kernel,lhs,ExpressionNode.MINUS,rhs);
			
		}
		else {
			n = new ExpressionNode(kernel,rhs,ExpressionNode.MINUS,lhs);
		}
		Double d = n.getCoefficient(fv[1]);
		Application.debug(d);
		Function fun =null;
		if(d!= null && !Kernel.isZero(d)){
			isAboveBorder = d>0;
			ExpressionNode m = new ExpressionNode(kernel,new ExpressionNode(kernel,n,ExpressionNode.DIVIDE,new MyDouble(kernel,-d)),ExpressionNode.PLUS,fv[1]);
			m.simplifyLeafs();
			fun = new Function(m,fv[0]);
			type = INEQUALITY_PARAMETRIC_Y;
		}else {
			d = n.getCoefficient(fv[0]);
			Application.debug(d);
			isAboveBorder = d>0;
			ExpressionNode m = new ExpressionNode(kernel, new ExpressionNode(kernel,n,ExpressionNode.DIVIDE,new MyDouble(kernel,-d)),ExpressionNode.PLUS,fv[0]);
			m.simplifyLeafs();
			fun = new Function(m,fv[1]);
			type = INEQUALITY_PARAMETRIC_X;
		}
		border = new GeoFunction(kernel.getConstruction());
		border.setFunction((Function)fun.deepCopy(kernel));
		if(type == INEQUALITY_PARAMETRIC_X){
			border.swapEval();			
		}
		if(isStrict())
			border.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		else
			border.setLineType(EuclidianView.LINE_TYPE_FULL);
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
	public GeoFunction getBorder() {
		return border;
	}

	/**
	 * Returns true for parametric ineqs like y>border(x), false for y<border(x)
	 * (for PARAMETRIC_X vars are swapped)
	 * @return true for parametric ineqs like y>border(x), false for y<border(x)
	 * 
	 */
	public boolean isAboveBorder() {
		return isAboveBorder;
	}

	/**
	 * Returns type of ineq
	 * @return one of {@link #INEQUALITY_IMPLICIT} {@link #INEQUALITY_PARAMETRIC_X} {@link #INEQUALITY_PARAMETRIC_Y} 
	 */
	public int getType() {
		return type;
	}
	

	

 
} // end of class Equation
