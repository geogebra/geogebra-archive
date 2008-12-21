package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbMatrix4x4;
import geogebra.kernel.linalg.GgbVector;

public abstract class GeoCoordSys extends GeoElement3D{

	//matrix for the coord sys
	private GgbMatrix m_matrix;
	private int m_dimension;
	
	//matrix completed to 4x4 for drawing, etc.
	private GgbMatrix m_matrix4x4 = new GgbMatrix(4,4);

	
	private GeoCoordSys(Construction c) {
		super(c);
		
	}
	
	/** create a coord sys with a_dimension dimensions, creating m_matrix for this */
	public GeoCoordSys(Construction c, int a_dimension) {
		this(c);
		m_matrix=new GgbMatrix(4,a_dimension+1);
		m_dimension = a_dimension;
		
	}	
	
	

	
	public GgbMatrix getMatrix(){
		return m_matrix;
	}
	
	public void setOrigin(GgbVector a_O){
		m_matrix.set(a_O,m_dimension+1);
	}
	
	public void setVector(GgbVector a_V, int a_column){
		m_matrix.set(a_V,a_column);
	}
	
	
	public void updateDrawingMatrix(){
		m_matrix4x4 = GgbMatrix4x4.toMatrix4x4(m_matrix);
	}
	
	
	/** returns completed matrix for drawing : (V1 V2 V3 O)  */
	public GgbMatrix getMatrix4x4(){
		return m_matrix4x4.copy();
	}
	

	
}
