package geogebra3D.kernel3D;

import geogebra.kernel.Construction;

import geogebra.kernel.GeoPolygon;


public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon s) {
	    this(c, labels, plane, s, s.asBoundary()); 
	    }
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			 GeoPolygon p, GeoPlane3D plane, boolean asBoundary) {	
		this(c, labels, plane, p, asBoundary);
	}
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p, boolean asBoundary) {		
		super(c, labels, AlgoIntersectCS2D2D.getIntersectPlanePlane(plane, p), p, asBoundary);
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPlanePolygon";
	}
	
}

