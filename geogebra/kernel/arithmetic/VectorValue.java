/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * VectorValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoVec2D;

/**
 *
 * @author  Markus
 * @version 
 */
public interface VectorValue extends ExpressionValue { 
    public GeoVec2D getVector();    
    public int getMode(); // POLAR or CARTESIAN
    public void setMode(int mode);       
}
