package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.euclidian.Previewable;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;

import java.util.ArrayList;

public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	
	
	protected void updateForItSelf(){
		

		updateForItSelf(true);

	}
	
	protected void updateForItSelf(boolean updateDrawMinMax){
		

		if (updateDrawMinMax)
			updateDrawMinMax();
		
		super.updateForItSelf();

	}

	protected void updateDrawMinMax(){
		
		GgbCoordSys cs = ((GeoCoordSys1D) getGeoElement()).getCoordSys();
		
		GgbVector o = getView3D().getToScreenMatrix().mul(cs.getOrigin());
		GgbVector v = getView3D().getToScreenMatrix().mul(cs.getVx());
						
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v);
		
		//Application.debug("minmax="+minmax[0]+", "+minmax[1]);
		
		setDrawMinMax(minmax[0], minmax[1]);
	}
	
	
	
	
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	//private ArrayList selectedPoints;

	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		
	}	


	
	

	

}
