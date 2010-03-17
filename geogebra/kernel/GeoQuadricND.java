package geogebra.kernel;


/** Abstract class describing quadrics in n-dimension space.
 * Extended by GeoConic, GeoQuadric3D
 * @author matthieu
 *
 */
public abstract class GeoQuadricND extends GeoElement {

	/** default constructor
	 * @param c
	 */
	public GeoQuadricND(Construction c) {
		super(c);
	}


	/** set the center and radius of the N-sphere
	 * @param M center
	 * @param radius
	 */
	abstract public void setNSphere(GeoPointInterface M, double radius);
	
	/** set the center and radius (as segment) of the N-sphere
	 * @param M center
	 * @param segment
	 */
	abstract public void setNSphere(GeoPointInterface M, GeoSegmentInterface segment);

}
