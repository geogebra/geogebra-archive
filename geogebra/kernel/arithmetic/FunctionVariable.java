/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;

/**
 * @author Markus Hohenwarter
 */
public class FunctionVariable extends MyDouble {
	
	private String varStr = "x";
	
	public FunctionVariable(Kernel kernel) {
		super(kernel);
	}
	
	/**
	 * Returns true to avoid deep copies in an ExpressionNode tree.
	 */
	final public boolean isConstant() {
		return false;
	}
	
	public void setVarString(String varStr) {
		this.varStr = varStr;
	}		
	
	final public String toString() {
		return varStr;
	}

}
