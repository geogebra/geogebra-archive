/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawImplicitPoly.java
 *
 * Created on 03. June 2010, 12:21
 */
package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImplicitPoly;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class DrawImplicitPoly extends Drawable {
	
	private GeoImplicitPoly implicitPoly;
	
	public DrawImplicitPoly(EuclidianView view,GeoImplicitPoly implicitPoly) {
		this.view=view;
		this.implicitPoly = implicitPoly;
		this.geo=implicitPoly;
		update();
	}

	@Override
	public void draw(Graphics2D g2) {
		// TODO Auto-generated method stub

	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		if (geo instanceof GeoImplicitPoly){
			implicitPoly=(GeoImplicitPoly) geo;
			this.geo=geo;
		}
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
