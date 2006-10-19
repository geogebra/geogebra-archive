/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.util;

public class MyPoint {
	public double x, y;
	public boolean lineTo;
	
	public MyPoint(double x, double y, boolean lineTo) {
		this.x = x;
		this.y = y;
		this.lineTo = lineTo;
	}
}
