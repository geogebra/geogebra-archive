/* 
 GeoGebra - Dynamic Geometry and Algebra
 Copyright Markus Hohenwarter, http://www.geogebra.at

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation; either version 2 of the License, or 
 (at your option) any later version.
 */

/*
 * GeoNumeric.java
 *
 * Created on 18. September 2001, 12:04
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoNumeric extends GeoElement 
implements NumberValue,  AbsoluteScreenLocateable, GeoFunctionable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int DEFAULT_SLIDER_WIDTH_RW = 4;
	private static int DEFAULT_SLIDER_WIDTH_PIXEL = 100;	
	double DEFAULT_SLIDER_MIN = -5;
	double DEFAULT_SLIDER_MAX = 5;	

	protected double value;

	private boolean isDrawable = false;
	

	private int slopeTriangleSize = 1;

	// for slider	
	private boolean intervalMinActive = false;
	private boolean intervalMaxActive = false;
	private double intervalMin = Double.NEGATIVE_INFINITY;
	private double intervalMax = Double.POSITIVE_INFINITY; 
	private double sliderWidth = DEFAULT_SLIDER_WIDTH_PIXEL;
	private double sliderX, sliderY;
	private boolean sliderFixed = false;
	private boolean sliderHorizontal = true;
	
	// for absolute screen location
	boolean hasAbsoluteScreenLocation = true;	

	/** Creates new GeoNumeric */
	public GeoNumeric(Construction c) {
		super(c);
		setEuclidianVisible(false);
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
		animationStep = 0.1;					
	}

	String getClassName() {
		return "GeoNumeric";
	}
	
    String getTypeString() {
		return "Numeric";
	}

	public GeoNumeric(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}

	public GeoNumeric(Construction c, double x) {
		this(c);
		value = x;
	}

	public GeoElement copy() {
		return new GeoNumeric(cons, value);
	}
	
	public boolean isDrawable() {
		return isDrawable || isIndependent();
	}

	public boolean isFillable() {
		return isDrawable;
	}

	public void setDrawable(boolean flag) {
		isDrawable = flag;
		
		if (isDrawable && kernel.isNotifyViewsActive() ) {
			//System.out.println("side effect for: " + this);
			setEuclidianVisible(true);
		}						
	}
	
	public void setEuclidianVisible(boolean visible) {
		// slider is only possible for independent
		// number with given min and max
		if (isIndependent()) {
			if (visible) {				
				if (!intervalMinActive) {
					if (!intervalMaxActive) {
						// set both to default
						double min = Math.min(DEFAULT_SLIDER_MIN, Math.floor(value));
						double max = Math.max(DEFAULT_SLIDER_MAX, Math.ceil(value));
						setIntervalMin(min);
						setIntervalMax(max);					
					} else {
						// max is available but no min
						double min = Math.min(DEFAULT_SLIDER_MIN, Math.floor(value));
						setIntervalMin(min);				
					}
				}
				else { // min exists
					if (!intervalMaxActive) {
						//	min is available but no max
						double max = Math.max(DEFAULT_SLIDER_MAX, Math.ceil(value));
						setIntervalMax(max);					
					}
				}			
				
				// init screen location
				if (sliderX == 0 && sliderY ==0) {				
					Iterator it = cons.getAllGeoElementsIterator();
					int count = 0;
					while (it.hasNext()) {
						GeoElement ob = (GeoElement) it.next();
			    		if (ob.isGeoNumeric()) {
							GeoNumeric num = (GeoNumeric) ob;
							if (num.isIndependent() && num.isEuclidianVisible())
								count++;
			    		}
					}
					
					if (isAbsoluteScreenLocActive()) {
						sliderX = 30;
						sliderY = 50 + 40 * count;
						// make sure slider is visible on screen
						sliderY = (int) sliderY / 400 * 10 + sliderY % 400;
					} else {
						sliderX = -5;
						sliderY = 10 - count;
					}
				}
			}
			
			/* we don't want to remove min, max values when slider is hidden			
			else { // !visible
				intervalMinActive = false;
				intervalMaxActive = false;
			}*/
		} 
		
		super.setEuclidianVisible(visible);
	}

	public boolean showInEuclidianView() {
		return isDrawable() && isDefined() && !isInfinite();
	}

	final boolean showInAlgebraView() {
		// independent or defined
		return isIndependent() || isDefined();
	}

	public void set(GeoElement geo) {
		NumberValue num = (NumberValue) geo;
		setValue(num.getDouble());
	}

	final public void setUndefined() {
		value = Double.NaN;
	}

	final public boolean isDefined() {
		return !Double.isNaN(value);
	}

	final public boolean isFinite() {
		return isDefined() && !isInfinite();
	}

	final public boolean isInfinite() {
		return Double.isInfinite(value);
	}

	final public boolean equals(GeoNumeric n) {
		return kernel.isEqual(value, n.value);
	}

	public void setValue(double x) {
		if (intervalMinActive && x < intervalMin) {	
			value = intervalMin;
		}			
		else if (intervalMaxActive && x > intervalMax) {
			value = intervalMax;
		}			
		else 
			value = x;
	}

	final public void setValue(MyDouble x) {
		setValue(x.getDouble());
	}

	final public double getValue() {
		return value;
	}

	// dummy implementation of mode
	final public void setMode(int mode) {
	}

	final public int getMode() {
		return -1;
	}

	final public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());
		return sbToString.toString();
	}

	private StringBuffer sbToString = new StringBuffer(50);

	public String toValueString() {
		return kernel.format(value);
	}

	/**
	 * interface NumberValue
	 */
	public MyDouble getNumber() {
		return new MyDouble(kernel, value);
	}

	final public double getDouble() {
		return value;
	}

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

	public void setAllVisualProperties(GeoElement geo) {
		super.setAllVisualProperties(geo);
		
		if (geo instanceof GeoNumeric) {
			isDrawable = ((GeoNumeric) geo).isDrawable;
		}
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoNumeric()) {
			slopeTriangleSize = ((GeoNumeric) geo).slopeTriangleSize;
		}
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"/>\n");
		
		//	colors
		sb.append(getXMLvisualTags());

		//	if number is drawable then we need to save visual options too
		if (isDrawable || isSliderable()) {
			// save slider info before show to have min and max set
			// before setEuclidianVisible(true) is called
			sb.append(getXMLsliderTag());						
			
			//	line thickness and type
			sb.append(getLineStyleXML());

			// for slope triangle
			if (slopeTriangleSize > 1) {
				sb.append("\t<slopeTriangleSize val=\"");
				sb.append(slopeTriangleSize);
				sb.append("\"/>\n");
			}						
		}
		sb.append(getXMLanimationTags());
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		sb.append(getBreakpointXML());
		return sb.toString();
	}
	
	protected boolean isSliderable() {
		return isIndependent() && intervalMinActive || intervalMaxActive;
	}
	
	String getXMLsliderTag() {
		if (!isSliderable())
			return "";
		
		StringBuffer sb = new StringBuffer();		
		sb.append("\t<slider");
		if (intervalMinActive) {
			sb.append(" min=\"");
			sb.append(intervalMin);
			sb.append("\"");
		}
		if (intervalMinActive) {
			sb.append(" max=\"");
			sb.append(intervalMax);
			sb.append("\"");
		}
		
		if (hasAbsoluteScreenLocation) {
			sb.append(" absoluteScreenLocation=\"true\"");
		}
		
		sb.append(" width=\"");
		sb.append(sliderWidth);
		sb.append("\" x=\"");
		sb.append(sliderX);
		sb.append("\" y=\"");
		sb.append(sliderY);
		sb.append("\" fixed=\"");
		sb.append(sliderFixed);
		sb.append("\" horizontal=\"");
		sb.append(sliderHorizontal);
		sb.append("\"/>\n");		
		return sb.toString();
	}

	public boolean isNumberValue() {
		return true;
	}
	
	public boolean isGeoNumeric() {
		return true;
	}
	

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	/**
	 * @return
	 */
	final public int getSlopeTriangleSize() {
		return slopeTriangleSize;
	}

	/**
	 * @param i
	 */
	public void setSlopeTriangleSize(int i) {
		slopeTriangleSize = i;
	}

	public boolean isTextValue() {
		return false;
	}
	
	public void setIntervalMax(double max) {	
		if (Double.isNaN(max) || Double.isInfinite(max) ||
            intervalMinActive && max <= intervalMin) return;
				
		intervalMax = max;
		intervalMaxActive = true;
		setValue(value);			
	}
	
	public void setIntervalMin(double min) {
		if (Double.isNaN(min) || Double.isInfinite(min) ||
	            intervalMaxActive && min >= intervalMax) return;

		intervalMin = min;
		intervalMinActive = true;		
		setValue(value);			
	}
	
	public final void setSliderWidth(double width) {
		if (width > 0 && !Double.isInfinite(width))
			sliderWidth = width;
	}
	
	/**
	 * Sets the location of the slider for this number.
	 * @param x, y: coords of the slider
	 */
	public final void setSliderLocation(double x, double y) {
		if (sliderFixed) return;
		sliderX = x;
		sliderY = y;			
	}
	
	public final double getIntervalMax() {
		return intervalMax;
	}

	public final double getIntervalMin() {
		return intervalMin;
	}

	public final double getSliderWidth() {
		return sliderWidth;
	}
	
	public final double getSliderX() {
		return sliderX;
	}

	public final double getSliderY() {
		return sliderY;
	}
	

	public final boolean isIntervalMaxActive() {
		return intervalMaxActive;
	}
	public final boolean isIntervalMinActive() {
		return intervalMinActive;
	}
	public final void setIntervalMaxInactive() {
		intervalMaxActive = false;
		setEuclidianVisible(false);
	}
	public final void setIntervalMinInactive() {
		intervalMinActive = false;
		setEuclidianVisible(false);
	}
	
	public final boolean isSliderFixed() {
		return sliderFixed;
	}
	
	public final void setSliderFixed(boolean isSliderFixed) {
		sliderFixed = isSliderFixed;
	}

	public final boolean isSliderHorizontal() {
		return sliderHorizontal;
	}

	public void setSliderHorizontal(boolean sliderHorizontal) {
		this.sliderHorizontal = sliderHorizontal;
	}
	
	
	public void setAbsoluteScreenLoc(int x, int y) {
		setSliderLocation(x, y);		
	}

	public int getAbsoluteScreenLocX() {	
		return (int) sliderX;
	}

	public int getAbsoluteScreenLocY() {		
		return (int) sliderY;
	}
	
	public void setRealWorldLoc(double x, double y) {
		sliderX = x;
		sliderY = y;
	}
	
	public double getRealWorldLocX() {
		return sliderX;
	}
	
	public double getRealWorldLocY() {
		return sliderY;
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		hasAbsoluteScreenLocation = flag;			
		if (flag)
			sliderWidth = DEFAULT_SLIDER_WIDTH_PIXEL;
		else 
			sliderWidth = DEFAULT_SLIDER_WIDTH_RW;
	}

	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocSetable() {
		return isSliderable();
	}
	
	/**
	 * Creates a GeoFunction of the form f(x) = thisNumber 
	 * @return
	 */	
	public GeoFunction getGeoFunction() {
		ExpressionNode en = new ExpressionNode(kernel, this);
		Function fun = new Function(en, new FunctionVariable(kernel));			
		GeoFunction ret;
		
		// we get a dependent function if this number has a label
		if (isLabelSet()) {
			// don't create a label for the new dependent function
			boolean oldMacroMode = cons.isInMacroMode();
			cons.setMacroMode(true);
			ret = kernel.DependentFunction(null, fun);
			cons.setMacroMode(oldMacroMode);
		} else {
			ret = new GeoFunction(cons);
			ret.setFunction(fun);
		}					
				
		return ret;
	}
	
	public boolean isGeoFunctionable() {
		return true;
	}

}