package geogebra3D.euclidian3D.plots;

import geogebra.Matrix.GgbVector;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.TriListElem;
import geogebra3D.euclidian3D.plots.SurfaceMesh.CullInfo;
import geogebra3D.kernel3D.GeoCurveCartesian3D;

/**
 * An element in a CurveMesh.
 * 
 * @author André Eriksson
 */
class CurveSegment extends AbstractDynamicMeshElement {

	/** points evaluated on a circle - used for generating points[] */
	public final float[] sines, cosines;

	/** a reference to the curve being drawn */
	private GeoCurveCartesian3D curve;

	/** error value associated with the segment */
	double error;

	/** parameter values at the start and end of the segment */
	double[] params = new double[3];

	/** positions at the start/end of the sement */
	GgbVector[] vertices = new GgbVector[3];
	
	/** squared distances from the origin of the two endpoints - used for culling */
	double[] distances = new double[2];

	/** tangents at start and end positions */
	public GgbVector[] tangents = new GgbVector[3];
	
	/** triangle list element */
	public TriListElem triListElem;
	
	/**
	 * the vertices of the segment addressed in the order [start/end][vertex
	 * num][x/y/z]
	 */
	float[][][] points = new float[2][][];
	/** normals for the vertices */
	float[][][] normals = new float[2][][];

	/** culling status of the element */
	public CullInfo cullInfo;
	
	/**
	 * @param curve
	 * @param level
	 * @param pa1 parameter value at first endpoint
	 * @param pa2 parameter value at second endpoint
	 * @param cosines 
	 * @param sines
	 */
	public CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, float[] cosines, float[] sines) {
		super(CurveMesh.nChildren, CurveMesh.nParents, level);
		
		this.curve=curve;
		
		GgbVector v1 = curve.evaluateCurve(pa1);
		GgbVector v2 = curve.evaluateCurve(pa2);
		GgbVector t1 = approxTangent(pa1,v1);
		GgbVector t2 = approxTangent(pa2,v2);

		//generate p1,p2,n1,n2
		float[][] p1 = new float[CurveMesh.nVerts][3];
		float[][] p2 = new float[CurveMesh.nVerts][3];
		float[][] n1 = new float[CurveMesh.nVerts][3];
		float[][] n2 = new float[CurveMesh.nVerts][3];

		this.cosines=cosines;
		this.sines=sines;

		genPoints(v1,t1,p1,n1);
		genPoints(v2,t2,p2,n2);
		
		init(curve, level, pa1, pa1, v1, v2, t1, t2, p1, p2, n1, n2);
	}

	private CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, GgbVector v1, GgbVector v2, GgbVector t1, GgbVector t2,
			float[][] p1, float[][] p2, float[][] n1, float[][] n2,
			float[] cosines, float[] sines) {
		super(CurveMesh.nChildren, CurveMesh.nParents, level);
		this.cosines=cosines;
		this.sines=sines;
		init(curve, level, pa1, pa1, v1, v2, t1, t2, p1, p2, n1, n2);
	}
	
	private void init(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, GgbVector v1, GgbVector v2, GgbVector t1, GgbVector t2,
			float[][] p1, float[][] p2, float[][] n1, float[][] n2){
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

		// generate middle point
		params[1] = (pa1 + pa2) * 0.5;
		vertices[1] = curve.evaluateCurve(params[1]);
		tangents[1] = approxTangent(params[1],vertices[1]);

		generateError();
	}
	

	/** 
	 * Approximates the tangent by a simple forward difference quotient.
	 * Should only be called in the constructor.
	 */
	private GgbVector approxTangent(double param, GgbVector v){
		GgbVector d = curve.evaluateCurve(param+CurveMesh.deltaParam);
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
	 * 			  reference to a 2D array with dimension [nVerts][3]	
	 */
	private void genPoints(GgbVector vertex, GgbVector tangent, float[][] pts,
			float[][] nrms) {

		GgbVector[] v = tangent.completeOrthonormal();
		GgbVector c = vertex;

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			GgbVector point = c.add(v[0].mul(cosines[j])).add(
					v[1].mul(sines[j]));
			GgbVector normal = point.sub(c).normalized();

			pts[j][0] = (float) point.getX();
			pts[j][1] = (float) point.getY();
			pts[j][2] = (float) point.getZ();

			nrms[j][0] = (float) normal.getX();
			nrms[j][1] = (float) normal.getY();
			nrms[j][2] = (float) normal.getZ();
		}
	}

	private void generateError() {
		// TODO: work out a more refined error measure
		// for now, use the area of the triangle defined by vertices[]

		// Heron's formula:
		double a = vertices[2].distance(vertices[0]);
		double b = vertices[1].distance(vertices[0]);
		double c = vertices[2].distance(vertices[1]);

		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c));
	}

	@Override
	public void updateCullInfo(double radSq, DynamicMeshTriList drawList) {
		boolean a = distances[0]<radSq;
		boolean b = distances[1]<radSq;
		CullInfo prev = cullInfo;
		cullInfo = a?(b?CullInfo.ALLIN:CullInfo.SOMEIN):(b?CullInfo.SOMEIN:CullInfo.OUT);
		if(cullInfo==CullInfo.SOMEIN || cullInfo!=prev){
			if(children[0]!=null)
				children[0].updateCullInfo(radSq, drawList);
			if(children[1]!=null)
				children[1].updateCullInfo(radSq, drawList);
		}
		if(cullInfo!=prev){
			if(cullInfo==CullInfo.OUT)
				drawList.hide(this);
			else if(prev==CullInfo.OUT)
				drawList.show(this);
		}
	}

	@Override
	protected void createChild(int i) {

		// first generate midpoints and -normals
		float[][] midPoints = new float[CurveMesh.nVerts][3];
		float[][] midNormals = new float[CurveMesh.nVerts][3];

		GgbVector[] v = tangents[1].completeOrthonormal();
		GgbVector c = vertices[1];

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			GgbVector point = c.add(v[0].mul(cosines[j])).add(
					v[1].mul(sines[j]));
			GgbVector normal = point.sub(c).normalized();

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
				midPoints, normals[0], midNormals, cosines, sines);
		children[1] = new CurveSegment(curve, level + 1, params[1], params[2],
				vertices[1], vertices[2], tangents[1], tangents[2], midPoints,
				points[1], midNormals, normals[1], cosines, sines);
	}
}

