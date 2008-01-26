/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * Assignment.java
 *
 * Created on 18. September 2001, 18:40
 *
 * rhs = lhs (both Strings) 
 */

package geogebra.kernel.arithmetic;

/**
 *
 * @author  Markus
 * @version 
 */
public class Assignment extends ValidExpression {
    
    private String var;   
    
    /** Creates Assignment     */
    public Assignment(String variable) {
       var = variable;   
    }
       
    final public String getVariable() { return var; }
  
    
    public String toString() {       
                return var;                      
    }
}
