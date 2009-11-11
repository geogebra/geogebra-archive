package geogebra3D.euclidian3D;




import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPlane3D;




public class DrawPlane3D extends Drawable3DSurfaces {



	
	
	public DrawPlane3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	

	public void drawGeometry(Renderer renderer) {
		//GeoPlane3D p = (GeoPlane3D) getGeoElement();
		
		//renderer.setMaterial(getGeoElement().getObjectColor(),1);
		//renderer.drawQuad(p.getXmin(),p.getYmin(),p.getXmax(),p.getYmax());
		
		renderer.drawPlane();
		
					
	}
	
	
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	
	public void drawGeometryPicked(Renderer renderer){}
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	public void drawHighlighting(Renderer renderer){

	};	
	
	

	
	

	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}		
	
	
	
	

}
