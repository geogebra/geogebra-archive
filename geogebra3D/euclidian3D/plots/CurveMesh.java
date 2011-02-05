package geogebra3D.euclidian3D.plots;

import geogebra.Matrix.Coords;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.TriListElem;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

//TODO: Investigate how/why the error of Curve[t,0,t*t,t,-10,10] converges to ~31.75

/**
 * An element in a CurveMesh.
 * 
 * @author André Eriksson
 */
class CurveSegment extends AbstractDynamicMeshElement {

	/** points evaluated on a circle - used for generating points[] */
	public final float[] sines;
	/** same */
	public final float[] cosines;

	/** a reference to the curve being drawn */
	private GeoCurveCartesian3D curve;

	/** error value associated with the segment */
	double error;

	/** length of the segment */
	double length;

	private float scale;

	/** parameter values at the start and end of the segment */
	double[] params = new double[3];

	/** positions at the start/end of the sement */
	Coords[] vertices = new Coords[3];

	/** tangents at start and end positions */
	public Coords[] tangents = new Coords[3];

	/** triangle list element */
	public TriListElem triListElem;

	/**
	 * the vertices of the segment addressed in the order [start/end][vertex
	 * num][x/y/z]
	 */
	float[][][] points = new float[2][][];
	/** normals for the vertices */
	float[][][] normals = new float[2][][];

