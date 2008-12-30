package geogebra3D.euclidian3D;


import geogebra3D.kernel3D.GeoVector3D;

public class DrawVector3D extends Drawable3DSolid {

	
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector3D)
	{
		
		super(a_view3D, a_vector3D);
	}
	
	
	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		renderer.drawSegment(); 
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		renderer.drawSegment(); 
	}
	

	public void drawGeometryHidden(EuclidianRenderer3D renderer){};
	
	


	
	public int getPickOrder() {		
		return DRAW_PICK_ORDER_1D;
	}

	



}
