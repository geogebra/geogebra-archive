package tutor.model;

/**
 * a TutorEdge is the representation of a GeoLine from a TutorGraph point of view 
 * @author AlbertV
 *
 */
public class TutorEdge {
	
	private int numAdjacentVertices;
	private int numAdjacentEdges;
	
	public int getNumAdjacentVertices() {
		return numAdjacentVertices;
	}
	public void setNumAdjacentVertices(int numAdjacentVertices) {
		this.numAdjacentVertices = numAdjacentVertices;
	}
	public int getNumAdjacentEdges() {
		return numAdjacentEdges;
	}
	public void setNumAdjacentEdges(int numAdjacentEdges) {
		this.numAdjacentEdges = numAdjacentEdges;
	}
	
}
