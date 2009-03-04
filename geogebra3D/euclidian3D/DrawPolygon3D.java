package geogebra3D.euclidian3D;




import geogebra3D.kernel3D.GeoPolygon3D;




public class DrawPolygon3D extends Drawable3DTransparent {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon3D a_polygon3D){
		
		super(a_view3D, a_polygon3D);
	}
	

	
	//drawing

	public void drawGeometry(EuclidianRenderer3D renderer) {
		if (((GeoPolygon3D)getGeoElement3D()).isDefined())
			renderer.drawPolygon(((GeoPolygon3D)getGeoElement3D()).getVertices());
	}
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		drawGeometry(renderer);
	}
	public void drawGeometryHiding(EuclidianRenderer3D renderer) {
		drawGeometry(renderer);
	}
	
	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){};
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	
	

	

}
