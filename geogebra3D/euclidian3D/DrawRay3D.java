package geogebra3D.euclidian3D;

import geogebra.main.Application;
import geogebra3D.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoRay3D;

public class DrawRay3D extends Drawable3DSolid {

	
	public DrawRay3D(EuclidianView3D a_view, GeoRay3D a_ray)
	{
 		super(a_view, a_ray);
	}
	

	public void drawGeometry(EuclidianRenderer3D renderer) {
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		renderer.drawRay();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		renderer.drawRay();
	}


	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		
		GeoRay3D l_ray3D = (GeoRay3D) getGeoElement();
		double dashLength = 0.12f/((float) l_ray3D.getUnit()); //TODO use object property
		//renderer.drawRayDashed(LINE3D_THICKNESS*getGeoElement().getLineThickness(),dashLength); 
		drawGeometry(renderer);
		
	}
	


	

	

	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

	

}
