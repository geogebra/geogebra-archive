/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * ExpressionNode.java
 *
 * binary tree node for ExpressionValues (NumberValues, VectorValues)
 *
 * Created on 03. Oktober 2001, 09:37
 */

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;
import geogebra.kernel.ParametricCurve;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Tree node for expressions like "3*a - b/5"
 * @author  Markus
 * @version 
 */
public class ExpressionNode extends ValidExpression
implements ExpressionValue {   
	
	public static final int STRING_TYPE_GEOGEBRA_XML = 0;
	public static final int STRING_TYPE_GEOGEBRA = 1;
	public static final int STRING_TYPE_MATH_PIPER = 3;
	public static final int STRING_TYPE_LATEX = 100;
	
	public static final String UNICODE_PREFIX = "uNiCoDe";
	public static final String UNICODE_DELIMITER = "U";  
    
	// boolean
	public static final int NOT_EQUAL = -100;
	public static final int NOT = -99;
	public static final int OR = -98;
    public static final int AND = -97;
    public static final int EQUAL_BOOLEAN = -96;
    public static final int LESS = -95;
    public static final int GREATER = -94;
    public static final int LESS_EQUAL = -93;
    public static final int GREATER_EQUAL = -92;    
    public static final int PARALLEL = -91;  
    public static final int PERPENDICULAR = -90;
    
    public static final String strNOT = "\u00ac";
    public static final String strAND = "\u2227";
    public static final String strOR = "\u2228";
    public static final String strLESS_EQUAL = "\u2264";
    public static final String strGREATER_EQUAL = "\u2265";
    public static final String strEQUAL_BOOLEAN = "\u225f";
    public static final String strNOT_EQUAL = "\u2260";
    public static final String strPARALLEL = "\u2225";
    public static final String strPERPENDICULAR = "\u22a5";
    public static final String strCOMPLEXMULTIPLY = "\u2297";
        
    // arithmetic
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int MULTIPLY = 2;
    public static final int DIVIDE = 3;
    public static final int POWER = 4;               
    public static final int COS = 5;   
    public static final int SIN = 6;   
    public static final int TAN = 7;   
    public static final int EXP = 8;   
    public static final int LOG = 9;   
    public static final int ARCCOS = 10;   
    public static final int ARCSIN = 11;   
    public static final int ARCTAN = 12;   
    public static final int SQRT = 13;   
    public static final int ABS = 14;   
    public static final int SGN = 15;   
    public static final int XCOORD = 16; 
    public static final int YCOORD = 17;  
    public static final int COSH = 18;
    public static final int SINH = 19;
    public static final int TANH = 20;
    public static final int ACOSH = 21;
    public static final int ASINH = 22;
    public static final int ATANH = 23;
    public static final int FLOOR = 24;
    public static final int CEIL = 25;  
    public static final int FACTORIAL = 26;
    public static final int ROUND = 27;  
    public static final int GAMMA = 28;    
    public static final int LOG10 = 29;  
    public static final int LOG2 = 30; 
    public static final int CBRT = 31;   
    public static final int RANDOM = 32;
    public static final int COMPLEXMULTIPLY = 33;
     
    public static final int FUNCTION = 100;
    public static final int VEC_FUNCTION = 101;
    public static final int DERIVATIVE = 110;   
    
    // spreadsheet absolute reference using $ signs
    public static final int $VAR_ROW = 501;
    public static final int $VAR_COL = 502;
    public static final int $VAR_ROW_COL = 503;
    
    private Application app;
    private Kernel kernel;
    private ExpressionValue left, right; 
    int operation = -100;
    public boolean forceVector = false, forcePoint = false;
    
    private boolean holdsLaTeXtext = false;
    
    // for leaf mode
    private boolean leaf = false;

    /** Creates new ExpressionNode */        
    public ExpressionNode(Kernel kernel, ExpressionValue left, int operation, ExpressionValue right) {  
        this.kernel = kernel;
        app = kernel.getApplication();
        
        this.operation = operation;         
        setLeft(left);
        if (right != null) {
            setRight(right);        
        } else { // set dummy value
        	setRight(new MyDouble(kernel, 0.0d));
        }     
    }           
    
    /** for only one leaf */
    // for wrapping ExpressionValues as ValidExpression
    public ExpressionNode(Kernel kernel, ExpressionValue leaf) {    
        this.kernel = kernel;
        app = kernel.getApplication();
              
        setLeft(leaf);
        this.leaf = true;        
    }
    
    // copy constructor: NO deep copy of subtrees is done here!
    // this is needed for translation of functions
    public ExpressionNode(ExpressionNode node) {
        kernel = node.kernel;
        app = node.app;
        
        leaf = node.leaf;
        operation = node.operation;
        setLeft(node.left);
        setRight(node.right);
    }
    
    public Kernel getKernel() {
        return kernel;
    }
    
    final public int getOperation() {
        return operation;
    }
    
    public void setOperation(int op) {
        operation = op;
    }
    
    public void setHoldsLaTeXtext(boolean flag) {
    	holdsLaTeXtext = flag;
    }
    
    final public ExpressionValue getLeft() {
        return left;
    }
    
    final public void setLeft(ExpressionValue l) {
        left = l;      
        left.setInTree(true); // needed fot list operations eg k=2 then k {1,2}
    }
    
    public ExpressionNode getLeftTree() {       
        if (left.isExpressionNode())
            return (ExpressionNode) left;
        else 
            return new ExpressionNode(kernel, left);
    }
    
    final public ExpressionValue getRight() {
        return right;
    }
    
    final public void setRight(ExpressionValue r) {
        right = r;
        right.setInTree(true); // needed fot list operations eg k=2 then k {1,2}
        leaf = false;      
    }
    
    public ExpressionNode getRightTree() {
        if (right == null) return null;
        
        if (right.isExpressionNode())
            return (ExpressionNode) right;
        else 
            return new ExpressionNode(kernel, right);
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return getCopy(kernel);
    }
    
    /** copy the whole tree structure except leafs */
    public ExpressionNode getCopy(Kernel kernel) {
        //Application.debug("getCopy() input: " + this);   
        ExpressionNode newNode = null;
        ExpressionValue lev = null, rev = null;                
        
        if (left != null) lev = copy(left, kernel);
        if (right != null) rev = copy(right, kernel);
        
        if (lev != null) {
            newNode = new ExpressionNode(kernel, lev, operation, rev);
            newNode.leaf = leaf;
        } else
			// something went wrong
			   return null;
               
        // set member vars that are not set by constructors
        newNode.forceVector = forceVector;
        newNode.forcePoint = forcePoint;
        //Application.debug("getCopy() output: " + newNode);   
        return newNode;
    }        
    
    /** deep copy except for GeoElements */
    public static ExpressionValue copy(ExpressionValue ev, Kernel kernel) {
        if (ev == null) return null;
        
        ExpressionValue ret = null;        
        //Application.debug("copy ExpressionValue input: " + ev);        
        if (ev.isExpressionNode()) {
        	ExpressionNode en = (ExpressionNode) ev;
            ret = en.getCopy(kernel); 
        } 
        // deep copy
        else if (ev.isPolynomialInstance()
        			|| ev.isConstant()
        			|| ev instanceof Command) 
        {           
            ret = ev.deepCopy(kernel);              
        }        
        else {            
            ret = ev;                        
        }           
        //Application.debug("copy ExpressionValue output: " + ev);        
        return ret;        
    }                  
    
    /**
     * Replaces all ExpressionNodes in tree that are leafs (=wrappers) by their leaf
     * objects (of type ExpressionValue).
     */
    final public void simplifyLeafs() {         
        if (left.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) left;
            if (node.leaf) {            	   
            	left = node.left;
            	simplifyLeafs();
            }                        
        }       
        
        if (right != null) {
        	if (right.isExpressionNode()) {        
	            ExpressionNode node = (ExpressionNode) right;
	            if (node.leaf) {             	            	         	
	            	right = node.left;
	            	simplifyLeafs();
	            }      
        	}        	
        }               
    }
    
    /**
     * Replaces all Command objects in tree by their evaluated GeoElement
     * objects. 
     */
    final private void simplifyAndEvalCommands() {   
    	// don't evaluate any commands for the CAS here
    	if (kernel.isResolveVariablesForCASactive()) return;
    	
        if (left.isExpressionNode()) {
        	((ExpressionNode) left).simplifyAndEvalCommands();                      
        }
        else if (left instanceof Command) {
        	left = ((Command) left).evaluate();
        }
        
        if (right != null) {
        	if (right.isExpressionNode()) {        
        		((ExpressionNode) right).simplifyAndEvalCommands(); 	             
        	}
        	 else if (right instanceof Command) {
        		 right = ((Command) right).evaluate();
             }
        }               
    }
    
    /** 
     * Replaces all constant parts in tree by their values
     */
    final public void simplifyConstants() {                 
        if (left.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) left;
            if (left.isConstant()) {            	
            	ExpressionValue eval = node.evaluate();
            	if (eval.isNumberValue()) {
            		// we only simplify numbers that have integer values
            		if (kernel.isInteger(((NumberValue) eval).getDouble())) 
            			left = eval;            		
            	} else {
            		left = eval;
            	}
            } else
                node.simplifyConstants();
        }
        
        if (right != null && right.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) right;
            if (right.isConstant()) {
            	ExpressionValue eval = node.evaluate();
	        	if (eval.isNumberValue()) {
	        		// we only simplify numbers that have integer values
	        		if (kernel.isInteger(((NumberValue) eval).getDouble())) 
	        			right = eval;            		
	        	} else {
	        		right = eval;
	        	}
            } else 
                node.simplifyConstants();
        }             
    }
       
     /**
     * interface ExpressionValue implementation
     */         
    final public ExpressionValue evaluate() {    	  
        if (leaf) return left.evaluate(); // for wrapping ExpressionValues as ValidExpression
               
        ExpressionValue lt, rt;
        MyDouble num;
        MyBoolean bool;
        GeoVec2D vec;
        MyStringBuffer msb;
        Polynomial poly;
                        
        lt = left.evaluate(); // left tree
        rt = right.evaluate(); // right tree      

        // handle list operations first 
        
        // matrix * 2D vector   
        if (lt.isListValue()) {
        	if (operation == MULTIPLY && rt.isVectorValue()) { 
            	MyList myList = ((ListValue) lt).getMyList();
            	boolean isMatrix = myList.isMatrix();
            	int rows = myList.getMatrixRows();
            	int cols = myList.getMatrixCols();
            	if (isMatrix && rows == 2 && cols == 2)
            	{
            		GeoVec2D myVec = ((VectorValue) rt).getVector();
            		// 2x2 matrix
            		myVec.multiplyMatrix(myList);
            		
            		return myVec;
            	}
            	else if (isMatrix && rows == 3 && cols == 3)
            	{
            		GeoVec2D myVec = ((VectorValue) rt).getVector();
            		// 3x3 matrix, assume it's affine
            		myVec.multiplyMatrixAffine(myList, rt);
            		return myVec;
            	}

	        }
        	else if (operation != EQUAL_BOOLEAN  // added EQUAL_BOOLEAN Michael Borcherds 2008-04-12	
	            		&& !rt.isTextValue()) // bugfix "" + {1,2} Michael Borcherds 2008-06-05
        	{ 
	            	MyList myList = ((ListValue) lt).getMyList();
	            	// list lt operation rt
	            	myList.applyRight(operation, rt);
	            	return myList;
	        }	        
        }
        else if (rt.isListValue() && operation != EQUAL_BOOLEAN // added EQUAL_BOOLEAN Michael Borcherds 2008-04-12	
        	&& !lt.isTextValue()) { // bugfix "" + {1,2} Michael Borcherds 2008-06-05
        	MyList myList = ((ListValue) rt).getMyList();
        	// lt operation list rt
        	myList.applyLeft(operation, lt);
        	return myList;
        }
       	 
        // NON-List operations (apart from EQUAL_BOOLEAN and list + text)
        switch (operation) {
            /*
        case NO_OPERATION:                      
            if (lt.isNumber())
                return ((NumberValue)lt).getNumber();
            else if (lt.isVector())
                return ((VectorValue)lt).getVector();
            else if (lt.isText()) 
                return ((TextValue)lt).getText();
            else {                 
                throw new MyError(app, "Unhandeled ExpressionNode entry: " + lt);
            }*/
        
        // spreadsheet reference: $A1, A$1, $A$1
        case $VAR_ROW: 
        case $VAR_COL:
        case $VAR_ROW_COL:
        	return lt;
    
        /*
         * BOOLEAN operations
         */    
        case NOT:
        	// NOT boolean
        	if (lt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(!bool.getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean",  strNOT, lt.toString()};
                throw new MyError(app, str);
            }
        	
        
        case OR:
        	// boolean OR boolean
        	if (lt.isBooleanValue() && rt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(bool.getBoolean() || ((BooleanValue)rt).getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean", lt.toString(), strOR,  rt.toString() };
                throw new MyError(app, str);
            }
        	
        case AND:
        	// boolean AND boolean
        	if (lt.isBooleanValue() && rt.isBooleanValue()) {
        		bool = ((BooleanValue) lt).getMyBoolean();
        		bool.setValue(bool.getBoolean() && ((BooleanValue)rt).getBoolean());
        		return bool;
        	}
        	else { 
                String [] str = { "IllegalBoolean", lt.toString(), strAND,  rt.toString() };
                throw new MyError(app, str);
            }  
        	
    	/*
         * COMPARING operations
         */  
               
        case EQUAL_BOOLEAN:
        	{
        		MyBoolean b = evalEquals(lt, rt);
        		if (b == null) {
        			String [] str = { "IllegalComparison", lt.toString(), strEQUAL_BOOLEAN,  rt.toString() };
                    throw new MyError(app, str);
        		} else {
        			return b;
        		}
        	}
        	
        case NOT_EQUAL:
	        {
	    		MyBoolean b = evalEquals(lt, rt);
	    		if (b == null) {
	    			String [] str = { "IllegalComparison", lt.toString(), strNOT_EQUAL,  rt.toString() };
	                throw new MyError(app, str);
	    		} else {
	    			// NOT equal
	    			b.setValue(!b.getBoolean());
	    			return b;
	    		}
            }         	
                	
        case LESS:
        	// number < number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreater(
    					((NumberValue)rt).getDouble(),
						((NumberValue)lt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), "<",  rt.toString() };
                throw new MyError(app, str);
            } 
        
        case GREATER:
        	// number > number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreater(
    					((NumberValue)lt).getDouble(),
						((NumberValue)rt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), ">",  rt.toString() };
                throw new MyError(app, str);
            } 
        	
        case LESS_EQUAL:
        	// number <= number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreaterEqual(
    					((NumberValue)rt).getDouble(),
						((NumberValue)lt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), strLESS_EQUAL,  rt.toString() };
                throw new MyError(app, str);
            } 
        	
        case GREATER_EQUAL:
        	// number >= number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isGreaterEqual(
    					((NumberValue)lt).getDouble(),
						((NumberValue)rt).getDouble()	
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), strGREATER_EQUAL,  rt.toString() };
                throw new MyError(app, str);
            }         	  
        	        	
        case PARALLEL:
        	// line parallel to line
        	if (lt instanceof GeoLine && rt instanceof GeoLine) {
				return new MyBoolean(((GeoLine)lt).isParallel((GeoLine)rt));        		
        	} else { 
                String [] str = { "IllegalComparison", lt.toString(), strPARALLEL,  rt.toString() };
                throw new MyError(app, str);
            }         
        	
        case PERPENDICULAR:
        	// line perpendicular to line
        	if (lt instanceof GeoLine && rt instanceof GeoLine) {
				return new MyBoolean(((GeoLine)lt).isPerpendicular((GeoLine)rt));   
        	} else { 
                String [] str = { "IllegalComparison", lt.toString(), strPERPENDICULAR,  rt.toString() };
                throw new MyError(app, str);
            }         	
        
        /*
         * ARITHMETIC operations
         */ 
        case PLUS:                             
            // number + number
            if (lt.isNumberValue() && rt.isNumberValue()) {           
                num = ((NumberValue)lt).getNumber();                
                MyDouble.add(num, ((NumberValue)rt).getNumber(), num);
                return num;
            }
            // vector + vector
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.add(vec, ((VectorValue)rt).getVector(), vec);                                         
                return vec;
            }     
            // vector + number (for complex addition)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.add(vec, ((NumberValue)rt) , vec);                                         
                return vec;
            }     
            // number + vector (for complex addition)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.add(vec, ((NumberValue)lt) , vec);                                         
                return vec;
            }     
            // text concatenation (left)
            else if (lt.isTextValue()) { 
                msb = ((TextValue)lt).getText();
                if (holdsLaTeXtext) {
                	msb.append(rt.toLaTeXString(false));  
                } else {
	                if (rt.isGeoElement()) {	                	
	                    GeoElement geo = (GeoElement) rt;                   
	                    msb.append(geo.toDefinedValueString());	                    
	                } else {      
	            		msb.append(rt.toValueString());
	                }         
                }
                return msb;
            } // text concatenation (right)
            else if (rt.isTextValue()) { 
                msb = ((TextValue)rt).getText();
                if (holdsLaTeXtext) {
            		msb.insert(0, lt.toLaTeXString(false));                		
            	} else {
	                if (lt.isGeoElement()) {
	                    GeoElement geo = (GeoElement) lt;                   
	                    msb.insert(0, geo.toDefinedValueString());  
	                } else {                	
	                	msb.insert(0, lt.toValueString());                		                	                                      
	                }   
            	}                
                return msb;
            } 
            // polynomial + polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {         
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.add((Polynomial)rt);                
                return poly;
            }           
            else {    
                String [] str = { "IllegalAddition", lt.toString(), "+",  rt.toString() };
                throw new MyError(app, str);
            }
        
        case MINUS:            
            // number - number
            if (lt.isNumberValue() && rt.isNumberValue()) {
                num = ((NumberValue)lt).getNumber();                
                MyDouble.sub(num, ((NumberValue)rt).getNumber(), num);
                return num;
            }
            // vector - vector
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.sub(vec, ((VectorValue)rt).getVector(), vec);                                         
                return vec;
            }
            // vector - number (for complex subtraction)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.sub(vec, ((NumberValue)rt), vec);                                         
                return vec;
            }     
            // number - vector (for complex subtraction)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.sub(((NumberValue)lt), vec, vec);                                         
                return vec;
            }     
            // polynomial - polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) {                 
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.sub((Polynomial)rt);                
                return poly;
            }           
            else { 
                String [] str = { "IllegalSubtraction", lt.toString(), "-", rt.toString() };
                throw new MyError(app, str);
            }
        
        case MULTIPLY:            
            // number * ...
            if (lt.isNumberValue()) {
                // number * number
                if (rt.isNumberValue()) {
                    num = ((NumberValue)lt).getNumber();                               
                    MyDouble.mult(num, ((NumberValue)rt).getNumber(), num);       
                    return num;
                } 
                // number * vector
                else if (rt.isVectorValue()) { 
                    vec = ((VectorValue)rt).getVector();                
                    GeoVec2D.mult(vec, ((NumberValue)lt).getDouble(), vec);
                    return vec;
                }                        	  
                else {                	
                    String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                    throw new MyError(app, str);    
                }
            }
            // vector * ...
            else if (lt.isVectorValue()) {
                //  vector * number
                if (rt.isNumberValue()) { 
                    vec = ((VectorValue)lt).getVector();                
                    GeoVec2D.mult(vec, ((NumberValue)rt).getDouble(), vec);
                    return vec;
                }            
                // vector * vector (inner product)
                else if (rt.isVectorValue()) { 
                    num = new MyDouble(kernel);
                    vec = ((VectorValue)lt).getVector();
                    GeoVec2D.inner(vec, ((VectorValue)rt).getVector(), num);
                    return num;
                }      
                else {    
                    String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                    throw new MyError(app, str);    
                }
            }                            
            // polynomial * polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                poly = new Polynomial(kernel, (Polynomial)lt);
                poly.multiply((Polynomial)rt);                
                return poly;
            }   
            else {    
                String [] str = { "IllegalMultiplication", lt.toString(), "*", rt.toString() };
                throw new MyError(app, str);    
            }
            
        case DIVIDE:
            if (rt.isNumberValue()) {
                //  number / number
                 if (lt.isNumberValue()) {
                     num = ((NumberValue)lt).getNumber();                
                     MyDouble.div(num, ((NumberValue)rt).getNumber(), num);
                     return num;
                 }            
                 // vector / number
                 else if (lt.isVectorValue()) { 
                     vec = ((VectorValue)lt).getVector();                
                     GeoVec2D.div(vec, ((NumberValue)rt).getDouble(), vec);
                     return vec;
                 }          
                else { 
                       String [] str = { "IllegalDivision", lt.toString(), "/", rt.toString() };
                       throw new MyError(app, str);
                 }   
            }          
            // polynomial / polynomial
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                // the divisor must be a polynom of degree 0
                if (((Polynomial)rt).degree() != 0) {
                    String [] str = { "DivisorMustBeConstant", lt.toString(), "/", rt.toString() };
                    throw new MyError(app, str);
                }
                
                poly = new Polynomial(kernel, (Polynomial)lt);                
                poly.divide((Polynomial)rt);                
                return poly;
            }
            // vector / vector (complex division Michael Borcherds 2007-12-09)
            else if (lt.isVectorValue() && rt.isVectorValue()) { 
                    vec = ((VectorValue)lt).getVector();
                    GeoVec2D.complexDivide(vec, ((VectorValue)rt).getVector(), vec);                                         
                    return vec;
                    
            }                
            // number / vector (complex division Michael Borcherds 2007-12-09)
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                	vec = ((VectorValue)rt).getVector(); // just to initialise vec
                    GeoVec2D.complexDivide( (NumberValue)lt, ((VectorValue)rt).getVector(), vec);                                         
                    return vec;
                    
            }                
            else { 
                String [] str = { "IllegalDivision", lt.toString(), "/", rt.toString() };
                throw new MyError(app, str);
            }
        case COMPLEXMULTIPLY:
            if (lt.isVectorValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.complexMultiply(vec, ((VectorValue)rt).getVector(), vec);                                         
                return vec;
                
            }                
            else if (lt.isNumberValue() && rt.isVectorValue()) { 
                vec = ((VectorValue)rt).getVector();
                GeoVec2D.complexMultiply(vec, (NumberValue)lt, vec);                                         
                return vec;
                
            }                
            else if (rt.isNumberValue() && lt.isVectorValue()) { 
                vec = ((VectorValue)lt).getVector();
                GeoVec2D.complexMultiply(vec, (NumberValue)rt, vec);                                         
                return vec;
                
            }                
            else { 
                String [] str = { "IllegalComplexMultiplication", lt.toString(), strCOMPLEXMULTIPLY, rt.toString() };
                throw new MyError(app, str);
            }
                                               
        case POWER:
            // number ^ number
            if (lt.isNumberValue() && rt.isNumberValue()) {
            	num = ((NumberValue)lt).getNumber();  
            	double base = num.getDouble();
            	MyDouble exponent = ((NumberValue)rt).getNumber();
            	
            	// special case: e^exponent (Euler number)
            	if (base == Math.E) {
            		return exponent.exp();
            	}
            	
				// special case: left side is negative and 
            	// right side is a fraction a/b with a and b integers
            	// x^(a/b) := (x^a)^(1/b)
            	if (base < 0 && right.isExpressionNode()) {            		
            		ExpressionNode node = (ExpressionNode) right;
            		if (node.operation == DIVIDE) {
            			// check if we have a/b with a and b integers
            			double a = ((NumberValue) node.left.evaluate()).getDouble(); 
            			long al = Math.round(a);
            			if (kernel.isEqual(a, al)) { // a is integer         				
            				double b = ((NumberValue) node.right.evaluate()).getDouble();                 			
            				long bl = Math.round(b);
            				if (b == 0)
                				// (x^a)^(1/0)
                				num.set(Double.NaN);            				
                			else if (kernel.isEqual(b, bl)) { // b is integer
                				// divide through greatest common divisor of a and b
                				long gcd = Kernel.gcd(al, bl);
                				al = al / gcd;
                				bl = bl / gcd;
                				
                				// we will now evaluate (x^a)^(1/b) instead of x^(a/b)                				
                				// set base = x^a
                				if (al != 1) base = Math.pow(base, al);             						            						
            					if (base > 0) {             						
            						// base > 0 => base^(1/b) is no problem
            						num.set(Math.pow(base, 1d/bl));            						
            					}           
            					else { // base < 0            				
	                				boolean oddB = Math.abs(bl) % 2 == 1;
	                				if (oddB) {      	 
	            						// base < 0 and b odd: (base)^(1/b) = -(-base^(1/b)) 
	            						num.set(-Math.pow(-base, 1d/bl));	                					
	                				} else {         	                					
	                					// base < 0 and a & b even: (base)^(1/b) = undefined 
	                					num.set(Double.NaN);	                				
	                				}
            					}
                				return num;
                			}
            			}            	
            		}
            	}
            	
            	// standard case                           
                MyDouble.pow(num, exponent, num);
                return num;
            }               
            // vector ^ 2 (inner product)
            else if (lt.isVectorValue() && rt.isNumberValue()) { 
               // if (!rt.isConstant()) {
               //     String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
               //     throw new MyError(app, str);
               // }                
                num = ((NumberValue)rt).getNumber();                
                if (num.getDouble() == 2.0) {                    
                    vec = ((VectorValue)lt).getVector();
                    GeoVec2D.inner(vec, vec, num);                
                    return num;                       
                } else {
                    String [] str = { "IllegalExponent", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }                                               
            }  
            // polynomial ^ number
            else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()) { 
                // the exponent must be a number
                if (((Polynomial)rt).degree() != 0) {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }          
                
                // is the base also a number? In this case pull base^exponent together into lt polynomial
             	boolean baseIsNumber = ((Polynomial)lt).degree() == 0;
            	if (baseIsNumber) {
            		Term base = ((Polynomial)lt).getTerm(0);
            		Term exponent = ((Polynomial)rt).getTerm(0);
            		Term newBase = new Term(kernel,
            						new ExpressionNode(kernel, base.getCoefficient(), 
            								ExpressionNode.POWER, 
            								exponent.getCoefficient()),
            						"");
            		            		
            		return new Polynomial(kernel, newBase);            		
            	}
             	
             	// number is not a base
                if (!rt.isConstant()) {
                    String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
                
                // get constant coefficent of given polynomial                
                double exponent = ((Polynomial)rt).getConstantCoeffValue();
                if ((kernel.isInteger(exponent) && (int) exponent >= 0)) 
                {
                    poly = new Polynomial(kernel, (Polynomial)lt);
                    poly.power((int) exponent);                
                    return poly;
                } else {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
            }                
            else { 
                Application.debug("power: lt :" + lt.getClass() + ", rt: " + rt.getClass());
                String [] str = { "IllegalExponent", lt.toString(), "^", rt.toString() };
                throw new MyError(app, str);
            }
            
        case COS:            
            // cos(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cos();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COS, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "cos", lt.toString() };
                throw new MyError(app, str);
            }            
            
        case SIN:
            // sin(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sin();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SIN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sin", lt.toString() };
                throw new MyError(app, str);
            }
            
        case TAN:
            // tan(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().tan();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.TAN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "tan", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCCOS:
            // arccos(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().acos();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCCOS, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arccos", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCSIN:
            // arcsin(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().asin();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCSIN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arcsin", lt.toString() };
                throw new MyError(app, str);
            }
            
        case ARCTAN:
            // arctan(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().atan();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ARCTAN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "arctan", lt.toString() };
                throw new MyError(app, str);
            }
        
        case COSH:            
            // cosh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cosh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.COSH, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "cosh", lt.toString() };
                throw new MyError(app, str);
            }      

        case SINH:
            // sinh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sinh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SINH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sinh", lt.toString() };
                throw new MyError(app, str);
            }
        
        case TANH:
            // tanh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().tanh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.TANH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "tanh", lt.toString() };
                throw new MyError(app, str);
            }

        case ACOSH:            
            // acosh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().acosh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ACOSH, null),
                                ""
                            )
                       );                   
            }                                    
            else {
                String [] str = { "IllegalArgument", "acosh", lt.toString() };
                throw new MyError(app, str);
            }      

        case ASINH:
            // asinh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().asinh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ASINH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "asinh", lt.toString() };
                throw new MyError(app, str);
            }
    
        case ATANH:
            // tanh(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().atanh();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ATANH, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "atanh", lt.toString() };
                throw new MyError(app, str);
            }
  
        case EXP:
            // exp(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().exp();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.EXP, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "exp", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "log", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG10:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log10();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG10, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "lg", lt.toString() };
                throw new MyError(app, str);
            }
            
        case LOG2:
            // log(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().log2();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.LOG2, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "ld", lt.toString() };
                throw new MyError(app, str);
            }
            
        case SQRT:
            // sqrt(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sqrt();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SQRT, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sqrt", lt.toString() };
                throw new MyError(app, str);
            }
            
        case CBRT:
            // cbrt(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().cbrt();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CBRT, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "cbrt", lt.toString() };
                throw new MyError(app, str);
            }
                        
            
        case ABS:
            // abs(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().abs();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ABS, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "abs", lt.toString() };
                throw new MyError(app, str);                
            }
            
        case SGN:
            // sgn(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().sgn();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.SGN, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "sgn", lt.toString() };
                throw new MyError(app, str);
            }

        case FLOOR:
            // floor(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().floor();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FLOOR, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "floor", lt.toString() };
                throw new MyError(app, str);
            }
            
        case CEIL:
            // ceil(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().ceil();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.CEIL, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "ceil", lt.toString() };
                throw new MyError(app, str);
            }       

        case ROUND:
            // ceil(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().round();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.ROUND, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "round", lt.toString() };
                throw new MyError(app, str);
            }    
            
        case FACTORIAL:
            // factorial(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().factorial();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FACTORIAL, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", lt.toString(), " !" };
                throw new MyError(app, str);
            }   
            
        case GAMMA:
            // ceil(number)
            if (lt.isNumberValue())
				return ((NumberValue)lt).getNumber().gamma();
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                lt = ((Polynomial) lt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.GAMMA, null),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", "gamma", lt.toString() };
                throw new MyError(app, str);
            }
            
        case RANDOM:
            // random()
        	// note: left tree holds MyDouble object to set random number
        	// in randomize()
        	return ((NumberValue) lt).getNumber();
                            
        case XCOORD:
            // x(vector)
            if (lt.isVectorValue())
				return new MyDouble(kernel, ((VectorValue)lt).getVector().getX());
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                  lt = ((Polynomial) lt).getConstantCoefficient();                    
                  return new Polynomial( kernel,
                              new Term(kernel, 
                                  new ExpressionNode(kernel, lt, ExpressionNode.XCOORD, null),
                                  ""
                              )
                         );                   
              }                                              
            else { 
                 String [] str = { "IllegalArgument", "x(", lt.toString(), ")" };
                throw new MyError(app, str);
            }
        
        case YCOORD:
            // y(vector)
            if (lt.isVectorValue())
				return new MyDouble(kernel, ((VectorValue)lt).getVector().getY());
			else if (lt.isPolynomialInstance() && ((Polynomial) lt).degree() == 0) {                                 
                  lt = ((Polynomial) lt).getConstantCoefficient();                    
                  return new Polynomial( kernel,
                              new Term(kernel, 
                                  new ExpressionNode(kernel, lt, ExpressionNode.YCOORD, null),
                                  ""
                              )
                         );                   
              }                                            
            else { 
                 String [] str = { "IllegalArgument", "y(", lt.toString(), ")" };
                throw new MyError(app, str);
            }                
       
        case FUNCTION:      
            // function(number)
            if (rt.isNumberValue() && lt instanceof Functional) {    
            	NumberValue arg = (NumberValue) rt;                     			            
            	return arg.getNumber().apply((Functional)lt);
            }
            else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.FUNCTION, rt),
                                ""
                            )
                       );                   
            }     
            else {                 
            	//Application.debug("FUNCTION lt: " + lt + ", " + lt.getClass() + " rt: " + rt + ", " + rt.getClass());
                String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }
            
        case VEC_FUNCTION:      
            // vecfunction(number)
            if (rt.isNumberValue()) {    
            	NumberValue arg = (NumberValue) rt;
            	return ((ParametricCurve)lt).evaluateCurve(arg.getDouble());            	
            }
            else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.VEC_FUNCTION, rt),
                                ""
                            )
                       );                   
            }     
            else { 
                //Application.debug("lt: " + lt.getClass() + " rt: " + rt.getClass());
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }            
            
        case DERIVATIVE:
        //Application.debug("DERIVATIVE called");
            // derivative(function, order)
            if (rt.isNumberValue()) {            	
            	return ((Functional)lt).
	            	getGeoDerivative((int)Math.round(((NumberValue)rt).getDouble()));
            }				
			else if (lt.isPolynomialInstance() &&
                rt.isPolynomialInstance() && ((Polynomial) rt).degree() == 0) {  
                lt = ((Polynomial) lt).getConstantCoefficient();                               
                rt = ((Polynomial) rt).getConstantCoefficient();                    
                return new Polynomial( kernel,
                            new Term(kernel, 
                                new ExpressionNode(kernel, lt, ExpressionNode.DERIVATIVE, rt),
                                ""
                            )
                       );                   
            }     
            else { 
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }                 
                
        default:
            throw new MyError(app, "ExpressionNode: Unhandled operation");
        
        }       
    }
    
    /**
     * 
     * @param lt
     * @param rt
     * @return false if not defined
     */
    private MyBoolean evalEquals(ExpressionValue lt, ExpressionValue rt) {
    	//  nummber == number
    	if (lt.isNumberValue() && rt.isNumberValue())
			return new MyBoolean(
    			kernel.isEqual(
    				((NumberValue)lt).getDouble(),
					((NumberValue)rt).getDouble()
				)
    		);
    	else if (lt.isBooleanValue() && rt.isBooleanValue())
			return new MyBoolean(
					((BooleanValue)lt).getBoolean() == ((BooleanValue)rt).getBoolean()
				);      
    	
    	// needed for eg If[""=="a",0,1]
    	// when lt and rt are MyStringBuffers
    	else if (lt.isTextValue() && rt.isTextValue()) {
    		
    		String strL = ((TextValue)lt).toValueString();
    		String strR = ((TextValue)rt).toValueString();
    		
    		// needed for eg Sequence[If[Element[list1,i]=="b",0,1],i,i,i]
    		if (strL == null || strR == null)
    			return new MyBoolean(false);
    		
			return new MyBoolean(strL.equals(strR));      
    	}
    	else if (lt.isGeoElement() && rt.isGeoElement()) {
    		GeoElement geo1 = (GeoElement) lt;
    		GeoElement geo2 = (GeoElement) rt;
    		
    		// Michael Borcherds 2008-05-01
    		// replaced following code with one line:
    		return new MyBoolean(geo1.isEqual(geo2));
    		
    		/*
    		if (geo1.isGeoPoint() && geo2.isGeoPoint()) {
    			return new MyBoolean(((GeoPoint)geo1).equals((GeoPoint) geo2));
    		}
    		else if (geo1.isGeoLine() && geo2.isGeoLine()) {
    			return new MyBoolean(((GeoLine)geo1).equals((GeoLine) geo2));
    		}
    		else if (geo1.isGeoConic() && geo2.isGeoConic()) {
    			return new MyBoolean(((GeoConic)geo1).equals((GeoConic) geo2));
    		}
    		else if (geo1.isGeoVector() && geo2.isGeoVector()) {
    			return new MyBoolean(((GeoVector)geo1).equals((GeoVector) geo2));
    		}
    		else if (geo1.isGeoList() && geo2.isGeoList()) { // Michael Borcherds 2008-04-12
    			return new MyBoolean(((GeoList)geo1).equals((GeoList) geo2));
    		}*/
    	}      
    	
    	return new MyBoolean(false);
    }
    
    /** 
     *  look for Variable objects in the tree and replace them
     *  by their resolved GeoElement
     */
    public void resolveVariables() {   
    	doResolveVariables();
    	simplifyAndEvalCommands();
    	simplifyLeafs();    	
    }       
    	
    private void doResolveVariables() {    	    	
        // resolve left wing
    	if (left.isVariable()) {
            left = ((Variable) left).resolveAsExpressionValue();                
        } else
        	left.resolveVariables();
    	
        // resolve right wing
        if (right != null) {
            if (right.isVariable()) {
                right = ((Variable) right).resolveAsExpressionValue();
            } 
            else
            	right.resolveVariables();
        }                       
    }
    
    /** 
     *  Looks for Variable objects that hold String var in the tree and 
     *  replaces them by their newOb.
     *
    public void replaceSpecificVariable(String var, ExpressionValue newOb) {
    	// left wing
    	if (left.isVariable()) {
            if (var.equals(((Variable) left).getName()))
            		left = newOb;                
        }
    	else if (left.isExpressionNode()) {
    		((ExpressionNode) left).replaceSpecificVariable(var, newOb);
    	}
        	    	
        // right wing
        if (right != null) {
        	if (right.isVariable()) {
                if (var.equals(((Variable) right).getName()))
                	right = newOb;                
            }
        	else if (right.isExpressionNode()) {
        		((ExpressionNode) right).replaceSpecificVariable(var, newOb);
        	}
        }     
    } */
    
    
    /** 
     *  look for GeoFunction objects in the tree and replace them
     *  by FUNCTION ExpressionNodes. This makes operations like
     *  f + g possible by changing this to f(x) + g(x)
     *
    public void wrapGeoFunctionsAsExpressionNode() {     
    	Polynomial polyX = new Polynomial(kernel, "x");
    	
        // left wing
        if (left.isExpressionNode()) {
            ((ExpressionNode)left).wrapGeoFunctionsAsExpressionNode();
        }
        else if (left instanceof GeoFunction) {
            left = new ExpressionNode(kernel, left, ExpressionNode.FUNCTION, polyX);           
        } 
        
        // resolve right wing
        if (right != null) {
            if (right.isExpressionNode()) {
                ((ExpressionNode)right).wrapGeoFunctionsAsExpressionNode();
            }
            else if (right instanceof GeoFunction) {
            	right = new ExpressionNode(kernel, right, ExpressionNode.FUNCTION, polyX);        
            } 
        }                       
    }*/
        
    /**
     * returns true if there is at least one Polynomial in the tree
     */
    public boolean includesPolynomial() {               
        if (left.isExpressionNode()) {             
            if (((ExpressionNode)left).includesPolynomial()) return true;            
        }                                                               
        else if (left.isPolynomialInstance())
			return true;          
        
        if (right != null) {
            if (right.isExpressionNode()) {
                if (((ExpressionNode)right).includesPolynomial()) return true;            
            } else if (right.isPolynomialInstance())
                return true;                    
        }
        
        return false;        
    }
    
    /**
    * Returns true if this tree contains only Polynomials that
    * return true for isX()
    */    
   final public boolean isFunctionInX() {                
        boolean isFunction = true;
        if (left.isExpressionNode()) {             
            isFunction = ((ExpressionNode)left).isFunctionInX();            
        } else if (left.isPolynomialInstance()) {            
            isFunction = ((Polynomial) left).isX();
        }      
        if (!isFunction) return false;
       
       if (right != null) {
        if (right.isExpressionNode()) {
            isFunction =((ExpressionNode)right).isFunctionInX();            
        }else if (right.isPolynomialInstance()) {            
            isFunction = ((Polynomial) right).isX();
        }
       }
                 
       return isFunction;
   }
   
   /**
    * Returns true if this tree includes a division by val
    */    
   final public boolean includesDivisionBy(ExpressionValue val) {                      
        if (operation == DIVIDE) {
        	if (right.contains(val))
        		return true;
        	 
        	if (left.isExpressionNode() && 
        			((ExpressionNode) left).includesDivisionBy(val))
        		return true;        	
        } else {
        	if (left.isExpressionNode() && 
        			((ExpressionNode) left).includesDivisionBy(val))
        		return true;        	
        		
        	if (right != null && right.isExpressionNode() && 
        			((ExpressionNode) right).includesDivisionBy(val))
        		return true;
        }
        
        return false;
   }
     
   
    /**
     * Replaces all Polynomials in tree by function variable
     * @return number of replacements done
     */
    int replacePolynomials(FunctionVariable x) {
    	int replacements = 0;
    	
        // left tree
        if (left.isExpressionNode()) {
        	replacements += ((ExpressionNode) left).replacePolynomials( x);
        }
        else if (left.isPolynomialInstance()) {
            left = x;
            replacements++;
        }
        
        // right tree
        if (right != null) {
            if (right.isExpressionNode()) {
            	replacements += ((ExpressionNode) right).replacePolynomials(x);
            }
            else if (right.isPolynomialInstance()) {
                right = x;
                replacements++;
            }
        }
         
        return replacements;
    }
    
    /**
     * Replaces every oldOb by newOb in this tree 
     * @return resulting expression node
     */
    public ExpressionNode replace(ExpressionValue oldOb, ExpressionValue newOb) {
        if (this == oldOb) {
            if (newOb.isExpressionNode()) 
                return (ExpressionNode) newOb;
            else
                return new ExpressionNode(kernel, newOb);
        }                                                       
                                                        
        // left tree
        if (left == oldOb) {
            left = newOb;
        } else if (left.isExpressionNode()) {
            left = ((ExpressionNode) left).replace(oldOb, newOb);
        } 
        
        // right tree
        if (right != null) {
	        if (right == oldOb) {        	
	            right = newOb;
	        } else if (right.isExpressionNode()) {
	            right = ((ExpressionNode) right).replace(oldOb, newOb);
	        } 

        }
        return this;
    }
    
    /**
     * Replaces geo and all its dependent geos in this tree by
     * copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {                                                
        // left tree
        if (left.isGeoElement()) {
        	GeoElement treeGeo = (GeoElement) left;
        	if (left == geo || treeGeo.isChildOf(geo)) {        		 
        		left = treeGeo.copyInternal(treeGeo.getConstruction());        		
        	}
        }         
        else if (left.isExpressionNode()) {
            ((ExpressionNode) left).replaceChildrenByValues(geo);
        } 
        // handle command arguments
	    else if (left instanceof Command) {
	       ((Command) left).replaceChildrenByValues(geo); 	       
	     }
        
        // right tree
        if (right != null) {
        	if (right.isGeoElement()) {
        		GeoElement treeGeo = (GeoElement) right;
            	if (right == geo || treeGeo.isChildOf(geo)) {            		     
            		right = treeGeo.copyInternal(treeGeo.getConstruction());
            	}
            }         
            else if (right.isExpressionNode()) {
                ((ExpressionNode) right).replaceChildrenByValues(geo);
            } 
        	// handle command arguments
    	    else if (right instanceof Command) {
    	       ((Command) right).replaceChildrenByValues(geo); 	       
    	     }
        }
    }
    
   
    
    /** 
     * Returns true when the given object is found in this expression tree.
     */
    final public boolean contains(ExpressionValue ev) {
        if (leaf)
            return left.contains(ev);
		else
			return left.contains(ev) || right.contains(ev);            
    }
    
    /**
     * transfers every non-polynomial in this tree to a polynomial.
     * This is needed to enable polynomial simplification by evaluate()
     */    
    final void makePolynomialTree() {                
        // transfer left subtree        
        if (left.isExpressionNode()) {
            ((ExpressionNode)left).makePolynomialTree();
        }      
        else if (!(left.isPolynomialInstance()) ) { 
            left = new Polynomial(kernel, new Term(kernel, left, ""));
        }
    
        
        // transfer right subtree
        if (right != null) {
            if (right.isExpressionNode()) {
                ((ExpressionNode)right).makePolynomialTree();
            }                  
            else if (!(right.isPolynomialInstance()) ) {                
                right = new Polynomial(kernel, new Term(kernel, right, ""));
            }
        }
    }
    
    /** returns true, if there are no variable objects
     * in the subtree
     */
    final public boolean isConstant() {
        if (isLeaf()) 
        	return left.isConstant();
        else
        	return left.isConstant() && right.isConstant();                  
    }
    
    /* *
     * returns true, if all variables are angles (GeoAngle)
     * or if a number followed by '�' or "rad" was entered (e.g.
     * 30� or 20 rad)
     *
    final public boolean isAngle() {              
        // check if evaluation states that this is an angle
        // get MyDouble of evaluation
        ExpressionValue ev = evaluate();
        if (ev instanceof MyDouble) {            
            if (((MyDouble)ev).isAngle()) return true;
        } else return false; // only a number can be an angle
        
        // all veriables must be angles
        GeoElement [] vars = getGeoElementVariables();                
        if (vars == null || vars.length == 0) return false;        
        for (int i=0; i < vars.length; i++) {          
            if (!(vars[i] instanceof GeoAngle)) return false;            
        }
        return true;
    }
     */
    
    /**
     * returns true, if no variable is a point (GeoPoint)
     */
    final public boolean isVectorValue() {
        if (forcePoint) return false;
        if (forceVector) return true;
        
        GeoElement [] vars = getGeoElementVariables();                
        if (vars == null || vars.length == 0) return false;
                        
        // at least one vector has to be part of the dependent tree
        boolean vecFound = false;
        for (int i=0; i < vars.length; i++) {          
            if (vars[i].isGeoPoint() ) return false;  
            if (!vecFound && vars[i].isGeoVector()) vecFound = true;
        }
        return vecFound;
    }
    
    public void forceVector() {
        // this expression should be considered as a vector, not a point
        forceVector = true;
    }
    
    public void forcePoint() {
        // this expression should be considered as a vector, not a point
        forcePoint = true;
    }         
    
    /** 
     * Returns whether this tree has any operations
     */
    final public boolean hasOperations() {   
        if (leaf) {
        	if (left.isExpressionNode()) {
        		((ExpressionNode)left).hasOperations();
        	} else
        		return false;
        }        	
        
        return (right != null);        	             
    }       
    
    /** 
     * Returns all GeoElement objects in the subtree 
     */
    final public HashSet getVariables() {   
        if (leaf) return left.getVariables();
        
        HashSet leftVars = left.getVariables();
        HashSet rightVars = right.getVariables();        
        if (leftVars == null) {
        	return rightVars;        		
        } 
        else if (rightVars == null) {
        	return leftVars;
        }
        else {        	
        	leftVars.addAll(rightVars);        	
        	return leftVars;
        }        	
    }               
    
    final public GeoElement [] getGeoElementVariables() {
    	HashSet varset = getVariables();
        if (varset == null) return null;
        Iterator i = varset.iterator();        
        GeoElement [] ret = new GeoElement[varset.size()];
        int j=0;
        while (i.hasNext()) {
            ret[j++] = (GeoElement) i.next();
        }                
        return ret;
    }       
      
    final public boolean isLeaf() {
         return leaf; //|| operation == NO_OPERATION;
    }   
    
    public boolean isSingleVariable() {
        return (isLeaf() && (left instanceof Variable));
    }
    
    /**
     * Returns a string representation of this node that can be used with 
     * the given CAS, e.g. "*" and "^" are always printed.
     * @param symbolic: true for variable names, false for values of variables
     * @param STRING_TYPE: e.g. ExpressionNode.STRING_TYPE_JASYMCA
     */
    final public String getCASstring(int STRING_TYPE, boolean symbolic) {
        int oldPrintForm = kernel.getCASPrintForm();
        kernel.setCASPrintForm(STRING_TYPE);
        kernel.setTemporaryMaximumPrintAccuracy();        
                
        String ret = printCASstring(symbolic);
        
        kernel.restorePrintAccuracy();      
        kernel.setCASPrintForm(oldPrintForm);  
                             
        return ret;
    }
        
    private String printCASstring(boolean symbolic) {  
        String ret = null;

        if (leaf) { // leaf is GeoElement or not      
        	/*
        	if (symbolic) {
	            if (left.isGeoElement()) 
	            	ret = ((GeoElement) left).getLabel();     
	            else if (left.isExpressionNode())
	                ret = ((ExpressionNode)left).printJSCLString(symbolic);
	            else
	                ret = left.toString();      
        	} else {        		
        		 ret = left.toValueString();  
        	}         */
        	
        	if (symbolic && left.isGeoElement()) 
	            ret = ((GeoElement)left).getLabel();                            
	        else if (left.isExpressionNode())
	            ret = ((ExpressionNode)left).printCASstring(symbolic);
	        else 
	        	ret = symbolic ? left.toString() : left.toValueString(); 		        	         	       
        } 
        
        // STANDARD case: no leaf
        else {        
	        // expression node 
	        String leftStr = null, rightStr = null;                
	        if (symbolic && left.isGeoElement()) {  
	            leftStr = ((GeoElement)left).getLabel(); 
	        } else if (left.isExpressionNode()) {
	            leftStr  = ((ExpressionNode)left).printCASstring(symbolic);
	        } else {
	        	leftStr = symbolic ? left.toString() : left.toValueString(); 
	        }
	        
	        if (right != null) {
	            if (symbolic && right.isGeoElement()) {
	                rightStr = ((GeoElement)right).getLabel();
	            } else if (right.isExpressionNode()) {
	                rightStr  = ((ExpressionNode)right).printCASstring(symbolic);
	            } else {
	            	rightStr = symbolic ? right.toString() : right.toValueString(); 
	            }            
	        }     
	        ret = operationToString(leftStr, rightStr, !symbolic); 
        }                

        return ret;     
    }
    
 
    
    /**
     * Returns a string representation of this node.
     */
    final public String toString() {    
        if (leaf) { // leaf is GeoElement or not  
            if (left.isGeoElement())
				return ((GeoElement)left).getLabel();
			else
                return left.toString();             
        }
        
        // expression node 
        String leftStr = null, rightStr = null;
        if (left.isGeoElement()) {
            leftStr = ((GeoElement)left).getLabel();
        } else {
            leftStr = left.toString();
        }
        
        if (right != null) {
            if (right.isGeoElement()) {
                rightStr = ((GeoElement)right).getLabel();
            } else {
                rightStr = right.toString();
            }
        }        
        return operationToString(leftStr, rightStr, false);
    }
  
    
    /** like toString() but with current values of variables */
    final public String toValueString() {
        if (isLeaf()) { // leaf is GeoElement or not                            
            if (left != null)
				return left.toValueString();
        }

        // expression node 
        String leftStr = left.toValueString();        
        
        String rightStr = null;     
        if (right != null) {
            rightStr = right.toValueString();
        }
            
        return operationToString(leftStr, rightStr, true);
    }
    
    /**
     * Returns a string representation of this node in LaTeX syntax.
     * Note: the resulting string may contain special unicode characters like
     * greek characters or special signs for integer exponents. These sould be
     * handled afterwards!
     * @param symbolic: true for variable names, false for values of variables
     */
    final public String toLaTeXString(boolean symbolic) {
    	 if (isLeaf()) { // leaf is GeoElement or not                            
            if (left != null)
				return left.toLaTeXString(symbolic);
        }

        // expression node 
        String leftStr =  left.toLaTeXString(symbolic);
        String rightStr = null;     
        if (right != null) {
            rightStr = right.toLaTeXString(symbolic);
        }
              
        // build latex string
        int oldPrintForm = kernel.getCASPrintForm();       
        kernel.setCASPrintForm(STRING_TYPE_LATEX);
        String ret = operationToString(leftStr, rightStr,!symbolic);
        kernel.setCASPrintForm(oldPrintForm);
        
        return ret;
    }
    

        
    /**
     * Returns a string representation of this node.
     * Note: STRING_TYPE is used for LaTeX, MathPiper, Jasymca conform output, valueForm is used
     * by toValueString(), forLaTeX is used for LaTeX output
     * 
     */
    final private String operationToString(String leftStr, String rightStr, 
    		boolean valueForm) {

    	ExpressionValue leftEval;   
    	StringBuffer sb = new StringBuffer();
    	
    	int STRING_TYPE = kernel.getCASPrintForm();
        
        switch (operation) {      
        	case NOT:
        		switch (STRING_TYPE) {
	         		case STRING_TYPE_LATEX:
	         			sb.append("\\neg ");
	         			break;
	         			
	         		case STRING_TYPE_MATH_PIPER:
	         			sb.append("Not ");
	         			break;
	         			
	         		default:
	         			sb.append(strNOT);        		
	         	}           		
        		sb.append(leftStr);
        		break;
        
        
        	case OR:
        		 sb.append(leftStr);
        		 sb.append(' ');
        		 
        		 switch (STRING_TYPE) {
	         		case STRING_TYPE_LATEX:
	         			sb.append("\\vee");
	         			break;
	         			
	         		case STRING_TYPE_MATH_PIPER:
	         			sb.append("Or");
	         			break;
	         			
	         		default:
	         			sb.append(strOR);        		
	         	}           		 
        		 
        		 sb.append(' ');
                 sb.append(rightStr);
                 break;
                 
        	case AND:
        		if (left.isLeaf() || opID(left) >= AND) {
       		 		sb.append(leftStr);
        		} else {
        			sb.append('(');
        			sb.append(leftStr);
        			sb.append(')');
        		}
        		        		
       		 	sb.append(' ');
	       		switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\wedge");
		      			break;
		      			
		      		case STRING_TYPE_MATH_PIPER:
		      			sb.append("And");
		      			break;
		      			
		      		default:
		      			sb.append(strAND);        		
		      	}     
       		 	sb.append(' ');
       		 	
	       		if (right.isLeaf() || opID(right) >= AND) {
	   		 		sb.append(rightStr);
	    		} else {
	    			sb.append('(');
	    			sb.append(rightStr);
	    			sb.append(')');
	    		}               
                break;
                
        	case EQUAL_BOOLEAN:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      		case STRING_TYPE_MATH_PIPER:
		      		//case STRING_TYPE_JASYMCA:
		      			sb.append("=");
		      			break;
		      					      		
		      		default:
		      			sb.append(strEQUAL_BOOLEAN);        		
		      	}              	 	
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
        	case NOT_EQUAL:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\neq");
		      			break;
		      			
		      		case STRING_TYPE_MATH_PIPER:
		      			sb.append("!=");
		      			break;
		      			
		      		default:
		      			sb.append(strNOT_EQUAL);        		
		      	}       
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
            case LESS:
            	 sb.append(leftStr);
        		 sb.append(" < ");
                 sb.append(rightStr);
                 break;
                 
            case GREATER:
           	 	sb.append(leftStr);
           	 	sb.append(" > ");
                sb.append(rightStr);
                break;
            	
           case LESS_EQUAL:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	 switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\leq");
		      			break;
		      			
		      		case STRING_TYPE_MATH_PIPER:
		      			sb.append("<=");
		      			break;
		      			
		      		default:
		      			sb.append(strLESS_EQUAL);        		
		      	}        
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
           case GREATER_EQUAL:
          	 	sb.append(leftStr);
          	 	sb.append(' ');
          	 	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\geq");
		      			break;
		      			
		      		case STRING_TYPE_MATH_PIPER:
		      			sb.append(">=");
		      			break;
		      			
		      		default:
		      			sb.append(strGREATER_EQUAL);        		
		      	}    
          	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
           case PARALLEL:
         	 	sb.append(leftStr);
         	 	sb.append(' ');
        	 	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\parallel");
		      			break;
		      		
		      		default:
		      			sb.append(strPARALLEL);        		
		      	}             	 
         	 	sb.append(' ');
                sb.append(rightStr);
                break;
               
           case PERPENDICULAR:
       	 	sb.append(leftStr);
       	 	sb.append(' ');
       	 	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\perp");
		      			break;
		      		
		      		default:
		      			sb.append(strPERPENDICULAR);        		
		      	}   
       	 	sb.append(' ');
               sb.append(rightStr);
               break;
       
           case COMPLEXMULTIPLY:
       	 	sb.append(leftStr);
       	 	sb.append(' ');
       	 	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\times");
		      			break;
		      		
		      		default:
		      			sb.append(strCOMPLEXMULTIPLY);        		
		      	}   
       	 	sb.append(' ');
               sb.append(rightStr);
               break;
       
            case PLUS:          
            	switch (STRING_TYPE) {
	        		//case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append('(');
	        			sb.append(leftStr);
	                    sb.append(") + (");
	                    sb.append(rightStr);
	                    sb.append(')');
	                    break;
	                    
	                default:
	                    // check for 0
            			if ("0".equals(leftStr)) {
		            		sb.append(rightStr);
        		    		break;
            			} else if ("0".equals(rightStr)) {
		            		sb.append(leftStr);
        		    		break;
            			}
            			
	                	// we need parantheses around right text
	                	// if right is not a leaf expression or
	                	// it is a leaf GeoElement without a label (i.e. it is calculated somehow)        
	                    if (left.isTextValue() &&
	                     	(
	                     			!right.isLeaf() || 
	                    			(right.isGeoElement() && !((GeoElement) right).isLabelSet())
	    					) )
	    				{
	                        sb.append(leftStr);
	                        sb.append(" + (");
	                        sb.append(rightStr);
	                        sb.append(')');
	                    }
	                    else {
	                        sb.append(leftStr);                  
	                        if (rightStr.charAt(0) == '-') { // convert + - to -
	                            sb.append(" - ");
	                            sb.append(rightStr.substring(1));
	                        } else {
	                            sb.append(" + ");                                     
	                            sb.append(rightStr);
	                        }               
	                    }  
	                break;
            	}
                break;
                
            case MINUS:  
            	switch (STRING_TYPE) {
	        		//case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append('(');
	        			sb.append(leftStr);
	                    sb.append(") - (");
	                    sb.append(rightStr);
	                    sb.append(')');
	                    break;
	                    
	                default:
		                sb.append(leftStr);                
		                if (right.isLeaf() || opID(right) >= MULTIPLY) { // not +, -                    
		                    if (rightStr.charAt(0) == '-') { // convert - - to +
		                        sb.append(" + ");
		                        sb.append(rightStr.substring(1));
		                    } else {
		                        sb.append(" - ");                                     
		                        sb.append(rightStr);
		                    }                        
		                }
		                else { 
		                    sb.append(" - (");
		                    sb.append(rightStr);
		                    sb.append(')');
		                }	   
		             break;
	            }
                break;
                
            case MULTIPLY: 
            	switch (STRING_TYPE) {
        		//case STRING_TYPE_JASYMCA:
        		case STRING_TYPE_MATH_PIPER:
        			sb.append('(');
        			sb.append(leftStr);
                    sb.append(") * (");
                    sb.append(rightStr);
                    sb.append(')');
                    break;
                    
                default:
                    // check for 1
            		if ("1".equals(leftStr)) {
            			sb.append(rightStr);
            			break;
            		}
            		else if ("1".equals(rightStr)) {
            			sb.append(leftStr);
            			break;
            		}
            		
	                boolean nounary = true;
	                
	                // left wing                  
	                if (left.isLeaf() || opID(left) >= MULTIPLY) { // not +, - 
	                    if (leftStr.equals("-1")) { // unary minus
	                        nounary = false;
	                        sb.append('-');                     
	                    } else {
	                        sb.append(leftStr);                      
	                    }
	                } else { 
	                    sb.append('(');
	                    sb.append(leftStr);
	                    sb.append(')');                 
	                }               
	                     
	                // right wing
	                if (right.isLeaf() || opID(right) >= MULTIPLY) { // not +, -           
	                    // two digits colide: insert *    
	                    if (nounary) {                    	
	                		   if (Character.isDigit(rightStr.charAt(0)) &&
	                                Character.isDigit(sb.charAt(sb.length() - 1)) )
	                           {
	                			   sb.append(" * ");                    			   
	                            }
	                           else 
	                               sb.append(' '); // space instead of '*'                      	                           
	                    }                 
	                    
	                    if (rightStr.charAt(0) == '-') {
	                    	sb.append('(');
	                        sb.append(rightStr);
	                        sb.append(')');
	                    } else
	                    	sb.append(rightStr);
	                } else {
	                    if (nounary) {
	                    	switch (STRING_TYPE) {
	                			case STRING_TYPE_GEOGEBRA_XML:
	                				sb.append(" * ");  
	                				break;
	                				
	                			default:                        
	                				sb.append(' '); // space instead of '*'  
	                    	}
	                    }                      
	                    sb.append('(');
	                    sb.append(rightStr);
	                    sb.append(')');
	                }       
            	}
                break;
                
            case DIVIDE:   
            	switch (STRING_TYPE) {
    				case STRING_TYPE_LATEX:
    					sb.append("\\frac{");
                		sb.append(leftStr);
                		sb.append("}{");
                		sb.append(rightStr);
                		sb.append("}");
                		break;
                		
    				//case STRING_TYPE_JASYMCA:
    				case STRING_TYPE_MATH_PIPER:
    					 sb.append('(');
		                 sb.append(leftStr);
		                 sb.append(")/(");
		                 sb.append(rightStr);
		                 sb.append(')');
		                 break;
    				
	    			default:
	    				// check for 1 in denominator
            			if ("1".equals(rightStr)) {
            				sb.append(leftStr);
            				break;
            			}   
            			                       
		                // left wing              	
		                if (left.isLeaf()|| opID(left) >= MULTIPLY) { // not +, -
		                    sb.append(leftStr);                
		                } else { 
		                    sb.append('(');
		                    sb.append(leftStr);
		                    sb.append(')');
		                }                
		                sb.append(" / ");
		                 
		                // right wing
		                if (right.isLeaf() || opID(right) >= POWER) // not +, -, *, /                
		                    sb.append(rightStr);
		                else { 
		                    sb.append('(');
		                    sb.append(rightStr);
		                    sb.append(')');
		                }          
	            	}
	                break;
                
            case POWER:
            	switch (STRING_TYPE) {			
					//case STRING_TYPE_JASYMCA:
					case STRING_TYPE_MATH_PIPER:
						sb.append('(');
	                    sb.append(leftStr);
	                    sb.append(')');
		                 break;
		            
					default:
					    // check for 1 in exponent
            			if ("1".equals(rightStr)) {
            				sb.append(leftStr);
            				break;
            			}   
            	
		                // left wing                   	
		                if (leftStr.charAt(0) != '-' && // no unary
		                	(left.isLeaf() || opID(left) > POWER)) { // not +, -, *, /, ^                     
		                	sb.append(leftStr);                
		                } else { 
		                    sb.append('(');
		                    sb.append(leftStr);
		                    sb.append(')');
		                }       
					break;
            	}
	                
                // right wing  
                switch (STRING_TYPE) {
                	case STRING_TYPE_LATEX:
                		sb.append('^'); 
                        sb.append('{');
                        sb.append(rightStr);
                        sb.append('}');
                        break;
                        
	        		case STRING_TYPE_GEOGEBRA_XML:
					case STRING_TYPE_MATH_PIPER:
	        			sb.append('^'); 
                        sb.append('(');
                        sb.append(rightStr);
                        sb.append(')');
	        			break;
	
	        			
	        		default:
	        			 if (right.isLeaf() || opID(right) > POWER) { // not +, -, *, /, ^  	                        
/*
// Michael Borcherds 2008-05-14
// display powers over 9 as unicode superscript
	        				 try {
	        					 int i = Integer.parseInt(rightStr);
	        					 String index="";
	        					 if (i<0)
	        					 {
	        						 sb.append('\u207B'); // superscript minus sign
	        						 i=-i;
	        					 }
	        					 
	        					 if (i==0) sb.append('\u2070'); // zero     					 
	        					 else while (i>0) {
	        						 switch (i%10) {
	                                case 0: index="\u2070"+index; break;
	                                case 1: index="\u00b9"+index; break;
	                                case 2: index="\u00b2"+index; break;
	                                case 3: index="\u00b3"+index; break;
	                                case 4: index="\u2074"+index; break;
	                                case 5: index="\u2075"+index; break;
	                                case 6: index="\u2076"+index; break;
	                                case 7: index="\u2077"+index; break;
	                                case 8: index="\u2078"+index; break;
	                                case 9: index="\u2079"+index; break;
	        						 
	        						 }
	        						 i=i/10;
	        					 }
	        					 
	        					 sb.append(index);
	        				 }
	        				 catch (Exception e)
	        				 {
                                 sb.append('^'); 
                                 sb.append(rightStr);	        					 
	        				 }*/
	        				 
	        				 
	        				 
	        				 if (rightStr.length() == 1) {
                                 switch (rightStr.charAt(0)) {
                                 
                                 	case '0': sb.append('\u2070'); break;
                                 	case '1': sb.append('\u00b9'); break;
                                 	case '2': sb.append('\u00b2'); break;
                                     case '3': sb.append('\u00b3'); break;
                                     case '4': sb.append('\u2074'); break;
                                     case '5': sb.append('\u2075'); break;
                                     case '6': sb.append('\u2076'); break;
                                     case '7': sb.append('\u2077'); break;
                                     case '8': sb.append('\u2078'); break;
                                     case '9': sb.append('\u2079'); break;
                                     default: 
                                                 sb.append('^'); 
                                                 sb.append(rightStr);
                                 }
                            } else {
                                sb.append('^'); 
                                sb.append(rightStr);
                            }                         
	                     } else { 
	                         sb.append('^'); 
	                         sb.append('(');
	                         sb.append(rightStr);
	                         sb.append(')');
	                     }   
                }      
                break;
                
            case FACTORIAL:
                if (leftStr.charAt(0) != '-' && // no unary
                		left.isLeaf() || opID(left) > POWER) { // not +, -, *, /, ^
                    sb.append(leftStr);             
                } else {
                    sb.append('(');
                    sb.append(leftStr);
                    sb.append(')');
                }                           
                sb.append('!');
                break;    
                        
            case COS:  
            	switch (STRING_TYPE) {
            		case STRING_TYPE_LATEX:
            			sb.append("\\cos(");
            			break;
            			
            		case STRING_TYPE_MATH_PIPER:
            			sb.append("Cos(");
            			break;
            			
            		default:
            			sb.append("cos(");         		
            	}    		
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case SIN:            	
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\sin(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Sin(");
	        			break;
	        			
	        		default:
	        			sb.append("sin(");         		
	        	}           		
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case TAN:  
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\tan(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Tan(");
	        			break;
	        			
	        		default:
	        			sb.append("tan(");         		
	        	}            		
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ARCCOS:
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\arccos(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcCos(");
	        			break;
	        			
	        		default:
	        			sb.append("acos(");         		
	        	}  	             	
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ARCSIN:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\arcsin(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcSin(");
	        			break;
	        			
	        		default:
	        			sb.append("asin(");         		
	        	}  	   
                sb.append(leftStr);
                sb.append(')');
                break;

            case ARCTAN:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\arctan(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcTan(");
	        			break;
	        			
	        		default:
	        			sb.append("atan(");         		
	        	}  	        	      
                sb.append(leftStr);
                sb.append(')');
                break;

            case COSH:      
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\cosh(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Cosh(");
	        			break;
	        			
	        		default:
	        			sb.append("cosh(");         		
	        	}  	         	       		                
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case SINH:  
              	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\sinh(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Sinh(");
	        			break;
	        			
	        		default:
	        			sb.append("sinh(");         		
	        	}    		                 
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case TANH:  
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\tanh(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Tanh(");
	        			break;
	        			
	        		default:
	        			sb.append("tanh(");         		
	        	}    	
            	sb.append(leftStr);
                sb.append(')');
                break;

            case ACOSH:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\mathrm{acosh}(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcCosh(");
	        			break;
	        			
	        		default:
	        			sb.append("acosh(");         		
	        	}           	        	            	
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case ASINH:
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\mathrm{asinh}(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcSinh(");
	        			break;
	        			
	        		default:
	        			sb.append("asinh(");         		
	        	}               	
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ATANH:
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\mathrm{atanh}(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("ArcTanh(");
	        			break;
	        			
	        		default:
	        			sb.append("atanh(");         		
	        	}      
            	sb.append(leftStr);
                sb.append(')');
                break;
               
            case EXP:
               	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("e^{");
	        			 sb.append(leftStr);
	        			 sb.append('}');
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Exp(");
	        			sb.append(leftStr);
	                    sb.append(')');
	        			break;
	        			
	        		//case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_GEOGEBRA_XML:
	        			sb.append("exp(");
	        			sb.append(leftStr);
	                    sb.append(')');
	        			break;
	        			
	        		default:
	        			sb.append(Kernel.EULER_STRING);
	        			if (left.isLeaf()) {
	        				sb.append("^");  
	        				sb.append(leftStr);
	        			} else {
		        			sb.append("^(");     
			        		sb.append(leftStr);
		                    sb.append(')');
	        			}
	        			break;
	        	}           	
                break;

            case LOG:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\log(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Ln(");
	        			break;
	        			
	        		//case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_GEOGEBRA_XML:
	        			sb.append("log(");	        			
	        			break;
	        			
	        		default:
	        			sb.append("ln("); 
	        			break;
	        	}              	      	
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case LOG10:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\log_{10}(");
	        			sb.append(leftStr);
	                    sb.append(')');
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Ln(");
	        			sb.append(leftStr);
	        			sb.append(")/Ln(10)");
	        			break;
	        			
//	        		case STRING_TYPE_JASYMCA:
//	        			sb.append("log(");	
//	        			sb.append(leftStr);
//	        			sb.append(")/log(10)");
//	        			break;
	        			
	        		default:
	        			sb.append("lg(");  
	        			sb.append(leftStr);
	        			sb.append(')');
                    	break;
	        	}              	      	
                break;
                
            case LOG2:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\log_{2}(");
	        			sb.append(leftStr);
	                    sb.append(')');
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Ln(");
	        			 sb.append(leftStr);
	        			 sb.append(")/Ln(2)");
	        			break;
	        			
//	        		case STRING_TYPE_JASYMCA:
//	        			sb.append("log(");	
//	        			sb.append(leftStr);
//	        			sb.append(")/log(2)");
//	        			break;
	        			
	        		default:
	        			sb.append("ld(");   
		        		sb.append(leftStr);
	                    sb.append(')');
	                    break;
	        	}              	      	
                break;
                                            
            case SQRT:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\sqrt{");
	        			 sb.append(leftStr);
	        			 sb.append('}');
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Sqrt(");
	        			sb.append(leftStr);
	                    sb.append(')');
	        			break;
	        			
	        		default:
	        			sb.append("sqrt(");     
		        		sb.append(leftStr);
	                    sb.append(')');
	        	}         
                break;
                
            case CBRT:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\sqrt[3]{");
	        			 sb.append(leftStr);
	        			 sb.append('}');
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("(");
	        			sb.append(leftStr);
	                    sb.append(")^(1/3)");
	        			break;
	        			
	        		default:
	        			sb.append("cbrt(");     
		        		sb.append(leftStr);
	                    sb.append(')');
	        	}         
                break;
                
            case ABS:   
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\left|");
	            		sb.append(leftStr);
	                    sb.append("\\right|");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Abs(");
	        			sb.append(leftStr);
		                sb.append(')');
	        			break;
	        			
	        		default:
	        			sb.append("abs(");  
		        		sb.append(leftStr);
		                sb.append(')');
	        	}               
                break;
            
            case SGN:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\mathrm{sgn}(");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Sign(");
	        			break;
	        			
//	        		case STRING_TYPE_JASYMCA:
//	        			sb.append("sign(");
//	        			break;

	        		default:
	        			sb.append("sgn(");         		
	        	}        
            	sb.append(leftStr);
	            sb.append(')');
                break;
                
            case FLOOR:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\lfloor");
	                     sb.append(leftStr);
	                     sb.append("\\rfloor");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Floor(");
	        			sb.append(leftStr);
		                sb.append(')');
	        			break;
	        			
	        		default:
	        			sb.append("floor(");  
		        		sb.append(leftStr);
		                sb.append(')');
	        	}         
                break;                

            case CEIL:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\lceil");
	                     sb.append(leftStr);
	                     sb.append("\\rceil");
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Ceil(");
	        			sb.append(leftStr);
		                sb.append(')');
	        			break;
	        			
	        		default:
	        			sb.append("ceil(");  
		        		sb.append(leftStr);
		                sb.append(')');
	        	}               	
                break;       
                
            case ROUND:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\mathrm{round}(");            
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Round(");        			
	        			break;
	        			
	        		default:
	        			sb.append("round(");      		
	        	}                 	
                sb.append(leftStr);
                sb.append(')');
                break;  
                
            case GAMMA:
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_LATEX:
	        			sb.append("\\Gamma(");            
	        			break;
	        			
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append("Gamma(");        			
	        			break;
	        			
	        		default:
	        			sb.append("gamma(");      		
	        	}                 	         
                sb.append(leftStr);
                sb.append(')');
                break;  
                
            case RANDOM:   
            	if (valueForm)
            		sb.append(leftStr);
            	else
            		sb.append("random()");		        		           
                break;
                
            case XCOORD:
            	if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {            												
            		sb.append(kernel.format(((VectorValue)leftEval).getVector().getX()));
            	} else {
            		switch (STRING_TYPE) {
            			case STRING_TYPE_LATEX:            		
            				sb.append("\\mathrm{x}(");
            				sb.append(leftStr);
                    		sb.append(')');
            				break;
            				
            			//case STRING_TYPE_JASYMCA:
    	        		case STRING_TYPE_MATH_PIPER:
    	        			// note: see GeoGebraCAS.insertSpecialChars()
    	        			sb.append("x");		        		
    	        			sb.append(UNICODE_PREFIX);
		        			sb.append("40"); // decimal unicode for (
		        			sb.append(UNICODE_DELIMITER);
		        			sb.append(leftStr);
		        			sb.append(UNICODE_PREFIX);
		        			sb.append("41"); // decimal unicode for )
		        			sb.append(UNICODE_DELIMITER);
		        			break;
            		
    	        		default:
    	        			sb.append("x(");
    	        			sb.append(leftStr);
    	        			sb.append(')');
            		}            		            		
            	}
                break;
                
            case YCOORD:            	
            	if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {            												
            		sb.append(kernel.format(((VectorValue)leftEval).getVector().getY()));            		
            	} else {
            		switch (STRING_TYPE) {
	        			case STRING_TYPE_LATEX:            		
	        				sb.append("\\mathrm{y}(");
	        				sb.append(leftStr);
	                		sb.append(')');
	        				break;
	        				
	        			//case STRING_TYPE_JASYMCA:
		        		case STRING_TYPE_MATH_PIPER:
		        			// note: see GeoGebraCAS.insertSpecialChars()
		        			sb.append("y");		        		
		        			sb.append(UNICODE_PREFIX);
		        			sb.append("40"); // decimal unicode for (
		        			sb.append(UNICODE_DELIMITER);
		        			sb.append(leftStr);
		        			sb.append(UNICODE_PREFIX);
		        			sb.append("41"); // decimal unicode for )
		        			sb.append(UNICODE_DELIMITER);
		        			break;
	        		
		        		default:
		        			sb.append("y(");
		        			sb.append(leftStr);
		        			sb.append(')');
	        		}       
            	} 
                break;
                
            case FUNCTION:
            	// GeoFunction and GeoFunctionConditional should not be expanded
            	if (left.isGeoElement() &&
                     	((GeoElement)left).isGeoFunction()) {
            		 sb.append(((GeoElement)left).getLabel());
            	} else
            		 sb.append(leftStr);            	 
                sb.append('(');
                sb.append(rightStr);
                sb.append(')');
                break;
               
            case VEC_FUNCTION:
            	// GeoCurveables should not be expanded
            	if (left.isGeoElement() &&
                     	((GeoElement)left).isGeoCurveable()) {
            		 sb.append(((GeoElement)left).getLabel());
            	} else
            		 sb.append(leftStr);            	 
                sb.append('(');
                sb.append(rightStr);
                sb.append(')');
                break;                  
                
            case DERIVATIVE: // e.g. f''
            	// labeled GeoElements should not be expanded
            	if (left.isGeoElement() && ((GeoElement)left).isLabelSet()) {
            		 sb.append(((GeoElement)left).getLabel());
            	} else
            		 sb.append(leftStr); 
            	
            	if (right.isNumberValue()) {
	            	int order = (int) Math.round(((MyDouble)right).getDouble());
	            	for (;order > 0; order--) 
	            		sb.append('\'');
            	}
            	else
            		sb.append(right);
                break;
                
                
            case $VAR_ROW: // e.g. A$1
            	if (valueForm) {
            		// GeoElement value
            		sb.append(leftStr); 
            	} else {
            		// $ for row
            		GeoElement geo = (GeoElement)left;
            		if (geo.getSpreadsheetCoords() != null) {
            			sb.append(geo.getSpreadsheetLabelWithDollars(false, true));	
            		} else {
            			sb.append(leftStr); 
            		} 
            	}
            	break;
            	
            case $VAR_COL: // e.g. $A1
            	if (valueForm) {
            		// GeoElement value
            		sb.append(leftStr); 
            	} else {            	
            		// $ for row
            		GeoElement geo = (GeoElement)left;
            		if (geo.getSpreadsheetCoords() != null) {
            			sb.append(geo.getSpreadsheetLabelWithDollars(true, false));	
            		} else {
            			sb.append(leftStr); 
            		} 
            	}            
            	break;
            	
            case $VAR_ROW_COL: // e.g. $A$1
            	if (valueForm) {
            		// GeoElement value
            		sb.append(leftStr); 
            	} else {
            		// $ for row
            		GeoElement geo = (GeoElement)left;
            		if (geo.getSpreadsheetCoords() != null) {
            			sb.append(geo.getSpreadsheetLabelWithDollars(true, true));	
            		} else {
            			sb.append(leftStr); 
            		}            		            		         
            	}
            	break;
                
            default:
                sb.append("unhandled operation " + operation);
        }                
        return sb.toString();
    }
    
     
    
    
    // return operation number
    static public int opID(ExpressionValue ev) {
        if (ev.isExpressionNode())
			return ((ExpressionNode)ev).operation;
		else return -1;
    }        
    
    public boolean isNumberValue() {
        return evaluate().isNumberValue();
    }
    
    public boolean isBooleanValue() {
        return evaluate().isBooleanValue();
    }
    
    public boolean isListValue() {		
		return evaluate().isListValue();
	}


    public boolean isPolynomialInstance() {
        //return evaluate().isPolynomial();
        return false;
    }   
        
    public boolean isTextValue() {
        // should be efficient as it is used in operationToString()
        if (leaf)
            return left.isTextValue();
        else
            return (operation == PLUS && (left.isTextValue() || right.isTextValue()));
    }
    
    final public boolean isExpressionNode() {
        return true;
    }

	
}
