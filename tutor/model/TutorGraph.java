package tutor.model;

import geogebra.kernel.GeoPoint;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * dealing with adjacencies, based on
 * 
 * Weisstein, Eric W. "Graph." From MathWorld--A Wolfram Web Resource. 
 * {@link http://mathworld.wolfram.com/Graph.html} 
 * 
 * and
 * 
 * {@link http://jgrapht.sourceforge.net/}
 * 
 * @author AlbertV
 *
 */
public class TutorGraph {
	
	/** TutorVertex */
	private Set tutorVertices;
	/** TutorEdge */
	private Set tutorEdges;
	
	public TutorGraph() {
		tutorVertices = new TreeSet();
		tutorEdges = new TreeSet();
	}

	/** TutorVerex */
	public Set getTutorVertices() {
		return tutorVertices;
	}
	public void setTutorVertices(Set tutorVertices) {
		this.tutorVertices = tutorVertices;
	}
	
	public void addTutorVertex(TutorVertex tutorVertex) {
		if(tutorVertices == null) {
			tutorVertices = new TreeSet();
		}
		tutorVertices.add(tutorVertex);
	}

	public void addTutorVertex(GeoPoint geoPoint) {
		if(tutorVertices == null) {
			tutorVertices = new TreeSet();
		}
		TutorVertex tutorVertex = new TutorVertex(geoPoint.getLabel());
		// set no te duplicats, pq peta doncs si fas add del mateix objecte? 
		//ANSWER--> pq no havia implementat el compare !!
		tutorVertices.add(tutorVertex);
	}
	
	/**
	 * find a vertex in this graph with the given label and return it,
	 * if not found, then create one
	 * @param label
	 * @return
	 */
	public TutorVertex getOrCreateVertexByLabel(String label) {
		if(tutorVertices == null) {
			tutorVertices = new TreeSet();
		}
		
		// new candidate vertex
		TutorVertex newVertex = new TutorVertex(label);
		
		Iterator it = tutorVertices.iterator();
		while(it.hasNext()) {
			TutorVertex vertex = (TutorVertex) it.next();
			//if (vertex.getLabel().equals(label)) {
			// 28/5/8 --> now using Comparable!!
			if (vertex.compareTo(newVertex) == 0) {
				// existing vertex with the given label
				return vertex;
			}
		}
		return newVertex;
	}
	
	/** TutorEdge */
	public Set getTutorEdges() {
		return tutorEdges;
	}
	public void setTutorEdges(Set tutorEdges) {
		this.tutorEdges = tutorEdges;
	}
	public void addTutorEdge(TutorEdge tutorEdge) {
		if(tutorEdges == null) {
			//TODO: fer TutorEdge implements Comparable
			tutorEdges = new TreeSet();
		}
		tutorEdges.add(tutorEdge);
	}
}
