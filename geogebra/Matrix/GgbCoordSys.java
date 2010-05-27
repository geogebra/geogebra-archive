package geogebra.Matrix;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * Class describing 1D, 2D and 3D coordinate systems.
 * 
 * @author ggb3D
 *
 */
public class GgbCoordSys {

	//matrix for the coord sys
	private GgbMatrix matrix;
	private int dimension;
	private int madeCoordSys;
	private GgbMatrix4x4 matrixOrthonormal;
	
	private GgbVector origin;
	private GgbVector[] vectors;
	
	/** dimension of the space (2 for 2D, 3 for 3D, ...) */
	private int spaceDimension = 3;
	

	
	/** create a coord sys  
	 * @param dimension number of vectors of the coord sys
	 */
	public GgbCoordSys(int dimension) {
		matrix=new GgbMatrix(4,4);
		matrixOrthonormal = new GgbMatrix4x4();
		this.dimension = dimension;
		
		origin = new GgbVector(spaceDimension+1); 
		origin.set(spaceDimension+1, 1);
		vectors = new GgbVector[spaceDimension];
		for(int i=0;i<spaceDimension;i++){
			vectors[i] = new GgbVector(spaceDimension+1); 
		}
		
		
		
		resetCoordSys();
		
	}	
	
	

	
	public GgbMatrix getMatrix(){
		return matrix;
	}
	
	
	
	public int getDimension(){
		return dimension;
	}
	
	////////////////////////////
	// setters
	
	public void setOrigin(GgbVector o){
		origin.set(o);
	}
	
	public void setVx(GgbVector v){
		setV(v,0);
	}
	
	public void setVy(GgbVector v){
		setV(v,1);
	}
	
	public void setVz(GgbVector v){
		setV(v,2);
	}
	
	public void setV(GgbVector v, int i){
		vectors[i].set(v);
	}
	
	public GgbVector getV(int i){
		return vectors[i];
	}
	
	public GgbVector getOrigin(){
		return origin;
	}
	
	public GgbVector getVx(){
		return getV(0);
	}
	
	public GgbVector getVy(){
		return getV(1);
	}
	
	public GgbVector getVz(){
		return getV(2);
	}
	
	
	public GgbVector getPoint(GgbVector coords2D){
		return getPoint(coords2D.getX(), coords2D.getY());
	}
	
	public GgbVector getPoint(double x, double y){
		return (GgbVector) matrixOrthonormal.getOrigin().add(getVector(x,y));
	}
	
	public GgbVector getPoint(double x){
		return (GgbVector) getOrigin().add(getVx().mul(x));
	}
	
	public GgbVector getVector(GgbVector coords2D){
		return getVector(coords2D.getX(), coords2D.getY());
	}	
	
	public GgbVector getVector(double x, double y){
		return (GgbVector) matrixOrthonormal.getVx().mul(x).add(matrixOrthonormal.getVy().mul(y));
	}
	
	
	public GgbVector getNormal(){
		return matrixOrthonormal.getVz();//getVx().crossProduct(getVy()).normalized();
	}
	
	/////////////////////////////////////
	//
	// FOR REGION3D INTERFACE
	//
	/////////////////////////////////////
	
	
	public GgbVector[] getNormalProjection(GgbVector coords) {
		return coords.projectPlane(this.getMatrixOrthonormal());
	}

	public GgbVector[] getProjection(GgbVector coords,
			GgbVector willingDirection) {
		return coords.projectPlaneThruV(this.getMatrixOrthonormal(),willingDirection);
	}
	
	
	///////////////////////////////////////
	// creating a coord sys
	
	
	/**
	 * set how much the coord sys is made
	 * @param i value of made coord sys
	 */
	public void setMadeCoordSys(int i){
		madeCoordSys = i;
	}
	
	/**
	 * set the coord sys is finish
	 */
	public void setMadeCoordSys(){
		setMadeCoordSys(dimension);
	}
	
	/**
	 * reset the coord sys
	 */
	public void resetCoordSys(){
		setMadeCoordSys(-1);
	}
	
	/** return how much the coord sys is made
	 * @return how much the coord sys is made
	 */
	public int getMadeCoordSys(){
		return madeCoordSys;
	}
	
