package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.GeoQuadricND;
import geogebra.kernel.GeoSegmentInterface;
import geogebra.main.Application;
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
	
	

	//TODO merge with 2D eigenvec
	private GgbVector[] eigenvec3D;

	
	private static String[] vars3D = { "x\u00b2", "y\u00b2",  "z\u00b2", "x y", "x z", "y z", "x", "y" , "z" };
	
	
	

	public GeoQuadric3D(Construction c) {
		super(c,3);
		
		//TODO merge with 2D eigenvec
		eigenvec3D = new GgbVector[3];
		for (int i=0;i<3;i++){
			eigenvec3D[i] = new GgbVector(4);
			eigenvec3D[i].set(i+1,1);
		}
		
	}
	
	
	
	
	////////////////////////////////
	// EIGENVECTORS
	
	public GgbVector getEigenvec3D(int i){
		return eigenvec3D[i];
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
	
	
	////////////////////////////////
	// CONE
	
	public void setCone(GeoPoint3D origin, GeoVector3D direction, double r){
		
		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite(); 
		
		// check direction
		
		
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCone(origin.getCoords().get(), direction.getCoords().normalized(), r);
		} 
		

	}
	
	private void setCone(double[] coords, GgbVector direction, double r){
		 
		// set center
		setMidpoint(coords);
		
		// set direction
		eigenvec3D[2] = direction;
		
		// set halfAxes = radius	
		for (int i=1;i<3;i++)
			halfAxes[i] = r;
		
		// set matrix
		double xd = direction.getX(); double xd2 = xd*xd; double xd3 = xd2*xd; double xd4 = xd3*xd;
		double yd = direction.getY(); double yd2 = yd*yd; double yd3 = yd2*yd; double yd4 = yd3*yd;
		double zd = direction.getZ(); double zd2 = zd*zd; double zd3 = zd2*zd; double zd4 = zd3*zd;
		double x0 = midpoint.getInhomCoords().getX(); double x02 = x0*x0;
		double y0 = midpoint.getInhomCoords().getY(); double y02 = y0*y0;
		double z0 = midpoint.getInhomCoords().getZ(); double z02 = z0*z0;
		double r2 = r*r;
		
		//P,xd^2=xd2,yd^2=yd2,zd^2=zd2,xd^3=xd3,yd^3=yd3,zd^3=zd3,xd^4=xd4,yd^4=yd4,zd^4=zd4,x0^2=x02,y0^2=y02,z0^2=z02,r^2=r2;
		matrix[0]=xd2*zd2+xd2*yd2+xd4-r2*xd2-2*xd2+1; //x²
		matrix[1]=yd2*zd2+yd4+xd2*yd2-r2*yd2-2*yd2+1; //y²
		matrix[2]=zd4+yd2*zd2+xd2*zd2-r2*zd2-2*zd2+1; //z²
		matrix[3]=z02*zd4+2*y0*yd*z0*zd3+2*x0*xd*z0*zd3+yd2*z02*zd2+xd2*z02*zd2-2*z02*zd2+y02*yd2*zd2+2*x0*xd*y0*yd*zd2+x02*xd2*zd2+2*y0*yd3*z0*zd+2*x0*xd*yd2*z0*zd+2*xd2*y0*yd*z0*zd-4*y0*yd*z0*zd+2*x0*xd3*z0*zd-4*x0*xd*z0*zd+r2*z0*zd+z02+y02*yd4+2*x0*xd*y0*yd3+xd2*y02*yd2-2*y02*yd2+x02*xd2*yd2+2*x0*xd3*y0*yd-4*x0*xd*y0*yd+r2*y0*yd+y02+x02*xd4-2*x02*xd2+r2*x0*xd+x02; //1
		matrix[4]=xd*yd*zd2+xd*yd3+xd3*yd-r2*xd*yd-2*xd*yd; //xy
		matrix[5]=xd*zd3+xd*yd2*zd+xd3*zd-r2*xd*zd-2*xd*zd; //xz
		matrix[6]=yd*zd3+yd3*zd+xd2*yd*zd-r2*yd*zd-2*yd*zd; //yz
		matrix[7]=-xd*z0*zd3-xd*y0*yd*zd2-x0*xd2*zd2-xd*yd2*z0*zd-xd3*z0*zd+r2*xd*z0*zd+2*xd*z0*zd-xd*y0*yd3-x0*xd2*yd2-xd3*y0*yd+r2*xd*y0*yd+2*xd*y0*yd-x0*xd4+r2*x0*xd2+2*x0*xd2-x0; //x
		matrix[8]=-yd*z0*zd3-y0*yd2*zd2-x0*xd*yd*zd2-yd3*z0*zd-xd2*yd*z0*zd+r2*yd*z0*zd+2*yd*z0*zd-y0*yd4-x0*xd*yd3-xd2*y0*yd2+r2*y0*yd2+2*y0*yd2-x0*xd3*yd+r2*x0*xd*yd+2*x0*xd*yd-y0; //y
		matrix[9]=-z0*zd4-y0*yd*zd3-x0*xd*zd3-yd2*z0*zd2-xd2*z0*zd2+r2*z0*zd2+2*z0*zd2-y0*yd3*zd-x0*xd*yd2*zd-xd2*y0*yd*zd+r2*y0*yd*zd+2*y0*yd*zd-x0*xd3*zd+r2*x0*xd*zd+2*x0*xd*zd-z0; //z
		
		// set type
		type = QUADRIC_CONE;
	}
	
	

	////////////////////////////////
	// CONE
	
	public void setCylinder(GeoPoint3D origin, GeoVector3D direction, double r){
		
		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite(); 
		
		// check direction
		
		
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCylinder(origin.getCoords().get(), direction.getCoords().normalized(), r);
		} 
		

	}
	
	private void setCylinder(double[] coords, GgbVector direction, double r){
		 
		// set center
		setMidpoint(coords);
		
		// set direction
		eigenvec3D[2] = direction;
		
		// set halfAxes = radius	
		for (int i=1;i<3;i++)
			halfAxes[i] = r;
		
		// TODO set matrix
		/* 
		double xd = direction.getX(); double xd2 = xd*xd; double xd3 = xd2*xd; double xd4 = xd3*xd;
		double yd = direction.getY(); double yd2 = yd*yd; double yd3 = yd2*yd; double yd4 = yd3*yd;
		double zd = direction.getZ(); double zd2 = zd*zd; double zd3 = zd2*zd; double zd4 = zd3*zd;
		double x0 = midpoint.getInhomCoords().getX(); double x02 = x0*x0;
		double y0 = midpoint.getInhomCoords().getY(); double y02 = y0*y0;
		double z0 = midpoint.getInhomCoords().getZ(); double z02 = z0*z0;
		double r2 = r*r;
		
		//P,xd^2=xd2,yd^2=yd2,zd^2=zd2,xd^3=xd3,yd^3=yd3,zd^3=zd3,xd^4=xd4,yd^4=yd4,zd^4=zd4,x0^2=x02,y0^2=y02,z0^2=z02,r^2=r2;
		matrix[0]=xd2*zd2+xd2*yd2+xd4-r2*xd2-2*xd2+1; //x²
		matrix[1]=yd2*zd2+yd4+xd2*yd2-r2*yd2-2*yd2+1; //y²
		matrix[2]=zd4+yd2*zd2+xd2*zd2-r2*zd2-2*zd2+1; //z²
		matrix[3]=z02*zd4+2*y0*yd*z0*zd3+2*x0*xd*z0*zd3+yd2*z02*zd2+xd2*z02*zd2-2*z02*zd2+y02*yd2*zd2+2*x0*xd*y0*yd*zd2+x02*xd2*zd2+2*y0*yd3*z0*zd+2*x0*xd*yd2*z0*zd+2*xd2*y0*yd*z0*zd-4*y0*yd*z0*zd+2*x0*xd3*z0*zd-4*x0*xd*z0*zd+r2*z0*zd+z02+y02*yd4+2*x0*xd*y0*yd3+xd2*y02*yd2-2*y02*yd2+x02*xd2*yd2+2*x0*xd3*y0*yd-4*x0*xd*y0*yd+r2*y0*yd+y02+x02*xd4-2*x02*xd2+r2*x0*xd+x02; //1
		matrix[4]=xd*yd*zd2+xd*yd3+xd3*yd-r2*xd*yd-2*xd*yd; //xy
		matrix[5]=xd*zd3+xd*yd2*zd+xd3*zd-r2*xd*zd-2*xd*zd; //xz
		matrix[6]=yd*zd3+yd3*zd+xd2*yd*zd-r2*yd*zd-2*yd*zd; //yz
		matrix[7]=-xd*z0*zd3-xd*y0*yd*zd2-x0*xd2*zd2-xd*yd2*z0*zd-xd3*z0*zd+r2*xd*z0*zd+2*xd*z0*zd-xd*y0*yd3-x0*xd2*yd2-xd3*y0*yd+r2*xd*y0*yd+2*xd*y0*yd-x0*xd4+r2*x0*xd2+2*x0*xd2-x0; //x
		matrix[8]=-yd*z0*zd3-y0*yd2*zd2-x0*xd*yd*zd2-yd3*z0*zd-xd2*yd*z0*zd+r2*yd*z0*zd+2*yd*z0*zd-y0*yd4-x0*xd*yd3-xd2*y0*yd2+r2*y0*yd2+2*y0*yd2-x0*xd3*yd+r2*x0*xd*yd+2*x0*xd*yd-y0; //y
		matrix[9]=-z0*zd4-y0*yd*zd3-x0*xd*zd3-yd2*z0*zd2-xd2*z0*zd2+r2*z0*zd2+2*z0*zd2-y0*yd3*zd-x0*xd*yd2*zd-xd2*y0*yd*zd+r2*y0*yd*zd+2*y0*yd*zd-x0*xd3*zd+r2*x0*xd*zd+2*x0*xd*zd-z0; //z
		*/
		
		
		// set type
		type = QUADRIC_CYLINDER;
	}

	
	
	
	
	

	///////////////////////////////
	// GeoElement
	
	
    public GeoElement copy() {

        return null;

    }

    public int getGeoClassType() {

        return GeoElement3D.GEO_CLASS_QUADRIC;

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
    	
    	switch (type) {					
    	case QUADRIC_SPHERE :	
    		buildSphereNDString();
    		break;
    	case QUADRIC_CONE:
    		double[] coeffs = new double[10];
    		coeffs[0] = matrix[0]; // x²
    		coeffs[1] = matrix[1]; // y²
    		coeffs[2] = matrix[2]; // z²
    		coeffs[9] = matrix[3]; // constant
    		
    		coeffs[3] = 2 * matrix[4]; // xy        
    		coeffs[4] = 2 * matrix[5]; // xz
    		coeffs[5] = 2 * matrix[6]; // yz
    		coeffs[6] = 2 * matrix[7]; // x      
    		coeffs[7] = 2 * matrix[8]; // y
    		coeffs[8] = 2 * matrix[9]; // z
    		
    		return kernel.buildImplicitEquation(coeffs, vars3D, false, true);
    	}
    	
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





	public GgbVector getViewDirection() {
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
