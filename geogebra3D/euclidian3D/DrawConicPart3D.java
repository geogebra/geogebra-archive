package geogebra3D.euclidian3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoConicPart;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra3D.euclidian3D.opengl.PlotterBrush;

public class DrawConicPart3D extends DrawConic3D {

	public DrawConicPart3D(EuclidianView3D view3d, GeoConicPart conic) {
		super(view3d, conic);
	}
	
	
	
	
	
	protected double getStart(){
		//return 0;
		return ((GeoConicPart) getGeoElement()).getParameterStart();
	}
	
	protected double getExtent(){
		return ((GeoConicPart) getGeoElement()).getParameterExtent();
	}
	

	protected void updateCircle(PlotterBrush brush){
		
		GeoConicPart conic = (GeoConicPart) getGeoElement();
		Coords m = conic.getMidpoint3D();
		Coords ev0 = conic.getEigenvec3D(0); 
		Coords ev1 = conic.getEigenvec3D(1);
		double radius = conic.getHalfAxis(0);
		double start = conic.getParameterStart();
		double extent = conic.getParameterExtent();
		brush.arc(m, ev0, ev1, radius,start,extent);
		
		updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1, radius, radius, start, start+extent);
	}
	
	protected void updateEllipse(PlotterBrush brush){
		
		GeoConicPart conic = (GeoConicPart) getGeoElement();
		Coords m = conic.getMidpoint3D();
		Coords ev0 = conic.getEigenvec3D(0); 
		Coords ev1 = conic.getEigenvec3D(1);
		double r0 = conic.getHalfAxis(0);
		double r1 = conic.getHalfAxis(1);
		double start = conic.getParameterStart();
		double extent = conic.getParameterExtent();
		brush.arcEllipse(m, ev0, ev1, r0, r1,start,extent);
		
		updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1, r0, r1, start, start+extent);
	}

	private void updateSectorSegments(PlotterBrush brush, int type, Coords m, Coords ev0, Coords ev1, double r0, double r1, double start, double end){
			
		//if sector draws segments
		if (type==GeoConicPart.CONIC_PART_SECTOR){
			brush.setAffineTexture(0.5f,  0.25f);
			brush.segment(m, m.add(ev0.mul(r0*Math.cos(start))).add(ev1.mul(r1*Math.sin(start))));
			brush.segment(m, m.add(ev0.mul(r0*Math.cos(end))).add(ev1.mul(r1*Math.sin(end))));
		}
	}

}
