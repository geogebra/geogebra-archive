/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * GeoNumeric.java
 *
 * Created on 18. September 2001, 12:04
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoNumeric extends GeoElement 
implements NumberValue,  AbsoluteScreenLocateable, GeoFunctionable, Animatable {	
	
	private static final long serialVersionUID = 1L;
	private static int DEFAULT_SLIDER_WIDTH_RW = 4;
	private static int DEFAULT_SLIDER_WIDTH_PIXEL = 100;	
	private static int DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE = 72;	
	public static double DEFAULT_SLIDER_MIN = -5;
	public static double DEFAULT_SLIDER_MAX = 5;
	public static double DEFAULT_SLIDER_INCREMENT = 0.1;
	
	double DEFAULT_SLIDER_MIN_ANGLE = 0;
	double DEFAULT_SLIDER_MAX_ANGLE = 2*Math.PI;

	protected double value;	

	private boolean isDrawable = false;
	//private boolean isRandomNumber = false;
	
	private int slopeTriangleSize = 1;

	// for slider	
	private boolean intervalMinActive = false;
	private boolean intervalMaxActive = false;
	private double intervalMin = Double.NEGATIVE_INFINITY;
	private double intervalMax = Double.POSITIVE_INFINITY; 
	private double sliderWidth = this instanceof GeoAngle ? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE : DEFAULT_SLIDER_WIDTH_PIXEL;
	private double sliderX, sliderY;
	private boolean sliderFixed = false;
	private boolean sliderHorizontal = true;
	private double animationValue = Double.NaN;	
	
	// for absolute screen location
	boolean hasAbsoluteScreenLocation = true;	

	/** Creates new GeoNumeric */
	public GeoNumeric(Construction c) {
		super(c);
		setEuclidianVisible(isGeoAngle());
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
		animationIncrement = DEFAULT_SLIDER_INCREMENT;					
	}

	public String getClassName() {
		return "GeoNumeric";
	}
	
    protected String getTypeString() {
		return "Numeric";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_NUMERIC;
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
	
	 public void setZero() {
	    	setValue(0);
	}
	
	public boolean isDrawable() {		
		return isDrawable || (isIndependent() && isLabelSet());		
	}

	public boolean isFillable() {
		return isDrawable;
	}

	public void setDrawable(boolean flag) {
		isDrawable = flag;
		
		if (isDrawable && kernel.isNotifyViewsActive() && kernel.isAllowVisibilitySideEffects() ) {
			setEuclidianVisible(true);
		}						
	}
	
	public void setEuclidianVisible(boolean visible) {
		if (visible == isSetEuclidianVisible() || kernel.isMacroKernel() ) return;		
					
		// slider is only possible for independent
		// number with given min and max
		if (isIndependent()) {
			if (visible) {		
				// make sure the slider value is not fixed 
				setFixed(false);
				
				if (!intervalMinActive) {
					if (!intervalMaxActive) {
						// set both to default
						double min = Math.min(getDefaultSliderMin(), Math.floor(value));
						double max = Math.max(getDefaultSliderMax(), Math.ceil(value));
						setIntervalMin(min);
						setIntervalMax(max);												
					} else {
						// max is available but no min
						double min = Math.min(getDefaultSliderMin(), Math.floor(value));
						setIntervalMin(min);				
					}
				}
				else { // min exists
					if (!intervalMaxActive) {
						//	min is available but no max
						double max = Math.max(getDefaultSliderMax(), Math.ceil(value));
						setIntervalMax(max);					
					}
				}			
				
				// init screen location
				if (sliderX == 0 && sliderY ==0) {	
					int count = countSliders();
					
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
				
				// make sure
			}
			
			/* we don't want to remove min, max values when slider is hidden			
			else { // !visible
				intervalMinActive = false;
				intervalMaxActive = false;
			}*/
		} 
		
		super.setEuclidianVisible(visible);
	}
	
	private int countSliders() {
		int count = 0;
		
		// get all number and angle sliders		
		TreeSet numbers = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_NUMERIC);
		TreeSet angles = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_ANGLE);		
		if (numbers != null) {
			if (angles != null)
				numbers.addAll(angles);
		} else {
			numbers = angles;
		}
		
		if (numbers != null) {
			Iterator it = numbers.iterator();
			while (it.hasNext()) {
				GeoNumeric num = (GeoNumeric) it.next();
	    		if (num.isSlider()) count++;
			}
		}
		
		return count;
	}
	
	private boolean isSlider() {
		return isIndependent() && isEuclidianVisible();			
	}

	public boolean showInEuclidianView() {
		return isDrawable() && isDefined() && !isInfinite();
	}

	public final boolean showInAlgebraView() {
		// independent or defined
		//return isIndependent() || isDefined();
		return true;
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

	public String getLaTeXdescription() {
		if (strLaTeXneedsUpdate) {			
			if (!isDefined()) {
				strLaTeX = app.getPlain("undefined");
			} else if (isInfinite()) {
				if (value >= 0) strLaTeX = "\\infty"; else strLaTeX = "-\\infty";
			} else {				
				strLaTeX = toLaTeXString(false);
			}
		}
		return strLaTeX;		
	}

	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type, otherwise use equals() method
		if (geo.isGeoNumeric()) return kernel.isEqual(value, ((GeoNumeric)geo).value); else return false;
	}

	final public void setValue(double x) {
		setValue(x, true);
	}
	
	void setValue(double x, boolean changeAnimationValue) {
		if (intervalMinActive && x < intervalMin) {			
			value = intervalMin;			
		}					
		else if (intervalMaxActive && x > intervalMax) {
			value = intervalMax;			
		}						
		else		 
			value = x;
		
		// remember value for animation also
		if (changeAnimationValue) 
			animationValue = value;
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

	public String toString() {
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);

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

	public void setAllVisualProperties(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualProperties(geo, keepAdvanced);
		
		if (geo.isGeoNumeric()) {
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
	protected void getXMLtags(StringBuilder sb) {
		sb.append("\t<value val=\"");
		sb.append(value);
		sb.append("\"/>\n");
		
		//	colors
		getXMLvisualTags(sb);

		//	if number is drawable then we need to save visual options too
		if (isDrawable || isSliderable()) {
			// save slider info before show to have min and max set
			// before setEuclidianVisible(true) is called
			getXMLsliderTag(sb);						
			
			//	line thickness and type
			getLineStyleXML(sb);

			// for slope triangle
			if (slopeTriangleSize > 1) {
				sb.append("\t<slopeTriangleSize val=\"");
				sb.append(slopeTriangleSize);
				sb.append("\"/>\n");
			}						
		}
		getXMLanimationTags(sb);
		getXMLfixedTag(sb);
		getAuxiliaryXML(sb);
		getBreakpointXML(sb);
	}
	
	protected boolean isSliderable() {
		return isIndependent() && (intervalMinActive || intervalMaxActive);
	}
	
	public boolean isFixable() {
		// visible slider should not be fixable
		return !isSetEuclidianVisible();
	}
	
	void getXMLsliderTag(StringBuilder sb) {
		if (!isSliderable())
			return;
		

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
		if (Double.isNaN(max) || Double.isInfinite(max)) return;

		intervalMax = max;
		intervalMaxActive = true;
		
		if (intervalMinActive && max <= intervalMin) {
			setIntervalMin(max - 1);
		}
		
		setValue(value);			
	}
	
	public void setIntervalMin(double min) {
		if (Double.isNaN(min) || Double.isInfinite(min))
				return;
		
		intervalMin = min;
		intervalMinActive = true;	
		
		if (intervalMaxActive && min >= intervalMax) {
			setIntervalMax(min + 1);
		}
		
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
			sliderWidth = this instanceof GeoAngle ? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE : DEFAULT_SLIDER_WIDTH_PIXEL;
		else 
			sliderWidth = DEFAULT_SLIDER_WIDTH_RW;
	}

	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocateable() {
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
		
		// we get a dependent function if this number has a label or is dependent
		if (isLabelSet() || !isIndependent()) {
			// don't create a label for the new dependent function
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			ret = kernel.DependentFunction(null, fun);
			cons.setSuppressLabelCreation(oldMacroMode);
		} else {
			ret = new GeoFunction(cons);
			ret.setFunction(fun);
		}					
				
		return ret;
	}
	
	public boolean isGeoFunctionable() {
		return true;
	}
	
	protected void doRemove() {
		super.doRemove();
		
		// if this was a random number, make sure it's removed
		cons.removeRandomGeo(this);
	}

	
	public void update() {  	
		super.update();
		//G.Sturr 2010-5-12
		EuclidianView view = kernel.getApplication().getEuclidianView();	
		if (this == view.getEuclidianController().recordObject){	
    		cons.getApplication().getGuiManager().traceToSpreadsheet(this);
    	}
    		
    	/* spreadsheet trace now handled in GeoElement.update()	
    	if (isSpreadsheetTraceable() && getSpreadsheetTrace()){ 
        	cons.getApplication().getGuiManager().traceToSpreadsheet(this);
    	}		
		*/
		
		// -------- old code
		
		/*
		// TODO: can we move this to Drawable? (problem: dependant GeoNumeric not drawn!)
		EuclidianView view = kernel.getApplication().getEuclidianView();	
        // record to spreadsheet tool
    	if (this == view.getEuclidianController().recordObject
    			&& this.getLastTrace1() != value) {
    		
    		cons.getApplication().getGuiManager().traceToSpreadsheet(this);
	    	/*
	    	String col = getTraceColumn1(); // must be called before getTraceRow()
	    	String row = getTraceRow() + "";
	    	
	    	cons.getApplication().getGuiManager().setScrollToShow(true);
	    	GeoNumeric traceCell = new GeoNumeric(cons,col+row,value);
	    	cons.getApplication().getGuiManager().setScrollToShow(false);
	    	
	    	traceCell.setAuxiliaryObject(true);
	    	
	    	setLastTrace1(value);*/
	    
    //	}
    	
    	//END G.Sturr
    	
    }	
	
	/**
	 * Returns whether this number can be animated. Only free numbers with min and max interval
	 * values can be animated (i.e. shown or hidden sliders). 
	 */
	public boolean isAnimatable() {
		return isIndependent() && intervalMinActive && intervalMaxActive;
	}
	
	/**
	 * Sets the state of this object to animating on or off.
	 */
	public synchronized void setAnimating(boolean flag) {
		animationValue = Double.NaN;		
		super.setAnimating(flag);		
	}
	
	/**
	 * Performs the next automatic animation step for this numbers. This changes
	 * the value but will NOT call update() or updateCascade().
	 * 
	 * @return whether the value of this number was changed
	 */
	final public synchronized boolean doAnimationStep(double frameRate) {
		// check that we have valid min and max values
		if (!intervalMinActive || !intervalMaxActive) 
			return false;
		
		// remember old value of number to decide whether update is necessary
		double oldValue = getValue();
		
		// compute animation step based on speed and frame rates
		double intervalWidth = intervalMax - intervalMin;
		double step = intervalWidth * getAnimationSpeed() * getAnimationDirection() /
				      (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);
		
		// update animation value
		if (Double.isNaN(animationValue))
			animationValue = oldValue;
		animationValue = animationValue + step;
		
		// make sure we don't get outside our interval
		switch (getAnimationType()) {		
			case GeoElement.ANIMATION_DECREASING:
			case GeoElement.ANIMATION_INCREASING:
				// jump to other end of slider
				if (animationValue > intervalMax) 
					animationValue = animationValue - intervalWidth;
				else if (animationValue < intervalMin) 
					animationValue = animationValue + intervalWidth;		
				break;
			
			case GeoElement.ANIMATION_OSCILLATING:
			default: 		
				if (animationValue >= intervalMax) {
					animationValue = intervalMax;
					changeAnimationDirection();
				} 
				else if (animationValue <= intervalMin) {
					animationValue = intervalMin;
					changeAnimationDirection();			
				}		
				break;
		}
				
		// take current slider increment size into account:
		// round animationValue to newValue using slider's increment setting	
		double param = animationValue - intervalMin;
		param = Kernel.roundToScale(param, animationIncrement);		
		double newValue = intervalMin + param;	
		
		if (animationIncrement > Kernel.MIN_PRECISION) {
			// round to decimal fraction, e.g. 2.800000000001 to 2.8
			newValue = kernel.checkDecimalFraction(newValue);
		}
										
		// change slider's value without changing animationValue
		setValue(newValue, false);
		
		// return whether value of slider has changed
		return getValue() != oldValue;	
	}	
	
	/**
	 * Returns a comparator for GeoNumeric objects.
	 * If equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator<GeoNumeric> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<GeoNumeric>() {
		      public int compare(GeoNumeric itemA, GeoNumeric itemB) {
		        
		        double comp = itemA.getValue() - itemB.getValue();
		        if (Kernel.isZero(comp))
		        // don't return 0 for equal objects, otherwise the TreeSet deletes duplicates
		        	return itemA.getConstructionIndex() > itemB.getConstructionIndex() ? -1 : 1;
		        else
		        	return comp < 0 ? -1 : +1;
		      }
			};
		}
		
		return comparator;
	}
	private static Comparator<GeoNumeric> comparator;

	public double getDefaultSliderMin() {
		return isGeoAngle() ? DEFAULT_SLIDER_MIN_ANGLE : DEFAULT_SLIDER_MIN;
	}

	public double getDefaultSliderMax() {
		return isGeoAngle() ? DEFAULT_SLIDER_MAX_ANGLE : DEFAULT_SLIDER_MAX;
	}
	
	//protected void setRandomNumber(boolean flag) {
	//	isRandomNumber = flag;
	//}
	
	//public boolean isRandomNumber() {
	//	return isRandomNumber;
	//}
	
	@Override
	final public void updateRandomGeo() {	
		// set random value (for numbers used in trees using random())
		setValue(Math.random());
		
		super.updateRandomGeo();
	}

	public boolean isVector3DValue() {
		return false;
	}
	
	/**
	 * @return minimum line thickness (normally 1, but 0 for polygons, integrals etc)
	 */
	public int getMinimumLineThickness() {
		return (isSlider() ? 1 : 0);
	}


}