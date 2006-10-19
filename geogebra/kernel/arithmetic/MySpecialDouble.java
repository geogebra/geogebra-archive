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
public class MySpecialDouble extends MyDouble {
	
	private String strToString;
	
	public MySpecialDouble(Kernel kernel, double val, String strToString) {
		super(kernel, val);
		this.strToString = strToString;
	}
	
	public String toString() {
		return strToString;		 
	}

}
