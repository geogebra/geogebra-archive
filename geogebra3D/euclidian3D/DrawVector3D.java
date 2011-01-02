package geogebra3D.euclidian3D;


import java.util.ArrayList;

import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoSegment3D;
import geogebra3D.kernel3D.GeoVector3D;

/**
 * Class for drawing vectors
 * @author matthieu
 *
 */
public class DrawVector3D extends Drawable3DCurves
implements Previewable {

	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_vector3D
	 */
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector3D)
	{
		
		super(a_view3D, a_vector3D);
	}
	
	/////////////////////////////////////////
	// DRAWING GEOMETRIES
	
	
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}


	
	protected boolean updateForItSelf(){

		setColors();
		
		GeoVector3D geo = ((GeoVector3D) getGeoElement());
		
		geo.updateStartPointPosition();

		Renderer renderer = getView3D().getRenderer();

		
		GgbVector p1;
		if (geo.getStartPoint()==null){
			p1 = new GgbVector(4);
			p1.setW(1);
		}else
			p1 = geo.getStartPoint().getCoordsInD(3);
		GgbVector p2 = (GgbVector) p1.add(geo.getCoords());
		
		PlotterBrush brush = renderer.getGeometryManager().getBrush();

		brush.setArrowType(PlotterBrush.ARROW_TYPE_SIMPLE);
		brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());
		
		brush.start(8);
		brush.setAffineTexture(0.5f, 0.125f);
		brush.segment(p1,p2);
		brush.setArrowType(PlotterBrush.ARROW_TYPE_NONE);
		setGeometryIndex(brush.end());
		
		return true;
	}
	
	protected void updateForView(){
		updateForItSelf();
	}
	
	
	
	
	
	public int getPickOrder() {		
		return DRAW_PICK_ORDER_1D;
	}

	
	
	

	
	////////////////////////////////
	// Previewable interface 
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ArrayList selectedPoints;

	/**
	 * constructor for previewable
	 * @param view3D
	 * @param selectedPoints
	 * @param cs1D
	 */
	@SuppressWarnings("unchecked")
	public DrawVector3D(EuclidianView3D view3D, ArrayList selectedPoints){
		
		super(view3D);
		
		GeoVector3D v = new GeoVector3D(view3D.getKernel().getConstruction());
		setGeoElement(v);
		v.setIsPickable(false);
		setGeoElement(v);
		
		this.selectedPoints = selectedPoints;
		
		updatePreview();
		
	}	

	




	public void updateMousePos(double xRW, double yRW) {	
		
	}


	public void updatePreview() {
		
		GeoPointND firstPoint = null;
		GeoPointND secondPoint = null;
		if (selectedPoints.size()>=1){
			firstPoint = (GeoPointND) selectedPoints.get(0);
			if (selectedPoints.size()==2)
				secondPoint = (GeoPointND) selectedPoints.get(1);
			else
				secondPoint = getView3D().getCursor3D();
		}
			
		
		if (selectedPoints.size()>=1){
			((GeoVector3D) getGeoElement()).setCoords(
					secondPoint.getCoordsInD(3).sub(firstPoint.getCoordsInD(3)).get());
			try {
				((GeoVector3D) getGeoElement()).setStartPoint(firstPoint);
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
			getGeoElement().setEuclidianVisible(true);
			setWaitForUpdate();
		}else{
			getGeoElement().setEuclidianVisible(false);
		}
		
			
	}
		
		
		

}
