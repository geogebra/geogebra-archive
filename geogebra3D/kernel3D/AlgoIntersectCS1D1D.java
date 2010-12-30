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
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.kernel.kernelND.GeoLineND;




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
    public AlgoIntersectCS1D1D(Construction cons, String label, GeoLineND cs1, GeoLineND cs2) {

    	super(cons,label, (GeoElement) cs1, (GeoElement) cs2);
 
    }
    
 

    
    
    
    
    ///////////////////////////////////////////////
    // COMPUTE
    

    protected void compute(){
    	
    	if (!outputIsDefined())
    		return;
    	
    	GeoLineND line1 = (GeoLineND) getCS1();
    	GeoLineND line2 = (GeoLineND) getCS2();
    	
    	GgbVector o1 = line1.getPointInD(3, 0);
    	GgbVector d1 = line1.getPointInD(3, 1).sub(o1);
    	GgbVector o2 = line2.getPointInD(3, 0);
       	GgbVector d2 = line2.getPointInD(3, 1).sub(o2);
           	

    	GgbVector[] project = GgbMatrixUtil.nearestPointsFromTwoLines(
    			o1,d1,o2,d2
    	);
    	
    	GeoPoint3D p = (GeoPoint3D) getIntersection();
    	
    	if (project==null)
    		p.setUndefined(); //TODO infinite point
    	else if (project[0].equalsForKernel(project[1], Kernel.STANDARD_PRECISION)){
    		if (project[2].get(1) > line1.getMinParameter()
    				&& project[2].get(1) < line1.getMaxParameter() 				
    				&& project[2].get(2) > line2.getMinParameter()
    				&& project[2].get(2) < line2.getMaxParameter()
    				)   		
    			p.setCoords(project[0]);
    		else
    			p.setUndefined();
    	}
    	else
    		p.setUndefined();
    	
    }
    
    
    
    
    
    
 
    
    
    
    

	public String getClassName() {
    	
    	return "AlgoIntersectCoordSys";
	}

	
	
  
 

}
