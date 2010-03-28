package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoQuadricND;
import geogebra.kernel.GeoSegmentInterface;
import geogebra3D.euclidian3D.Drawable3D;



/** class describing quadric for 3D space
 * @author matthieu
 *
 *           ( A[0]  A[4]  A[5]  A[7])
 *  matrix = ( A[4]  A[1]  A[6]  A[8])
 *           ( A[5]  A[6]  A[2]  A[9])
 *           ( A[7]  A[8]  A[9]  A[3])
 *
 */
public class GeoQuadric3D extends GeoQuadricND
implements GeoElement3DInterface{
	
	


	
	
	
	
	

	public GeoQuadric3D(Construction c) {
		super(c,3);
		
	}
	
	
	
	
	
	////////////////////////////////
	// SPHERE
	

	
	public void setSphereND(GeoPointInterface M, GeoSegmentInterface segment){
		//TODO
	}
	
	public void setSphereND(GeoPointInterface M, GeoPointInterface P){
		//TODO do this in GeoQuadricND, implement degenerate cases
		setSphereNDMatrix(M, M.distance(P));
	}
	
	
	
	
	///////////////////////////////
	// GETTERS
	

	
	public double getHalfAxis(int i){
		return halfAxes[i];
	}
	
	
	
	
	
	

	///////////////////////////////
	// GeoElement
	
	
    public GeoElement copy() {

        return null;

    }

    public int getGeoClassType() {

        return GeoElement3D.GEO_CLASS_QUADRIC;

    }
    
    
    /** return type of quadric
     * @return type of quadric
     */
    public int getType(){
    	return type;
    }

    protected String getTypeString() {
		switch (type) {
		case GeoQuadric3D.QUADRIC_SPHERE: 
			return "Sphere";
 		default:
			return "Quadric";
		}                       


    }

    public boolean isEqual(GeoElement Geo) {

        return false;

    }

    public void set(GeoElement geo) {

    }

    public void setUndefined() {

    }

    public boolean showInAlgebraView() {

        return true;

    }

    protected boolean showInEuclidianView() {

        return true;

    }


    
    protected StringBuilder buildValueString() {
    	
    	sbToValueString().setLength(0);	
    	
    	buildSphereNDString();
    	
    	return sbToValueString;
    }

    protected String getClassName() {

        return "GeoQuadric";

    }


    
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}



	public boolean isGeoElement3D(){
		return true;
	}


	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}


	
	
	///////////////////////////////////////////
	// GEOELEMENT3D INTERFACE
	///////////////////////////////////////////


	private Drawable3D drawable3D = null;

	public Drawable3D getDrawable3D() {
		return drawable3D;
	}
	
	public void setDrawable3D(Drawable3D d){
		drawable3D = d;
	}
	
	





	public GgbMatrix4x4 getDrawingMatrix() {
		// TODO Auto-generated method stub
		return null;
	}





	public GeoElement getGeoElement2D() {
		// TODO Auto-generated method stub
		return null;
	}





	public GgbMatrix4x4 getLabelMatrix() {
		// TODO Auto-generated method stub
		return null;
	}





	public GgbVector getNormal() {
		// TODO Auto-generated method stub
		return null;
	}





	public boolean hasGeoElement2D() {
		// TODO Auto-generated method stub
		return false;
	}





	public boolean isPickable() {
		// TODO Auto-generated method stub
		return false;
	}










	public void setDrawingMatrix(GgbMatrix4x4 aDrawingMatrix) {
		// TODO Auto-generated method stub
		
	}





	public void setGeoElement2D(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}





	public void setIsPickable(boolean v) {
		// TODO Auto-generated method stub
		
	}


}
