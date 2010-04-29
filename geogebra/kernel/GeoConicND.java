package geogebra.kernel;

import geogebra.Matrix.GgbVector;


/** Class for conic in any dimension.
 * 
 * @author matthieu
 *
 */
public abstract class GeoConicND extends GeoQuadricND implements LineProperties {
	
	
	// two Eigenvectors (unit vectors), set by setEigenvectors()
	protected GeoVec2D[] eigenvec = { new GeoVec2D(kernel, 1, 0), new GeoVec2D(kernel, 0, 1)};
	
	
	protected GgbVector getEigenvec(int i){
		return new GgbVector(eigenvec[i].getCoords());
	}


	/** default constructor
	 * @param c
	 * @param dimension
	 */
	public GeoConicND(Construction c, int dimension) {
		super(c, dimension);
	}

	

	/**
	 * Adds a point to the list of points that this conic passes through.
	 * @param p 
	 */
	abstract protected void addPointOnConic(GeoPointInterface p);
	
	
	
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
	
	
	
	
	
	
	
}
