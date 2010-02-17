package geogebra3D.euclidian3D;


import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoVector3D;

public class DrawVector3D extends Drawable3DCurves {

	
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector3D)
	{
		
		super(a_view3D, a_vector3D);
	}
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		double t = getGeoElement().getLineThickness();
		renderer.setThickness(t);
		renderer.setArrowType(Renderer.ARROW_TYPE_SIMPLE);
		renderer.setArrowLength(10*t);
		renderer.setArrowWidth(5*t);
		

		renderer.drawSegment();
		
		renderer.setArrowType(Renderer.ARROW_TYPE_NONE);		
	}


	
	
	
	
	
	public int getPickOrder() {		
		return DRAW_PICK_ORDER_1D;
	}

	

	protected void updateForItSelf(){
		
		((GeoVector3D) getGeoElement()).updateStartPointPosition();
		
		
	}
	
	protected void updateForView(){
		
	}

}
