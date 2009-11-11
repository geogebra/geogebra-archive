package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Color;

public class GeoPlane3DConstant extends GeoPlane3D {
	
	public static final int XOY_PLANE = 1;

	/** construct the plane xOy, ...
	 * @param c
	 * @param type
	 */
	public GeoPlane3DConstant(Construction c, int type) {
		
		super(c);
		
		
		switch (type) {
		case XOY_PLANE:
			setCoord(EuclidianView3D.o,EuclidianView3D.vx,EuclidianView3D.vy);
			label = "xOyPlane";
			setObjColor(new Color(0.5f,0.5f,0.5f));
			setLabelVisible(false);
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

	
}
