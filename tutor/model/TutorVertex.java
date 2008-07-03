package tutor.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * a TutorVertex is the representation of a GeoPoint from a TutorGraph point of view 
 * @author AlbertV
 *
 */
public class TutorVertex implements Comparable {
	
	private String label;
	private int numAdjacentVertices;
	private int numAdjacentEdges;
	private Set adjacentVertices;
	
	public TutorVertex() {
		adjacentVertices = new TreeSet();
	}
	public TutorVertex(String label) {
		this();
		setLabel(label);
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getNumAdjacentVertices() {
		return numAdjacentVertices;
//		return adjacentVertices.size();
	}
	public void setNumAdjacentVertices(int numAdjacentVertices) {
		this.numAdjacentVertices = numAdjacentVertices;
	}
	public void addNumAdjacentVertices(int numAdjacentVertices) {
		this.numAdjacentVertices = this.numAdjacentVertices + numAdjacentVertices;
	}
	public int getNumAdjacentEdges() {
		return numAdjacentEdges;
	}
	public void setNumAdjacentEdges(int numAdjacentEdges) {
		this.numAdjacentEdges = numAdjacentEdges;
	}
	public Set getAdjacentVertices() {
		return adjacentVertices;
	}
	
	public void addAdjacentVertex(TutorVertex vertex) {
		if(adjacentVertices == null) {
			adjacentVertices = new TreeSet();
		}
		
		// only count this new vertex as adjacent if it was not before in the list
		boolean added = adjacentVertices.add(vertex);
		if(added) {
			addNumAdjacentVertices(1);
		}
	}
	
	public int compareTo(Object arg0) {
		TutorVertex vertex = ((TutorVertex) arg0);
		return vertex.label.compareTo(this.label);
	}
	
	/**
	 * sort by number of adjacent vertices
	 * @author AlbertV
	 *
	 */
	public static class TutorVertexComparator implements Comparator {
		
		public int compare(Object arg0, Object arg1) {
			TutorVertex tutorVertex0 = (TutorVertex) arg0;
			TutorVertex tutorVertex1 = (TutorVertex) arg1;
			
			if (tutorVertex0.numAdjacentVertices == tutorVertex1.numAdjacentVertices) {
				return 0;				
			}
	        else if (tutorVertex0.numAdjacentVertices > tutorVertex1.numAdjacentVertices) {
	            return 1;
	        }
	        else {
	            return -1;
	        }
		}
	    
	}
	
}
