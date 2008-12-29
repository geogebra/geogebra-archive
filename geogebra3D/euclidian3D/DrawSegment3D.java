package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoSegment3D;



public class DrawSegment3D extends Drawable3DSolid {

	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegment3D a_segment3D){
		
		super(a_view3D,a_segment3D);
	}


	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.drawSegment(LINE3D_THICKNESS*getGeoElement().getLineThickness()); 
	}
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		renderer.drawSegment(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness()); 
	}
		

	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		
		GeoSegment3D l_segment3D = (GeoSegment3D) getGeoElement();
		dashLength = 0.12f/((float) l_segment3D.getLength()); //TODO use object property
		
		//renderer.drawSegmentDashed(LINE3D_THICKNESS*getGeoElement().getLineThickness(),dashLength); 
		drawGeometry(renderer);
	} 
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	
	

}
