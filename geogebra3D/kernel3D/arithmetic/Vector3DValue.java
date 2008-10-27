/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * VectorValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra3D.kernel3D.arithmetic;


import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra3D.kernel3D.GeoPoint3D;

/**
 *
 * @author  Markus + ggb3D
 * @version 
 */
public interface Vector3DValue extends ExpressionValue { 
    public GeoPoint3D getPoint();    
    //public int getMode(); // POLAR or CARTESIAN
    //public void setMode(int mode);       
}
