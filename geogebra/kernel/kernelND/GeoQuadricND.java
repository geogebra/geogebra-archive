package geogebra.kernel.kernelND;

import java.awt.geom.AffineTransform;

import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoQuadric3D;


/** Abstract class describing quadrics in n-dimension space.
 * Extended by GeoConic, GeoQuadric3D
 * @author matthieu
 *
 */
public abstract class GeoQuadricND extends GeoElement {
	
	
	private int dimension;	
	private int matrixDim;
	
	// types    
	public static final int QUADRIC_SINGLE_POINT = 1;
	public static final int QUADRIC_INTERSECTING_LINES = 2;
	public static final int QUADRIC_ELLIPSOID = 3;
	public static final int QUADRIC_SPHERE = 4;
	public static final int QUADRIC_HYPERBOLOID = 5;
	public static final int QUADRIC_EMPTY = 6;
	public static final int QUADRIC_DOUBLE_LINE = 7;
	public static final int QUADRIC_PARALLEL_LINES = 8;
	public static final int QUADRIC_PARABOLOID = 9;
	public static final int QUADRIC_LINE = 10;
	
	public static final int QUADRIC_CONE = 30;
	public static final int QUADRIC_CYLINDER = 31;
	
	public int type = -1; // of quadric



	/**  flat matrix 
	 * @see {@link GeoConic}
	 * @see {@link GeoQuadric3D}
	 */
	public double[] matrix;
	
	
	public double[] halfAxes;
	

	
	
	
	
	public double linearEccentricity, eccentricity, p;
	
	
	

	protected boolean defined = true;
	
	
	protected Coords midpoint;
	//TODO merge with 2D eigenvec
	protected Coords[] eigenvecND;


	protected double[] diagonal;
	
	
	//string
	protected static final char[] VAR_STRING = {'x','y','z'};
	
	
	/** default constructor
	 * @param c
	 * @param dimension dimension of the space (2D or 3D)
	 */
	public GeoQuadricND(Construction c, int dimension) {
		super(c);
		this.dimension = dimension;
		matrixDim = (dimension+1)*(dimension+2)/2;
		matrix = new double[matrixDim];
		halfAxes = new double[dimension];
		midpoint = new Coords(dimension+1);
		midpoint.set(dimension+1, 1);
	}
	
	
	
	/////////////////////////////////
	// MATRIX REPRESENTATION
	/////////////////////////////////
	
	/**
	 * @return the matrix representation of the quadric in its dimension
	 * regarding vals
	 */
	abstract protected CoordMatrix getSymetricMatrix(double[] vals);
		
	/**
	 * @return the matrix representation of the quadric in its dimension
	 */
	public CoordMatrix getSymetricMatrix(){
		return getSymetricMatrix(matrix);
	}
	
	
	/**
	 * sets the matrix values from eigenvectors, midpoint and "diagonal" values
	 */
	protected void setMatrixFromEigen(){
		
		CoordMatrix diagonalizedMatrix = CoordMatrix.DiagonalMatrix(diagonal);
		
		CoordMatrix eigenMatrix = new CoordMatrix(4, 4);
		eigenMatrix.set(eigenvecND);
		eigenMatrix.set(getMidpoint(),4);
		
		CoordMatrix eigenMatrixInv = eigenMatrix.inverse();
		
		CoordMatrix finalMatrix = eigenMatrixInv.transposeCopy().mul(diagonalizedMatrix).mul(eigenMatrixInv);
		
		setMatrix(finalMatrix);
	}

	/**
	 * sets the matrix values from the symmetric matrix m
	 * @param m
	 */
	abstract protected void setMatrix(CoordMatrix m);
	
	/////////////////////////////////
	// SPECIAL CASES SETTERS
	/////////////////////////////////

	
	/** set the center and radius (as segment) of the N-sphere
	 * @param M center
	 * @param segment
	 */
	abstract public void setSphereND(GeoPointND M, GeoSegmentND segment);
	
	
	
