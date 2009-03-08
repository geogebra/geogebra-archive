package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra.kernel.GeoPolygon;


/**
 * Class extending {@link GeoPolygon} in 3D world.
 * 
 * @author ggb3D
 *
 */
public class GeoPolygon3D 
extends GeoPolygon implements GeoElement3DInterface {

	
	private GeoCoordSys2D coordSys; 
	
	/** link with drawable3D */
	private Drawable3D drawable3D = null;
	
	public GeoPolygon3D(Construction c, GeoPoint[] points) {
		super(c, points);
		setUseVisualDefaults(false);
		setAlphaValue(ConstructionDefaults3D.DEFAULT_POLYGON3D_ALPHA);
		
	}


	/////////////////////////////////////////
	// GeoPolygon3D
	public int getGeoClassType() {
		return GeoElement3D.GEO_CLASS_POLYGON3D;
	}
	
	
	/**
	 * it's a 3D GeoElement.
	 * @return true
	 */
	public boolean isGeoElement3D(){
		return true;
	}

	
	
	
	
	/////////////////////////////////////////
	// link with the 2D coord sys
	
	
	/** set the 2D coordinate system
	 * @param cs the 2D coordinate system
	 */
	public void setCoordSys(GeoCoordSys2D cs){
		this.coordSys = cs;
	}
	
	
	
	/** return the 2D coordinate system
	 * @return the 2D coordinate system
	 */
	public GeoCoordSys2D getCoordSys(){
		return coordSys;
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
	
	
	
	
	
	public Ggb3DMatrix4x4 getDrawingMatrix() {
		if (coordSys!=null)
			return coordSys.getDrawingMatrix();
		else
			return null;
	}

	
	public void setDrawingMatrix(Ggb3DMatrix4x4 matrix) {
		coordSys.setDrawingMatrix(matrix);

	}

	
	
	
	
    /** set the alpha value to alpha for openGL
     * @param alpha alpha value
     */
	public void setAlphaValue(float alpha) {

		alphaValue = alpha;

	}
	
	
	
	public GeoElement getGeoElement2D() {
		return null;
	}

	public boolean hasGeoElement2D() {
		return false;
	}


	public void setGeoElement2D(GeoElement geo) {

	}

}
