package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;

public abstract class GeoCoordSys extends GeoElement3D{

	//matrix for the coord sys
	private Ggb3DMatrix m_matrix;
	private int m_dimension;
	
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
		
	}	
	
	

	
	public Ggb3DMatrix getMatrix(){
		return m_matrix;
	}
	
	public void setOrigin(Ggb3DVector a_O){
		m_matrix.set(a_O,m_dimension+1);
	}
	
	public void setVx(Ggb3DVector a_V){
		m_matrix.set(a_V,1);
	}
	
	public void setVy(Ggb3DVector a_V){
		m_matrix.set(a_V,2);
	}
	
	public void setVz(Ggb3DVector a_V){
		m_matrix.set(a_V,3);
	}
	
	
	public void updateDrawingMatrix(){
		m_matrix4x4 = new Ggb3DMatrix4x4(m_matrix);
		setDrawingMatrix(m_matrix4x4);
	}
	
	
	/** returns completed matrix for drawing : (V1 V2 V3 O)  */
	public Ggb3DMatrix4x4 getMatrix4x4(){
		return m_matrix4x4;
	}
	

	
}
