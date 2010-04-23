package geogebra.Matrix;

import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Kernel;

/**
 * Class describing 1D, 2D and 3D coordinate systems.
 * 
 * @author ggb3D
 *
 */
public class GgbCoordSys {

	//matrix for the coord sys
	private GgbMatrix matrix;
	private int m_dimension;
	private int m_madeCoordSys;
	private GgbMatrix4x4 matrix4x4;
	

	
	/** create a coord sys with a_dimension dimensions, creating m_matrix for this */
	public GgbCoordSys(int a_dimension) {
		matrix=new GgbMatrix(4,a_dimension+1);
		m_dimension = a_dimension;
		resetCoordSys();
		
	}	
	
	

	
	public GgbMatrix getMatrix(){
		return matrix;
	}
	
	
	public GgbCoordSys getCoordSys(){
		return this;
	}
	
	public int getDimension(){
		return m_dimension;
	}
	
	////////////////////////////
	// setters
	
	public void setOrigin(GgbVector a_O){
		matrix.set(a_O,m_dimension+1);
	}
	
	public void setVx(GgbVector a_V){
		setV(a_V,1);
	}
	
	public void setVy(GgbVector a_V){
		setV(a_V,2);
	}
	
	public void setVz(GgbVector a_V){
		setV(a_V,3);
	}
	
	public void setV(GgbVector a_V, int i){
		matrix.set(a_V,i);
	}
	
	public GgbVector getV(int i){
		return matrix.getColumn(i);
	}
	
	public GgbVector getOrigin(){
		return getV(m_dimension+1);
	}
	
	public GgbVector getVx(){
		return getV(1);
	}
	
	public GgbVector getVy(){
		return getV(2);
	}
	
	public GgbVector getVz(){
		return getV(3);
	}
	
	
	
	public GgbVector getPoint(double x, double y){
		//return (GgbVector) getOrigin().add(getVx().mul(x).add(getVy().mul(y)));
		return (GgbVector) matrix4x4.getOrigin().add(matrix4x4.getVx().mul(x).add(matrix4x4.getVy().mul(y)));
	}
	
	
	public GgbVector getNormal(){
		return getVx().crossProduct(getVy()).normalized();
	}
	
	/////////////////////////////////////
	//
	// FOR REGION3D INTERFACE
	//
	/////////////////////////////////////
	
	
	public GgbVector[] getNormalProjection(GgbVector coords) {
		return coords.projectPlane(this.getMatrix4x4());
	}

	public GgbVector[] getProjection(GgbVector coords,
			GgbVector willingDirection) {
		return coords.projectPlaneThruV(this.getMatrix4x4(),willingDirection);
	}
	
	
	///////////////////////////////////////
	// creating a coord sys
	
	
	/**
	 * set how much the coord sys is made
	 * @param i value of made coord sys
	 */
	public void setMadeCoordSys(int i){
		m_madeCoordSys = i;
	}
	
	/**
	 * set the coord sys is finish
	 */
	public void setMadeCoordSys(){
		setMadeCoordSys(m_dimension);
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
		return m_madeCoordSys;
	}
	
	/** return if the coord sys is made
	 * @return if the coord sys is made
	 */
	public boolean isMadeCoordSys(){
		return (getMadeCoordSys()==m_dimension);
	}
	
	
	/**
	 * Try to add the point described by v to complete the coord sys.
	 * @param v a point (x,y,z,1)
	 * @param orthonormal say if the coord sys has to be orthonormal
	 * 
	 */
	public void addPointToCoordSys(GgbVector v, boolean orthonormal){
		
		addPointToCoordSys(v, orthonormal, false);
	
	}
	
	
	
	/**
	 * Try to add the point described by v to complete the coord sys.
	 * @param v a point (x,y,z,1)
	 * @param orthonormal say if the coord sys has to be orthonormal
	 * @param standardCS says if the coord sys has to be "standard": 
	 * (0,0,0) projected for origin,
	 * and vector Vx parallel to xOy plane
	 * 
	 */
	public void addPointToCoordSys(GgbVector v, boolean orthonormal, boolean standardCS){
		
		if (isMadeCoordSys())
			return;
		
		GgbVector v1;
		
		
		switch(getMadeCoordSys()){
		case -1: //add the origin
			setOrigin(v);
			setMadeCoordSys(0);
			break;
		case 0: //add first vector
			v1 = v.sub(getOrigin());
			//check if v==0
			if (!Kernel.isEqual(v1.norm(), 0, Kernel.STANDARD_PRECISION)){		
				if(orthonormal)
					v1.normalize();
				setVx(v1);
				setMadeCoordSys(1);
			}
			break;
		case 1: //add second vector
			v1 = v.sub(getOrigin());
			//calculate normal vector
			GgbVector vn = getVx().crossProduct(v1);
			//check if vn==0
			if (!Kernel.isEqual(vn.norm(), 0, Kernel.STANDARD_PRECISION)){	
				if(orthonormal){
					v1=vn.crossProduct(getVx());
					v1.normalize();
				}
				setVy(v1);
				setMadeCoordSys(2);
			}
			break;						
		}

		//if the coord sys is made, the drawing matrix is updated
		if (isMadeCoordSys()){
			
			updateMatrix4x4();
			
			if (standardCS && m_dimension==2){
				
				
				
				// (0,0,0) projected for origin
				GgbVector o = new GgbVector(new double[] {0,0,0,1});
				setOrigin(o.projectPlane(getMatrix4x4())[0]);
				
				// vector Vx parallel to xOy plane
				GgbVector vn = getVx().crossProduct(getVy());
				GgbVector vz = new GgbVector(new double[] {0,0,1,0});
				GgbVector vx = vn.crossProduct(vz);
				if (!Kernel.isEqual(vx.norm(), 0, Kernel.STANDARD_PRECISION)){
					vx.normalize();
					setVx(vx);
					GgbVector vy = vn.crossProduct(vx);
					vy.normalize();
					setVy(vy);
				} else {
					setVx(new GgbVector(new double[] {1,0,0,0}));
					setVy(new GgbVector(new double[] {0,1,0,0}));
				}
				

			}
		}
		
	}
	
	
	
	
	public boolean isDefined() {
		return isMadeCoordSys();
	}
	
	public void setUndefined() {
		resetCoordSys();
		
	}
	
	
	
	
	private void updateMatrix4x4(){
		matrix4x4 = new GgbMatrix4x4(matrix);
	}
	
	
	/** returns completed matrix for drawing : (V1 V2 V3 O)  */
	public GgbMatrix4x4 getMatrix4x4(){
		return matrix4x4;
	}

	

	

	
	
}
