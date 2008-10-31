/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoElement.java
 *
 * Created on 30. August 2001, 17:10
 */

package geogebra3D.kernel3D;

import java.text.DecimalFormat;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;


/**
 *
 * @author  Mathieu
 * @version 
 */
public abstract class GeoElement3D
	extends GeoElement{
	
	private static final float EPSILON_Z = 0.0001f;//0.0001f;//10000000; //limit to consider two objects to be at the same place
	private static final boolean DEBUG = false;
	
	
	private boolean wasHighlighted = false;
	private boolean willBeHighlighted = false;

	
	public float zPickMax, zPickMin; //for odering elements with openGL picking
	
	//
	// GeoElement3D types 
	// be care to put them in a correct order : points before 1D objects before 2D objects ...
	//
	// points
	public static final int GEO_CLASS_POINT3D = 3010;
	//
	// 1D objects
	public static final int GEO_CLASS_SEGMENT3D = 3110;
	public static final int GEO_CLASS_LINE3D = 3120;
	//
	// 2D objects
	public static final int GEO_CLASS_TRIANGLE3D = 3210;
	public static final int GEO_CLASS_PLANE3D = 3220;


	
	/********************************************************/

	/** Creates new GeoElement for given construction */
	public GeoElement3D(Construction c) {
		super(c);
		
	}
	
	
	public boolean isGeoElement3D(){
		return true;
	}

	// Path1D interface
	public boolean isPath1D(){
		return false;
	}
	
	public boolean hasPath1D() {
		return false;
	}	
	
	/** stores the current highlighted flag to wasHighlighted */ 
	final public void setWasHighlighted(){
		wasHighlighted = highlighted;
	}

	/** stores the future highlighted flag to willBeHighlighted */ 
	final public void setWillBeHighlighted(boolean flag){
		willBeHighlighted = flag;
	}
	
	/** update the highlighted flag */ 
	final public void updateHighlighted(boolean repaint){
		//Application.debug(getLabel()+" : "+wasHighlighted+","+willBeHighlighted);
		if (wasHighlighted!=willBeHighlighted){
			setHighlighted(willBeHighlighted); //GeoElement method
			if (repaint)
				updateRepaint();//for highlighting in algebraView
		}
	}
	
	
	
	/** set the highlighted flag and eventually repaint it for algebraView */ 
	final public void setHighlighted(boolean flag, boolean repaint) {
		boolean highlightedOld = highlighted;
		setHighlighted(flag); //GeoElement method
		if ((highlightedOld!=highlighted)&&repaint)
			updateRepaint();//for highlighting in algebraView
		
	}	
	
	
	
	

	
	/** compare this to another GeoElement3D with zPicking */
	public int zPickCompareTo(GeoElement3D geo2, boolean checkGeoClassType){
		
		//check if the two objects are "mixed"			
		if ((this.zPickMin-geo2.zPickMin)*(this.zPickMax-geo2.zPickMax)<EPSILON_Z){
			
			if (DEBUG){
				DecimalFormat df = new DecimalFormat("0.000000000");
				Application.debug("mixed :\n"
						+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getLabel()+")\n"
						+"zMin= "+df.format(geo2.zPickMin)+" | zMax= "+df.format(geo2.zPickMax)+" ("+geo2.getLabel()+")\n");
			}
			
			if (checkGeoClassType){
				if (this.getGeoClassType()<geo2.getGeoClassType())
					return -1;
				if (this.getGeoClassType()>geo2.getGeoClassType())
					return 1;
			}
			
			// check if one is on a path and the other not
			//TODO do this only for points
			if ((this.hasPath1D())&&(!geo2.hasPath1D()))
				return -1;
			if ((!this.hasPath1D())&&(geo2.hasPath1D()))
				return 1;


			//check if one is the child of the other
			if (this.isChildOf(geo2))
				return -1;
			if (geo2.isChildOf(this))
				return 1;
		
		}

		//finally check if one is before the other
		if (this.zPickMax<geo2.zPickMax)
			return -1;
		if (this.zPickMax>geo2.zPickMax)
			return 1;

		//says that the two objects are equal for the comparator
		if (DEBUG){
			DecimalFormat df = new DecimalFormat("0.000000000");
			Application.debug("equality :\n"
					+"zMin= "+df.format(this.zPickMin)+" | zMax= "+df.format(this.zPickMax)+" ("+this.getLabel()+")\n"
					+"zMin= "+df.format(geo2.zPickMin)+" | zMax= "+df.format(geo2.zPickMax)+" ("+geo2.getLabel()+")\n");
		}
		return 0;

		
	}
	
	
	
	
	
	
	

}