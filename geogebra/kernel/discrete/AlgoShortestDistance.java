package geogebra.kernel.discrete;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.MyPoint;
import geogebra.kernel.discrete.AlgoShortestDistance.MyLink;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;

public class AlgoShortestDistance extends AlgoElement {
	
	GeoPointND start, end;
	GeoList inputList;
	GeoLocus locus;
	GeoBoolean weighted;
    protected ArrayList<MyPoint> al;

	public AlgoShortestDistance(Construction cons, String label, GeoList inputList, GeoPointND start, GeoPointND end, GeoBoolean weighted) {
        super(cons);
        this.inputList = inputList;
        this.start = start;
        this.end = end;
        this.weighted = weighted;
               
        locus = new GeoLocus(cons);

        setInputOutput();
        compute();
        locus.setLabel(label);
		
	}
	
    protected void setInputOutput(){
        input = new GeoElement[4];
        input[0] = inputList;
        input[1] = (GeoElement)start;
        input[2] = (GeoElement)end;
        input[3] = (GeoElement)weighted;

        output = new GeoElement[1];
        output[0] = locus;
        setDependencies(); // done by AlgoElement
    }

    public GeoLocus getResult() {
        return locus;
    }

    public String getClassName() {
        return "AlgoShortestDistance";
    }
    
    protected final void compute() {
    	
    	int size = inputList.size();
    	if (!inputList.isDefined() || !weighted.isDefined() ||  size == 0) {
    		locus.setUndefined();
    		return;
    	} 
    	
        
        HashMap<GeoPointND, MyNode> nodes = new HashMap<GeoPointND, MyNode>();
        HashMap<MyNode, GeoPointND> nodes2 = new HashMap<MyNode, GeoPointND>();
        
        
        SparseMultigraph<MyNode, MyLink> g = new SparseMultigraph<MyNode, MyLink>();
        
        MyNode node1, node2, startNode = null, endNode = null;

        for (int i = 0 ; i < size ; i++) {
			GeoElement geo = inputList.get(i);
			if (geo.isDefined() && geo.isGeoSegment()) {
				GeoSegment seg = (GeoSegment)geo;
				GeoPointND p1 = seg.getStartPoint();
				GeoPointND p2 = seg.getEndPoint();
				node1 = nodes.get(p1);
				node2 = nodes.get(p2);
				if (node1 == null) {
					node1 = new MyNode(p1);
					nodes.put(p1, node1);
					nodes2.put(node1, p1);
				} 
				if (node2 == null) {
					node2 = new MyNode(p2);
					nodes.put(p2, node2);
					nodes2.put(node2, p2);
				} 
				
				// take note of start and end points
				if (p1 == start) startNode = node1;
				else if (p1 == end) endNode = node1;
				
				if (p2 == start) startNode = node2;
				else if (p2 == end) endNode = node2;
							
				// add edge to graph
				  g.addEdge(new MyLink(seg.getLength(), 1, node1, node2),node1, node2, EdgeType.UNDIRECTED); 

			}
		}
        
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        if (startNode == null || endNode == null) {
    		locus.setPoints(al);
    		locus.setDefined(false);
    		return;
        }
        
        DijkstraShortestPath<MyNode,MyLink> alg;

        if (weighted.getBoolean() == true) {
        	//weighted Shortest Path
        	// use length of segments to weight
	        Transformer<MyLink, Double> wtTransformer = new Transformer<MyLink,Double>() {
	        	public Double transform(MyLink link) {
	        	return link.weight;
	        	}
	        	};
        	alg = new DijkstraShortestPath<MyNode, MyLink>(g, wtTransformer);
        } else {
        	//Unweighted Shortest Path
        	alg = new DijkstraShortestPath<MyNode, MyLink>(g);
        }
         		
        List<MyLink> list = alg.getPath(startNode, endNode);
        
		double inhom[] = new double[2];
	   	
        for (int i = 0 ; i < list.size() ; i++) {
        	MyLink link = list.get(i);
        	GeoPointND p1 = nodes2.get(link.n1);
        	GeoPointND p2 = nodes2.get(link.n2);
        	
            p1.getInhomCoords(inhom);
            al.add(new MyPoint(inhom[0] , inhom[1], false));
            p2.getInhomCoords(inhom);
            al.add(new MyPoint(inhom[0] , inhom[1], true));

        }
        
		locus.setPoints(al);
		locus.setDefined(true);
       
    }
    
    protected int edgeCount = 0;

    class MyLink {
    	protected MyNode n1, n2;
    	double capacity; // should be private
    	double weight; // should be private for good practice
    	int id;
    	public MyLink(double weight, double capacity, MyNode n1, MyNode n2) {
    		this.id = edgeCount++; // This is defined in the outer class.
    		this.weight = weight;
    		this.capacity = capacity;
    		this.n1 = n1;
    		this.n2 = n2;
    	}
    	public String toString() { // Always good for debugging
    		return "Edge" + id;
    	}
    }
}
