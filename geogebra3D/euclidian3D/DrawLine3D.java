package geogebra3D.euclidian3D;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.Previewable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;

import java.util.ArrayList;

/**
 * Class for drawing lines
 * @author matthieu
 *
 */
public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	/**
	 * common constructor
	 * @param a_view3D
	 * @param line
	 */
	public DrawLine3D(EuclidianView3D a_view3D, GeoLineND line){
		
		super(a_view3D, (GeoElement) line);
	}	
	
	
	
	
	protected boolean updateForItSelf(){
		

		updateForItSelf(true);
		return true;

	}
	
	/**
	 * update the drawable when the element changes
	 * @param updateDrawMinMax update min and max values
	 */
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}

	/**
	 *  update min and max values
	 * @param extendedDepth says if the depth is to be extended
	 */
	protected void updateDrawMinMax(boolean extendedDepth){
		
		GeoLineND line = (GeoLineND) getGeoElement();
		
		Coords o = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 0));
		Coords v = getView3D().getToScreenMatrix().mul(line.getPointInD(3, 1)).sub(o);
						
		double[] minmax = 
			getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v, extendedDepth);
		
		//Application.debug("minmax="+minmax[0]+", "+minmax[1]);
		
		setDrawMinMax(minmax[0], minmax[1]);
	}
	
	
	/**
	 *  update min and max values
	 */
	public void updateDrawMinMax(){
		updateDrawMinMax(true);
	}
	

	protected void updateForView(){
		if (getView3D().viewChanged())
			updateForItSelf();
	}
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	

	/**
	 * constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		
	}	


	
	

	

}
