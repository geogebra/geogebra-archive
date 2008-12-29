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
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbMatrix4x4;


/**
 *
 * @author  ggb3D
 * @version 
 */
public abstract class GeoElement3D
	extends GeoElement{
	
	private static final boolean DEBUG = false;
	
	
	private boolean wasHighlighted = false;
	private boolean willBeHighlighted = false;
	
	
	//link to drawable3D
	private GgbMatrix4x4 m_drawingMatrix = null;
	
	//link to GeoElement
	protected GeoElement geo2D = null;
	
	
	
	
	// GeoElement3D types 
	public static final int GEO_CLASS_POINT3D = 3010;
	public static final int GEO_CLASS_VECTOR3D = 3011;
	public static final int GEO_CLASS_SEGMENT3D = 3110;
	public static final int GEO_CLASS_LINE3D = 3120;
	public static final int GEO_CLASS_RAY3D = 3121;
	public static final int GEO_CLASS_TRIANGLE3D = 3210;
	public static final int GEO_CLASS_POLYGON3D = 3211;
	public static final int GEO_CLASS_PLANE3D = 3220;
	


	
	/********************************************************/

	/** Creates new GeoElement for given construction */
	public GeoElement3D(Construction c) {
		super(c);
		
	}

	
	
	public boolean isGeoElement3D(){
		return true;
	}

	
	/** returns a 4x4 matrix for drawing the drawable */
	public GgbMatrix4x4 getDrawingMatrix(){
		return m_drawingMatrix;
	}
	
	/** sets the 4x4 matrix for drawing the drawable */
	public void setDrawingMatrix(GgbMatrix4x4 a_drawingMatrix){
		m_drawingMatrix = a_drawingMatrix;
	}	
	
	
	// Path1D interface
	public boolean isPath1D(){
		return false;
	}
	
	public boolean hasPathOn() {
		return false;
	}	
	
	
	// Path2D interface
	public boolean isPath2D(){
		return false;
	}
	
	public boolean hasPathIn() {
		return false;
	}
	
	// link to GeoElement
    public boolean hasGeoElement2D() {
    	return false;
    }
    

    public GeoElement getGeoElement2D(){ //TODO turn it to abstract
    	return null; 
    }
    
    public void setGeoElement2D(GeoElement geo){ 
    	this.geo2D = geo;
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
	
	
	
	

	

	
	
	
	

}