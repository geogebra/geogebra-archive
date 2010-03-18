package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoQuadric3D;

public class DrawQuadric3D extends Drawable3DSurfaces {
	
	
	/** gl index of the quadric */
	private int quadricIndex = -1;


	public DrawQuadric3D(EuclidianView3D a_view3d, GeoQuadric3D a_quadric) {
		
		super(a_view3d, a_quadric);
		
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(quadricIndex);
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
	
	
	
	
	
	
	
	
	
	
	
	protected void updateForItSelf(){
		
		
		super.updateForItSelf();
		
		
		Renderer renderer = getView3D().getRenderer();
		
		renderer.getGeometryManager().remove(quadricIndex);
		
		//creates the polygon
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();
		
		GgbVector center = quadric.getMidpoint();
		double r = quadric.getHalfAxis(0);
		
		
		quadricIndex =  renderer.getGeometryManager().newSphere(
				(float) center.get(1),(float) center.get(2),(float) center.get(3),
				(float) r,
				quadric.getObjectColor(),
				alpha);
				//(float) (200/getView3D().getScale()));
		
		
		
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){
		switch(((GeoQuadric3D) getGeoElement()).getType()){
		case GeoQuadric3D.QUADRIC_SPHERE:
			return DRAW_TYPE_CLOSED_SURFACES;
		default:
			return DRAW_TYPE_SURFACES;
		}
	}

}
