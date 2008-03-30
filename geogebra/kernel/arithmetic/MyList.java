/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package geogebra.kernel.arithmetic;

import geogebra.MyError;
import geogebra.kernel.Kernel;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * MyList is used to store a list of ExpressionNode objects read by the parser
 * and to evaluate them. So a MyList object is used when a list is entered (e.g.
 * {2, 3, 7, 9}) and also when a list is used for arithmetic operations.
 * 
 * @see ExpressionNode.evaluate()
 * 
 * @author Markus Hohenwarter
 */
public class MyList extends ValidExpression implements ListValue {

	private Kernel kernel;

	// list for list elements
	private ArrayList listElements;

	public MyList(Kernel kernel) {
		this(kernel, 20);
	}

	public MyList(Kernel kernel, int size) {
		this.kernel = kernel;
		listElements = new ArrayList(size);
	}

	public void addListElement(ExpressionNode arg) {
		listElements.add(arg);
	}
		
	/**
	 * Applies an operation to this list using the given value:
	 * <this> <operation> <value>.
	 * 
	 * @param operation: int value like ExpressionNode.MULTIPLY
	 * @param value: value that should be applied to this list using the given operation
	 * @author Markus Hohenwarter	 
	 */
	final public void applyRight(int operation, ExpressionValue value) {
		apply(operation, value, true);
	}
	
	/**
	 * Applies an operation to this list using the given value:
	 * <value> <operation> <this>.
	 * 
	 * @param operation: int value like ExpressionNode.MULTIPLY
	 * @param value: value that should be applied to this list using the given operation
	 * @author Markus Hohenwarter	 
	 */
	final public void applyLeft(int operation, ExpressionValue value) {
		apply(operation, value, false);
	}		
		
	/**
	 * Applies an operation to this list using the given value.
	 * 
	 * @param operation: int value like ExpressionNode.MULTIPLY
	 * @param value: value that should be applied to this list using the given operation
	 * @param right: true for <this> <operation> <value>, false for <value> <operation> <this>
	 * @author Markus Hohenwarter	 
	 */
	private void apply(int operation, ExpressionValue value, boolean right) {
		int size = size();
				
//	
//		if (!right) 
//			System.out.println("apply: " + value + " < op: " + operation + " > " + this);
//		else
//			System.out.println("apply: " + this + " < op: " + operation + " > " + value);
		
		
		// expression value is list		
		MyList valueList = value.isListValue() ? ((ListValue) value).getMyList() : null;		
		
		// return empty list if sizes don't match
		if (size == 0 || (valueList != null && size != valueList.size())) 
		{			
			listElements.clear();
			return;
		}
		
		// temp ExpressionNode to do evaluation of single elements
		ExpressionNode tempNode = new ExpressionNode(kernel, (ExpressionNode) listElements.get(0));
		tempNode.setOperation(operation);
		
		for (int i = 0; i < size; i++) {	
			try {				
				// singleValue to apply to i-th element of this list
				ExpressionValue singleValue = valueList == null ? value : valueList.getListElement(i);								
				
				// apply operation using singleValue
				if (right) {
					// this operation value
					tempNode.setLeft((ExpressionNode) listElements.get(i));
					tempNode.setRight(singleValue);
				} else {
					// value operation this					
					tempNode.setLeft(singleValue);
					tempNode.setRight((ExpressionNode) listElements.get(i));
				}
				
				// evaluate operation
				ExpressionValue operationResult = tempNode.evaluate(); 
				
				
			//	System.out.println("        tempNode : " + tempNode + ", result: " + operationResult);
			
				
				// set listElement to operation result
				if (!operationResult.isExpressionNode()) {
					operationResult = new ExpressionNode(kernel, operationResult); 
				}
				listElements.set(i, (ExpressionNode) operationResult);
			} 
			catch (MyError err) {
				// TODO: remove
				System.err.println(err.getLocalizedMessage());
				
				// return empty list if any of the elements aren't numbers			
				listElements.clear();
				return;
			}							
		}
		
		
//		System.out.println("   gives : " + this);
	
	}


	// Michael Borcherds 2008-02-04
	// adapted from GeoList
	public String toString() {
		StringBuffer sbBuildValueString = new StringBuffer();
		sbBuildValueString.append("{");

		// first (n-1) elements
		int lastIndex = listElements.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				ExpressionNode exp = (ExpressionNode) listElements.get(i);
				sbBuildValueString.append(exp.toString()); // .toOutputValueString());
				sbBuildValueString.append(", ");
			}

			// last element
			ExpressionNode exp = (ExpressionNode) listElements.get(lastIndex);
			sbBuildValueString.append(exp.toString());
		}

		sbBuildValueString.append("}");
		return sbBuildValueString.toString();
	}

	public int size() {
		return listElements.size();
	}

	public void resolveVariables() {
		for (int i = 0; i < listElements.size(); i++) {
			ExpressionNode en = (ExpressionNode) listElements.get(i);
			en.resolveVariables();
		}
	}

	public ExpressionNode getListElement(int i) {
		return (ExpressionNode) listElements.get(i);
	}

	/*
	 * public String toString() { }
	 */

	public ExpressionValue evaluate() {
		return this;
	}

	public boolean isConstant() {
		return getVariables().size() == 0;
	}

	public boolean isLeaf() {
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

		// return evaluate().isPolynomial();
	}

	public boolean isTextValue() {
		return false;
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		// copy arguments
		int size = listElements.size();
		MyList c = new MyList(kernel, size());

		for (int i = 0; i < size; i++) {
			c.addListElement(((ExpressionNode) listElements.get(i))
					.getCopy(kernel));
		}
		return c;
	}

	public HashSet getVariables() {
		HashSet varSet = new HashSet();
		int size = listElements.size();
		for (int i = 0; i < size; i++) {
			HashSet s = ((ExpressionNode) listElements.get(i)).getVariables();
			if (s != null)
				varSet.addAll(s);
		}

		return varSet;
	}

	public String toValueString() {
		return "MyList.toValueString()";
		/*
		 * int size = listElements.size(); for (int i=0; i < size; i++) {
		 * ((ExpressionNode) listElements.get(i)).evaluate(); }
		 */
	}

	public String toLaTeXString(boolean symbolic) {
		// return evaluate().toLaTeXString(symbolic);
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
		if (isInTree()) {
			// used in expression node tree: be careful
			return (MyList) deepCopy(kernel);
		} else {
			// not used anywhere: reuse this object
			return this;
		}		
	}
}