/**
 * Triangle list used for curves
 * 
 * @author André Eriksson
 */
class CurveTriList extends DynamicMeshTriList {
	private double totalError = 0;

	/**
	 * @param capacity
	 *            the goal amount of triangles available
	 * @param marigin
	 *            extra triangle amount
	 */
	CurveTriList(int capacity, int marigin) {
		super(capacity, marigin, CurveMesh.nVerts * 2);
	}

	/**
	 * @return the total error of the curve
	 */
	public double getError() {
		return totalError;
	}

	/**
	 * Adds a segment to the curve. If the segment vertices are unspecified,
	 * these are created.
	 * 
	 * @param s
	 *            the segment to add
	 * @return the triangle list element created
	 */
	public TriListElem add(AbstractDynamicMeshElement e) {
		CurveSegment s = (CurveSegment) e;

		totalError+=s.error;
		
		float[] vertices = new float[2*CurveMesh.nVerts*9];
		float[] normals = new float[2*CurveMesh.nVerts*9];
		// create our polygons
		for (int i = 0; i < CurveMesh.nVerts; i++) {
			int j = (i + 1) % CurveMesh.nVerts;
			int k = (2*i)*9;
			int l = (2*i+1)*9;

			// triangle 1
			vertices[k]   = s.points[0][i][0];
			vertices[k+1] = s.points[0][i][1];
			vertices[k+2] = s.points[0][i][2];
			vertices[k+3] = s.points[0][j][0];
			vertices[k+4] = s.points[0][j][1];
			vertices[k+5] = s.points[0][j][2];
			vertices[k+6] = s.points[1][i][0];
			vertices[k+7] = s.points[1][i][1];
			vertices[k+8] = s.points[1][i][2];

			normals[k]   = s.normals[0][i][0];
			normals[k+1] = s.normals[0][i][1];
			normals[k+2] = s.normals[0][i][2];
			normals[k+3] = s.normals[0][j][0];
			normals[k+4] = s.normals[0][j][1];
			normals[k+5] = s.normals[0][j][2];
			normals[k+6] = s.normals[1][i][0];
			normals[k+7] = s.normals[1][i][1];
			normals[k+8] = s.normals[1][i][2];

			// triangle 2
			vertices[l]   = s.points[0][j][0];
			vertices[l+1] = s.points[0][j][1];
			vertices[l+2] = s.points[0][j][2];
			vertices[l+3] = s.points[1][j][0];
			vertices[l+4] = s.points[1][j][1];
			vertices[l+5] = s.points[1][j][2];
			vertices[l+6] = s.points[1][i][0];
			vertices[l+7] = s.points[1][i][1];
			vertices[l+8] = s.points[1][i][2];

			normals[l]   = s.normals[0][j][0];
			normals[l+1] = s.normals[0][j][1];
			normals[l+2] = s.normals[0][j][2];
			normals[l+3] = s.normals[1][j][0];
			normals[l+4] = s.normals[1][j][1];
			normals[l+5] = s.normals[1][j][2];
			normals[l+6] = s.normals[1][i][0];
			normals[l+7] = s.normals[1][i][1];
			normals[l+8] = s.normals[1][i][2];
		}

		TriListElem lm = add(vertices, normals);
		s.triListElem = lm;
		return lm;
	}

