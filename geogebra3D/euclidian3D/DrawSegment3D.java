package geogebra3D.euclidian3D;




import geogebra3D.kernel3D.GeoSegment3D;

import java.util.ArrayList;



public class DrawSegment3D extends DrawCoordSys1D {

	
	double dashLength;
	
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegment3D a_segment3D){
		
		super(a_view3D,a_segment3D);
		
		setDrawMinMax(0, 1);
	}


	
	protected void updateForView(){
		
	}
	
	////////////////////////////////
	// Previewable interface 
	
	
	public DrawSegment3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoSegment3D(a_view3D.getKernel().getConstruction()));
		
		setDrawMinMax(0, 1);
		
	}	


}
