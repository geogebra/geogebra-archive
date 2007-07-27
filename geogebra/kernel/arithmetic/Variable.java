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
 * VarString.java
 *
 * Created on 18. November 2001, 14:49
 */

package geogebra.kernel.arithmetic;

import geogebra.MyParseError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.util.HashSet;


/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class Variable implements ExpressionValue {

    private String name;
    private Kernel kernel;
        
    /** Creates new VarString */
    public Variable(Kernel kernel, String name) {
        this.name = name;
        this.kernel = kernel;
    }      
    
	public ExpressionValue deepCopy() {
		return new Variable(kernel, name);
	}   
    
    public String getName() { return name; }

    public boolean isConstant() {
        return false;
    }
    
    public boolean isLeaf() {
        return true;
    }
    
    public ExpressionValue evaluate() {
    	return this;
    }   
    
    public GeoElement resolve() {
        GeoElement geo = kernel.lookupLabel(name);
        if (geo != null)
			return  geo;
		else {
            String [] str = { "UndefinedVariable", name };
            
            // TODO: remove
            System.out.println("undefined var: " + name);
            System.out.println(" kernel class: " + kernel.getClass());
            System.out.flush();
            
            throw new MyParseError(kernel.getApplication(), str);                 
        }
    }
    
    public HashSet getVariables() {
        HashSet ret = new HashSet();
        ret.add(resolve());
        return ret;
    }
    
    public void resolveVariables() {
    	// this has to be handled in ExpressionNode
    }
    
    public String toString() {
        return name;
    }
    
	public String toValueString() {
		return toString();
	}
	
	
	final public String toLaTeXString(boolean symbolic) {
		return toString();
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
		return false;
	}
	
	final public boolean isExpressionNode() {
		return false;
	}
	
	final public boolean isVariable() {
		return true;
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
    
}
