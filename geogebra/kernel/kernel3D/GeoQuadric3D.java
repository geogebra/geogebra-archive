package geogebra.kernel.kernel3D;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
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
		
		
		
		
		
		GgbMatrix diagonalizedMatrix = GgbMatrix.DiagonalMatrix(diagonal);
		
		GgbMatrix eigenMatrix = new GgbMatrix(4, 4);
		eigenMatrix.set(eigenvecND);
		eigenMatrix.set(getMidpoint(),4);
		
		GgbMatrix eigenMatrixInv = eigenMatrix.inverse();
		
		GgbMatrix finalMatrix = eigenMatrixInv.transposeCopy().mul(diagonalizedMatrix).mul(eigenMatrixInv);
		
		
		
		Application.debug("matrix:\n"+getGgbMatrix().toString());
		Application.debug("mul:\n"+
				finalMatrix
				.toString()
						);
		
		//Application.debug("diagonalized:\n"+diagonalizedMatrix.toString());
		Application.debug("eigen:\n"+eigenMatrix.toString());
		*/
			
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
		return true;
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
