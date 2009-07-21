package geogebra3D.euclidian3D;



import geogebra.euclidian.Previewable;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.ConstructionDefaults3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;





public class DrawPoint3D extends Drawable3DSolid implements Previewable{
	
	
	
	
	
		
	
	public DrawPoint3D(EuclidianView3D a_view3D, GeoPoint3D a_point3D) {     
		
		super(a_view3D, a_point3D);
		
	}
	
	
	
	
	

	public void drawGeometry(Renderer renderer) {

		GeoPoint3D point = (GeoPoint3D) getGeoElement(); 
		
		if (point.hasCoordDecoration()){
			renderer.setThickness(1);
			//TODO use gui
			renderer.drawCoordSegments(ConstructionDefaults3D.colXAXIS,ConstructionDefaults3D.colYAXIS,ConstructionDefaults3D.colZAXIS); 
		}
		

		if (point.hasPath())
			renderer.drawPoint(point.getPointSize()); //points on path are more visible 
		else
			renderer.drawPoint(point.getPointSize());
		

	}
	
	public void drawGeometryPicked(Renderer renderer){
		GeoPoint3D l_point = (GeoPoint3D) getGeoElement();
		renderer.drawPoint(l_point.getPointSize());
		
	}


	

	
	public void drawGeometryHidden(Renderer renderer){

		drawGeometry(renderer);
	}	
	

	
	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_0D;
	}	
	
	
	
	////////////////////////////////
	// Previewable interface 
	

	/**
	 * @param a_view3D
	 */
	public DrawPoint3D(EuclidianView3D a_view3D){
		
		super(a_view3D);
		
		setGeoElement(a_view3D.getCursor3D());
		
	}	

	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}


	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}


	public void updateMousePos(int x, int y) {

			
		
	}


	public void updatePreview() {

		
	}
	
	

	
	
	

}
