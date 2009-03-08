package geogebra3D.euclidian3D;




import geogebra.main.Application;
import geogebra3D.kernel3D.GeoPolygon3D;




public class DrawPolygon3D extends Drawable3DTransparent {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoPolygon3D a_polygon3D){
		
		super(a_view3D, a_polygon3D);
	}
	

	
	//drawing

	public void drawGeometry(EuclidianRenderer3D renderer) {
		
		if (getGeoElement().isDefined()){
			renderer.startPolygon();
			GeoPolygon3D polygon = (GeoPolygon3D) getGeoElement();
			
			//Application.debug("polygon points : "+polygon.getNumPoints());
			//Application.debug("polygon points : "+polygon.getNumPoints());
			
			//getMatrix().SystemPrint();
			
			for(int i=0;i<polygon.getNumPoints();i++){
				renderer.addToPolygon(polygon.getPointX(i), polygon.getPointY(i));
				//Application.debug("point["+i+"]=("+polygon.getPointX(i)+","+polygon.getPointY(i)+")");
			}
			
			renderer.endPolygon();
			
		}
		//renderer.drawPolygon(((GeoPolygon3D)getGeoElement3D()).getVertices());
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
