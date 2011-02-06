/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentPoint.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.VectorValue;

/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDependentPoint extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoPoint P;     // output         
    
    private GeoVec2D temp;
        
    /** Creates new AlgoJoinPoints */
    public AlgoDependentPoint(Construction cons, String label, ExpressionNode root, boolean complex) {
    	super(cons);
        this.root = root;        
        
        P = new GeoPoint(cons); 
        
        
        setInputOutput(); // for AlgoElement

        if (complex)
    		P.setMode(Kernel.COORD_COMPLEX);

    	// compute value of dependent number
        compute();      
        

    	P.setLabel(label);
    }   
    
	public String getClassName() {
		return "AlgoDependentPoint";
	}
	
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();  
        
        setOutputLength(1);        
        setOutput(0,P);        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoPoint getPoint() { return P; }
    
    public ExpressionNode getExpressionNode() {
    	return root;
    }
    
    // calc the current value of the arithmetic tree
    protected final void compute() {   
    	try {
	        temp = ((VectorValue) root.evaluate()).getVector();
	        if (Double.isInfinite(temp.x) || Double.isInfinite(temp.y)) {
	        	P.setUndefined();
	        } else {
				P.setCoords( temp.x, temp.y, 1.0); 
	        }		
	        
	        //P.setMode(temp.getMode());
	        
    	} catch (Exception e) {
	    	P.setUndefined();
	    }
    }   
    
    final public String toString() {              
        return root.toString();
    }
    
    final public String toRealString() {              
        return root.toRealString();
    }
}
