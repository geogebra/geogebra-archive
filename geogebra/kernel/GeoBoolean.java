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

import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyBoolean;

import java.util.HashSet;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoBoolean extends GeoElement implements BooleanValue,
AbsoluteScreenLocateable {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean value;
	private boolean isDefined = true;
		
	public GeoBoolean(Construction c) {
		super(c);				
	}

	String getClassName() {
		return "GeoBoolean";
	}
	
    String getTypeString() {
		return "Boolean";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_BOOLEAN;
    }
    
    public void setValue(boolean val) {
    	value = val;
    }
    
    final public boolean getBoolean() {
    	return value;
    }
    
    final public MyBoolean getMyBoolean() {
    	return new MyBoolean(value);
    }

	public GeoElement copy() {
		GeoBoolean ret = new GeoBoolean(cons);
		ret.setValue(value);		
		return ret;
	}
	
	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return false;
	}

	final boolean showInAlgebraView() {		
		//return isDefined;
		return true;
	}

	public void set(GeoElement geo) {
		GeoBoolean b = (GeoBoolean) geo;
		setValue(b.value);
		isDefined = b.isDefined;
	}

	final public void setUndefined() {
		isDefined = false;
	}
	
	final public void setDefined() {
		isDefined = true;
	}

	final public boolean isDefined() {
		return isDefined;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	final public String toValueString() {
		return value ? "true" : "false";
	}
	
	final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());
		return sbToString.toString();
	}
	private StringBuffer sbToString = new StringBuffer(20);

	/**
	 * interface BooleanValue
	 */
	final public boolean isConstant() {
		return false;
	}

	final public boolean isLeaf() {
		return true;
	}

	final public HashSet getVariables() {
		HashSet varset = new HashSet();
		varset.add(this);
		return varset;
	}

	final public ExpressionValue evaluate() {
		return this;
	}		

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	String getXMLtags() {
		// TODO: implement XML saving
		StringBuffer sb = new StringBuffer();
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"/>\n");

		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		return sb.toString();
	}	

	public boolean isBooleanValue() {
		return true;
	}
	
	public boolean isGeoBoolean() {
		return true;
	}	

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {
		return false;
	}

	

	public double getRealWorldLocX() {
		return 0;
	}

	public double getRealWorldLocY() {		
		return 0;
	}

	public boolean isAbsoluteScreenLocActive() {		
		return true;
	}

	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {				
	}

	public void setRealWorldLoc(double x, double y) {				
	}		

}