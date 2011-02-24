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

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * 
 * @author Markus
 * @version
 */
public class GeoNumeric extends GeoElement 
implements NumberValue,  AbsoluteScreenLocateable, GeoFunctionable, Animatable {	
	
	private static final int LISTENER_NONE = 0;
	public static int LISTENER_XMIN = 1;
	public static int LISTENER_XMAX = 2;
	public static int LISTENER_YMIN = 4;
	public static int LISTENER_YMAX = 8;
	private static final long serialVersionUID = 1L;
	
	private static int DEFAULT_SLIDER_WIDTH_RW = 4;
	private static int DEFAULT_SLIDER_WIDTH_PIXEL = 100;	
	private static int DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE = 72;
	/** Default maximum value when displayed as slider*/
	public static double DEFAULT_SLIDER_MIN = -5;
	/** Default minimum value when displayed as slider*/
	public static double DEFAULT_SLIDER_MAX = 5;
	/** Default increment when displayed as slider*/
	public static double DEFAULT_SLIDER_INCREMENT = 0.1;
	
	/** value of the number or angle */
	protected double value;	

	private boolean isDrawable = false;
	//private boolean isRandomNumber = false;
	
	private int slopeTriangleSize = 1;

	// for slider	
	private boolean intervalMinActive = false;
	private boolean intervalMaxActive = false;
	private NumberValue intervalMin;
	private NumberValue intervalMax; 
	private double sliderWidth = this instanceof GeoAngle ? DEFAULT_SLIDER_WIDTH_PIXEL_ANGLE : DEFAULT_SLIDER_WIDTH_PIXEL;
	private double sliderX, sliderY;
	private boolean sliderFixed = false;
	private boolean sliderHorizontal = true;
	private double animationValue = Double.NaN;	
	
	/** absolute screen location, true by default */
	boolean hasAbsoluteScreenLocation = true;	

	/** 
	 * Creates new GeoNumeric
	 * @param c Construction 
	 */
	public GeoNumeric(Construction c) {
		super(c);
		setEuclidianVisible(isGeoAngle());
		setAlphaValue(ConstructionDefaults.DEFAULT_POLYGON_ALPHA);
		setAnimationStep(DEFAULT_SLIDER_INCREMENT);					
	}

	public String getClassName() {
		return "GeoNumeric";
	}
	
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_SLIDER;
    }	
	
    protected String getTypeString() {
		return "Numeric";
	}
    
    public int getGeoClassType() {
    	return GEO_CLASS_NUMERIC;
    }

	/**
	 * Creates new labeled number
	 * @param c Cons
	 * @param label Label for new number
	 * @param x Number value
	 */
    public GeoNumeric(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}

    /**
	 * Creates new number
	 * @param c Cons
	 * @param x Number value
	 */
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
		return isDrawable || (getDrawAlgorithm()!=getParentAlgorithm()) || (isIndependent() && isLabelSet());		
	}

	public boolean isFillable() {
		return isDrawable;
	}

	/**
	 * Sets whether the number should be drawable (as slider or angle in case of GeoAngle)
	 * If possible, makes the number also visible.
	 * @param flag true iff this number should be drawable
	 */
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
						setIntervalMin(new MyDouble(kernel,min));
						setIntervalMax(new MyDouble(kernel,max));												
					} else {
						// max is available but no min
						double min = Math.min(getDefaultSliderMin(), Math.floor(value));
						setIntervalMin(new MyDouble(kernel,min));				
					}
				}
				else { // min exists
					if (!intervalMaxActive) {
						//	min is available but no max
						double max = Math.max(getDefaultSliderMax(), Math.ceil(value));
						setIntervalMax(new MyDouble(kernel,max));					
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
		TreeSet<GeoElement> numbers = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_NUMERIC);
		TreeSet<GeoElement> angles = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_ANGLE);		
		if (numbers != null) {
			if (angles != null)
				numbers.addAll(angles);
		} else {
			numbers = angles;
		}
		
		if (numbers != null) {
			Iterator<GeoElement> it = numbers.iterator();
			while (it.hasNext()) {
				GeoNumeric num =  (GeoNumeric) it.next();
	    		if (num.isSlider()) count++;
			}
		}
		
		return count;
	}
	
	/**
	 * @return true if displayed as slider
	 */
	public boolean isSlider() {
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

	/**
	 * Returns true iff defined and infinite
	 * @return true iff defined and infinite
	 */
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
		if (geo.isGeoNumeric()) 
			return Kernel.isEqual(value, ((GeoNumeric)geo).value); 
		else 
			return false;
	}

	/**
	 * Sets value of the number
	 * @param x number value
	 */
	final public void setValue(double x) {
		setValue(x, true);
	}
	
	/**
	 * Sets value of the number
	 * @param x number value
	 * @param changeAnimationValue if true, value is changed also for animation
	 */
	void setValue(double x, boolean changeAnimationValue) {
		if (intervalMinActive && x < getIntervalMin()) {			
			value = getIntervalMin();			
		}					
		else if (intervalMaxActive && x > getIntervalMax()) {
			value = getIntervalMax();			
		}						
		else		 
			value = x;
		
		// remember value for animation also
		if (changeAnimationValue) 
			animationValue = value;
	}

	/**
	 * Returns value of the number
	 * @return number value
	 */
	final public double getValue() {
		return value;
	}

	/** dummy implementation of mode 
	 * @param mode dummy parameter 
	 */
	final public void setMode(int mode) {
	}

	/** dummy implementation of mode 
	 * @return -1 (allways) 
	 */
	final public int getMode() {
		return -1;
	}

	public String toString() {
		if(sbToString == null)return null;
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append(toValueString());
		return sbToString.toString();
	}

	private StringBuilder sbToString = new StringBuilder(50);
	private ArrayList<GeoNumeric> minMaxListeners;
	private boolean randomSlider = false;

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

	final public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> varset = new HashSet<GeoElement>();
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
		sb.append("\"");
		if (isRandom()) {
			sb.append(" random=\"true\"");
		}
		sb.append("/>\n");
		
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
		getScriptTags(sb);
	}
	
	/**
	 * Returns true iff slider is possible
	 * @return true iff slider is possible
	 */
	protected boolean isSliderable() {
		return isIndependent() && (intervalMinActive || intervalMaxActive);
	}
	
	public boolean isFixable() {
		// visible slider should not be fixable
		return !isSetEuclidianVisible();
	}
	
	/**
	 * Adds the slider tag to the string builder
	 * @param sb String builder to be written to
	 */
	void getXMLsliderTag(StringBuilder sb) {
		if (!isSliderable())
			return;
		

		sb.append("\t<slider");
		if (intervalMinActive) {
			sb.append(" min=\"");
			sb.append(Util.encodeXML(getIntervalMinObject().getLabel()));
			sb.append("\"");
		}
		if (intervalMinActive) {
			sb.append(" max=\"");
			sb.append(Util.encodeXML(getIntervalMaxObject().getLabel()));
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
	 * Returns size of the triangle when used for slop
	 * @return size of the triangle when used for slope
	 */
	final public int getSlopeTriangleSize() {
		return slopeTriangleSize;
	}

	/**
	 * Set size of the triangle when used for slop
	 * @param i Size of the slope triangle
	 */
	public void setSlopeTriangleSize(int i) {
		slopeTriangleSize = i;
	}

	public boolean isTextValue() {
		return false;
	}
	/**
	 * Changes maximal value for slider
	 * @param max New maximum for slider
	 */
	public void setIntervalMax(NumberValue max) {	
		if (Double.isNaN(max.getDouble()) || Double.isInfinite(max.getDouble())) return;

		if(intervalMax instanceof GeoNumeric){
			((GeoNumeric)intervalMax).unregisterMinMaxListener(this);
		}
		intervalMax = max;
		intervalMaxActive = !Double.isNaN(max.getDouble());
		if(max instanceof GeoNumeric){
			((GeoNumeric)max).registerMinMaxListener(this);
		}
		resolveMinMax();
	}
	
	/**
	 * Changes minimal value for slider
	 * @param min New minimum for slider
	 */
	public void setIntervalMin(NumberValue min) {
		if (Double.isNaN(min.getDouble()) || Double.isInfinite(min.getDouble()))
				return;
		if(intervalMin instanceof GeoNumeric){
			((GeoNumeric)intervalMin).unregisterMinMaxListener(this);
		}
		intervalMin = min;
		intervalMinActive = !Double.isNaN(min.getDouble());;	
		if(min instanceof GeoNumeric){
			((GeoNumeric)min).registerMinMaxListener(this);
		}
		resolveMinMax();
	}
	
	/**
	 * Changes slider width in pixels
	 * @param width slider width in pixels
	 */
	public final void setSliderWidth(double width) {
		if (width > 0 && !Double.isInfinite(width))
			sliderWidth = width;
	}
	
	/**
	 * Sets the location of the slider for this number.
	 * @param x x-coord of the slider
	 * @param y y-coord of the slider
	 */
	public final void setSliderLocation(double x, double y) {
		if (sliderFixed) return;
		sliderX = x;
		sliderY = y;			
	}
	
	/**
	 * Returns maximal value for slider
	 * @return maximal value for slider
	 */ 
	public final double getIntervalMax() {
		return intervalMax.getDouble();
	}

	/**
	 * Returns minimal value for slider
	 * @return minimal value for slider
	 */ 
	public final double getIntervalMin() {
		return intervalMin.getDouble();
	}
	/**
	 * Returns slider width in pixels
	 * @return slider width in pixels
	 */ 
	public final double getSliderWidth() {
		return sliderWidth;
	}
	/**
	 * Returns x-coord of the slider
	 * @return x-coord of the slider
	 */ 
	public final double getSliderX() {
		return sliderX;
	}
	/**
	 * Returns y-coord of the slider
	 * @return y-coord of the slider
	 */ 
	public final double getSliderY() {
		return sliderY;
	}
	

	/**
	 * Returns true if slider max value wasn't disabled in Properties
	 * @return true if slider max value wasn't disabled
	 */
	public final boolean isIntervalMaxActive() {
		return intervalMaxActive;
	}
	/**
	 * Returns true if slider min value wasn't disabled in Properties
	 * @return true if slider min value wasn't disabled
	 */
	public final boolean isIntervalMinActive() {
		return intervalMinActive;
	}
	/**
	 * Disables slider max value
	 */
	public final void setIntervalMaxInactive() {
		intervalMaxActive = false;
		setEuclidianVisible(false);
	}
	/**
	 * Disables slider min value
	 */
	public final void setIntervalMinInactive() {
		intervalMinActive = false;
		setEuclidianVisible(false);
	}
	
	/**
	 * Returns true iff slider is fixed in graphics view
	 * @return true iff slider is fixed in graphics view
	 */
	public final boolean isSliderFixed() {
		return sliderFixed;
	}
	
	/**
	 * Sets whether slider is fixed in graphics view
	 * @param isSliderFixed true iff slider is fixed in graphics view
	 */
	public final void setSliderFixed(boolean isSliderFixed) {
		sliderFixed = isSliderFixed;
	}

	/**
	 * Returns whether slider shoud be horizontal or vertical
	 * @return true iff should be horizontal
	 */
	public final boolean isSliderHorizontal() {
		return sliderHorizontal;
	}

	/**
	 * Sets whether slider shoud be horizontal or vertical
	 * @param sliderHorizontal true iff should be horizontal
	 */
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
	 * @return constant function
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
		if(intervalMin instanceof GeoNumeric)
			((GeoNumeric)intervalMin).unregisterMinMaxListener(this);
		if(intervalMax instanceof GeoNumeric)
			((GeoNumeric)intervalMax).unregisterMinMaxListener(this);
	}

	/**
	 * Given geo depends on this one (via min or max value for slider)
	 * and should be updated
	 * @param geo geo to be updated
	 */
	public void registerMinMaxListener(GeoNumeric geo){
		if(minMaxListeners == null)
			minMaxListeners = new ArrayList<GeoNumeric>();
		minMaxListeners.add(geo);		
	}
	
	/**
	 * Given geo no longer depends on this one (via min or max value for slider)
	 * and should not be updated any more
	 * @param geo
	 */
	public void unregisterMinMaxListener(GeoNumeric geo){
		if(minMaxListeners == null)
			minMaxListeners = new ArrayList<GeoNumeric>();
		minMaxListeners.remove(geo);		
	}
	
	/**
	 * @return list of min/max listeners
	 */
	public List<GeoNumeric> getMinMaxListeners() {
		return minMaxListeners;
	}
	
	/**
	 * @param random true for random slider
	 */
	public void setRandom(boolean random) {
		randomSlider = random;
		if (random) cons.addRandomGeo(this);
		else cons.removeRandomGeo(this);
	}
	
	/**
	 * returns true for random sliders
	 * (can be hidden to make random numbers which still use intervalMin, Max, interval)
	 * @return true for random sliders 
	 */
	public boolean isRandom() {
		return randomSlider;
	}
	 
	/**
	 * Updates random slider
	 */
	public void updateRandom() {
		if (randomSlider && isIntervalMaxActive() && isIntervalMinActive()) {
			// update all algorithms in the algorithm set of this GeoElement    
			value = getRandom();
			updateCascade();
		}

	}
	
	/*
	 * returns a random number in the slider's range (and using step-size)
	 */
	private double getRandom() {
		double min = getIntervalMin();
		double max = getIntervalMax();
		double increment = getAnimationStep();
		int n = 1 + (int)Math.round( (max - min ) / increment );
		return kernel.checkDecimalFraction(Math.floor(Math.random() * n) * increment + min);	
	}
	
	public void update() {  	
		super.update();
		//G.Sturr 2010-5-12
		EuclidianView view = kernel.getApplication().getEuclidianView();	
		if (this == view.getEuclidianController().recordObject){	
    		cons.getApplication().getGuiManager().traceToSpreadsheet(this);
    	}
	  	//END G.Sturr
		if (minMaxListeners != null) {			
			for (int i=0; i < minMaxListeners.size(); i++) {
				GeoNumeric geo = minMaxListeners.get(i);
				geo.resolveMinMax();								
			}					
		}
		if(evListenerType != LISTENER_NONE)
			view.updateBounds();
			return;				
    }	
	
	private void resolveMinMax() {
		if(intervalMin == null || intervalMax == null)
			return;
		boolean ok =  (getIntervalMin() <= getIntervalMax());
		intervalMinActive = ok;
		intervalMaxActive = ok;
			
		if(ok)
			setValue(isDefined() ? value : 1.0);		
		else 
			setUndefined();
		
		updateCascade();
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
		
		
		// special case for random slider
		// animationValue goes from 0 to animationStep
		if (isRandom()) {
			
			double animationStep = getAnimationStep();
			
			// check not silly value
			if (animationValue < -2 * animationStep) {
				animationValue = 0;
			}
			
			double intervalWidth = getIntervalMax() - getIntervalMin();
			double step = intervalWidth * getAnimationSpeed() /
					      (AnimationManager.STANDARD_ANIMATION_TIME * frameRate);			
			// update animation value
			if (Double.isNaN(animationValue) || animationValue < 0)
				animationValue = 0;
			animationValue = animationValue + Math.abs(step);
			
			if (animationValue > animationStep) {
				animationValue -= animationStep;
				setValue(getRandom(), false);
				return true;				
			}			
			
			// no update needed
			return false;
		}
		
		// remember old value of number to decide whether update is necessary
		double oldValue = getValue();
		
		// compute animation step based on speed and frame rates
		double intervalWidth = getIntervalMax() - getIntervalMin();
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
				if (animationValue > getIntervalMax()) 
					animationValue = animationValue - intervalWidth;
				else if (animationValue < getIntervalMin()) 
					animationValue = animationValue + intervalWidth;		
				break;
			
			case GeoElement.ANIMATION_INCREASING_ONCE:
				// stop if outside range
				if (animationValue > getIntervalMax()) {
					setAnimating(false);
					setValue(getIntervalMax(), false);
					return true;
				} else if (animationValue < getIntervalMin()) {
					setAnimating(false);
					setValue(getIntervalMin(), false);
					return true;
				}
				break;
			
			case GeoElement.ANIMATION_OSCILLATING:
			default: 		
				if (animationValue >= getIntervalMax()) {
					animationValue = getIntervalMax();
					changeAnimationDirection();
				} 
				else if (animationValue <= getIntervalMin()) {
					animationValue = getIntervalMin();
					changeAnimationDirection();			
				}		
				break;
		}
		
		double newValue;
				
		// take current slider increment size into account:
		// round animationValue to newValue using slider's increment setting	
		double param = animationValue - getIntervalMin();
		param = Kernel.roundToScale(param, getAnimationStep());		
		newValue = getIntervalMin() + param;	
		
		if (getAnimationStep() > Kernel.MIN_PRECISION) {
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
	 * @return 1 if first is greater (or same but sooner in construction), -1 otherwise 
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

	/**
	 * Returns default minimum for slider
	 * @return default minimum for slider
	 */
	public double getDefaultSliderMin() {
		return isGeoAngle() ? GeoAngle.DEFAULT_SLIDER_MAX : DEFAULT_SLIDER_MIN;
	}

	/**
	 * Returns default maximum for slider
	 * @return default maximum for slider
	 */
	public double getDefaultSliderMax() {
		return isGeoAngle() ? GeoAngle.DEFAULT_SLIDER_MAX : DEFAULT_SLIDER_MAX;
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

	/**
	 * Set interval min
	 * @param value
	 */
	public void setIntervalMin(double value) {
			setIntervalMin(new MyDouble(kernel, value));		
	}
	
	/**
	 * Set interval max
	 * @param value
	 */
	public void setIntervalMax(double value) {
		setIntervalMax(new MyDouble(kernel, value));		
	}

	/**
	 * Get interval min as geo
	 * @return interval min
	 */
	public GeoElement getIntervalMinObject() {
		if (intervalMin == null) return null;
		return intervalMin.toGeoElement();
	}
	
	/**
	 * Get interval max as geo
	 * @return interval max
	 */
	public GeoElement getIntervalMaxObject() {
		if (intervalMax == null) return null;
		return intervalMax.toGeoElement();
	}

	public boolean canHaveClickScript() {
		return false;
	}
	
	final public boolean isCasEvaluableObject() {
		return true;
	}
	private int evListenerType = LISTENER_NONE;
	public void addEVSizeListener(int type){
		evListenerType |= type;
	}

	public void removeEVSizeListener(int type) {
		evListenerType &= ~type;	
	}
}