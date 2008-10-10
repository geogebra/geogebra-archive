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
    
    
}
