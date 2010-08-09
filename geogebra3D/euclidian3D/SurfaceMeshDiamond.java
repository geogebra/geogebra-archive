package geogebra3D.euclidian3D;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.GeoFunctionNVar;

/**
 * A class representing a diamond.
 * 
 * @author André Eriksson
 */
public class SurfaceMeshDiamond {
	/** vertex position */
	GgbVector v;
	/** vertex normal */
	GgbVector normal;
	/** radius of sphere bound squared */
	double boundingRadSq;
	/** error measure */
	double[] errors = new double[2];
	/** diamond's parents (two of its corners) */
	SurfaceMeshDiamond[] parents = new SurfaceMeshDiamond[2];
	/** the other two corners */
	SurfaceMeshDiamond[] a = new SurfaceMeshDiamond[2];
	/** children */
	private SurfaceMeshDiamond[] children = new SurfaceMeshDiamond[4];
	/** the index of this diamond within each of its parents */
	int[] indices = new int[2];
	/** level of resolution */
	final int level;
	/** a reference to the function used */
	GeoFunctionNVar func;
	/** flag indicating if the diamond has been split */
	private boolean split = false;
	/** flag indicating if the diamond is in the merge queue */
	boolean inMergeQueue = false;
	/** flag indicating if the diamond is in the split queue */
	boolean inSplitQueue = false;

	/** half the area of the diamond (parameter wise) */
	public double area;

	private double infConst = 3000.0;

	/** (approximately) the minimum distance from the origin to the diamond */
	double minRadSq;
	/** (approximately) the maximum distance from the origin to the diamond */
	double maxRadSq;

	/** a reference to the merge queue */
	MergeQueue mergeQueue;
	/** a reference to the split queue */
	SplitQueue splitQueue;

	/** x/y difference used when estimating normal */
	private final double normalDelta = 1e-8;

	/** flag used to discern triangles outside domain of defininition */
	boolean isClipped = false;

	/** culling info for the diamond */
	SurfaceMesh.CullInfo cullInfo;

	/** pointer to the next element in a priority queue */
	SurfaceMeshDiamond nextInQueue;
	/** pointer to the previous element in a priority queue */
	SurfaceMeshDiamond prevInQueue;
	/** index of bucket in priority queue, set to -1 if not a member of a queue */
	int bucketIndex = -1;

	private double param1, param2;

	private TriListElem[] triangles = new TriListElem[2];

	/**
	 * @return true if the diamond is split, otherwise false
	 */
	public boolean isSplit() {
		return split;
	}

	/**
	 * @return true iff at least one child is split
	 */
	public boolean childrenSplit() {
		return (children[0] != null ? children[0].split : false)
				|| (children[1] != null ? children[1].split : false)
				|| (children[2] != null ? children[2].split : false)
				|| (children[3] != null ? children[3].split : false);
	}

	/**
	 * sets the split flag to the specified value
	 * 
	 * @param val
	 */
	public void setSplit(boolean val) {
		split = val;
	}

	/**
	 * @param j
	 *            either 0 or 1.
	 * @return the triangle with index j or null if that triangle does not
	 *         exist.
	 */
	public TriListElem getTriangle(int j) {
		return triangles[j];
	}

	/**
	 * sets triangles[j] to t.
	 * 
	 * @param j either 0 or 1.
	 * @param t
	 */
	public void setTriangle(int j, TriListElem t) {
		triangles[j] = t;
	}

	/**
	 * releases the triangle with index j
	 * 
	 * @param j either 0 or 1.
	 */
	public void freeTriangle(int j) {
		triangles[j] = null;
	}

	/**
	 * A simple constructor. Use only when bootstrapping a mesh.
	 * 
	 * @param func
	 * @param p1
	 * @param p2
	 * @param level
	 * @param spQ
	 * @param merQ
	 */
	SurfaceMeshDiamond(GeoFunctionNVar func, double p1, double p2, int level,
			SplitQueue spQ, MergeQueue merQ) {
		this.level = level;
		this.func = func;
		v = func.evaluatePoint(p1, p2);
		param1 = p1;
		param2 = p2;
		estimateNormal(func);
		splitQueue = spQ;
		mergeQueue = merQ;
	}

	/**
	 * sets area to the base area of the diamond (parameter wise)
	 */
	public void setArea() {
		if(a[0].param1 - param1!=0)
			area = Math.abs((a[0].param1 - param1) * (parents[0].param2 - param2));
		else
			area = Math.abs((parents[1].param1 - param1) * (a[0].param2 - param2));
		if((parents[1].param1 - param1) * (a[0].param2 - param2) < 0 || 
				(a[0].param1 - param1) * (parents[0].param2 - param2) < 0)
			System.out.print("");
			
	}

