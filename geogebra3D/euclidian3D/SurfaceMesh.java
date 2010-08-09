package geogebra3D.euclidian3D;

import java.nio.FloatBuffer;
import java.util.Date;

import geogebra.kernel.GeoFunctionNVar;
import geogebra3D.euclidian3D.TriList;

//TODO: replace periodic arrangement with null pointers
//TODO: use simpler base mesh and implement expand()

/**
 * @author André Eriksson
 */
public class SurfaceMesh {
	private GeoFunctionNVar function;
	private double radSq;
	private SplitQueue splitQueue = new SplitQueue();
	private MergeQueue mergeQueue = new MergeQueue();

	private SurfaceMeshDiamond root;

	private SurfTriList drawList;

	private final double errorConst = .04; // desired error per area unit
	private final int triGoal = 10000;
	private final int minTriCount = 4000;
	/** base diamonds - level 0 */
	private SurfaceMeshDiamond[][] base0 = new SurfaceMeshDiamond[4][4];
	/** base diamonds - level -1 or lower */
	private SurfaceMeshDiamond[][] base1 = new SurfaceMeshDiamond[4][4];

	private final int initialRefinement = 300;
	private final int stepRefinement = 1000;

	private double baseArea;
	private final int maxTriangles = 200000;
	private final int triBufMarigin = 1000;

	/** the maximum level of refinement */
	private static final int maxLevel = 20;

	private static final boolean printInfo = true;
	
	/**
	 * An enum for describing the culling status of a diamond
	 * 
	 * @author André Eriksson
	 */
	public enum CullInfo {
		/** the entire diamond is in the viewing sphere */
		ALLIN,
		/** part of the diamond is in the viewing sphere */
		SOMEIN,
		/** the entire diamond is outside the viewing sphere */
		OUT;
	};

	/**
	 * @param r
	 *            the bounding radius of the viewing volume
	 */
	public void setRadius(double r) {
		radSq = r * r;
	}

	/**
	 * @param function
	 * @param radSq
	 */
	public SurfaceMesh(GeoFunctionNVar function, double radSq,
			boolean unlimitedRange) {
		this.function = function;
		this.radSq = radSq;
		drawList = new SurfTriList(maxTriangles, triBufMarigin);
		if (unlimitedRange)
			initMesh(-radSq, radSq, -radSq, radSq);
		else
			initMesh(function.getMinParameter(0), function.getMaxParameter(0),
					function.getMinParameter(1), function.getMaxParameter(1));
		base0[1][1].addToSplitQueue();
		optimizeSub(initialRefinement);
	}

	/**
	 * Bootstraps a simple square mesh. Sets base0 and base1.
	 * 
	 * @param xMin
	 *            the minimum x coordinate
	 * @param xMax
	 *            the maximum x coordinate
	 * @param yMin
	 *            the minimum y coordinate
	 * @param yMax
	 *            the maximum y coordinate
	 */
	@SuppressWarnings("unused")
	private void initSmallMesh(double xMin, double xMax, double yMin,
			double yMax) {

		// TODO: implement this function properly, have it replace initMesh()
		SurfaceMeshDiamond p1 = new SurfaceMeshDiamond(function, xMin, yMin, -1,
				splitQueue, mergeQueue);
		SurfaceMeshDiamond a0 = new SurfaceMeshDiamond(function, xMin, yMax, -2,
				splitQueue, mergeQueue);
		SurfaceMeshDiamond p0 = new SurfaceMeshDiamond(function, xMax, yMax, -1,
				splitQueue, mergeQueue);
		SurfaceMeshDiamond a1 = new SurfaceMeshDiamond(function, xMax, yMin, -3,
				splitQueue, mergeQueue);

		p1.setSplit(true);
		p0.setSplit(true);
		a0.setSplit(true);
		a1.setSplit(true);

		int index0 = 0;
		int index1 = 1;

		root = new SurfaceMeshDiamond(function, p0, index0, p1, index1, a0, a1, 0,
				splitQueue, mergeQueue);

		p0.setChild(index0, root);
		p1.setChild(index1, root);
	}

