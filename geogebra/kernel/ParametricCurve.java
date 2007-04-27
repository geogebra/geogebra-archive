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

import geogebra.kernel.roots.RealRootFunction;

import java.awt.Color;


public interface ParametricCurve extends Traceable {
	double getMinParameter(); 
	double getMaxParameter();	
	
	RealRootFunction getRealRootFunctionX();
	RealRootFunction getRealRootFunctionY();
			
	void evaluateCurve(double t, double [] out);
	GeoVec2D evaluateCurve(double t);	

//	Victor Franco 25-04-2007

	/*
	 * Evaluate Curve or Function and return 
	 * a Color depending on the value of curvature
	 */
	public Color evaluateColorCurvature(double t, double[] out);

    //Victor Franco 25-04-2007
	GeoElement toGeoElement();
}
