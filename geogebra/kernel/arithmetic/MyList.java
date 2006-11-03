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
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package geogebra.kernel.arithmetic;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.util.ArrayList;
import java.util.HashSet;
/**
 * MyList is used to store a list of ExpressionNode objects read by 
 * the parser and to evaluate them. So a MyList object is used
 * when a list is entered (e.g. {2, 3, 7, 9}) and also when
 * a list is used for arithmetic operations.
 *  
 * @see ExpressionNode.evaluate()
 *
 * @author Markus Hohenwarter
 */
public class MyList extends ValidExpression 
implements ListValue {
         
    // list for list elements
    private ArrayList listElements;        
    private Kernel kernel;   
    boolean isEvaluated = false;
    
    private HashSet varSet;
          
    public MyList(Kernel kernel) {    
        this(kernel, 20);        
    }
    
    public MyList(Kernel kernel, int size) {    
        this.kernel = kernel;
        listElements = new ArrayList(size);
    }    
       
    public void addListElement(ExpressionValue arg) {
    	listElements.add( arg );
    }
    
    public int size() {
    	return listElements.size();
    }
    
    public void resolveVariables() {
        for (int i=0; i < listElements.size(); i++) {        
            ((ExpressionNode) listElements.get(i)).resolveVariables();        
        }
    }
    
    public ExpressionValue getListElement(int i) {
        return (ExpressionValue) listElements.get(i);
    }
      
    /*
    public String toString() {
  
    } */
    
    public ExpressionValue evaluate() {        
        if (isEvaluated) return this;

        int size = listElements.size();
        for (int i=0; i < size; i++) {
        	((ExpressionValue) listElements.get(i)).evaluate();
        }
                                    
        return this;
    }

    public boolean isConstant() {
    	return getVariables().size() == 0;        
    }

    public boolean isLeaf() {
        //return evaluate().isLeaf();
        return true;
    }

    public boolean isNumberValue() {
        return false;
    }

    public boolean isVectorValue() {
        return false;
    }
    
    final public boolean isBooleanValue() {
        return false;
    }

    public boolean isPolynomialInstance() {
        return false;
                
        //return evaluate().isPolynomial();
    }
    
    public boolean isTextValue() {
        return false;
    }   

    public ExpressionValue deepCopy() {
        // copy arguments     
        int size = listElements.size();
        MyList c = new MyList(kernel, size());
    
        for (int i=0; i < size; i++) {
            c.addListElement(((ExpressionValue) listElements.get(i)).deepCopy());
        }
        return c;
    }

    public HashSet getVariables() {  
    	if (varSet == null) {    		    	
	    	varSet = new HashSet();
	        int size = listElements.size();
	        for (int i=0; i < size; i++) {
	            HashSet s = ((ExpressionValue)listElements.get(i)).getVariables();
	            if (s != null) varSet.addAll(s);
	        }	        	        
    	}
    	
        return varSet;
    }    
    
    // TODO: think about this
    public String toValueString() {
    	return "MyList.toValueString()";
    	/*
    	int size = listElements.size();
        for (int i=0; i < size; i++) {
        	((ExpressionValue) listElements.get(i)).evaluate();
        }*/
    }
    
    // TODO: think about this
	public String toLaTeXString(boolean symbolic) {
		//return evaluate().toLaTeXString(symbolic);
		return "MyList.toLaTeXString()";
	}    

    final public boolean isExpressionNode() {
        return false;
    }
    
    public boolean isListValue() {
        return true;
    }

      
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }

	public MyList getMyList() {		
		return this;
	}
}