	/**
	 * Bootstraps a fairly complex mesh. Sets base0 and base1.
	 * 
	 * @param xMin
	 *            the minimum x coordinate
	 * @param xMax
	 *            the maximum x coordinate
	 * @param yMin
	 *            the minimum y caoordinate
	 * @param yMax
	 *            the maximum y coordinate
	 */
	private void initMesh(double xMin, double xMax, double yMin, double yMax) {
		int di, ix, jx;
		double x, y;
		SurfaceMeshDiamond t;

		baseArea = (xMax - xMin) * (yMax - yMin);

		double dx = (xMax - xMin);
		double dy = (yMax - yMin);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				x = xMin + (i - 0.5) * dx;
				y = yMin + (j - 0.5) * dy;
				base0[j][i] = new SurfaceMeshDiamond(function, x, y, 0, splitQueue,
						mergeQueue);
				if (!(i == 1 && j == 1))
					base0[j][i].isClipped = true;

				x = xMin + (i - 1) * dx;
				y = yMin + (j - 1) * dy;
				base1[j][i] = t = new SurfaceMeshDiamond(function, x, y,
						((i ^ j) & 1) != 0 ? -1 : -2, splitQueue, mergeQueue);
				t.setSplit(true);
			}

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				t = base0[j][i];
				di = ((i ^ j) & 1) != 0 ? 1 : -1;
				ix = ((2 * i + 1 - di) >> 1) % 4;
				jx = (2 * j >> 1) % 4;
				t.parents[0] = base1[jx][ix];
				ix = ((2 * i + 1 + di) >> 1) % 4;
				jx = (2 * (j + 1) >> 1) % 4;
				t.parents[1] = base1[jx][ix];
				ix = (2 * i >> 1) % 4;
				jx = ((2 * j + 1 + di) >> 1) % 4;
				t.a[0] = base1[jx][ix];
				ix = ((2 * (i + 1)) >> 1) % 4;
				jx = ((2 * j + 1 - di) >> 1) % 4;
				t.a[1] = base1[jx][ix];
				ix = (di < 0 ? 0 : 3);
				t.parents[0].setChild(ix, t);
				t.indices[0] = ix;
				ix = (di < 0 ? 2 : 1);
				t.parents[1].setChild(ix, t);
				t.indices[1] = ix;
			}
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				t = base1[j][i];
				t.a[1] = base1[(j + 3) % 4][i];
				t.a[0] = base1[(j + 1) % 4][i];
				t.parents[0] = base1[j][(i + 3) % 4];
				t.parents[1] = base1[j][(i + 1) % 4];
			}
		base0[1][1].setBoundingRadius();
		base0[1][1].setArea();
	}

	private void updateCullingInfo() {
		SurfaceMeshDiamond d = base0[1][1];
		d.updateCullInfo(radSq, drawList);

		if (d.childCreated(0))
			d.getChild(0).updateCullInfo(radSq, drawList);
		if (d.childCreated(1))
			d.getChild(1).updateCullInfo(radSq, drawList);
		if (d.childCreated(2))
			d.getChild(2).updateCullInfo(radSq, drawList);
		if (d.childCreated(3))
			d.getChild(3).updateCullInfo(radSq, drawList);
	}

	/**
	 * Expands the mesh to accomodate for a larger bounding sphere
	 */
	@SuppressWarnings("unused")
	private void expand() {

	}

	/** used in optimizeSub() */
	private enum Side {
		MERGE, SPLIT, NONE
	};

	/**
	 * Performs a set number (stepRefinement) of splits/merges
	 */
	public void optimize() {
		optimizeSub(stepRefinement);
	}

	private void optimizeSub(int maxCount) {
		int count = 0;

		updateCullingInfo();

		long t1 = new Date().getTime();

		Side side = tooCoarse() ? Side.SPLIT : Side.MERGE;
		while (side != Side.NONE && count < maxCount) {
			if (side == Side.MERGE) {
				merge(mergeQueue.pop());
				if (tooCoarse())
					side = Side.NONE;
			} else {
				split(splitQueue.pop());
				if (!tooCoarse())
					side = Side.NONE;
			}
			count++;
		}
		if (printInfo)
			System.out.println(this.function + ":\tupdate time: "
					+ (new Date().getTime() - t1) + "ms\ttriangles: "
					+ drawList.getCount() + "\terror: "
					+ (float) drawList.getError());
	}

	/**
	 * @return true if the mesh should be refined, otherwise false
	 */
	private boolean tooCoarse() {
		double minError = 0.1;
		if (drawList.getError() < minError * drawList.getArea())
			if (drawList.getCount() > triGoal)
				return false;
		if (drawList.getCount() < minTriCount)
			return true;
		if (!drawList.isFull())
			if ((drawList.getError() > errorConst * baseArea)
					|| drawList.getCount() < minTriCount)
				return true;
		return false;
	}

	/**
	 * Recursively splits the target diamond
	 * 
	 * @param t
	 *            the target diamond
	 */
	private void split(SurfaceMeshDiamond t) {
		if (t == null)
			return;

		// dont split a diamond that has already been split
		if (t.isSplit()) {
			return;
		}

		SurfaceMeshDiamond temp;

		t.removeFromSplitQueue();

		t.addToMergeQueue(); // add to merge queue

		t.setSplit(true); // mark as split

		// recursively split parents
		for (int i = 0; i < 2; i++) {
			temp = t.parents[i];
			if (temp == null)
				continue;
			split(temp);
			temp.removeFromMergeQueue();
		}

		// get kids, insert into priority queue
		for (int i = 0; i < 4; i++) {
			temp = t.getChild(i);
			if (!temp.isClipped) {

				temp.updateCullInfo(radSq, drawList);

				if (t.level <= maxLevel && !temp.isSplit())
					temp.addToSplitQueue();

				// add child to drawing list
				drawList.add(temp, (temp.parents[1] == t ? 1 : 0));
			}
		}

		// remove from drawing list
		drawList.remove(t, 0);
		drawList.remove(t, 1);
	}

	/**
	 * Merges the triangles in the target diamond
	 * 
	 * @param t
	 *            the target diamond
	 */
	private void merge(SurfaceMeshDiamond t) {
		if (t == null)
			return;

		SurfaceMeshDiamond temp;

		// if already merged, skip
		if (!t.isSplit())
			return;
		t.setSplit(false); // mark as not split

		// handle kids
		for (int i = 0; i < 4; i++) {
			temp = t.getChild(i);
			if (!temp.getOtherParent(t).isSplit()) {
				temp.removeFromSplitQueue(); // remove from split queue
				if (temp.isSplit())
					temp.addToMergeQueue();
			}

			// remove children from draw list
			drawList.remove(temp, (temp.parents[1] == t ? 1 : 0));
		}
		t.removeFromMergeQueue();

		for (int i = 0; i < 2; i++) {
			SurfaceMeshDiamond p = t.parents[i];
			if (!p.childrenSplit()) {
				p.updateCullInfo(radSq, drawList);
				p.addToMergeQueue();
			}
		}

		t.addToSplitQueue(); // add to split queue

		// add parent triangles to draw list
		drawList.add(t, 0);
		drawList.add(t, 1);
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
}

