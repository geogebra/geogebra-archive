/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.Functional;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;

/**
 * Explicit function in one variable ("x"). This is actually a wrapper class for Function
 * in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a Function.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunction extends GeoElement
implements Path, Translateable, Traceable, Functional, GeoFunctionable,
GeoDeriveable, ParametricCurve, LineProperties, RealRootFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Function fun;		
	protected boolean isDefined = true;
	public boolean trace, spreadsheetTrace;	
	
	// if the function includes a division by var, e.g. 1/x, 1/(2+x)
    private boolean includesDivisionByVar = false;
    
    //  function may be limited to interval [a, b] 
    protected boolean interval = false; 
    protected double intervalMin, intervalMax; // interval borders 
    
    // parent conditional function
   // private GeoFunctionConditional parentCondFun = null;
    
	
//	Victor Franco Espino 25-04-2007
	/*
	 * Parameter in dialog box for adjust color of curvature
	 */
	double CURVATURE_COLOR = 15;//optimal value 

    //Victor Franco Espino 25-04-2007

	public GeoFunction(Construction c) {
		super(c);
	}

	public GeoFunction(Construction c, String label, Function f) {
		super(c);
		fun = f;		
		setLabel(label);		
	}
	
	protected String getClassName() {
		return "GeoFunction";
	}
	
	protected String getTypeString() {
		return "Function";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_FUNCTION;
    }

	/** copy constructor */
	public GeoFunction(GeoFunction f) {
		super(f.cons);
		set(f);
	}

	public GeoElement copy() {
		return new GeoFunction(this);
	}

	public void set(GeoElement geo) {
		GeoFunction geoFun = (GeoFunction) geo;				
						
		if (geo == null || geoFun.fun == null) {
			fun = null;
			isDefined = false;
			return;
		} else {
			isDefined = geoFun.isDefined;
			fun = new Function(geoFun.fun, kernel);
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
	

	public void setFunction(Function f) {
		fun = f;
	}
			
	final public Function getFunction() {
		return fun;
	}	
	
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
	 * Returns the corresponding Function for the given x-value.
	 * This is important for conditional functions where we have
	 * two differen Function objects.
	 * @param startValue
	 * @return
	 */
	public Function getFunction(double x) {
		return fun;
	}
	
	/**
	 * Set this function to the n-th derivative of f
	 * @param f
	 * @param order
	 */
	public void setDerivative(GeoDeriveable fd, int n) {
		GeoFunction f = (GeoFunction) fd;
		
		if (f.isDefined()) {
			fun = f.fun.getDerivative(n);
			isDefined = fun != null;
		} else {
			isDefined = false;
		}		
	}
	
	/**
	 * Set this function to the integral of f
	 * @param f
	 */
	public void setIntegral(GeoFunction f) {
		if (f.isDefined()) {
			fun = f.fun.getIntegral();	
		} else {
			isDefined = false;
		}	
	}
	
	/**
	 * Set this function to the expanded version of f, e.g. 3*(x-2) is expanded to 3*x - 6.
	 */
	public void setExpanded(GeoFunction f) {
		if (f.isDefined()) {
			fun = f.fun.getExpanded();	
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
	
	public final double getIntervalMin() {
		return intervalMin;
	}

	public final double getIntervalMax() {
		return intervalMax;
	}
	
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
	final public void translate(GeoVector v) {
		translate(v.x, v.y);
	}
	
	final public boolean isTranslateable() {
		return fun != null && !isBooleanFunction();
	}
	
	public void translate(double vx, double vy) {
		fun.translate(vx, vy);
	}

	public void setMode(int mode) {
		// dummy
	}

	public int getMode() {
		// dummy
		return -1;
	}
	
	/**
	 * Returns true if this function is a polynomial.
	 * 
	 * @param forRootFinding: set to true if you want to allow
	 * functions that can be factored into polynomial factors
	 * for root finding (e.g. sqrt(x) could be replaced by x)
	 * @param symbolic: function's symbolic expression must be a polynomial,
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
	
	public boolean isPolynomialFunction(boolean forRootFinding) {
		return isPolynomialFunction(forRootFinding, false);
	}
	
	/**
     * Returns whether this function includes a division by variable,
     * e.g. f(x) = 1/x, 1/(2+x), sin(3/x), ...
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
	private StringBuilder sbToString = new StringBuilder(80);
	
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
		 // e.g. f(x) = x� - 3x
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
	   sb.append(getLineStyleXML());	  

   }

	/* 
	 * Path interface
	 */	 
	public void pointChanged(GeoPointInterface PI) {			
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.z == 1.0) {
			P.x = P.x;			
		} else {
			P.x = P.x / P.z;			
		}
				
		if (interval) {
			//	don't let P move out of interval			
			if (P.x < intervalMin) 
				P.x = intervalMin;
			else if (P.x > intervalMax) 
				P.x = intervalMax;
		}
		
		P.y = fun.evaluate(P.x);
		P.z = 1.0;
		
		// set path parameter for compatibility with
		// PathMoverGeneric
		PathParameter pp = P.getPathParameter();
		pp.t = P.x;
	}
	
	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		return isDefined &&
			Math.abs(fun.evaluate(P.inhomX) - P.inhomY) <= eps;
	}

	public void pathChanged(GeoPointInterface PI) {
		
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
	 * @return
	 */
	public double getMinParameter() {
		return kernel.getXmin();
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		return kernel.getXmax();
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}
	
	public boolean isClosedPath() {
		return false;
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

	public void setSpreadsheetTrace(boolean spreadsheetTrace) {
		this.spreadsheetTrace = spreadsheetTrace;
	}

	public boolean getSpreadsheetTrace() {
		return spreadsheetTrace;
	}
	
	
	
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

	
	public void evaluateCurve(double t, double[] out) {
		out[0] = t;
		out[1] = evaluate(t);		
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

	public boolean isGeoDeriveable() {
		return true;
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

	public boolean isVector3DValue() {
		return false;
	}
	
	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public boolean evaluateCondition(double x) {
		if (!interval) return true;
		return x > intervalMin && x < intervalMax;
	}
	
	public double getLimit(double x, int direction) {
   	String functionIn = getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
	    
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
	    sb.setLength(0);
        sb.append("Limit(x,");
        sb.append(x+"");
        switch (direction) {
        case -1:
            sb.append(",Right)");
        	break;
        case 1:
            sb.append(",Left)");
       	break;
        case 0:
            sb.append(')');
       	break;
        }
        sb.append(functionIn);
		String functionOut = evaluateMathPiper(sb.toString());
		
		try {
			NumberValue nv = kernel.getAlgebraProcessor().evaluateToNumeric(functionOut);
			return nv.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		} catch (Throwable e) {
			e.printStackTrace();
			return Double.NaN;
	}
	}

	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public void getVerticalAsymptotes(GeoFunction f, StringBuilder verticalSB, boolean reverse) {
		getVerticalAsymptotesStatic(this, f, verticalSB, reverse);
	}
	
	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public void getHorizontalPositiveAsymptote(GeoFunction f, StringBuilder SB) {
		getHorizontalAsymptoteStatic(this, f, SB, true);		
	}
	
	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public void getHorizontalNegativeAsymptote(GeoFunction f, StringBuilder SB) {
		getHorizontalAsymptoteStatic(this, f, SB, false);		
	}
	
	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public void getDiagonalPositiveAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, true);		
	}
	
	/* over-ridden in GeoFunctionConditional
	 * 
	 */
	public void getDiagonalNegativeAsymptote(GeoFunction f, StringBuilder SB) {
		getDiagonalAsymptoteStatic(this, f, SB, false);		
	}
	
	private static StringBuilder sb;

    protected static void getDiagonalAsymptoteStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder SB, boolean positiveInfinity) {
    	String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
	    
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
        sb.append("Simplify(Differentiate(x)");
        sb.append(functionIn);
        sb.append(')');
		String firstDerivative = f.evaluateMathPiper(sb.toString());
		
		if (!f.mathPiperError(firstDerivative, false)) {
	
		
			String gradientStrMinus="";
			String interceptStrMinus="";
			
			{
				
				sb.setLength(0);
		        sb.append("Limit(x,");
		        if (!positiveInfinity) sb.append('-'); // -Infinity
		        sb.append("Infinity)");
		        sb.append(firstDerivative);
				gradientStrMinus = f.evaluateMathPiper(sb.toString());
				
				if (!f.mathPiperError(gradientStrMinus, false) && !gradientStrMinus.equals("0")) {
					sb.setLength(0);
			        sb.append("Limit(x,");
			        if (!positiveInfinity) sb.append('-'); // -Infinity
			        sb.append("Infinity)Simplify(");
			        sb.append(functionIn);
			        sb.append("-");
			        sb.append(gradientStrMinus);
			        sb.append("*x)");
					interceptStrMinus = f.evaluateMathPiper(sb.toString());
					
					if (!f.mathPiperError(interceptStrMinus, false)) {
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

    }
    
    protected static void getHorizontalAsymptoteStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder SB, boolean positiveInfinity) {
    	String functionStr = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
        sb.append("Limit(x,");
        if (!positiveInfinity) sb.append('-'); // -Infinity
        sb.append("Infinity)");
        sb.append(functionStr);
		String limit = f.evaluateMathPiper(sb.toString()).trim();
		
		//System.err.println(sb.toString()+" = "+limit);
		
	    if (!f.mathPiperError(limit, false)) {
	    	
	    	// check not duplicated
	    	sb.setLength(0);
	    	sb.append("y=");
	    	sb.append(limit);
	    	if (!SB.toString().endsWith(sb.toString())) { // not duplicated
	    	
		    	if (SB.length() > 1) SB.append(',');
		    	SB.append(sb);
	    	}
	    }


    }
    
    protected static void getVerticalAsymptotesStatic(GeoFunction f, GeoFunction parentFunction, StringBuilder verticalSB, boolean reverseCondition) {
    	
    	String functionStr = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);
    	// solve 1/f(x) == 0 to find vertical asymptotes
    	if (sb == null) sb = new StringBuilder();
    	else sb.setLength(0);
	    
        sb.append("Solve(Simplify(1/(");
        
        sb.append(functionStr);
        sb.append("))==0,x)");
		String verticalAsymptotes = f.evaluateMathPiper(sb.toString());
		
		//Application.debug("solutions: "+verticalAsymptotes);
		
    	
    	if (!f.mathPiperError(verticalAsymptotes, false) && verticalAsymptotes.length() > 2) {
		
	    	verticalAsymptotes = verticalAsymptotes.replace('{',' ');
	    	verticalAsymptotes = verticalAsymptotes.replace('}',' ');
	    	verticalAsymptotes = verticalAsymptotes.replace('(',' '); // eg (-1)
	    	verticalAsymptotes = verticalAsymptotes.replace(')',' ');
	    	verticalAsymptotes = verticalAsymptotes.replaceAll("x==", "");
	    	String[] verticalAsymptotesArray = verticalAsymptotes.split(",");
	    	
	    	// check they are really asymptotes
	    	for (int i = 0 ; i < verticalAsymptotesArray.length ; i++) {
	    		
	    		boolean repeat = false;
	    		if (i > 0) { // check for repeats
	    			for (int j = i ; j < verticalAsymptotesArray.length ; i++) {
	    				if (verticalAsymptotesArray[i].equals(verticalAsymptotesArray[j])) {
	    					repeat = true;
	    					break;
	    				}
	    			}
	    		}
	    		
	    		boolean isInRange = false;
	    		try {
	    			isInRange = parentFunction.evaluateCondition(Double.parseDouble(verticalAsymptotesArray[i]));
	    		} catch (Exception e) {Application.debug("Error parsing: "+verticalAsymptotesArray[i]);}
	    		if (reverseCondition) isInRange = !isInRange;
	    		
	    		if (!repeat && isInRange) {
	    		
		    		sb.setLength(0);
		            sb.append("Limit(x,");
		            sb.append(verticalAsymptotesArray[i]);
		            sb.append(",Left)");
		            sb.append(functionStr);
		     		String limit = f.evaluateMathPiper(sb.toString());
		            //Application.debug("checking for vertical asymptote: "+sb.toString()+" = "+limit);
		            if (!f.mathPiperError(limit, true)) {
		            	if (verticalSB.length() > 1) verticalSB.append(',');
           	
		            	verticalSB.append("x=");
		            	verticalSB.append(verticalAsymptotesArray[i]);
		            }
	    		}
	   		
	    	}
	
			//Application.debug("verticalAsymptotes: "+verticalSB);
		}
	}

	final private boolean mathPiperError(String str, boolean allowInfinity) {
		if (str == null || str.length()==0) return true;
		if (str.length() > 6) {
			if (str.startsWith("Limit")) return true;
			if (str.startsWith("Solve")) return true;
			if (str.startsWith("Undefined")) return true;
			if (!allowInfinity && str.indexOf("Infinity") > -1) return true;
		}
		return false;    	
    }
    
   
    private String evaluateMathPiper(String exp) {
    	return kernel.evaluateMathPiper(exp);

    	/*
    	// workaround for http://code.google.com/p/mathpiper/issues/detail?id=35
    	// TODO remove when fixed
    	if (ret != null) ret = ret.replaceAll("else", " else ");
    	
    	// *partial* workaround for MathPiper bug
    	// http://code.google.com/p/mathpiper/issues/detail?id=31
    	if (ret == null || ret.indexOf("else") == -1) return ret;
    	
    	String str[] = ret.split("else");
    	if (str.length != 2) return "Undefined";
    	
    	if (str[0].equals(str[1])) {
    		//System.err.println("Manually simplifying "+ret+" to "+str[1]);
    		return str[0]; // eg 0else0 returned
    	}
    	
    	return ret; // might be error or valid expression */
    	
    }

}
