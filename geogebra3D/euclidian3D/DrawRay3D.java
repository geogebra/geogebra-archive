package geogebra3D.euclidian3D;

import java.util.ArrayList;

import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoRay3D;

public class DrawRay3D extends DrawCoordSys1D {

	
	public DrawRay3D(EuclidianView3D a_view, GeoRay3D a_ray)
	{
 		super(a_view, a_ray);
	}
	

	public void drawGeometry(EuclidianRenderer3D renderer) {
		//renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		super.drawGeometry(renderer);
		renderer.drawRay();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		//renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		super.drawGeometryPicked(renderer);
		renderer.drawRay();
	}


	/*
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		
		GeoRay3D l_ray3D = (GeoRay3D) getGeoElement();
		double dashLength = 0.12f/((float) l_ray3D.getUnit()); //TODO use object property
		//renderer.drawRayDashed(LINE3D_THICKNESS*getGeoElement().getLineThickness(),dashLength); 
		drawGeometry(renderer);
		
	}
	*/
	


	

	

	/*
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

	*/
	
	

	////////////////////////////////
	// Previewable interface 
	
	
	public DrawRay3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoRay3D(a_view3D.getKernel().getConstruction()));
		

		
	}	

}
