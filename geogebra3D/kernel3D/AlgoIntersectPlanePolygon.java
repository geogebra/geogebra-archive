package geogebra3D.kernel3D;

import geogebra.kernel.Construction;

import geogebra.kernel.GeoPolygon;


public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			 GeoPolygon p, GeoPlane3D plane) {	
		this(c, labels, plane, p);
	}
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {		
		super(c, labels, AlgoIntersectCS2D2D.getIntersectPlanePlane(plane, p), p);
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPlanePolygon";
	}
	
}

