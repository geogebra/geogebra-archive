/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * GeoAngle.java
 *
 * The toString() depends on the kernels angle unit state (DEGREE or RADIANT)
 *
 * Created on 18. September 2001, 12:04
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.MyDouble;

/**
 * 
 * @author Markus
 * @version
 */
public final class GeoAngle extends GeoNumeric {

	private static final long serialVersionUID = 1L;

	//public int arcSize = EuclidianView.DEFAULT_ANGLE_SIZE;
	public int arcSize;

	// allow angle > pi
	// private boolean allowReflexAngle = true;

	// shows whether the current value was changed to (2pi - value)
	// private boolean changedReflexAngle;

	// states whether a special right angle appearance should be used to draw
	// this angle
	private boolean emphasizeRightAngle = true;

	// Michael Borcherds 2007-10-20
	private double rawValue;

	final public static int ANGLE_ISANTICLOCKWISE = 0; // old
														// allowReflexAngle=true

	final public static int ANGLE_ISCLOCKWISE = 1;

	final public static int ANGLE_ISNOTREFLEX = 2; // old allowReflexAngle=false

	final public static int ANGLE_ISREFLEX = 3;

	private int angleStyle = ANGLE_ISANTICLOCKWISE;

	// added by Loïc
	public static final Integer[] getDecoTypes() {
		Integer[] ret = { new Integer(GeoElement.DECORATION_NONE),
				new Integer(GeoElement.DECORATION_ANGLE_TWO_ARCS),
				new Integer(GeoElement.DECORATION_ANGLE_THREE_ARCS),
				new Integer(GeoElement.DECORATION_ANGLE_ONE_TICK),
				new Integer(GeoElement.DECORATION_ANGLE_TWO_TICKS),
				new Integer(GeoElement.DECORATION_ANGLE_THREE_TICKS),
				new Integer(GeoElement.DECORATION_ANGLE_ARROW_ANTICLOCKWISE), // Michael
																				// Borcherds
																				// 2007
																				// -
																				// 11
																				// -
																				// 19
				new Integer(GeoElement.DECORATION_ANGLE_ARROW_CLOCKWISE), // Michael
																			// Borcherds
																			// 2007
																			// -
																			// 11
																			// -
																			// 19
		};
		return ret;
	}

	// end Loic

	/** Creates new GeoAngle */
	public GeoAngle(Construction c) {
		super(c);
		setAlphaValue(ConstructionDefaults.DEFAULT_ANGLE_ALPHA);
		setLabelMode(GeoElement.LABEL_NAME);
		setEuclidianVisible(false);
		animationIncrement = Math.PI / 180.0;
		DEFAULT_SLIDER_MIN = 0;
		DEFAULT_SLIDER_MAX = Kernel.PI_2;
	}

	public GeoAngle(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}

	protected String getClassName() {
		return "GeoAngle";
	}

	protected String getTypeString() {
		return "Angle";
	}

	public int getGeoClassType() {
		return GEO_CLASS_ANGLE;
	}

	public GeoAngle(Construction c, double x) {
		this(c);
		setValue(x);
	}

	final public boolean isGeoAngle() {
		return true;
	}
	
	final public boolean isAngle() {
		return true;
	}

