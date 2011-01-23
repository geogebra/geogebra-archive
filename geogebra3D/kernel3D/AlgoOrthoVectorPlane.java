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
import geogebra.kernel.kernelND.GeoCoordSys2D;
import geogebra.main.Application;




/**
 *
 * @author  ggb3D
 * @version 
 * 
 * Calculate the ortho vector of a plane (or polygon, ...)
 * 
 */
public class AlgoOrthoVectorPlane extends AlgoElement3D {

	
	//input
	/** plane */
	private GeoCoordSys2D plane;
	
	//output
	/** ortho vector */
	private GeoVector3D vector;


    /** Creates new AlgoIntersectLinePlane 
     * @param cons the construction
     * @param label name of point
     */    
    AlgoOrthoVectorPlane(Construction cons, String label, GeoCoordSys2D plane) {

    	super(cons);


    	this.plane = plane;
    	
    	vector = new GeoVector3D(cons);
  
    	setInputOutput(new GeoElement[] {(GeoElement) plane}, new GeoElement[] {vector});

    	vector.setLabel(label);
 
    }
    
 



    
    
    
    
    /**
     * return the ortho vector
     * @return the ortho vector
     */   
    public GeoVector3D getVector() {
        return vector;
    }
   
    
    
    

    ///////////////////////////////////////////////
    // COMPUTE
    
    
    
    
    protected void compute(){
    	
    	
    	
    	vector.setCoords(plane.getCoordSys().getVz());
    	
    	

    
    }
    
    
    
    

	public String getClassName() {
    	
    	return "AlgoOrthoVectorPlane";
	}

	
	
	
    final public String toString() {
        return app.getPlain("VectorPerpendicularToA", ((GeoElement) plane).getLabel());

    }  
  
 

}
