package geogebra3D.euclidian3D.plots;


import geogebra3D.euclidian3D.BucketPQ;
import java.nio.FloatBuffer;
import java.util.Date;

/**
 * An enum for describing the culling status of a diamond
 * 
 * @author Andr� Eriksson
 */
enum CullInfo {
	/** the entire diamond is in the viewing sphere */
	ALLIN,
	/** part of the diamond is in the viewing sphere */
	SOMEIN,
	/** the entire diamond is outside the viewing sphere */
	OUT;
};

/**
 * Abstract class representing an element to be used in a dynamic mesh.
 * @author André Eriksson
 */
abstract class AbstractDynamicMeshElement {
	private boolean isSplit;
	/** children of the element */
	protected AbstractDynamicMeshElement[] children;
	/** parents of the element */
	protected AbstractDynamicMeshElement[] parents;
	
	private final int nChildren;
	
	/** relative level of the element */
	protected final int level;
	
	/** squared distances from the origin of the two endpoints - used for culling */
	double minRadSq, maxRadSq;
	
	/** Culling status of the element */
	public CullInfo cullInfo;

	/** true if any evaluated point of the segment is singular */
	protected boolean isSingular;
	
	/**
	 * @param nChildren the amount of children the element has
	 * @param nParents the amount of parents the element has
	 * @param level the relative level of the element 
	 */
	public AbstractDynamicMeshElement(int nChildren, int nParents, int level) {
		this.nChildren = nChildren;
		this.level = level;
		children = new AbstractDynamicMeshElement[nChildren];
		parents = new AbstractDynamicMeshElement[nParents];
	}
	
	/**
	 * @return the level of the element
	 */
	public int getLevel(){
		return level;
	}
	
	/**
	 * @return true if the element has been split, otherwise false.
	 */
	public boolean isSplit() {
		return isSplit;
	}

	/**
	 * @param b true if the element has been split, false if it has been merged
	 */
	public void setSplit(boolean b) {
		isSplit=true;
	}

	/**
	 * @param i
	 * @return the child at index i
	 */
	public AbstractDynamicMeshElement getChild(int i){
		if(i>=nChildren)
			throw new IndexOutOfBoundsException();
		if(children[i]==null)
			createChild(i);
		return children[i];
	}
	
	/**
	 * Generated one or more children
	 * @param i the child needed
	 */
	protected abstract void createChild(int i);
	
	/**
	 * @param i
	 * @return the parent at index i
	 */
	public AbstractDynamicMeshElement getParent(int i){
		return parents[i];
	}
	
	/**
	 * Sets the culling flags of the element, based on the bounding box radius
	 * @param radSq
	 * @param drawList
	 */
	public void updateCullInfo(double radSq, DynamicMeshTriList drawList) {
		
		if(ignoreCull())
			return;
		
		CullInfo parentCull = getParentCull();
		CullInfo prev = cullInfo;

		//update cull flag
		if (parentCull == CullInfo.SOMEIN || parentCull==null){
			if (maxRadSq < radSq)
				cullInfo = CullInfo.ALLIN;
			else if (minRadSq < radSq)
				cullInfo = CullInfo.SOMEIN;
			else
				cullInfo = CullInfo.OUT;
		}else
			cullInfo = parentCull;

		//special case for singular segments - make sure
		//children are always checked
		if(isSingular)
			cullInfo=CullInfo.SOMEIN;
		
		//handle new culling info
		if (prev != cullInfo || cullInfo == CullInfo.SOMEIN) {
			//hide/show the element
			setHidden(drawList, cullInfo==CullInfo.OUT);
			
			// reinsert into priority queue
			if (prev == CullInfo.OUT || cullInfo == CullInfo.OUT)
				reinsertInQueue();

			//update children
			cullChildren(radSq,drawList);
		}
	}
	
	/**
	 * Override if culling is to be ignored in certain cases.
	 * Always returns false by default.
	 * @return whether culling should be ignored or not
	 */
	protected boolean ignoreCull(){
		return false;
	}
	
	/**
	 * @return true if any vertex in the segment is singular, otherwise false.
	 */
	public boolean isSingular(){
		return isSingular;
	}
	
	/**
	 * @return the culling info of the relevant parent
	 */
	abstract protected CullInfo getParentCull();
	/**
	 * Hides/shows the element based on val
	 * @param drawList
	 * @param val
	 */
	abstract protected void setHidden(DynamicMeshTriList drawList, boolean val);
	
	/**
	 * Reinsert the element into whatever queue it's in
	 */
	abstract protected void reinsertInQueue();
	
	/**
	 * @param radSq
	 * @param drawList
	 */
	abstract protected void cullChildren(double radSq,DynamicMeshTriList drawList);
	
	/**
	 * @return true if all children have been split
	 */
	public boolean childrenSplit(){
		boolean ret = false;
		for(int i = 0; i < nChildren; i++)
			ret = ret || (children[i] != null ? children[i].isSplit() : false);
		return ret;
	}
}

/**
 * An abstract class representing a mesh that can be dynamcially refined.
 * Refines the mesh based on two priority queues sorted by a user-defined
 * error measure. One priority queue handles merge operations and another
 * handles split operations.
 * 
 * @author André Eriksson
 */
public abstract class AbstractDynamicMesh {
	
	/** the queue used for merge operations */
	protected BucketPQ<AbstractDynamicMeshElement> mergeQueue;
	/** the queue used for split operations */
	protected BucketPQ<AbstractDynamicMeshElement> splitQueue;
	
	/**controls if debug info is displayed or not*/
	protected boolean printInfo;
	