	/** return if the coord sys is made
	 * @return if the coord sys is made
	 */
	public boolean isMadeCoordSys(){
		return (getMadeCoordSys()==dimension);
	}
	
	
	
	
	/**
	 * Try to add the point described by p to complete the coord sys.
	 * @param p a point (x,y,z,1)
	 * 
	 */
	public void addPoint(GgbVector p){
		
		if (isMadeCoordSys())
			return;
		
		if(getMadeCoordSys()==-1){
			//add the origin
			setOrigin(p);
			setMadeCoordSys(0);
		}else{
			//point is the end of a vector
			addVectorWithoutCheckMadeCoordSys(p.sub(getOrigin()));
			
		}

		
		
	}
	
	
	/**
	 * Try to add the vector described by v to complete the coord sys.
	 * @param v a vector (x,y,z,1)
	 * 
	 */
	public void addVector(GgbVector v){
		
		if (isMadeCoordSys())
			return;
		
		addVectorWithoutCheckMadeCoordSys(v);
		
	}
	
	
	/**
	 * Try to add the vector described by v to complete the coord sys.
	 * @param v a vector (x,y,z,1)
	 * 
	 */
	private void addVectorWithoutCheckMadeCoordSys(GgbVector v){
			
		
		switch(getMadeCoordSys()){
		case 0: //add first vector
			//check if v==0
			if (!Kernel.isEqual(v.norm(), 0, Kernel.STANDARD_PRECISION)){	
				setVx(v);
				setMadeCoordSys(1);
			}
			break;
		case 1: //add second vector
			//calculate normal vector to check if v1 depends to vx
			GgbVector vn = getVx().crossProduct(v);
			//check if vn==0
			if (!Kernel.isEqual(vn.norm(), 0, Kernel.STANDARD_PRECISION)){	
				setVy(v);
				setVz(getVx().crossProduct(getVy()));
				setMadeCoordSys(2);
			}
			break;						
		}
		
		//Application.printStacktrace("v["+getMadeCoordSys()+"]=\n"+v);

		
		
	}
	

	
	
	/** makes an orthonormal matrix describing this coord sys
	 * @param projectOrigin if true, origin of the coord sys is the projection of 0
	 * @return true if it's possible
	 */
	public boolean makeOrthoMatrix(boolean projectOrigin){
		
		
		//if the coord sys is made, the drawing matrix is updated
		if (!isMadeCoordSys())
			return false;
			

		if (dimension==1){ 
			//compute Vy and Vz
			GgbVector vy = (new GgbVector(new double[] {0,0,1,0})).crossProduct(getVx());
			// check if vy=0 (if so, vx is parallel to Oz)
			if (vy.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
				setVy(new GgbVector(new double[] {1,0,0,0}));
				setVz(new GgbVector(new double[] {0,1,0,0}));
			}else{
				setVy(vy);
				setVz(getVx().crossProduct(getVy()));
			}
			
			//sets orthonormal matrix
			matrixOrthonormal.set(new GgbVector[] {
					getVx().normalized(),
					getVy().normalized(),
					getVz().normalized(),
					getOrigin()});
			
			if (projectOrigin)
				projectOrigin();
			
			return true;
			
		}
		
		if (dimension==2){ //vy and Vz are computed

			// vector Vx parallel to xOy plane
			GgbVector vz = new GgbVector(new double[] {0,0,1,0});
			GgbVector vx = getVz().crossProduct(vz);
			GgbVector vy;
			//if (!Kernel.isEqual(vx.norm(), 0, Kernel.STANDARD_PRECISION)){
			if (!vx.equalsForKernel(0, Kernel.STANDARD_PRECISION)){
				vx.normalize();
				vy = getVz().crossProduct(vx);
				vy.normalize();
				vz = getVz().normalized();
			} else {
				vx = new GgbVector(new double[] {1,0,0,0});
				vy = new GgbVector(new double[] {0,1,0,0});
			}

			
			GgbVector o = getOrigin();
			matrixOrthonormal.set(new GgbVector[] {vx,vy,vz,o});
			
			if (projectOrigin)
				projectOrigin();
			
			
			//Application.debug("matrix ortho=\n"+getMatrixOrthonormal());
		
			return true;
		}
		
		return false;
		
	}
	
	
	/**
	 * project 0 for origin
	 */
	private void projectOrigin(){
		GgbVector o = (new GgbVector(new double[] {0,0,0,1})).projectPlane(getMatrixOrthonormal())[0];
		matrixOrthonormal.set(o, 4);
	}
	
	
	public boolean isDefined() {
		return isMadeCoordSys();
	}
	
	public void setUndefined() {
		resetCoordSys();
		
	}
	
	
	
	
	
	/** returns orthonormal matrix   */
	public GgbMatrix4x4 getMatrixOrthonormal(){
		return matrixOrthonormal;
	}

	

	

	
	
}
