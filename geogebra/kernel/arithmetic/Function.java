/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.Kernel;
import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Function of one variable x that returns either a number
 * or a boolean. This depends on the expression this function
 * is based on.
 * 
 * @author Markus Hohenwarter
 */
public class Function extends FunctionNVar 
implements ExpressionValue, RealRootFunction, Functional {

	 /**
     * Creates new Function from expression where x is
     * the variable. 
     * Note: call initFunction() after this constructor.
     */ 
    public Function(ExpressionNode expression) {
    	super(expression);     
    }
    
    
    /**
     * Creates new Function from expression where the function
     * variable in expression is already known.
     */ 
    public Function(ExpressionNode exp, FunctionVariable fVar) {
    	super(exp, new FunctionVariable [] {fVar});
    }
    
    /**
     * Creates a Function that has no expression yet. Use setExpression() to
     * do this later.    
     */ 
    public Function(Kernel kernel) {
       super(kernel);
    }
    
    // copy constructor
    public Function(Function f, Kernel kernel) {   
        super(f.expression.getCopy(kernel));
        fVars = f.fVars; // no deep copy of function variable            
        isBooleanFunction = f.isBooleanFunction;
        isConstantFunction = f.isConstantFunction;
       
        app = kernel.getApplication();
        this.kernel = kernel;
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new Function(this, kernel);        
    }   
    
    /**
     * Use this methode only if you really know
     * what you are doing.
     */
    public void setExpression(ExpressionNode exp, FunctionVariable var) {
        super.setExpression(exp, new FunctionVariable[] {var});
    }
    
    final public Function getFunction() {
        return this;
    }
    
    public FunctionVariable getFunctionVariable() {
        return fVars[0];
    }
    
    final public String getVarString() {
    	return fVars == null ? "x" : fVars[0].toString();
    }

    /**
     * Call this function to resolve variables and init the function.
     * May throw MyError (InvalidFunction).
     */
    public void initFunction() {              	
        if (fVars == null) {
        	// try function variable x
        	fVars = new FunctionVariable[] {new FunctionVariable(kernel)};   
        }
        
        super.initFunction();
    }
    
    /**
     * Returns this function's value at position x.    
     * @param x
     * @return f(x)
     */
    final public double evaluate(double x) {
    	if (isBooleanFunction) {
    		// BooleanValue
    		return evaluateBoolean(x) ? 1 : 0;
    	}
    	else {
    		// NumberValue
    		fVars[0].set(x);
    		return ((NumberValue) expression.evaluate()).getDouble();
    	}     
    }
    
    /**
     * Returns this function's value at position x.
     * (Note: use this method if isBooleanFunction() returns true.
     * @param x
     * @return f(x)
     */
    final public boolean evaluateBoolean(double x) {       
        fVars[0].set(x);
        return ((BooleanValue) expression.evaluate()).getBoolean();       
    }
    
    /**
     * translate this function by vector (vx, vy)
     */
    final public void translate(double vx, double vy) {                  
        boolean isLeaf = expression.isLeaf();
        ExpressionValue left = expression.getLeft();

        // translate x
        if (!kernel.isZero(vx)) {
            if (isLeaf && left == fVars[0]) { // special case: f(x) = x
                expression = shiftXnode(vx);            
             } else {
                //  replace every x in tree by (x - vx)
                // i.e. replace fVar with (fvar - vx)
                translateX(expression, vx);
             }
        }
         
        // translate y
        if (!kernel.isZero(vy)) {                       
            if (isLeaf && left != fVars[0]) { // special case f(x) = constant               
                MyDouble c = ((NumberValue) expression.getLeft()).getNumber();
                c.set(c.getDouble() + vy);
                expression.setLeft(c);
            } else {                
                // f(x) = f(x) + vy
                translateY(vy);
            }
        }       
        
        // make sure that expression object is changed!
        // this is needed to know that the expression has changed
        if (expression.isLeaf() && expression.getLeft().isExpressionNode()) {
        	expression = new ExpressionNode( (ExpressionNode) expression.getLeft());    
        } else {
            expression = new ExpressionNode(expression);
        }
    }
    
    // replace every x in tree by (x - vx)
    // i.e. replace fVar with (fvar - vx)
    final private void translateX(ExpressionNode en, double vx) {                               
        ExpressionValue left = en.getLeft();
        ExpressionValue right = en.getRight();  
        
        // left tree
        if (left == fVars[0]) {         
            try { // is there a constant number to the right?
                MyDouble num = (MyDouble) right;
                double temp;
                switch (en.getOperation()) {
                    case ExpressionNode.PLUS :
                        temp = num.getDouble() - vx;                    
                        if (kernel.isZero(temp)) {                      
                            expression = expression.replace(en, fVars[0]);                          
                        } else if (temp < 0) {
                            en.setOperation(ExpressionNode.MINUS);
                            num.set(-temp);
                        } else {
                            num.set(temp);
                        }
                        return;

                    case ExpressionNode.MINUS :
                        temp = num.getDouble() + vx;
                        if (kernel.isZero(temp)) {
                            expression = expression.replace(en, fVars[0]);                      
                        } else if (temp < 0) {
                            en.setOperation(ExpressionNode.PLUS);
                            num.set(-temp);
                        } else {
                            num.set(temp);
                        }
                        return;

                    default :
                        en.setLeft(shiftXnode(vx));
                }
            } catch (Exception e) {
                en.setLeft(shiftXnode(vx));
            }   
        }
        else if (left instanceof ExpressionNode) {
            translateX((ExpressionNode) left, vx);
        }       

        // right tree
        if (right == fVars[0]) {
            en.setRight(shiftXnode(vx));
        }
        else if (right instanceof ExpressionNode) {
            translateX((ExpressionNode) right, vx);
        }       
    }
    
    // node for (x - vx)
    final private ExpressionNode shiftXnode(double vx) {
        ExpressionNode node;        
        if (vx > 0) {
            node =
                new ExpressionNode(kernel,
                		fVars[0],
                    ExpressionNode.MINUS,
                    new MyDouble(kernel,vx));
        } else {
            node =
                new ExpressionNode(kernel,
                		fVars[0],
                    ExpressionNode.PLUS,
                    new MyDouble(kernel,-vx));
        }
        return node;
    }
    
    final public void translateY(double vy) {                                  
        try { // is there a constant number to the right
            MyDouble num = (MyDouble) expression.getRight();
            if (num == fVars[0]) { // right side might be the function variable
                addNumber(vy);
                return;
            }
            double temp;
            switch (expression.getOperation()) {
                case ExpressionNode.PLUS :
                    temp = num.getDouble() + vy;
                    if (kernel.isZero(temp)) {
                        expression = expression.getLeftTree();
                    } else if (temp < 0) {
                        expression.setOperation(ExpressionNode.MINUS);
                        num.set(-temp);
                    } else {
                        num.set(temp);
                    }
                    break;

                case ExpressionNode.MINUS :
                    temp = num.getDouble() - vy;
                    if (kernel.isZero(temp)) {
                        expression = expression.getLeftTree();
                    } else if (temp < 0) {
                        expression.setOperation(ExpressionNode.PLUS);
                        num.set(-temp);
                    } else {
                        num.set(temp);
                    }
                    break;                              

                default :
                    addNumber(vy);
            }
        } catch (Exception e) {         
            addNumber(vy);
        }
    }
    
    final private void addNumber(double n) {        
        if (n > 0) {
            expression =
                new ExpressionNode(kernel, 
                    expression,
                    ExpressionNode.PLUS,
                    new MyDouble(kernel,n));
        } else {
            expression =
                new ExpressionNode(kernel,
                    expression,
                    ExpressionNode.MINUS,
                    new MyDouble(kernel,-n));
        }   
    }
    
    /* ********************
     * POLYNOMIAL FACTORING
     * ********************/
     
    // remember calculated factors
    // do factoring only if expression changed
    private ExpressionNode factorParentExp;
    
    //  factors of polynomial function   
    private LinkedList symbolicPolyFactorList;
    private LinkedList numericPolyFactorList;
    private boolean symbolicPolyFactorListDefined;
    
    /**
     * Returns all non-constant polynomial factors of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * @param rootFindingSimplification: for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     * 
     */
    final public LinkedList getPolynomialFactors(boolean rootFindingSimplification) { 
    	// try to get symbolic polynomial factors
    	LinkedList result = getSymbolicPolynomialFactors(rootFindingSimplification);     	          	    
    	
    	// if this didn't work try to get numeric polynomial factors
    	if (result == null) {
    		result = getNumericPolynomialFactors(rootFindingSimplification);   
    	}
    	return result;
    }
    
    /**
     * Returns all non-constant polynomial factors of the n-th derivative
     * of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if the n-th derivative is no polynomial.
     * 
     * @param rootFindingSimplification: for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x  
     */
    final public LinkedList getSymbolicPolynomialDerivativeFactors(int n, boolean rootFindingSimplification) { 
    	Function deriv = getDerivative(n);
    	if (deriv == null)
			return null;
    	
    	// try to get symbolic polynomial factors
    	return deriv.getSymbolicPolynomialFactors(rootFindingSimplification);
    }
 
    /**
     * Tries to expand this function to a polynomial with numeric coefficients
     * and returns its n-th derivative as a PolyFunction object.
     * Note: may return null if the n-th derivative is no polynomial.
     * 
     */
    final public PolyFunction getNumericPolynomialDerivative(int n) {      	
    	// we expand the numerical expression of this function (all variables are
    	// replaced by their values) and try to get a polynomial.
    	// Then we take the derivative of this polynomial.    	
    	PolyFunction poly = expandToPolyFunction(expression, false);
		if (poly != null) { // we got a polynomial
			for (int i=0; i<n; i++) {
				poly = poly.getDerivative();
			}
		}	       	
    	return poly;
    }
       
    /**
     * Returns all symbolic non-constant polynomial factors of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * @param rootFindingSimplification: for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     */
    public LinkedList getSymbolicPolynomialFactors(boolean rootFindingSimplification) {       	
        if (factorParentExp != expression) { 
            // new expression
            factorParentExp = expression;
            
            if (symbolicPolyFactorList == null)
            	symbolicPolyFactorList = new LinkedList();
            else
            	symbolicPolyFactorList.clear();
            symbolicPolyFactorListDefined = 
            	addPolynomialFactors(expression, 
            			symbolicPolyFactorList, true, rootFindingSimplification);                   
        }               
        
        if (symbolicPolyFactorListDefined && symbolicPolyFactorList.size() > 0)
            return symbolicPolyFactorList;
        else
            return null;
    }
    
    /**
     * Returns all numeric non-constant polynomial factors of this function
     * relevant for root finding. A list of SymbolicPolyFunction objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * Note: we use the values of variables here (different to getSymbolicPolynomialFactors()).
     * 
     * @param rootFindingSimplification: for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     */
    private LinkedList getNumericPolynomialFactors(boolean rootFindingSimplification) {  
    	if (numericPolyFactorList == null)
    		numericPolyFactorList = new LinkedList();
        else
        	numericPolyFactorList.clear();
        
    	boolean success = addPolynomialFactors(expression, numericPolyFactorList, false, rootFindingSimplification);
        if (success && numericPolyFactorList.size() > 0)
        	return numericPolyFactorList;
        else
        	return null;
    }

    
    /**
     * Adds all polynomial factors in ev to the given list (ev is
     * an ExpressionNode in the beginning).
     * @return false when a non-polynomial was found (e.g. sin(x))
     * @param symbolic: true for symbolic coefficients, false for numeric coefficients
     * @param rootFindingSimplification: for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     */
    private boolean addPolynomialFactors(ExpressionValue ev, List l, 
    									 boolean symbolic, boolean rootFindingSimplification) {
        if (ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            switch (node.operation) {
                case ExpressionNode.MULTIPLY:
                    return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification) && 
                                addPolynomialFactors(node.getRight(), l, symbolic, rootFindingSimplification);
                    
            // try some simplifications of factors for root finding                                
                case ExpressionNode.POWER:
                case ExpressionNode.DIVIDE:   
                	if (!rootFindingSimplification) break;
                	
              	  	// divide: x in denominator: no polynomial
                	// power: x in exponent: no polynomial
                	if (node.getRight().contains(fVars[0]))
						return false;

                    // power: 
                    // symbolic: non-zero constants in exponent may be omitted   
                    // numeric: non-zero values in exponent may be omitted
                    if (!symbolic || node.getRight().isConstant()) {
                    	double rightVal;
                    	try {
                    		rightVal = ((NumberValue) node.getRight().evaluate()).getDouble();           
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    		return false;
                    	}                    	   
                 		if (node.operation == ExpressionNode.POWER) {                    			
                			if (kernel.isZero(rightVal))
                				// left^0 = 1
                				return addPolynomialFactors(new MyDouble(kernel, 1), l, symbolic, rootFindingSimplification);
                			else if (rightVal > 0) 
                				// left ^ right = 0  <=>  left = 0     for right > 0
                				return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);       
                		}                				
            			else { // division            				               				                			    
                    		if (kernel.isZero(rightVal))
								// left / 0 = undefined	 
               					return false;
							else
								// left / right = 0  <=>  left = 0     for right != null
                    			return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);
            			}
                    }                   
                    break;                                                             
                                                                            
                case ExpressionNode.ABS:
                case ExpressionNode.SGN:
                case ExpressionNode.SQRT:
                	if (!rootFindingSimplification) break;
                	
                    // these functions can be omitted as f(x) = 0 iff x = 0         
                    return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);                                              
            }           
        }
        
        // if we get here we have to add the ExpressionValue ev
        // add only non constant factors that are relevant for root finding
        if (!ev.isConstant()) {
            // build the factor: expanded ev, get the coefficients and build 
            // a polynomial with them         	
            PolyFunction factor = expandToPolyFunction(ev, symbolic);
            if (factor == null)
				return false; // did not work
            l.add(factor);
        }
        return true;
    }
    
    
    /**
     * Expands the given expression and builds a PolyFunction (or SymbolicPolyFunction) object
     * with the coefficients of the resulting polynomial.    
     * @return null when node is not a polynomial
     * @param symbolic: true for symbolic coefficients (SymbolicPolyFunction), false for numeric coefficients (PolyFunction)
     */
    private PolyFunction expandToPolyFunction(ExpressionValue ev, boolean symbolic) {
        ExpressionNode node;
        if (ev.isExpressionNode()) {
            node = (ExpressionNode) ev;
        } else {
            // wrap expressionValue
            node = new ExpressionNode(kernel, ev);
        }
                
        // get coefficients as strings
        String function = node.getCASstring(ExpressionNode.STRING_TYPE_MATH_PIPER, symbolic);        
        String [] strCoeffs = kernel.getPolynomialCoeffs(function, fVars[0].toString());
        if (strCoeffs == null)
			// this is not a valid polynomial           
            return null;
        
        // convert sring coefficients to coefficients of a SymbolicPolyFunction resp. PolyFunction
        int degree = strCoeffs.length - 1;
        if (symbolic) { 
        	// build SymbolicPolyFunction
	        SymbolicPolyFunction symbPolyFun = new SymbolicPolyFunction(degree);        
	        ExpressionNode [] symbCoeffs = symbPolyFun.getSymbolicCoeffs();                 
	        for (int i=0; i < strCoeffs.length; i++) {
	            symbCoeffs[i] = evaluateToExpressionNode(strCoeffs[i]);         
	            if (symbCoeffs[i] == null)
					return null; 
	            symbCoeffs[i].simplifyConstantIntegers();
	        }                       
	        return symbPolyFun;   
        } else { 
        	// build PolyFunction
        	try {
	        	PolyFunction polyFun = new PolyFunction(degree);                        
	  	        for (int i=0; i < strCoeffs.length; i++) {
	  	            polyFun.coeffs[i] = ((NumberValue) evaluateToExpressionNode(strCoeffs[i]).evaluate()).getDouble();         
	  	        }                       
	  	        return polyFun; 
        	} catch (Exception e) {
        		Application.debug("error in buildPolyFunction:");
        		e.printStackTrace();
        		return null;
        	}
        }
    }
    
    /**
     * Parses given String str and tries to evaluate it to an ExpressionNode.
     * Returns null if something went wrong.
     */
    private ExpressionNode evaluateToExpressionNode(String str) {
         try {
            ExpressionNode en = kernel.getParser().parseExpression(str);
            en.resolveVariables();
            return en;
         }
         catch (Exception e) {
            e.printStackTrace();
             return null;
         } 
         catch (Error e) {
            Application.debug("error in evaluateToExpressionNode: " + str);
            e.printStackTrace();
             return null;
         }
    }
    



