package geogebra3D.euclidian3D;



import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;





public class DrawPoint3D extends Drawable3DSolid{
	
	
	
	
	
		
	
	public DrawPoint3D(EuclidianView3D a_view3D, GeoPoint3D a_point3D) {     
		
		super(a_view3D, a_point3D);
		
	}
	
	
	
	
	public void updateDrawingMatrix() {

		GeoPoint3D l_point = (GeoPoint3D) getGeoElement();    
		GgbMatrix l_matrix = new GgbMatrix(4,4);
		l_matrix.set(l_point.getCoords(), 4);
		//TODO use point "thickness" and view3D scaling
		for(int i=1;i<=3;i++){
			l_matrix.set(i,i,0.001);
		}
		setMatrix(l_matrix);

	}
	

	public void drawPrimitive(EuclidianRenderer3D renderer) {
		GeoPoint3D l_point = (GeoPoint3D) getGeoElement(); 
		if (l_point.hasPathOn())
			renderer.drawSphere(POINT3D_RADIUS*POINT_ON_PATH_DILATATION); //points on path are more visible 
		else
			renderer.drawSphere(POINT3D_RADIUS);//TODO use object property
	}
	
	public void drawPrimitivePicked(EuclidianRenderer3D renderer){
		
		renderer.drawSphere(POINT3D_RADIUS*PICKED_DILATATION);//TODO use object property
		
	}


	

	
	public void drawHidden(EuclidianRenderer3D renderer){
		draw(renderer);
		
	}	
	

	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	

}
