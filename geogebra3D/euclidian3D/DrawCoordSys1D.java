package geogebra3D.euclidian3D;

import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.Coords;
import geogebra.euclidian.Hits;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.AlgoIntersectCS2D2D;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPoint3D;

import java.awt.Color;
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
	public DrawCoordSys1D(EuclidianView3D a_view3D, GeoElement cs1D){
		
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
		
		setColors();
		
		GeoLineND cs = (GeoLineND) getGeoElement();
		double[] minmax = getDrawMinMax(); 
		updateForItSelf(cs.getPointInD(3,minmax[0]).getInhomCoords(),cs.getPointInD(3,minmax[1]).getInhomCoords());
	
		return true;
	}

	/**
	 * update the drawable as a segment from p1 to p2
	 * @param p1
	 * @param p2
	 */
	protected void updateForItSelf(Coords p1, Coords p2){

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
				(float) ((0.5-minmax[0])/(minmax[1]-minmax[0])),  0.25f);
		brush.segment(p1, p2);
		setGeometryIndex(brush.end());
		
		
		
	}
	
	/**
	 * @return the line thickness
	 */
	protected int getLineThickness(){
		return getGeoElement().getLineThickness();
	}
	
	
	
	
	
	
	
	
	

	
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}	

	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	private final int PREVIEW_NOT = 0;
	private final int PREVIEW_FROM_POINTS = 1;
	private final int PREVIEW_FROM_PLANES = 2;
	private int previewFromType;
	
	@SuppressWarnings("unchecked")
	private ArrayList selectedObjs;
	
	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedPoints, GeoCoordSys1D cs1D){
		this(a_view3D,selectedPoints,1,cs1D);
	}	

	public DrawCoordSys1D(EuclidianView3D a_view3D, ArrayList selectedObjs, int previewFromType, GeoCoordSys1D cs1D){
		
		super(a_view3D);

		cs1D.setIsPickable(false);
		setGeoElement(cs1D);
		
		this.previewFromType = previewFromType;
		this.selectedObjs = selectedObjs;
		
		if (previewFromType==2) //TODO: put to a better place
			cs1D.setObjColor(Color.GRAY);
		
		updatePreview();
		
	}	

	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {
		
		switch(previewFromType) {
		case 1: //2 points or 1 point
			if (selectedObjs.size()==2){
				GeoPointND firstPoint = (GeoPointND) selectedObjs.get(0);
				GeoPointND secondPoint = (GeoPointND) selectedObjs.get(1);
				((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoordsInD(3), secondPoint.getCoordsInD(3));
				getGeoElement().setEuclidianVisible(true);
				setWaitForUpdate();
			}else if (selectedObjs.size()==1){
				GeoPointND firstPoint = (GeoPointND) selectedObjs.get(0);
				GeoPointND secondPoint = getView3D().getCursor3D();
				((GeoCoordSys1D) getGeoElement()).setCoordFromPoints(firstPoint.getCoordsInD(3), secondPoint.getCoordsInD(3));
				getGeoElement().setEuclidianVisible(true);
				setWaitForUpdate();
			}
			break;
		case 2: //two planes
			//Application.debug(selectedObjs);
			
			if (selectedObjs.size()==0){
				Hits hitPlanes = new Hits();
				getView3D().getEuclidianController().getHighlightedgeos()
					.getHits(GeoCoordSys2D.class, hitPlanes);
				hitPlanes.removeAllPolygons();
				if (hitPlanes.size()==2) {
					GeoCoordSys2D firstPlane = (GeoCoordSys2D) hitPlanes.get(0);
					GeoCoordSys2D secondPlane = (GeoCoordSys2D) hitPlanes.get(1);
					Coords[] intersection = CoordMatrixUtil.intersectPlanes(
				  			firstPlane.getCoordSys().getMatrixOrthonormal(),
				  			secondPlane.getCoordSys().getMatrixOrthonormal());
				  	((GeoCoordSys1D) getGeoElement()).setCoord(intersection[0], intersection[1]);
					getGeoElement().setEuclidianVisible(true);
					setWaitForUpdate();
				} else {
					getGeoElement().setEuclidianVisible(false);
					setWaitForUpdate();
				}
			} else if (selectedObjs.size()==1 && selectedObjs.get(0) instanceof GeoCoordSys2D ) {
				Hits hitPlanes = new Hits();
				getView3D().getHits().getHits(GeoCoordSys2D.class, hitPlanes);
				hitPlanes.remove(selectedObjs.get(0));
				
				if (hitPlanes.size()==1) {
					GeoCoordSys2D firstPlane = (GeoCoordSys2D) selectedObjs.get(0);
					GeoCoordSys2D secondPlane = (GeoCoordSys2D) hitPlanes.get(0);
					Coords[] intersection = CoordMatrixUtil.intersectPlanes(
							firstPlane.getCoordSys().getMatrixOrthonormal(),
							secondPlane.getCoordSys().getMatrixOrthonormal());
					((GeoCoordSys1D) getGeoElement()).setCoord(intersection[0], intersection[1]);
					getGeoElement().setEuclidianVisible(true);
					setWaitForUpdate();
				}else {
					getGeoElement().setEuclidianVisible(false);
					setWaitForUpdate();
				}
			}
			break;
		default:
			getGeoElement().setEuclidianVisible(false);
		}
		
		//Application.debug("selectedPoints : "+selectedPoints+" -- isEuclidianVisible : "+getGeoElement().isEuclidianVisible());
	
			
	}
	
	


}
