package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;
import geogebra3D.kernel3D.GeoLine3D;

import java.awt.Color;

public class DrawLine3D extends Drawable3DSolid {

	
	double dashLength;
	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	
	

	
	public void drawGeometry(EuclidianRenderer3D renderer) {
		
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		renderer.drawLine();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		
		renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		renderer.drawLine(); 
	}
	
	
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
	
		GeoLine3D l_line3D = (GeoLine3D) getGeoElement();
		dashLength = 0.12f/((float) l_line3D.getUnit()); //TODO use object property
		//renderer.drawLineDashed(LINE3D_THICKNESS*getGeoElement().getLineThickness(),dashLength); 
		drawGeometry(renderer);
	};
	
	


	


	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}
	
	

	
	

	

}
