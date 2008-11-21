/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

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
import geogebra.kernel.parser.Parser;

/**
 * Handles parsing and evaluating of input in the CAS view.
 * 
 * @author Markus Hohenwarter
 */
public class CASparser {
	
	private Kernel kernel;
	private Parser parser;
	
	public CASparser(Kernel kernel) {	
		this.kernel = kernel;
		parser = kernel.getParser();
	}
	
	/**
	 * Parses the given expression and returns it as a ValidExpression.
	 * @throws Throwable when something goes wrong
	 */
	public ValidExpression parseForCAS(String exp) throws Throwable {
		return parser.parse(exp);
	}
	
	/**
	 * Resolves all variables in ValidExpression. Unknown variables are
	 * kept as symbolic variables.
	 */
	public void resolveVariablesForCAS(ExpressionValue ev) {
		// resolve variables of valid expression
		kernel.setResolveUnkownVarsAsDummyGeos(true);		
		ev.resolveVariables();
		kernel.setResolveUnkownVarsAsDummyGeos(false);				
	}
	
	/**
	 * Returns the given expression as a string in Yacas syntax.
	 */
	public String toYacasString(ExpressionValue ev, boolean substituteVariables) {
		String yacasString;
		
		if (!ev.isExpressionNode()) {
			ev = new ExpressionNode(kernel, ev);			
		}
		
		yacasString = ((ExpressionNode) ev).getCASstring(ExpressionNode.STRING_TYPE_YACAS, !substituteVariables);		
		return yacasString;
	}
	

	
	
}
