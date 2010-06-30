/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPointsSegment
 *
 * Created on 21. August 2003
 */

package geogebra3D.kernel3D;

import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCS1D2D extends AlgoIntersectCoordSys {


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectCS1D2D(Construction cons, String label, GeoCoordSys cs1, GeoCoordSys cs2) {

    	super(cons,label,cs1,cs2);
 
    }

	// sets the 1D coord sys as cs1
    protected void setCoordSys(GeoCoordSys cs1, GeoCoordSys cs2){
  
    	if (cs1 instanceof GeoCoordSys1D)
    		super.setCoordSys(cs1, cs2);
    	else
    		super.setCoordSys(cs2, cs1);
    	
    }
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    

    

    protected void compute(){



    	GeoCoordSys1D line = (GeoCoordSys1D) getCS1();
    	GeoCoordSys2D cs2D = (GeoCoordSys2D) getCS2();
    	
    	GgbCoordSys cs = line.getCoordSys();
    	GgbVector[] project = 
    		cs.getOrigin().projectPlaneThruV(cs2D.getCoordSys().getMatrixOrthonormal(), cs.getVx());
    	
    	GeoPoint3D p = (GeoPoint3D) getPoint();
    	
    	//check if the point is in the line (segment or half-line)
    	// and if the point is in the region (polygon, ...)
 		if (
 				line.isValidCoord(-project[1].get(3))
 				&&
 				cs2D.isInRegion(project[1].get(1),project[1].get(2))
 		){
			p.setCoords(project[0]);
		}else
			p.setUndefined();
    	


    }
    
    
    
    
    
    
 
    
    
    
    

	protected String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
  
 

}
