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

import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCS2D2D extends AlgoIntersectCoordSys {


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    public AlgoIntersectCS2D2D(Construction cons, String label, GeoCoordSys cs1, GeoCoordSys cs2) {

    	super(cons,label,(GeoElement) cs1, (GeoElement) cs2);
 
    }

    
    protected GeoElement3D createIntersection(Construction cons){
    	
    	return new GeoLine3D(cons);
    	
    }

    ///////////////////////////////////////////////
    // COMPUTE
    
    

    

    protected void compute(){

    	GeoCoordSys2D cs1 = (GeoCoordSys2D) getCS1();
    	GeoCoordSys2D cs2 = (GeoCoordSys2D) getCS2();
    	
    	// compute direction vector
    	Coords vn1 = cs1.getCoordSys().getNormal();
    	Coords vn2 = cs2.getCoordSys().getNormal();
    	Coords v = vn1.crossProduct(vn2);
    	
    	// compute origin:
    	// projection of first plane origin on second plane
    	// direction orthogonal to v and colinear to first plane
    	Coords[] project = 
    		cs1.getCoordSys().getOrigin().projectPlaneThruV(
    				cs2.getCoordSys().getMatrixOrthonormal(), 
    			vn1.crossProduct(v));
    	
    	// update line
    	GeoLine3D l = (GeoLine3D) getIntersection();
    	
    	l.setCoord(project[0], v);
 
    	


    }
    
    
    
    
    
    
 
    
    
    
    

	public String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
  
 

}
