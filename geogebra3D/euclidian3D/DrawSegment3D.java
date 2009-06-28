package geogebra3D.euclidian3D;




import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.GeoSegment3D;



public class DrawSegment3D extends Drawable3DSolid {

	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegment3D a_segment3D){
		
		super(a_view3D,a_segment3D);
	}


	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		renderer.drawSegment(); 
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		renderer.drawSegment(); 
	}
		

	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		
		//renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		drawGeometry(renderer);
	} 
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	
	

}
