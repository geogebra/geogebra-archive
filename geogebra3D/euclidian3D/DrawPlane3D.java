package geogebra3D.euclidian3D;




import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3DSurfaces {


	/** gl index of the plane */
	private int planeIndex = -1;

	
	
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	

	public void drawGeometry(Renderer renderer) {
		//GeoPlane3D p = (GeoPlane3D) getGeoElement();
		
		//renderer.setMaterial(getGeoElement().getObjectColor(),1);
		//renderer.drawQuad(p.getXmin(),p.getYmin(),p.getXmax(),p.getYmax());
		
		//renderer.drawPlane();
		renderer.initMatrix();
		renderer.getGeometryManager().draw(planeIndex);
		renderer.resetMatrix();
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	public void drawHighlighting(Renderer renderer){

	};	
	
	

	
	protected void updateForItSelf(){
		
		
		super.updateForItSelf();

		Renderer renderer = getView3D().getRenderer();
		
		/*
		if (renderer.getGeometryManager() == null)
			return;
			*/
		
		renderer.getGeometryManager().remove(planeIndex);
		
		GeoPlane3D geo = (GeoPlane3D) getGeoElement();
		
		
		planeIndex = renderer.getGeometryManager().newPlane(geo.getObjectColor(),alpha);
		
		//Application.debug("plane : "+geo.getLabel()+", index = "+planeIndex);
	}

	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	

}
