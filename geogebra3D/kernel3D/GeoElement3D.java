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
import geogebra3D.Test3D;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;


/**
 * Class for describing GeoElement in 3D version.
 * 
   <p>
   
   <h3> How to create a new Element </h3>
   <p>
   
   We'll call here our new element "GeoNew3D"
   
   <p>
   
   <b> In GeoElement3D (this class), create an new constant to identify GeoNew3D </b>
   <p>
   <code> public static final int GEO_CLASS_NEW3D = 30??; </code>
   <p>
   
   <b> Create an new class GeoNew3D </b> 
   <ul>
   <li> It will eventually extend another class (at least GeoElement3D) :
   <p>
   <code>
   final public class GeoNew3D extends ??? {
   </code>
   </li>
   <li> Eclipse will add auto-generated methods ; modify it : 
   <p>
   <code>
    public GeoElement copy() {
        return null;
    }
    <br>
    public int getGeoClassType() {
       return GEO_CLASS_NEW3D;
    }
   <br>
    protected String getTypeString() {
        return "New3D";
    }
   <br>
    public boolean isDefined() {
       return true;
    }
   <br>
    public boolean isEqual(GeoElement Geo) {
       return false;
    }
   <br>
    public void set(GeoElement geo) {

    }
   <br>
    public void setUndefined() {

    }
   <br>
    protected boolean showInAlgebraView() {
        return true;
    }
   <br>
    protected boolean showInEuclidianView() {
       return true;
    }
   <br>
    public String toValueString() {
        return "todo";
    }
    <br>
    protected String getClassName() {
        return "GeoNew3D";
    }
  </code>
  </li>
  <li> Create a constructor <p>
  <code>
    public GeoNew3D(Construction c, ?? args) { <br> &nbsp;&nbsp;
        super(c); // eventually + args <br> &nbsp;&nbsp;
        + stuff <br>
    }
   </code>
   </li>     
   </ul>
   
   	<h3> See </h3> 
	<ul>
	<li> {@link Drawable3D} to create a drawable linked to this new element.
	</li>
	<li> {@link Kernel3D} to add a method to create this new element 
	</li> 
	<li> {@link Test3D#Test3D(Kernel3D, geogebra.euclidian.EuclidianView)} to test it
	</li> 
	</ul>

   
 * 
 * 
 *
 * @author  ggb3D
 * 
 */
public abstract class GeoElement3D
	extends GeoElement implements GeoElement3DInterface{
	
	
	
	
	/** matrix used as orientation by the {@link Drawable3D} */
	private Ggb3DMatrix4x4 m_drawingMatrix = null;
	
	/** matrix used to draw the label by the {@link Drawable3D} */
	private Ggb3DMatrix4x4 labelMatrix = null;
	
	/** for some 3D element (like conics, polygons, etc), a 2D GeoElement is linked to (for calculation) */
	private GeoElement geo2D = null;

	
	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	
	/** says if it's a pickable object */
	private boolean isPickable = true;
	
	
	
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
	/** id for {@link GeoConic3D} */
	public static final int GEO_CLASS_CONIC3D = 3122;
	/** id for {@link GeoAxis3D} */
	public static final int GEO_CLASS_AXIS3D = 3123;


	/** id for {@link GeoPolygon3D} */
	public static final int GEO_CLASS_POLYGON3D = 3211;
	/** id for {@link GeoPlane3D} */
	public static final int GEO_CLASS_PLANE3D = 3220;
	/** id for {@link GeoQuadric} */
	public static final int GEO_CLASS_QUADRIC = 3230;
	
	/** id for {@link GeoPolyhedron} */
	public static final int GEO_CLASS_POLYHEDRON = 3310;


	
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
	
	
	/** returns a 4x4 matrix for drawing the label
	 * @return the label drawing matrix*/
	public Ggb3DMatrix4x4 getLabelMatrix(){
		return labelMatrix;
	}
	
	/** sets the 4x4 matrix for drawing the {@link Drawable3D} and the label
	 * @param a_drawingMatrix the drawing matrix*/
	public void setDrawingMatrix(Ggb3DMatrix4x4 a_drawingMatrix){
		this.m_drawingMatrix = a_drawingMatrix;
	}	
	
	/** sets the 4x4 matrix for drawing the label
	 * @param a_drawingMatrix the drawing matrix*/
	public void setLabelMatrix(Ggb3DMatrix4x4 labelMatrix){
		this.labelMatrix = labelMatrix;
	}
	
	/** sets the pickability of the object
	 * @param v pickability
	 */
	public void setIsPickable(boolean v){
		isPickable = v;
	}
	
	/** says if the object is pickable
	 * @return true if the object is pickable
	 */
	public boolean isPickable(){
		return isPickable;
	}
	
	
	// link to 2D GeoElement
    /**
     * return if linked to a 2D GeoElement
     * @return has a 2D GeoElement
     */
    public boolean hasGeoElement2D() {
    	return (geo2D!=null);
    }
    

    /**
     * return the 2D GeoElement linked to
     * @return 2D GeoElement
     */
    public GeoElement getGeoElement2D(){ 
    	return geo2D; 
    }
    
    
    /**
     * set the 2D GeoElement linked to
     * @param geo a 2D GeoElement
     */
    public void setGeoElement2D(GeoElement geo){ 
    	this.geo2D = geo;
    }
	
	
    /** set the alpha value to alpha for openGL
     * @param alpha alpha value
     */
	public void setAlphaValue(float alpha) {

		alphaValue = alpha;

	}
	


	/////////////////////////////////////////
	// link with Drawable3D
	
	/**
	 * set the 3D drawable linked to
	 * @param d the 3D drawable 
	 */
	public void setDrawable3D(Drawable3D d){
		drawable3D = d;
	}
	
	/** return the 3D drawable linked to
	 * @return the 3D drawable linked to
	 */
	public Drawable3D getDrawable3D(){
		return drawable3D;
	}
	
	
	////////////////////////////
	// for toString()
	
	private StringBuffer sbToString;
	protected StringBuffer getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuffer(50);
		return sbToString;
	}
	
	
	
	/////////////////////////////////////////
	// ExpressionValue implementation
	
	public boolean isVector3DValue() {
		return false;
	}
	
	
	
	
	

}