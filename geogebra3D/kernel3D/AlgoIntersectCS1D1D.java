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

import geogebra.Matrix.GgbMatrixUtil;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectCS1D1D extends AlgoIntersectCoordSys {

	


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectCS1D1D(Construction cons, String label, GeoCoordSys cs1, GeoCoordSys cs2) {

    	super(cons,label,cs1,cs2);
 
    }
    
 

    
    
    
    
    ///////////////////////////////////////////////
    // COMPUTE
    

    protected void compute(){
    	
    	if (!outputIsDefined())
    		return;
    	
    	GeoCoordSys1D line1 = (GeoCoordSys1D) getCS1();
    	GeoCoordSys1D line2 = (GeoCoordSys1D) getCS2();
    	

    	GgbVector[] project = GgbMatrixUtil.nearestPointsFromTwoLines(
    			line1.getCoordSys().getOrigin(), 
    			line1.getCoordSys().getVx(),
    			line2.getCoordSys().getOrigin(), 
    			line2.getCoordSys().getVx()
    	);
    	
    	GeoPoint3D p = (GeoPoint3D) getPoint();
    	
    	if (project==null)
    		p.setUndefined(); //TODO infinite point
    	else if (project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
    		if (line1.isValidCoord(project[2].get(1)) && line2.isValidCoord(project[2].get(2)))
    			p.setCoords(project[0]);
    		else
    			p.setUndefined();
    	}
    	else
    		p.setUndefined();
    	
    }
    
    
    
    
    
    
 
    
    
    
    

	protected String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
  
 

}
