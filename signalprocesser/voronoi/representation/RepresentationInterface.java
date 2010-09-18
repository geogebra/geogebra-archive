package signalprocesser.voronoi.representation;

import java.util.ArrayList;
import java.util.Collection;
import signalprocesser.voronoi.VPoint;
import signalprocesser.voronoi.statusstructure.VLinkedNode;

public interface RepresentationInterface {
    
    // Executed before the algorithm begins to process (can be used to
    //   initialise any data structures required)
    public void beginAlgorithm(Collection<VPoint> points);
    
    // Called to record various events
    public void siteEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 );
    public void circleEvent( VLinkedNode n1 , VLinkedNode n2 , VLinkedNode n3 , int circle_x , int circle_y );
    
    // Called when the algorithm has finished processing
    public void endAlgorithm(Collection<VPoint> points, double lastsweeplineposition, VLinkedNode headnode);
    
}