	/**switch that controls if the mesh should be updated or not -
	 * typically set to false when the mesh is sufficiently refined*/
	private boolean doUpdates = true;
	
	/** the squared radius of the bounding volume */
	protected double radSq;
	
	/** the triangle list used by the mesh */
	protected DynamicMeshTriList drawList;
	
	/** the maximum amount of operations to perform in one update */
	private int stepRefinement = 1000;
	
	/** the maximum level of refinement */
	private final int maxLevel;
	
	private final int nChildren;
	private final int nParents;
	
	/** used in optimizeSub() */
	private enum Side {
		MERGE, SPLIT, NONE
	};
	
	/**
	 * @param mergeQueue the PQ used for merge operations
	 * @param splitQueue the PQ used for split operations
	 * @param drawList the list used fo)r drawing
	 * @param nParents 
	 * @param nChildren 
	 * @param maxLevel 
	 */
	AbstractDynamicMesh(BucketPQ<AbstractDynamicMeshElement> mergeQueue,
			BucketPQ<AbstractDynamicMeshElement> splitQueue, DynamicMeshTriList drawList,
			int nParents, int nChildren, int maxLevel) {
		this.mergeQueue = mergeQueue;
		this.splitQueue = splitQueue;
		this.drawList = drawList;
		this.nParents=nParents;
		this.nChildren=nChildren;
		this.maxLevel=maxLevel;
	}
	
	/**
	 * Performs a set number (stepRefinement) of splits/merges
	 */
	public void optimize() {
		if (doUpdates)
			optimizeSub(stepRefinement);
	}
	
	/** updates the bounding box radius 
	 * @param r
	 */
	public void setRadius(double r) {
		radSq = r * r;
	}
	
	/**
	 * @return Returns a FloatBuffer containing the current mesh as a triangle
	 *         list. Each triangle is represented as 9 consecutive floats. The
	 *         FloatBuffer will probably contain extra floats - use
	 *         getTriangleCount() to find out how many floats are valid.
	 */
	public FloatBuffer getVertices() {
		return drawList.getTriangleBuffer();
	}

	/**
	 * @return Returns a FloatBuffer containing the current mesh as a triangle
	 *         list.
	 */
	public FloatBuffer getNormals() {
		return drawList.getNormalBuffer();
	}

	/**
	 * @return the amount of triangles in the current mesh.
	 */
	public int getTriangleCount() {
		return drawList.getCount();
	}

	/** 
	 * Contains the logic for split/merge operations.
	 * @param maxCount maximum amount of operations to be performed
	 */
	private void optimizeSub(int maxCount) {
		int count = 0;

		updateCullingInfo();

		long t1 = new Date().getTime();

		Side side = tooCoarse() ? Side.SPLIT : Side.MERGE;
		while (side != Side.NONE && count < maxCount) {
			if (side == Side.MERGE) {
				merge(mergeQueue.poll());
				if (tooCoarse())
					side = Side.NONE;
			} else {
				split(splitQueue.poll());
				if (!tooCoarse())
					side = Side.NONE;
			}
			count++;
		}

//		if (count < maxCount) // this only happens if the LoD
//			doUpdates = false; // is at the desired level

		if (printInfo)
			System.out.println(getDebugInfo(new Date().getTime()-t1));
	}
	
	/**
	 * updates the culling info of each element
	 */
	protected abstract void updateCullingInfo();
	
	/**
	 * @param time the time of the last update
	 * @return a string with the desired debug info
	 */
	protected abstract String getDebugInfo(long time);

	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	protected abstract boolean tooCoarse();

	/**
	 * Perform a merge operation on the target element.
	 * 
	 * @param t
	 *            the target element
	 */
	protected void merge(AbstractDynamicMeshElement t) {

		if (t == null)
			return;

		AbstractDynamicMeshElement temp;

		// if already merged, skip
		if (!t.isSplit())
			return;
		t.setSplit(false); // mark as not split

		// handle kids
		for (int i = 0; i < nChildren; i++) {
			temp = t.getChild(i);
			splitQueue.remove(temp);
			
			if (temp.isSplit())
				mergeQueue.add(temp);
			
			// remove children from draw list
			drawList.remove(temp);
		}

		// handle parents
		for (int i = 0; i < nParents; i++) {
			AbstractDynamicMeshElement p = t.getParent(i);
			if (!p.childrenSplit()) {
				p.updateCullInfo(radSq, drawList);
				mergeQueue.add(p);
			}
		}

		mergeQueue.remove(t);

		splitQueue.add(t); // add to split queue

		// add to draw list
		drawList.add(t);
	}

	/**
	 * Perform a split operation on the target element.
	 * 
	 * @param t
	 *            the target element
	 */
	protected void split(AbstractDynamicMeshElement t) {

		if (t == null)
			return;

		// dont split an element that has already been split
		if (t.isSplit())
			return;

		splitQueue.remove(t);

		mergeQueue.add(t); // add to merge queue

		t.setSplit(true); // mark as split

		// remove parent from merge queue
		for(int i = 0; i < nParents; i++){
			AbstractDynamicMeshElement p = t.getParent(i);
			if(p!=null){
				
				//TODO: diff here
				
				mergeQueue.remove(p);
			}
		}

		// get kids, insert into priority queue
		for (int i = 0; i < nChildren; i++) {
			AbstractDynamicMeshElement c = t.getChild(i);
			
			//TODO: diff here
			
			// add child to drawing list
			drawList.add(c); //TODO: diff here

			c.updateCullInfo(radSq, drawList);

			if (t.getLevel() <= maxLevel && !c.isSplit())
				splitQueue.add(c);

		}

		// remove from drawing list
		drawList.remove(t); //TODO: diff here
	}
}
