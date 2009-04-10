/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package geogebra3D.kernel3D.arithmetic;

import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.MyParseError;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.util.HashSet;

/** 
 * 
 * @author  Markus + ggb3D
 * @version 
 */
public class MyVec3DNode extends ValidExpression implements Vector3DValue {

    private ExpressionValue x, y, z;   
    //private int mode = Kernel.COORD_CARTESIAN;    
    private Kernel kernel;
    
    /** Creates new MyVec3D */
    public MyVec3DNode(Kernel kernel) {
        this.kernel = kernel;
    }
    
    /** Creates new MyPoint3DNode with coordinates (x,y,z) as ExpresssionNodes.
     * Both nodes must evaluate to NumberValues.     
     */
    public MyVec3DNode(Kernel kernel, ExpressionValue x, ExpressionValue y, ExpressionValue z) {
        this(kernel);
        setCoords(x,y,z);
    }                      
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new MyVec3DNode(kernel, x.deepCopy(kernel), y.deepCopy(kernel), z.deepCopy(kernel));
    }
    
    public void resolveVariables() {    	
    	x.resolveVariables();
    	y.resolveVariables(); 
    	z.resolveVariables();
    }
    
    public ExpressionValue getX() {
    	return x;
    }
    
    public ExpressionValue getY() {
    	return y;
    }

    public ExpressionValue getZ() {
    	return z;
    }
    

    
    private void setCoords(ExpressionValue x, ExpressionValue y, ExpressionValue z) {                               
        this.x = x;
        this.y = y;
        this.z = z;
    }
   
    
    final public double [] getCoords() {   
		// check if both ExpressionNodes represent NumberValues	
        ExpressionValue evx = x.evaluate();
        if (!evx.isNumberValue()) {        
            String [] str = { "NumberExpected", evx.toString() };
            throw new MyParseError(kernel.getApplication(), str);
        }        
        ExpressionValue evy = y.evaluate();
        if (!evy.isNumberValue()) {        
            String [] str = { "NumberExpected", evy.toString() };
            throw new MyParseError(kernel.getApplication(), str);
        }            	    	
        ExpressionValue evz = z.evaluate();
        if (!evz.isNumberValue()) {        
            String [] str = { "NumberExpected", evz.toString() };
            throw new MyParseError(kernel.getApplication(), str);
        }      	

        double [] ret = { ((NumberValue)evx).getDouble(),
        		((NumberValue)evy).getDouble(),
        		((NumberValue)evz).getDouble()};
        return ret;

    }                  
      
            
    final public String toString() {         
        StringBuffer sb = new StringBuffer();
        
        sb.append('(');
        sb.append(x.toString());
        sb.append(", ");
        sb.append(y.toString());
        sb.append(", ");
        sb.append(z.toString());       
        sb.append(')');
        return sb.toString();
    }    
    
    public String toValueString() {
        return toString();
    }    
	
	final public String toLaTeXString(boolean symbolic) {
		return toString();
	}
    
    /**
     * interface Point3DValue implementation
     */           
    public GeoPoint3D getPoint() {
    	GeoPoint3D ret = null;//new GeoPoint3D(kernel, getCoords());
        //ret.setMode(mode);
        return ret;
    }        
        
    public boolean isConstant() {
        return x.isConstant() && y.isConstant() && z.isConstant();
    }
    
    public boolean isLeaf() {
        return true;
    }             
 
    
    public ExpressionValue evaluate() { return this; }
    
    /** returns all GeoElement objects in the both coordinate subtrees */
    public HashSet getVariables() {           
        HashSet temp, varset = x.getVariables();
        if (varset == null) varset = new HashSet();                        
        temp = y.getVariables();
        if (temp != null) varset.addAll(temp);              
        temp = z.getVariables();
        if (temp != null) varset.addAll(temp);           
        return varset;                                        
    }
     
    
    
    
    
    
    
    
    
    
    // TODO could be vector or point
    public boolean isVectorValue() {
        return false;
    }    
    public boolean isPoint3DValue() {
        return true;
    }
    
    public boolean isNumberValue() {
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
    
    public boolean isListValue() {
        return false;
    }

    
    final public boolean isExpressionNode() {
        return false;
    }
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }       
    
    final public boolean isVector3DValue() {
    	return true;
    }
}
