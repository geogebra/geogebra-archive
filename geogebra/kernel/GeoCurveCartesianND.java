package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.main.Application;


/**
 * Abstract class for cartesian curves in any dimension
 * @author matthieu
 *
 */
public abstract class GeoCurveCartesianND extends GeoElement{
	
	/** coordinates and derivative functions */
	protected Function[] fun, funD1, funD2;
	
	protected double startParam, endParam;
	

	protected boolean isDefined = true;


	/** common constructor
	 * @param c
	 */
	public GeoCurveCartesianND(Construction c) {
		super(c);
	}
	
	/** constructor with functions
	 * @param c
	 * @param fun 
	 */
	public GeoCurveCartesianND(Construction c, Function[] fun) {
		this(c);
		this.fun = fun;
		
		//sets the derivates
		funD1 = new Function[fun.length];
		for (int i=0;i<fun.length;i++){
			funD1[i]=fun[i].getDerivative(1);
		}
		
		funD2 = new Function[fun.length];
		for (int i=0;i<fun.length;i++){
			funD2[i]=fun[i].getDerivative(2);
		}
	}	
	
	/** 
	 * Sets the start and end parameter value of this curve.
	 * @param startParam 
	 * @param endParam 
	 */
	public void setInterval(double startParam, double endParam) {
		
		this.startParam = startParam;
		this.endParam = endParam;
		
		isDefined = startParam <= endParam;	
	}
	
	
	
	/**
	 * Returns the start parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		return startParam;
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		return endParam;
	}
	
	
	
	
	
	
	/**
	* returns all class-specific xml tags for getXML
	*/
	protected void getXMLtags(StringBuilder sb) {
	   super.getXMLtags(sb);
	 
	   //	line thickness and type  
	   sb.append(getLineStyleXML());	  
 
   }
	

	public boolean isPath() {
		return true;
	}
	
	

	final public boolean isDefined() {
		return isDefined;
	}

	public void setDefined(boolean defined) {
		isDefined = defined;
	}

	public void setUndefined() {
		isDefined = false;
	}

	
	
	public String toString() {
		if (sbToString == null) {
			sbToString = new StringBuilder(80);
		}
		sbToString.setLength(0);
		if (isLabelSet()) {
			sbToString.append(label);
			sbToString.append('(');
			sbToString.append(fun[0].getFunctionVariable().toString());
			sbToString.append(") = ");					
		}		
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	protected StringBuilder sbToString;
	protected StringBuilder sbTemp;
	
	

	public String toValueString() {		
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toValueString());
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}	
	
	public String toSymbolicString() {	
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append('(');
			
			for (int i=0; i< fun.length;i++){
			sbTemp.append(fun[i].toString());
			if (i<fun.length-1)
				sbTemp.append(", ");
			}
			
			sbTemp.append(')');
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");
	}
	
	public String toLaTeXString(boolean symbolic) {
		if (isDefined) {
			if (sbTemp == null) {
				sbTemp = new StringBuilder(80);
			}
			sbTemp.setLength(0);
			sbTemp.append("\\left(\\begin{array}{c}");
			
			for (int i=0; i< fun.length;i++){
				sbTemp.append(fun[i].toLaTeXString(symbolic));
				if (i<fun.length-1)
					sbTemp.append("\\\\");
				}
			
			sbTemp.append("\\end{array}\\right)");
			return sbTemp.toString();
		} else
			return app.getPlain("undefined");		
	}		
	
}
