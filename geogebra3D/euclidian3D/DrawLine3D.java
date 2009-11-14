package geogebra3D.euclidian3D;

import geogebra.euclidian.Previewable;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.kernel3D.GeoCoordSys1D;
import geogebra3D.kernel3D.GeoLine3D;

import java.util.ArrayList;

public class DrawLine3D extends DrawCoordSys1D implements Previewable {

	
	
	public DrawLine3D(EuclidianView3D a_view3D, GeoLine3D a_line3D){
		
		super(a_view3D, a_line3D);
	}	
	
	
	
	
	protected void updateForView(){
		
		Ggb3DVector o = getView3D().getToScreenMatrix().mul(((GeoCoordSys1D) getGeoElement()).getOrigin());
		Ggb3DVector v = getView3D().getToScreenMatrix().mul(((GeoCoordSys1D) getGeoElement()).getVx());
		
		//Application.debug("matrix =\n"+((GeoCoordSys1D) getGeoElement()).getMatrix());
		//Application.debug("screen matrix =\n"+getView3D().getToScreenMatrix());
				
		double[] minmax = getView3D().getRenderer().getIntervalInFrustum(
				new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
				o, v);
		

		
		setDrawMinMax(minmax[0], minmax[1]);
		
		//Application.debug("drawMin = "+minmax[0]+"\ndrawMax = "+minmax[1]);

	}
	
	
	
	

	/*
	public void drawGeometry(EuclidianRenderer3D renderer) {
		
		//renderer.setThickness(LINE3D_THICKNESS*getGeoElement().getLineThickness());
		super.drawGeometry(renderer);
		renderer.drawLine();
	}
	
	public void drawGeometryPicked(EuclidianRenderer3D renderer){
		
		//renderer.setThickness(LINE3D_THICKNESS*PICKED_DILATATION*getGeoElement().getLineThickness());
		super.drawGeometryPicked(renderer);
		renderer.drawLine(); 
	}
	
*/

	


	/*
	public int getPickOrder(){
		return DRAW_PICK_ORDER_1D;
	}

	*/
	
	
	
	
	////////////////////////////////
	// Previewable interface 
	
	
	//private ArrayList selectedPoints;

	public DrawLine3D(EuclidianView3D a_view3D, ArrayList selectedPoints){
		
		super(a_view3D,selectedPoints, new GeoLine3D(a_view3D.getKernel().getConstruction()));
		
		
	}	


	
	

	

}