	/**
	 * @param curve
	 * @param level
	 * @param pa1
	 *            parameter value at first endpoint
	 * @param pa2
	 *            parameter value at second endpoint
	 * @param cosines
	 * @param sines
	 */
	public CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, float[] cosines, float[] sines) {
		super(2, 1, level, false);

		this.curve = curve;

		Coords v1 = curve.evaluateCurve(pa1);
		Coords v2 = curve.evaluateCurve(pa2);
		Coords t1 = approxTangent(pa1, v1);
		Coords t2 = approxTangent(pa2, v2);

		// generate p1,p2,n1,n2
		float[][] p1 = new float[CurveMesh.nVerts][3];
		float[][] p2 = new float[CurveMesh.nVerts][3];
		float[][] n1 = new float[CurveMesh.nVerts][3];
		float[][] n2 = new float[CurveMesh.nVerts][3];

		this.cosines = cosines;
		this.sines = sines;

		genPoints(v1, t1, p1, n1);
		genPoints(v2, t2, p2, n2);

		init(curve, level, pa1, pa2, v1, v2, t1, t2, p1, p2, n1, n2);
	}

	private CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2,
			float[][] p1, float[][] p2, float[][] n1, float[][] n2,
			float[] cosines, float[] sines, CurveSegment parent) {
		super(2, 1, level, false);
		this.cosines = cosines;
		this.sines = sines;
		parents[0] = parent;
		init(curve, level, pa1, pa2, v1, v2, t1, t2, p1, p2, n1, n2);
	}

	private void init(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2,
			float[][] p1, float[][] p2, float[][] n1, float[][] n2) {
		this.curve = curve;

		params[0] = pa1;
		params[2] = pa2;

		vertices[0] = v1;
		vertices[2] = v2;

		tangents[0] = t1;
		tangents[2] = t2;

		points[0] = p1;
		points[1] = p2;

		normals[0] = n1;
		normals[1] = n2;

		length = v1.distance(v2);

		// generate middle point
		params[1] = (pa1 + pa2) * 0.5;
		vertices[1] = curve.evaluateCurve(params[1]);
		tangents[1] = approxTangent(params[1], vertices[1]);

		setBoundingRadii();
		generateError();
	}

	private void setBoundingRadii() {

		minRadSq = maxRadSq = vertices[0].squareNorm();
		boolean isNaN = Double.isNaN(minRadSq);

		// might as well use the information in the midpoint vertex
		double r = vertices[1].squareNorm();
		if (r > maxRadSq)
			maxRadSq = r;
		else if (r < minRadSq)
			minRadSq = r;
		isNaN |= Double.isNaN(r);

		r = vertices[2].squareNorm();
		if (r > maxRadSq)
			maxRadSq = r;
		else if (r < minRadSq)
			minRadSq = r;
		isNaN |= Double.isNaN(r);

		if (isNaN || Double.isInfinite(maxRadSq)) {
			maxRadSq = Double.POSITIVE_INFINITY;
			isSingular = true;

			if (Double.isNaN(minRadSq))
				minRadSq = 0;
		}
	}

	/**
	 * Approximates the tangent by a simple forward difference quotient. Should
	 * only be called in the constructor.
	 */
	private Coords approxTangent(double param, Coords v) {
		Coords d = curve.evaluateCurve(param + CurveMesh.deltaParam);
		return d.sub(v).normalized();
	}

	/**
	 * Generates nVerts points lying on a circle (in a plane perpendicular to
	 * tangent) around vertex.
	 * 
	 * @param vertex
	 * @param tangent
	 * @param pts
	 *            reference to a 2D array with dimension [nVerts][3]
	 * @param nrms
	 *            reference to a 2D array with dimension [nVerts][3]
	 */
	private void genPoints(Coords vertex, Coords tangent, float[][] pts,
			float[][] nrms) {

		Coords[] v = tangent.completeOrthonormal();
		Coords c = vertex;

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			Coords point = c.add(v[0].mul(cosines[j])).add(v[1].mul(sines[j]));
			Coords normal = point.sub(c).normalized();

			pts[j][0] = (float) point.getX();
			pts[j][1] = (float) point.getY();
			pts[j][2] = (float) point.getZ();

			nrms[j][0] = (float) normal.getX();
			nrms[j][1] = (float) normal.getY();
			nrms[j][2] = (float) normal.getZ();
		}
	}

	private void generateError() {
		// Heron's formula:
		double a = vertices[2].distance(vertices[0]);
		double b = vertices[1].distance(vertices[0]);
		double c = vertices[2].distance(vertices[1]);

		// coefficient based on endpoint tangent difference
		double d = 1 - tangents[0].dotproduct(tangents[2]);

		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c)) + d;

		// alternative error measure for singular segments
		if (Double.isNaN(error) || Double.isInfinite(error)) {
			// TODO: investigate whether it would be a good idea to
			// attempt to calculate an error from any non-singular
			// dimensions
			d = params[1] - params[0];
			d /= 2;
			d *= d;
			error = d * 1.5;
		}
	}

	@Override
	protected CullInfo getParentCull() {
		if (parents[0] != null)
			return parents[0].cullInfo;
		return null;
	}

	@Override
	protected void setHidden(DynamicMeshTriList drawList, boolean val) {
		if (val)
			drawList.hide(this);
		else
			drawList.show(this);
	}

	@Override
	protected void reinsertInQueue(DynamicMeshBucketPQ splitQueue,
			DynamicMeshBucketPQ mergeQueue) {
		if (mergeQueue.remove(this))
			mergeQueue.add(this);
		else if (splitQueue.remove(this))
			splitQueue.add(this);
	}

	@Override
	protected void cullChildren(double radSq, DynamicMeshTriList drawList,
			DynamicMeshBucketPQ splitQueue, DynamicMeshBucketPQ mergeQueue) {
		if (!isSplit())
			return;

		if (children[0] != null)
			children[0].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
		if (children[1] != null)
			children[1].updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
	}

	@Override
	protected void createChild(int i) {

		// first generate midpoints and -normals
		float[][] midPoints = new float[CurveMesh.nVerts][3];
		float[][] midNormals = new float[CurveMesh.nVerts][3];

		Coords[] v = tangents[1].completeOrthonormal();
		Coords c = vertices[1];

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			Coords point = c.add(v[0].mul(cosines[j])).add(v[1].mul(sines[j]));
			Coords normal = point.sub(c).normalized();

			midPoints[j][0] = (float) point.getX();
			midPoints[j][1] = (float) point.getY();
			midPoints[j][2] = (float) point.getZ();

			midNormals[j][0] = (float) normal.getX();
			midNormals[j][1] = (float) normal.getY();
			midNormals[j][2] = (float) normal.getZ();
		}

		// generate both children at once
		children[0] = new CurveSegment(curve, level + 1, params[0], params[1],
				vertices[0], vertices[1], tangents[0], tangents[1], points[0],
				midPoints, normals[0], midNormals, cosines, sines, this);
		children[1] = new CurveSegment(curve, level + 1, params[1], params[2],
				vertices[1], vertices[2], tangents[1], tangents[2], midPoints,
				points[1], midNormals, normals[1], cosines, sines, this);
	}

	@Override
	protected double getError() {
		return error;
	}

	/**
	 * sets the scale of the segment
	 * 
	 * @param newScale
	 *            the scale to use
	 */
	public void setScale(float newScale) {
		scale = newScale;
	}

	/**
	 * @return the scale last associated with the segment
	 */
	public float getScale() {
		return scale;
	}
}

/**
 * Triangle list used for curves
 * 
 * @author André Eriksson
 */
class CurveTriList extends DynamicMeshTriList {
	private double totalError = 0;
	private double totalLength = 0;

	private float currScale;

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param marigin
	 *            extra triangle amount
	 * @param scale
	 *            the scale for the segment
	 */
	CurveTriList(int capacity, int marigin, float scale) {
		super(capacity, marigin, (CurveMesh.nVerts + 1) * 2 * 3);
		currScale = scale;
	}

	/**
	 * @return the total error of the visible parts of the curve
	 */
	public double getError() {
		return totalError;
	}

