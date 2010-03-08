package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;



public class GeoQuadric extends GeoElement3D {
	
	
	// types of quadrics
	public static final int QUADRIC_SINGLE_POINT = 1;
	public static final int QUADRIC_INTERSECTING_PLANES = 2;
	public static final int QUADRIC_ELLIPSOID = 3;
	public static final int QUADRIC_SPHERE = 4;


	
	int type = -1; // of quadric
	/** 
	 *           ( A[0]  A[3]  A[4]  A[6])
	 *  matrix = ( A[3]  A[1]  A[5]  A[7])
	 *           ( A[4]  A[5]  A[2]  A[8])
	 *           ( A[6]  A[7]  A[8]  A[9])
	 */
	private double[] matrix = new double[10]; // flat matrix A
	
	
	/** half axes (for ellipsoids, spheres, ...) */
	private double[] halfAxes = new double[3];
	
	/** translation vector (midpoint, vertex)  */
	private Ggb3DVector translationVector;
	

	public GeoQuadric(Construction c) {
		super(c);
		
		//TODO remove drawing matrix in GeoElement3D
		setDrawingMatrix(Ggb3DMatrix4x4.Identity());
	}
	
	
	
	
	
	////////////////////////////////
	// SPHERE
	
	
	public void setSphere(GeoPoint3D M, double r){
		
		type = GeoQuadric.QUADRIC_SPHERE;
		
		setSphereMatrix(M, r);
		
	}
	

	final private void setSphereMatrix(GeoPoint3D M, double r) {
		
		
		// set halfAxes = radius		
		halfAxes[0] = r; halfAxes[1] = r; halfAxes[2] = r;
		
		
		/* 
		 *               ( 1  0  0  -Mx)
		 *  flatMatrix = ( .  1  0  -My)
		 *               ( .  .  1  -Mz)
		 *               ( .  .  .  Mx²+My²+Mz²-r²)
		 */
		
		Ggb3DVector m = M.getInhomCoords();
		
		matrix[0] = 1;
		matrix[1] = 1;
		matrix[2] = 1;	
		matrix[3] = 0;
		matrix[4] = 0;
		matrix[5] = 0;
		
		matrix[6] = -m.getX();
		matrix[7] = -m.getY();
		matrix[8] = -m.getZ();
		matrix[9] = m.getX()*m.getX() + m.getY()*m.getY() + m.getZ()*m.getZ() - r * r;
		
		translationVector = m;
		
		
	}
	
	
	
	
	
	///////////////////////////////
	// GETTERS
	
	public Ggb3DVector getTranslationVector(){
		return translationVector;
	}
	
	public double getHalfAxis(int i){
		return halfAxes[i];
	}
	
	
	
	
	
	

	///////////////////////////////
	// GeoElement
	
	
    public GeoElement copy() {

        return null;

    }

    public int getGeoClassType() {

        return GEO_CLASS_QUADRIC;

    }
    
    
    /** return type of quadric
     * @return type of quadric
     */
    public int getType(){
    	return type;
    }

    protected String getTypeString() {
		switch (type) {
		case GeoQuadric.QUADRIC_SPHERE: 
			return "Sphere";
 		default:
			return "Quadric";
		}                       


    }

    public boolean isDefined() {

        return true;

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

    public String toValueString() {

        return "todo";

    }

    protected String getClassName() {

        return "GeoQuadric";

    }


    
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
	}





	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}


}
