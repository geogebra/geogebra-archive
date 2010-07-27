/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.Functional;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.util.Locale;

/**
 * Explicit function in multiple variables, e.g. f(a, b, c) := a^2 + b - 3c. 
 * This is actually a wrapper class for FunctionNVar
 * in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a FunctionNVar.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionNVar extends GeoElement
implements FunctionalNVar {

	protected FunctionNVar fun;		
	protected boolean isDefined = true;

	public GeoFunctionNVar(Construction c) {
		super(c);
	}

	public GeoFunctionNVar(Construction c, String label, FunctionNVar f) {
		super(c);
		fun = f;		
		setLabel(label);		
	}
	
	public String getClassName() {
		return "GeoFunctionNVar";
	}
	
	protected String getTypeString() {
		return "FunctionNVar";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTION_NVAR;
    }

	/** copy constructor */
	public GeoFunctionNVar(GeoFunctionNVar f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoFunctionNVar(this);
	}

	public void set(GeoElement geo) {
		GeoFunctionNVar geoFun = (GeoFunctionNVar) geo;				
						
		if (geo == null || geoFun.fun == null) {
			fun = null;
			isDefined = false;
			return;
		} else {
			isDefined = geoFun.isDefined;
			fun = new FunctionNVar(geoFun.fun, kernel);
		}			
	
		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {								
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's expression
			if (!geoFun.isIndependent()) {
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(this.fun);	
			}			
		}
	}
	

	public void setFunction(FunctionNVar f) {
		fun = f;
	}
			
	final public FunctionNVar getFunction() {
		return fun;
	}
	
	final public ExpressionNode getFunctionExpression() {
		if (fun == null)
			return null;
		else 
			return fun.getExpression();
	}	
	
	 /**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (fun != null) {
    		fun.replaceChildrenByValues(geo);
    	}
    }
    
    /**
     * Returns this function's value at position.    
     * @param vals
     * @return f(vals)
     */
	public double evaluate(double[] vals) {
		if (fun == null)
			return Double.NaN;
		else 
			return fun.evaluate(vals);
	}	
	
	/**
	 * Set this function to the n-th derivative of f
	 * @param f
	 * @param order
	 */
//	public void setDerivative(GeoDeriveable fd, int n) {
//		GeoFunctionNVar f = (GeoFunctionNVar) fd;
//		
//		if (f.isDefined()) {
//			fun = f.fun.getDerivative(n);
//			isDefined = fun != null;
//		} else {
//			isDefined = false;
//		}		
//	}
	
	/**
	 * Set this function to the integral of f
	 * @param f
	 */
//	public void setIntegral(GeoFunctionNVar f) {
//		if (f.isDefined()) {
//			fun = f.fun.getIntegral();	
//		} else {
//			isDefined = false;
//		}	
//	}
	
	/**
	 * Set this function to the expanded version of f, e.g. 3*(x-2) is expanded to 3*x - 6.
	 */
//	public void setExpanded(GeoFunctionNVar f) {
//		if (f.isDefined()) {
//			fun = f.fun.getExpanded();	
//		} else {
//			isDefined = false;
//		}	
//	}
	
//	public GeoFunctionNVar getGeoDerivative(int order){	
//		if (derivGeoFun == null) {
//			derivGeoFun = new GeoFunctionNVar(cons);
//		}
//		
//		derivGeoFun.setDerivative(this, order);
//		return derivGeoFun;					
//	}
//	private GeoFunctionNVar derivGeoFun;
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	public boolean isDefined() {
		return isDefined && fun != null;
	}

	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	public void setUndefined() {
		isDefined = false;
	}

	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {
		return isDefined() && !isBooleanFunction();
	}
	
	
	public String toString() {
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append("(");
			sbToString.append(getVarString());
			sbToString.append(") = ");
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	protected StringBuilder sbToString = new StringBuilder(80);
	
	public String toValueString() {	
		if (isDefined())
			return fun.toValueString();
		else
			return app.getPlain("undefined");
	}	
	
	public String toSymbolicString() {	
		if (isDefined())
			return fun.toString();
		else
			return app.getPlain("undefined");
	}
	
	public String toLaTeXString(boolean symbolic) {
		if (isDefined())
			return fun.toLaTeXString(symbolic);
		else
			return app.getPlain("undefined");
	}
	
	/*
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(label);
		sb.append("(x) = ");
		if (fun != null)
			sb.append(fun.toValueString());
		else
			sb.append(app.getPlain("undefined"));		
		return sb.toString();
	}
	
	// function names should not be expanded 
	public final String toValueString() {
		if (label == null) { 
			// this is a special case that will only occur
			// for functions without label that are directly
			// used as command arguments
			return fun.toString();
		}
		return label;
	}*/
	
	/**
	   * save object in xml format
	   */ 
	  public final void getXML(StringBuilder sb) {
		 
		 // an indpendent function needs to add
		 // its expression itself
		 // e.g. f(a,b) = a^2 - 3*b
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label =\"");
				sb.append(label);
				sb.append("\" exp=\"");
				sb.append(toString());
				// expression   
			sb.append("\"/>\n");
		 }
	  		  
		  sb.append("<element"); 
			  sb.append(" type=\"functionNVar\"");
			  sb.append(" label=\"");
			  sb.append(label);
		  sb.append("\">\n");
		  getXMLtags(sb);
		  sb.append(getCaptionXML());
		  sb.append("</element>\n");

	  }

	

	public boolean isNumberValue() {
		return false;		
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   

	public boolean isTextValue() {
		return false;
	}
	
	public boolean isBooleanFunction() {
		if (fun != null)
			return fun.isBooleanFunction();
		else
			return false;
	}


//	public boolean isGeoDeriveable() {
//		return true;
//	}
	
	public String getVarString(int i) {	
		return fun == null ? "" : fun.getVarString(i);
	}

	public String getVarString() {	
		return fun == null ? "" : fun.getVarString();
	}
	
	final public boolean isFunctionInX() {		
		return false;
	}
	
    // Michael Borcherds 2009-02-15
	public boolean isEqual(GeoElement geo) {
		if (!(geo instanceof GeoFunctionNVar))
			return false;
		
		String f = getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
		String g = geo.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
		String diff = ""; 
		try {
			diff = kernel.evaluateMathPiper("TrigSimpCombine(ExpandBrackets(" + f + "-(" + g + ")))");
		}
		catch (Exception e) { return false; }
		
		if ("0".equals(diff)) 
			return true; 
		else 
			return false;
	}
	
	public boolean isVector3DValue() {
		return false;
	}
	
    /**
	 * Returns a symbolic representation of geo in GeoGebraCAS syntax.
	 * For example, "f(x, y) := a x^2 + b y"
	 */
	public String toGeoGebraCASString() {
		if (!isDefined()) return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(getLabelForAssignment());
		sb.append(getAssignmentOperator());
		sb.append(fun.getExpression().getCASstring(true));
		return sb.toString();
	}
    
	 public String getLabelForAssignment() {
		StringBuilder sb = new StringBuilder();
		sb.append(getLabel());
		sb.append("(" );
		sb.append(fun.getVarString());
		sb.append(")");
		return sb.toString();
	 }



}
