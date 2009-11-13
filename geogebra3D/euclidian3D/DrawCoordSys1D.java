package geogebra3D.euclidian3D;

import geogebra.euclidian.Previewable;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class DrawCoordSys1D extends Drawable3DCurves implements Previewable {

	private double drawMin;
	private double drawMax;

	
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoCoordSys1D cs1D){
		
		super(a_view3D, cs1D);
	}	
	
	
	
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);
		
	}

	
	
	protected void setDrawMinMax(double drawMin, double drawMax){
		this.drawMin = drawMin;
		this.drawMax = drawMax;
	}
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.setThickness(getGeoElement().getLineThickness());
		renderer.drawSegment(drawMin,drawMax);
		//Application.debug("drawMin = "+drawMin+"\ndrawMax = "+drawMax);
	}
	
	public void drawGeometryPicked(Renderer renderer){
		renderer.setThickness(getGeoElement().getLineThickness());
		renderer.drawSegment(drawMin,drawMax);
	}
	
	public void drawGeometryHidden(Renderer renderer){
		
		drawGeometry(renderer);
	} 
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	protected void updateForItSelf(){
		
		viewChanged();
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
		
		//viewChanged();

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
			update();
		}else if (selectedPoints.size()==1){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = getView3D().getCursor3D();
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
			update();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
		setWaitForUpdate();
			
	}
	
	


}
