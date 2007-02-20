package sharptools;
/*
 * @(#)Function.java
 * 
 * $Id: Function.java,v 1.1 2007-02-20 13:58:21 hohenwarter Exp $
 *
 * Created on October 30, 2000, 10:29 AM
 */

import java.util.*;
import java.io.*;

/**
 * Function classes used only by Formula to evaluate functions.
 * Any function needs to have a function handler that implements
 * the "evaluate" interface of the base class Function.
 *
 * A function can accept zero, one, or more parameters.  Each parameter
 * is a number, a relative/absolute address or an address range (e.g., A1:B3).
 *
 * @author Hua Zhong
 * @version $Revision: 1.1 $
 */
public abstract class Function {    

    static private ParserException exception = new ParserException("#PARAM?");
    
    // whether the specified parameter node is an address range
    protected boolean isRange(Node param) {
	LinkedList exp = param.getExp();
	return exp.size() == 1 &&
	    ((Node)exp.getFirst()).isType(Node.COLON);
	    //((Node)param.getExp().getFirst()).isType(Node.COLON);
    }

    // return the first node of a specified parameter
    protected Node getFirst(Node param) {
	return (Node)param.getExp().getFirst();
    }    

    // whether this function has any parameter
    protected void checkParamsExist(Node func) throws ParserException {
	
	if (func.getParams().size()==0)
	    throw exception;
    }
    
    /**
     * This gets the first float number of a parameter list, for functions
     * only accepting a single parameter such as <code>ABS</code>, <code>COS
     * </code>, etc.
     * 
     * @param table the SharpTabelModel
     * @param node the formula unit
     * @param col the int column coordinate
     * @param row the int row coordinate
     * @return the float number
     */
    static protected float getSingleParameter(SharpTableModel table, Node node,
					    int row, int col)
	throws ParserException {
	//	Node param = node.getNextParam();
	LinkedList params = node.getParams();

	if (params.size() != 1)
	    throw new ParserException("#PARAM?");

	LinkedList exp = ((Node)params.getFirst()).getExp();
	
	return Formula.evaluate(table, exp, row, col).floatValue();

    }

    /*
     * This gets two float numbers of a parameter list, for functions
     * only accepting two parameters.
     * 
     * @param table the SharpTabelModel
     * @param node the formula unit
     * @param col the int column coordinate
     * @param row the int row coordinate
     * @return two float nubmers
     */
    /*
    static protected float[] getDoubleParameter(SharpTableModel table,
						Node node,
						int row, int col)
	throws ParserException {
	//	Node param = node.getNextParam();
	LinkedList params = node.getParams();

	if (params.size() != 2)
	    throw new ParserException("#PARAM?");

	float[] values = new float[2];
	
	LinkedList exp = ((Node)params.getFirst()).getExp();
	values[0] = Formula.evaluate(table, exp, row, col).floatValue();

	exp = ((Node)params.getLast()).getExp();
	values[1] = Formula.evaluate(table, exp, row, col).floatValue();

	return values;
    }
    */
    /**
     * This should be implemented in each function.
     * 
     * @param table the SharpTabelModel
     * @param node the function node starting with the funciton name
     * with a chain of parameters
     * @param col the int column coordinate
     * @param row the int row coordinate
     * @exception ParserException
     */
    public abstract Number evaluate(SharpTableModel table, Node node,
				   int row, int col) throws ParserException;

    /**
     * Return the usage of the function
     */
    public abstract String getUsage();
    
    /**
     * Return the description of the function
     */
    public abstract String getDescription();

    /**
     * Whether this function requires parameters.
     * By default yes.
     * @see FunctionPI
     * @see FunctionE
     */
    public boolean requireParams() { return true; }
}

/**
 * <code>SUM</code><br>
 *    usage: <code>=SUM(parameter list)</code><br>
 *    returns the arithmetic sum of the specified parameters<br>
 *    example: <code>=SUM(-1,2,57)</code> returns <code>58.0</code>
 */
class FunctionSum extends Function {
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	// requires parameters
	checkParamsExist(node);
	
	float sum = 0;

	LinkedList params = node.getParams();
	
	if (params != null) {

	    // go over the parameters
	    Iterator it = params.iterator();

	    while (it.hasNext()) {

		// get this parameter
		Node exp = (Node)it.next();

		// if it's a range of cells
		if (isRange(exp)) {	    
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    // for a range, go over the whole range
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++)
			    // get the numeric value of that cell
			    sum += table.getNumericValueAt(i, j).floatValue();
		}
		else {
		    // evaluate this parameter's expression (sub-formula)
		    sum += Formula.evaluate(table, exp.getExp(), row, col).floatValue();
		}
	    }
	    
	}

	return (Number)(new Float(sum));
    }

    public String getUsage() {
	return "SUM(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Adds all the numbers in a set of values.";
    }
}


