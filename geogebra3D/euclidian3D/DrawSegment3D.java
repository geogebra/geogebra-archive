package geogebra3D.euclidian3D;




import geogebra.kernel.kernel3D.GeoSegment3D;

import java.util.ArrayList;



/**
 * Class for drawing segments
 * @author matthieu
 *
 */
public class DrawSegment3D extends DrawCoordSys1D {

	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_segment3D
	 */
	public DrawSegment3D(EuclidianView3D a_view3D, GeoSegment3D a_segment3D){
		
		super(a_view3D,a_segment3D);
		
		setDrawMinMax(0, 1);
	}


	
	
	////////////////////////////////
	// Previewable interface 
	
	
	/**
	 * Constructor for previewable
	 * @param a_view3D
	 * @param selectedPoints
	 */
	@SuppressWarnings("unchecked")
	public DrawSegment3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoSegment3D(a_view3D.getKernel().getConstruction()));
		
		setDrawMinMax(0, 1);
		
	}	


}
