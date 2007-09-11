/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

/**
 * Interface for GeoElements that have an absolute screen position
 * (GeoImage, GeoText, GeoNumeric)
 */
public interface AbsoluteScreenLocateable {
	public void setAbsoluteScreenLoc(int x, int y);	
	public int getAbsoluteScreenLocX();
	public int getAbsoluteScreenLocY();
	
	public void setRealWorldLoc(double x, double y);	
	public double getRealWorldLocX();	
	public double getRealWorldLocY();
	
	public void setAbsoluteScreenLocActive(boolean flag);
	public boolean isAbsoluteScreenLocActive();

	public boolean isAbsoluteScreenLocateable();
	public GeoElement toGeoElement();
}
