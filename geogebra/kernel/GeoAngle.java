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
 * GeoAngle.java
 *
 * The toString() depends on the kernels angle unit state (DEGREE or RADIANT)
 *
 * Created on 18. September 2001, 12:04
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.MyDouble;
/**
 *
 * @author  Markus
 * @version 
 */
public final class GeoAngle extends GeoNumeric {  	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int arcSize = EuclidianView.DEFAULT_ANGLE_SIZE; 
	
	// allow angle > pi
	private boolean allowReflexAngle = true; 	
	
    // shows whether the current value was changed to (2pi - value)
	private boolean changedReflexAngle; 

	// states whether a special right angle appearance should be used to draw this angle
	private boolean emphasizeRightAngle = true;
	
	// added by Loïc
	public static final Integer[] getDecoTypes() {
		Integer[] ret = { new Integer(GeoElement.DECORATION_NONE),
				new Integer(GeoElement.DECORATION_ANGLE_TWO_ARCS),
				new Integer(GeoElement.DECORATION_ANGLE_THREE_ARCS),
				new Integer(GeoElement.DECORATION_ANGLE_ONE_TICK),
				new Integer(GeoElement.DECORATION_ANGLE_TWO_TICKS),
				new Integer(GeoElement.DECORATION_ANGLE_THREE_TICKS),	
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
		animationStep = Math.PI / 180.0;	
		DEFAULT_SLIDER_MIN = 0;
		DEFAULT_SLIDER_MAX = Kernel.PI_2;	
    }
    
	public GeoAngle(Construction c, String label, double x) {
		this(c, x);
		setLabel(label);
	}  
	
	String getClassName() {
		return "GeoAngle";
	}
	
    String getTypeString() {
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
    
    public void set(GeoElement geo) {    
        GeoNumeric num = (GeoNumeric) geo;        
        setValue(num.getValue());     
    }    
    
    public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);
		
		if (geo.isGeoAngle()) {
			GeoAngle ang = (GeoAngle) geo;
			arcSize = ang.arcSize;
			allowReflexAngle = ang.allowReflexAngle;
			emphasizeRightAngle = ang.emphasizeRightAngle;
		}
	}
    
    
    /**
     * Sets the value of this angle. Every value
     * is limited between 0 and 2pi. If allowReflexAngle() is false
     * every value > pi will be changed to (2pi - value).
     * @see allowReflexAngle() 	
     */
    public void setValue(double val) {    	    	    	
    	// limit to [0, 2pi]
		double angVal = kernel.convertToAngleValue(val);						
		
		// if needed: limit to [0, pi]
		changedReflexAngle = !allowReflexAngle && angVal > Math.PI;
		if (changedReflexAngle) {
			angVal = Kernel.PI_2 - angVal;
		}					
		
		super.setValue(angVal);
    }
    
	public void setIntervalMax(double max) {
		if (max > Kernel.PI_2) return;
		super.setIntervalMax(max);
	}
	
	public void setIntervalMin(double min) {
		if (min < 0) return;
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
		angle.setValue(value);
        return angle;
    }
    
	
    
    
    /**
     * If allowReflexAngle is set to false every value > pi will be changed to (2pi - value).    
     * @see setValue()
     */
    final public void setAllowReflexAngle(boolean allowReflexAngle) {
    	if (allowReflexAngle == this.allowReflexAngle) return;
    	
    	this.allowReflexAngle = allowReflexAngle;
    	
    	// we have to reset the value of this angle
    	AlgoElement algoParent = getParentAlgorithm();    	
    	if (algoParent == null)
    		setValue(value);
    	else 
    		algoParent.update();
    }
    
    final public boolean allowReflexAngle() {
    	return allowReflexAngle;
    }
    
    /**
     * Returns true if the current value was limited to a
     * value between 0 and pi in setValue()
     * @see setAllowReflexAngle(), setValue()
     */
    final public boolean changedReflexAngle() {
    	return changedReflexAngle;
    }
    
	public boolean isAngle() {
		return true;
	}                        
       
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
	String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\t<value val=\"");
				sb.append(value);
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
		sb.append(getXMLanimationTags());
		sb.append(getXMLfixedTag());
		sb.append(getAuxiliaryXML());
		sb.append(getBreakpointXML());
		return sb.toString();   
	}    
	
	private String getXMLAllowReflexAngleTag() {
		if (isIndependent()) return "";
		
		StringBuffer sb = new StringBuffer();
		sb.append("\t<allowReflexAngle val=\"");
			sb.append(allowReflexAngle);
		sb.append("\"/>\n");	
		return sb.toString();
	}

	public boolean isEmphasizeRightAngle() {
		return emphasizeRightAngle;
	}

	public void setEmphasizeRightAngle(boolean emphasizeRightAngle) {
		this.emphasizeRightAngle = emphasizeRightAngle;
	}
	
	

}