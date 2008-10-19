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

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement; 
//TODO in GeoElement, change private to protected


/**
 *
 * @author  Mathieu
 * @version 
 */
public abstract class GeoElement3D
	extends GeoElement{
	
	//transient Kernel3D kernel3D;

	
	// GeoElement types
	public static final int GEO_CLASS_POINT3D = 1130;
	public static final int GEO_CLASS_SEGMENT3D = 1131;
	public static final int GEO_CLASS_PLANE3D = 1132;
	public static final int GEO_CLASS_TRIANGLE3D = 1133;
	public static final int GEO_CLASS_LINE3D = 1134;

	
	/********************************************************/

	/** Creates new GeoElement for given construction */
	public GeoElement3D(Construction c) {
		super(c);
		
	}
	
	
	public boolean isGeoElement3D(){
		return true;
	}
	
	
	/** set the highlighted flag and eventually repaint it for algebraView */ 
	final public void setHighlighted(boolean flag, boolean repaint) {
		boolean highlightedOld = highlighted;
		setHighlighted(flag); //GeoElement method
		if ((highlightedOld!=highlighted)&&repaint)
			updateRepaint();//for highlighting in algebraView
		
	}	
	
		

	
	
	

}