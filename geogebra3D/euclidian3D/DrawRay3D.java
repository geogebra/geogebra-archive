package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoRay3D;

import java.util.ArrayList;

/**
 * Class for drawing a 3D ray.
 * @author matthieu
 *
 */
public class DrawRay3D extends DrawCoordSys1D {

	
	/**
	 * common constructor
	 * @param a_view
	 * @param a_ray
	 */
	public DrawRay3D(EuclidianView3D a_view, GeoRay3D a_ray)
	{
 		super(a_view, a_ray);
	}
	
	
	
	protected void updateForItSelf(){
		

		updateForItSelf(true);

	}
	
	/**
	 * update when the element is modified
	 * @param updateDrawMinMax update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}
	
	
	
	/**
	 * update min and max values
	 */
	protected void updateDrawMinMax(){
		
		GgbCoordSys cs = ((GeoCoordSys1D) getGeoElement()).getCoordSys();
		
		GgbVector o = getView3D().getToScreenMatrix().mul(cs.getOrigin());
		GgbVector v = getView3D().getToScreenMatrix().mul(cs.getVx());
		
				
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {0,Double.POSITIVE_INFINITY},
				o, v);
		
		setDrawMinMax(minmax[0], minmax[1]);
		
		
	}
	
	
	/*
	public void drawGeometry(EuclidianRenderer3D renderer) {
		//renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		super.drawGeometry(renderer);
		renderer.drawRay();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		//renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		super.drawGeometryPicked(renderer);
		renderer.drawRay();
	}
	*/


	/*
	public void drawGeometryHidden(EuclidianRenderer3D renderer){
		
		GeoRay3D l_ray3D = (GeoRay3D) getGeoElement();
		double dashLength = 0.12f/((float) l_ray3D.getUnit()); //TODO use object property
		//renderer.drawRayDashed(LINE3D_THICKNESS*getGeoElement().getLineThickness(),dashLength); 
		drawGeometry(renderer);
		
	}
	*/
	


	

	

	/*
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}

	*/
	
	

	////////////////////////////////
	// Previewable interface 
	
	
	/**
	 * Constructor for previable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawRay3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoRay3D(a_view3D.getKernel().getConstruction()));
		

		
	}	

}
