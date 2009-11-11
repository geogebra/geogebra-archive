package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoAxis3D;

public class DrawAxis3D extends DrawLine3D {
	
	public DrawAxis3D(EuclidianView3D a_view3D, GeoAxis3D axis3D){
		
		super(a_view3D, axis3D);
		
	}	
	
	public void drawGeometry(Renderer renderer) {
		
		renderer.setArrowType(Renderer.ARROW_TYPE_SIMPLE);
		renderer.setArrowLength(20);
		renderer.setArrowWidth(10);
		

		super.drawGeometry(renderer);
		
		renderer.setArrowType(Renderer.ARROW_TYPE_NONE);
	}
	

}
