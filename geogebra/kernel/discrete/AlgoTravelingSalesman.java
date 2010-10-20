package geogebra.kernel.discrete;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VPoint;
import geogebra.kernel.discrete.signalprocesser.voronoi.VoronoiAlgorithm;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.AbstractRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.RepresentationFactory;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.simpletriangulation.SimpleTriangulationRepresentation;
import geogebra.kernel.discrete.signalprocesser.voronoi.representation.simpletriangulation.VTriangle;
import geogebra.kernel.discrete.tsp.method.tsp.BranchBound;
import geogebra.kernel.discrete.tsp.method.tsp.Opt3;
import geogebra.kernel.discrete.tsp.model.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class AlgoTravelingSalesman extends AlgoHull{

	public AlgoTravelingSalesman(Construction cons, String label, GeoList inputList) {
		super(cons, label, inputList, null);
	}
	
    public String getClassName() {
        return "AlgoTravelingSalesman";
    }
    
    protected void compute() {
    	
    	size = inputList.size();
    	if (!inputList.isDefined() ||  size < 3) {
    		locus.setUndefined();
    		return;
    	} 
    	
        if (vl == null) vl = new ArrayList<VPoint>();
        else vl.clear();
   	
		double inhom[] = new double[2];
		
		Opt3 opt3 = new Opt3();
		final BranchBound construction = new BranchBound(500, opt3);

		Node[] nodes = new Node[size];

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoPoint()) {
				GeoPointInterface p = (GeoPointInterface)geo;
				p.getInhomCoords(inhom);
				nodes[i] = new Node(inhom[0], inhom[1]);
			}
		}
        
        int[] route = construction.method(nodes);
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
       for (int i = 0 ; i < size ; i++) {
        	Node n = nodes[route[i]];
            al.add(new MyPoint(n.getX(), n.getY(), i != 0));

        }
        
       // join up
   	Node n = nodes[route[0]];
      al.add(new MyPoint(n.getX(), n.getY(), true));

		locus.setPoints(al);
		locus.setDefined(true);

        
       
    }


}
