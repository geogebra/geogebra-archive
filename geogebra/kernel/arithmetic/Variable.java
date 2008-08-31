/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * VarString.java
 *
 * Created on 18. November 2001, 14:49
 */

package geogebra.kernel.arithmetic;

import geogebra.MyParseError;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;

import java.util.HashSet;


/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class Variable extends ValidExpression implements ExpressionValue {

    private String name;
    private Kernel kernel;
        
    /** Creates new VarString */
    public Variable(Kernel kernel, String name) {
        this.name = name;
        this.kernel = kernel;
    }      
    
	public ExpressionValue deepCopy(Kernel kernel) {
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
    
    /**
     * Returns whether the name of this variable includes a "$" sign. This 
     * is needed for handling of spreadsheet expressions with absolute references, e.g.
     * "A$1" for the cell "A1".
     */
    public boolean nameHasDollarSign() {
    	return name.indexOf('$') > -1;
    }
    
    /**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object.
     */
    private GeoElement resolve() {
    	return resolve(true);
    }
    	
	 /**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object.
     */
    GeoElement resolve(boolean allowAutoCreateGeoElement) {
    	Construction cons = kernel.getConstruction();
    	
    	// allow autocreation of elements
        GeoElement geo = cons.lookupLabel(name, allowAutoCreateGeoElement);
        
        if (geo != null)
			return  geo;                            
		        
		/*
		 * SPREADSHEET $ HANDLING
		 * In the spreadsheet we may have variable names like
		 * "A$1" for the "A1" to deal with absolute references.
		 * Let's remove all "$" signs and try again.
		 */ 	
        if (nameHasDollarSign()) {
			StringBuffer labelWithout$ = new StringBuffer(name.length());
			for (int i=0; i < name.length(); i++) {
				char ch = name.charAt(i);
				if (ch != '$')
					labelWithout$.append(ch);
			}

			// allow autocreation of elements
	        geo = cons.lookupLabel(labelWithout$.toString(), allowAutoCreateGeoElement);				
			if (geo != null) {
				// geo found for name that includes $ signs
				return geo;
			}
        }			
        
        /* moved to Construction.lookupLabel()
        // if referring to variable "i" that is undefined, create it
        if (name.equals("i")) {
    		geo = new GeoPoint(kernel.getConstruction(), "i", 0.0d, 1.0d, 1.0d);
    		((GeoPoint)geo).setFixed(true);   
    		return geo;
        }
			
        // if referring to variable "e" that is undefined, create it
        else if (name.equals("e")) {
    		geo =  new GeoNumeric(kernel.getConstruction(), "e", Math.E);
    		return geo;
        }*/
			
        // if we get here we couldn't resolve this variable name as a GeoElement
        String [] str = { "UndefinedVariable", name };
        throw new MyParseError(kernel.getApplication(), str);                         
    }
    
    /**
     * Looks up the name of this variable in the kernel and returns the 
     * according GeoElement object. For absolute spreadsheet reference names
     * like A$1 or $A$1 a special ExpressionNode wrapper object is returned
     * that preserves this special name for displaying of the expression.
     */
    final ExpressionValue resolveAsExpressionValue() {
    	GeoElement geo = resolve();
    	
    	// spreadsheet dollar sign reference
    	if (nameHasDollarSign()) {
    		// row and/or column dollar sign present?
    		boolean col$ = name.indexOf('$') == 0;
    		boolean row$ = name.length() > 2 && name.indexOf('$', 1) > -1;
    		int operation = 0;
    		if (row$ && col$)
    			operation = ExpressionNode.$VAR_ROW_COL;    			 
    		else if (row$)
    			operation = ExpressionNode.$VAR_ROW;  
    		else // if (col$)
    			operation = ExpressionNode.$VAR_COL;  
    
    		// build an expression node that wraps the resolved geo
    		return new ExpressionNode(kernel, geo, operation, null);    		
    	} 
    	// standard case: no dollar sign
    	else {    		
    		return geo;
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
	
    public boolean isListValue() {
        return false;
    }

	
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}    
    
}
