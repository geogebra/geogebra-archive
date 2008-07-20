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
	private int matrixRows = -1;  // -1 means not calculated, 0 means not a matrix
	private int matrixCols = -1;  //

	

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
		matrixRows=-1; // reset
		matrixCols=-1;
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
				
	
		if (!right) 
			System.out.println("apply: " + value + " < op: " + operation + " > " + this);
		else
			System.out.println("apply: " + this + " < op: " + operation + " > " + value);
		
		
		// expression value is list		
		MyList valueList = value.isListValue() ? ((ListValue) value).getMyList() : null;		
		
		// Michael Borcherds 2008-04-14 BEGIN
//		 check for matrix multiplication eg {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,16}}
		//try{
		if (operation == ExpressionNode.MULTIPLY && valueList != null) 
		{ 
			MyList LHlist,RHlist;
			
			if (!right) {LHlist=valueList; RHlist=(MyList)this.deepCopy(kernel);} else {RHlist=valueList; LHlist=(MyList)this.deepCopy(kernel);}
			
			boolean isMatrix = (LHlist.isMatrix() && RHlist.isMatrix());
			int LHcols = LHlist.getMatrixCols(), LHrows=LHlist.getMatrixRows();
			int RHcols = RHlist.getMatrixCols();//, RHrows=RHlist.getMatrixRows();
			

			
			/*
			int LHcols = LHlist.size(), LHrows=0;
			int RHcols = RHlist.size(), RHrows=0;
			//System.out.println("LHcols"+LHcols);
			//System.out.println("RHcols"+RHcols);
			
			boolean isMatrix=true;
			
			//System.out.println("MULT LISTS"+size);
			
			// check LHlist is a matrix
			ExpressionValue singleValue=((ExpressionValue)LHlist.getListElement(0)).evaluate();
			if ( singleValue.isListValue() ){
				LHrows=((ListValue)singleValue).getMyList().size();
				//System.out.println("LHrows"+LHrows);
				if (LHcols>1) for (int i=1 ; i<LHcols ; i++) // check all vectors same length
				{
					//System.out.println(i);
					singleValue=((ExpressionValue)LHlist.getListElement(i)).evaluate();
					//System.out.println("size"+((ListValue)singleValue).getMyList().size());
					if ( singleValue.isListValue() ){
						if (((ListValue)singleValue).getMyList().size()!=LHrows) isMatrix=false;				
					}
					else isMatrix=false;
				}
			}
			else isMatrix = false;

			// check RHlist is a matrix
			singleValue=((ExpressionValue)RHlist.getListElement(0)).evaluate();
			if ( singleValue.isListValue() ){
				RHrows=((ListValue)singleValue).getMyList().size();
				//System.out.println("RHrows"+RHrows);
				if (RHcols>1) for (int i=1 ; i<RHcols ; i++) // check all vectors same length
				{
					//System.out.println(i);
					singleValue=((ExpressionValue)RHlist.getListElement(i)).evaluate();
					//System.out.println("size"+((ListValue)singleValue).getMyList().size());
					if ( singleValue.isListValue() ){
						if (((ListValue)singleValue).getMyList().size()!=RHrows) isMatrix=false;				
					}
					else isMatrix=false;
				}
			}
			else isMatrix = false;
			
			if (LHcols != RHrows) isMatrix=false; // incompatible matrices
			
			System.out.println("isMatrix="+isMatrix);		
*/
			ExpressionNode totalNode;
			ExpressionNode tempNode; 
			
			if (isMatrix)
			{
				listElements.clear();
				for (int col=0 ; col < RHcols ; col++)
				{
					MyList col1 = new MyList(kernel);
					for (int row=0 ; row < LHrows ; row++)
					{
						ExpressionValue totalVal = new ExpressionNode(kernel, new MyDouble(kernel,0.0d));
						for (int i=0 ; i<LHcols ; i++)
						{
							ExpressionValue leftV=getCell(LHlist,i,row);
							ExpressionValue rightV=getCell(RHlist,col,i);							
							tempNode = new ExpressionNode(kernel,leftV,ExpressionNode.MULTIPLY,rightV);
									
							// multiply two cells...
							ExpressionValue operationResult = tempNode.evaluate(); 	
		
							totalNode = new ExpressionNode(kernel,totalVal,ExpressionNode.PLUS,operationResult);
							//totalNode.setLeft(operationResult);
							//totalNode.setRight(totalVal);
							//totalNode.setOperation(ExpressionNode.PLUS);
							
							// ...then add the result to a running total
							totalVal = totalNode.evaluate();	
						
						}
						tempNode = new ExpressionNode(kernel, totalVal); 			
						col1.addListElement(tempNode);
					}
					ExpressionNode col1a = new ExpressionNode(kernel, col1); 
					listElements.add(col1a);
					
				}
			}			
			System.out.println(toString());
			if (isMatrix){
				matrixRows=-1; // reset
				matrixCols=-1;
				return; // finished matrix multiplication successfully
			}
		}
		//}
		//catch (Exception e) { } // not valid matrices
		// Michael Borcherds 2008-04-14 END

		matrixRows=-1; // reset
		matrixCols=-1;
		
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
	
	/**
	 * returns 0 if not a matrix
	 * 
	 * @author Michael Borcherds
	 */
	public int getMatrixRows()
	{
		// check if already calculated
		if (matrixRows != -1 && matrixCols != -1) return matrixRows;
		
		isMatrix(); // do calculation
		
		return matrixRows;
		
	}
	
	/**
	 * returns 0 if not a matrix
	 * 
	 * @author Michael Borcherds
	 */
	public int getMatrixCols()
	{
		// check if already calculated
		if (matrixRows != -1 && matrixCols != -1) return matrixCols;
		
		isMatrix(); // do calculation
		
		return matrixCols;
		
	}
	
	public boolean isMatrix()
	{
	   	return isMatrix(this);
	}
	
	private boolean isMatrix(MyList LHlist)
	{
		// check if already calculated
		if (matrixRows > 0 && matrixCols > 0) return true;
		
		
		boolean isMatrix=true;
		
		int LHcols = LHlist.size(), LHrows=0;
		
		//System.out.println("MULT LISTS"+size);
		
		// check LHlist is a matrix
		ExpressionValue singleValue=((ExpressionValue)LHlist.getListElement(0)).evaluate();
		if ( singleValue.isListValue() ){
			LHrows=((ListValue)singleValue).getMyList().size();
			//System.out.println("LHrows"+LHrows);
			if (LHcols>1) for (int i=1 ; i<LHcols ; i++) // check all vectors same length
			{
				//System.out.println(i);
				singleValue=((ExpressionValue)LHlist.getListElement(i)).evaluate();
				//System.out.println("size"+((ListValue)singleValue).getMyList().size());
				if ( singleValue.isListValue() ){
					if (((ListValue)singleValue).getMyList().size()!=LHrows) isMatrix=false;				
				}
				else isMatrix=false;
			}
		}
		else isMatrix = false;

		System.out.println("isMatrix="+isMatrix);	
		
		if (isMatrix)
		{
			matrixCols=LHcols;
			matrixRows=LHrows;
		}
		else
		{
			matrixCols=0;
		 	matrixRows=0;		
		}
		
		return isMatrix;
		
	}
	
//	 Michael Borcherds 2008-04-15
	public static ExpressionValue getCell(MyList list, int col, int row)
		{
			ExpressionValue singleValue=((ExpressionValue)list.getListElement(col)).evaluate();
			if ( singleValue.isListValue() ){
				ExpressionValue ret = (((ListValue)singleValue).getMyList().getListElement(row)).evaluate();
				//if (ret.isListValue()) System.out.println("isList*********");
				return ret;
			}		
			return null;
		}
/*
//	 Michael Borcherds 2008-04-14 
	private static MyDouble getCell(MyList list, int col, int row)
		{
			ExpressionValue singleValue=((ExpressionValue)list.getListElement(col)).evaluate();
			if ( singleValue.isListValue() ){
				ExpressionValue cell = (((ListValue)singleValue).getMyList().getListElement(row)).evaluate();
				if (cell.isNumberValue())
				{
					NumberValue cellValue=(NumberValue)cell;
					MyDouble cellDouble = (MyDouble)cellValue;
					return cellDouble; 
				}		
			}		
			return null;
		}*/


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
		return toString(); // Michael Borcherds 2008-06-05
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
