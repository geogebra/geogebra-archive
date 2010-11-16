/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.ArrayList;

/**
 * Path mover for locus
 */
public class PathMoverLocus extends PathMoverGeneric {		
	
	private ArrayList<MyPoint> myPointList;
	
	/**
	 * Creates new path mover for given locus
	 * @param locus
	 */
	public PathMoverLocus(GeoLocus locus) {		
		super(locus);
		myPointList = locus.getMyPointList();
	}
	
	protected void calcPoint(GeoPoint p) {
		double param = curr_param;		
		PathParameter pp = p.getPathParameter();
		pp.t = param;
		
		// PATH MOVER CHANGED PARAMETER (see PathMoverGeneric.calcPoint())
		// get points left and right of path parameter				
		int leftIndex = (int) Math.max(0, Math.floor(param));
		int rightIndex = (int) Math.min(myPointList.size()-1, Math.ceil(param));
		MyPoint leftPoint = (MyPoint) myPointList.get(leftIndex);
		MyPoint rightPoint = (MyPoint) myPointList.get(rightIndex);				
				
		// interpolate between leftPoint and rightPoint
		double param1 = (param - leftIndex);
		double param2 = 1.0 - param1;
		p.x = param2 * leftPoint.x + param1 * rightPoint.x;
		p.y = param2 * leftPoint.y + param1 * rightPoint.y;
		p.z = 1.0;	
		
		p.updateCoords();
	}		

}
