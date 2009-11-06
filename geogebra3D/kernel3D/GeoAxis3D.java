package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.EuclidianView3D;

public class GeoAxis3D extends GeoLine3D {
	
	public static final int X_AXIS_3D = 1;
	public static final int Y_AXIS_3D = 2;
	public static final int Z_AXIS_3D = 3;


	public GeoAxis3D(Construction cons) {
		super(cons);
	}
	
	
	public GeoAxis3D(Construction c, int type){
		this(c);
		
		switch (type) {
		case X_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vx);
			label = "xAxis3D";
			setObjColor(Color.BLUE);
			break;

		case Y_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vy);
			label = "yAxis3D";
			setObjColor(Color.RED);
			break;
			
		case Z_AXIS_3D:
			setCoord(EuclidianView3D.o,EuclidianView3D.vz);
			label = "zAxis3D";
			setObjColor(Color.GREEN);
			break;
		}
		
		setFixed(true);
		
		
	}
	
	
	

	protected boolean isAvailableAtConstructionStep(int step) {
		// this method is overwritten
		// in order to make the axes available
		// in empty constructions too (for step == -1)
		return true;
	}




	public boolean isDefined() {
		return true;
	}

	public int getGeoClassType() {
		
		return GEO_CLASS_AXIS3D;
	}

}