	/**
	 * @param func
	 * @param parent0
	 * @param index0
	 * @param parent1
	 * @param index1
	 * @param a0
	 * @param a1
	 * @param level
	 * @param spQ
	 * @param merQ
	 */
	SurfaceMeshDiamond(GeoFunctionNVar func, SurfaceMeshDiamond parent0, int index0,
			SurfaceMeshDiamond parent1, int index1, SurfaceMeshDiamond a0,
			SurfaceMeshDiamond a1, int level, SplitQueue spQ, MergeQueue merQ) {
		splitQueue = spQ;
		mergeQueue = merQ;
		this.level = level;
		this.func = func;
		parents[0] = parent0;
		parents[1] = parent1;
		indices[0] = index0;
		indices[1] = index1;
		a[0] = a0;
		a[1] = a1;
		param1 = (a[0].param1 + a[1].param1) * 0.5;
		param2 = (a[0].param2 + a[1].param2) * 0.5;
		v = func.evaluatePoint(param1, param2);
		estimateNormal(func);
		setBoundingRadius();
		setArea();
		setError();

		// set clipping flag
		if (a0.isClipped || (parent0.isClipped && parent1.isClipped))
			isClipped = true;
	}

	/**
	 * Estimates a normal at the current point by evaluating the curve at two
	 * nearby locations and taking the cross product.
	 * 
	 * @param func
	 *            the function to be evaluated
	 */
	private void estimateNormal(GeoFunctionNVar func) {
		GgbVector dx = func.evaluatePoint(param1 + normalDelta, param2);
		GgbVector dy = func.evaluatePoint(param1, param2 + normalDelta);
		normal = dx.sub(v).crossProduct(dy.sub(v)).normalized();
	}

	/**
	 * Computes the error for the diamond.
	 */
	private void setError() {
		GgbVector v0 = a[1].v.sub(parents[0].v);
		GgbVector v1 = a[0].v.sub(parents[0].v);
		GgbVector v2 = a[0].v.sub(parents[1].v);
		GgbVector v3 = a[1].v.sub(parents[1].v);

		GgbVector n0 = v0.crossProduct(v1);
		GgbVector n1 = v2.crossProduct(v3);

		double a0 = n0.norm(); // proportional to area
		double a1 = n1.norm();

		n0.normalize();
		n1.normalize();

		GgbVector o0 = v.sub(parents[0].v);
		GgbVector o1 = v.sub(parents[1].v);

		double d0 = Math.abs(n0.dotproduct(o0));
		double d1 = Math.abs(n1.dotproduct(o1));

		// vol is proportional to actual volume
		double vol1 = d0 * a0;
		double vol2 = d1 * a1;

		if (Double.isNaN(vol1))
			// use a different error measure for infinite points
			// namely the base area times some constant
			errors[0] = area * area * infConst;
		else
			errors[0] = vol1;
		if (Double.isNaN(vol2))
			errors[1] = area * area * infConst;
		else
			errors[1] = vol2;

		int fac = 0;
		if (!parents[0].v.isDefined())fac++;
		if (!parents[1].v.isDefined())fac++;
		if (!a[0].v.isDefined())fac++;
		if (!a[1].v.isDefined())fac++;
		if(fac==4)
			errors[0] = errors[1] = 0;
		else if (fac > 2)
			errors[0]*=2.0; errors[1]*=2.0;
	}

	/**
	 * Sets the (squared) bounding radius of the triangle based on the distances
	 * from its midpoint to its corner vertices.
	 */
	public void setBoundingRadius() {
		boundingRadSq = a[0].v.sub(v).squareNorm();
		double r = a[1].v.sub(v).squareNorm();

		if (r > boundingRadSq)
			boundingRadSq = r;

		if (parents[0] != null) {
			r = parents[0].v.sub(v).squareNorm();
			if (r > boundingRadSq)
				boundingRadSq = r;
		}
		if (parents[1] != null) {
			r = parents[1].v.sub(v).squareNorm();
			if (r > boundingRadSq)
				boundingRadSq = r;
		}

		minRadSq = v.norm() - Math.sqrt(boundingRadSq);
		maxRadSq = v.norm() + Math.sqrt(boundingRadSq);
		if (minRadSq < 0)
			minRadSq = 0;
		if (Double.isNaN(minRadSq))
			minRadSq = 0;
		if (Double.isNaN(maxRadSq))
			maxRadSq = Double.POSITIVE_INFINITY;
		minRadSq *= minRadSq;
	}

	/**
	 * @param ix
	 *            index of child
	 * @param t
	 *            reference to child
	 */
	public void setChild(int ix, SurfaceMeshDiamond t) {
		children[ix] = t;
	}

	/**
	 * note: this assumes that p isn't a copy of a parent, or some other diamond
	 * 
	 * @param p
	 *            a reference to a parent
	 * @return the other parent
	 */
	public SurfaceMeshDiamond getOtherParent(SurfaceMeshDiamond p) {
		if (p == parents[0])
			return parents[1];
		return parents[0];
	}

