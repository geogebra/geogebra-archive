package geogebra3D.euclidian3D.plots;

import geogebra.Matrix.Coords;
import geogebra3D.euclidian3D.BucketAssigner;
import geogebra3D.euclidian3D.TriListElem;
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
	
	/** length of the segment */
	double length;

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
	 * @param pa1 parameter value at first endpoint
	 * @param pa2 parameter value at second endpoint
	 * @param cosines 
	 * @param sines
	 */
	public CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, float[] cosines, float[] sines) {
		super(CurveMesh.nChildren, CurveMesh.nParents, level);
		
		this.curve=curve;
		
		Coords v1 = curve.evaluateCurve(pa1);
		Coords v2 = curve.evaluateCurve(pa2);
		Coords t1 = approxTangent(pa1,v1);
		Coords t2 = approxTangent(pa2,v2);

		//generate p1,p2,n1,n2
		float[][] p1 = new float[CurveMesh.nVerts][3];
		float[][] p2 = new float[CurveMesh.nVerts][3];
		float[][] n1 = new float[CurveMesh.nVerts][3];
		float[][] n2 = new float[CurveMesh.nVerts][3];

		this.cosines=cosines;
		this.sines=sines;

		genPoints(v1,t1,p1,n1);
		genPoints(v2,t2,p2,n2);
		
		init(curve, level, pa1, pa2, v1, v2, t1, t2, p1, p2, n1, n2);
	}

	private CurveSegment(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2,
			float[][] p1, float[][] p2, float[][] n1, float[][] n2,
			float[] cosines, float[] sines, CurveSegment parent) {
		super(CurveMesh.nChildren, CurveMesh.nParents, level);
		this.cosines=cosines;
		this.sines=sines;
		parents[0]=parent;
		init(curve, level, pa1, pa2, v1, v2, t1, t2, p1, p2, n1, n2);
	}
	
	private void init(GeoCurveCartesian3D curve, int level, double pa1,
			double pa2, Coords v1, Coords v2, Coords t1, Coords t2,
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
		
		length = v1.distance(v2);
		
		// generate middle point
		params[1] = (pa1 + pa2) * 0.5;
		vertices[1] = curve.evaluateCurve(params[1]);
		tangents[1] = approxTangent(params[1],vertices[1]);

		setBoundingRadii();
		generateError();
	}
	
	private void setBoundingRadii(){
		minRadSq = maxRadSq = vertices[0].squareNorm();
		boolean isNaN = Double.isNaN(minRadSq);
		
		//might as well use the information in the midpoint vertex
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
		
		if (isNaN || Double.isInfinite(maxRadSq)){
			maxRadSq = Double.POSITIVE_INFINITY;
			minRadSq = 0;
			isSingular=true;
		}
	}
	

	/** 
	 * Approximates the tangent by a simple forward difference quotient.
	 * Should only be called in the constructor.
	 */
	private Coords approxTangent(double param, Coords v){
		Coords d = curve.evaluateCurve(param+CurveMesh.deltaParam);
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
	private void genPoints(Coords vertex, Coords tangent, float[][] pts,
			float[][] nrms) {

		Coords[] v = tangent.completeOrthonormal();
		Coords c = vertex;

		// create midpoints
		for (int j = 0; j < CurveMesh.nVerts; j++) {
			Coords point = c.add(v[0].mul(cosines[j])).add(
					v[1].mul(sines[j]));
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
		
		//coefficient based on endpoint tangent difference
		double d = 1-tangents[0].dotproduct(tangents[2]);
		
		double s = 0.5 * (a + b + c);
		error = Math.sqrt(s * (s - a) * (s - b) * (s - c))+d;
	}

	@Override
	protected CullInfo getParentCull(){
		if(parents[0]!=null)
			return parents[0].cullInfo;
		return null;
	}
	
	@Override
	protected void setHidden(DynamicMeshTriList drawList, boolean val) {
		if(val)
			drawList.hide(this);
		else
			drawList.show(this);
	}

	@Override
	protected void reinsertInQueue(){
		//TODO: implement this method
	}

	@Override
	protected void cullChildren(double radSq,DynamicMeshTriList drawList){
		if(!isSplit())
			return;
		
		if(children[0]!=null)
			children[0].updateCullInfo(radSq, drawList);
		if(children[1]!=null)
			children[1].updateCullInfo(radSq, drawList);
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
			Coords point = c.add(v[0].mul(cosines[j])).add(
					v[1].mul(sines[j]));
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
}

/**
 * Triangle list used for curves
 * 
 * @author André Eriksson
 */
class CurveTriList extends DynamicMeshTriList {
	private double totalError = 0;
	private double totalLength = 0;
	
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
	 * @param s
	 *            the segment to add
	 * @return the triangle list element created
	 */
	public TriListElem add(AbstractDynamicMeshElement e) {
		if(e.isSingular())
			return null;

		CurveSegment s = (CurveSegment) e;

		totalError+=s.error;
		totalLength+=s.length;
		
		
		float[] vertices = new float[2*CurveMesh.nVerts*9];
		float[] normals = new float[2*CurveMesh.nVerts*9];
		// create our polygons
		for (int i = 0; i < CurveMesh.nVerts; i++) {
			int j = (i + 1) % CurveMesh.nVerts;
			int k = (2*i)*9;
			int l = (2*i+1)*9;
			
			//TODO: convert to triangle strips
			
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
		
		boolean ret = hide(s);

		// free triangle
		s.triListElem=null;
		return ret;
	}

	@Override
	public boolean hide(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;
		
		if (hide(s.triListElem)){
			totalError -= s.error;
			totalLength -= s.length;
			return true;
		}

		return false;
	}

	@Override
	public boolean show(AbstractDynamicMeshElement t) {
		CurveSegment s = (CurveSegment) t;
		
		if (show(s.triListElem)){
			totalError += s.error;
			totalLength += s.length;
			return true;
		}
		
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
		return (int)(Math.log(Math.E+e)+.5);
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
		CurveTriList d = (CurveTriList)drawList;

		double error = d.getError();
		double errorGoal = 0.1;
		int count = drawList.getCount();
		if (drawList.isFull())
			return false;
		if (error < errorGoal)
			return false;
		return true;
	}

	@Override
	protected String getDebugInfo(long time) {
		return curve + ":\tupdate time: " + time + "ms\ttriangles: "
				+ drawList.getCount() + "\terror: "
				+ (float) ((CurveTriList) drawList).getError() + "\tgoal:";
	}
}
