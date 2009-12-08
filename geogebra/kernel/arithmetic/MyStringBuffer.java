/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;

import java.util.HashSet;

/**
 * Helper class to evaluate expressions  with GeoText objects in it.
 * @see ExpressionNode.evaluate()
 * @author Markus Hohenwarter
 */
public class MyStringBuffer extends ValidExpression implements TextValue {
    
    private StringBuilder sb;
    
    public MyStringBuffer(String str) {
        sb = new StringBuilder(str);
    }
    
    public void append(String str) {
        sb.append(str);
    }
    
    public void insert(int pos, String str) {
        sb.insert(pos, str);
    }
    
    public String toString() {
        StringBuilder temp = new StringBuilder();
        temp.append("\"");
        temp.append(sb);
        temp.append("\"");
        return temp.toString();
    }
    
    public void resolveVariables() {    	
    }


    public boolean isConstant() {
        return true;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean isNumberValue() {
        return false;
    }

    public boolean isVectorValue() {
        return false;
    }
    	
	public boolean isBooleanValue() {
		return false;
	}

    public boolean isPolynomialInstance() {
        return false;
    }

    public boolean isTextValue() {
        return true;
    }

    public ExpressionValue deepCopy(Kernel kernel) {
        return getText();
    }

    public ExpressionValue evaluate() {
        return this;
    }

    public HashSet getVariables() {
        return null;
    }

    final public String toValueString() {
        return sb.toString();
    }
    
    final public String toLaTeXString(boolean symbolic) {
        return sb.toString();
    }

    public MyStringBuffer getText() {
        return new MyStringBuffer(sb.toString());
    }
    
    final public boolean isExpressionNode() {
        return false;
    }       
    
    public boolean isListValue() {
        return false;
    }
    
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
}