	/**
	 * @return the total length of the visible parts of the curve
	 */
	public double getLength() {
		return totalLength;
	}

	/**
	 * Adds a segment to the curve. If the segment vertices are unspecified,
	 * these are created.
	 * 
	 * @param e
	 *            the segment to add
	 */
	public void add(AbstractDynamicMeshElement e) {
		CurveSegment s = (CurveSegment) e;

		totalError += s.error;

		if (e.isSingular()) {
			// create an empty TriListElem to show that
			// the element has been 'added' to the list

			s.triListElem = new TriListElem();
			s.triListElem.setOwner(s);
			return;
		}

		totalLength += s.length;

		float[] vertices = getVertices(s);
		float[] normals = getNormals(s);

		TriListElem lm = add(vertices, normals);
		lm.setOwner(s);
		s.triListElem = lm;
		return;
	}

	private float[] getVertices(CurveSegment s) {
		float[] v = new float[2 * (CurveMesh.nVerts + 1) * 3];

		Coords v0 = s.vertices[0];
		Coords v1 = s.vertices[2];

		for (int i = 0; i <= CurveMesh.nVerts; i++) {
			int j = i % CurveMesh.nVerts;
			int k = i * 6;

			v[k] = s.points[1][j][0];
			v[k + 1] = s.points[1][j][1];
			v[k + 2] = s.points[1][j][2];
			v[k + 3] = s.points[0][j][0];
			v[k + 4] = s.points[0][j][1];
			v[k + 5] = s.points[0][j][2];

			// scale vertices
			float sf = 1 / currScale;
			v[k] = v[k] * sf + (float) v1.getX() * (1 - sf);
			v[k + 1] = v[k + 1] * sf + (float) v1.getY() * (1 - sf);
			v[k + 2] = v[k + 2] * sf + (float) v1.getZ() * (1 - sf);
			v[k + 3] = v[k + 3] * sf + (float) v0.getX() * (1 - sf);
			v[k + 4] = v[k + 4] * sf + (float) v0.getY() * (1 - sf);
			v[k + 5] = v[k + 5] * sf + (float) v0.getZ() * (1 - sf);

			s.setScale(currScale);
		}

		return v;
	}

	private float[] getNormals(CurveSegment s) {
		float[] normals = new float[2 * (CurveMesh.nVerts + 1) * 3];

		for (int i = 0; i <= CurveMesh.nVerts; i++) {
			int j = i % CurveMesh.nVerts;
			int k = i * 6;

			normals[k] = s.normals[1][j][0];
			normals[k + 1] = s.normals[1][j][1];
			normals[k + 2] = s.normals[1][j][2];
			normals[k + 3] = s.normals[0][j][0];
			normals[k + 4] = s.normals[0][j][1];
			normals[k + 5] = s.normals[0][j][2];
		}

		return normals;
	}

	/**
	 * Removes a segment if it is part of the curve.
	 * 
	 * @param e
	 *            the segment to remove
	 * @return true if the segment was removed, false if it wasn't in the curve
	 *         in the first place
	 */
	public boolean remove(AbstractDynamicMeshElement e) {
		CurveSegment s = (CurveSegment) e;

		boolean ret = hide(s);

		// free triangle
		s.triListElem = null;
		return ret;
	}

	@Override
	public boolean hide(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;

		if (s.isSingular() && s.triListElem != null
				&& s.triListElem.getIndex() != -1) {
			totalError -= s.error;
			return true;
		} else if (hide(s.triListElem)) {
			totalError -= s.error;
			totalLength -= s.length;
			return true;
		}

		return false;
	}

	@Override
	public boolean show(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;

		if (s.isSingular() && s.triListElem != null
				&& s.triListElem.getIndex() == -1) {
			totalError += s.error;
			return true;
		} else if (show(s.triListElem)) {
			totalError += s.error;
			totalLength += s.length;
			return true;
		}

		return false;
	}

	/**
	 * rescales all visible elements to the given scale
	 * 
	 * @param newScale
	 */
	public void rescale(float newScale) {
		TriListElem t = front;
		while (t != null) {

			float[] v = getVertices(t);

			CurveSegment owner = (CurveSegment) t.getOwner();

			Coords v0 = owner.vertices[0];
			Coords v1 = owner.vertices[2];

			float sf = owner.getScale() / (newScale); // scale factor

			for (int i = 0; i <= CurveMesh.nVerts; i++) {
				int k = i * 6;

				v[k] = v[k] * sf + (float) v1.getX() * (1 - sf);
				v[k + 1] = v[k + 1] * sf + (float) v1.getY() * (1 - sf);
				v[k + 2] = v[k + 2] * sf + (float) v1.getZ() * (1 - sf);
				v[k + 3] = v[k + 3] * sf + (float) v0.getX() * (1 - sf);
				v[k + 4] = v[k + 4] * sf + (float) v0.getY() * (1 - sf);
				v[k + 5] = v[k + 5] * sf + (float) v0.getZ() * (1 - sf);
			}

			setVertices(t, v);
			t = t.getNext();
			owner.setScale(newScale);
		}
		currScale = newScale;
	}

