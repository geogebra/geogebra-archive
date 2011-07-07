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
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.kernel.kernelND.GeoLineND;
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
    	
    	GeoLine3D ret = new GeoLine3D(cons);
    	ret.setIsIntersection(true);
    	
    	return ret;
    	
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
    
    
    public static int RESULTCATEGORY_GENERAL = 1;
    public static int RESULTCATEGORY_PARALLEL = 2;
    public static int RESULTCATEGORY_CONTAINED = 3;
    
    //TODO optimize it, using the coefficients of planes directly
    public static int getConfigPlanePlane(GeoCoordSys2D plane1, GeoCoordSys2D plane2) {
    	//normal vectors of plane1,2 are parallel
    	if (plane1.getDirectionInD3().crossProduct(plane2.getDirectionInD3()).isZero()) { 
    		//one normal vector is perpendicular to the difference of the two two origins 
    		if (Kernel.isZero(
    				plane2.getCoordSys().getOrigin().sub(plane1.getCoordSys().getOrigin())
    				.dotproduct(plane1.getDirectionInD3())
    				)) {
    			return RESULTCATEGORY_CONTAINED;
    		} else {
    			return RESULTCATEGORY_PARALLEL;
    		}	
    	} else {
    		return RESULTCATEGORY_GENERAL;
    	}
    }
 
    
    
	protected String getIntersectionTypeString(){
		return "IntersectionLineOfAB";
	}
 

}
