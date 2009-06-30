package geogebra3D.euclidian3D;

import geogebra.euclidian.Previewable;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.opengl.EuclidianRenderer3D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class DrawLine3D extends Drawable3DSolid implements Previewable {

	
	
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
	
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		drawGeometry(renderer);
	};
	
	


	


	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}

	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	GeoPoint3D[] points;
	int numPoints = 0;

	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		getGeoElement().setEuclidianVisible(false);
		
		points = new GeoPoint3D[2];
		for (int i=0;i<selectedPoints.size();i++)
			points[i]=(GeoPoint3D) selectedPoints.get(i);
		
		numPoints = selectedPoints.size();

		updatePreview();
		
	}	

	public void disposePreview() {
		// TODO Auto-generated method stub
		
	}


	public void drawPreview(Graphics2D g2) {
		// TODO Auto-generated method stub
		
	}


	public void updateMousePos(int x, int y) {
		switch (numPoints){
		case 1:
			points[1]=getView3D().getKernel().Point3D(null, x, y, 0);
			numPoints = 2;
			updatePreview();
			break;
		case 2:
			points[1].setCoords(x, y, 0, 1);
			updatePreview();
			break;
		}
			
		
	}


	public void updatePreview() {
		if (numPoints==2){
			Application.debug("hop");
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(points[0].getCoords(), points[1].getCoords());
			getGeoElement().setEuclidianVisible(true);
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
	}
	
	

	
	

	

}
