package geogebra3D.euclidian3D;



import geogebra3D.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;





public class DrawPoint3D extends Drawable3DSolid{
	
	
	
	
	
		
	
	public DrawPoint3D(EuclidianView3D a_view3D, GeoPoint3D a_point3D) {     
		
		super(a_view3D, a_point3D);
		
	}
	
	
	
	
	

	public void drawGeometry(EuclidianRenderer3D renderer) {
		GeoPoint3D l_point = (GeoPoint3D) getGeoElement(); 
		if (l_point.hasPathOn())
			renderer.drawSphere(POINT3D_RADIUS*POINT_ON_PATH_DILATATION); //points on path are more visible 
		else
			renderer.drawSphere(POINT3D_RADIUS);//TODO use object property
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		
		renderer.drawSphere(POINT3D_RADIUS*PICKED_DILATATION);//TODO use object property
		
	}


	

	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		drawGeometry(renderer);
		
	}	
	

	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	

}
