/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoImplicitPoly.java
 *
 * Created on 03. June 2010, 11:57
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.Polynomial;

public class GeoImplicitPoly extends GeoElement {

	private Polynomial poly;
	
	protected GeoImplicitPoly(Construction c) {
		super(c);
	}
	
	protected GeoImplicitPoly(Construction c, String label,Polynomial poly){
		this(c);
		setLabel(label);
		this.poly=poly;
	}
	
	public GeoImplicitPoly(GeoImplicitPoly g){
		this(g.cons,g.label,g.poly);
	}

	@Override
	public GeoElement copy() {
		return new GeoImplicitPoly(this);
	}

	@Override
	public int getGeoClassType() {
		return GEO_CLASS_IMPLICIT_POLY;
	}

	@Override
	protected String getTypeString() {
		return "ImplicitPoly";
	}

	@Override
	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toValueString() {
		return poly.toString()+"=0";
	}
	
	@Override
	public String toString() {
		return label+": "+toValueString();
	}

	@Override
	protected String getClassName() {
		return "GeoImplicitPoly";
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
