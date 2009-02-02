/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * GeoVec3D.java
 *
 * Created on 31. August 2001, 11:22
 */

package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoVec3D;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public abstract class GeoVec4D extends GeoVec {

    public GeoVec4D(Construction c) {  super(c);  }
    public GeoVec4D(Construction c, int n) {super(c,n);}  

    /** Creates new GeoVec4D with coordinates (x,y,z,w) and label */
    public GeoVec4D(Construction c, double x, double y, double z, double w) {  
     	super(c,new double[] {x,y,z,w});   
       
    }                 
    
    /** Copy constructor */
    public GeoVec4D(Construction c, GeoVec4D v) {   
    	super(c); 	
        set(v);
    }
    
    public void setCoords(double x, double y, double z, double w){
     	setCoords(new double[] {x,y,z,w});
    }
    

    public double getX(){
    	return getCoords().get(1);
    }
    public double getY(){
    	return getCoords().get(2);
    }
    public double getZ(){
    	return getCoords().get(3);
    }
    public double getW(){
    	return getCoords().get(4);
    }
    

    /** Calculates the line through the point A with direction v.
     * The result is stored in g.
     */
    
    final public static void lineThroughPointVector(GeoPoint3D A, GeoVec4D v, GeoRay3D g) {
    	// note: this could be done simply using cross(A, v, g)
    	// but we want to avoid large coefficients in the line
    	// and we want v to be the direction vector of the line
        Application.debug("GeoVec4D : lineThroughPointVector");
    	
 ///   	if (A.isInfinite()) {// A is direction
//	TODO		g.setUndefined();
 //   	}
 //   	else 
//   	{ // through point A
        Ggb3DVector B = new Ggb3DVector( new double[]{
        		   A.getX()+v.getX(),
        		   A.getY()+v.getY(),
        		   A.getZ()+v.getZ(),
        		   v.getW()});
			// v is direction
		    g.setCoord(A.getCoords(), B);
// 	}        
    }      
}









