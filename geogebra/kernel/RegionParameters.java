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
 * @author Mathieu Blossier
 */
public class RegionParameters {
	
	double t1, t2;
	
	public RegionParameters() {
		this(Double.NaN,Double.NaN);
	}
	
	public RegionParameters(double t1, double t2) {
		
		setT1(t1);
		setT2(t2);

	}
	
	final public void set(RegionParameters rp) {
		setT1(rp.t1);
		setT2(rp.t2);
	}
	
	/*
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
	*/


	public final double getT1() {
		return t1;
	}

	public final void setT1(double t1) {
		this.t1 = t1;
	}
	public final double getT2() {
		return t2;
	}

	public final void setT2(double t2) {
		this.t2 = t2;
	}
}
