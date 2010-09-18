package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.MyPoint;

import java.util.ArrayList;

import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.VoronoiAlgorithm;
import signalprocesser.voronoi.representation.AbstractRepresentation;
import signalprocesser.voronoi.representation.RepresentationFactory;
import signalprocesser.voronoi.representation.simpletriangulation.SimpleTriangulationRepresentation;
import signalprocesser.voronoi.representation.simpletriangulation.VTriangle;
import signalprocesser.voronoi.representation.triangulation.TriangulationRepresentation;
import signalprocesser.voronoi.representation.triangulation.VHalfEdge;
import signalprocesser.voronoi.representation.triangulation.VVertex;

public class AlgoDelauneyTriangulation extends AlgoHull{

	public AlgoDelauneyTriangulation(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoDelauneyTriangulation";
    }
    
    protected final void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
   	
        
        AbstractRepresentation representation;
        representation = RepresentationFactory.createSimpleTriangulationRepresentation();

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointInterface p = (GeoPointInterface)geo;
				p.getInhomCoords(inhom);
				vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}

        representation = RepresentationFactory.createSimpleTriangulationRepresentation();
        
        SimpleTriangulationRepresentation trianglarrep = (SimpleTriangulationRepresentation) representation;
        
        TestRepresentationWrapper representationwrapper = new TestRepresentationWrapper();
        representationwrapper.innerrepresentation = representation;
        
        VoronoiAlgorithm.generateVoronoi(representationwrapper, vl);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        for ( VTriangle triangle : trianglarrep.triangles ) {
            al.add(new MyPoint(triangle.p1.x , triangle.p1.y, false));
            al.add(new MyPoint(triangle.p2.x , triangle.p2.y, true));
            al.add(new MyPoint(triangle.p3.x , triangle.p3.y, true));
            al.add(new MyPoint(triangle.p1.x , triangle.p1.y, true));

        }


		locus.setPoints(al);
		locus.setDefined(true);
       
    }


}
