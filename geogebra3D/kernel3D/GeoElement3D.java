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
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;


/**
 * Class for describing GeoElement in 3D version.
 * 
 *
 * @author  ggb3D
 * 
 */
public abstract class GeoElement3D
	extends GeoElement{
	
	
	
	
	/** matrix used as orientation by the {@link Drawable3D} */
	private Ggb3DMatrix4x4 m_drawingMatrix = null;
	
	/** for some 3D element (like conics, polygons, etc), a 2D GeoElement is linked to (for calculation) */
	private GeoElement geo2D = null;
	
	
	
	// GeoElement3D types 
	/** id for {@link GeoPoint3D} */
	public static final int GEO_CLASS_POINT3D = 3010;
	/** id for {@link GeoVector3D} */
	public static final int GEO_CLASS_VECTOR3D = 3011;
	/** id for {@link GeoSegment3D} */
	public static final int GEO_CLASS_SEGMENT3D = 3110;
	/** id for {@link GeoLine3D} */
	public static final int GEO_CLASS_LINE3D = 3120;
	/** id for {@link GeoRay3D} */
	public static final int GEO_CLASS_RAY3D = 3121;
	/** TODO remove */
	public static final int GEO_CLASS_TRIANGLE3D = 3210;
	/** id for {@link GeoPolygon3D} */
	public static final int GEO_CLASS_POLYGON3D = 3211;
	/** id for {@link GeoPlane3D} */
	public static final int GEO_CLASS_PLANE3D = 3220;
	/** id for {@link GeoPolygon3D} */
	public static final int GEO_CLASS_QUADRIC = 3230;
	


	
	/********************************************************/

	/** Creates new GeoElement for given construction 
	 * @param c construction*/
	public GeoElement3D(Construction c) {
		super(c);
		
	}

	
	/**
	 * it's a 3D GeoElement.
	 * @return true
	 */
	public boolean isGeoElement3D(){
		return true;
	}

	
	/** returns a 4x4 matrix for drawing the {@link Drawable3D} 
	 * @return the drawing matrix*/
	public Ggb3DMatrix4x4 getDrawingMatrix(){
		return m_drawingMatrix;
	}
	
	/** sets the 4x4 matrix for drawing the {@link Drawable3D} 
	 * @param a_drawingMatrix the drawing matrix*/
	public void setDrawingMatrix(Ggb3DMatrix4x4 a_drawingMatrix){
		m_drawingMatrix = a_drawingMatrix;
	}	
	
	
	// Path1D interface
	public boolean isPath1D(){
		return false;
	}
	
	/**
	 * @return false
	 */
	public boolean hasPath() {
		return false;
	}	
	
	
	// Path2D interface
	public boolean isPath2D(){
		return false;
	}
	
	public boolean hasPathIn() {
		return false;
	}
	
	
	
	
	// link to 2D GeoElement
    /**
     * return if liked to a 2D GeoElement
     * @return has a 2D GeoElement
     */
    public boolean hasGeoElement2D() {
    	return (geo2D!=null);
    }
    

    /**
     * return the 2D GeoElement liked to
     * @return 2D GeoElement
     */
    public GeoElement getGeoElement2D(){ 
    	return geo2D; 
    }
    
    
    /**
     * set the 2D GeoElement liked to
     * @param geo a 2D GeoElement
     */
    public void setGeoElement2D(GeoElement geo){ 
    	this.geo2D = geo;
    }
	
	
	
	

	

	
	
	
	

}