/**
 * <code>COUNT</code><br>
 *   usage: <code>=COUNT(parameter list)</code><br>
 *   returns the number of parameters specified<br>
 *   example: <code>=COUNT(A1:A7)</code> returns <code>7.0</code>
 */
class FunctionCount extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			int row, int col) throws ParserException {

	// requires parameters
	checkParamsExist(node);

	int count = 0;

	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {
		// the first parameter
		Node exp = (Node)it.next();
		
		if (isRange(exp)) {
		    // if it's a range then count the number of cells
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    count += (addr[1].getRow()-addr[0].getRow()+1) *
			(addr[1].getCol()-addr[0].getCol()+1);
		}
		else {
		    // otherwise count one
		    count++;		    
		}
	    }
	    
	}
	
	return new Integer(count);
    }

    public String getUsage() {
	return "COUNT(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Counts the number of cells that contain numbers and numbers within the list of arguments.";
    }
}

/**
 * <code>AVERAGE</code><br>
 *   usage: <code>=AVERAGE(parameter list)</code><br>
 *   returns the arithmetic mean of the specified parameters<br>
 *   example: <code>=AVERAGE(1,2,3)</code> returns <code>2.0</code>
 */
class FunctionAverage extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	float sum = (new FunctionSum()).evaluate(table, node, row, col).
	    floatValue();
	float nCells = (new FunctionCount()).evaluate(table, node, row, col).
	    floatValue();
	return new Float(sum/nCells);
    }

    public String getUsage() {
	return "AVERAGE(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the average (arithmetric mean) of its arguments.";
    }    
}

/**
 * <code>MEDIAN</code><br>
 *   usage: <code>=MEDIAN(parameter list)</code><br>
 *   returns the median (the value in the middle) of the specified parameters<br>
 *   example: <code>=MEDIAN(1,2,5)</code> returns <code>2.0</code>
 */
class FunctionMedian extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	// requires parameters
	checkParamsExist(node);

	// get number of values
	int nCells = (int)(new FunctionCount()).
	    evaluate(table, node, row, col).floatValue();

	float[] values = new float[nCells];

	// get all the values
	
	int index = 0;
	
	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {
		// the first parameter
		Node exp = (Node)it.next();
		
		if (isRange(exp)) {
		    // if it's a range get each cell's value
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++){
			    values[index++] =
				table.getNumericValueAt(i, j).floatValue();
			}
		}
		else {
		    // otherwise get this expression's value
		    values[index++] =
			Formula.evaluate(table, exp.getExp(), row, col).floatValue();
		}
	    }
	    
	}

	// sort the values array
	for (int m = 0; m < nCells-1; m++)
	    for (int n = m; n < nCells-1; n++)
		if (values[m]>values[n]) {
		    float tmp = values[m];
		    values[m] = values[n];
		    values[n] = tmp;
		}

	// get the median
	int half = nCells/2;
	
	if (nCells != half*2)
	    return new Float(values[half]);
	else
	    return new Float((values[half-1]+values[half])/2);
    }

    public String getUsage() {
	return "MEDIAN(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the median (value in the middle) of its arguments.";
    }    
}

/**
 * <code>MIN</code><br>
 *   usage: <code>=MIN(parameter list)</code><br>
 *   returns the minimum value of the specified parameters<br>
 *   example: <code>=MIN(5,6,-1)</code> returns <code>-1.0</code>
 */
class FunctionMin extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	
	// requires parameters
	checkParamsExist(node);
	
	float min = Float.MAX_VALUE;
	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {

		Node exp = (Node)it.next();
		
		if (isRange(exp)) {	    
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++){
			    float value =
				table.getNumericValueAt(i, j).floatValue();
			    if (value < min)
				min = value;
			}
		}
		else {
		    float value =
			Formula.evaluate(table, exp.getExp(), row, col).floatValue();
		    if (value < min)
			min = value;
		}
	    }
	    
	}
	
	return new Float(min);
    }

    public String getUsage() {
	return "MIN(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the smallest number in a set of values.";
    }        
}

/**
 * <code>MAX</code><br>
 *   usage: <code>=MAX(parameter list)</code><br>
 *   returns the maximum value of the specified parameters<br>
 *   example: <code>=MAX(5,6,-1)</code> returns <code>6.0</code>
 */
class FunctionMax extends Function {
     
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	
	// requires parameters
	checkParamsExist(node);
	
	float max = Float.MIN_VALUE;
	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {

		Node exp = (Node)it.next();
		
		if (isRange(exp)) {	    
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++){
			    float value =
				table.getNumericValueAt(i, j).floatValue();
			    if (value > max)
				max = value;
			}
		}
		else {
		    float value =
			Formula.evaluate(table, exp.getExp(), row, col).floatValue();
		    if (value > max)
			max = value;
		}
	    }
	    
	}
	
	return new Float(max);
    }


    public String getUsage() {
	return "MAX(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the largest number in a set of values.";
    }         
}

