/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoDummyVariable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Unicode;

import java.util.HashSet;
import java.util.Iterator;

/**
 * stores left and right hand side of an inequality as
 * Expressions
 */
public class Inequality extends Equation {

	public char op = '<';
  
    /** check whether ExpressionNodes are evaluable to instances of Polynomial
     * or NumberValue and build an Inequality out of them
     */
    public Inequality(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs, String op) {
    	super(kernel, lhs, rhs);
    	if (op.equals(">=")) this.op = Unicode.GREATER_EQUAL;
    	else if (op.equals("<=")) this.op = Unicode.LESS_EQUAL;
    	else this.op = op.charAt(0);
    }  
    
	final public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // left hand side
        if (lhs != null) 
        	sb.append(lhs);
        else 
        	sb.append('0');
        
        // equal sign
        switch (kernel.getCASPrintForm()){
		case ExpressionNode.STRING_TYPE_MATH_PIPER:
		case ExpressionNode.STRING_TYPE_MAXIMA:
		        switch (op) {
		        case Unicode.GREATER_EQUAL:
		        	sb.append(" >= ");
		        	break;
		        case Unicode.LESS_EQUAL:
		        	sb.append(" <= ");
		        	break;
		        case '<':
		        	sb.append(' ');
		        	sb.append('<');
		        	sb.append(' ');
		        	break;
		        case '>':
		        	sb.append(' ');
		        	sb.append('<');
		        	sb.append(' ');
		        	break;
		        }
		        break;
		        
			default:	       	        
		        sb.append(' ');	        
		        sb.append(op);	        
		        sb.append(' ');	        
        }
        
        // right hand side
        if (rhs != null) 
        	sb.append(rhs);
        else
        	sb.append('0');
        
        return sb.toString();
    }


 
} // end of class Equation
