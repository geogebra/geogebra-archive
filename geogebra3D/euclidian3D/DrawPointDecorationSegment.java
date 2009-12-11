package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;

public class DrawPointDecorationSegment extends DrawCoordSys1D {

	public DrawPointDecorationSegment(EuclidianView3D aView3d) {
		super(aView3d);
		
		setDrawMinMax(0, 1);
	}

	
	
	public void draw(Renderer renderer) {
		
		//Application.debug("hop");

	}
	
	

	protected void updateForView() {

	}

}
