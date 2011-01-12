package geogebra.kernel.kernelND;

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.CoordMatrix;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.LineProperties;
import geogebra.kernel.Path;
import geogebra.kernel.PathMover;
import geogebra.kernel.PathMoverGeneric;
import geogebra.kernel.PathNormalizer;
import geogebra.kernel.PathParameter;
import geogebra.util.MyMath;


/** Class for conic in any dimension.
 * 
 * @author matthieu
 *
 */
public abstract class GeoConicND extends GeoQuadricND implements LineProperties {
	
	

	/** single point type*/    
	public static final int CONIC_SINGLE_POINT = QUADRIC_SINGLE_POINT;
	/** intersecting lines type*/
	public static final int CONIC_INTERSECTING_LINES = QUADRIC_INTERSECTING_LINES;
	/** ellipse type*/
	public static final int CONIC_ELLIPSE = QUADRIC_ELLIPSOID;
	/** circle type*/
	public static final int CONIC_CIRCLE = QUADRIC_SPHERE;
	/** hyperbola type*/
	public static final int CONIC_HYPERBOLA = QUADRIC_HYPERBOLOID;
	/** empty conic type*/
	public static final int CONIC_EMPTY = QUADRIC_EMPTY;
	/** double line type*/
	public static final int CONIC_DOUBLE_LINE = QUADRIC_DOUBLE_LINE;
	/** parallel lines type */
	public static final int CONIC_PARALLEL_LINES = QUADRIC_PARALLEL_LINES;
	/** parabola type */
	public static final int CONIC_PARABOLA = QUADRIC_PARABOLOID;
	/** line type */
	public static final int CONIC_LINE = QUADRIC_LINE;

	

	protected GeoPoint singlePoint;

	/** lines of which this conic consists in case it's degenerate */
	public GeoLine[] lines;
	
	// two Eigenvectors (unit vectors), set by setEigenvectors()
	public GeoVec2D[] eigenvec = { new GeoVec2D(kernel, 1, 0), new GeoVec2D(kernel, 0, 1)};
	
	
	/**
	 * 
	 * @param i
	 * @return eigen vector in native dimension of the conic
	 * 	 */
	protected Coords getEigenvec(int i){
		return new Coords(eigenvec[i].getCoords());
	}
	
	/**
	 * 
	 * @param i
	 * @return eigen vector in dimension 3
	 */
	 abstract public Coords getEigenvec3D(int i);

	 /**
	  * If 2D conic, return null (xOy plane)
	  * @return coord sys where the conic lies
	  */
	 public CoordSys getCoordSys(){
		 return null;
	 }

	/** default constructor
	 * @param c
	 * @param dimension
	 */
	public GeoConicND(Construction c, int dimension) {
		super(c, dimension);
	}

	
	
	
	/**
	 * @return the matrix representation of the conic in its 2D sub space
	 */
	protected CoordMatrix getGgbMatrix(double[] vals){
		//TODO
		return null;
	}
	
	
	/**
	 * sets the matrix values from the symmetric matrix m
	 * @param m
	 */
	protected void setMatrix(CoordMatrix m){
		//TODO
	}
	
	
	

	/**
	 * Adds a point to the list of points that this conic passes through.
	 * @param p 
	 */
	public abstract void addPointOnConic(GeoPointND p);
	
	
	
	/**
	 * makes this conic a circle with midpoint M and radius r
	 * @param M 
	 * @param r 
	 */
	final public void setCircle(GeoPoint M, double r) {
		
		setSphereND(M, r);
		
	}
	
	
	/**
	 * makes this conic a circle with midpoint M through Point P
	 * @param M 
	 * @param P 
	 */
	abstract public void setCircle(GeoPoint M, GeoPoint P);
	
	
	
	
	
	
	

	//////////////////////////////////////
	// PATH INTERFACE
	//////////////////////////////////////
	
	 public boolean isPath(){
		 return true;
	 }

