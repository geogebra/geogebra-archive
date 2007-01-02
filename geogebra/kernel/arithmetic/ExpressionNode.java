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
 * ExpressionNode.java
 *
 * binary tree node for ExpressionValues (NumberValues, VectorValues)
 *
 * Created on 03. Oktober 2001, 09:37
 */

package geogebra.kernel.arithmetic;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.Kernel;

import java.util.HashSet;
import java.util.Iterator;


/**
 *
 * @author  Markus
 * @version 
 */
public class ExpressionNode extends ValidExpression
implements ExpressionValue {   
    
	// boolean
	public static final int NOT = -9;
	public static final int OR = -8;
    public static final int AND = -7;
    public static final int EQUAL_BOOLEAN = -6;
    public static final int LESS = -5;
    public static final int GREATER = -4;
    public static final int LESS_EQUAL = -3;
    public static final int GREATER_EQUAL = -2;
    
    private static final String strNOT = "\u00ac";
    private static final String strAND = "\u2227";
    private static final String strOR = "\u2228";
    private static final String strLESS_EQUAL = "\u2264";
    private static final String strGREATER_EQUAL = "\u2265";

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
     
    public static final int FUNCTION = 100;
    public static final int VEC_FUNCTION = 101;
    public static final int DERIVATIVE = 110;    
    
    private Application app;
    private Kernel kernel;
    ExpressionValue left, right; 
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
        this.left = left;
        if (right != null) {
            this.right = right;        
        } else { // set dummy value
            this.right = new MyDouble(kernel, 0.0d);
        }     
    }           
    
    /** for only one leaf */
    // for wrapping ExpressionValues as ValidExpression
    public ExpressionNode(Kernel kernel, ExpressionValue leaf) {    
        this.kernel = kernel;
        app = kernel.getApplication();
              
        left = leaf;
        this.leaf = true;
    }
    
    // copy constructor: NO deep copy of subtrees is done here!
    // this is needed for translation of functions
    public ExpressionNode(ExpressionNode node) {
        kernel = node.kernel;
        app = node.app;
        
        leaf = node.leaf;
        operation = node.operation;
        left = node.left;
        right = node.right;
    }
    
    public Kernel getKernel() {
        return kernel;
    }
    
    public int getOperation() {
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
    }
    
    public ExpressionNode getRightTree() {
        if (right == null) return null;
        
        if (right.isExpressionNode())
            return (ExpressionNode) right;
        else 
            return new ExpressionNode(kernel, right);
    }
    
    public ExpressionValue deepCopy() {
        return getCopy();
    }
    
    /** copy the whole tree structure except leafs */
    public ExpressionNode getCopy() {
        //System.out.println("getCopy() input: " + this);   
        ExpressionNode newNode = null;
        ExpressionValue lev = null, rev = null;                
        
        if (left != null) lev = copy(left);
        if (right != null) rev = copy(right);
        
        if (lev != null) {
            newNode = new ExpressionNode(kernel, lev, operation, rev);
            newNode.leaf = leaf;
        } else
			// something went wrong
			   return null;
               
        // set member vars that are not set by constructors
        newNode.forceVector = forceVector;
        newNode.forcePoint = forcePoint;
        //System.out.println("getCopy() output: " + newNode);   
        return newNode;
    }        
    
    /** deep copy except for GeoElements */
    public static ExpressionValue copy(ExpressionValue ev) {
        if (ev == null) return null;
        
        ExpressionValue ret = null;        
        //System.out.println("copy ExpressionValue input: " + ev);        
        if (ev.isExpressionNode()) {
            ret = ((ExpressionNode)ev).getCopy(); 
        } else if (ev.isPolynomialInstance()) {           
            ret = ev.deepCopy();              
        } else if (ev.isConstant()) {           
            ret = ev.deepCopy();             
        } else {            
            ret = ev;                        
        }           
        //System.out.println("copy ExpressionValue output: " + ev);        
        return ret;        
    }                  
    
    /**
     * Replaces all ExpressionNodes in tree that are leafs by their leaf
     * objects (of type ExpressionValue).
     */
    final public void simplifyLeafs() {         
        if (left.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) left;
            if (node.leaf) 
                left = node.evaluate(); // get leaf object
            else 
                node.simplifyLeafs();
        }
        
        if (right != null && right.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) right;
            if (node.leaf) 
                right = node.evaluate(); // get leaf object
            else 
                node.simplifyLeafs();
        }               
    }
    
    /** 
     * Replaces all constant parts in tree by their values
     */
    final public void simplifyConstants() {                 
        if (left.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) left;
            if (left.isConstant()) 
                left = node.evaluate();
            else
                node.simplifyConstants();
        }
        
        if (right != null && right.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) right;
            if (right.isConstant()) 
                right = node.evaluate(); 
            else 
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
        	// nummber == number
        	if (lt.isNumberValue() && rt.isNumberValue())
				return new MyBoolean(
        			kernel.isEqual(
        				((NumberValue)lt).getDouble(),
						((NumberValue)rt).getDouble()
					)
        		);
			else { 
                String [] str = { "IllegalComparison", lt.toString(), "==",  rt.toString() };
                throw new MyError(app, str);
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
            else { 
                String [] str = { "IllegalDivision", lt.toString(), "/", rt.toString() };
                throw new MyError(app, str);
            }
                                               
        case POWER:
            // number ^ number
            if (lt.isNumberValue() && rt.isNumberValue()) {
                num = ((NumberValue)lt).getNumber();                
                MyDouble.pow(num, ((NumberValue)rt).getNumber(), num);
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
                // the number must be constant evaluate to a positiv integer
                if (((Polynomial)rt).degree() != 0) {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "/", rt.toString() };
                    throw new MyError(app, str);
                }                
                
                if (!rt.isConstant()) {
                    String [] str = { "ExponentMustBeConstant", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
                
                // get constant coefficent of given polynomial                
                double exponent = ((Polynomial)rt).getConstantCoeffValue();
                if (kernel.isInteger(exponent) && (int) exponent >= 0) {
                    poly = new Polynomial(kernel, (Polynomial)lt);
                    poly.power((int) exponent);                
                    return poly;
                } else {
                    String [] str = { "ExponentMustBeInteger", lt.toString(), "^", rt.toString() };
                    throw new MyError(app, str);
                }
            }                
            else { 
                System.err.println("power: lt :" + lt.getClass() + ", rt: " + rt.getClass());
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
            if (rt.isNumberValue()) {    
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
                //System.out.println("lt: " + lt.getClass() + " rt: " + rt.getClass());
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }
            
        case VEC_FUNCTION:      
            // vecfunction(number)
            if (rt.isNumberValue()) {    
            	NumberValue arg = (NumberValue) rt;
            	return ((Parametric2D)lt).evaluate(arg.getDouble());            	
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
                //System.out.println("lt: " + lt.getClass() + " rt: " + rt.getClass());
                 String [] str = { "IllegalArgument", rt.toString() };
                throw new MyError(app, str);
            }            
            
        case DERIVATIVE:
        //System.out.println("DERIVATIVE called");
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
                                new ExpressionNode(kernel, lt, ExpressionNode.FUNCTION, rt),
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
     *  look for Variable objects in the tree and replace them
     *  by their resolved GeoElement
     */
    public void resolveVariables() {        
        // resolve left wing
    	if (left instanceof Variable) {
            left = ((Variable) left).resolve();                
        } else
        	left.resolveVariables();
    	
        // resolve right wing
        if (right != null) {
            if (right instanceof Variable) {
                right = ((Variable) right).resolve();
            } 
            else
            	right.resolveVariables();
        }                       
    }
    
    
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
     * Returns a string representation of this node that can be used with JSCL.
     * e.g. "*" and "^" are always printed.
     * @param symbolic: true for variable names, false for values of variables
     */
    final public String getJSCLString(boolean symbolic) {
        int oldDigits = kernel.getMaximumFractionDigits();
        kernel.setMaximumFractionDigits(50);
        String ret = printJSCLString(symbolic);
        kernel.setMaximumFractionDigits(oldDigits);
        return ret;
    }
        
    private String printJSCLString(boolean symbolic) {  
        boolean oldPrintForm = kernel.isJSCLprintForm();
        kernel.setJSCLprintForm(true);
        
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
	            ret  = ((ExpressionNode)left).printJSCLString(symbolic);
	        else 
	        	ret = symbolic ? left.toString() : left.toValueString(); 	        
        } 
        
        // STANDARD case: no leaf
        else {        
	        // expression node 
	        String leftStr = null, rightStr = null;                
	        if (symbolic && left.isGeoElement()) {  
	            leftStr = ((GeoElement)left).getLabel();                            
	        } else if (left.isExpressionNode())
	            leftStr  = ((ExpressionNode)left).printJSCLString(symbolic);
	        else {
	        	leftStr = symbolic ? left.toString() : left.toValueString(); 
	        }
	        
	        if (right != null) {
	            if (symbolic && right.isGeoElement()) {
	                rightStr = ((GeoElement)right).getLabel();
	            } else if (right.isExpressionNode()) {
	                rightStr  = ((ExpressionNode)right).printJSCLString(symbolic);
	            } else {
	            	rightStr = symbolic ? right.toString() : right.toValueString(); 
	            }            
	        }     
	        ret = operationToString(leftStr, rightStr, true, !symbolic, false);
        }                
                
        kernel.setJSCLprintForm(oldPrintForm);
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
        return operationToString(leftStr, rightStr, false, false, false);
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
            
        return operationToString(leftStr, rightStr, false, true, false);
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
        return operationToString(leftStr, rightStr, false, !symbolic, true);
    }
    

        
    /**
     * Returns a string representation of this node.
     * Note: longForm is used for JSCL conform output, valueForm is used
     * by toValueString(), forLaTeX is used for LaTeX output
     * 
     */
    final private String operationToString(String leftStr, String rightStr, 
    		boolean printLongForm, boolean valueForm, boolean forLaTeX) {
    	   
    	ExpressionValue leftEval;
        StringBuffer sb = new StringBuffer();
        
        switch (operation) {      
        	case NOT:
        		sb.append(strNOT);
        		sb.append(leftStr);
        		break;
        
        
        	case OR:
        		 sb.append(leftStr);
        		 sb.append(' ');
        		 sb.append(strOR);
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
       		 	sb.append(strAND);
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
           	 	sb.append(" == ");
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
           	 	sb.append(strLESS_EQUAL);
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
           case GREATER_EQUAL:
          	 	sb.append(leftStr);
          	 	sb.append(' ');
          	 	sb.append(strGREATER_EQUAL);
          	 	sb.append(' ');
                sb.append(rightStr);
                break;
        
            case PLUS:          
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
                
            case MINUS:    
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
                
            case MULTIPLY: 
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
                        if (printLongForm ||
                             Character.isDigit(rightStr.charAt(0)) &&
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
                        if (printLongForm)
                            sb.append(" * ");          
                        else 
                            sb.append(' '); // space instead of '*'                      
                    }                      
                    sb.append('(');
                    sb.append(rightStr);
                    sb.append(')');
                }                                
                break;
                
            case DIVIDE:   
            	if (forLaTeX) {
            		sb.append("\\frac{");
            		sb.append(leftStr);
            		sb.append("}{");
            		sb.append(rightStr);
            		sb.append("}");
            	}
            	else {
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
                // left wing                   	
                if (leftStr.charAt(0) != '-' && // no unary
                	(left.isLeaf() || opID(left) > POWER)) { // not +, -, *, /, ^                     
                	sb.append(leftStr);                
                } else { 
                    sb.append('(');
                    sb.append(leftStr);
                    sb.append(')');
                }                                
                // right wing  
                if (forLaTeX) {
                	 sb.append('^'); 
                     sb.append('{');
                     sb.append(rightStr);
                     sb.append('}');
                }                
                else if (right.isLeaf() || opID(right) > POWER) { // not +, -, *, /, ^  
                    if (printLongForm) {
                        sb.append('^'); 
                        sb.append(rightStr);
                    }                     
                    else {
                        if (rightStr.length() == 1) {
                            switch (rightStr.charAt(0)) {
                                case '2': sb.append('\u00b2'); break;
                                case '3': sb.append('\u00b3'); break;
                                case '4': sb.append('\u2074'); break;
                                case '5': sb.append('\u2075'); break;
                                case '6': sb.append('\u2076'); break;
                                default: 
                                            sb.append('^'); 
                                            sb.append(rightStr);
                            }
                       } else {
                           sb.append('^'); 
                           sb.append(rightStr);
                       }
                    }                
                } else { 
                    sb.append('^'); 
                    sb.append('(');
                    sb.append(rightStr);
                    sb.append(')');
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
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("cos(");
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case SIN:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("sin(");
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case TAN:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("tan(");
                sb.append(leftStr);
                sb.append(')');
                break;

            case COSH:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("cosh(");
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case SINH:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("sinh(");
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case TANH:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("tanh(");
                sb.append(leftStr);
                sb.append(')');
                break;

            case ACOSH:
            	if (forLaTeX) 
            		sb.append("\\mathrm{acosh}(");
            	else
            		sb.append("acosh(");
                sb.append(leftStr);
                sb.append(')');
                break;
            
            case ASINH:
            	if (forLaTeX) 
            		sb.append("\\mathrm{asinh}(");
            	else
               	 	sb.append("asinh(");
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ATANH:
              	if (forLaTeX) 
            		sb.append("\\mathrm{atanh}(");
            	else
            		sb.append("atanh(");
                sb.append(leftStr);
                sb.append(')');
                break;
               
            case EXP:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("exp(");
                sb.append(leftStr);
                sb.append(')');
                break;

            case LOG:
            	if (forLaTeX) 
            		sb.append("\\");
                sb.append("log(");
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ARCCOS:
            	if (forLaTeX) 
            		sb.append("\\mathrm{acos}(");
            	else
            		sb.append("acos(");
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case ARCSIN:
            	if (forLaTeX) 
            		sb.append("\\mathrm{asin}(");
            	else
            		sb.append("asin(");
                sb.append(leftStr);
                sb.append(')');
                break;

            case ARCTAN:
            	if (forLaTeX) 
            		sb.append("\\mathrm{atan}(");
            	else
            		sb.append("atan(");        
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case SQRT:
            	if (forLaTeX) {
            		sb.append("\\sqrt{");
            		sb.append(leftStr);
                    sb.append('}');
            	} else {
            		 sb.append("sqrt(");
                     sb.append(leftStr);
                     sb.append(')');
            	}            		               
                break;
                
            case ABS:    
            	if (forLaTeX) {
            		sb.append("\\left|");
            		sb.append(leftStr);
                    sb.append("\\right|");
            	} else {
	                sb.append("abs(");
	                sb.append(leftStr);
	                sb.append(')');
            	}
                break;
            
            case SGN:
            	if (forLaTeX) 
            		sb.append("\\mathrm{sgn}(");
            	else
            		sb.append("sgn("); 
                sb.append(leftStr);
                sb.append(')');
                break;
                
            case FLOOR:
            	if (forLaTeX) {
            		 sb.append("\\lfloor");
                     sb.append(leftStr);
                     sb.append("\\rfloor");
            	} else {
            		 sb.append("floor(");
                     sb.append(leftStr);
                     sb.append(')');
            	}               
                break;                

            case CEIL:
               	if (forLaTeX) {
               		sb.append("\\lceil");
                    sb.append(leftStr);
                    sb.append("\\rceil");
	           	} else {
	           		 sb.append("ceil(");
	                 sb.append(leftStr);
	                 sb.append(')');
	           	}   
                break;       
                
            case ROUND:
                sb.append("round(");
                sb.append(leftStr);
                sb.append(')');
                break;  
                
            case GAMMA:
            	if (forLaTeX)
            		sb.append("\\Gamma(");
            	else
            		sb.append("gamma(");
                sb.append(leftStr);
                sb.append(')');
                break;  
                
            case XCOORD:
            	if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {            												
            		sb.append(kernel.format(((VectorValue)leftEval).getVector().getX()));
            	} else {
            		if (forLaTeX)
                		sb.append("\\mathrm{x}(");
            		else
            			sb.append("x(");
            		sb.append(leftStr);
            		sb.append(')');
            	}
                break;
                
            case YCOORD:            	
            	if (valueForm && (leftEval = left.evaluate()).isVectorValue()) {            												
            		sb.append(kernel.format(((VectorValue)leftEval).getVector().getY()));            		
            	} else {
            		if (forLaTeX)
                		sb.append("\\mathrm{y}(");
            		else
            			sb.append("y(");            	
            		sb.append(leftStr);
            		sb.append(')');
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
            	// GeoFunction and GeoFunctionConditional should not be expanded
            	if (left.isGeoElement() &&
                     	((GeoElement)left).isGeoCurveCartesian()) {
            		 sb.append(((GeoElement)left).getLabel());
            	} else
            		 sb.append(leftStr);            	 
                sb.append('(');
                sb.append(rightStr);
                sb.append(')');
                break;                  
                
            case DERIVATIVE: // e.g. f''
            	// GeoFunctionConditional should not be expanded
            	if (left.isGeoElement() &&
                     	((GeoElement)left).isGeoFunctionConditional()) {
            		 sb.append(((GeoElement)left).getLabel());
            	} else
            		 sb.append(leftStr); 
            	
            	int order = (int) Math.round(((MyDouble)right).getDouble());
            	for (;order > 0; order--) 
            		sb.append('\'');
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
