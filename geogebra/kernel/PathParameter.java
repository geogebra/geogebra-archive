/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel;

/**
 * @author Markus Hohenwarter
 */
public class PathParameter {
	
	double t;
	int branch = -1;
	int pathType = -1;
	
	public PathParameter() {
		t = Double.NaN;
		branch = 0;
	}
	
	public PathParameter(double t) {
		this.t = t;
		branch = 0;
	}
	
	public void set(PathParameter pp) {
		t = pp.t;
		branch = pp.branch;
		pathType = pp.pathType;
	}
}
