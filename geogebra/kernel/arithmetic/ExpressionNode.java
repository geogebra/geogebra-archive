/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

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
import geogebra.kernel.GeoFunction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic3D.Vector3DValue;
import geogebra.main.Application;

import java.util.HashSet;
import java.util.Iterator;


/**
 * Tree node for expressions like "3*a - b/5"
 * @author  Markus
 * @version 
 */
public class ExpressionNode extends ValidExpression
implements ExpressionValue, ExpressionNodeConstants {   
	 
    

    
    public Application app;
    public Kernel kernel;
    public ExpressionValue left, right; 
    public int operation = NO_OPERATION;
    public boolean forceVector = false, forcePoint = false;
    
    public boolean holdsLaTeXtext = false;
    
    // for leaf mode
    public boolean leaf = false;

    public ExpressionNode(){};
    
    
    /** Creates new ExpressionNode */        
    public ExpressionNode(Kernel kernel, ExpressionValue left, int operation, ExpressionValue right) {  
        this.kernel = kernel;
        app = kernel.getApplication();
        
        this.operation = operation;         
        setLeft(left);
        if (right != null) {
            setRight(right);        
        } else { // set dummy value
        	setRight(new MyDouble(kernel, Double.NaN));
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
        right.setInTree(true); // needed for list operations eg k=2 then k {1,2}
        leaf = operation == NO_OPERATION; // right is a dummy MyDouble by default         
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
    final public void simplifyConstantIntegers() {                 
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
                node.simplifyConstantIntegers();
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
                node.simplifyConstantIntegers();
        }             
    }
    
    /**
     *  Expands equation expressions like (3*x + 2 = 5) / 2 to (3*x + 2)/2 = 5/2.
     */         
    final public ExpressionValue expandEquationExpressions() {    
    	if (leaf) return this;
    	
    	if (left.isExpressionNode()) {
    		left = ((ExpressionNode) left).expandEquationExpressions();
        }
    	if (right.isExpressionNode()) {
    		right = ((ExpressionNode) right).expandEquationExpressions();
        }
    	    	
    	switch (operation) {
	    	case PLUS:	  
	    	case MINUS:
	    	case MULTIPLY:
	    	case DIVIDE:
	    		// equ <operation> val 
	    		if (left instanceof Equation) {	    			
	    			((Equation) left).applyOperation(operation, right, false);
	    			leaf = true;
	    			right = null;
	        	}
	    		// val <operation> equ  
	    		else if (right instanceof Equation) {
	    			((Equation) right).applyOperation(operation, left, true);
	    			left = right;
	    			right = null;
	    			leaf = true;
	    		}
	    		break;	    		
    	}
    	
    	return this;
    }
    
    // used for 3D
    /*
    protected ExpressionValue evaluate(ExpressionValue v){
    	return v.evaluate();
    }
    */
    
     /**
     * interface ExpressionValue implementation
     */      
    
    public ExpressionValue evaluate() {   
    	return kernel.getExpressionNodeEvaluator().evaluate(this);
    }
    
    /**
     * 
     * @param lt
     * @param rt
     * @return false if not defined
     */
    private MyBoolean evalEquals(ExpressionValue lt, ExpressionValue rt) {
    	// booleans
    	if (lt.isBooleanValue() && rt.isBooleanValue())
			return new MyBoolean(
					((BooleanValue)lt).getBoolean() == ((BooleanValue)rt).getBoolean()
				); 
    	
    	//  nummber == number
    	else if (lt.isNumberValue() && rt.isNumberValue())
			return new MyBoolean(
    			kernel.isEqual(
    				((NumberValue)lt).getDouble(),
					((NumberValue)rt).getDouble()
				)
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
    		
    		return new MyBoolean(geo1.isEqual(geo2));
    	}
    	else if (lt.isVectorValue() && rt.isVectorValue()) {
    		VectorValue vec1 = (VectorValue) lt;
    		VectorValue vec2 = (VectorValue) rt;		
    		return new MyBoolean(vec1.getVector().equals(vec2.getVector()));
    	}
    		
    		/*    		// Michael Borcherds 2008-05-01
    		// replaced following code with one line:

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
     * Returns whether this ExpressionNode should evaluate to a GeoVector.
     * This method returns true when all GeoElements in this tree are GeoVectors and
     * there are no other constanct VectorValues (i.e. constant points)
     */
    public boolean shouldEvaluateToGeoVector() {   
    	boolean evalToVector = false;
    	
        if (left.isExpressionNode()) {             
        	evalToVector = (((ExpressionNode)left).shouldEvaluateToGeoVector());            
        }                                                               
        else if (left.isGeoElement()) {   
        	GeoElement geo = (GeoElement) left;
        	evalToVector = geo.isGeoVector() || geo.isNumberValue();
       }			     
        else if (left.isNumberValue()) {
        	evalToVector = true;
        }
        
        if (right != null && evalToVector) {
            if (right.isExpressionNode()) {
            	evalToVector = ((ExpressionNode)right).shouldEvaluateToGeoVector();            
            }
            else if (right.isGeoElement()) {
            	GeoElement geo = (GeoElement) right;
            	evalToVector = geo.isGeoVector() || geo.isNumberValue();
           }	
           else if (right.isNumberValue()) {
            	evalToVector = true;
           }
        }
        
        return evalToVector;        
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
                     
        return shouldEvaluateToGeoVector();
    }
    
    public void setForceVector() {
        // this expression should be considered as a vector, not a point
        forceVector = true;
    }
    
    final public boolean isForcedVector() {
    	return forceVector;
    }
    
    public void setForcePoint() {
        // this expression should be considered as a point, not a vector
        forcePoint = true;
    }
    
    final public boolean isForcedPoint() {
    	return forcePoint;
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
    
    final public boolean isSingleGeoElement() {
		return leaf && left.isGeoElement();
	}
    
    final public GeoElement getSingleGeoElement() {
    	return (GeoElement) left;
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
 
        String ret = printCASstring(symbolic);
            
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
    	StringBuilder sb = new StringBuilder();
    	
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
        		if (left.isLeaf()) {
       		 		sb.append(leftStr);
        		} else {
        			sb.append('(');
        			sb.append(leftStr);
        			sb.append(')');
        		}
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
		      		case STRING_TYPE_JASYMCA:
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
                
        	case IS_ELEMENT_OF:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\in");
		      			break;
		      			
		      		default:
		      			sb.append(strIS_ELEMENT_OF);        		
		      	}       
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
        	case CONTAINS:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\subseteq");
		      			break;
		      			      			
		      		default:
		      			sb.append(strCONTAINS);        		
		      	}       
           	 	sb.append(' ');
                sb.append(rightStr);
                break;
                
        	case CONTAINS_STRICT:
           	 	sb.append(leftStr);
           	 	sb.append(' ');
	           	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\subset");
		      			break;
		      			
		      		default:
		      			sb.append(strCONTAINS_STRICT);        		
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
       
           case VECTORPRODUCT:
       	 	sb.append(leftStr);
       	 	sb.append(' ');
       	 	switch (STRING_TYPE) {
		      		case STRING_TYPE_LATEX:
		      			sb.append("\\times");
		      			break;
		      		
		      		default:
		      			sb.append(strVECTORPRODUCT);        		
		      	}   
       	 	sb.append(' ');
               sb.append(rightStr);
               break;
       
            case PLUS:          
            	switch (STRING_TYPE) {
	        		case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append('(');
	        			sb.append(leftStr);
	                    sb.append(") + (");
	                    sb.append(rightStr);
	                    sb.append(')');
	                    break;
	                    
	                default:
	                	// TODO: remove
//	                	System.out.println("PLUS: left: " + leftStr + " " + isEqualString(left, 0, !valueForm) +
//	                			", right: " + isEqualString(left, 0, !valueForm) + " " + rightStr);
	                	
	                    // check for 0
            			if (isEqualString(left, 0, !valueForm)) {
		            		if (right.isLeaf() || opID(right) >= PLUS) {
	        					sb.append(rightStr);
	        				} else {
	        					sb.append('(');
	    	                    sb.append(rightStr);
	    	                    sb.append(')'); 
	        				}
        		    		break;
            			} 
            			else if (isEqualString(right, 0, !valueForm)) {
            				if (left.isLeaf() || opID(left) >= PLUS) {
	        					sb.append(leftStr);
	        				} else {
	        					sb.append('(');
	    	                    sb.append(leftStr);
	    	                    sb.append(')'); 
	        				}
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
	        		case STRING_TYPE_JASYMCA:
	        		case STRING_TYPE_MATH_PIPER:
	        			sb.append('(');
	        			sb.append(leftStr);
	                    sb.append(") - (");
	                    sb.append(rightStr);
	                    sb.append(')');
	                    break;
	                    
	                default:
		                sb.append(leftStr);   
		                
		                // check for 0 at right
	        			if (rightStr.equals("0")) {
	    		    		break;
	        			}
		                
		                
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
        		case STRING_TYPE_JASYMCA:
        		case STRING_TYPE_MATH_PIPER:
        			sb.append('(');
        			sb.append(leftStr);
                    sb.append(") * (");
                    sb.append(rightStr);
                    sb.append(')');
                    break;
                    
                default:
                	// check for 1 at left
        			if (isEqualString(left, 1, !valueForm)) {
        				if (right.isLeaf() || opID(right) >= MULTIPLY) {
        					sb.append(rightStr);
        				} else {
        					sb.append('(');
    	                    sb.append(rightStr);
    	                    sb.append(')'); 
        				}
    		    		break;
        			} 
        			// check for 0 at right
        			else if (isEqualString(right, 1, !valueForm)) {
        				if (left.isLeaf() || opID(left) >= MULTIPLY) {
        					sb.append(leftStr);
        				} else {
        					sb.append('(');
    	                    sb.append(leftStr);
    	                    sb.append(')'); 
        				}
    		    		break;
        			}
                   	// check for 0 at left
        			else if (valueForm && isEqualString(left, 0, !valueForm)) {
        				sb.append("0");
    		    		break;
        			} 
        			// check for 0 at right
        			else if (valueForm && isEqualString(right, 0, !valueForm)) {
        				sb.append("0");
    		    		break;
        			}
        			// check for degree sign at right
        			else if (rightStr.equals("1\u00b0") || rightStr.equals("\u00b0")) {
        				sb.append(leftStr);
        				sb.append("\u00b0");
        				break;
        			}
                	           
        			
	                boolean nounary = true;
	                
	                // left wing                  
	                if (left.isLeaf() || opID(left) >= MULTIPLY) { // not +, - 
	                    if (isEqualString(left, -1, !valueForm)) { // unary minus
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
	                int opIDright = opID(right);
	                if (right.isLeaf() || opIDright >= MULTIPLY) { // not +, -           
	                    // two digits colide: insert *    
	                    if (nounary) {                    	
	                		   if (Character.isDigit(rightStr.charAt(0)) &&
	                                Character.isDigit(sb.charAt(sb.length() - 1)) )
	                           {
	                			   sb.append(" * ");                    			   
	                            }
	                           else {
	                        	   switch (STRING_TYPE) {
		                			case STRING_TYPE_GEOGEBRA_XML:
		                				sb.append(" * ");  
		                				break;
		                				
		                			default:                        
		                				sb.append(' '); // space instead of '*'  
	                        	   }	                        	   
	                           }	                                             	                          
	                    } 	                 
	                    
	                    // show parentheses around these cases	
	                    if (rightStr.charAt(0) == '-'   // 2 (-5) or -(-5)
	                    	|| !nounary  && opIDright <= DIVIDE) // -(x * a) or -(x / a)
	                    {
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
                		
    				case STRING_TYPE_JASYMCA:
    				case STRING_TYPE_MATH_PIPER:
    					 sb.append('(');
		                 sb.append(leftStr);
		                 sb.append(")/(");
		                 sb.append(rightStr);
		                 sb.append(')');
		                 break;
    				
	    			default:	    				
	    				// check for 1 in denominator
            			if (isEqualString(right, 1, !valueForm)) {
		            		sb.append(leftStr);
        		    		break;
            			}
            			                       
		                // left wing              	
		                if (left.isLeaf()|| opID(left) >= DIVIDE) { // not +, -, *
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
					case STRING_TYPE_JASYMCA:
					case STRING_TYPE_MATH_PIPER:
						sb.append('(');
	                    sb.append(leftStr);
	                    sb.append(')');
		                 break;
		            
					default:
						
						/* removed Michael Borcherds 2009-02-08
						 * doesn't work eg  m=1   g(x) = (x - 1)^m (x - 3)
						 */
						 
					    // check for 1 in exponent
            			if (isEqualString(right, 1, !valueForm)) {
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
                     
   	        		case STRING_TYPE_JASYMCA:    
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
	        			
	        		case STRING_TYPE_JASYMCA:
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
	        			
	        		case STRING_TYPE_JASYMCA:
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
	        			
	        		case STRING_TYPE_JASYMCA:
	        			sb.append("log(");	
	        			sb.append(leftStr);
	        			sb.append(")/log(10)");
	        			break;
	        			
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
	        			
	        		case STRING_TYPE_JASYMCA:
	        			sb.append("log(");	
	        			sb.append(leftStr);
	        			sb.append(")/log(2)");
	        			break;
	        			
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
	        			
	        		case STRING_TYPE_JASYMCA:
	        			sb.append("sign(");
	        			break;

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
            	} else if (valueForm && (leftEval = left.evaluate()).isVector3DValue()) {            												
            		sb.append(kernel.format(((Vector3DValue)leftEval).getPointAsDouble()[0]));            		
            	} else {
            		switch (STRING_TYPE) {
            			case STRING_TYPE_LATEX:            		
            				sb.append("\\mathrm{x}(");
            				sb.append(leftStr);
                    		sb.append(')');
            				break;
            				
            			case STRING_TYPE_JASYMCA:
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
            	} else if (valueForm && (leftEval = left.evaluate()).isVector3DValue()) {            												
                		sb.append(kernel.format(((Vector3DValue)leftEval).getPointAsDouble()[1]));            		
            	} else {
            		switch (STRING_TYPE) {
	        			case STRING_TYPE_LATEX:            		
	        				sb.append("\\mathrm{y}(");
	        				sb.append(leftStr);
	                		sb.append(')');
	        				break;
	        				
	        			case STRING_TYPE_JASYMCA:
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
                
            case ZCOORD:            	
            	if (valueForm && (leftEval = left.evaluate()).isVector3DValue()) {            												
            		sb.append(kernel.format(((Vector3DValue)leftEval).getPointAsDouble()[2]));            		
            	} else {
            		switch (STRING_TYPE) {
	        			case STRING_TYPE_LATEX:            		
	        				sb.append("\\mathrm{z}(");
	        				sb.append(leftStr);
	                		sb.append(')');
	        				break;
	        				
	        			//case STRING_TYPE_JASYMCA:
		        		case STRING_TYPE_MATH_PIPER:
		        			// note: see GeoGebraCAS.insertSpecialChars()
		        			sb.append("z");		        		
		        			sb.append(UNICODE_PREFIX);
		        			sb.append("40"); // decimal unicode for (
		        			sb.append(UNICODE_DELIMITER);
		        			sb.append(leftStr);
		        			sb.append(UNICODE_PREFIX);
		        			sb.append("41"); // decimal unicode for )
		        			sb.append(UNICODE_DELIMITER);
		        			break;
	        		
		        		default:
		        			sb.append("z(");
		        			sb.append(leftStr);
		        			sb.append(')');
	        		}       
            	} 
                break;
                
            case FUNCTION:            	
            	// GeoFunction and GeoFunctionConditional should not be expanded
            	if (left.isGeoElement() &&
                     	((GeoElement)left).isGeoFunction()) 
            	{
            		GeoFunction geo = (GeoFunction)left;
            		if (geo.isLabelSet()) {
            			sb.append(geo.getLabel());    
            			sb.append('(');
                        sb.append(rightStr);
                        sb.append(')');
            		} else {    
            			// inline function: replace function var by right side
            			FunctionVariable var = geo.getFunction().getFunctionVariable();
            			String oldVarStr = var.toString();
            			var.setVarString(rightStr);
            			sb.append(geo.getLabel());
            			var.setVarString(oldVarStr);
            		}
            	} 
            	else if (valueForm && left.isExpressionNode()) {
            		ExpressionNode en = (ExpressionNode) left;
            		// left could contain $ nodes to wrap a GeoElement
            		// e.g. A1(x) = x^2 and B1(x) = $A$1(x) 
            		// value form of B1 is x^2 and NOT x^2(x)
            		switch (en.operation) {
            			case $VAR_ROW:
            			case $VAR_COL:
            			case $VAR_ROW_COL:
            				sb.append(leftStr); 
            				break;
            				
            			default:
            				sb.append(leftStr);  
	    	        		sb.append('(');
	    	                sb.append(rightStr);
	    	                sb.append(')');
	    	                break;
            		}
            	} 
            	else  {
	            	// standard case if we get here
	            	sb.append(leftStr);  
	        		sb.append('(');
	                sb.append(rightStr);
	                sb.append(')');
            	}
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

	public boolean isVector3DValue() {
		return false;
	}
	
	public static boolean isEqual(ExpressionValue ev1, ExpressionValue ev2) {
		if (ev1.isNumberValue() && ev2.isNumberValue()) {
			return Kernel.isEqual( ((NumberValue)ev1).getDouble(), ((NumberValue)ev2).getDouble(), Kernel.EPSILON);
		} else if (ev1.isTextValue() && ev2.isTextValue()) {
			return ((TextValue)ev1).toString().equals(((TextValue)ev2).toString());
		} else if (ev1.isGeoElement() && ev2.isGeoElement()) {
			return ((GeoElement)ev1).isEqual(((GeoElement)ev2));
		} else
			return false;
	}

    /**
     * Returns whether the given expression will give the same String output
     * as val.
     * @param symbolic: whether we should use the value (true) or the label (false) of ev when
     * it is a GeoElement
     */
    final public static boolean isEqualString(ExpressionValue ev, double val, boolean symbolic) {
    	if (ev.isLeaf() && ev instanceof NumberValue) {
    		// function variables need to be kept
    		if (ev instanceof FunctionVariable) {
    			return false;
    		}
    		
    		// check if ev is a labeled GeoElement
    		if (symbolic) {
    			if (ev.isGeoElement()) {
    				// labeled GeoElement
    				GeoElement geo = (GeoElement) ev;
    				if (geo.isLabelSet() || geo.isLocalVariable() || !geo.isIndependent())
    					return false;
    			}
    		}
    		
    		NumberValue nv = (NumberValue) ev;
    		return nv.getDouble() == val;    		
    	}
    	return false;
    }
	
}
