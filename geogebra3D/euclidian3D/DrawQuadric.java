package geogebra3D.euclidian3D;

import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPolygon3D;
import geogebra3D.kernel3D.GeoQuadric;

public class DrawQuadric extends Drawable3DSurfaces {
	
	
	/** gl index of the quadric */
	private int quadricIndex = -1;


	public DrawQuadric(EuclidianView3D a_view3d, GeoQuadric a_quadric) {
		
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
		GeoQuadric quadric = (GeoQuadric) getGeoElement();
		
		Ggb3DVector center = quadric.getTranslationVector();
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
		switch(((GeoQuadric) getGeoElement()).getType()){
		case GeoQuadric.QUADRIC_SPHERE:
			return DRAW_TYPE_CLOSED_SURFACES;
		default:
			return DRAW_TYPE_SURFACES;
		}
	}

}
