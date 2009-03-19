package geogebra.kernel;

import java.awt.Color;

/**
 * Simple interface used to join GeoSegment and GeoSegment3D
 * 
 * @author ggb3D
 *
 */
public interface GeoSegmentInterface {


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
	
	boolean isOnPath(GeoPointInterface p, double eps);
	
	void pointChanged(GeoPointInterface p);

	
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





	

}
