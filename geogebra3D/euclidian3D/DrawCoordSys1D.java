package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.Brush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class DrawCoordSys1D extends Drawable3DCurves implements Previewable {

	private double drawMin;
	private double drawMax;
	
	/** gl index of the segment */
	private int segmentIndex = -1;


	
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
	
	
	protected double getDrawMin(){
		return drawMin;
	}
	
	protected double getDrawMax(){
		return drawMax;
	}
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.setThickness(getGeoElement().getLineThickness());
		//renderer.drawSegment(drawMin,drawMax);
		
		//renderer.getTextures().setDashTexture(Textures.DASH_SIMPLE,1f);
		renderer.getGeometryManager().draw(segmentIndex);
	}
	
	
	
	
	
	protected void updateForItSelf(){


		Renderer renderer = getView3D().getRenderer();
		GeoCoordSys1D cs = (GeoCoordSys1D) getGeoElement();
		

		renderer.getGeometryManager().remove(segmentIndex);
		
		GgbVector p1 = cs.getPoint(getDrawMin()).getInhomCoords();
		GgbVector p2 = cs.getPoint(getDrawMax()).getInhomCoords();
		
		float thickness = (float) (Brush.LINE3D_THICKNESS*getGeoElement().getLineThickness()/getView3D().getScale());
		Brush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		brush.setAffineTexture(
				(float) ((0.-getDrawMin())/(getDrawMax()-getDrawMin())),  0.25f);
		brush.segment(p1, p2);
		segmentIndex = brush.end();
		/*
		segmentIndex = renderer.getGeometryManager().newSegment(
				getGeoElement().getObjectColor(),
				cs.getPoint(getDrawMin()).getInhomCoords(),
				cs.getPoint(getDrawMax()).getInhomCoords(),
				(float) getGeoElement().getLineThickness(),
				(float) getView3D().getScale(),
				(float) ((0.5-getDrawMin())/(getDrawMax()-getDrawMin())));
		*/
		
		
		
	}
	
	protected void updateForView(){
		updateForItSelf();
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
		
		//viewChanged();

		updatePreview();
		
	}	

	




	public void updateMousePos(int x, int y) {
		
	}


	public void updatePreview() {
		
		
		if (selectedPoints.size()==2){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = (GeoPoint3D) selectedPoints.get(1);
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else if (selectedPoints.size()==1){
			GeoPoint3D firstPoint = (GeoPoint3D) selectedPoints.get(0);
			GeoPoint3D secondPoint = getView3D().getCursor3D();
			((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoords(), secondPoint.getCoords());
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
		//Application.debug("selectedPoints : "+selectedPoints+" -- isEuclidianVisible : "+getGeoElement().isEuclidianVisible());
	
			
	}
	
	


}
