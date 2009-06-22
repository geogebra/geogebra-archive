package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;

public class GeoVector3D extends GeoVec4D {

	public GeoVector3D(Construction c) {
		super(c);
	}

	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c,x,y,z,0);
	}
	
	
	public void setCoords(double[] vals){
		super.setCoords(vals);
		
		//sets the drawing matrix 
		Ggb3DMatrix matrix = new Ggb3DMatrix(4,2);
		matrix.set(getCoords(), 1);
		
		//TODO use start point
		matrix.set(4, 2, 1.0);
		
		setDrawingMatrix(new Ggb3DMatrix4x4(matrix));
		
	}




	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}


	public int getGeoClassType() {
		return GEO_CLASS_VECTOR3D;		
	}


	protected String getTypeString() {
		return "Vector3D";
	}


	public boolean isDefined() {
		return true;
	}


	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}


	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}


	public void setUndefined() {
		// TODO Auto-generated method stub

	}


	public boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}


	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}


	public String toValueString() {
		// TODO Auto-generated method stub
		return "toValueString-todo";
	}

	
	protected String getClassName() {
		return "GeoVector3D";
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
