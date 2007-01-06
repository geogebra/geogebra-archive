/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
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
