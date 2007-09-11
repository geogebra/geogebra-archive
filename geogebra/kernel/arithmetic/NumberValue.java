/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

/*
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;

/**
 *
 * @author  Markus
 * @version 
 */
public interface NumberValue extends ExpressionValue { 
    public MyDouble getNumber();
    public double getDouble();    
    public GeoElement toGeoElement();
}