/**
 * <code>Range</code>
 */
class FunctionRange extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	float max = Formula.getFuncHandler("MAX").
	    evaluate(table, node, row, col).
	    floatValue();
	float min = Formula.getFuncHandler("MIN").
	    evaluate(table, node, row, col).
	    floatValue();	
	return new Float(max - min);
    }


    public String getUsage() {
	return "RANGE(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the difference between MAX and MIN in a set of values.";
    }    
}

/**
 * <code>ABS</code><br>
 *   usage: <code>=ABS(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the absolute value of the specified parameter<br>
 *   example: <code>=ABS(-92)</code> returns <code>92.0</code>
 */
class FunctionAbs extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	return new Float(Math.abs
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "ABS(value)";
    }
    
    public String getDescription() {
	return "Returns the absolute value of a number.";
    }         
}

/**
 * <code>SIN</code><br>
 *   usage: <code>=SIN(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the sine of the specified parameter (in radians)<br>
 *   example: <code>=SIN(45)</code> returns <code>0.8509035</code>
 */
class FunctionSin extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	return new Float(Math.sin
			 (getSingleParameter(table, node, row, col)));
    }


    public String getUsage() {
	return "SIN(value)";
    }
    
    public String getDescription() {
	return "Returns the sine of an angle.";
    }         
}

/**
 * <code>COS</code><br>
 *   usage: <code>=COS(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the cosine of the specified parameter (in radians)<br>
 *   example: <code>=COS(30)</code> returns <code>0.15425146</code>
 */
class FunctionCos extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	
	return new Float(Math.cos
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "COS(value)";
    }
    
    public String getDescription() {
	return "Returns the cosine of an angle.";
    }         
}

/**
 * <code>TAN</code><br>
 *   usage: <code>=TAN(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the tangent of the specified parameter (in radians)<br>
 *   example: <code>=TAN(60)</code> returns <code>0.32004037</code>
 */
class FunctionTan extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
    
	return new Float(Math.tan
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "TAN(value)";
    }
    
    public String getDescription() {
	return "Returns the tangent of an angle.";
    }    
}

/**
 * <code>ASIN</code><br>
 *   usage: <code>=ASIN(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the arcsine of the specified parameter (in radians)<br>
 *   example: <code>=ASIN(60)</code> returns <code>0.5235988</code>
 */
class FunctionAsin extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	return new Float(Math.asin
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "ASIN(value)";
    }
    
    public String getDescription() {
	return "Returns the arcsine of a number in radians, in the range -Pi/2 to Pi/2.";
    }             
}

/**
 * <code>ACOS</code><br>
 *   usage: <code>=ACOS(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the arccosine of the specified parameter (in radians)<br>
 *   example: <code>=ACOS(.5)</code> returns <code>1.0471976</code>
 */
class FunctionAcos extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	return new Float(Math.acos
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "ACOS(value)";
    }
    
    public String getDescription() {
	return "Returns the arccosine of a number in radians, in the range 0 to Pi.";
    }                 
}

/**
 * <code>ATAN</code><br>
 *   usage: <code>=ATAN(parameter)</code><br>
 *   accepts only one literal or address<br>
 *   returns the arctangent of the specified parameter (in radians)<br>
 *   example: <code>=ATAN(60)</code> returns <code>1.5541312</code>
 */
class FunctionAtan extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	return new Float(Math.atan
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "ATAN(value)";
    }
    
    public String getDescription() {
	return "Returns the arctangent of a number in radians, in the range -Pi/2 to Pi/2.";
    }                 
}

/**
 * <code>INT</code>
 * returns the integer part of a number
 */
class FunctionInt extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	return new Float((int)getSingleParameter(table, node, row, col));
    }

    public String getUsage() {
	return "INT(value)";
    }
    
    public String getDescription() {
	return "Returns the integer part of a number.";
    }                 
}

/**
 * <code>ROUND</code><br>
 *   usage: <code>=ROUND(parameter, num_digits)</code><br>
 *   Rounds a number to a specified number of digits<br>
 *   example: <code>=ROUND(1.534, 2)</code> returns <code>1.53</code>
 */
class FunctionRound extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {
	
	return new Float(Math.round
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "ROUND(value)";
    }
    
    public String getDescription() {
	return "Returns the nearest integer of a number.";
    }
}

/**
 * <code>SQRT</code><br>
 *   usage: <code>=STDDEV(parameter)</code><br>
 *   returns the standard deviation of the specified parameter<br>
 *   example: <code>=SQRT(19044)</code> returns <code>138.0</code>
 */
