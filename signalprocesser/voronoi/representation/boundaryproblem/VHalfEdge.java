package signalprocesser.voronoi.representation.boundaryproblem;
import signalprocesser.voronoi.VPoint;

import java.util.*;

public class VHalfEdge {

    /* ***************************************************** */
    // Variables
    
    public int vertexnumber;
    public boolean isdeleted = false;
    public VVertex vertex;
    
    /* ***************************************************** */
    // Constructors
    
    public VHalfEdge(int _vertexnumber, VVertex _vertex) {
        this.vertexnumber = _vertexnumber;
        this.vertex = _vertex;
    }

    /* ***************************************************** */
}
