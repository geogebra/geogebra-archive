/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.VectorValue;

/**
 * List expression, e.g. with L1 = {3, 2, 1}, L2 = {5, 1, 7}
 * such an expression could be L1 + L2
 */
public class AlgoDependentListExpression extends AlgoElement {
   
	private static final long serialVersionUID = 1L;
	private ExpressionNode root;  // input
    private GeoList list;     // output              
        

    public AlgoDependentListExpression(Construction cons, String label, ExpressionNode root) {
    	super(cons);
        this.root = root;  
        
        list = new GeoList(cons);       
        setInputOutput(); // for AlgoElement
        
        // compute value of dependent list
        compute();      
        list.setLabel(label);
    }   
    
	protected String getClassName() {
		return "AlgoDependentListExpression";
	}
    
    // for AlgoElement
	protected void setInputOutput() {
        input = root.getGeoElementVariables();
        
        output = new GeoElement[1];        
        output[0] = list;        
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoList getList() { return list; }
    
    ExpressionNode getExpression() { return root; }
    
    // evaluate the current value of the arithmetic tree
    protected final void compute() {    
		// get resulting list of ExpressionNodes		    	
    	MyList myList = (MyList) root.evaluate();

		int evalListSize = myList.size();
		int cachedListSize = list.getCacheSize();    		
		
		list.clear();
		for (int i=0; i < evalListSize; i++) {
			ExpressionValue element = myList.getListElement(i).evaluate();    			    			
			GeoElement geo = null;
			
			// number result
			if (element.isNumberValue()) {	
				double val = ((NumberValue) element).getDouble();    				
				
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a number: set value
					if (cachedGeo.isGeoNumeric()) {
						((GeoNumeric) cachedGeo).setValue(val);
						geo = cachedGeo;
					}     			
				}
				
				// no cached number: create new one
				if (geo == null) {
					geo = new GeoNumeric(cons, val);
				}
				
				// add number to list
				list.add(geo);					    				
			} 
			
			// point 
			else if (element.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) element).getVector();   				
				
				// try to use cached element of same type
				if (i < cachedListSize) {
					GeoElement cachedGeo = list.getCached(i);
					
					// the cached element is a point: set value
					if (cachedGeo.isGeoPoint()) {
						((GeoPoint) cachedGeo).setCoords(vec);
						geo = cachedGeo;
					}     			
				}
				
				// no cached point: create new one
				if (geo == null) {
					GeoPoint point = new GeoPoint(cons);
					point.setCoords(vec);
					geo = point;
				}
				
				// add point to list
				list.add(geo);	
			}
			// needed for matrix multiplication 
			// eg {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,a}}
			else if (element instanceof MyList) {
				MyList myList2 = (MyList)element;
				GeoList list2 = new GeoList(cons);
				list2.clear();
				for (int j=0 ; j < myList2.size() ; j++)
				{
				
					ExpressionNode en = myList2.getListElement(j);
					ExpressionValue ev = en.evaluate();
					
					if (ev instanceof MyDouble) {
						GeoNumeric geo2 = new GeoNumeric(cons);
						geo2.setValue(((NumberValue)ev).getDouble());
						list2.add(geo2);
					}
					
				}
				
				list.add(list2);
			}
		}
    }   
    
    final public String toString() {
        // was defined as e.g.  L = 3 * {a, b, c}  
        return root.toString();
    }
}