class FunctionSqrt extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	return new Float(Math.sqrt
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "SQRT(value)";
    }
    
    public String getDescription() {
	return "Returns a square root of a number.";
    }    
}

/**
 * <code>LOG</code><br>
 *   usage: <code>=LOG(parameter)</code><br>
 *   returns the logarithm base E of the specified parameter<br>
 *   example: <code>=LOG(1)</code> returns <code>0.0</code>
 */
class FunctionLog extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	return new Float(Math.log
			 (getSingleParameter(table, node, row, col)));
    }

    public String getUsage() {
	return "LOG(value)";
    }
    
    public String getDescription() {
	return "Returns the logarithm of a number to the base e.";
    }
}

/**
 * <code>MEANDEV</code><br>
 *   usage: <code>=MEANDEV(parameter list)</code><br>
 *   returns the mean deviation of the specified parameters<br>
 *   example: <code>=STDDEV(100,60,60,80,80)</code> returns <code>12.8</code>
 */
class FunctionMeandev extends Function {    

    // mean deviation is the average of absolute deviations from the mean value
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	float dev = 0;
	float nCells = (new FunctionCount()).
	    evaluate(table, node, row, col).floatValue();
	float mean = (new FunctionAverage()).
	    evaluate(table, node, row, col).floatValue();

	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {

		Node exp = (Node)it.next();
		
		if (isRange(exp)) {	    
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++){
			    dev += Math.abs(table.getNumericValueAt(i, j).
					    floatValue() - mean);
			}
		}
		else {
		    dev +=
			Math.abs(Formula.evaluate(table, exp.getExp(), row, col).floatValue());
		}
	    }
	    
	}	

	return new Float(dev/nCells);
    }

    public String getUsage() {
	return "MEANDEV(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the average absolute deviation in a set of values.";
    }  
}

/**
 * <code>STDDEV</code><br>
 *   usage: <code>=STDDEV(parameter list)</code><br>
 *   returns the standard deviation of the specified parameters<br>
 *   example:
 *     <code>=STDDEV(100,60,60,80,80)</code> returns <code>16.7332</code>
 */
class FunctionStddev extends Function {

    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	float dev = 0;
	float nCells = (new FunctionCount()).
	    evaluate(table, node, row, col).floatValue();
	float mean = (new FunctionAverage()).
	    evaluate(table, node, row, col).floatValue();

	LinkedList params = node.getParams();
	
	if (params != null) {

	    Iterator it = params.iterator();

	    while (it.hasNext()) {

		Node exp = (Node)it.next();
		
		if (isRange(exp)) {	    
		    CellPoint[] addr =
			getFirst(exp).getAddressRange(row, col);
		    
		    for (int i = addr[0].getRow(); i <= addr[1].getRow(); i++)
			for (int j = addr[0].getCol(); j <= addr[1].getCol(); j++){
			    float temp = Math.abs(table.getNumericValueAt(i, j).
						  floatValue() - mean);
			    dev += temp*temp;
			}
		}
		else {
		    float temp = Math.abs(Formula.evaluate(table, exp.getExp(), row, col).floatValue());
		    dev += temp*temp;

		}
	    }
	    
	}	

	return new Float(Math.sqrt(dev/(nCells - 1)));
    }

    public String getUsage() {
	return "STDDEV(value1,value2,...)";
    }
    
    public String getDescription() {
	return "Returns the standard deviation in a set of values.";
    }
}

/**
 * This gives us contant PI.<br>
 * <code>PI</code><br>
 * usage: <code>=PI()</code><br>
 * returns an approximation of the constant pi<br>
 * example: <code>=PI()</code> returns <code>3.1415927</code>
 */
class FunctionPI extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	// no parameters allowed
	if (node.getParams().size() != 0)
	    throw new ParserException("#PARAM?");
		
	return new Float(Math.PI);
    }

    public String getUsage() {
	return "PI()";
    }
    
    public String getDescription() {
	return "Returns the value of PI.";
    }

    public boolean requireParams() { return false; }
}

/**
 * This gives us contant E.<br>
 * <code>E</code><br>
 * usage: <code>=E()</code><br>
 * returns an approximation of the golden mean<br>
 * example: <code>=E()</code> returns <code>2.7182817</code>
 */
class FunctionE extends Function {
    
    public Number evaluate(SharpTableModel table, Node node,
			  int row, int col) throws ParserException {

	// no parameters allowed
	if (node.getParams().size() != 0)
	    throw new ParserException("#PARAM?");

	return new Float(Math.E);
    }

    public String getUsage() {
	return "E()";
    }
    
    public String getDescription() {
	return "Returns value of e.";
    }

    public boolean requireParams() { return false; }
}

