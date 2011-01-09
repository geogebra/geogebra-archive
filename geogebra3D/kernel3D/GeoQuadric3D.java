package geogebra3D.kernel3D;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoQuadricND;
import geogebra.kernel.kernelND.GeoSegmentND;
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
implements GeoElement3DInterface, Functional2Var{
	
	

	
	private static String[] vars3D = { "x\u00b2", "y\u00b2",  "z\u00b2", "x y", "x z", "y z", "x", "y" , "z" };
	
	
	

	public GeoQuadric3D(Construction c) {
		super(c,3);
		
		//TODO merge with 2D eigenvec
		eigenvecND = new GgbVector[3];
		for (int i=0;i<3;i++){
			eigenvecND[i] = new GgbVector(4);
			eigenvecND[i].set(i+1,1);
		}
		
		//diagonal (diagonalized matrix)
		diagonal = new double[4];
		
	}
	
	
	
	/////////////////////////////////
	// MATRIX REPRESENTATION
	/////////////////////////////////
	
	/**
	 * @return the matrix representation of the quadric in its dimension
	 */
	protected GgbMatrix getGgbMatrix(double[] vals){
		
		GgbMatrix ret = new GgbMatrix(4, 4);
		
		ret.set(1, 1, vals[0]);
		ret.set(2, 2, vals[1]);
		ret.set(3, 3, vals[2]);
		ret.set(4, 4, vals[3]);
		
		ret.set(1, 2, vals[4]); ret.set(2, 1, vals[4]);
		ret.set(1, 3, vals[5]); ret.set(3, 1, vals[5]);
		ret.set(2, 3, vals[6]); ret.set(3, 2, vals[6]);
		
		ret.set(1, 4, vals[7]); ret.set(4, 1, vals[7]);
		ret.set(2, 4, vals[8]); ret.set(4, 2, vals[8]);
		ret.set(3, 4, vals[9]); ret.set(4, 3, vals[9]);
		
		return ret;
	}
		
	protected void setMatrix(GgbMatrix m){
		
		matrix[0] = m.get(1, 1);
		matrix[1] = m.get(2, 2);
		matrix[2] = m.get(3, 3);
		matrix[3] = m.get(4, 4);
		
		matrix[4] = m.get(1, 2);
		matrix[5] = m.get(1, 3); 
		matrix[6] = m.get(2, 3); 
		
		matrix[7] = m.get(1, 4); 
		matrix[8] = m.get(2, 4); 
		matrix[9] = m.get(3, 4);
		
	}
	

	
	
	////////////////////////////////
	// EIGENVECTORS
	
	public GgbVector getEigenvec3D(int i){
		return eigenvecND[i];
	}
	
	
	////////////////////////////////
	// SPHERE
	

	
	public void setSphereND(GeoPointND M, GeoSegmentND segment){
		//TODO
	}
	
	public void setSphereND(GeoPointND M, GeoPointND P){
		//TODO do this in GeoQuadricND, implement degenerate cases
		setSphereNDMatrix(M, M.distance(P));
	}
	
	
	////////////////////////////////
	// CONE
	
	public void setCone(GeoPoint3D origin, GeoVector3D direction, double angle){
		
		// check midpoint
		defined = ((GeoElement) origin).isDefined() && !origin.isInfinite(); 
		
		// check direction
		
		
		
		// check angle
		double r;
		double c = Math.cos(angle);
		double s = Math.sin(angle);

		if (c<0 || s<0) 
			defined = false;
		else if (Kernel.isZero(c))
			defined = false;//TODO if c=0 then draws a plane
		else if (Kernel.isZero(s))
			defined = false;//TODO if s=0 then draws a line
		else{
			r=s/c;
			setCone(origin.getCoords().get(), direction.getCoords().normalized(), r);
		} 
		

	}
	
	private void setCone(double[] coords, GgbVector direction, double r){
		 
		// set center
		setMidpoint(coords);
		
		// set direction
		eigenvecND[2] = direction;
		
		// set others eigen vecs
		GgbVector[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = -r*r;
		diagonal[3] = 0;
		
		// set matrix
		setMatrixFromEigen();
		
			
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
		eigenvecND[2] = direction;
		

		// set others eigen vecs
		GgbVector[] ee = direction.completeOrthonormal();
		eigenvecND[0] = ee[0];
		eigenvecND[1] = ee[1];
		
		// set halfAxes = radius	
		for (int i=0;i<2;i++)
			halfAxes[i] = r;
		
		// set the diagonal values
		diagonal[0] = 1;
		diagonal[1] = 1;
		diagonal[2] = 0;
		diagonal[3] = -r*r;
		
		// set matrix
		setMatrixFromEigen();
		
		
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
    	case QUADRIC_CYLINDER:
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
    		
    		return kernel.buildImplicitEquation(coeffs, vars3D, false, true, '=');
    	}
    	
    	return sbToValueString;
    }

    public String getClassName() {

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


	
	

	/////////////////////////////////////////
	// SURFACE (u,v)->(x,y,z) INTERFACE
	/////////////////////////////////////////
	
	public GgbVector evaluatePoint(double u, double v){
		
		GgbVector p;
		
		switch (type){
		case QUADRIC_SPHERE :
			
			GgbVector n = new GgbVector(new double[] {
					Math.cos(u)*Math.cos(v)*getHalfAxis(0),
					Math.sin(u)*Math.cos(v)*getHalfAxis(0),
					Math.sin(v)*getHalfAxis(0)});
			
			return (GgbVector) n.add(getMidpoint());
			
		case QUADRIC_CONE :

			double v2 = Math.abs(v);
			p = (GgbVector) 
			getEigenvec3D(1).mul(Math.sin(u)*getHalfAxis(1)*v2).add(
					getEigenvec3D(0).mul(Math.cos(u)*getHalfAxis(0)*v2).add(
							getEigenvec3D(2).mul(v)
					)
			);
			
			return (GgbVector) p.add(getMidpoint());		
			
		case QUADRIC_CYLINDER :

			p = (GgbVector) 
			getEigenvec3D(1).mul(Math.sin(u)*getHalfAxis(1)).add(
					getEigenvec3D(0).mul(Math.cos(u)*getHalfAxis(0)).add(
							getEigenvec3D(2).mul(v)
					)
			);
			
			return (GgbVector) p.add(getMidpoint());					
			
		default:
			return null;
		}

	}
	

	public GgbVector evaluateNormal(double u, double v){
				
		GgbVector n;
		
		switch (type){
		case QUADRIC_SPHERE :
			return new GgbVector(new double[] {
					Math.cos(u)*Math.cos(v),
					Math.sin(u)*Math.cos(v),
					Math.sin(v)});
			
		case QUADRIC_CONE :

			double r = getHalfAxis(0);
			double r2 = Math.sqrt(1+r*r);
			if (v<0)
				r=-r;
			
			n = (GgbVector) 
			getEigenvec3D(1).mul(Math.sin(u)/r2).add(
					getEigenvec3D(0).mul(Math.cos(u)/r2).add(
							getEigenvec3D(2).mul(-r/r2)
					)
			);
			
			return n;
			
			
		case QUADRIC_CYLINDER :
			
			n = (GgbVector) 
			getEigenvec3D(1).mul(Math.sin(u)).add(
					getEigenvec3D(0).mul(Math.cos(u))
			);
			
			return n;
			
		default:
			return null;
		}
		
		
	}
	
	
	
	
	
	public double getMinParameter(int index) {
		
		switch (type){
		case QUADRIC_SPHERE :
			switch(index){
			case 0: //u
			default:
				return 0;
			case 1: //v
				return -Math.PI/2;
			}
		case QUADRIC_CONE :
		case QUADRIC_CYLINDER :
			switch(index){
			case 0: //u
			default:
				return 0;
			case 1: //v
				return Double.NEGATIVE_INFINITY;
			}
			
			
		default:
			return 0;
		}

		
	}
	

	public double getMaxParameter(int index) {
		
		switch (type){
		case QUADRIC_SPHERE :
			switch(index){
			case 0: //u
			default:
				return 2*Math.PI; 
			case 1: //v
				return Math.PI/2;
			}
			
		case QUADRIC_CONE :
		case QUADRIC_CYLINDER :
			switch(index){
			case 0: //u
			default:
				return 2*Math.PI; 
			case 1: //v
				return Double.POSITIVE_INFINITY;
			}
		default:
			return 0;
		}
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




	

	public GgbVector getLabelPosition(){
		return new GgbVector(4); //TODO
	}





	public GgbVector getMainDirection() {
		// TODO Auto-generated method stub
		return null;
	}





	public boolean hasGeoElement2D() {
		// TODO Auto-generated method stub
		return false;
	}













	public void setDrawingMatrix(GgbMatrix4x4 aDrawingMatrix) {
		// TODO Auto-generated method stub
		
	}





	public void setGeoElement2D(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}







}
