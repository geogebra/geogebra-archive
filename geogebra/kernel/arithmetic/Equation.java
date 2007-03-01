/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.kernel.arithmetic;

import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.util.HashSet;
import java.util.Iterator;

/**
 * stores left and right hand side of an equation as
 * Exprssions
 */
public class Equation extends ValidExpression {

    private ExpressionNode lhs;
    private ExpressionNode rhs;
    
    private Polynomial leftPoly, rightPoly; // polynomial in normalForm   
    private Polynomial normalForm; // polynomial in normalForm
    private Kernel kernel;
   
    /** check whether ExpressionNodes are evaluable to instances of Polynomial
     * or NumberValue and build an Equation out of them
     */
    public Equation(Kernel kernel, ExpressionNode lhs, ExpressionNode rhs) {
    	this.lhs = lhs;
    	this.rhs = rhs;
    	this.kernel = kernel;    	    	
    }  
    
    public ExpressionNode getRHS() {
    	return rhs;
    }
    
    /**
     * Call this method to check that this is a valid equation.
     * May throw MyError (InvalidEquation).     
     */
    public void initEquation() {
        boolean valid = lhs.includesPolynomial() || rhs.includesPolynomial();
        
        if (!valid)            
			throw new MyError(kernel.getApplication(), "InvalidEquation");              
        
        // resolve variables
        lhs.resolveVariables();
        rhs.resolveVariables();
        
     // build normal form polynomial        
        // copy the expression trees
        ExpressionNode leftEN  = lhs.getCopy();
        ExpressionNode rightEN = rhs.getCopy();
        
        // ensure that they only consist of polynomials
        leftEN.makePolynomialTree();
        rightEN.makePolynomialTree();		
        		
        // simplify the both sides to single polynomials
        leftPoly  = (Polynomial) leftEN.evaluate();
        rightPoly = (Polynomial) rightEN.evaluate();		
        		
        // bring to normal form left - right = 0
        normalForm = new Polynomial(kernel, rightPoly);
        normalForm.multiply(-1.0d);
        normalForm.add(leftPoly);   
    }
    
    public Polynomial getNormalForm() {        
        return normalForm;
    }           
                
    /**
     *  returns the degree of the equation's normalform
     *  (max length of variables in a Term of the normalform)
     */
    public int degree() {        
        return normalForm.degree();
    } 
    
    public ExpressionValue getCoefficient(String variables) {         
        return normalForm.getCoefficient(variables);        
    }
    
    public double getCoeffValue(String variables) { 
        ExpressionValue ev = getCoefficient(variables);
        
        try {
            NumberValue nv = (NumberValue) ev;
            return nv.getDouble();
        } catch (Exception e) {
            System.out.println("getCoeffValue("+variables+") failed:" + e);
            return Double.NaN;
        }
    }
    
    final public GeoElement [] getGeoElementVariables() {
        HashSet varset = new HashSet();
        try { varset.addAll(lhs.getVariables()); } catch (Exception e) {}
        try { varset.addAll(rhs.getVariables()); } catch (Exception e) {}
        
        Iterator i = varset.iterator();        
        GeoElement [] ret = new GeoElement[varset.size()];
        int j=0;
        while (i.hasNext()) {
            ret[j++] = (GeoElement) i.next();
        }                
        return ret;
    }
    
    final public boolean isIndependent() {
        GeoElement [] vars = getGeoElementVariables();
        return (vars == null || vars.length == 0);
    }
     
    /**
     * returns true if this Equation is explicit
     * (lhs is "+1y" and rhs does not contain y)
     * or (rhs is "+1y" and lhs does not contain y)
     */
    public boolean isExplicit(String var) {  
        Polynomial lhs = leftPoly;
        Polynomial rhs = rightPoly;
        
        // var = ... || ... = var
        return (    lhs.length() == 1 && 
                    ((NumberValue)lhs.getCoefficient(var).evaluate()).getDouble() 
                        == 1 && 
                    !rhs.contains(var)                  ) 
                || 
               (    rhs.length() == 1 && 
                    ((NumberValue)rhs.getCoefficient(var).evaluate()).getDouble() 
                        == 1 && 
                    !lhs.contains(var)                  ) ;
    }
    
    /**
     * returns true if this Equation is implicit (not explicit)
     */
    public boolean isImplicit() {
        return !isExplicit("x") && !isExplicit("y");
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        if (lhs != null) sb.append(lhs);
        else sb.append('0');
        sb.append(" = ");
        if (rhs != null) sb.append(rhs);
        else sb.append('0');
        return sb.toString();
    }
 
} // end of class Equation
