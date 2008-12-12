package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.linalg.GgbMatrix;

public abstract class GeoCoordSys extends GeoElement3D{

	//matrice du repere
	GgbMatrix M;
	//matrice pour le dessin
	GgbMatrix matrixCompleted = new GgbMatrix(4,4);

	
	public GeoCoordSys(Construction c) {
		super(c);
		
	}
	

	
	public GgbMatrix getMatrix(){
		return M;
	}
	

	
}