	public void pointChanged(GeoPointND P) {

		Coords coords = P.getCoordsInD2(getCoordSys());
		PathParameter pp = P.getPathParameter();

		pointChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false,getCoordSys());
	}
	
	
	
	public void pointChanged(Coords P, PathParameter pp) {
		
		double px, py;	
		pp.setPathType(type);
			
		switch (type) {
			case CONIC_EMPTY:
				P.setX(Double.NaN);
				P.setY(Double.NaN);
				P.setZ(Double.NaN);
			break;
			
			case CONIC_SINGLE_POINT:
				P.setX(singlePoint.x);
				P.setY(singlePoint.y);
				P.setZ(singlePoint.z);
			break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				/* 
				 * For line conics, we use the parameter ranges 
				 *   first line: t = (-1, 1)
				 *   second line: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   first line: s = t /(1 - abs(t)) 
				 *   second line:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the line's path parameter s
				 */
				
				// choose closest line
				boolean firstLine = lines[0].distanceHom(P) <= lines[1].distanceHom(P);
				GeoLine line = firstLine ? lines[0] : lines[1];
				
				// compute line path parameter
				line.doPointChanged(P,pp);
							
				// convert line parameter to (-1,1)
				pp.setT(PathNormalizer.inverseInfFunction(pp.getT()));
				if (!firstLine) {
					pp.setT(pp.getT() + 2);// convert from (-1,1) to (1,3)									
				}				
			break;
			
			case CONIC_LINE:
			case CONIC_DOUBLE_LINE:
				lines[0].doPointChanged(P,pp);
			break;
			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:			
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);				

				// calc parameter 
				px = P.getX() / P.getZ();
				py = P.getY() / P.getZ();		
				
				// relation between the internal parameter t and the angle theta:
				// t = atan(a/b tan(theta)) where tan(theta) = py / px
				pp.setT( Math.atan2(halfAxes[0]*py, halfAxes[1]*px));											
				
				// calc Point on conic using this parameter
				P.setX( halfAxes[0] * Math.cos(pp.getT()));	
				P.setY( halfAxes[1] * Math.sin(pp.getT()));												
				P.setZ( 1.0);
				
				//	transform back to real world coord system
				coordsEVtoRW(P);				
			break;			
			
			case CONIC_HYPERBOLA:
				/* 
				 * For hyperbolas, we use the parameter ranges 
				 *   right branch: t = (-1, 1)
				 *   left branch: t = (1, 3)
				 * and get this from  s = (-inf, inf) using		
				 *   right branch: s = t /(1 - abs(t)) 
				 * where we use the parameter form
				 *   (a*cosh(s), b*sinh(s))
				 * for the right branch of the hyperbola.
				 */ 
				
				// transform to eigenvector coord-system
				coordsRWtoEV(P);
				px = P.getX() / P.getZ();
				py = P.getY() / P.getZ();	
				
				// calculate s in (-inf, inf) and keep py				
				double s = MyMath.asinh(py / halfAxes[1]);
				P.setX( halfAxes[0] * MyMath.cosh(s));	
				P.setY( py);
				P.setZ( 1.0);

				// compute t in (-1,1) from s in (-inf, inf)
				pp.setT(PathNormalizer.inverseInfFunction(s));	
				if (px < 0) { // left branch									
					pp.setT( pp.getT() + 2); // convert (-1,1) to (1,3)
					P.setX( -P.getX());
				}		

				// transform back to real world coord system
				coordsEVtoRW(P);													
			break;																			
			
			case CONIC_PARABOLA:
				//	transform to eigenvector coord-system
				coordsRWtoEV(P);
				
				// keep py
				py = P.getY() / P.getZ();
				pp.setT(  py / p);
				P.setX( p * pp.getT()  * pp.getT()  / 2.0);
				P.setY( py);
				P.setZ( 1.0); 									
				
				// transform back to real world coord system
				coordsEVtoRW(P);		
			break;
		}		
	}
	
	
	
	public void pathChanged(GeoPointND P) {
		Coords coords = P.getCoordsInD2(getCoordSys());
		PathParameter pp = P.getPathParameter();

		pathChanged(coords, pp);

		P.setCoords2D(coords.getX(), coords.getY(), coords.getZ());
		P.updateCoordsFrom2D(false,getCoordSys());
	}
	

	public void pathChanged(Coords P, PathParameter pp) {
		
		
		// if type of path changed (other conic) then we
		// have to recalc the parameter with pointChanged()
		if (pp.getPathType() != type || Double.isNaN(pp.getT())) {		
			pointChanged(P,pp);
			return;
		}
		
		switch (type) {
			case CONIC_EMPTY:
				P.setX(Double.NaN);
				P.setY(Double.NaN);
				P.setZ(Double.NaN);
				break;
			
			case CONIC_SINGLE_POINT:
				P.setX(singlePoint.x);
				P.setY(singlePoint.y);
				P.setZ(singlePoint.z);
				break;
			
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				/* 
				 * For line conics, we use the parameter ranges 
				 *   first line: t = (-1, 1)
				 *   second line: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   first line: s = t /(1 - abs(t)) 
				 *   second line:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the line's path parameter s
				 */ 
				double pathParam = pp.getT();
				boolean leftBranch = pathParam > 1;
				pp.setT( leftBranch ? pathParam - 2 : pathParam);
				// convert from (-1,1) to (-inf, inf) line path parameter
				pp.setT( pp.getT() /(1 - Math.abs(pp.getT())));
				if (leftBranch) {
					lines[1].pathChanged(P,pp);					 
				} else {
					lines[0].pathChanged(P,pp);										
				}
						
				// set our path parameter again
				pp.setT( pathParam);
				break;
				
			case CONIC_LINE:
			case CONIC_DOUBLE_LINE:
				lines[0].pathChanged(P,pp);	
				break;
			
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:						
				// calc Point on conic using this parameter (in eigenvector space)
				P.setX( halfAxes[0] * Math.cos(pp.getT()));	
				P.setY( halfAxes[1] * Math.sin(pp.getT()));												
				P.setZ( 1.0);		
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;			
			
			case CONIC_HYPERBOLA:			
				/* 
				 * For hyperbolas, we use the parameter ranges 
				 *   right branch: t = (-1, 1)
				 *   left branch: t = (1, 3)
				 * and convert this to s = (-inf, inf) using		
				 *   right branch: s = t /(1 - abs(t)) 
				 *   left branch:  s = (t-2) /(1 - abs(t-2))
				 * which allows us to use the parameter form
				 *   (a*cosh(s), b*sinh(s))
				 * for the right branch of the hyperbola.
				 */ 
				leftBranch = pp.getT() > 1;
				double t = leftBranch ? pp.getT() - 2 : pp.getT();
				double s = t /(1 - Math.abs(t));
				
				P.setX( halfAxes[0] * MyMath.cosh(s));
				P.setY( halfAxes[1] * MyMath.sinh(s));
				P.setZ( 1.0);				
				if (leftBranch) P.setX( -P.getX());
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;																			
			
			case CONIC_PARABOLA:
				P.setY( p * pp.getT());				
				P.setX( P.getY() * pp.getT()  / 2.0);				
				P.setZ( 1.0);
				
				// transform back to real world coord system
				coordsEVtoRW(P);
				break;
		}
	}
	
	
	
	
	

	
	/**
	 * Returns the largest possible parameter value for this path
	 * @return the largest possible parameter value for this
	 * path (may be Double.POSITIVE_INFINITY)
	 */
	public double getMaxParameter() {
		switch (type) {
			case CONIC_DOUBLE_LINE:			
			case CONIC_PARABOLA:
			case CONIC_LINE:
				return Double.POSITIVE_INFINITY;
									
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return Math.PI;
				
			case CONIC_HYPERBOLA:
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				// For hyperbolas and line conics, we use the parameter ranges 
				//   right branch: t = (-1, 1)
				//   left branch: t = (1, 3)
				return 3;
				
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			default:
				return 0;		
		}		
	}
	
	
	
	
	
	
	/**
	 * Returns the smallest possible parameter value for this path
	 * @return the smallest possible parameter value for this
	 * path (may be Double.NEGATIVE_INFINITY)
	 */
	public double getMinParameter() {
		switch (type) {		
			case CONIC_PARABOLA:
			case CONIC_DOUBLE_LINE:
			case CONIC_LINE:
				return Double.NEGATIVE_INFINITY;
				
			case CONIC_HYPERBOLA:
			case CONIC_INTERSECTING_LINES:
			case CONIC_PARALLEL_LINES:
				// For hyperbolas and line conics, we use the parameter ranges 
				//   right branch: t = (-1, 1)
				//   left branch: t = (1, 3)
				return -1;
															
			case CONIC_CIRCLE:
			case CONIC_ELLIPSE:
				return -Math.PI;
				
			case CONIC_EMPTY:										
			case CONIC_SINGLE_POINT:
			default:
				return 0;		
		}		
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
	
	public boolean isOnPath(GeoPointND PI, double eps) {
		
		GeoPoint P = (GeoPoint) PI;
		
		if (P.getPath() == this)
			return true;
		
		return isOnFullConic(P, eps);
	}
	

	
	 /** 
	 * states wheter P lies on this conic or not. Note: this method
	 * is not overwritten by subclasses like isIntersectionPointIncident()
	 * @return true P lies on this conic
	 * @param P
	 * @param eps precision
	 */
	public final boolean isOnFullConic(GeoPointND P, double eps) {
		if (!P.isDefined()) return false;
		
		return isOnFullConic(P.getCoordsInD(2), eps);
	}
	
	
	final boolean isOnFullConic(Coords P, double eps) {						
		switch (type) {	
			 case GeoConic.CONIC_SINGLE_POINT:  
				Coords singlePointCoords = new Coords(singlePoint.x,singlePoint.y,singlePoint.z);
	            return P.distance(singlePointCoords) < eps;                             
	            
	        case GeoConic.CONIC_INTERSECTING_LINES:  
	        case GeoConic.CONIC_DOUBLE_LINE: 
	        case GeoConic.CONIC_PARALLEL_LINES:                
	            return lines[0].isOnFullLine(P, eps) || lines[1].isOnFullLine(P, eps);	
	            
	        case GeoConic.CONIC_LINE:                
	            return lines[0].isOnFullLine(P, eps);	    
	        
	        case GeoConic.CONIC_EMPTY:
	        	return false;
		}        
		
		// if we get here let's handle the remaining cases
				
     	// remember coords of P
		double Px = P.getX();
		double Py = P.getY();
		double Pz = P.getZ();
														
		// convert P to eigenvector coord system
		coordsRWtoEV(P);	
		double px = P.getX() / P.getZ();
		double py = P.getY() / P.getZ();
		
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
		P.setX( Px); 
		P.setY( Py); 
		P.setZ( Pz);
		return result;				
	}
	
	
	
	
	
	/**
	 * Transforms coords of point P from Eigenvector space to real world space.
	 * @param P 2D point in EV coords
	 */
	final void coordsEVtoRW(Coords P) {
		// rotate by alpha
		double px = P.getX();
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		P.setX(px * eigenvec0.getX() + P.getY() * eigenvec1.getX());
		P.setY(px * eigenvec0.getY() + P.getY() * eigenvec1.getY()); 
	
		// translate by b
		Coords b = getMidpoint();
		P.setX(P.getX() + P.getZ() * b.getX());
		P.setY(P.getY() + P.getZ() * b.getY());
	}
	
	/**
	 * Transforms coords of point P from real world space to Eigenvector space. 
	 * @param P 2D point in EV coords
	 */
	private void coordsRWtoEV(Coords P) {
		
		Coords b = getMidpoint();
		
		// translate by -b
		P.setX(P.getX() - P.getZ() * b.getX());
		P.setY(P.getY() - P.getZ() * b.getY());
		
		// rotate by -alpha
		double px = P.getX();	
		Coords eigenvec0 = getEigenvec(0);
		Coords eigenvec1 = getEigenvec(1);
		P.setX(px * eigenvec0.getX() + P.getY() * eigenvec0.getY());
		P.setY(px * eigenvec1.getX() + P.getY() * eigenvec1.getY());
	}
	
	
}
