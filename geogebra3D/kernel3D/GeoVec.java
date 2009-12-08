/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;

/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public abstract class GeoVec extends GeoElement3D {
       
    public Ggb3DVector v;
    
    private int m_length;
	    
	
    public GeoVec(Construction c) {super(c);}  

    public GeoVec(Construction c, int n) {
    	this(c);
    	m_length = n;
    	v = new Ggb3DVector(n);
    	setDrawingMatrix(Ggb3DMatrix4x4.Identity());
    	setLabelMatrix(Ggb3DMatrix4x4.Identity());
    }  

    /** Creates new GeoVec with coordinates coords[] and label */
    public GeoVec(Construction c, double[] coords) {    	
    	this(c,coords.length);   
    	setCoords(coords);
    }                 
    
    /** Copy constructor */
    /*
    public GeoVec(Construction c, GeoVec vec) {   
    	super(c); 	
        set(vec);
    }
    */
    
    
    
    

	
	public void setCoords(double[] vals){
		v.set(vals);
		//Application.debug("v="+v.toString());
		
	}
	
	
	
	
	

	public void setCoords(Ggb3DMatrix v0){		
		setCoords(v0.get());		
	}

	public void setCoords(GeoVec vec){
		setCoords(vec.v);		
	}
	
	

	final public Ggb3DVector getCoords() {
		return v;
	}             
	
	
	
	public void translate(Ggb3DVector v0){
		
		Ggb3DVector v1 = v.add(v0).getColumn(1);
		setCoords(v1);		
	}

	

    
}
