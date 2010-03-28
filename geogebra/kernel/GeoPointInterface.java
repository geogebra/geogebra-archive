package geogebra.kernel;

import geogebra.Matrix.GgbVector;



/**
 * 
 * @author ggb3D
 *
 * interface for stuff common to 2D and 3D points
 *
 */

public interface GeoPointInterface {

	
	/** Returns whether this point has changeable numbers as coordinates */
	public boolean hasChangeableCoordParentNumbers();

	public void setLabel(String string);

	public boolean isLabelSet();

	public String getLabel();

	public boolean isInfinite();

	public boolean showInEuclidianView();

	public void remove();
	
	public boolean getSpreadsheetTrace();

	public RegionParameters getRegionParameters();

	public void updateCoords2D();

	public double getX2D();
	
	public double getY2D();

	public void updateCoordsFrom2D(boolean b);

	public boolean isPointOnPath();

	public int getMode();
	
	public boolean isFinite();

	public void set(GeoPointInterface p);

	/** return the coordinates of the vector (this,Q) 
	 * @param Q ending point
	 * @return coords of the vector */
	public double[] vectorTo(GeoPointInterface Q);
	
	
	public GgbVector getInhomCoords();
	
	public double distance(GeoPointInterface P);
	

}