	public void set(GeoElement geo) {
		GeoNumeric num = (GeoNumeric) geo;
		setValue(num.getValue());
	}

	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);

		if (geo.isGeoAngle()) {
			GeoAngle ang = (GeoAngle) geo;
			arcSize = ang.arcSize;
			// allowReflexAngle = ang.allowReflexAngle;
			angleStyle = ang.angleStyle;
			emphasizeRightAngle = ang.emphasizeRightAngle;
		}
	}

	// Michael Borcherds 2007-10-21 BEGIN
	/**
	 * Sets the value of this angle. Every value is limited between 0 and 2pi.
	 * Under some conditions a value > pi will be changed to (2pi - value).
	 * 
	 * @see setAngleStyle()
	 */
	public void setValue(double val) {
		double angVal = calcAngleValue(val);
		super.setValue(angVal);
	}
	
	/**
	 * Converts the val to a value between 0 and 2pi.
	 */
	private double calcAngleValue(double val) {
		// limit to [0, 2pi]
		double angVal = kernel.convertToAngleValue(val);

		rawValue = angVal;

		// if needed: change angle
		switch (angleStyle) {
		case ANGLE_ISCLOCKWISE:
			angVal = 2.0 * Math.PI - angVal;
			break;

		case ANGLE_ISNOTREFLEX:
			if (angVal > Math.PI)
				angVal = 2.0 * Math.PI - angVal;
			break;

		case ANGLE_ISREFLEX:
			if (angVal < Math.PI)
				angVal = 2.0 * Math.PI - angVal;
			break;
		}		
		
		return angVal;
	}

	// Michael Borcherds 2007-10-21 END

	public void setIntervalMax(double max) {
		if (max > Kernel.PI_2)
			return;
		super.setIntervalMax(max);
	}

	public void setIntervalMin(double min) {
		if (min < 0)
			return;
		super.setIntervalMin(min);
	}

	public void setEuclidianVisible(boolean flag) {
		if (flag && isIndependent()) {
			setLabelMode(GeoElement.LABEL_NAME_VALUE);
		}
		super.setEuclidianVisible(flag);
	}

	public GeoElement copy() {
		GeoAngle angle = new GeoAngle(cons);
		angle.setValue(rawValue);
		return angle;
	}

	// Michael Borcherds 2007-10-21 BEGIN
	/**
	 * Depending upon angleStyle, some values > pi will be changed to (2pi -
	 * value). raw_value contains the original value.
	 * 
	 * @see setValue()
	 */
	final public void setAllowReflexAngle(boolean allowReflexAngle) {
		switch (angleStyle) {
		case ANGLE_ISNOTREFLEX:
			if (allowReflexAngle)
				setAngleStyle(ANGLE_ISANTICLOCKWISE);
			break;
		case ANGLE_ISREFLEX:
			// do nothing
			break;
		default: // ANGLE_ISANTICLOCKWISE
			if (!allowReflexAngle)
				setAngleStyle(ANGLE_ISNOTREFLEX);
			break;

		}
		if (allowReflexAngle)
			setAngleStyle(ANGLE_ISANTICLOCKWISE);
		else
			setAngleStyle(ANGLE_ISNOTREFLEX);
	}

	final public void setForceReflexAngle(boolean forceReflexAngle) {

		switch (angleStyle) {
		case ANGLE_ISNOTREFLEX:
			if (forceReflexAngle)
				setAngleStyle(ANGLE_ISREFLEX);
			break;
		case ANGLE_ISREFLEX:
			if (forceReflexAngle)
				setAngleStyle(ANGLE_ISREFLEX);
			else
				setAngleStyle(ANGLE_ISANTICLOCKWISE);
			break;
		default: // ANGLE_ISANTICLOCKWISE
			if (forceReflexAngle)
				setAngleStyle(ANGLE_ISREFLEX);
			break;

		}
	}

	public void setAngleStyle(int angleStyle) {
		if (angleStyle == this.angleStyle)
			return;

		this.angleStyle = angleStyle;
		switch (angleStyle) {
		case ANGLE_ISCLOCKWISE:
			angleStyle = ANGLE_ISCLOCKWISE;
			break;

		case ANGLE_ISNOTREFLEX:
			angleStyle = ANGLE_ISNOTREFLEX;
			break;

		case ANGLE_ISREFLEX:
			angleStyle = ANGLE_ISREFLEX;
			break;

		default:
			angleStyle = ANGLE_ISANTICLOCKWISE;
		}
		// we have to reset the value of this angle
		AlgoElement algoParent = getParentAlgorithm();
		if (algoParent == null)
			// setValue(value);
			setValue(rawValue);
		else
			algoParent.update();
	}

	public int getAngleStyle() {
		return angleStyle;

	}

	/*
	 * final public boolean allowReflexAngle() { // return allowReflexAngle;
	 * return angleStyle==ANGLE_ISREFLEX; }
	 */

	/**
	 * Returns true if the current value was limited to a value between 0 and pi
	 * in setValue()
	 * 
	 * @see setAllowReflexAngle(), setValue()
	 */
	/*
	 * final public boolean changedReflexAngle() { return changedReflexAngle; }
	 */

	final public double getRawAngle() {
		return rawValue;
	}

	final public int angleStyle() {
		return angleStyle;
	}

	// Michael Borcherds 2007-10-21 END	

	final public String toValueString() {
		return kernel.formatAngle(value).toString();
	}

	// overwrite
	final public MyDouble getNumber() {
		MyDouble ret = new MyDouble(kernel, value);
		ret.setAngle();
		return ret;
	}

	public int getArcSize() {
		return arcSize;
	}

	public void setArcSize(int i) {
		arcSize = i;
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected String getXMLtags() {
		StringBuffer sb = new StringBuffer();

		sb.append("\t<value val=\"");
		sb.append(rawValue);
		sb.append("\"/>\n");

		// if angle is drawable then we need to save visual options too
		if (isDrawable() || isSliderable()) {
			// save slider info before show to have min and max set
			// before setEuclidianVisible(true) is called
			sb.append(getXMLsliderTag());

			sb.append(getXMLvisualTags());
			sb.append(getLineStyleXML());

			// arc size
			sb.append("\t<arcSize val=\"");
			sb.append(arcSize);
			sb.append("\"/>\n");
		}
		sb.append(getXMLAllowReflexAngleTag());
		sb.append(getXMLEmphasizeRightAngleTag());		
		sb.append(getXMLanimationTags());
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		sb.append(getBreakpointXML());
		return sb.toString();
	}

	private String getXMLAllowReflexAngleTag() {
		if (isIndependent())
			return "";

		StringBuffer sb = new StringBuffer();
		// Michael Borcherds 2007-10-21
		sb.append("\t<allowReflexAngle val=\"");
		sb.append(angleStyle != ANGLE_ISNOTREFLEX);
		sb.append("\"/>\n");
		if (angleStyle == ANGLE_ISREFLEX) {
			sb.append("\t<forceReflexAngle val=\"");
			sb.append(true);
			sb.append("\"/>\n");
		}

		// sb.append("\t<angleStyle val=\"");
		// sb.append(angleStyle);
		// sb.append("\"/>\n");
		// Michael Borcherds 2007-10-21
		return sb.toString();
	}
	
	private String getXMLEmphasizeRightAngleTag() {
		if (emphasizeRightAngle) 
			return "";
		
		// only store emphasizeRightAngle if "false"
		StringBuffer sb = new StringBuffer();		
		sb.append("\t<emphasizeRightAngle val=\"");
		sb.append(emphasizeRightAngle);
		sb.append("\"/>\n");		
		return sb.toString();
	}

	// Michael Borcherds 2007-11-20
	public void setDecorationType(int type) {
		if (type >= getDecoTypes().length || type < 0)
			decorationType = DECORATION_NONE;
		else
			decorationType = type;
	}

	// Michael Borcherds 2007-11-20

	public boolean isEmphasizeRightAngle() {
		return emphasizeRightAngle;
	}

	public void setEmphasizeRightAngle(boolean emphasizeRightAngle) {
		this.emphasizeRightAngle = emphasizeRightAngle;
	}

	public void setZero() {
		rawValue = 0;
	}
}