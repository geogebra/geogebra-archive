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
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Explicit function in multiple variables, e.g. f(a, b, c) := a^2 + b - 3c. 
 * This is actually a wrapper class for FunctionNVar
 * in geogebra.kernel.arithmetic. In arithmetic trees (ExpressionNode) it evaluates
 * to a FunctionNVar.
 * 
 * @author Markus Hohenwarter
 */
public class GeoFunctionNVar extends GeoElement
implements FunctionalNVar, CasEvaluableFunction, Region {

	private static final double STRICT_INEQ_OFFSET = 4*Kernel.MIN_PRECISION;
	private static final int SEEK_DENSITY = 30;
	protected FunctionNVar fun;
	private List<Inequality> ineqs;
	private boolean isAboveBorder;
	protected boolean isDefined = true;
	
	/** intervals for plotting, may be null (then interval is R) */
	private double[] from, to;

	public GeoFunctionNVar(Construction c) {
		super(c);
	}
	
	public GeoFunctionNVar(Construction c, FunctionNVar f) {
		this(c);
		fun = f;	
	}

	public GeoFunctionNVar(Construction c, String label, FunctionNVar f) {
		this(c,f);	
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
		initIneqs(this.getFunctionExpression());
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
		//Application.printStacktrace("");
		if (fun == null)
			return Double.NaN;
		else 
			return fun.evaluate(vals);
	}	
	
	/**
	 * Sets this function by applying a GeoGebraCAS command to a function.
	 * 
	 * @param ggbCasCmd the GeoGebraCAS command needs to include % in all places
	 * where the function f should be substituted, e.g. "Derivative(%,x)"
	 * @param f the function that the CAS command is applied to
	 */
	public void setUsingCasCommand(String ggbCasCmd, CasEvaluableFunction f, boolean symbolic){
		GeoFunctionNVar ff = (GeoFunctionNVar) f;
		
		if (ff.isDefined()) {
			fun = ff.fun.evalCasCommand(ggbCasCmd, symbolic);
			isDefined = fun != null;
		} else {
			isDefined = false;
		}		
	}
	

	
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
		return isDefined();
	}
	
	
	public String toString() {
		sbToString.setLength(0);
		if (isLabelSet() && !this.isBooleanFunction()) {
			sbToString.append(label);
			sbToString.append("(");
			sbToString.append(getVarString());
			sbToString.append(") = ");
		}else {
			sbToString.append(label);
			sbToString.append(": ");
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
				sb.append(toValueString());
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

	final public boolean isCasEvaluableFunction() {
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

	 
	 

	 
	 
	 
	 

	 
		/////////////////////////////////////////
		// INTERVALS
		/////////////////////////////////////////

	 /**
	  * return Double.NaN if none has been set
	  * @param index of parameter
	  * @return min parameter
	  */
	 public double getMinParameter(int index) {

		 if (from==null) 
			 return Double.NaN;

		 return from[index];

	 }


	 /**
	  * return Double.NaN if none has been set
	  * @param index of parameter
	  * @return max parameter
	  */
	 public double getMaxParameter(int index) {

		 if (to==null)
			 return Double.NaN;

		 return to[index];
	 }

		

		/** 
		 * Sets the start and end parameters values of this function.
		 * @param from
		 * @param to
		 */
		public void setInterval(double[] from, double[] to) {
			
			this.from = from;
			this.to =to;
			
			
			
		}
	 
		/////////////////////////////////////////
		// For 3D
		/////////////////////////////////////////
		
	 /** used if 2-var function, for plotting 
	 * @param u 
	 * @param v 
	 * @return coords of the point (u,v,f(u,v)) */
		 public GgbVector evaluatePoint(double u, double v){

			 GgbVector p = new GgbVector(3);
			 double val = fun.evaluate(new double[] {u,v});
			 p.set(1, u);
			 p.set(2, v);
			 p.set(3, val);
//			 p.set(3, Double.isNaN(val)?0:val);

			 return p;

		 }
	 


	 //will be drawn as a surface if can be interpreted as (x,y)->z function
	  	public boolean hasDrawable3D() {
			return fun.getVarNumber()==2;
		}
	  	
	  	
		public GgbVector getLabelPosition(){
			return new GgbVector(0, 0, 0, 1); //TODO
		}

	    
		/** to be able to fill it with an alpha value */
		public boolean isFillable() {
			return hasDrawable3D();
		}
		
		public List<Inequality> getIneqs() {
			if(ineqs == null)initIneqs(this.getFunctionExpression());			
			return ineqs;
		}


		private void initIneqs(ExpressionNode fe) {
			if(ineqs==null)
				ineqs = new ArrayList<Inequality>();
			int op = fe.getOperation();
			ExpressionNode leftTree = fe.getLeftTree();
			ExpressionNode rightTree = fe.getRightTree();
			if(op == ExpressionNode.GREATER||op == ExpressionNode.GREATER_EQUAL||
					op == ExpressionNode.LESS||op == ExpressionNode.LESS_EQUAL)	{
				Inequality newIneq = new Inequality(kernel,leftTree,rightTree,op,getFunction().getFunctionVariables());
				if(newIneq.getType()!=Inequality.INEQUALITY_INVALID){
					newIneq.getBorder().setInverseFill(newIneq.isAboveBorder() ^ this.isInverseFill());
					ineqs.add(newIneq);
				}
			}if(op == ExpressionNode.AND || op == ExpressionNode.OR){
				initIneqs(leftTree);
				initIneqs(rightTree);
			}
			
			
			
		}


				
		public void update(){
			super.update();
			for(Inequality ineq:getIneqs())
				ineq.updateCoef();			
		}
		public boolean isRegion() {
			return isBooleanFunction();
		}
		public boolean isInRegion(GeoPointND P) {
			P.updateCoords2D();
			return isInRegion(P.getX2D(),P.getY2D());
		}

		public boolean isInRegion(double x0, double y0) {
			return fun.evaluateBoolean(new double[] {x0,y0});
		}

		public void pointChangedForRegion(GeoPointND P) {
			if(!((GeoPoint)P).isDefined())
				return;
			RegionParameters rp = P.getRegionParameters();
			if(!isInRegion(P) && ((GeoPoint)P).isDefined()){
				double bestX = rp.getT1(), bestY = rp.getT2(), 
				myX = P.getX2D(), myY = P.getY2D();
				double bestDist = (bestY-myY)*(bestY-myY)+(bestX-myX)*(bestX-myX);
				
				if(ineqs==null)initIneqs(getFunctionExpression());
				int size = ineqs.size();
				
				for(int i = 0; i<size; i++){
					Inequality in = ineqs.get(i);
					double px=0,py=0;
					if(in.getType()==Inequality.INEQUALITY_PARAMETRIC_Y){
						px = P.getX2D();
						py = in.getFunBorder().evaluate(px);
						py += in.isAboveBorder()? STRICT_INEQ_OFFSET : -STRICT_INEQ_OFFSET;
					}
					else if(in.getType()==Inequality.INEQUALITY_PARAMETRIC_X){
						py = P.getY2D();
						px = in.getFunBorder().evaluate(py);
						px += in.isAboveBorder()? STRICT_INEQ_OFFSET : -STRICT_INEQ_OFFSET;
					}
					double myDist = (py-myY)*(py-myY)+(px-myX)*(px-myX);
					if((myDist < bestDist) && isInRegion(px,py)){
						bestDist = myDist;
						bestX = px;
						bestY = py;
					}
				}
				if(isInRegion(bestX,bestY)){
					rp.setT1(bestX);
					rp.setT2(bestY);
					((GeoPoint)P).setCoords(bestX, bestY, 1);
				}
				else tryLocateInEV(P); 
					
			}else{
				rp.setT1(P.getX2D());
				rp.setT2(P.getY2D());
			}
			
		}

	/**
	 * We seek for a point in region by desperately testing grid points
	 * in euclidian view. This should be called only when every algorithm fails.
	 * @param P
	 */
	private void tryLocateInEV(GeoPointND P) {
		EuclidianView ev = kernel.getApplication().getEuclidianView();
		boolean found = false;
		for (int i = 0; !found && i < ev.getWidth() / SEEK_DENSITY; i++)
			for (int j = 0; !found && j < ev.getHeight() / SEEK_DENSITY; j++) {
				double rx = ev.toRealWorldCoordX(SEEK_DENSITY * i);
				double ry = ev.toRealWorldCoordX(SEEK_DENSITY * i);
				if (isInRegion(rx, ry)) {
					((GeoPoint) P).setCoords(rx, ry, 1);
					//Application.debug("Desperately found"+rx+","+ry);
					found = true;
				}
			}	
		if(!found)
			((GeoPoint)P).setUndefined();
			
	}

		public void regionChanged(GeoPointND P) {
			pointChangedForRegion(P);
			
		}

		
	 
/*
		public GgbVector evaluateNormal(double u, double v){
			if (funD1 == null) {
				funD1 = new FunctionNVar[2];
				for (int i=0;i<2;i++){
					funD1[i] = fun.derivative(i, 1);
				}
			}

			
			GgbVector vec = new GgbVector(
					-funD1[0].evaluate(new double[] {u,v}),
					-funD1[1].evaluate(new double[] {u,v}),
					1,
					0).normalized();
		
			//Application.debug("vec=\n"+vec.toString());
		
			return vec;
			
			//return new GgbVector(0,0,1,0);
		}

*/
		
	 

}
