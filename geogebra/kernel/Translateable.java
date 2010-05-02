/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.Matrix.GgbVector;

public interface Translateable {
	public void translate(GgbVector v);
	public GeoElement toGeoElement();
	public boolean isTranslateable();
}
