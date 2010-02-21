/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.cas;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.Variable;
import geogebra.kernel.parser.Parser;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser {
	
	private GeoGebraCAS ggbCAS;
	private Kernel kernel;
	private Parser ggbParser;
	
	public CASparser(GeoGebraCAS ggbCAS) {	
		this.ggbCAS = ggbCAS;
		this.kernel = ggbCAS.getKernel();
		ggbParser = kernel.getParser();
	}
	
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @throws Throwable when something goes wrong
	 */
	public ValidExpression parseGeoGebraCASInput(String exp) throws Throwable {
		return ggbParser.parseGeoGebraCAS(exp);
	}
	
	/**
	 * Resolves all variables in ValidExpression. Unknown variables are
	 * kept as symbolic variables.
	 */
	public synchronized void resolveVariablesForCAS(ExpressionValue ev) {
		// resolve variables of valid expression
		kernel.setResolveVariablesForCASactive(true);
		ev.resolveVariables();
		kernel.setResolveVariablesForCASactive(false);				
	}
	
	/**
	 * Returns the given expression as a string in MathPiper syntax.
	 */
	public String toMathPiperString(ExpressionValue ev, boolean substituteVariables) {
		String MathPiperString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		MathPiperString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_MATH_PIPER, !substituteVariables);		
				
		return MathPiperString;
	}
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public ValidExpression parseMathPiper(String MathPiperString) throws Throwable {
		return ggbParser.parseMathPiper(MathPiperString);		
	}
	
	/**
	 * Tries to convert the given MathPiper string to GeoGebra syntax.
	 */
	public String toGeoGebraString(ExpressionValue ev) throws Throwable {
		String GeoGebraString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		ExpressionNode en = (ExpressionNode) ev;
		GeoGebraString = en.getCASstring(ExpressionNode.STRING_TYPE_GEOGEBRA, true);		
		return GeoGebraString;
	}


}
