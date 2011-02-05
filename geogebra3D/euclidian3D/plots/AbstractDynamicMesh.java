package geogebra3D.euclidian3D.plots;

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
 * 
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

	/** set to true if the element should be ignored when drawing/updating */
	final boolean ignoreFlag;

	/**
	 * squared min/max distances from the origin of the two endpoints - used for
	 * culling
	 */
	double minRadSq;
	/** ditto */
	double maxRadSq;

	/** Culling status of the element */
	public CullInfo cullInfo;

	/** true if any evaluated point of the segment is singular */
	protected boolean isSingular;

	/**
	 * @param nChildren
	 *            the amount of children the element has
	 * @param nParents
	 *            the amount of parents the element has
	 * @param level
	 *            the relative level of the element
	 * @param ignoreFlag
	 *            true if the element shouldn't be updated or drawn
	 */
	public AbstractDynamicMeshElement(int nChildren, int nParents, int level,
			boolean ignoreFlag) {
		this.nChildren = nChildren;
		this.level = level;
		this.ignoreFlag = ignoreFlag;
		children = new AbstractDynamicMeshElement[nChildren];
		parents = new AbstractDynamicMeshElement[nParents];
	}

	/**
	 * @return the level of the element
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return true if the element has been split, otherwise false.
	 */
	public boolean isSplit() {
		return isSplit;
	}

	/**
	 * @param b
	 *            true if the element has been split, false if it has been
	 *            merged
	 */
	public void setSplit(boolean b) {
		isSplit = b;
	}

	/**
	 * @param i
	 * @return the child at index i
	 */
	public AbstractDynamicMeshElement getChild(int i) {
		if (i >= nChildren)
			throw new IndexOutOfBoundsException();
		if (children[i] == null)
			createChild(i);
		return children[i];
	}

	/**
	 * Generated one or more children
	 * 
	 * @param i
	 *            the child needed
	 */
	protected abstract void createChild(int i);

	/**
	 * @return the error value associated with the segment
	 */
	protected abstract double getError();

	/**
	 * @param i
	 * @return the parent at index i
	 */
	public AbstractDynamicMeshElement getParent(int i) {
		return parents[i];
	}

	/**
	 * Sets the culling flags of the element, based on the bounding box radius
	 * 
	 * @param radSq
	 * @param drawList
	 * @param splitQueue
	 * @param mergeQueue
	 */
	public void updateCullInfo(double radSq, DynamicMeshTriList drawList,
			DynamicMeshBucketPQ splitQueue, DynamicMeshBucketPQ mergeQueue) {

		if (ignoreCull())
			return;

		CullInfo parentCull = getParentCull();
		CullInfo prev = cullInfo;

		// update cull flag
		if (parentCull == CullInfo.SOMEIN || parentCull == null) {
			if (maxRadSq < radSq)
				cullInfo = CullInfo.ALLIN;
			else if (minRadSq < radSq)
				cullInfo = CullInfo.SOMEIN;
			else
				cullInfo = CullInfo.OUT;
		} else
			cullInfo = parentCull;

		// // special case for singular segments - make sure
		// // children are always checked
		// if (isSingular)
		// cullInfo = CullInfo.SOMEIN;

		// handle new culling info
		if (prev != cullInfo || cullInfo == CullInfo.SOMEIN) {
			// hide/show the element
			setHidden(drawList, cullInfo == CullInfo.OUT);

			// reinsert into priority queue
			if (prev == CullInfo.OUT || cullInfo == CullInfo.OUT)
				reinsertInQueue(splitQueue, mergeQueue);

			// update children
			cullChildren(radSq, drawList, splitQueue, mergeQueue);
		}
	}

	/**
	 * Override if culling is to be ignored in certain cases. Always returns
	 * false by default.reinsertInQueue
	 * 
	 * @return whether culling should be ignored or not
	 */
	protected boolean ignoreCull() {
		return false;
	}

	/**
	 * @return true if any vertex in the segment is singular, otherwise false.
	 */
	public boolean isSingular() {
		return isSingular;
	}

	/**
	 * @return the culling info of the relevant parent
	 */
	abstract protected CullInfo getParentCull();

	/**
	 * Hides/shows the element based on val
	 * 
	 * @param drawList
	 *            a reference to the drawing list
	 * @param val
	 *            true if the element should be hidden, otherwise false
	 */
	abstract protected void setHidden(DynamicMeshTriList drawList, boolean val);

	/**
	 * Reinsert the element into whatever queue it's in
	 * 
	 * @param splitQueue
	 *            a reference to the split queue
	 * @param mergeQueue
	 *            a reference to the merge queue
	 */
	abstract protected void reinsertInQueue(DynamicMeshBucketPQ splitQueue,
			DynamicMeshBucketPQ mergeQueue);

	/**
	 * @param radSq
	 *            squared radius of viewing volume
	 * @param drawList
	 *            a reference to the draw list in use
	 * @param splitQueue
	 *            a reference to the split queue
	 * @param mergeQueue
	 *            a reference to the merge queue
	 */
	abstract protected void cullChildren(double radSq,
			DynamicMeshTriList drawList, DynamicMeshBucketPQ splitQueue,
			DynamicMeshBucketPQ mergeQueue);

	/**
	 * @return true if all children have been split
	 */
	public boolean childrenSplit() {
		boolean ret = false;
		for (int i = 0; i < nChildren; i++)
			ret = ret || (children[i] != null ? children[i].isSplit() : false);
		return ret;
	}

	/**
	 * Checks if the element is ready to be moved from the split to the merge queue.
	 * @param activeParent the parent that is trying to initiate the move
	 * @return true if the element can be moved, otherwise false
	 */
	public boolean readyForMerge(AbstractDynamicMeshElement activeParent) {
		return true;
	}
}

