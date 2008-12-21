package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoVector3D;

public class DrawVector3D extends Drawable3DSolid {

	
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector3D)
	{
		
		super(a_view3D, a_vector3D);
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

		GeoVector3D l_vector3D = (GeoVector3D) getGeoElement();		
		GgbMatrix l_matrix = l_vector3D.getMatrixCompleted(); 
		setMatrix(l_matrix);

	}

}