	/**
	 * Removes a segment if it is part of the curve.
	 * 
	 * @param s
	 *            the segment to remove
	 * @return true if the segment was removed, false if it wasn't in the curve
	 *         in the first place
	 */
	public boolean remove(AbstractDynamicMeshElement e) {
		CurveSegment s = (CurveSegment) e;
		
		// free triangle
		s.triListElem=null;

		return hide(s);
	}

	@Override
	public boolean hide(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;
		
		if (hide(s.triListElem))
			totalError -= s.error;

		return false;
	}

	@Override
	public boolean show(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;
		
		if (hide(s.triListElem))
			totalError += s.error;
		
		return false;
	}
}

/**
 * A bucket assigner used for split operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class SplitBucketAssigner implements BucketAssigner<AbstractDynamicMeshElement> {

	public int getBucketIndex(Object o, int bucketAmt) {
		CurveSegment d = (CurveSegment) o;
		double e = d.error;

		// TODO: assign a bucket value based on error
		return 0;
	}
}

/**
 * A bucket assigner used for merge operations. Sorts based on
 * SurfaceMeshDiamond.error.
 * 
 * @author André Eriksson
 */
class MergeBucketAssigner implements BucketAssigner<AbstractDynamicMeshElement> {

	public int getBucketIndex(Object o, int bucketAmt) {
		CurveSegment d = (CurveSegment) o;
		double e = d.error;

		// TODO: assign a bucket value based on error
		return 0;
	}
}

/**
 * @author Andr� Eriksson Tree representing a parametric curve
 */
public class CurveMesh extends AbstractDynamicMesh {

	private static final int maxSegments = 3000;
	public static final int nParents = 1;
	public static final int nChildren = 2;
	private static final int maxLevel = 20;
	public static double deltaParam = 1e-3;

	/** the amount of vertices at the end of each segment */
	static public final int nVerts = 4;

	/** radius of the segments */
	static public final float radius = 0.1f;

	private CurveSegment root;
	
	private GeoCurveCartesian3D curve;

	/**
	 * @param curve
	 * @param radSq
	 */
	public CurveMesh(GeoCurveCartesian3D curve, double rad) {
		super(new DynamicMeshBucketPQ(new MergeBucketAssigner()), new DynamicMeshBucketPQ(
				new SplitBucketAssigner()), new CurveTriList(maxSegments, 20),
				nParents, nChildren, maxLevel);

		setRadius(rad);
		
		this.curve=curve;
		
		generateRoot();
	}

	/**
	 * generates the first segment
	 */
	private void generateRoot() {
		float[] cosines = new float[nVerts];
		float[] sines = new float[nVerts];
		double fac = 2 * Math.PI / nVerts;
		for (int i = 0; i < nVerts; i++) {
			cosines[i] = radius * (float) Math.cos(i * fac);
			sines[i] = radius * (float) Math.sin(i * fac);
		}
		root = new CurveSegment(curve, 0, curve.getMinParameter(),
				curve.getMaxParameter(), cosines, sines);
		
		splitQueue.add(root);
		drawList.add(root);
	}

	protected void updateCullingInfo() {
		root.updateCullInfo(radSq, drawList);
	}

	// TODO: fix
	@Override
	protected boolean tooCoarse() {
		return true;
	}

	@Override
	protected String getDebugInfo(long time) {
		return curve + ":\tupdate time: " + time + "ms\ttriangles: "
				+ drawList.getCount() + "\terror: "
				+ (float) ((CurveTriList) drawList).getError() + "\tgoal:";
	}
}
