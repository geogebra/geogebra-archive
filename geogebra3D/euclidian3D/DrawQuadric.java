package geogebra3D.euclidian3D;

import geogebra3D.kernel3D.GeoQuadric;

public class DrawQuadric extends Drawable3DTransparent {

	public DrawQuadric(EuclidianView3D a_view3d, GeoQuadric a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.drawSphere(1,40);
	}

	void drawGeometryHiding(EuclidianRenderer3D renderer) {
		drawGeometry(renderer);
	}

	public void drawGeometryHidden(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	public void drawGeometryPicked(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}

}
