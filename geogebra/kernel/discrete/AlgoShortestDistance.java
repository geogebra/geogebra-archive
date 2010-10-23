package geogebra.kernel.discrete;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.MyPoint;
import geogebra.kernel.kernelND.GeoPointND;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections15.Transformer;

public class AlgoShortestDistance extends AlgoElement {
	
	GeoPointND start, end;
	GeoList inputList;
	GeoLocus locus;
    protected ArrayList<MyPoint> al;

	public AlgoShortestDistance(Construction cons, String label, GeoList inputList, GeoPointND start, GeoPointND end) {
        super(cons);
        this.inputList = inputList;
        this.start = start;
        this.end = end;
               
        locus = new GeoLocus(cons);

        setInputOutput();
        compute();
        locus.setLabel(label);
		
	}
	
    protected void setInputOutput(){
        input = new GeoElement[3];
        input[0] = inputList;
        input[1] = (GeoElement)start;
        input[2] = (GeoElement)end;

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
    	if (!inputList.isDefined() ||  size == 0) {
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
				
				if (p1 == start) startNode = node1;
				else if (p1 == end) endNode = node1;
				
				if (p2 == start) startNode = node2;
				else if (p2 == end) endNode = node2;
				
				//if (i == 0) startNode = node1;
				//else if (i == size - 1) endNode = node2;
				
				// add edge to graph
				  g.addEdge(new MyLink(seg.getLength(), 1, node1, node2),node1, node2, EdgeType.UNDIRECTED); 

				//p.getInhomCoords(inhom);
				//vl.add( representation.createPoint(inhom[0], inhom[1]) );			
			}
		}
        
        
        if (al == null) al = new ArrayList<MyPoint>();
        else al.clear();
        
        if (startNode == null || endNode == null) {
    		locus.setPoints(al);
    		locus.setDefined(false);
    		return;
        }
        

        /*
     // Graph<V, E> where V is the type of the vertices
     // and E is the type of the edges
     Graph<Integer, String> g = new SparseMultigraph<Integer, String>();
     // Add some vertices. From above we defined these to be type Integer.
     g.addVertex((Integer)1);
     g.addVertex((Integer)2);
     g.addVertex((Integer)3);
     // Add some edges. From above we defined these to be of type String
     // Note that the default is for undirected edges.
     g.addEdge("Edge-A", 1, 2); // Note that Java 1.5 auto-boxes primitives
     g.addEdge("Edge-B", 2, 3);
     // Let's see what we have. Note the nice output from the
     // SparseMultigraph<V,E> toString() method
     System.out.println("The graph g = " + g.toString());
*/
     
        /*
  // Create some MyNode objects to use as vertices
     MyNode n1 = new MyNode(1); MyNode n2 = new MyNode(2); MyNode n3 = new MyNode(3);
     MyNode n4 = new MyNode(4); MyNode n5 = new MyNode(5); // note n1-n5 declared elsewhere.
  // Add some directed edges along with the vertices to the graph
  g.addEdge(new MyLink(2.0, 48),n1, n2, EdgeType.DIRECTED); // This method
  g.addEdge(new MyLink(2.0, 48),n2, n3, EdgeType.DIRECTED);
  g.addEdge(new MyLink(3.0, 192), n3, n5, EdgeType.DIRECTED);
  g.addEdge(new MyLink(2.0, 48), n5, n4, EdgeType.DIRECTED); // or we can use
  g.addEdge(new MyLink(2.0, 48), n4, n2); // In a directed graph the
  g.addEdge(new MyLink(2.0, 48), n3, n1); // first node is the source
  g.addEdge(new MyLink(10.0, 48), n2, n5);// and the second the destination
*/

        
        Transformer<MyLink, Double> wtTransformer = new Transformer<MyLink,Double>() {
        	public Double transform(MyLink link) {
        	return link.weight;
        	}
        	};
        	DijkstraShortestPath<MyNode,MyLink> alg = new DijkstraShortestPath(g, wtTransformer);
        	
        		
        		
        //Unweighted Shortest Path
        //To find the shortest path assuming uniform link weights (e.g., 1 for each link) in the graph we created
        //we can use the following code from BasicDirectedGraph.java:
        //DijkstraShortestPath<MyNode,MyLink> alg = new DijkstraShortestPath(g);
        List<MyLink> list = alg.getPath(startNode, endNode);
        //System.out.println("The shortest unweighted path from" + n1 +
        //" to " + n4 + " is:");
        //System.out.println(list.toString());
        
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
        
        
        
        
        
        
        /*
        for ( VPoint point : trianglarrep.vertexpoints ) {
            VVertex vertex = (VVertex) point;
            
            // Check the vertex has edges
            if ( vertex.hasEdges()==false ) {
                continue;
            }
            

            // Paint each of those edges
            for ( VHalfEdge edge : vertex.getEdges() ) {
                // Simple addition to show MST
                if ( edge.shownonminimumspanningtree==false ) continue;
                
                VVertex vertex2 = edge.next.vertex;
                
                 }
            }*/

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
