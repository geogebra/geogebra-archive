/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import java.util.HashSet;

/**
 * Helper class to evaluate expressions with GeoBoolean objects in it.
 * @see ExpressionNode.evaluate()
 * @author Markus Hohenwarter
 */
public class MyBoolean implements BooleanValue {
    
    private boolean value;
    
    public MyBoolean(boolean value) {
        this.value = value;
    }
    
    final public void setValue(boolean value) { 
    	this.value = value; 
    }
       
    public String toString() {    	
        return value ? "true" : "false";
    }

    public boolean isConstant() {
        return true;
    }

    final public boolean isLeaf() {
        return true;
    }
    
    public void resolveVariables() {    	
    }

    final public boolean isNumberValue() {
        return false;
    }

    final public boolean isVectorValue() {
        return false;
    }
    
    final public boolean isBooleanValue() {
        return true;
    }

    public boolean isPolynomialInstance() {
        return false;
    }

    public boolean isTextValue() {
        return false;
    }

    public ExpressionValue deepCopy() {
        return new MyBoolean(value);
    }

    public ExpressionValue evaluate() {
        return this;
    }

    public HashSet getVariables() {
        return null;
    }

    final public String toValueString() {
        return toString();
    }
    
    final public String toLaTeXString(boolean symbolic) {
    	return toString();
    }
    
    final public boolean isExpressionNode() {
        return false;
    } 
    
    final public boolean isVariable() {
        return false;
    }   
     
    final public boolean isGeoElement() {
       return false;
    }
    
    public boolean isListValue() {
        return false;
    }

    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	final public MyBoolean getMyBoolean() {		
		return new MyBoolean(value);
	}

	final public boolean getBoolean() {		
		return value;
	}
}
