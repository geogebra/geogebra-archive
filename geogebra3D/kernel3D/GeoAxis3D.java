package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

public class GeoAxis3D extends GeoLine3D {

	public GeoAxis3D(Construction cons) {
		super(cons);
	}
	
	
	public GeoAxis3D(Construction c, Ggb3DVector o, Ggb3DVector v){
		super(c,o,v);
		
		
	}
	
	
	




	public boolean isDefined() {
		return true;
	}

	public int getGeoClassType() {
		
		return GEO_CLASS_AXIS3D;
	}

}
