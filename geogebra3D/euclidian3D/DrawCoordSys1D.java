package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.kernel.kernel3D.GeoCoordSys1D;
import geogebra.kernel.kernel3D.GeoPoint3D;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;

import java.util.ArrayList;

/**
 * Class for drawing 1D coord sys (lines, segments, ...)
 * @author matthieu
 *
 */
public abstract class DrawCoordSys1D extends Drawable3DCurves implements Previewable {

	private double[] drawMinMax = new double[2];
	


	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param cs1D
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoCoordSys1D cs1D){
		
		super(a_view3D, cs1D);
	}	
	
	
	
	/**
	 * common constructor for previewable
	 * @param a_view3d
	 */
	public DrawCoordSys1D(EuclidianView3D a_view3d) {
		super(a_view3d);
		
	}

	
	
	/**
	 * sets the values of drawable extremities
	 * @param drawMin
	 * @param drawMax
	 */
	public void setDrawMinMax(double drawMin, double drawMax){
		this.drawMinMax[0] = drawMin;
		this.drawMinMax[1] = drawMax;
	}
	
	
	/**
	 * @return the min-max extremity
	 */
	public double[] getDrawMinMax(){
		return drawMinMax;
	}
	
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}
	
	
	
	
	
	protected boolean updateForItSelf(){
		
		GeoCoordSys1D cs = (GeoCoordSys1D) getGeoElement();
		double[] minmax = getDrawMinMax(); 
		updateForItSelf(cs.getPoint(minmax[0]).getInhomCoords(),cs.getPoint(minmax[1]).getInhomCoords());
	
		return true;
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * @param p1
	 * @param p2
	 */
	protected void updateForItSelf(GgbVector p1, GgbVector p2){

		//TODO prevent too large values
		
		double[] minmax = getDrawMinMax(); 
		
		if (Math.abs(minmax[0])>1E10)
			return;
		
		if (Math.abs(minmax[1])>1E10)
			return;
		
		if (minmax[0]>minmax[1])
			return;
		
		
		Renderer renderer = getView3D().getRenderer();
		


		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();
		
		brush.start(8);
		brush.setThickness(getLineThickness(),(float) getView3D().getScale());
		//brush.setColor(getGeoElement().getObjectColor());
		brush.setAffineTexture(
				(float) ((0.-minmax[0])/(minmax[1]-minmax[0])),  0.25f);
		brush.segment(p1, p2);
		setGeometryIndex(brush.end());
		
		
		
	}
	
	/**
	 * @return the line thickness
	 */
	protected int getLineThickness(){
		return getGeoElement().getLineThickness();
	}
	
	protected void updateForView(){
		updateForItSelf();
	}
	
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings("unchecked")
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoCoordSys1D cs1D){
		
		super(a_view3D);
		
		
		cs1D.setIsPickable(false);
		setGeoElement(cs1D);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
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
