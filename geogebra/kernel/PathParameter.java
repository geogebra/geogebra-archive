/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
	
	final public void set(PathParameter pp) {
		t = pp.t;
		branch = pp.branch;
		pathType = pp.pathType;
	}
	
	void appendXML(StringBuffer sb) {
		// pathParameter
		sb.append("\t<pathParameter val=\"");
			sb.append(t);
		if (branch > 0) {
			sb.append("\" branch=\"");
			sb.append(branch);
		}		
		if (pathType > -1) {
			sb.append("\" type=\"");
			sb.append(pathType);
		}
		sb.append("\"/>\n");
	}

	public final int getBranch() {
		return branch;
	}

	public final void setBranch(int branch) {
		this.branch = branch;
	}

	public final int getPathType() {
		return pathType;
	}

	public final void setPathType(int pathType) {
		this.pathType = pathType;
	}

	public final double getT() {
		return t;
	}

	public final void setT(double t) {
		this.t = t;
	}
}
