package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

public class GeoVector3D extends GeoVec4D {

	public GeoVector3D(Construction c) {
		super(c);
	}

	public GeoVector3D(Construction c, double x, double y, double z) {
		super(c,x,y,z,0);
	}

	/** returns completed matrix for drawing */
	public GgbMatrix getMatrixCompleted(){
		GgbMatrix m = new GgbMatrix(4,4);
		GgbVector 
			Vn1 = new GgbVector(4), 
			Vn2 = new GgbVector(4), 
			V = getCoords();
		if (V.get(1)!=0){
			Vn1.set(1,-V.get(2));
			Vn1.set(2,V.get(1));
			Vn1.normalize();
		}else{
			Vn1.set(1, 1.0);
		}		
		Vn2 = V.crossProduct(Vn1);
		Vn2.normalize();

		m.set(new GgbVector[] {getCoords(), Vn1, Vn2, new GgbVector(new double[]{0,0,0,1})});
		return m;
	}

	@Override
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGeoClassType() {
		return GEO_CLASS_VECTOR3D;		
	}

	@Override
	protected String getTypeString() {
		return "Vector3D";
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String toValueString() {
		// TODO Auto-generated method stub
		return "toValueString-todo";
	}

	@Override
	protected String getClassName() {
		return "GeoVector3D";
	}

}
