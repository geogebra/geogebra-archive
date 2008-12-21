package geogebra3D.euclidian3D;




import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoTriangle3D;




public class DrawPolygon3D extends Drawable3DTransparent {


	
	
	public DrawPolygon3D(EuclidianView3D a_view3D, GeoTriangle3D a_triangle3D){
		
		super(a_view3D, a_triangle3D);
	}
	

	public void updateDrawingMatrix() {
		
		GeoTriangle3D l_triangle3D = (GeoTriangle3D) getGeoElement();
		GgbMatrix l_matrix = l_triangle3D.getMatrix4x4(); 
		setMatrix(l_matrix);

	}
	
	
	
	
	//drawing

	public void drawPrimitive(EuclidianRenderer3D renderer) {
		renderer.drawTriangle();
	}
	public void drawPrimitivePicked(EuclidianRenderer3D renderer){
		renderer.drawTriangle();
	}
	
	
	
	
	
	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_2D;
	}	
	
	

	
	

	

}
