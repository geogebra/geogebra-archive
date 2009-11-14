package geogebra3D.kernel3D;

import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;



/**
 * GeoElement3D's common interface, used for special cases 
 * where the GeoElement3D must extend a GeoElement (e.g. GeoPolygon3D extends GeoPolygon).
 * 
 * See {@link GeoElement3D} for standard implementation.
 * 
 * @author ggb3D
 *
 */
public interface GeoElement3DInterface {
	
	/** returns a 4x4 matrix for drawing the {@link Drawable3D} 
	 * @return the drawing matrix*/
	public Ggb3DMatrix4x4 getDrawingMatrix();
	
	/** returns a 4x4 matrix for drawing the label
	 * @return the label drawing matrix*/
	public Ggb3DMatrix4x4 getLabelMatrix();
	
	/** sets the 4x4 matrix for drawing the {@link Drawable3D} 
	 * @param a_drawingMatrix the drawing matrix*/
	public void setDrawingMatrix(Ggb3DMatrix4x4 a_drawingMatrix);
	

	
	// link to 2D GeoElement
    /**
     * return if linked to a 2D GeoElement
     * @return has a 2D GeoElement
     */
    public boolean hasGeoElement2D();
    

    /**
     * return the 2D GeoElement linked to
     * @return 2D GeoElement
     */
    public GeoElement getGeoElement2D();
    
    
    /**
     * set the 2D GeoElement linked to
     * @param geo a 2D GeoElement
     */
    public void setGeoElement2D(GeoElement geo);
	
	


	/////////////////////////////////////////
	// link with Drawable3D
	
	/**
	 * set the 3D drawable linked to
	 * @param d the 3D drawable 
	 */
	public void setDrawable3D(Drawable3D d);
	
	/** return the 3D drawable linked to
	 * @return the 3D drawable linked to
	 */
	public Drawable3D getDrawable3D();

	
	/** says if the object is pickable
	 * @return true if the object is pickable
	 */
	public boolean isPickable();
	
	
	/** sets the pickability of the object
	 * @param v pickability
	 */
	public void setIsPickable(boolean v);

	


}
