/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Matrix.Coords;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.Functional;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.FunctionNVar.IneqTree;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;
import geogebra.util.Unicode;
import geogebra.util.Util;


import java.util.Locale;

/**
 * Explicit function in one variable ("x"). This is actually a wrapper class for Function
 * in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a Function.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunction extends GeoElement
implements VarString, Path, Translateable, Traceable, Functional, FunctionalNVar, GeoFunctionable,Region,
CasEvaluableFunction, ParametricCurve, LineProperties, RealRootFunction, Dilateable, Transformable {

	private static final long serialVersionUID = 1L;
	
	/** inner function representation */
	protected Function fun;		
	/** true if this function should be considered defined */
	protected boolean isDefined = true;
	private boolean trace;	
	
	// if the function includes a division by var, e.g. 1/x, 1/(2+x)
    private boolean includesDivisionByVar = false;
    
    /**  function may be limited to interval [a, b] */ 
    protected boolean interval = false;
    /** lower interval bound */
    protected double intervalMin;
    /** upper interval bound */
    protected double intervalMax; 
    private boolean evalSwapped;
    // parent conditional function
   // private GeoFunctionConditional parentCondFun = null;

	private boolean isInequality;    
	
	/**
	 * Creates new function
	 * @param c construction
	 */
	public GeoFunction(Construction c) {
		super(c);
	}

	/**
	 * Creates new function
	 * @param c construction
	 * @param label label for function
	 * @param f function
	 */
	public GeoFunction(Construction c, String label, Function f) {
		super(c);
		fun = f;				
		fun.initFunction();
		if(fun.isBooleanFunction()){
			GeoElement ge = cons.getConstructionDefaults().getDefaultGeo(ConstructionDefaults.DEFAULT_INEQUALITY_1VAR);
			setVisualStyle(ge);
			setAlphaValue(ge.getAlphaValue());
		//TODO: Remove following code for 5.0 -- it's there to make sure no functions of y are created	
		}
		setLabel(label);
		if(isLabelSet() && !isBooleanFunction() && "y".equals(fun.getVarString(0))){
			FunctionVariable fv = new FunctionVariable(kernel,"x");
			fun.getExpression().replace(fun.getFunctionVariables()[0], fv);
			fun.getFunctionVariables()[0]=fv;
			fun.initFunction();
			update();
		}
	}
	
	public void setVisualStyle(GeoElement g){
		super.setVisualStyle(g);
		if(g instanceof GeoFunction)
			setShowOnAxis(((GeoFunction)g).showOnAxis); 
	}
	
	public String getClassName() {
		return "GeoFunction";
	}
	
	protected String getTypeString() {
		return isInequality?"Inequality":"Function";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTION;
    }

	/** copy constructor 
	 * @param f Function to be copied */
	public GeoFunction(GeoFunction f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoFunction(this);
	}
	
	public void set(GeoElement geo) {
		Function geoFun = ((GeoFunction) geo).getFunction();
			
		if (geo == null || geoFun == null) {
			fun = null;
			isDefined = false;
			return;
		} else {
			isDefined = geo.isDefined();
			fun = new Function(geoFun, kernel);		
		}			
	
		// macro OUTPUT
		if (geo.cons != cons && isAlgoMacroOutput()) {								
			// this object is an output object of AlgoMacro
			// we need to check the references to all geos in its function's expression
			if (!geo.isIndependent()) {
				AlgoMacro algoMacro = (AlgoMacro) getParentAlgorithm();
				algoMacro.initFunction(this.fun);	
			}			
		}
		isInequality = fun.initIneqs(this.getFunctionExpression(),this);
	}
	

	/**
	 * Sets the inner function
	 * @param f function
	 */
	public void setFunction(Function f) {
		fun = f;
	}
			
	public Function getFunction() {
		return fun;
	}	
	
	/**
	 * Sets interval for the function
	 * @param a lower bound
	 * @param b upper bound
	 * @return true if the resulting interval is non-empty
	 */
	final public boolean setInterval(double a, double b) {
    	if (a <= b) {         
            interval = true;
            this.intervalMin = a; 
            this.intervalMax = b;              
        } else {
        	interval = false;            
        }   
    	
    	return interval;  
    }		
	
   /**
    * Returns function expression
    * @return function expression
    */
	final public ExpressionNode getFunctionExpression() {
		if (getFunction() == null)
			return null;
		else 
			return getFunction().getExpression();
	}	
	
	 /**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     * @param geo geo to be replaced 
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (fun != null) {
    		fun.replaceChildrenByValues(geo);
    	}
    }
	
	/**
	 * Returns the corresponding Function for the given x-value.
	 * This is important for conditional functions where we have
	 * two differen Function objects.
	 * @param x x-value
	 * @return coresponding function
	 */
	public Function getFunction(double x) {
		return fun;
	}
	
	/**
	 * Set this function to the n-th derivative of f
	 * @param fd function to be differenced
	 * @param n order of derivative
	 */
	public void setDerivative(CasEvaluableFunction fd, int n) {
		GeoFunction f = (GeoFunction) fd;
		
		if (f.isDefined()) {
			fun = f.fun.getDerivative(n);
			isDefined = fun != null;
		} else {
			isDefined = false;
		}		
	}
	
	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd the GeoGebraCAS command needs to include % in all places
	 * where the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f the function that the CAS command is applied to
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f, boolean symbolic){
		GeoFunction ff = (GeoFunction) f;
		
		if (ff.isDefined()) {
			fun = (Function) ff.fun.evalCasCommand(ggbCasCmd, symbolic);
			isDefined = fun != null;
		} else {
			isDefined = false;
		}		
	}	
	
	/**
	 * Returns this function's value at position x.
	 * @param x
	 * @return f(x)
	 */
	public double evaluate(double x) {
		if (fun == null)
			return Double.NaN;
		else
			return fun.evaluate(x);
	}
	
	/**
	 * Returns this function's value at position x.
	 * @param vals array of length 1 containing x
	 * @return f(val[0]) or f(val[1])
	 */
	public double evaluate(double[] vals) {
		
		return evaluate(vals[0]);
	}
	
	/**
	 * If restricted to interval, returns its minimum
	 * @return interval minimum
	 */
	public final double getIntervalMin() {
		return intervalMin;
	}
	/**
	 * If restricted to interval, returns its maximum
	 * @return interval maximum
	 */
	public final double getIntervalMax() {
		return intervalMax;
	}
	/**
	 * Iff restricted to interval, returns true
	 * @return true iff restricted to interval
	 */
	public final boolean hasInterval() {
		return interval;
	}
	
	/**
	 * Returns this boolean function's value at position x.
	 * @param x
	 * @return f(x)
	 */
	final public boolean evaluateBoolean(double x) {		
		return fun.evaluateBoolean(x);
	}
	
	public GeoFunction getGeoDerivative(int order){	
		if (derivGeoFun == null) {
			derivGeoFun = new GeoFunction(cons);
		}
		
		derivGeoFun.setDerivative(this, order);
		return derivGeoFun;					
	}
	private GeoFunction derivGeoFun;
	
	public ExpressionValue evaluate() {
		return this;
	}
	
	/**
	 * translate function by vector v
	 */
	final public void translate(Coords v) {
		translate(v.getX(), v.getY());
	}
	
	final public boolean isTranslateable() {
		return fun != null && !isBooleanFunction();
	}
	
	/**
	 * Shifts the function by vx to right and by vy up
	 * @param vx horizontal shift
	 * @param vy vertical shift
	 */
	public void translate(double vx, double vy) {
		fun.translate(vx, vy);
	}

	
	/**
	 * Returns true if this function is a polynomial.
	 * @return true if this function is a polynomial.
	 * @param forRootFinding set to true if you want to allow
	 * functions that can be factored into polynomial factors
	 * for root finding (e.g. sqrt(x) could be replaced by x)
	 * @param symbolic function's symbolic expression must be a polynomial,
	 * e.g. x^2 is ok but not x^a
	 */
	public boolean isPolynomialFunction(boolean forRootFinding, boolean symbolic) {		
		// don't do root finding simplification here
		// i.e. don't replace a factor "sqrt(x)" by "x"
		if (!isDefined()) 
			return false;
		else
			return fun.isConstantFunction() || 
				(symbolic ? 
						fun.getSymbolicPolynomialFactors(forRootFinding) :
						fun.getPolynomialFactors(forRootFinding))
					!= null;
	}

	/**
	 * Returns true if this function is a polynomial.
	 * @return true if this function is a polynomial.
	 * @param forRootFinding set to true if you want to allow
	 * functions that can be factored into polynomial factors
	 * for root finding (e.g. sqrt(x) could be replaced by x)
	 */
	
	public boolean isPolynomialFunction(boolean forRootFinding) {
		return isPolynomialFunction(forRootFinding, false);
	}
	
	/**
     * Returns whether this function includes a division by variable,
     * e.g. f(x) = 1/x, 1/(2+x), sin(3/x), ...
     * @return true iff this function includes a division by variable
     */
    final public boolean includesDivisionByVar() {
    	if (includesDivisionByVarFun != fun) {
    		includesDivisionByVarFun = fun;    		
    		includesDivisionByVar = fun != null && fun.includesDivisionByVariable();    		
    	}
    	return includesDivisionByVar;
    }
    private Function includesDivisionByVarFun = null;
    

	public boolean isDefined() {
		return isDefined && fun != null;
	}
	
	public boolean isFillable(){
		return isInequality;
	}

	/**
	 * Changes the defined state
	 * @param defined true iff the function should be considered defined
	 */
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
		return isDefined() && (!isBooleanFunction() || isInequality);
	}

		
	/**
	 * @return function description as f(x)=... for real and e.g. f:x>4 for bool
	 */
	public String toString() {		
		sbToString.setLength(0);
		if(isLabelSet()) {
			sbToString.append(label);
			if(isBooleanFunction())
				sbToString.append(": ");
			else{
				sbToString.append("(");
				sbToString.append(getVarString());
				sbToString.append(") = ");
			}
		}
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	/** StringBuilder for temporary string manipulation */
	protected StringBuilder sbToString = new StringBuilder(80);

	private boolean showOnAxis;
	
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
	
	
		
	/**
	   * save object in xml format
	   */ 
	  public final void getXML(StringBuilder sb) {
		 
		 // an indpendent function needs to add
		 // its expression itself
		 // e.g. f(x) = x� - 3x
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label =\"");
				sb.append(label);
				sb.append("\" exp=\"");
				sb.append(Util.encodeXML(toString()));
				// expression   
			sb.append("\"/>\n");
		 }
	  		  
		  sb.append("<element"); 
			  sb.append(" type=\"function\"");
			  sb.append(" label=\"");
			  sb.append(label);
		  sb.append("\">\n");
		  getXMLtags(sb);
		  sb.append(getCaptionXML());
		  sb.append("</element>\n");

	  }
	
	/**
	* returns all class-specific xml tags for getXML
	*/
		protected void getXMLtags(StringBuilder sb) {
	   super.getXMLtags(sb);
	 
	   //	line thickness and type  
		getLineStyleXML(sb);
		if(showOnAxis()){
			sb.append("<showOnAxis val=\"true\" />");
		}

   }

	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPointND PI) {			
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.z == 1.0) {
			P.x = P.x;			
		} else {
			P.x = P.x / P.z;			
		}
				
		
		if(!isBooleanFunction()){
			if (interval) {
				//	don't let P move out of interval			
				if (P.x < intervalMin) 
					P.x = intervalMin;
				else if (P.x > intervalMax) 
					P.x = intervalMax;
			}
			P.y = evaluate(P.x);// changed from fun.evaluate so that it works with eg Point[If[x < -1, x + 1, x�]] 
		}
		else {
			pointChangedBoolean(true,P);
		}
		P.z = 1.0;
		
		// set path parameter for compatibility with
		// PathMoverGeneric
		PathParameter pp = P.getPathParameter();
		pp.t = P.x;
	}
	
	private void pointChangedBoolean(boolean b, GeoPoint P) {
		double px;
		boolean yfun = getVarString().equals("y");
		if(yfun){
			if(b)P.x = 0.0;
			px = P.y;
		}else{
			if(b)P.y = 0.0;
			px = P.x;
		}
		double bestDist = Double.MAX_VALUE;
		getIneqs();			
		if(!this.evaluateBoolean(px)){		
			FunctionNVar.IneqTree ineqs = fun.getIneqs();
			int ineqCount = ineqs.getSize();
			for(int i=0;i<ineqCount;i++){
				for(GeoPoint point:ineqs.get(i).getZeros())
					if(Math.abs(point.x-px)<bestDist){
						bestDist = Math.abs(point.x-px);
						if(yfun)
							P.y = point.x;
						else
							P.x=point.x;
					}
			}
		}
		
	}

	public boolean isOnPath(GeoPointND PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		if(!isBooleanFunction()){
			return isDefined &&	Math.abs(fun.evaluate(P.inhomX) - P.inhomY) <= eps;
		}
		else{
			double px = getVarString().equals("y") ? P.y :P.x;
			if (P.z != 1.0) {
					px = px / P.z;		
			}
			return evaluateBoolean(px);
		}
	}

	public void pathChanged(GeoPointND PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		PathParameter pp = P.getPathParameter();
		P.x = pp.t;
		pointChanged(P);
	}
	
	public boolean isPath() {
		return true;
	}

	
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * Last change by Zbynek Konecny, see #5
	 * @version 2010-05-14
	 * @return smallest possible parameter value (may be Double.NEGATIVE_INFINITY)
	 */
	public double getMinParameter() {
		if (interval)
			return Math.max(getXmin(), intervalMin);
		else
			return getXmin();
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * Last change by Zbynek Konecny, see #5
	 * @version 2010-05-14
	 * @return largest possible parameter value (may be Double.POSITIVE_INFINITY)
	 */
	public double getMaxParameter() {
		if (interval)
			return Math.min(getXmax(), intervalMax);
		else
			return getXmax();
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	public boolean isClosedPath() {
		return false;
	}

	final public boolean isCasEvaluableObject() {
		return true;
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
	
	public boolean isTraceable() {
		return true;
	}

	public boolean getTrace() {		
		return trace;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;	
	}   

	//G.Sturr 2010-5-18  get/set spreadsheet trace not needed here
	/*
	public void setSpreadsheetTrace(boolean spreadsheetTrace) {
		this.spreadsheetTrace = spreadsheetTrace;
	}

	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}
	*/
	
	
	public GeoFunction getGeoFunction() {
		return this;
	}
	
	public boolean isGeoFunction() {
		if (fun != null)
			return !fun.isBooleanFunction();
		else
			return true;
	}
	
	public boolean isGeoFunctionable() {
		return isGeoFunction();
	}
		
	public boolean isBooleanFunction() {
		if (fun != null)
			return fun.isBooleanFunction();
		else
			return false;
	}

	/** changes variable interpretation:
	 * if swapped, the function is considered to be 
	 * x=f(y).
	 */
	public void swapEval(){
		evalSwapped = !evalSwapped;
	}
	
	public void evaluateCurve(double t, double[] out) {
		if(evalSwapped){
			out[1] = t;
			out[0] = evaluate(t);
		}
		else{
			out[0] = t;
			out[1] = evaluate(t);
		}
	}		
	
	/**
	 * Evaluates curvature for function:  k(x) = f''/T^3, T = sqrt(1+(f')^2)
	 * @author Victor Franco Espino, Markus Hohenwarter
	 */
	public double evaluateCurvature(double x) {			
		Function f1 = fun.getDerivative(1);
		Function f2 = fun.getDerivative(2);				
		if (f1 == null || f2 == null)
			return Double.NaN;
		
		double f1eval = f1.evaluate(x);
		double t = Math.sqrt(1 + f1eval * f1eval);
		double t3 = t * t * t;
		return f2.evaluate(x) / t3;    	
	}
    
	final public RealRootFunction getRealRootFunctionX() {
		return new RealRootFunction() {
			public double evaluate(double t) {
				return t;
			}
		};
	}
	
	final public RealRootFunction getRealRootFunctionY() {
		return new RealRootFunction() {
			public double evaluate(double t) {
				return GeoFunction.this.evaluate(t);
			}
		};
	}

	public GeoVec2D evaluateCurve(double t) {
		return new GeoVec2D(kernel, t, evaluate(t));
	}
	
	public String getVarString() {	
		return fun == null ? "x" : fun.getVarString();
	}

	final public boolean isFunctionInX() {		
		return true;
	}
	
	/*
	public final GeoFunctionConditional getParentCondFun() {
		return parentCondFun;
	}

	public final void setParentCondFun(GeoFunctionConditional parentCondFun) {
		this.parentCondFun = parentCondFun;
	}*/
	
    // Michael Borcherds 2009-02-15
	public boolean isEqual(GeoElement geo) {
		
		if (!geo.isGeoFunction() || geo.getGeoClassType() == GeoElement.GEO_CLASS_INTERVAL)
			return false;
		
		
		// return return geo.isEqual(this); rather than false
		// in case we improve checking in GeoFunctionConditional in future
		if (geo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTIONCONDITIONAL)
			return geo.isEqual(this);

		
		String f = getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
		String g = geo.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
		
		String diff = ""; 
			
		try {
			diff = kernel.evaluateMathPiper("TrigSimpCombine(ExpandBrackets(" + f + "-(" + g + ")))");
		}
		catch (Exception e) { return false; }
		
		
		if ("0".equals(diff)) return true; else return false;
		
	}
	
	/**
	 * Sums two functions and stores the result to another 
	 * @param resultFun resulting function
	 * @param fun1 first addend
	 * @param fun2 second addend
	 * @return resultFun
	 */
	public static GeoFunction add(GeoFunction resultFun, GeoFunction fun1, GeoFunction fun2) {
		
		Kernel kernel = fun1.getKernel();
		
    	FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
    	FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
    	FunctionVariable x =  new FunctionVariable(kernel);
    	
    	ExpressionNode left = fun1.getFunctionExpression().getCopy(kernel);
       	ExpressionNode right = fun2.getFunctionExpression().getCopy(kernel);    
       	
    	ExpressionNode sum = new ExpressionNode(fun1.getKernel(), left.replace(x1,x), ExpressionNode.PLUS, right.replace(x2,x));
    	
    	Function f = new Function(sum,x);
    	
       	resultFun.setFunction(f);
       	resultFun.setDefined(true);
       	
       	return resultFun;
	}
	/**
	 * Applies an operation on first and second function and returns the result
	 * @param op
	 * @param fun1
	 * @param fun2
	 * @return resulting GeoFunction or GeFunctionNvar
	 */
	public static GeoElement operationSymb(int op, GeoFunction fun1, GeoFunction fun2) {
		Kernel kernel = fun1.getKernel();
		FunctionVariable x =  new FunctionVariable(kernel,"x");
		FunctionVariable y =  new FunctionVariable(kernel,"y");
		FunctionVariable swap;
		
		if("y".equals(fun1.getVarString())^"y".equals(fun2.getVarString())){

			FunctionVariable[] xy = new FunctionVariable[] {x,y};
			if("y".equals(fun2.getVarString())){
				swap = x;
				x= y;
				y=swap;
			}
			
			ExpressionNode sum = new ExpressionNode(kernel,
					new ExpressionNode(kernel,fun1,ExpressionNode.FUNCTION,y),
					op,
					fun2==null ? null:new ExpressionNode(kernel,fun2,ExpressionNode.FUNCTION,x));
			
	    	FunctionNVar f = new FunctionNVar(sum,xy);
	    	f.initFunction();       	
	       	AlgoDependentFunctionNVar adf = new AlgoDependentFunctionNVar(fun1.getConstruction(),null,f);
	       	return adf.getFunction();
		}
			
		if("y".equals(fun1.getVarString()))x=y;
		ExpressionNode sum = new ExpressionNode(kernel,
				new ExpressionNode(kernel,fun1,ExpressionNode.FUNCTION,x),
				op,
				fun2==null ? null:new ExpressionNode(kernel,fun2,ExpressionNode.FUNCTION,x));
		
		
    	Function f = new Function(sum,x);
    	f.initFunction();       	
       	AlgoDependentFunction adf = new AlgoDependentFunction(fun1.getConstruction(),null,f);
       	return adf.getFunction();
	}
	/**
	 * Applies an operation on this function and number value
	 * @param op
	 * @param fun1
	 * @param nv
	 * @param right f op nv for true, nv op f for false
	 * @return resulting function
	 */
	public static GeoFunction applyNumberSymb(int op, GeoFunction fun1, ExpressionValue nv,boolean right) {
		
		Kernel kernel = fun1.getKernel();
		
		FunctionVariable x =  new FunctionVariable(kernel);
		if(nv instanceof ExpressionNode)
			((ExpressionNode)nv).replaceVariables(fun1.getVarString(), x);
		else if((nv instanceof FunctionVariable) && nv.toString().equals(fun1.getVarString()))
			nv = x;
		ExpressionNode sum;
		if(right){sum = new ExpressionNode(kernel,
				new ExpressionNode(kernel,fun1,ExpressionNode.FUNCTION,x),				
				op, nv);			
		}
		else{ sum = new ExpressionNode(kernel,nv,op,
				new ExpressionNode(kernel,fun1,ExpressionNode.FUNCTION,x));
		}
    	Function f = new Function(sum,x);
    	f.initFunction();       	
       	AlgoDependentFunction adf = new AlgoDependentFunction(fun1.getConstruction(),null,f);
       	
       	return adf.getFunction();
	}

	
	
	/**
	 * Subtracts two functions and stores the result to another 
	 * @param resultFun resulting function
	 * @param fun1 minuend
	 * @param fun2 subtrahend
	 * @return resultFun
	 */
	public static GeoFunction subtract(GeoFunction resultFun, GeoFunction fun1, GeoFunction fun2) {
		
		Kernel kernel = fun1.getKernel();
		
    	FunctionVariable x1 = fun1.getFunction().getFunctionVariable();
    	FunctionVariable x2 = fun2.getFunction().getFunctionVariable();
    	FunctionVariable x =  new FunctionVariable(kernel);
    	

    	ExpressionNode left = fun1.getFunctionExpression().getCopy(kernel);
       	ExpressionNode right = fun2.getFunctionExpression().getCopy(kernel);    
       	
    	ExpressionNode sum = new ExpressionNode(fun1.getKernel(), left.replace(x1,x), ExpressionNode.MINUS, right.replace(x2,x));
    	
    	Function f = new Function(sum,x);
    	
       	resultFun.setFunction(f);
       	resultFun.setDefined(true);
       	
       	return resultFun;
	}	

	/** Multiplication of number and function.
	 * Needed in Fit[<List of Points>,<List of Functions>]
	 * to make the result a linear combination of existing functions; fit(x)=a*f(x)+b*g(x)+c*h(x)+..
	 * @author Hans-Petter Ulven
	 * @version 2010-02-22
	 * @param resultFun Resulting function
	 * @param number number
	 * @param fun function
	 * @return resultFun
	 * 
	 */
	public static GeoFunction mult(GeoFunction resultFun, double number, GeoFunction fun) {
		
		Kernel kernel = fun.getKernel();
		geogebra.kernel.arithmetic.MyDouble num = new geogebra.kernel.arithmetic.MyDouble(kernel,number);
		
    	FunctionVariable xold = fun.getFunction().getFunctionVariable();
    	FunctionVariable x =  new FunctionVariable(kernel);
    	

    	ExpressionNode left = new ExpressionNode(kernel,num);
       	ExpressionNode right = fun.getFunctionExpression().getCopy(kernel);    
       	
    	ExpressionNode product = new ExpressionNode(kernel,left, ExpressionNode.MULTIPLY, right.replace(xold,x));
    	
    	Function f = new Function(product,x);
    	
       	resultFun.setFunction(f);
       	resultFun.setDefined(true);
       	
       	return resultFun;
	}//mult()
	
	public boolean isVector3DValue() {
		return false;
	}
	
	/**
	 * Returns true iff x is in the interval
	 * over-ridden in GeoFunctionConditional
	 * @param x
	 * @return true iff x is in the interval
	 */
	public boolean evaluateCondition(double x) {
		if (!interval) return true;
		return x > intervalMin && x < intervalMax;
	}
	
	/**
	 * Returns the limit
	 * @param x point to evaluate the limit
	 * @param direction 1 for limit above, -1 for limit below, standard limit otherwise
	 * @return the limit
	 */
	public double getLimit(double x, int direction) {
   	String functionIn = fun.getExpression().getCASstring(kernel.getCurrentCAS(), true);
	    
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
	    sb.setLength(0);
        sb.append("Limit");
        if (direction == -1) sb.append("Above");
        else if (direction == 1) sb.append("Below");       
        sb.append('(');
        sb.append(functionIn);
        sb.append(',');
        sb.append(fun.getFunctionVariables()[0]);
        sb.append(',');
        sb.append(Double.toString(x));
        sb.append(')');


		
		try {
			String functionOut = kernel.evaluateGeoGebraCAS(sb.toString());
			NumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(functionOut, false);
			return nv.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		} catch (Throwable e) {
			e.printStackTrace();
			return Double.NaN;
	}
	}

	/**
	 * Adds vertical asymptotes to the StringBuilder
	 *  over-ridden in GeoFunctionConditional
	 * @param f function whose asymptotes we are looking for
	 * @param verticalSB StringBuilder for the result
	 * @param reverse if true, we reverse the parent conditional function condition
	 */
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB, boolean reverse) {
		getVerticalAsymptotesStatic(this, f, verticalSB, reverse);
	}
	
	/**
	 * Adds horizontal positive asymptotes to the StringBuilder
	 *  over-ridden in GeoFunctionConditional
	 * @param f function whose asymptotes we are looking for
	 * @param SB StringBuilder for the result
	 */
	public void getHorizontalPositiveAsymptote(GeoFunction f, StringBuilder SB) {
		getHorizontalAsymptoteStatic(this, f, SB, true);		
	}
	
	/**
	 * Adds horizontal negative asymptotes to the StringBuilder
	 *  over-ridden in GeoFunctionConditional
	 * @param f function whose asymptotes we are looking for
	 * @param SB StringBuilder for the result
	 */
	public void getHorizontalNegativeAsymptote(GeoFunction f, StringBuilder SB) {
		getHorizontalAsymptoteStatic(this, f, SB, false);		
	}
	
	/**
	 * Adds diagonal positive asymptotes to the StringBuilder
	 *  over-ridden in GeoFunctionConditional
	 * @param f function whose asymptotes we are looking for
	 * @param SB StringBuilder for the result
	 */
	
	public void getDiagonalPositiveAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, true);		
	}
	
	/**
	 * Adds diagonal negative asymptotes to the StringBuilder
	 *  over-ridden in GeoFunctionConditional
	 * @param f function whose asymptotes we are looking for
	 * @param SB StringBuilder for the result
	 */
	public void getDiagonalNegativeAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, false);		
	}
	
	private static StringBuilder sb;

	/**
     * Adds diagonal asymptotes to the string builder SB
     * @param f function whose asymptotes we are looking for
     * @param parentFunction parent function (in case of conditional functions)
     * @param SB StringBuilder for the result
     * @param positiveInfinity if true, we look for limit at positive infinity, for false, we use negative infinity
     */
    protected void getDiagonalAsymptoteStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder SB, boolean positiveInfinity) {
    	String functionIn = f.getFunction().getExpression().getCASstring(kernel.getCurrentCAS(), true);
	    
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
    	
    	try {
        sb.append("Simplify(Derivative(");
        sb.append(functionIn);
        sb.append("))");
		String firstDerivative = kernel.evaluateGeoGebraCAS(sb.toString());
		
		if (!f.CASError(firstDerivative, false)) {
	
		
			String gradientStrMinus="";
			String interceptStrMinus="";
			
			{
				
				sb.setLength(0);
		        sb.append("Limit(");
		        sb.append(firstDerivative);		        
		        sb.append(',');
		        if (!positiveInfinity) sb.append('-'); // -Infinity
		        sb.append(Unicode.Infinity);
		        sb.append(')');

				gradientStrMinus = kernel.evaluateGeoGebraCAS(sb.toString());
				
				if (!f.CASError(gradientStrMinus, false) && !gradientStrMinus.equals("0")) {
					sb.setLength(0);
			        sb.append("Limit(Simplify(");
			        sb.append(functionIn);
			        sb.append("-");
			        sb.append(gradientStrMinus);
			        sb.append("*x),");
			        if (!positiveInfinity) sb.append('-'); // -Infinity
			        sb.append(Unicode.Infinity);
			        sb.append(')');

			        interceptStrMinus = kernel.evaluateGeoGebraCAS(sb.toString());
					
					if (!f.CASError(interceptStrMinus, false)) {
						sb.setLength(0);
						sb.append("y=");
						sb.append(gradientStrMinus);
						sb.append("*x+");
						sb.append(interceptStrMinus);
						
						if (!SB.toString().endsWith(sb.toString())) { // not duplicated
							if (SB.length() > 1) SB.append(',');
							SB.append(sb);
							//Application.debug("diagonal asymptote minus: y = "+gradientStrMinus+"x + "+interceptStrMinus);			
						}
						
					}
				}		
			}
		}
    	}  catch (Throwable e) {
			e.printStackTrace();
		}

    }
    
    /**
     * Adds horizontal asymptotes to the string builder SB
     * @param f function whose asymptotes we are looking for
     * @param parentFunction parent function (in case of conditional functions)
     * @param SB StringBuilder for the result
     * @param positiveInfinity if true, we look for limit at positive infinity, for false, we use negative infinity
     */
    protected void getHorizontalAsymptoteStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder SB, boolean positiveInfinity) {
    	String functionStr = f.getFunction().getExpression().getCASstring(kernel.getCurrentCAS(), true);
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
        sb.append("Limit(");
        sb.append(functionStr);
        sb.append(',');
        if (!positiveInfinity) sb.append('-'); // -Infinity
        sb.append(Unicode.Infinity);
        sb.append(')');

        try {
			String limit = kernel.evaluateGeoGebraCAS(sb.toString()).trim();
			
			//System.err.println(sb.toString()+" = "+limit);
			
		    if (!f.CASError(limit, false)) {
		    	   	
		    	// check not duplicated
		    	sb.setLength(0);
		    	sb.append("y=");
		    	sb.append(limit);
		    	if (!SB.toString().endsWith(sb.toString())) { // not duplicated
		    	
			    	if (SB.length() > 1) SB.append(',');
			    	SB.append(sb);
		    	}
		    }
        } catch (Throwable t) {
        	// nothing to do
        }


    }
    @Override
	protected char getLabelDelimiter(){
		return isBooleanFunction()?':':'=';
	}
    /**
     * Adds vertical asymptotes to the string builder VerticalSB
     * @param f function whose asymptotes we are looking for
     * @param parentFunction parent function (in case of conditional functions)
     * @param verticalSB StringBuilder for the result
     * @param reverseCondition if true, we reverse the parent conditional function condition
     */
    protected void getVerticalAsymptotesStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder verticalSB, boolean reverseCondition) {
    	
    	String functionStr = f.getFunction().getExpression().getCASstring(kernel.getCurrentCAS(), true);
    	// solve 1/f(x) == 0 to find vertical asymptotes
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
	    
        sb.append("Solve(SimplifyFull(1/(");
        
        sb.append(functionStr);
        sb.append(")))");
        
        try {

			String verticalAsymptotes = kernel.evaluateGeoGebraCAS(sb.toString());
			
			//Application.debug("solutions: "+verticalAsymptotes);
			
	    	
	    	if (!f.CASError(verticalAsymptotes, false) && verticalAsymptotes.length() > 2) {
			
		    	verticalAsymptotes = verticalAsymptotes.replace('{',' ');
		    	verticalAsymptotes = verticalAsymptotes.replace('}',' ');
		    	//verticalAsymptotes = verticalAsymptotes.replace('(',' '); // eg (-1)
		    	//verticalAsymptotes = verticalAsymptotes.replace(')',' ');
		    	verticalAsymptotes = verticalAsymptotes.replaceAll("x==", "");
		    	verticalAsymptotes = verticalAsymptotes.replaceAll("x =", "");
		    	verticalAsymptotes = verticalAsymptotes.replaceAll("Complex(.*)", ""); // remove complex roots (MathPiper)
		    	
		    	//verticalAsymptotes = verticalAsymptotes.replaceAll("%i", ""); // remove complex roots (Maxima)

		    	String[] verticalAsymptotesArray = verticalAsymptotes.split(",");
		    	
		    	// check they are really asymptotes
		    	for (int i = 0 ; i < verticalAsymptotesArray.length ; i++) {
		    		//Application.debug(verticalAsymptotesArray[i]);
		    		boolean repeat = false;
		    		if (i > 0 && verticalAsymptotesArray.length > 1) { // check for repeats
		    			for (int j = 0  ; j < i ; j++) {
		    				if (verticalAsymptotesArray[i].equals(verticalAsymptotesArray[j])) {
		    					repeat = true;
		    					break;
		    				}
		    			}
		    		}
		    		
		    		boolean isInRange = false;
		    		try {
		    			//Application.debug(verticalAsymptotesArray[i]+"");
		    			if (verticalAsymptotesArray[i].trim().equals("")) isInRange = false; // was complex root
		    			//isInRange = parentFunction.evaluateCondition(Double.parseDouble(verticalAsymptotesArray[i]));
		    			else isInRange = parentFunction.evaluateCondition(kernel.getAlgebraProcessor().evaluateToNumeric(verticalAsymptotesArray[i], true).getDouble());
		    		} catch (Exception e) {Application.debug("Error parsing: "+verticalAsymptotesArray[i]);}
		    		if (reverseCondition) isInRange = !isInRange;
		    		
		    		if (!repeat && isInRange) {
		    		
			    		sb.setLength(0);
			            sb.append("Limit(");
			            sb.append(functionStr);
			            sb.append(",");
			            sb.append(verticalAsymptotesArray[i]);
			            sb.append(")");
	
			            try {
			     		String limit = kernel.evaluateGeoGebraCAS(sb.toString());
			            //Application.debug("checking for vertical asymptote: "+sb.toString()+" = "+limit);
			            if (limit.equals("?") || !f.CASError(limit, true)) {
			            	if (verticalSB.length() > 1) verticalSB.append(',');
	           	
			            	verticalSB.append("x=");
			            	verticalSB.append(verticalAsymptotesArray[i]);
			            }
			            } catch (Throwable e) {
			            	e.printStackTrace();
			            }
		    		}
		   		
		    	}
	    	}
	    		
	    }catch (Throwable t) { t.printStackTrace(); }
	}

	final private boolean CASError(String str, boolean allowInfinity) {
		if (str == null || str.length()==0) return true;
		if (str.equals("?")) return true; // undefined/NaN
//		if (str.indexOf("%i") > -1 ) return true; // complex answer
		str = str.toLowerCase(Locale.US);
		if (str.startsWith("'")) return true; // maxima error eg 'diff(
		if (!allowInfinity && str.indexOf(Unicode.Infinity) > -1) return true;
		if (str.length() > 6) {
			if (str.startsWith("limit")) return true;
			if (str.startsWith("solve")) return true;
			if (str.startsWith("undefined")) return true;
			//if (!allowInfinity && str.indexOf("Infinity") > -1) return true;
		}
		return false;    	
    }
	
    /**
	 * Returns a representation of geo in currently used CAS syntax.
	 * For example, "a*x^2"
	 */
	public String getCASString(boolean symbolic) {
		return fun.getExpression().getCASstring(symbolic);
	}
    
	 public String getLabelForAssignment() {
		StringBuilder sb = new StringBuilder();
		sb.append(getLabel());
		sb.append("(" );
		sb.append(fun == null ? "x" : fun.getFunctionVariable());
		sb.append(")");
		return sb.toString();
	 }
	 
	 /**
	  * Converts this function to cartesian curve and stores result to given curve
	  * @param curve Curve to be stored to
	  */
	 public void toGeoCurveCartesian(GeoCurveCartesian curve) {
		 curve.setFunctionY((Function)fun.deepCopy(kernel));
		 Function varFun = new Function(new ExpressionNode(kernel,fun.getFunctionVariable()),fun.getFunctionVariable());
		 curve.setFunctionX(varFun);
		 double min = app.getEuclidianView().getXminForFunctions();
		 double max = app.getEuclidianView().getXmaxForFunctions();
		 curve.setInterval(min, max);
	 }

	public void dilate(NumberValue r, GeoPoint S) {
		double rd=r.getNumber().getDouble(),
		a=S.x,
		b=S.y;
		if(Kernel.isZero(rd)){
			setUndefined();
			return;
		}
		FunctionVariable oldX = fun.getFunctionVariable();
		ExpressionNode newX= new ExpressionNode(kernel,
				new ExpressionNode(kernel,oldX,ExpressionNode.PLUS,new MyDouble(kernel,a*rd-a)),
				ExpressionNode.DIVIDE,
				r);
		ExpressionNode oldY = fun.getExpression().replace(oldX, newX);
		fun.setExpression(new ExpressionNode(kernel,
				new ExpressionNode(kernel,oldY,ExpressionNode.MULTIPLY,r),
				ExpressionNode.PLUS,
				new MyDouble(kernel,-b*rd+b)));
		
	}
	
	/*
	 * gets shortest distance to point p
	 * for compound paths
	 * (returns *vertical* distance for functions)
	 */
	public double distance(GeoPoint p) {
		return Math.abs(evaluate(p.inhomX) - p.inhomY);
	}

	

	public boolean isInRegion(GeoPointND P) {
		return isInRegion(P.getX2D(),P.getY2D());
	}

	public boolean isInRegion(double x0, double y0) {
		if(getVarString().equals("y"))
			return evaluateBoolean(y0);	
		return evaluateBoolean(x0);
	}

	public void pointChangedForRegion(GeoPointND PI) {
		GeoPoint P = (GeoPoint) PI;
		
		if (P.z == 1.0) {
			P.x = P.x;			
		} else {
			P.x = P.x / P.z;			
		}
				
		
		pointChangedBoolean(false,P);
		
		P.z = 1.0;
		
		// set path parameter for compatibility with
		// PathMoverGeneric
		RegionParameters pp = P.getRegionParameters();
		pp.setT1(P.x);
		pp.setT2(P.y);
		
		
	}

	public boolean isRegion(){
		return isBooleanFunction();
	}
	public void regionChanged(GeoPointND P) {
		pointChangedForRegion(P);
		
	}
	/**
	 * Reset all inequalities (slow, involves parser)
	 */
	public void resetIneqs(){
		isInequality = fun.initIneqs(getFunctionExpression(),this);
	}
	public IneqTree getIneqs() {
		if(fun.getIneqs() == null){
			isInequality = fun.initIneqs(fun.getExpression(),this);			
		}
		return fun.getIneqs();
	}

	/**
	 * For inequalities.
	 * @return true iff should be drawn on x-Axis only
	 */
	public boolean showOnAxis() {		
		return showOnAxis;
	}
	/**
	 * For inequalities.
	 * @param showOnAxis true iff should be drawn on x-Axis only
	 */
	public void setShowOnAxis(boolean showOnAxis){
		this.showOnAxis=showOnAxis;
	}
	
	public void update(){				
		super.update();
	}

}
