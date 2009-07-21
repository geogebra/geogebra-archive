package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoQuadric;

public class DrawQuadric extends Drawable3DTransparent {

	public DrawQuadric(EuclidianView3D a_view3d, GeoQuadric a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.drawSphere(1);
	}

	void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}

	public void drawGeometryHidden(Renderer renderer) {
		// TODO Auto-generated method stub

	}

	public void drawGeometryPicked(Renderer renderer) {
		// TODO Auto-generated method stub

	}


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}

}
