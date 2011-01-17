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

import geogebra.Matrix.CoordMatrix;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.kernelND.GeoCoordSys;
import geogebra.main.Application;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the GeoPoint3D intersection of two coord sys (eg line and plane).
 * 
 */
public class AlgoIntersectPlaneQuadric extends AlgoElement3D {

	
	//inputs
	/** plane */
	private GeoPlane3D plane;
	/** second coord sys */
	private GeoQuadric3D quadric;
	
	//output
	/** intersection */
	private GeoConic3D conic;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     * @param cs1 first coord sys
     * @param cs2 second coord sys
     */    
    AlgoIntersectPlaneQuadric(Construction cons, String label, GeoPlane3D plane, GeoQuadric3D quadric) {

    	super(cons);


    	this.plane = plane;
    	this.quadric = quadric;
    	
    	conic = new GeoConic3D(cons);
  
    	setInputOutput(new GeoElement[] {plane,quadric}, new GeoElement[] {conic});

    	conic.setLabel(label);
 
    }
    
 



    
    
    
    
    /**
     * return the intersection
     * @return the intersection
     */   
    public GeoConic3D getConic() {
        return conic;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    protected void compute(){
    	//Application.debug("quadric=\n"+quadric.getSymetricMatrix()+"\nplan=\n"+plane.getParametricMatrix().toString());	 
    
    	CoordMatrix qm = quadric.getSymetricMatrix();
    	CoordMatrix pm = plane.getParametricMatrix();
    	CoordMatrix pmt = pm.transposeCopy();
    	
    	//sets the conic matrix from plane and quadric matrix
    	CoordMatrix cm = pmt.mul(qm).mul(pm);
    	
    	//Application.debug("conic=\n"+cm.toString());
    	conic.setCoordSys(plane.getCoordSys());
    	conic.setMatrix(cm);
    
    }
    
    
    
    

	public String getClassName() {
    	
    	return "AlgoIntersectPlaneQuadric";
	}

	
	
	
    final public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(app.getPlain("Intersection",plane.getLabel(),quadric.getLabel()));
        
        return sb.toString();
    }   
  
 

}