	/**
	 * Retrieves a child of the diamond. If the child doesn't exist, it is
	 * created before being returned.
	 * 
	 * @param i
	 *            index of child
	 * @return reference to child
	 */
	public SurfaceMeshDiamond getChild(int i) {
		if (children[i] == null) {

			SurfaceMeshDiamond parent;
			// get other parent
			SurfaceMeshDiamond otherParent = null;
			if (i < 2) {
				parent = parents[0];
				if (parent != null)
					otherParent = parent.getChild((indices[0] + (i == 0 ? 1
							: -1)) & 3);
			} else {
				parent = parents[1];
				if (parent != null)
					otherParent = parent.getChild((indices[1] + (i == 2 ? 1
							: -1)) & 3);
			}
			SurfaceMeshDiamond a0 = parents[i >> 1];
			SurfaceMeshDiamond a1 = a[((i + 1) & 2) >> 1];

			int ix = (i & 1) ^ 1;
			if (otherParent != null && otherParent.parents[1] == parent)
				ix |= 2;
			if (i == 1 || i == 3)
				children[i] = new SurfaceMeshDiamond(func, otherParent, ix, this,
						i, a0, a1, level + 1, splitQueue, mergeQueue);
			else
				children[i] = new SurfaceMeshDiamond(func, this, i, otherParent,
						ix, a0, a1, level + 1, splitQueue, mergeQueue);

			if (otherParent != null)
				otherParent.children[ix] = children[i];
		}
		return children[i];
	}

	/**
	 * Checks if a child has been created.
	 * 
	 * @param i
	 *            the index of the child
	 * @return false if the child is null, otherwise true.
	 */
	public boolean childCreated(int i) {
		return children[i] != null;
	}

	/**
	 * @param radSq
	 * @param drawList
	 */
	public void updateCullInfo(double radSq, SurfTriList drawList) {
		// ignore clipped diamonds
		if (isClipped)
			return;
		SurfaceMesh.CullInfo parentCull = a[1].cullInfo;
		SurfaceMesh.CullInfo oldCull = cullInfo;

		if (parentCull == SurfaceMesh.CullInfo.ALLIN || parentCull == SurfaceMesh.CullInfo.OUT)
			cullInfo = parentCull;
		else {
			if (maxRadSq < radSq)
				cullInfo = SurfaceMesh.CullInfo.ALLIN;
			else if (minRadSq < radSq)
				cullInfo = SurfaceMesh.CullInfo.SOMEIN;
			else
				cullInfo = SurfaceMesh.CullInfo.OUT;
		}

		if (oldCull != cullInfo || cullInfo == SurfaceMesh.CullInfo.SOMEIN) {
			if (cullInfo == SurfaceMesh.CullInfo.OUT) {
				if (triangles[0] != null && triangles[0].getIndex() != -1)
					drawList.hide(this, 0);
				if (triangles[1] != null && triangles[1].getIndex() != -1)
					drawList.hide(this, 1);
			} else if (cullInfo == SurfaceMesh.CullInfo.ALLIN
					|| cullInfo == SurfaceMesh.CullInfo.SOMEIN) {
				if (triangles[0] != null && triangles[0].getIndex() == -1)
					drawList.show(this, 0);
				if (triangles[1] != null && triangles[1].getIndex() == -1)
					drawList.show(this, 1);
			}
			// reinsert into priority queue
			if (oldCull == SurfaceMesh.CullInfo.OUT && cullInfo != SurfaceMesh.CullInfo.OUT
			||  oldCull != SurfaceMesh.CullInfo.OUT && cullInfo == SurfaceMesh.CullInfo.OUT) {
				if (inMergeQueue) {
					mergeQueue.remove(this);
					mergeQueue.add(this);
				} else if (inSplitQueue) {
					splitQueue.remove(this);
					splitQueue.add(this);
				}
			}
		}

		if (split) {
			for (int i = 0; i < 4; i += 2) {
				if (childCreated(i)) {
					SurfaceMeshDiamond c = children[i];
					if (c.parents[0] == this) {
						if (c.childCreated(0))
							c.children[0].updateCullInfo(radSq, drawList);
						if (c.childCreated(1))
							c.children[1].updateCullInfo(radSq, drawList);
					} else {
						if (c.childCreated(2))
							c.children[2].updateCullInfo(radSq, drawList);
						if (c.childCreated(3))
							c.children[3].updateCullInfo(radSq, drawList);
					}
				}
			}
		}
	}

	/** inserts the diamond into the split queue */
	public void addToSplitQueue() {
		if (!inSplitQueue) {
			splitQueue.add(this);
			inSplitQueue = true;
		}
	}

	/** removes the diamond from the split queue */
	public void removeFromSplitQueue() {
		splitQueue.remove(this);
		inSplitQueue = false;
	}

	/** adds the diamond to the merge queue */
	public void addToMergeQueue() {
		if (!inMergeQueue) {
			mergeQueue.add(this);
			inMergeQueue = true;
		}
	}

	/** removes the diamond from the merge queue */
	public void removeFromMergeQueue() {
		if (inMergeQueue) {
			mergeQueue.remove(this);
			inMergeQueue = false;
		}
	}

	@Override
	public String toString() {
		return "[l=" + level + ", e=(" + errors[0] + ", " + errors[1] + ") ("
				+ v.getX() + "," + v.getY() + ")]";
	}
};