/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoConic.java
 *
 * Created on 10. September 2001, 08:52
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.MyMath;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 *
 * @author  Markus
 * @version 
 */
public class GeoConic extends GeoElement
implements Path, Traceable, 
Translateable, PointRotateable, Mirrorable, Dilateable, LineProperties  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// avoid very large and small coefficients for numerical stability	
	private static final double MAX_COEFFICIENT_SIZE = 10000;
	private static final double MIN_COEFFICIENT_SIZE = 1;

	// modes
	public static final int EQUATION_IMPLICIT = 0;
	public static final int EQUATION_EXPLICIT = 1; // for y = ...
	public static final int EQUATION_SPECIFIC = 2;

	private static String[] vars = { "x\u00b2", "x y", "y\u00b2", "x", "y" };
	
	// enable negative sign of first coefficient in implicit equations
	private static boolean KEEP_LEADING_SIGN = false;

	// types    
	public static final int CONIC_SINGLE_POINT = 1;
	public static final int CONIC_INTERSECTING_LINES = 2;
	public static final int CONIC_ELLIPSE = 3;
	public static final int CONIC_CIRCLE = 4;
	public static final int CONIC_HYPERBOLA = 5;
	public static final int CONIC_EMPTY = 6;
	public static final int CONIC_DOUBLE_LINE = 7;
	public static final int CONIC_PARALLEL_LINES = 8;
	public static final int CONIC_PARABOLA = 9;

	/* 
	 *               ( A[0]  A[3]    A[4] )
	 *      matrix = ( A[3]  A[1]    A[5] )
	 *               ( A[4]  A[5]    A[2] )
	 */

	int type; // of conic
	double[] matrix = new double[6]; // flat matrix A
	private double maxCoeffAbs; // maximum absolute value of coeffs in matrix A[]
	private AffineTransform transform;
	public boolean trace;	

	// (eigenvecX, eigenvecY) are coords of currently calculated first eigenvector
	// (eigenvecX, eigenvecY) is not a unit vector
	double eigenvecX, eigenvecY;
	// two Eigenvectors (unit vectors), set by setEigenvectors()
	GeoVec2D[] eigenvec = { new GeoVec2D(kernel, 1, 0), new GeoVec2D(kernel, 0, 1)};
	// translation vector (midpoint, vertex)    
	GeoVec2D b = new GeoVec2D(kernel);
	double[] halfAxes = new double[2];
	public double excent, p;
	GeoLine[] lines;
	private GeoPoint singlePoint;
	private boolean defined = true;
	private ArrayList pointsOnConic;
	
	private EquationSolver eqnSolver;
	
	// for classification
	transient private double detS, length, temp, temp1, temp2, nx, ny, lambda;
	private int index = 0;
	private double[] eigenval = new double[3];
	private double[] mu = new double[2];
	private GeoVec2D c = new GeoVec2D(kernel);		

	public GeoConic(Construction c) {
		super(c);
		eqnSolver = c.getEquationSolver();
		toStringMode = EQUATION_IMPLICIT;
	}		

	/** Creates new GeoConic */
	public GeoConic(Construction c, String label, double[] coeffs) {
		this(c);
		setCoeffs(coeffs);
		setLabel(label);
	}
	
	/** Creates new GeoConic with Coordinate System for 3D */
	public GeoConic(Construction c, String label, double[] coeffs, GeoElement cs) {
		this(c,label,coeffs);
		setCoordSys(cs);
	}	
	
	
	/** for 3D stuff (unused here)
	 * @param cs GeoCoordSys2D
	 */
	public void setCoordSys(GeoElement cs) {
		
	}
	

	public GeoConic(GeoConic conic) {
		this(conic.cons);
		set(conic);
	}
	
	protected String getClassName() {
		return "GeoConic";
	}
	
    public int getGeoClassType() {
    	return GEO_CLASS_CONIC;
    }
	
	protected String getTypeString() { 
		switch (type) {
			case GeoConic.CONIC_CIRCLE: 
				return "Circle";
			case GeoConic.CONIC_DOUBLE_LINE: 
				return "DoubleLine";
			case GeoConic.CONIC_ELLIPSE: 
				return "Ellipse"; 
			case GeoConic.CONIC_EMPTY: 
				return "EmptySet";
			case GeoConic.CONIC_HYPERBOLA: 
				return "Hyperbola";
			case GeoConic.CONIC_INTERSECTING_LINES: 
				return "IntersectingLines"; 
			case GeoConic.CONIC_PARABOLA: 
				return "Parabola"; 
			case GeoConic.CONIC_PARALLEL_LINES: 
				return "ParallelLines"; 
			case GeoConic.CONIC_SINGLE_POINT: 
				return "Point"; 
            
			default:
				return "Conic";
		}                       
	} 

	final public GeoElement copy() {
		return new GeoConic(this);
	}
	
	public boolean isFillable() {
		return true;
	}
	
	public boolean isTraceable() {
		return true;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean getTrace() {
		return trace;
	}
	
	public boolean isPath() {
		return true;
	}
	
	/**
	 * Returns a list of points that this conic passes through.
	 * May return null.
	 */
	final ArrayList getPointsOnConic() {
		return pointsOnConic;
	}
	
	/**
	 * Sets a list of points that this conic passes through.
	 * This method should only be used by AlgoMacro.
	 */
	final void setPointsOnConic(ArrayList points) {
		pointsOnConic = points;
	}
	
	/**
	 * Returns a list of points that this conic passes through.
	 * May return null.
	 */
	final void addPointOnConic(GeoPoint p) {
		if (pointsOnConic == null)
			pointsOnConic = new ArrayList();
		pointsOnConic.add(p);
	}

	/** geo is expected to be a conic. make deep copy of
	 * member vars of geo.
	 */
	public void set(GeoElement geo) {
		GeoConic co =(GeoConic) geo;
	
		// copy everything
		toStringMode = co.toStringMode;
		type = co.type;
		for (int i = 0; i < 6; i++)
			matrix[i] = co.matrix[i]; // flat matrix A   
		
		if (co.transform != null) {
			AffineTransform transform = getAffineTransform();
			transform.setTransform(co.transform);
		}
		
		eigenvec[0].setCoords(co.eigenvec[0]);
		eigenvec[1].setCoords(co.eigenvec[1]);
		b.setCoords(co.b);
		halfAxes[0] = co.halfAxes[0];
		halfAxes[1] = co.halfAxes[1];
		excent = co.excent;
		p = co.p;
		mu[0] = co.mu[0];
		mu[1] = co.mu[1];
		if (co.lines != null) {
			if (lines == null) {
				lines = new GeoLine[2];
				lines[0] = new GeoLine(cons);
				lines[1] = new GeoLine(cons);
			}
			lines[0].setCoords(co.lines[0]);
			lines[1].setCoords(co.lines[1]);
		}
		if (co.singlePoint != null) {
			if (singlePoint == null)
				singlePoint = new GeoPoint(cons);
			singlePoint.setCoords(co.singlePoint);
		}
		defined = co.defined;		
	}		

	final public void setToStringMode(int mode) {
		switch (mode) {
			case EQUATION_SPECIFIC :				
				this.toStringMode = EQUATION_SPECIFIC;
				break;
				
			case EQUATION_EXPLICIT:				
				this.toStringMode = EQUATION_EXPLICIT;
				break;

			default :
				this.toStringMode = EQUATION_IMPLICIT;
		}						
	}

	final public int getToStringMode() {
		return toStringMode;
	}
	final public int getType() {
		return type;
	}
	

	
	/**
	 * returns true if this conic is a circle or an ellipse
	 * @return
	 *
	public boolean isElliptic() {
		return (type == CONIC_CIRCLE || type == CONIC_ELLIPSE);
	}*/

	/**
	 * returns true if this conic is a circle 
	 * Michael Borcherds 2008-03-23
	 * @return
	 */
	public boolean isCircle() {
		return (type == CONIC_CIRCLE);
	}

	final public void setToSpecific() {
		setToStringMode(EQUATION_SPECIFIC);
	}

	final public void setToImplicit() {
		setToStringMode(EQUATION_IMPLICIT);
	}
	
	final public void setToExplicit() {
		setToStringMode(EQUATION_EXPLICIT);
	}

	/**
	 * Returns whether specific equation representation is possible.     
	 */
	final public boolean isSpecificPossible() {
		switch (type) {
			case CONIC_CIRCLE :
			case CONIC_DOUBLE_LINE :
			case CONIC_INTERSECTING_LINES :
			case CONIC_PARALLEL_LINES :
				return true;

			case CONIC_ELLIPSE :
			case CONIC_HYPERBOLA :
				//	xy vanished 
				return (kernel.isZero(matrix[3]));

			case CONIC_PARABOLA :
				// x\u00b2 or y\u00b2 vanished
				return kernel.isZero(matrix[0]) || kernel.isZero(matrix[1]);

			default :
				return false;
		}
	}
	
	/**
	 * Returns wheter explicit parabola equation representation (y = a x\u00b2 + b x + c) 
	 * is possible. 
	 */
	final public boolean isExplicitPossible() {
		return !kernel.isZero(matrix[5]) && kernel.isZero(matrix[3]) && kernel.isZero(matrix[1]);
	}

	public boolean isDefined() {
		return defined;
	}

	public void setUndefined() {
		defined = false;
		type = CONIC_EMPTY;		
	}

	final public void setDefined() {
		defined = true;
	}

	/**
	 * returns false if conic's matrix is the zero matrix
	 * or has infinite or NaN values
	 */
	final private boolean checkDefined() {
		boolean allZero = true;
		maxCoeffAbs = 0;		
		
		for (int i = 0; i < 6; i++) {
			if (Double.isNaN(matrix[i]) || Double.isInfinite(matrix[i]))
				return false;
			double abs = Math.abs(matrix[i]);			
			allZero = allZero && abs < Kernel.MIN_PRECISION;
			maxCoeffAbs = maxCoeffAbs > abs ? maxCoeffAbs : abs;			
		}
		if (allZero) return false;		
		
		// huge or tiny coefficients?
		if (maxCoeffAbs > MAX_COEFFICIENT_SIZE) {			
			double factor = 0.1;
			while (maxCoeffAbs * factor > MAX_COEFFICIENT_SIZE) factor /= 2;
			for (int i=0; i < 6; i++) matrix[i] *= factor;			
			maxCoeffAbs *= factor;
		}
		else if (maxCoeffAbs < MIN_COEFFICIENT_SIZE) {
			double factor = 10;
			while (maxCoeffAbs * factor < MIN_COEFFICIENT_SIZE) factor *= 2;			
			for (int i=0; i < 6; i++) matrix[i] *= factor;						
			maxCoeffAbs *= factor;
		}			
		
		return true;
	}

	final protected boolean showInEuclidianView() {
		return defined && (type != CONIC_EMPTY);
	}

	public final boolean showInAlgebraView() {
		//return defined;
		return true;
	}

	final public boolean isLineConic() {
		switch (type) {
			case CONIC_DOUBLE_LINE :
			case CONIC_PARALLEL_LINES :
			case CONIC_INTERSECTING_LINES :
				return true;

			default :
				return false;
		}
	}

	final public boolean isDegenerate() {
		switch (type) {
			case CONIC_CIRCLE :
			case CONIC_ELLIPSE :
			case CONIC_HYPERBOLA :
			case CONIC_PARABOLA :
				return false;

			default :
				return true;
		}
	}

	final public void setCoeffs(double[] coeffs) {
		setCoeffs(
			coeffs[0],
			coeffs[1],
			coeffs[2],
			coeffs[3],
			coeffs[4],
			coeffs[5]);
	}

	/*
	 *  sets conic's matrix from coefficients of equation
	 *  a x\u00b2 + b xy + c y\u00b2 + d x + e y + f = 0
	 */
	final public void setCoeffs(
		double a,
		double b,
		double c,
		double d,
		double e,
		double f) {
		matrix[0] = a; // x\u00b2
		matrix[1] = c; // y\u00b2
		matrix[2] = f; // constant
		matrix[3] = b / 2.0; // xy
		matrix[4] = d / 2.0; // x
		matrix[5] = e / 2.0; // y         

		classifyConic();
	}

	/**
	 * Changes the matrix coeffs so that the absolute maximum of A[0], ..., A[6] is
	 * between 10 and 100. Writes the results into out[].
	 *
	final private void avoidHugePrintCoeffs(double [] out) {
		//	avoid huge coefficients
		// find absolute maximum
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < 6; i++) {
			double abs = Math.abs(A[i]);
			if (abs > max)
				max = abs;
		}
		
		if (1.0 <= max && max < 100.0) {
			coeffs[0] = A[0]; // x\u00b2
			coeffs[2] = A[1]; // y\u00b2
			coeffs[5] = A[2]; // constant
			coeffs[1] = 2 * A[3]; // xy        
			coeffs[3] = 2 * A[4]; // x
			coeffs[4] = 2 * A[5]; // y  
			return;
		}
	
		// find factor to get 10 <= max < 100
		double divisor = 1E-2;
		while (true) {
			if (divisor > max)
				break;
			else
				divisor *= 10;
		}
		double factor = 100.0 / divisor;
		coeffs[0] = A[0] * factor; // x\u00b2
		coeffs[2] = A[1] * factor; // y\u00b2
		coeffs[5] = A[2] * factor; // constant
		coeffs[1] = 2 * A[3] * factor; // xy        
		coeffs[3] = 2 * A[4] * factor; // x
		coeffs[4] = 2 * A[5] * factor; // y
	}*/

	private double[] coeffs = new double[6];	
	
	/**
	 * returns equation of conic.
	 * in implicit mode: a x\u00b2 + b xy + c y\u00b2 + d x + e y + f = 0. 
	 * in specific mode: y\u00b2 = ...  , (x - m)\u00b2 + (y - n)\u00b2 = r\u00b2, ...
	 */
	public String toString() {	
		StringBuffer sbToString = getSbToString();
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(": ");
		sbToString.append(buildValueString()); 
		return sbToString.toString();
	}
		
	private StringBuffer sbToString;
	private StringBuffer getSbToString() {
		if (sbToString == null)
			sbToString = new StringBuffer(80);
		return sbToString;
	}
	
	public String toValueString() {
		return buildValueString().toString();	
	}			
	
	private StringBuffer buildValueString() {
		coeffs[0] = matrix[0]; // x\u00b2
		coeffs[2] = matrix[1]; // y\u00b2
		coeffs[5] = matrix[2]; // constant
		coeffs[1] = 2 * matrix[3]; // xy        
		coeffs[3] = 2 * matrix[4]; // x
		coeffs[4] = 2 * matrix[5]; // y  
		
		sbToValueString.setLength(0);				
		
		switch (toStringMode) {
			case EQUATION_SPECIFIC :
				if (!isSpecificPossible())
					return kernel.buildImplicitEquation(coeffs, vars, KEEP_LEADING_SIGN);						
				
				switch (type) {					
					case CONIC_CIRCLE :												
						if (kernel.isZero(b.x)) {
							sbToValueString.append("x\u00b2");
						} else {
							sbToValueString.append("(x ");
							sbToValueString.append(kernel.formatSigned(-b.x));
							sbToValueString.append(")\u00b2");
						}												
						sbToValueString.append(" + ");
						if (kernel.isZero(b.y)) {
							sbToValueString.append("y\u00b2");
						} else {
							sbToValueString.append("(y ");
							sbToValueString.append(kernel.formatSigned(-b.y));
							sbToValueString.append(")\u00b2");
						}
						sbToValueString.append(" = ");
						sbToValueString.append(kernel.format(halfAxes[0] * halfAxes[0]));
						return sbToValueString;

					case CONIC_ELLIPSE :					
						if (kernel.isZero(coeffs[1])) { // xy coeff = 0
							double coeff0, coeff1;
							// we have to check the first eigenvector: it could be (1,0) or (0,1)
							// if it is (0,1) we have to swap the coefficients of x^2 and y^2
							if (eigenvec[0].y == 0.0) {
								coeff0 = halfAxes[0];
								coeff1 = halfAxes[1];
							} else {
								coeff0 = halfAxes[1];
								coeff1 = halfAxes[0];
							}
							
							if (kernel.isZero(b.x)) {
								sbToValueString.append("x\u00b2");
							} else {
								sbToValueString.append("(x ");
								sbToValueString.append(kernel.formatSigned(-b.x));
								sbToValueString.append(")\u00b2");
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(coeff0 * coeff0));
							sbToValueString.append(" + ");
							if (kernel.isZero(b.y)) {
								sbToValueString.append("y\u00b2");
							} else {
								sbToValueString.append("(y ");
								sbToValueString.append(kernel.formatSigned(-b.y));
								sbToValueString.append(")\u00b2");
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(coeff1 * coeff1));
							sbToValueString.append(" = 1");
													
							return sbToValueString;
						} else
							return kernel.buildImplicitEquation(								
								coeffs,
								vars, 
								KEEP_LEADING_SIGN);

					case CONIC_HYPERBOLA :
						if (kernel.isZero(coeffs[1])) { // xy coeff = 0	
							char firstVar, secondVar;
							double b1, b2;
							// we have to check the first eigenvector: it could be (1,0) or (0,1)
							// if it is (0,1) we have to swap the x and y
							if (eigenvec[0].y == 0.0) {
								firstVar = 'x';
								secondVar = 'y';			
								b1 = b.x;
								b2 = b.y;
							} else {
								firstVar = 'y';
								secondVar = 'x';
								b1 = b.y;
								b2 = b.x;
							}
							
							if (kernel.isZero(b1)) {		
								sbToValueString.append(firstVar);
								sbToValueString.append("\u00b2");
							} else {
								sbToValueString.append('(');
								sbToValueString.append(firstVar);
								sbToValueString.append(' ');
								sbToValueString.append(kernel.formatSigned(-b1));
								sbToValueString.append(")\u00b2");
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(halfAxes[0] * halfAxes[0]));
							sbToValueString.append(" - ");
							if (kernel.isZero(b2)) {
								sbToValueString.append(secondVar);
								sbToValueString.append("\u00b2");								
							} else {
								sbToValueString.append('(');
								sbToValueString.append(secondVar);
								sbToValueString.append(' ');
								sbToValueString.append(kernel.formatSigned(-b2));
								sbToValueString.append(")\u00b2");
							}
							sbToValueString.append(" / ");
							sbToValueString.append(kernel.format(halfAxes[1] * halfAxes[1]));
							sbToValueString.append(" = 1");													
							
							return sbToValueString;
						} else
							return kernel.buildImplicitEquation(
								coeffs,
								vars,
								KEEP_LEADING_SIGN);

					case CONIC_PARABOLA :
						if (!kernel.isZero(coeffs[2]))
							return kernel.buildExplicitConicEquation(
								coeffs,
								vars,
								2,
								KEEP_LEADING_SIGN);
						else if (!kernel.isZero(coeffs[0]))
							return kernel.buildExplicitConicEquation(
								coeffs,
								vars,
								0,
								KEEP_LEADING_SIGN);
						else
							return kernel.buildImplicitEquation(
								coeffs,
								vars,
								KEEP_LEADING_SIGN);

					case CONIC_DOUBLE_LINE :
						sbToValueString.append('(');
						sbToValueString.append(lines[0].toStringLHS());
						sbToValueString.append(")\u00b2 = 0");
						return sbToValueString;

					case CONIC_PARALLEL_LINES :
					case CONIC_INTERSECTING_LINES :
						sbToValueString.append('(');
						sbToValueString.append(lines[0].toStringLHS());
						sbToValueString.append(") (");
						sbToValueString.append(lines[1].toStringLHS());
						sbToValueString.append(") = 0");
						return sbToValueString;
				}
				
			case EQUATION_EXPLICIT:
				if (isExplicitPossible())
					return kernel.buildExplicitConicEquation(coeffs, vars, 4, KEEP_LEADING_SIGN); 

			default : //implicit
				return kernel.buildImplicitEquation(coeffs, vars, KEEP_LEADING_SIGN);
		}
	}
	
	// TODO: continue 
	private StringBuffer sbToValueString = new StringBuffer(80);

/*
	public String printMatrix() {
		StringBuffer sb = new StringBuffer();
		sb.append("[\t");
		sb.append(kernel.format(A[0]));
		sb.append("\t\t");
		sb.append(kernel.format(A[3]));
		sb.append("\t\t");
		sb.append(kernel.format(A[4]));
		sb.append("\t]\n");

		sb.append("[\t");
		sb.append(kernel.format(A[3]));
		sb.append("\t\t");
		sb.append(kernel.format(A[1]));
		sb.append("\t\t");
		sb.append(kernel.format(A[5]));
		sb.append("\t]\n");

		sb.append("[\t");
		sb.append(kernel.format(A[4]));
		sb.append("\t\t");
		sb.append(kernel.format(A[5]));
		sb.append("\t\t");
		sb.append(kernel.format(A[2]));
		sb.append("\t]");

		return sb.toString();
	}
*/

	final public double[] getHalfAxes() {
		return halfAxes;
	}
	// for intersecting lines, parallel lines
	final public GeoLine[] getLines() {
		return lines;
	}
	final public GeoPoint getSinglePoint() {
		return singlePoint;
	}

	final public AffineTransform getAffineTransform() {
		if (transform == null)
			transform = new AffineTransform();
		return transform;
	}

	final private void setAffineTransform() {
		/*      ( v1x   v2x     bx )
		 *      ( v1y   v2y     by )
		 *      (  0     0      1  )   */
		AffineTransform transform = getAffineTransform();
		transform.setTransform(
			eigenvec[0].x,
			eigenvec[0].y,
			eigenvec[1].x,
			eigenvec[1].y,
			b.x,
			b.y);
	}

	final public GeoVec2D getTranslationVector() {
		return b;
	}
	
	/**
	 * return the radius of the circle (if the conic is a circle)
	 * @return the radius of the circle
	 */
	final public double getCircleRadius(){
		return halfAxes[0];
	}
	
	/**
	 * Transforms coords of point P from Eigenvector space to real world space.
	 * Note: P.setCoords() is not called here!
	 */
	final void coordsEVtoRW(GeoPoint P) {
		// rotate by alpha
		double px = P.x;
		P.x = px * eigenvec[0].x + P.y * eigenvec[1].x;
		P.y = px * eigenvec[0].y + P.y * eigenvec[1].y; 
		
		// translate by b
		P.x = P.x + P.z *  b.x;
		P.y = P.y + P.z * b.y;
	}
	
	/**
	 * Transforms coords of point P from real world space to Eigenvector space. 
	 * Note: P.setCoords() is not called here!
	 */
	private void coordsRWtoEV(GeoPoint P) {
		// translate by -b
		P.x = P.x - P.z * b.x;
		P.y = P.y - P.z * b.y;
		
		// rotate by -alpha
		double px = P.x;	
		P.x = px * eigenvec[0].x + P.y * eigenvec[0].y;
		P.y = px * eigenvec[1].x + P.y * eigenvec[1].y; 				
	}
	
	/** return copy of flat matrix */
	final public double[] getMatrix() {
		double[] ret = { matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5] };
		return ret;
	}

	/** set out with flat matrix of this conic */
	final public void getMatrix(double[] out) {
		for (int i = 0; i < 6; i++) {
			out[i] = matrix[i];
		}
	}

	/** set conic's matrix from flat matrix      
	 */
	final public void setMatrix(double[] matrix) {
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}
		classifyConic();
	}

	/**
	 * Sets both eigenvectors. 
	 * (note: needed for "near-to-relationship" after loading a file)  
	 * @param homogenous coordinates 
	 */
	final public void setEigenvectors(
		double x0,
		double y0,
		double z0,
		double x1,
		double y1,
		double z1) {
		eigenvec[0].x = x0 / z0;
		eigenvec[0].y = y0 / z0;
		eigenvec[1].x = x1 / z1;
		eigenvec[1].y = y1 / z1;
	}

	/** 
	 * Set conic's matrix from flat matrix (array of length 6).	 
	 */
	final public void setDegenerateMatrixFromArray(double[] matrix) {
		for (int i = 0; i < 6; i++) {
			this.matrix[i] = matrix[i];
		}				
		classifyConic(true);						
	}

	/** set conic's matrix from 3x3 matrix (not necessarily be symmetric).   
	 */
	final public void setMatrix(double[][] C) {
		matrix[0] = C[0][0];
		matrix[1] = C[1][1];
		matrix[2] = C[2][2];
		matrix[3] = (C[0][1] + C[1][0]) / 2.0;
		matrix[4] = (C[0][2] + C[2][0]) / 2.0;
		matrix[5] = (C[1][2] + C[2][1]) / 2.0;                              		
		classifyConic();
	}

	/**
	 * makes this conic a circle with midpoint M and radius r
	 */
	final public void setCircle(GeoPoint M, double r) {
		defined = M.isDefined() && !M.isInfinite(); // check midpoint
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		} 		
	}

	/**
	 * makes this conic a circle with midpoint M and radius BC
	 *  Michael Borcherds 2008-03-13	
	 */
	final public void setCircle(GeoPoint M, GeoPoint B, GeoPoint C) {
		defined = M.isDefined() && !M.isInfinite() &&
		B.isDefined() && !B.isInfinite() &&
		C.isDefined() && !C.isInfinite(); 
		
		double r=B.distance(C);
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		} 		
	}

	/**
	 * makes this conic a circle with midpoint M and radius geoSegment
	 *  Michael Borcherds 2008-03-13	
	 */
	final public void setCircle(GeoPoint M, GeoSegment geoSegment) {
		defined = M.isDefined() && !M.isInfinite() &&
		geoSegment.isDefined(); 
		
		double r=geoSegment.getLength();
		
		// check radius
		if (kernel.isZero(r)) {
			r = 0;
		} 
		else if (r < 0) {
			defined = false;
		}					

		if (defined) {
			setCircleMatrix(M, r);
			setAffineTransform();
		} 		
	}

	/**
	 * makes this conic a circle with midpoint M through Point P
	 */
	final public void setCircle(GeoPoint M, GeoPoint P) {
		defined = M.isDefined() && P.isDefined() && !P.isInfinite();
		if (!defined) {			
			return;
		}

		if (M.isInfinite()) {
			// midpoint at infinity -> parallelLines
			// one through P, the other through infinite point M 
			b.x = P.inhomX;
			b.y = P.inhomY;
			// M is normalvector of double line            
			eigenvecX = -M.y;
			eigenvecY = M.x;
			setEigenvectors();
			halfAxes[0] = Double.POSITIVE_INFINITY;
			halfAxes[1] = Double.POSITIVE_INFINITY;
			mu[0] = 0.0; // line at infinity is not drawn
			parallelLines(mu);
			// set line at infinity 0 = 1
			lines[1].x = Double.NaN;
			lines[1].y = Double.NaN;
			lines[1].z = Double.NaN;
			// set degenerate matrix
			matrix[0] = 0.0d;
			matrix[1] = 0.0d;
			matrix[2] = lines[0].z;
			matrix[3] = 0.0d;
			matrix[4] = lines[0].x / 2.0;
			matrix[5] = lines[0].y / 2.0;
		} else {
			setCircleMatrix(M, M.distance(P));
		}
		setAffineTransform();
	}

	final private void setCircleMatrix(GeoPoint M, double r) {
		// set midpoint
		b.x = M.inhomX;
		b.y = M.inhomY;

		// set halfAxes = radius		
		halfAxes[0] = r;
		halfAxes[1] = r;
		// set conic's matrix with M(mx, my, mz) and r
		//  [   1   0       -m       ]
		//  [   0   1       -n       ]
		//  [  -m  -n       m\u00b2+n\u00b2-r\u00b2 ]        
		matrix[0] = 1.0d;
		matrix[1] = 1.0d;
		matrix[2] = b.x * b.x + b.y * b.y - r * r;
		matrix[3] = 0.0;
		matrix[4] = -b.x;
		matrix[5] = -b.y;

		if (r > kernel.getEpsilon()) { // radius not zero 
			if (type != CONIC_CIRCLE) {
				type = CONIC_CIRCLE;
				excent = 0.0d;
				// set first eigenvector and eigenvectors
				eigenvecX = 1.0d;
				eigenvecY = 0.0d;
				setEigenvectors();
			}
		} else if (kernel.isZero(r)) { // radius == 0
			singlePoint();			
		} else { // radius < 0 or radius = infinite
			empty();
		}
	}

	/**
	 *  set Parabola from focus and line
	 */
	final public void setParabola(GeoPoint F, GeoLine g) {
		defined = F.isDefined() && !F.isInfinite() && g.isDefined();

		if (!defined)
			return;

		// set parabola's matrix
		double fx = F.inhomX;
		double fy = F.inhomY;

		matrix[0] = g.y * g.y;
		matrix[1] = g.x * g.x;
		double lsq = matrix[0] + matrix[1];
		matrix[2] = lsq * (fx * fx + fy * fy) - g.z * g.z;
		matrix[3] = -g.x * g.y;
		matrix[4] = - (lsq * fx + g.x * g.z);
		matrix[5] = - (lsq * fy + g.y * g.z);
				
		classifyConic();
	}

	// set the matrix
	// of ellipse or hyperbola with given foci B, C and length of first half axis a
	final public void setEllipseHyperbola(
		GeoPoint B,
		GeoPoint C,
		double a) {
			
		if (B.isInfinite() || C.isInfinite() || a < -kernel.getEpsilon()) {
			defined = false;
			return;
		}
							
		// set conics's matrix
		double b1 = B.inhomX;
		double b2 = B.inhomY;
		double c1 = C.inhomX;
		double c2 = C.inhomY;

		// precalculations
		double diff1 = b1 - c1;
		double diff2 = b2 - c2;
		double sqsumb = b1 * b1 + b2 * b2;
		double sqsumc = c1 * c1 + c2 * c2;
		double sqsumdiff = sqsumb - sqsumc;
		double a2 = 2.0 * a;
		double asq4 = a2 * a2;
		double asq = a * a;
		double afo = asq * asq;

		matrix[0] = 4.0 * (a2 - diff1) * (a2 + diff1);
		matrix[3] = -4.0 * diff1 * diff2;
		matrix[1] = 4.0 * (a2 - diff2) * (a2 + diff2);
		matrix[4] = -2.0 * (asq4 * (b1 + c1) - diff1 * sqsumdiff);
		matrix[5] = -2.0 * (asq4 * (b2 + c2) - diff2 * sqsumdiff);
		matrix[2] =
			-16.0 * afo - sqsumdiff * sqsumdiff + 8.0 * asq * (sqsumb + sqsumc);

		// set eigenvectors' directions (B -> C and normalvector)
		// this is needed, so that setEigenvectors() (called by classifyConic)
		// will surely take the right direction
		// normalizing is not needed at this point
		eigenvec[0].x = c1 - b1;
		eigenvec[0].y = c2 - b2;
		eigenvec[1].x = -eigenvec[0].y;
		eigenvec[1].y = eigenvec[0].x;
		
		classifyConic();
	}

	/*************************************
	 * MOVEMENTS
	 *************************************/

	/**
	 * translate conic by vector v
	 */
	final public void translate(GeoVector v) {
		doTranslate(v.x, v.y);

		classifyConic();
		setAffineTransform();
		updateDegenerates(); // for degenerate conics            
	}
	
	final public boolean isTranslateable() {
		return true;
	}

	/**
	 * translate this conic by vector (vx, vy)
	 */
	final public void translate(double vx, double vy) {
		doTranslate(vx, vy);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics      
	}
	
	final private void doTranslate(double vx, double vy) {
		// calc translated matrix   
		matrix[2] =
			matrix[2]
				+ vx * (matrix[0] * vx - 2.0 * matrix[4])
				+ vy * (matrix[1] * vy - 2.0 * matrix[5] + 2.0 * matrix[3] * vx);
		matrix[4] = matrix[4] - matrix[0] * vx - matrix[3] * vy;
		matrix[5] = matrix[5] - matrix[3] * vx - matrix[1] * vy;
		
		// avoid classification and set changes by hand:        
		b.x += vx;
		b.y += vy;
	}		

	/**
	 * rotate this conic by angle phi around (0,0)
	 */
	 final public void rotate(NumberValue phiVal) {
    	double phi = phiVal.getDouble();
		rotate(phi);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics     	
	}

	/**
	 * rotate this conic by angle phi around Q
	 */
	final public void rotate(NumberValue phiVal, GeoPoint Q) {
		double phi = phiVal.getDouble();
		double qx = Q.inhomX;
		double qy = Q.inhomY;

		// first translate to new origin Q
		doTranslate(-qx, -qy);
		// rotate around new origin Q
		rotate(phi);
		// translate back to old origin (0,0)
		doTranslate(qx, qy);
		
		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * rotate this conic by angle phi around (0,0)
	 * [ cos    -sin    0 ]
	 * [ sin    cos     0 ]
	 * [ 0      0       1 ]
	 */
	final private void rotate(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos - sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 - 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos - matrix[5] * sin;
		matrix[5] = matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].rotate(phi);
		eigenvec[1].rotate(phi);
		b.rotate(phi);		
	}
	
	/**
	 * dilate this conic from point S by factor r
	 */
	 final public void dilate(NumberValue rval, GeoPoint S) {  
	    double r = rval.getDouble();	
	 	double sx = S.inhomX;
		double sy = S.inhomY;
		
		// translate -S
		doTranslate(-sx, -sy);
		// do dilation
		doDilate(r);
		// translate +S
		doTranslate(sx, sy);
	
		classifyConic();        
	}
	
	final private void doDilate(double factor) {
		// calc dilated matrix
		double r = 1d/factor;
		double r2 = r*r;
		matrix[0] *= r2;
		matrix[1] *= r2;		
		matrix[3] *= r2;
		matrix[4] *= r;
		matrix[5] *= r;
	}	

	/**
	 * mirror this conic at point Q
	 */
	final public void mirror(GeoPoint Q) {
		double qx = Q.inhomX;
		double qy = Q.inhomY;

		matrix[2] =
			4.0
				* (qy * qy * matrix[1]
					+ qx * (qx * matrix[0] + 2.0 * qy * matrix[3] + matrix[4])
					+ qy * matrix[5])
				+ matrix[2];
		matrix[4] = -2.0 * (qx * matrix[0] + qy * matrix[3]) - matrix[4];
		matrix[5] = -2.0 * (qx * matrix[3] + qy * matrix[1]) - matrix[5];

		// change eigenvectors' orientation
		eigenvec[0].mult(-1.0);
		eigenvec[1].mult(-1.0);
		
		// mirror translation vector b
		b.mirror(Q);

		setAffineTransform();
		updateDegenerates(); // for degenerate conics
	}

	/**
	 * mirror this point at line g
	 */
	final public void mirror(GeoLine g) {
		// Y = S(phi).(X - Q) + Q
		// where Q is a point on g, S(phi) is the mirror transform
		// and phi/2 is the line's slope angle

		// get arbitrary point of line
		double qx, qy;
		if (Math.abs(g.x) > Math.abs(g.y)) {
			qx = -g.z / g.x;
			qy = 0.0d;
		} else {
			qx = 0.0d;
			qy = -g.z / g.y;
		}

		// translate -Q
		doTranslate(-qx, -qy);
		// do mirror transform
		mirror(2.0 * Math.atan2(-g.x, g.y));
		// translate +Q
		doTranslate(qx, qy);
	
		setAffineTransform();
		updateDegenerates(); // for degenerate conics		
	}

	/**
	* mirror transform with angle phi
	* [ cos    sin     0 ]
	* [ sin    -cos    0 ]
	* [ 0      0       1 ]
	*/
	final private void mirror(double phi) {
		// set rotated matrix
		double sum = matrix[0] + matrix[1];
		double diff = matrix[0] - matrix[1];
		double cos = Math.cos(phi);
		double sin = Math.sin(phi);
		// cos(2 phi) = cos(phi)\u00b2 - sin(phi)\u00b2 = (cos + sin)*(cos - sin)
		double cos2 = (cos + sin) * (cos - sin);
		// cos(2 phi) = 2 cos sin
		double sin2 = 2.0 * cos * sin;

		double temp = diff * cos2 + 2.0 * matrix[3] * sin2;
		double A0 = (sum + temp) / 2.0;
		double A1 = (sum - temp) / 2.0;
		double A3 = -matrix[3] * cos2 + diff * cos * sin;
		double A4 = matrix[4] * cos + matrix[5] * sin;
		matrix[5] = -matrix[5] * cos + matrix[4] * sin;
		matrix[0] = A0;
		matrix[1] = A1;
		matrix[3] = A3;
		matrix[4] = A4;

		// avoid classification: make changes by hand
		eigenvec[0].mirror(phi);
		eigenvec[1].mirror(phi);
		b.mirror(phi);			
	}

	// to avoid classification in movements 
	// this method is called to update
	// the lines (point) of degenerate conics
	final private void updateDegenerates() {
		// update lines of degenerate conic        
		switch (type) {
			case CONIC_SINGLE_POINT :
				singlePoint();
				break;

			case CONIC_INTERSECTING_LINES :
				intersectingLines(mu); // coefficient mu unchanged
				break;

			case CONIC_DOUBLE_LINE :
				doubleLine();
				break;

			case CONIC_PARALLEL_LINES :
				parallelLines(mu); // coefficient mu unchanged
				break;
		}
	}

	/*************************************
	 * CONIC CLASSIFICATION
	 *************************************/
	final private void setEigenvectors() {
		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]        
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = GeoVec2D.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}
		
		if (kernel.isContinuous()) {
			// first eigenvector
			if (eigenvec[0].x * eigenvecX < -eigenvec[0].y * eigenvecY) {
				eigenvec[0].x = -eigenvecX;
				eigenvec[0].y = -eigenvecY;
			} else {
				eigenvec[0].x = eigenvecX;
				eigenvec[0].y = eigenvecY;
			}
			
			// second eigenvector (compared to normalvector (-eigenvecY, eigenvecX)
			if (eigenvec[1].y * eigenvecX < eigenvec[1].x * eigenvecY) {
				eigenvec[1].x = eigenvecY;
				eigenvec[1].y = -eigenvecX;
			} else {
				eigenvec[1].x = -eigenvecY;
				eigenvec[1].y = eigenvecX;
			}
		} 	
		// non-continous
		else {
			eigenvec[0].x = eigenvecX;
			eigenvec[0].y = eigenvecY;
			eigenvec[1].x = -eigenvecY;
			eigenvec[1].y = eigenvecX;
		}		
	}

	final private void setParabolicEigenvectors() {
		// fist eigenvector of parabola must not be changed

		// newly calculated first eigenvector = (eigenvecX, eigenvecY)
		// old eigenvectors: eigenvec[0], eigenvec[1]        
		// set direction of eigenvectors with respect to old values:
		// take inner product >= 0 (small angle change)

		// make (eigenvecX, eigenvecY) a unit vector
		length = GeoVec2D.length(eigenvecX, eigenvecY);
		if (length != 1.0d) {
			eigenvecX = eigenvecX / length;
			eigenvecY = eigenvecY / length;
		}

		// first eigenvector
		eigenvec[0].x = eigenvecX;
		eigenvec[0].y = eigenvecY;

		if (kernel.isContinuous()) {
			// second eigenvector (compared to normalvector (-eigenvecY, eigenvecX)
			if (eigenvec[1].y * eigenvecX < eigenvec[1].x * eigenvecY) {
				eigenvec[1].x = eigenvecY;
				eigenvec[1].y = -eigenvecX;
			} else {
				eigenvec[1].x = -eigenvecY;
				eigenvec[1].y = eigenvecX;
			}
		} else {
			// non-continous
			eigenvec[1].x = -eigenvecY;
			eigenvec[1].y = eigenvecX;
		}
	}
	
	private void classifyConic() {
		classifyConic(false);
	}
	
	private void classifyConic(boolean degenerate) {		
		defined = degenerate || checkDefined();
		if (!defined)
			return;

		/* submatrix S =                  
		 *  ( A[0]     A[3] )
		 *  ( A[3]     A[1] )         
		 */								
		
		// det of S lets us distinguish between
		// parabolic and midpoint conics
		// det(S) = A[0] * A[1] - A[3] * A[3]
		double A0A1 =  matrix[0] * matrix[1];
		double A3A3 = matrix[3] * matrix[3];					
		
		if (kernel.isEqual(A0A1,  A3A3)) {	// det S = 0
			classifyParabolicConic(degenerate);
		} else {
			detS = A0A1 - A3A3;
			classifyMidpointConic(degenerate);
		}		
		setAffineTransform();		
		
		// Application.debug("conic: " + this.getLabel() + " type " + getTypeString() );
		// Application.debug("           detS: " + (A0A1 - A3A3));
	}

	/*************************************
	* midpoint conics
	*************************************/

	final private void classifyMidpointConic(boolean degenerate) {
		// calc eigenvalues and eigenvectors
		if (kernel.isZero(matrix[3])) {
			// special case: submatrix S is allready diagonal
			eigenval[0] = matrix[0];
			eigenval[1] = matrix[1];
			eigenvecX = 1.0d;
			eigenvecY = 0.0d;
		} else {
			// eigenvalues are solutions of 
			// 0 = det(S - x E) = x\u00b2 - spurS x + detS                                   
			// detS was computed in classifyConic()            
			// init array for solver
			eigenval[0] = detS;
			eigenval[1] = - (matrix[0] + matrix[1]); // -spurS
			eigenval[2] = 1.0d;
			eqnSolver.solveQuadratic(eigenval, eigenval);

			// set first eigenvector
			eigenvecX = matrix[3];
			eigenvecY = eigenval[0] - matrix[0];
		}

		// calc translation vector b = midpoint
		// b = -Inverse[S] . a, where a = (A[4], A[5])      
		b.x = (matrix[3] * matrix[5] - matrix[1] * matrix[4]) / detS;
		b.y = (matrix[3] * matrix[4] - matrix[0] * matrix[5]) / detS;

		// beta = a . b + alpha, where alpha = A[2]
		double beta = matrix[4] * b.x + matrix[5] * b.y + matrix[2];

		// beta lets us distinguish between Ellipse, Hyperbola,
		// single singlePoint and intersecting lines
		//  if (kernel.isZero(beta)) {
		if (degenerate || kernel.isZero(beta)) {
			setEigenvectors();
			// single point or intersecting lines
			mu[0] = eigenval[0] / eigenval[1];
			if (kernel.isZero(mu[0])) {
				mu[0] = 0.0;
				intersectingLines(mu);
			} else if (mu[0] < 0.0d) {
				mu[0] = Math.sqrt(-mu[0]);
				intersectingLines(mu);
			} else {												
				singlePoint();
			}
		} else {
			// Hyperbola, Ellipse, empty            
			mu[0] = -eigenval[0] / beta;
			mu[1] = -eigenval[1] / beta;
			if (detS < 0) {
				hyperbola(mu);
			} else {
				if (mu[0] > 0 && mu[1] > 0) {
					ellipse(mu);
				} else {
					empty();
				}
			}
		}
	}

	final private void singlePoint() {
		type = GeoConic.CONIC_SINGLE_POINT;

		if (singlePoint == null)
			singlePoint = new GeoPoint(cons);
		singlePoint.setCoords(b.x, b.y, 1.0d);
		//Application.debug("singlePoint : " + b);
	}

	final private void intersectingLines(double[] mu) {
		type = GeoConic.CONIC_INTERSECTING_LINES;

		// set intersecting lines
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		// n = T . (-mu, 1)
		temp1 = eigenvec[0].x * mu[0];
		temp2 = eigenvec[0].y * mu[0];
		nx = eigenvec[1].x - temp1;
		ny = eigenvec[1].y - temp2;

		// take line with smallest change of direction
		if (Math.abs(nx * lines[0].x + ny * lines[0].y)
			< Math.abs(nx * lines[1].x + ny * lines[1].y))
			index = 1;
		else
			index = 0;

		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = - (nx * b.x + ny * b.y);

		// n = T . (mu, 1)
		nx = eigenvec[1].x + temp1;
		ny = eigenvec[1].y + temp2;
		index = 1 - index;
		lines[index].x = nx;
		lines[index].y = ny;
		lines[index].z = - (nx * b.x + ny * b.y);
		//Application.debug("intersectingLines: " + lines[0] + ", " + lines[1]);
	}

	final private void ellipse(double[] mu) {
		if (mu[0] > mu[1]) {
			// swap eigenvectors and mu            
			temp = mu[0];
			mu[0] = mu[1];
			mu[1] = temp;

			// rotate eigenvector 90�
			temp = eigenvecX;
			eigenvecX = -eigenvecY;
			eigenvecY = temp;
		}
		setEigenvectors();

		// circle 
		if (kernel.isEqual(mu[0], mu[1])) {
			type = GeoConic.CONIC_CIRCLE;
			halfAxes[0] = Math.sqrt(1.0d / mu[0]);
			halfAxes[1] = halfAxes[0];
			excent = 0.0d;
			//Application.debug("circle: M = " + b + ", r = " + halfAxes[0]);    
		} else { // elipse
			type = GeoConic.CONIC_ELLIPSE;
			mu[0] = 1.0d / mu[0];
			mu[1] = 1.0d / mu[1];
			halfAxes[0] = Math.sqrt(mu[0]);
			halfAxes[1] = Math.sqrt(mu[1]);
			excent = Math.sqrt(mu[0] - mu[1]);

			/*
			Application.debug("Ellipse");            
			Application.debug("a : " + halfAxes[0]);
			Application.debug("b : " + halfAxes[1]);
			Application.debug("e : " + excent);                                  
			*/
		}
	}

	final private void hyperbola(double[] mu) {
		type = GeoConic.CONIC_HYPERBOLA;
		if (mu[0] < 0) {
			// swap eigenvectors and mu            
			temp = mu[0];
			mu[0] = mu[1];
			mu[1] = temp;

			// rotate eigenvector 90�
			temp = eigenvecX;
			eigenvecX = -eigenvecY;
			eigenvecY = temp;
		}
		setEigenvectors();

		mu[0] = 1.0d / mu[0];
		mu[1] = -1.0d / mu[1];
		halfAxes[0] = Math.sqrt(mu[0]);
		halfAxes[1] = Math.sqrt(mu[1]);
		excent = Math.sqrt(mu[0] + mu[1]);

		/*
		Application.debug("Hyperbola");            
		Application.debug("a : " + halfAxes[0]);
		Application.debug("b : " + halfAxes[1]);
		Application.debug("e : " + excent);                  
		 */
	}

	final private void empty() {
		type = GeoConic.CONIC_EMPTY;
		// Application.debug("empty conic");
	}

	/*************************************
	* parabolic conics
	*************************************/

	final private void classifyParabolicConic(boolean degenerate) {			
		// calc eigenvalues and first eigenvector               
		if (kernel.isZero(matrix[3])) {						
			// special cases: submatrix S is allready diagonal
			// either A[0] or A[1] have to be zero (due to detS = 0)
			if (kernel.isZero(matrix[0])) {

				// special case: the submatrix S is zero!!!
				if (kernel.isZero(matrix[1])) {
					handleSzero();
					return;
				}
								
				// else
				lambda = matrix[1];
				//	set first eigenvector
				eigenvecX = 1.0d; 
				eigenvecY = 0.0d;	
				// c = a . T = a, 
				// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
				c.x = matrix[4];
				c.y = matrix[5];
			} else { // A[1] is zero                
				lambda = matrix[0];
				eigenvecX = 0.0d; // set first eigenvector
				eigenvecY = 1.0d;
				// c = a . T, 
				// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
				c.x = matrix[5];
				c.y = -matrix[4];
			}
		} 
		else { // A[3] != 0			
			// eigenvalues are solutions of 
			// 0 = det(S - x E) = x^2 - spurS x + detS = x (x - spurS)                                    
			lambda = matrix[0] + matrix[1]; // spurS                         
			// set first eigenvector as a unit vector (needed fo computing vector c)
			length = GeoVec2D.length(matrix[3], matrix[0]);
			eigenvecX = matrix[3] / length;
			eigenvecY = -matrix[0] / length;
			// c = a . T, 
			// where T is the matrix of the eigenvectors and a = (A[4], A[5])                
			c.x = matrix[4] * eigenvecX + matrix[5] * eigenvecY;
			c.y = matrix[5] * eigenvecX - matrix[4] * eigenvecY;
		}		

		if (degenerate || kernel.isZero(c.x)) {
			setEigenvectors();
			// b = T . (0, -c.y/lambda)
			temp = c.y / lambda;
			b.x = temp * eigenvecY;
			b.y = -temp * eigenvecX;
			mu[0] = -temp * temp + matrix[2] / lambda;
			
			if (kernel.isZero(mu[0])) {			
				doubleLine();
			} else if (mu[0] < 0) {			
				mu[0] = Math.sqrt(-mu[0]);				
				parallelLines(mu);
			} else {
				empty();
			}
		} else { // parabola						
			parabola();
		}

	}

	final private void doubleLine() {
		type = GeoConic.CONIC_DOUBLE_LINE;

		// set double line
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		nx = -eigenvec[0].y;
		ny = eigenvec[0].x;
		lines[0].x = nx;
		lines[0].y = ny;
		lines[0].z = - (b.x * nx + b.y * ny);

		lines[1].x = lines[0].x;
		lines[1].y = lines[0].y;
		lines[1].z = lines[0].z;
		//Application.debug("double line : " + lines[0]);
	}
	
	final public void enforceDoubleLine() {
		defined = true;
		doubleLine();
	}
	
	// if S is the zero matrix, set conic as double line or empty
	final private void handleSzero() {			
		// conic is line 2*A[4] * x +  2*A[5] * y + A[2] = 0				
	    if (kernel.isZero(matrix[4])) {
	    	if (kernel.isZero(matrix[5])) {	    		
	    		empty();
	    		return;
	    	} 
	    	
	    	// A[5] not zero
	    	// make b a point on the line
	    	b.x = 0;
	    	b.y = -matrix[2] / (2*matrix[5]);	    	
	    } else { 
	    	// A[4] not zero
	    	// make b a point on the line
	    	b.x = -matrix[2] / (2*matrix[4]);
	    	b.y = 0;	    	
	    }
	    
	    eigenvecX =  matrix[5];
    	eigenvecY = -matrix[4];
    	setEigenvectors();
    	    	
	    doubleLine();    			
	}	

	final private void parallelLines(double[] mu) {
		type = GeoConic.CONIC_PARALLEL_LINES;

		// set double line
		if (lines == null) {
			lines = new GeoLine[2];
			lines[0] = new GeoLine(cons);
			lines[1] = new GeoLine(cons);
		}
		nx = -eigenvec[0].y;
		ny = eigenvec[0].x;
		temp1 = b.x * nx + b.y * ny;
		lines[0].x = nx;
		lines[0].y = ny;
		lines[1].x = nx;
		lines[1].y = ny;
		// smallest change: 
		temp2 = mu[0] - temp1;
		if (Math.abs(lines[0].z - temp2) < Math.abs(lines[1].z - temp2)) {
			lines[0].z = temp2;
			lines[1].z = -temp1 - mu[0];
		} else {
			lines[0].z = -temp1 - mu[0];
			lines[1].z = temp2;
		}
		// Application.debug("parallel lines : " + lines[0] + ", " + lines[1]);
		// Application.debug("coeff : " + mu[0]);
	}

	final private void parabola() {
		type = GeoConic.CONIC_PARABOLA;

		// calc vertex = b
		// b = T . ((c.y\u00b2/lambda - A2)/(2 c.x) , -c.y/lambda)
		temp2 = c.y / lambda;
		temp1 = (c.y * temp2 - matrix[2]) / (2 * c.x);
		b.x = eigenvecY * temp2 + eigenvecX * temp1;
		b.y = eigenvecY * temp1 - eigenvecX * temp2;
		setParabolicEigenvectors();

		// parameter p of parabola
		p = -c.x / lambda;
		if (p < 0) { // change orientation of first eigenvector
			eigenvec[0].x = -eigenvec[0].x;
			eigenvec[0].y = -eigenvec[0].y;
			p = -p;
		}

		/*
		Application.debug("parabola");
		Application.debug("Vertex: " + b);
		Application.debug("p = " + p);
		*/
	}
	

	/**********************************************************
	 * CACLCULATIONS ON CONIC (det, evaluate, intersect, ...)
	 **********************************************************/

	/** 
	 * Computes the determinant of a conic's 3x3 matrix.
	 * @param matrix: flat matrix of conic section 
	 */
	public static double det(double [] matrix) {
		return matrix[0] * (matrix[1] * matrix[2] - matrix[5] * matrix[5])
			- matrix[2] * matrix[3] * matrix[3]
			- matrix[1] * matrix[4] * matrix[4]
			+ 2 * matrix[3] * matrix[4] * matrix[5];
	}
	
	/**
	 * Returns true iff the determinant of 2x2 matrix of eigenvectors is
	 * positive. 
	 * @return
	 */
	final boolean hasPositiveEigenvectorOrientation() {
		//return eigenvec[0].x * eigenvec[1].y - eigenvec[0].y * eigenvec[1].x > 0;
		return eigenvec[0].x * eigenvec[1].y > eigenvec[0].y * eigenvec[1].x;
	}

	final void setPositiveEigenvectorOrientation(boolean flag) {
			if (flag != hasPositiveEigenvectorOrientation()) {
				eigenvec[1].x = -eigenvec[1].x;
				eigenvec[1].y = -eigenvec[1].y;
				
				setAffineTransform();
			}
	}
	
	
	/** 
	 * states wheter P lies on this conic or not 
	 */
	 public boolean isIntersectionPointIncident(GeoPoint P, double eps) {		
		return isOnFullConic(P, eps);
	 }
	 
	 /** 
	 * states wheter P lies on this conic or not. Note: this method
	 * is not overwritten by subclasses like isIntersectionPointIncident()
	 */
	final boolean isOnFullConic(GeoPoint P, double eps) {						
		switch (type) {	
			 case GeoConic.CONIC_SINGLE_POINT:                         
	            return P.distance(singlePoint) < eps;                             
	            
	        case GeoConic.CONIC_INTERSECTING_LINES:  
	        case GeoConic.CONIC_DOUBLE_LINE: 
	        case GeoConic.CONIC_PARALLEL_LINES:                
	            return lines[0].isOnFullLine(P, eps) || lines[1].isOnFullLine(P, eps);	    
	        
	        case GeoConic.CONIC_EMPTY:
	        	return false;
		}        
		
		// if we get here let's handle the remaining cases
				
     	// remember coords of P
		double Px = P.x;
		double Py = P.y;
		double Pz = P.z;
														
		// convert P to eigenvector coord system
		coordsRWtoEV(P);	
		double px = P.x / P.z;
		double py = P.y / P.z;
		
		boolean result = false;			
		switch (type) {	
			case GeoConic.CONIC_CIRCLE:
			  	// x^2 + y^2 = r^2
				double radius2 = halfAxes[0]*halfAxes[0];
			  	result = Kernel.isEqual(px*px/radius2 + py*py/radius2, 1, eps);
				break;		   					
		  	
			case GeoConic.CONIC_ELLIPSE:
          		// x^2/a^2 + y^2/b^2 = 1
			  	result = Kernel.isEqual(px*px / (halfAxes[0]*halfAxes[0]) + py*py / (halfAxes[1]*halfAxes[1]), 1, eps);
				break;	
				
			case GeoConic.CONIC_HYPERBOLA:   
	          	// 	x^2/a^2 - y^2/b^2 = 1
			  	result = Kernel.isEqual(px*px / (halfAxes[0]*halfAxes[0]), 1 + py*py / (halfAxes[1]*halfAxes[1]), eps);
				break;	
				
			case GeoConic.CONIC_PARABOLA: 
          		// y^2 = 2 p x								               
                result = Kernel.isEqual(py*py, 2*p*px, eps);
				break;	
		}
			
		// restore coords of P
		P.x = Px; 
		P.y = Py; 
		P.z = Pz;
		return result;				
	}

	/**
	 * return wheter this conic represents the same conic as c 
	 * (this = lambda * c).
	 */
	public boolean isEqual(GeoElement geo) {

		if (!geo.isGeoConic()) return false;
		
		GeoConic c = (GeoConic)geo;
		double[] B = c.matrix;

		double lambda = 0.0;
		boolean aZero, bZero, equal = true;
		for (int i = 0; i < 6; i++) {
			aZero = kernel.isZero(matrix[i]);
			bZero = kernel.isZero(B[i]);

			// A[i] == 0 and B[i] != 0  => not equal
			if (aZero && !bZero)
				equal = false;
			// B[i] == 0 and A[i] != 0  => not equal
			else if (bZero && !aZero)
				equal = false;
			// A[i] != 0 and B[i] != 0
			else if (!aZero && !bZero) {
				// init lambda?
				if (lambda == 0.0)
					lambda = matrix[i] / B[i];
				// check equality
				else
					equal = kernel.isEqual(matrix[i], lambda * B[i]);
			}
			// leaf loop
			if (!equal)
				break;
		}
		return equal;
	}

	/**
	 * evaluates P . A . P
	 */
	final public double evaluate(GeoPoint P) {
		return P.x * (matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z)
			+ P.y * (matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z)
			+ P.z * (matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z);
	}

	/**
	 * evaluates (p.x, p.y, 1) . A . (p.x, p.y, 1)
	 */
	final double evaluate(GeoVec2D p) {
		return matrix[2]
			+ matrix[4] * p.x
			+ matrix[5] * p.y
			+ p.y * (matrix[5] + matrix[3] * p.x + matrix[1] * p.y)
			+ p.x * (matrix[4] + matrix[0] * p.x + matrix[3] * p.y);
	}

	/**
	 * evaluates (x, y, 1) . A . (x, y, 1)
	 */
	final double evaluate(double x, double y) {
		return matrix[2]
			+ matrix[4] * x
			+ matrix[5] * y
			+ y * (matrix[5] + matrix[3] * x + matrix[1] * y)
			+ x * (matrix[4] + matrix[0] * x + matrix[3] * y);
	}

	/**
	 *  Sets the GeoLine polar to A.P, the polar line of P relativ to this conic.
	 */
	final public void polarLine(GeoPoint P, GeoLine polar) {
		polar.x = matrix[0] * P.x + matrix[3] * P.y + matrix[4] * P.z;
		polar.y = matrix[3] * P.x + matrix[1] * P.y + matrix[5] * P.z;
		polar.z = matrix[4] * P.x + matrix[5] * P.y + matrix[2] * P.z;
	}

	/**
	 * Sets the GeoLine diameter to X.S.v + a.v (v is a direction), 
	 * the diameter line parallel to g relativ to this conic.
	 */
	final public void diameterLine(GeoVector v, GeoLine diameter) {
		diameter.x = matrix[0] * v.x + matrix[3] * v.y;
		diameter.y = matrix[3] * v.x + matrix[1] * v.y;
		diameter.z = matrix[4] * v.x + matrix[5] * v.y;
	}

	/**
	 * returns all class-specific xml tags for saveXML
	 */
	protected String getXMLtags() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.getXMLtags());
		//	line thickness and type  
		sb.append(getLineStyleXML());

		sb.append("\t<eigenvectors ");
		sb.append(" x0=\"" + eigenvec[0].x + "\"");
		sb.append(" y0=\"" + eigenvec[0].y + "\"");
		sb.append(" z0=\"1.0\"");
		sb.append(" x1=\"" + eigenvec[1].x + "\"");
		sb.append(" y1=\"" + eigenvec[1].y + "\"");
		sb.append(" z1=\"1.0\"");
		sb.append("/>\n");

		// matrix must be saved after eigenvectors
		// as only <matrix> will cause a call to classifyConic()
		// see geogebra.io.MyXMLHandler: handleMatrix() and handleEigenvectors()
		sb.append("\t<matrix");
		for (int i = 0; i < 6; i++)
			sb.append(" A" + i + "=\"" + matrix[i] + "\"");
		sb.append("/>\n");

		// implicit or specific mode
		switch (toStringMode) {
			case GeoConic.EQUATION_SPECIFIC :
				sb.append("\t<eqnStyle style=\"specific\"/>\n");
				break;

			case GeoConic.EQUATION_EXPLICIT :
				sb.append("\t<eqnStyle style=\"explicit\"/>\n");
				break;
				
			default :
				sb.append("\t<eqnStyle style=\"implicit\"/>\n");
		}

		return sb.toString();
	}

	public String getSpecificEquation() {
		  String ret = null;
		  switch (type) {
			 case GeoConic.CONIC_CIRCLE:
				 ret = app.getPlain("CircleEquation");
				 break;

			 case GeoConic.CONIC_ELLIPSE:
				 ret = app.getPlain("EllipseEquation");
				 break;

			 case GeoConic.CONIC_HYPERBOLA:
				 ret = app.getPlain("HyperbolaEquation");
				 break;
 
			 case GeoConic.CONIC_PARABOLA:
				 ret = app.getPlain("ParabolaEquation");
				 break;        
    
			 case GeoConic.CONIC_DOUBLE_LINE:
				 ret = app.getPlain("DoubleLineEquation");
				 break;      
    
			 case GeoConic.CONIC_PARALLEL_LINES:
			 case GeoConic.CONIC_INTERSECTING_LINES:
				 ret = app.getPlain("ConicLinesEquation");
				 break;                  
		 }
		 return ret;
	  }

   

	/* 
	 * Path interface
	 */
	 
	public void pointChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		double px, py;		
		PathParameter pp = P.getPathParameter();
		pp.pathType = type;
		pp.branch = -1;
			
		switch (type) {
			case CONIC_EMPTY:
				P.x = Double.NaN;
				P.y = Double.NaN;
				P.z = Double.NaN;
			break;
			
			case CONIC_SINGLE_POINT:
				P.x = singlePoint.x;
				P.y = singlePoint.y;
				P.z = singlePoint.z;
			break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				if (lines[0].distanceHom(P) <= lines[1].distanceHom(P)) {
					lines[0].pointChanged(P);
					pp.branch = 0;
				} else {
					lines[1].pointChanged(P);
					pp.branch = 1;					
				}
			break;
			
			case CONIC_DOUBLE_LINE:
				lines[0].pointChanged(P);
				pp.branch = 0;
			break;
			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:			
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);				
				
				// calc parameter 
				px = P.x / P.z;
				py = P.y / P.z;		
				
				// relation between the internal parameter t and the angle theta:
				// t = atan(a/b tan(theta)) where tan(theta) = py / px
				pp.t = Math.atan2(halfAxes[0]*py, halfAxes[1]*px);

				// calc Point on conic using this parameter
				P.x = halfAxes[0] * Math.cos(pp.t);	
				P.y = halfAxes[1] * Math.sin(pp.t);
				P.z = 1.0;
				
				//	transform back to real world coord system
				coordsEVtoRW(P);								
			break;			
			
			case CONIC_HYPERBOLA:
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);				
				
				// calc parameter 
				px =P.x / P.z;
				py = P.y / P.z;
				
				// keep py				
				pp.t = MyMath.asinh(py / halfAxes[1]);
				P.x = halfAxes[0] * MyMath.cosh(pp.t);	
				P.y = py;
				P.z = 1.0;
				if (px < 0) {										
					pp.branch = 1;
					P.x = -P.x;
				} else {									
					pp.branch = 0;
				}
					
				//	transform back to real world coord system
				coordsEVtoRW(P);													
			break;																			
			
			case CONIC_PARABOLA:
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);
				
				// keep py
				py = P.y / P.z;
				pp.t = py / p;
				P.x = p * pp.t  * pp.t  / 2.0;
				P.y = py;
				P.z = 1.0; 									
				
				// transform back to real world coord system
				coordsEVtoRW(P);		
			break;
		}		
	}
	
	public void pathChanged(GeoPointInterface PI) {
		
		GeoPoint P = (GeoPoint) PI;
		
		// if type of path changed (other conic) then we
		// have to recalc the parameter with pointChanged()
		PathParameter pp = P.getPathParameter();
		if (pp.pathType != type) {
			pointChanged(P);
			return;
		}
		
		switch (type) {
			case CONIC_EMPTY:
				P.x = Double.NaN;
				P.y = Double.NaN;
				P.z = Double.NaN;
			break;
			
			case CONIC_SINGLE_POINT:
				P.x = singlePoint.x;
				P.y = singlePoint.y;
				P.z = singlePoint.z;
			break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				if (pp.branch == 0) {
					lines[0].pathChanged(P);					 
				} else {
					lines[1].pathChanged(P);										
				}
			break;
			
			case CONIC_DOUBLE_LINE:
				lines[0].pathChanged(P);	
			break;
			
			case CONIC_CIRCLE:	
				//	calc Point on conic using this parameter (in eigenvector space)
				 P.x = b.x + halfAxes[0] * Math.cos(pp.t);	
				 P.y = b.y + halfAxes[0] * Math.sin(pp.t);
				 P.z = 1.0;										
			break;
			
			case CONIC_ELLIPSE:												
				// calc Point on conic using this parameter (in eigenvector space)
				P.x = halfAxes[0] * Math.cos(pp.t);	
				P.y = halfAxes[1] * Math.sin(pp.t);
				P.z = 1.0;
				
				//	transform to real world coord system
				coordsEVtoRW(P);								
			break;			
			
			case CONIC_HYPERBOLA:																									
				P.x = halfAxes[0] * MyMath.cosh(pp.t);
				P.y = halfAxes[1] * MyMath.sinh(pp.t);
				P.z = 1.0;				
				if (pp.branch == 1) P.x = -P.x;
						
				//	transform back to real world coord system
				coordsEVtoRW(P);													
			break;																			
			
			case CONIC_PARABOLA:
				P.y = p * pp.t;				
				P.x = P.y * pp.t  / 2.0;				
				P.z = 1.0;				
				
				//	transform back to real world coord system
				coordsEVtoRW(P);					
			break;
		}		
			
	}
	
	public boolean isOnPath(GeoPointInterface PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		return isOnFullConic(P, eps);
	}
	
	/**
	 * Returns the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 * @return
	 */
	public double getMinParameter() {
		switch (type) {		
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
			case CONIC_DOUBLE_LINE:
			case CONIC_HYPERBOLA:
			case CONIC_PARABOLA:
				return Double.NEGATIVE_INFINITY;
							
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
			default:
				return 0;		
		}		
	}
	
	/**
	 * Returns the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 * @return
	 */
	public double getMaxParameter() {
		switch (type) {		
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
			case CONIC_DOUBLE_LINE:
			case CONIC_HYPERBOLA:
			case CONIC_PARABOLA:
				return Double.POSITIVE_INFINITY;
									
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return Kernel.PI_2;
				
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			default:
				return 0;		
		}		
	}
	
	public PathMover createPathMover() {
		return new PathMoverGeneric(this);
	}		
	
	public boolean isClosedPath() {
		switch (type) {			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return true;
	
			default:
				return false;		
		}		
	}
	
	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}   
	
	public boolean isTextValue() {
		return false;
	}
	
	public boolean isGeoConic() {
		return true;
	}
	
	public void setZero() {
		setCoeffs(1,0,1,0,0,0);
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}

}
