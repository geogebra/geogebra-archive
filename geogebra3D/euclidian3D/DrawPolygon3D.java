package geogebra3D.euclidian3D;




import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoPolygon3D;




public class DrawPolygon3D extends Drawable3DSurfaces {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon3D a_polygon3D){
		
		super(a_view3D, a_polygon3D);
	}
	

	
	//drawing

	public void drawGeometry(Renderer renderer) {

		
		
		renderer.setLayer(getGeoElement().getLayer());

		renderer.startPolygonAndInitMatrix();
		GeoPolygon3D polygon = (GeoPolygon3D) getGeoElement();



		for(int i=0;i<polygon.getNumPoints();i++){
			renderer.addToPolygon(polygon.getPointX(i), polygon.getPointY(i));
		}

		renderer.endPolygonAndResetMatrix();
		
		renderer.setLayer(0);
			

	}
	public void drawGeometryPicked(Renderer renderer){
		drawGeometry(renderer);
	}
	public void drawGeometryHiding(Renderer renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(Renderer renderer){};
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	public int getType(){
		if (((GeoPolygon3D) getGeoElement()).isPartOfClosedSurface())
			return DRAW_TYPE_CLOSED_SURFACES;
		else
			return DRAW_TYPE_SURFACES;
	}
	
	

	

}
