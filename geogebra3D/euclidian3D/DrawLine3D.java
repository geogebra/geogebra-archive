package geogebra3D.euclidian3D;

import geogebra.euclidian.Previewable;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.GeoLine3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class DrawLine3D extends Drawable3DSolid implements Previewable {

	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D);
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
	
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		drawGeometry(renderer);
	};
	
	


	


	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}

	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	

	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}


	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}


	public void updateMousePos(int x, int y) {
		// TODO Auto-generated method stub
		
	}


	public void updatePreview() {
		// TODO Auto-generated method stub
		
	}
	
	

	
	

	

}
