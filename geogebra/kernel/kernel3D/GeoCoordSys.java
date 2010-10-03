package geogebra.kernel.kernel3D;

import geogebra.Matrix.GgbCoordSys;

/** Simple interface for elements that have a coord sys
 * @author matthieu
 *
 */
public interface GeoCoordSys {
	
	/** set the coordinate system
	 * @param cs the coordinate system
	 */
	 //public void setCoordSys(CoordSys cs);
	 
	/** return the coordinate system
	 * @return the coordinate system
	 */
	public GgbCoordSys getCoordSys();

}
