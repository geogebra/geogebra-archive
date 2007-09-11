/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.Functional;
import geogebra.kernel.arithmetic.NumberValue;

/**
 * This class is only needed to handle dependencies
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunction extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Function fun;
    private GeoFunction f; // output         
    
    private Function expandedFun;
    private ExpressionNode expression;
    private boolean expContainsFunctions; // expression contains functions

    /** Creates new AlgoDependentFunction */
    public AlgoDependentFunction(Construction cons, String label, Function fun) {
        this(cons, fun);
        f.setLabel(label);
    }
    
    AlgoDependentFunction(Construction cons, Function fun) {
        super(cons);
        this.fun = fun;
        f = new GeoFunction(cons);
        f.setFunction(fun);
        
        // look for FUNCTION or DERIVATIVE nodes in function
        expression = fun.getExpression();
        expContainsFunctions = containsFunctions(expression);
        if (expContainsFunctions) {
            expandedFun = new Function(fun, kernel);
        }
        
        setInputOutput(); // for AlgoElement
        
        compute();
    }
    
    String getClassName() {
        return "AlgoDependentFunction";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = fun.getGeoElementVariables();

        output = new GeoElement[1];
        output[0] = f;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return f;
    }

    final void compute() {
        // evaluation of function will be done in view (see geogebra.euclidian.DrawFunction)
        
        // check if function is defined
        boolean isDefined = true;
        for (int i=0; i < input.length; i++) {
            if (!input[i].isDefined()) {
                isDefined = false;
                break;
            }
        }
        f.setDefined(isDefined);
        
        if (isDefined && expContainsFunctions) {
            // expand the functions and derivatives in expression tree
            ExpressionValue ev = null;
            ev = expandFunctionDerivativeNodes(expression.getCopy());

            ExpressionNode node;
            if (ev.isExpressionNode()) 
                node = (ExpressionNode) ev;
            else
                node = new ExpressionNode(kernel, ev);
                
            expandedFun.setExpression(node);
            f.setFunction(expandedFun);
        }
    }
    
    /**
     * Expandes all FUNCTION and DERIVATIVE nodes in the given
     * expression. 
     * @return new ExpressionNode as result
     */
    private static ExpressionValue expandFunctionDerivativeNodes(ExpressionValue ev) {
        if (ev != null && ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            ExpressionValue leftValue = node.getLeft();
            
            switch (node.getOperation()) {
                case ExpressionNode.FUNCTION:                                   
                    // could be DERIVATIVE node
                    if (leftValue.isExpressionNode()) {
                    	leftValue = expandFunctionDerivativeNodes(leftValue);
                        node.setLeft(leftValue);
                        if (leftValue.isExpressionNode())
                        	return node;
                    }                        
                    
                	// we do NOT expand GeoFunctionConditional objects in expression tree
                    if (leftValue.isGeoElement() &&
                    	((GeoElement)leftValue).isGeoFunctionConditional()) 
                    		return node;
                
                	Function fun = ((Functional) leftValue).getFunction();
                	FunctionVariable x = fun.getFunctionVariable();
                	//  don't destroy the function
                	ExpressionNode funcExpression = fun.getExpression().getCopy();
                	// now replace every x in function by the expanded argument
                	return funcExpression.replace(x, 
                                    expandFunctionDerivativeNodes(node.getRight()));                    
            
                case ExpressionNode.DERIVATIVE:                		
                	// don't expand derivative of GeoFunctionConditional 
                    if (leftValue.isGeoElement() &&
                    	((GeoElement)leftValue).isGeoFunctionConditional()) {
                    	return node;
                    }
                    // STANDARD case
                    else {
                    	int order = (int) Math.round(((NumberValue)node.getRight()).getDouble());                        
                    	return ((Functional) leftValue).getGeoDerivative(order);	
                    }
                
                default: // recursive calls
                    node.setLeft(expandFunctionDerivativeNodes(leftValue));
                    node.setRight(expandFunctionDerivativeNodes(node.getRight()));
                    return node;
            }
        } else
			return ev;
    }
    
    public static boolean containsFunctions(ExpressionValue ev) {
        if (ev != null && ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            int op = node.getOperation();
            if (op == ExpressionNode.FUNCTION || 
                op == ExpressionNode.DERIVATIVE)
                return true;
			else
				return containsFunctions(node.getLeft()) || 
                            containsFunctions(node.getRight());
        }
        return false;
    }

    final public String toString() {
        StringBuffer sb = new StringBuffer();
        if (f.isLabelSet()) {
            sb.append(f.label);
            sb.append("(x) = ");
        }       
        sb.append(fun.toString());
        return sb.toString();
    }

}
