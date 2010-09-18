package signalprocesser.voronoi.statusstructure.binarysearchtreeimpl;

import signalprocesser.voronoi.statusstructure.*;

public interface VNode {

    /* ***************************************************** */
    // Methods
    
    public void setParent(VInternalNode _parent);
    public VInternalNode getParent();
    
    public boolean isLeafNode();
    
    public boolean isInternalNode();
    
    
    /* ***************************************************** */
    
}