	@Override
	public void add(AbstractDynamicMeshElement e, int i) {
		add(e);
	}

	@Override
	public boolean remove(AbstractDynamicMeshElement e, int i) {
		return remove(e);
	}
}

// TODO: only use one bucket assigner

/**
 * A bucket assigner used for split operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class CurveSplitBucketAssigner implements
		BucketAssigner<AbstractDynamicMeshElement> {

	public int getBucketIndex(Object o, int bucketAmt) {
		CurveSegment d = (CurveSegment) o;
		double e = d.error;
		int bucket = (int) (Math.pow(e / 100, 0.3) * bucketAmt);
		if (bucket >= bucketAmt)
			bucket = bucketAmt - 1;
		if (bucket <= 0)
			bucket = 1;
		return bucket;
	}
}

/**
 * A bucket assigner used for merge operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class CurveMergeBucketAssigner implements
		BucketAssigner<AbstractDynamicMeshElement> {

	public int getBucketIndex(Object o, int bucketAmt) {
		CurveSegment d = (CurveSegment) o;
		double e = d.error;
		int bucket = (int) ((Math.pow(1e5 * e, -0.1) - 0.16) * bucketAmt);
		if (bucket >= bucketAmt)
			bucket = bucketAmt - 1;
		if (bucket <= 0)
			bucket = 1;
		return bucket;
	}
}

/**
 * @author Andr� Eriksson Tree representing a parametric curve
 */
public class CurveMesh extends AbstractDynamicMesh {

	private static final int maxLevel = 20;

	/** the parameter difference used to approximate tangents */
	public static double deltaParam = 1e-3;

	private static final float scalingFactor = .8f;

	/** the amount of vertices at the end of each segment */
	static public final int nVerts = 8;

	/** relative radius of the segments */
	static public final float radiusFac = 0.1f;

	private CurveSegment root;

	private GeoCurveCartesian3D curve;

	/**
	 * @param curve
	 * @param rad
	 * @param scale
	 */
	public CurveMesh(GeoCurveCartesian3D curve, double rad, float scale) {
		super(new DynamicMeshBucketPQ(new CurveMergeBucketAssigner()),
				new DynamicMeshBucketPQ(new CurveSplitBucketAssigner()),
				new CurveTriList(100, 0, scale * scalingFactor), 1, 2, maxLevel);

		setRadius(rad);

		this.curve = curve;

		initCurve();
	}

	/**
	 * generates the first few segments
	 */
	private void initCurve() {
		float[] cosines = new float[nVerts];
		float[] sines = new float[nVerts];
		double fac = 2 * Math.PI / nVerts;
		for (int i = 0; i < nVerts; i++) {
			cosines[i] = (float) Math.cos(i * fac);
			sines[i] = (float) Math.sin(i * fac);
		}
		root = new CurveSegment(curve, 0, curve.getMinParameter(), curve
				.getMaxParameter(), cosines, sines);

		root.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);

		splitQueue.add(root);
		drawList.add(root);

		// split the first few elements in order to avoid problems
		// with periodic funtions
		for (int i = 0; i < 30; i++)
			split(splitQueue.poll());

	}

	protected void updateCullingInfo() {
		root.updateCullInfo(radSq, drawList, splitQueue, mergeQueue);
	}

	@Override
	protected Side tooCoarse() {
		CurveTriList d = (CurveTriList) drawList;

		double error = d.getError();
		double errorGoal = 0.001 * d.getLength();
		
		//avoid problems with straigt lines etc
		if(Math.abs(error)<1e-10)
			return Side.NONE;
		
		if (error < errorGoal)
			return Side.MERGE;
		
		return Side.SPLIT;
	}

	@Override
	protected String getDebugInfo(long time) {
		return curve + ":\tupdate time: " + time + "ms\ttriangles: "
				+ drawList.getTriAmt() + "\terror: "
				+ (float) ((CurveTriList) drawList).getError() + "\tlength: "
				+ (float) ((CurveTriList) drawList).getLength();
	}

	/**
	 * @return the amount of visible segments
	 */
	public int getVisibleChunks() {
		return drawList.getChunkAmt();
	}

	/**
	 * @return the amount of vertices per segment
	 */
	public int getVerticesPerChunk() {
		return 2 * (nVerts + 1);
	}

	/**
	 * rescales the mesh
	 * 
	 * @param scale
	 *            the desired scale
	 */
	public void updateScale(float scale) {
		((CurveTriList) drawList).rescale(scale * scalingFactor);
	}
}