	/**
	 * makes this quadric a sphere with midpoint M and radius r
	 */
	public void setSphereND(GeoPointND M, double r) {
		defined = ((GeoElement) M).isDefined() && !M.isInfinite(); // check midpoint
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setSphereNDMatrix(M, r);
			setAffineTransform();
		} 		
	}
	
	
	abstract public void setSphereND(GeoPointND M, GeoPointND P);
	
	
	protected void setSphereNDMatrix(GeoPointND M, double r){
				
		
		double[] coords = M.getInhomCoordsInD(3).get();
		
		// set midpoint
		setMidpoint(coords);

		// set halfAxes = radius	
		for (int i=0;i<dimension;i++)
			halfAxes[i] = r;
		
		// set quadric's matrix with M(mx, my, mz) and r
		//  [   1   0       -m       ]
		//  [   0   1       -n       ]
		//  [  -m  -n       m\u00b2+n\u00b2-r\u00b2 ]  
		
		for (int i=0;i<dimension;i++)
			matrix[i] = 1.0d;

		matrix[dimension] = - r * r;
		for (int i=0;i<dimension;i++)
			matrix[dimension]+=coords[i]*coords[i];
		
		for (int i=dimension+1;i<matrixDim-dimension;i++)
			matrix[i] = 0.0;
		
		for (int i=matrixDim-dimension;i<matrixDim;i++)
			matrix[i] = -coords[i-(matrixDim-dimension)];
		

		if (r > kernel.getEpsilon()) { // radius not zero 
			if (type != QUADRIC_SPHERE) {
				type = QUADRIC_SPHERE;
				linearEccentricity = 0.0d;
				eccentricity = 0.0d;
				// set first eigenvector and eigenvectors
				setFirstEigenvector(new double[] {1,0});
				setEigenvectors();
			}
		} else if (kernel.isZero(r)) { // radius == 0
			singlePoint();			
		} else { // radius < 0 or radius = infinite
			empty();
		}
		
		
	}
	
	
	
	/**
	 * turn type of quadric to empty
	 */
	final protected void empty() {
		type = QUADRIC_EMPTY;
	}
	
	
	

	
	public void setUndefined() {
		defined = false;
		//type = GeoConic.CONIC_EMPTY;
		empty();
	}

	final public void setDefined() {
		defined = true;
	}
	
	
	
	
	protected void setMidpoint(double[] coords){
		
		midpoint.set(coords);
		
	}

	public Coords getMidpoint2D(){
		return midpoint;
	}
	
	public Coords getMidpoint(){
		return getMidpoint2D();
	}
	

	public Coords getMidpoint3D(){
		Coords ret = new Coords(4);
		for (int i=1; i<midpoint.getLength();i++)
			ret.set(i, midpoint.get(i));
		ret.setW(midpoint.getLast());
		return ret;
	}

	
	public double getHalfAxis(int i){
		return halfAxes[i];
	}
	
	
	
	
	public boolean isDefined() {
		return defined;
	}
	
	
	final public int getType() {
		return type;
	}
	
	final public void setType(int type) {
		this.type = type;
	}
	
	//////////////////////////////////////:
	// STRING
	//////////////////////////////////////:
	
	
	
	protected StringBuilder sbToValueString;
	protected StringBuilder sbToValueString() {
		if (sbToValueString == null)
			sbToValueString = new StringBuilder();
		return sbToValueString;
	}
	
	
	/**
	 * returns equation of conic.
	 * in implicit mode: a x\u00b2 + b xy + c y\u00b2 + d x + e y + f = 0. 
	 * in specific mode: y\u00b2 = ...  , (x - m)\u00b2 + (y - n)\u00b2 = r\u00b2, ...
	 */
	public String toString() {	
		StringBuilder sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");
		sbToString.append(buildValueString()); 
		return sbToString.toString();
	}
		
	private StringBuilder sbToString;
	protected StringBuilder getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuilder(80);
		return sbToString;
	}
	
	public String toValueString() {
		return buildValueString().toString();	
	}	
	
	abstract protected StringBuilder buildValueString();
	
	
	
	
	protected void buildSphereNDString(){
		String squared = (kernel.getCASPrintForm() == ExpressionNode.STRING_TYPE_LATEX) ? "^{2}" : "\u00b2";
				
		for (int i=0; i<dimension; i++){
			if (kernel.isZero(getMidpoint().get(i+1))) {
				sbToValueString.append(VAR_STRING[i]);
				sbToValueString.append(squared);
			} else {
				sbToValueString.append("(");
				sbToValueString.append(VAR_STRING[i]);
				sbToValueString.append(" ");
				sbToValueString.append(kernel.formatSigned(-getMidpoint().get(i+1)));
				sbToValueString.append(")");
				sbToValueString.append(squared);
			}	
			if (i<dimension-1)
				sbToValueString.append(" + ");
			else
				sbToValueString.append(" = ");
		}

		sbToValueString.append(kernel.format(halfAxes[0] * halfAxes[0]));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO turn methods below to abstract, implement it in GeoQuadric3D
	protected void setFirstEigenvector(double[] coords){
	}
	
	protected void setEigenvectors(){
		
	}
	
	
	
	
	//TODO implements methods below from GeoConic
	protected void singlePoint() {
		/*
		type = GeoConic.CONIC_SINGLE_POINT;

		if (singlePoint == null)
			singlePoint = new GeoPoint(cons);
		singlePoint.setCoords(b.x, b.y, 1.0d);
		//Application.debug("singlePoint : " + b);
		 */
		 
	}
	
	protected void setAffineTransform() {
		//AffineTransform transform = getAffineTransform();	
		
		/*      ( v1x   v2x     bx )
		 *      ( v1y   v2y     by )
		 *      (  0     0      1  )   */		
		/*
		transform.setTransform(
			eigenvec[0].x,
			eigenvec[0].y,
			eigenvec[1].x,
			eigenvec[1].y,
			b.x,
			b.y);
			*/
	}

}