/* ***************
 * CALULUS
 * ***************/
    
    /**
     * Returns n-th derivative of this function wrapped
     * as a GeoFunction object.
     */
    public GeoFunction getGeoDerivative(int n) {
    	if (geoDeriv == null)
    		geoDeriv = new GeoFunction(kernel.getConstruction());
    	
    	Function deriv = getDerivative(n);
    	geoDeriv.setFunction(deriv);
    	geoDeriv.setDefined(deriv != null);
    	return geoDeriv;
    }
    private GeoFunction geoDeriv;
 
    /**
     * Returns n-th derivative of this function
     */
    final public Function getDerivative(int n) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Derivative(%,");
    	sb.append(fVars[0]);
    	sb.append(",");
    	sb.append(n);
    	sb.append(")");
    	
    	return (Function) evalCasCommand(sb.toString(), true);
    }
    
    
    

//    final private Function derivative(int order) {     	 
//		// use CAS to get derivative
//        // temporarily replace the variable by "x"
//        String oldVar = fVar.toString();
//        fVar.setVarString("x");
//                
//        // build expression string for CAS             
//		sb.setLength(0);
//        sb.append("Derivative(");
//        // function expression with multiply sign "*"   
//		sb.append(expression.getCASstring(kernel.getCurrentCAS(), true));		
//		if (order > 1) {
//	        sb.append(",x,");
//	        sb.append(order);
//		}
//        sb.append(") ");
//					
//        try {                   	            
//            // evaluate expression by CAS 
//            String result = kernel.evaluateGeoGebraCAS(sb.toString());  
//           
//            // fast fail for e.g. "Differentiate(x)Floor(x)"
//            if (result.startsWith("Differentiate(") || result.startsWith("'diff("))
//            	return null;
//            
//    		// look for "Deriv(x)f(x)" strings and
//    		// replace them with "f'(x)"
//    		String [] derivs = result.split("Deriv\\(x\\)");
//    		sb.setLength(0);
//    		sb.append(derivs[0]); // part before first "Deriv(x)"
//    		for (int i=1; i < derivs.length; i++) {
//    			// replace "(x)" by "'(x)"
//    			sb.append(derivs[i].replaceFirst("\\(x\\)", "'(x)"));
//    		}
//    		result = sb.toString();
//                		
//            // it doesn't matter what label we use here as it is never used            			
//    		sb.setLength(0);
//    		sb.append("f(x) = ");			
//            sb.append(result);
//    
//             // parse result
//             Function fun = kernel.getParser().parseFunction(sb.toString());
//             fun.initFunction();
//             fun.getFunctionVariables().setVarString(oldVar);                       
//             return fun;
//         } catch (Error err) {       
//             //err.printStackTrace();
//        	 return null;
//         } catch (Exception e) {
//        	 //e.printStackTrace();
//             return null;
//         } catch (Throwable e) {
//			 return null;
//		}     
//         finally {
//        	 fVar.setVarString(oldVar);
//         }
//         
//    }	    
    
    /**
     * Creates the difference expression (a - b) and stores the result in
     * Function c.
     */
    final public static void difference(Function a, Function b, 
                    Function c) {
        // copy only the second function and replace b.fVar by a.fVar
        ExpressionNode left = a.expression;
        ExpressionNode right = b.expression.getCopy(a.kernel);
        
        // replace b.fVar in right by a.fVar to have only one function
        // variable in our function
        right.replace(b.fVars[0], a.fVars[0]);
        
        ExpressionNode diffExp= new ExpressionNode(a.kernel, left, ExpressionNode.MINUS, right);
        c.setExpression(diffExp);
        c.fVars[0] = a.fVars[0];
    }

    /**
     * Creates the difference expression (a - line) and stores the result in
     * Function c. This is needed for the intersection of function a and line ax + by + c = 0.
     * b != 0 is assumed.
     */
    final public static void difference(Function f, GeoLine line, Function c) {     
        // build expression for line: ax + by + c = 0 (with b != 0) 
        // explicit form: line: y = -a/b x - c/b
        // we need f - line: f(x) + a/b x + c/b
        double coeffX = line.x / line.y;
        double coeffConst = line.z / line.y;
        
        // build expression f - line: f(x) + a/b x + c/b
        ExpressionNode temp;
        // f(x) + a/b * x
        if (coeffX > 0) {
            temp = new ExpressionNode(f.kernel, 
                                    f.expression, 
                                    ExpressionNode.PLUS, 
                                    new ExpressionNode(f.kernel, 
                                        new MyDouble(f.kernel, coeffX),
                                        ExpressionNode.MULTIPLY, 
                                        f.fVars[0])
                                    );      
        } else {
            temp = new ExpressionNode(f.kernel, 
                                    f.expression, 
                                    ExpressionNode.MINUS, 
                                    new ExpressionNode(f.kernel, 
                                        new MyDouble(f.kernel, -coeffX),
                                        ExpressionNode.MULTIPLY, 
                                        f.fVars[0])
                                    );
        }
        
        
        // f(x) + a/b * x + c/b
        if (coeffConst > 0) {
            temp = new ExpressionNode(f.kernel, 
                            temp, 
                            ExpressionNode.PLUS, 
                            new MyDouble(f.kernel, coeffConst)
                        );          
        } else {
            temp = new ExpressionNode(f.kernel, 
                            temp, 
                            ExpressionNode.MINUS, 
                            new MyDouble(f.kernel, -coeffConst)
                        );
        }
                
        c.setExpression(temp);
        c.fVars[0] = f.fVars[0];
    }   
    
    
    /**
     * Tries to build a RealRootDerivFunction out of this
     * function and its derivative. This can be used for root finding.
     * Note: changes to the function will not affect the returned RealRootDerivFunction.
     */
    final public RealRootDerivFunction getRealRootDerivFunction() {
        Function deriv = getDerivative(1);
        if (deriv == null) 
            return null;
        else 
            return new DerivFunction(this, deriv);
    }
    
    /*
     * for root finding
     */
    private class DerivFunction implements RealRootDerivFunction {
        
        private Function fun, derivative;
        private double [] ret = new double[2];
        
        DerivFunction(Function fun, Function derivative) {
            this.fun = fun;
            this.derivative = derivative;
        }
        
        public double[] evaluateDerivFunc(double x) {
            ret[0] = fun.evaluate(x);
            ret[1] = derivative.evaluate(x);
            return ret;
        }

		public double evaluate(double x) { 
			return fun.evaluate(x);
		}

		public double evaluateDerivative(double x) {
			return derivative.evaluate(x);
		}
    }
	
	public final boolean includesDivisionByVariable() {
		if (expression == null)
			return false;
		else
			return expression.includesDivisionBy(fVars[0]);
	}

	public boolean isVector3DValue() {
		return false;
	}
	
}
