package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoRay3D;

public class DrawRay3D extends Drawable3DSolid {

	
	public DrawRay3D(EuclidianView3D a_view, GeoRay3D a_ray)
	{
 		super(a_view, a_ray);
	}
	

	public void drawPrimitive(EuclidianRenderer3D renderer) {
		renderer.drawCylinder(LINE3D_THICKNESS);
	}
	
	public void drawPrimitivePicked(EuclidianRenderer3D renderer){
		renderer.drawCylinder(LINE3D_THICKNESS*PICKED_DILATATION);
	}



	


	
	public void drawHidden(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	

	

	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

	


	
	public void updateDrawingMatrix() {

		GeoRay3D l_ray = (GeoRay3D) getGeoElement();
		GgbMatrix l_matrix = l_ray.getSegmentMatrix(0,21);  //TODO use frustrum
		setMatrix(l_matrix);


		//dashLength = 0.12f/((float) L.getUnit()); //TODO use object property

	}

}
