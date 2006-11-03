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
 * NumberValue.java
 *
 * Created on 03. Oktober 2001, 10:09
 */

package geogebra.kernel.arithmetic;

import java.util.HashSet;

/**
 *
 * @author  Markus
 * @version 
 */
public interface ExpressionValue {      
    public boolean isConstant();    
    public boolean isLeaf();
    public boolean isNumberValue();
	public boolean isVectorValue();	
	public boolean isListValue();
	public boolean isBooleanValue();
	public boolean isPolynomialInstance();
	public boolean isTextValue();
	public boolean isExpressionNode();
	public boolean isGeoElement();
	public boolean isVariable();
	public boolean contains(ExpressionValue ev);
	public ExpressionValue deepCopy();
    public ExpressionValue evaluate();
    public HashSet getVariables();   
    public String toValueString();
    public String toLaTeXString(boolean symbolic);   
    public void resolveVariables();
}

