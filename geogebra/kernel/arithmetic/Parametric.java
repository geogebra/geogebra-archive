/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;



/**
 * A Parametric is a ValidExpression that
 * represents a Line in parametric form
 * (px, py) + t (vx, vy)
 */
public class Parametric extends ValidExpression {
    private ExpressionNode P, v;
    private String parameter;
    private Kernel kernel;

    /**
     * Creates new Parametric P + parameter * v.
     * (X = P + parameter * v)
     */
    public Parametric(Kernel kernel, ExpressionNode P, ExpressionNode v, String parameter) {
        this.P = P;
        this.v = v;        
        this.parameter = parameter;  
        this.kernel = kernel;
    }

    public ExpressionNode getP() { return P; }
    public ExpressionNode getv() { return v; }
    public String getParameter() { return parameter; } 
    
  
    public String toString() {
        StringBuffer sb = new StringBuffer();        
        sb.append( getLabel() + " : ");
        sb.append( "X = " + P.evaluate() + " + " + parameter + " " + v.evaluate() );
        return sb.toString();    
    }      
    
}
