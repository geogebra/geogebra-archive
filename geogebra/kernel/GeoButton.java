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
 * 
 * @author Michael
 * @version
 */
public class GeoButton extends GeoElement implements AbsoluteScreenLocateable {			

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean buttonFixed = false;
	
	public GeoButton(Construction c) {
		super(c);			
		setEuclidianVisible(true);
	}

	protected String getClassName() {
		return "GeoButton";
	}
	
    protected String getTypeString() {
		return "Button";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_JAVASCRIPT_BUTTON;
    }
    
	public GeoElement copy() {
		return this;
	}
	
	public boolean isGeoButton() {
		return true;
	}

	public void resolveVariables() {     
    }
		
	public boolean showInEuclidianView() {
		return true;
	}

	public boolean showInAlgebraView() {		
		return false;
	}
	
	public boolean isFixable() {
		return true;
	}

	public void set(GeoElement geo) {
	}

	final public void setUndefined() {
	}
	
	final public void setDefined() {
	}

	final public boolean isDefined() {
		return true;
	}			
	
	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}
	
	final public String toValueString() {
		return "";
	}
	
	final public String toString() {
		StringBuffer sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		return sbToString.toString();
	}
	
	private StringBuffer sbToString;
	private StringBuffer getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuffer();
		return sbToString;
	}

	
	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append(getXMLvisualTags(isIndependent()));
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		
		// checkbox fixed
		if (buttonFixed) {			
			sb.append("\t<checkbox fixed=\"");
			sb.append(buttonFixed);
			sb.append("\"/>\n");	
		}
		
		return sb.toString();
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
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	public void setAbsoluteScreenLoc(int x, int y) {		
		if (buttonFixed) return;
		
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

	public final boolean isButtonFixed() {
		return buttonFixed;
	}

	public final void setButtonFixed(boolean buttonFixed) {
		this.buttonFixed = buttonFixed;
	}
	
    // Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		return false;
	}
	
	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
}