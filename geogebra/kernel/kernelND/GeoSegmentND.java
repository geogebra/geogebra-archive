package geogebra.kernel.kernelND;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoElement;

import java.awt.Color;

/**
 * Simple interface used to join GeoSegment and GeoSegment3D
 * 
 * @author ggb3D
 *
 */
public interface GeoSegmentND extends GeoLineND {


	void setLabel(String string);


	void setObjColor(Color objectColor);

	void setEuclidianVisible(boolean visible);

	void update();

	void setLineType(int type);

	void setLineThickness(int th);

	
	double getLength();
	
	public GeoElement getStartPointAsGeoElement();

	public GeoElement getEndPointAsGeoElement();

	
	////////////////////////////////////////////////
	// Path Interface
	
	boolean isOnPath(GeoPointND p, double eps);
	
	void pointChanged(GeoPointND p);
	
	public void pathChanged(GeoPointND PI);

	
	/**
	 * return the x-coordinate of the point on the segment according to the parameter value
	 * @param parameter the parameter
	 * @return the x-coordinate of the point
	 */
	public double getPointX(double parameter);
	
	/**
	 * return the y-coordinate of the point on the segment according to the parameter value
	 * @param parameter the parameter
	 * @return the y-coordinate of the point
	 */
	public double getPointY(double parameter);


	boolean isEuclidianVisible();

	
	// highlighting when segment of a polygon or polyhedron
	/**
	 * sets the highlighting ancestor
	 * @param geo 
	 * 
	 */
	public void setHighlightingAncestor(GeoElement geo);
	
	/**
	 * 
	 * @return the highlighting ancestor
	 */
	public GeoElement getHighlightingAncestor();

}
