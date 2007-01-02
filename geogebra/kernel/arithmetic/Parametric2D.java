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

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVec2D;


public interface Parametric2D {
	double getMinParameter(); 
	double getMaxParameter();	
	GeoVec2D evaluate(double t);		
	void evaluate(double t, double [] out);
	
	GeoElement toGeoElement();
}
