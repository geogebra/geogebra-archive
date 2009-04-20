package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;

/**
 * Class describing 1D, 2D and 3D coordinate systems.
 * 
 * @author ggb3D
 *
 */
public abstract class GeoCoordSys extends GeoElement3D{

	//matrix for the coord sys
	private Ggb3DMatrix m_matrix;
	private int m_dimension;
	private int m_madeCoordSys;
	
	//matrix completed to 4x4 for drawing, etc.
	private Ggb3DMatrix4x4 m_matrix4x4 = new Ggb3DMatrix4x4();

	
	private GeoCoordSys(Construction c) {
		super(c);
		
	}
	
	/** create a coord sys with a_dimension dimensions, creating m_matrix for this */
	public GeoCoordSys(Construction c, int a_dimension) {
		this(c);
		m_matrix=new Ggb3DMatrix(4,a_dimension+1);
		m_dimension = a_dimension;
		resetCoordSys();
		
	}	
	
	

	
	public Ggb3DMatrix getMatrix(){
		return m_matrix;
	}
	
	
	////////////////////////////
	// setters
	
	public void setOrigin(Ggb3DVector a_O){
		m_matrix.set(a_O,m_dimension+1);
	}
	
	public void setVx(Ggb3DVector a_V){
		setV(a_V,1);
	}
	
	public void setVy(Ggb3DVector a_V){
		setV(a_V,2);
	}
	
	public void setVz(Ggb3DVector a_V){
		setV(a_V,3);
	}
	
	public void setV(Ggb3DVector a_V, int i){
		m_matrix.set(a_V,i);
	}
	
	public Ggb3DVector getV(int i){
		return m_matrix.getColumn(i);
	}
	
	public Ggb3DVector getOrigin(){
		return getV(m_dimension+1);
	}
	
	public Ggb3DVector getVx(){
		return getV(1);
	}
	
	public Ggb3DVector getVy(){
		return getV(2);
	}
	
	public Ggb3DVector getVz(){
		return getV(3);
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
	public void addPointToCoordSys(Ggb3DVector v, boolean orthonormal){
		
		if (isMadeCoordSys())
			return;
		
		Ggb3DVector v1;
		
		//Application.debug("getMadeCoordSys()="+getMadeCoordSys());
		
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
				setV(v1,1);
				setMadeCoordSys(1);
			}
			break;
		case 1: //add second vector
			v1 = v.sub(getOrigin());
			//calculate normal vector
			Ggb3DVector vn = getVx().crossProduct(v1);
			//check if vn==0
			if (!Kernel.isEqual(vn.norm(), 0, Kernel.STANDARD_PRECISION)){	
				if(orthonormal){
					v1=vn.crossProduct(getVx());
					v1.normalize();
				}
				setV(v1,2);
				setMadeCoordSys(2);
			}
			break;						
		}

		//if the coord sys is made, the drawing matrix is updated
		if (isMadeCoordSys()){
			updateDrawingMatrix();
			//getDrawingMatrix().SystemPrint();
		}
		
	}
	
	
	
	
	public boolean isDefined() {
		return isMadeCoordSys();
	}
	
	public void setUndefined() {
		resetCoordSys();
		
	}
	
	
	
	////////////////////////////////////////
	// drawing matrix
	
	public void updateDrawingMatrix(){
		m_matrix4x4 = new Ggb3DMatrix4x4(m_matrix);
		setDrawingMatrix(m_matrix4x4);
	}
	
	
	/** returns completed matrix for drawing : (V1 V2 V3 O)  */
	public Ggb3DMatrix4x4 getMatrix4x4(){
		return m_matrix4x4;
	}
	

	
}
