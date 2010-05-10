package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Brush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Surface;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoFunction2Var;
import geogebra3D.kernel3D.GeoQuadric3D;

public class DrawFunction2Var extends Drawable3DSurfaces {
	
	
	/** gl index of the geometry */
	private int geometryIndex = -1;


	public DrawFunction2Var(EuclidianView3D a_view3d, GeoFunction2Var function) {
		
		super(a_view3d, function);
		
	}
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(geometryIndex);
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
		
		renderer.getGeometryManager().remove(geometryIndex);
		

		
		
		Surface surface = renderer.getGeometryManager().getSurface();
		GeoFunction2Var geo = (GeoFunction2Var) getGeoElement();
		surface.start(geo);
		surface.setU((float) geo.getMinParameter(0), (float) geo.getMaxParameter(0));surface.setDeltaU(0.1f);
		surface.setV((float) geo.getMinParameter(1), (float) geo.getMaxParameter(1));surface.setDeltaV(0.1f);
		surface.draw();
		geometryIndex=surface.end();
		
		
		
		
		
	}

	
	
	
	
	protected void updateForView(){
		
	}
	
	
	
	
	
	
	
	


	public int getPickOrder() {
		return DRAW_PICK_ORDER_2D;
	}
	

	public int getType(){

			return DRAW_TYPE_CLOSED_SURFACES;

			//return DRAW_TYPE_SURFACES;
		
	}

}
