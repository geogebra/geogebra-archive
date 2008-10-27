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
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbVector;

/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public abstract class GeoVec extends GeoElement3D {
       
    public GgbVector v;
	    
	
    public GeoVec(Construction c) {super(c);}  

    public GeoVec(Construction c, int n) {
    	this(c);
    	v = new GgbVector(n);
    }  

    /** Creates new GeoVec with coordinates coords[] and label */
    public GeoVec(Construction c, double[] coords) {    	
    	super(c);     	
    	v = new GgbVector(coords);
    	//v.set(Double.NaN);
    }                 
    
    /** Copy constructor */
    public GeoVec(Construction c, GeoVec vec) {   
    	super(c); 	
        set(vec);
    }
    
    
    
    
	public void setCoords(GgbVector v0){
		v.set(v0);
	}
	
	public void setCoords(double[] vals){
		v.set(vals);
	}
	

	public void setCoords(GgbMatrix v0){		
		setCoords(v0.v());		
	}

	public void setCoords(GeoVec vec){
		setCoords(vec.v);		
	}
	
	

	final public GgbVector getCoords() {
		return v;
	}             
	
	
	
	public void translate(GgbVector v0){
		
		GgbVector v1 = v.add(v0).getColumn(1);
		setCoords(v1);		
	}

	

    
}
