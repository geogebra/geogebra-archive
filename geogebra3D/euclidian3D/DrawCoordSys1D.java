package geogebra3D.euclidian3D;

import java.awt.Graphics2D;
import java.util.ArrayList;

import geogebra.euclidian.Previewable;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

public abstract class DrawCoordSys1D extends Drawable3DSolid implements Previewable {

	protected double drawMin = 0;
	protected double drawMax = 1;

	
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoCoordSys1D cs1D){
		
		super(a_view3D, cs1D);
	}	
	
	
	
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);
		
	}

	
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		renderer.drawSegment(drawMin,drawMax);
	}
	
	public void drawGeometryPicked(Renderer renderer){
		renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		renderer.drawSegment(drawMin,drawMax);
	}
	
	public void drawGeometryHidden(Renderer renderer){
		
		drawGeometry(renderer);
	} 
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	private ArrayList selectedPoints;

	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoCoordSys1D cs1D){
		
		super(a_view3D);
		
		Kernel3D kernel = (Kernel3D) getView3D().getKernel();

		
		cs1D.setIsPickable(false);
		setGeoElement(cs1D);
		
		this.selectedPoints = selectedPoints;
		

		updatePreview();
		
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
		
		//Application.debug("selectedPoints : "+selectedPoints);
		
		if (selectedPoints.size()==2){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = (GeoPoint3D) selectedPoints.get(1);
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
		}else if (selectedPoints.size()==1){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = getView3D().getCursor3D();
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
			
	}
	
	


}
