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

import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoPlaneND;




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
    	
    	Coords[] intersection = CoordMatrixUtil.intersectPlanes(cs1.getCoordSys().getMatrixOrthonormal(), cs2.getCoordSys().getMatrixOrthonormal());

    	// update line
    	GeoLine3D l = (GeoLine3D) getIntersection();
    	
    	l.setCoord(intersection[0], intersection[1]);
 
    	


    }
    
    public static GeoLine3D getIntersectPlanePlane (GeoCoordSys2D cs1, GeoCoordSys2D cs2) {

    	Coords[] intersection = CoordMatrixUtil.intersectPlanes(
    			cs1.getCoordSys().getMatrixOrthonormal(),
    			cs2.getCoordSys().getMatrixOrthonormal());

    	// update line
    	Construction c = cs1.toGeoElement().getConstruction();
    	c.getKernel().setSilentMode(true);
    	GeoLine3D l = new GeoLine3D(c, intersection[0], intersection[1]);
    	c.getKernel().setSilentMode(false);
    	return l;
    }
    
    
    
 
    
    
    
    
	
	

	protected String getIntersectionTypeString(){
		return "IntersectionLineOfAB";
	}
 

}