/**
 * A list of triangles representing the current mesh.
 * 
 * @author André Eriksson
 */
class SurfTriList extends TriList {

	private double totalError = 0;
	private double totalArea = 0;

	SurfTriList(int capacity, int marigin) {
		super(capacity, marigin);
	}

	/**
	 * @return the total error for all visible triangles
	 */
	public double getError() {
		return totalError;
	}

	/**
	 * @return the total area (parameter wise) for all visible triangles
	 */
	public double getArea() {
		return totalArea;
	}

	/**
	 * Adds a triangle to the list.
	 * 
	 * @param d
	 *            The parent diamond of the triangle
	 * @param j
	 *            The index of the triangle within the diamond
	 */
	public void add(SurfaceMeshDiamond d, int j) {
		// handle clipping
		if(d.isClipped || d.parents[j].isClipped)
			return;

		totalError += d.errors[j];
		totalArea += d.area;

		float[] v = new float[9];
		float[] n = new float[9];

		calcFloats(d, j, v, n);

		TriListElem t = super.add(v, n);
	
		d.setTriangle(j, t);

		if (d.cullInfo == SurfaceMesh.CullInfo.OUT)
			hide(d, j);
	}

	private void calcFloats(SurfaceMeshDiamond d, int j, float[] v, float[] n) {
		SurfaceMeshDiamond t[] = new SurfaceMeshDiamond[3];
		t[1] = d.parents[j];
		if (j != 0) {
			t[0] = d.a[1];
			t[2] = d.a[0];
		} else {
			t[0] = d.a[0];
			t[2] = d.a[1];
		}
		for (int i = 0, c = 0; i < 3; i++, c += 3) {
			v[c] = (float) t[i].v.getX();
			v[c + 1] = (float) t[i].v.getY();
			v[c + 2] = (float) t[i].v.getZ();
			n[c] = (float) t[i].normal.getX();
			n[c + 1] = (float) t[i].normal.getY();
			n[c + 2] = (float) t[i].normal.getZ();
		}
	}

	/**
	 * Removes a triangle from the queue.
	 * 
	 * @param d
	 *            The parent diamond of the triangle
	 * @param j
	 *            The index of the triangle within the diamond
	 */
	public void remove(SurfaceMeshDiamond d, int j) {
		// handle clipping
		if (d.isClipped || d.parents[j].isClipped)
			return;

		hide(d, j);

		// free triangle
		d.freeTriangle(j);
	}

	/**
	 * removes a triangle from the list, but does not erase it
	 * 
	 * @param d the diamond
	 * @param j the triangle index
	 */
	public void hide(SurfaceMeshDiamond d, int j) {
		if(super.hide(d.getTriangle(j))) {
			totalError -= d.errors[j];
			totalArea -= d.area;
		}
	}

	/**
	 * shows a triangle that has been hidden
	 * 
	 * @param d the diamond
	 * @param j the index of the triangle
	 */
	public void show(SurfaceMeshDiamond d, int j) {
		if(super.show(d.getTriangle(j))) {
			totalError += d.errors[j];
			totalArea += d.area;
		}
	}
}
/**
 * A priority queue used for split operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class SplitQueue extends BucketPriorityQueue {

	@Override
	protected int clamp(double d) {
		int f = (int) (Math.exp(d + 1) * 200) + 3;
		if(d==0.0)
			return 1;
		return f > BUCKETAMT - 1 || f < 0 ? BUCKETAMT - 1 : f;
	}
}

/**
 * A priority queue used for merge operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class MergeQueue extends BucketPriorityQueue {

	@Override
	protected int clamp(double d) {
		int f = (int) (Math.exp(1 - d) * 200);
		int ret = f > BUCKETAMT - 1 ? BUCKETAMT - 1 : f;
		if (ret < 0)
			ret = 0;
		return ret;
	}
}