/**
 * An abstract class representing a mesh that can be dynamcially refined.
 * Refines the mesh based on two priority queues sorted by a user-defined error
 * measure. One priority queue handles merge operations and another handles
 * split operations.
 * 
 * @author André Eriksson
 */
public abstract class AbstractDynamicMesh {

	/** the queue used for merge operations */
	protected DynamicMeshBucketPQ mergeQueue;
	/** the queue used for split operations */
	protected DynamicMeshBucketPQ splitQueue;

	/** controls if debug info is displayed or not */
	protected boolean printInfo = true;

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
	protected enum Side {
		/** indicates that elements should be merged*/
		MERGE, 
		/** indicates that elements should be split*/
		SPLIT,
		/** indicates that no action should be taken*/
		NONE
	};

	/**
	 * @param mergeQueue
	 *            the PQ used for merge operations
	 * @param splitQueue
	 *            the PQ used for split operations
	 * @param drawList
	 *            the list used fo)r drawing
	 * @param nParents
	 * @param nChildren
	 * @param maxLevel
	 */
	AbstractDynamicMesh(DynamicMeshBucketPQ mergeQueue,
			DynamicMeshBucketPQ splitQueue, DynamicMeshTriList drawList,
			int nParents, int nChildren, int maxLevel) {
		this.mergeQueue = mergeQueue;
		this.splitQueue = splitQueue;
		this.drawList = drawList;
		this.nParents = nParents;
		this.nChildren = nChildren;
		this.maxLevel = maxLevel;
	}

	/**
	 * Performs a set number (stepRefinement) of splits/merges
	 * 
	 * @return false if no more updates are needed
	 */
	public boolean optimize() {
		return optimizeSub(stepRefinement);
	}

	/**
	 * updates the bounding box radius
	 * 
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
		return drawList.getTriAmt();
	}

	/**
	 * Contains the logic for split/merge operations.
	 * 
	 * @param maxCount
	 *            maximum amount of operations to be performed
	 */
	private boolean optimizeSub(int maxCount) {
		int count = 0;

		updateCullingInfo();

		long t1 = new Date().getTime();
		
		Side side = tooCoarse();
		Side prevSide;
		
		do{
			if (side == Side.MERGE)
				merge(mergeQueue.poll());
			else
				split(splitQueue.poll());
			prevSide = side;
			side = tooCoarse();
			count++;
		}while (side != Side.NONE && count < maxCount && prevSide==side);

		if (printInfo)
			System.out.println(getDebugInfo(new Date().getTime() - t1));

		if (side==Side.NONE) // this only happens if the LoD
			return false; // is at the desired level

		return true;
	}

	/**
	 * updates the culling info of each element
	 */
	protected abstract void updateCullingInfo();

	/**
	 * @param time
	 *            the time of the last update
	 * @return a string with the desired debug info
	 */
	protected abstract String getDebugInfo(long time);

	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	protected abstract Side tooCoarse();

	/**
	 * Perform a merge operation on the target element.
	 * 
	 * @param t
	 *            the target element
	 */
	protected void merge(AbstractDynamicMeshElement t) {

		if (t == null)
			return;

		// if already merged, skip
		if (!t.isSplit())
			return;

		// switch queues
		mergeQueue.remove(t);
		splitQueue.add(t);

		// mark as merged
		t.setSplit(false);

		// handle children
		for (int i = 0; i < nChildren; i++) {
			AbstractDynamicMeshElement c = t.getChild(i);

			// TODO: difference here
			if(c.readyForMerge(t)){

				splitQueue.remove(c);
				
				if (c.isSplit())
					mergeQueue.add(c);
			}

			// remove children from draw list
			drawList.remove(c, (c.parents[0] == t ? 0 : 1));
		}

		// handle parents
		for (int i = 0; i < nParents; i++) {
			AbstractDynamicMeshElement p = t.getParent(i);
			if (!p.childrenSplit()) {
				p.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
				mergeQueue.add(p);
			}
		}

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

		// switch queues
		splitQueue.remove(t);
		mergeQueue.add(t);

		// mark as split
		t.setSplit(true);

		// handle parents
		for (int i = 0; i < nParents; i++) {
			AbstractDynamicMeshElement p = t.getParent(i);
			if (p != null) {

				split(p);

				mergeQueue.remove(p);
			}
		}

		// handle children
		for (int i = 0; i < nChildren; i++) {
			AbstractDynamicMeshElement c = t.getChild(i);

			if (!c.ignoreFlag){
				c.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
	
				// add child to drawing list
				drawList.add(c, (c.parents[0] == t ? 0 : 1));
	
				if (t.getLevel() <= maxLevel && !c.isSplit())
					splitQueue.add(c);
			}

		}

		// remove from drawing list
		drawList.remove(t);
	}
}
