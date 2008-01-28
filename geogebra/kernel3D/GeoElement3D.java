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

package geogebra.kernel3D;

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

	
	/********************************************************/

	/** Creates new GeoElement for given construction */
	public GeoElement3D(Construction c) {
		super(c);
		//kernel3D= (Kernel3D) getKernel();
	}
	
	//TODO add method with return false in GeoElement
	public boolean isGeoElement3D(){
		return true;
	}
	
	
	
		

	
	
	

}