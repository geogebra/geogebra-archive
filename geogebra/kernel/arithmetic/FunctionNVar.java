/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Construction;
import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Function of N variables that returns either a number
 * or a boolean. This depends on the expression this function
 * is based on.
 * 
 * @author Markus Hohenwarter + mathieu
 */
public class FunctionNVar extends ValidExpression 
implements ExpressionValue, FunctionalNVar {    	
	
	protected ExpressionNode expression;
    protected FunctionVariable[] fVars;    
    
    // standard case: number function, see initFunction()
    protected boolean isBooleanFunction = false;
    
    // if the function is of type f(x) = c
    protected boolean isConstantFunction = false; 
      
    protected Application app;
    protected Kernel kernel;    
    
	private StringBuilder sb = new StringBuilder(80);	

	 /**
     * Creates new Function from expression. 
     * Note: call initFunction() after this constructor.
     */ 
    public FunctionNVar(ExpressionNode expression) {
    	kernel = expression.getKernel();
    	app = kernel.getApplication();        	
        
        this.expression = expression;       
    }
    
    
    /**
     * Creates new Function from expression where the function
     * variables in expression is already known.
     */ 
    public FunctionNVar(ExpressionNode exp, FunctionVariable[] fVars) {
    	kernel = exp.getKernel();
    	app = kernel.getApplication();               
        
        expression = exp;
        this.fVars = fVars;
    }
    
    /**
     * Creates a Function that has no expression yet. Use setExpression() to
     * do this later.    
     */ 
    public FunctionNVar(Kernel kernel) {
        this.kernel = kernel;
        app = kernel.getApplication();

    }
    
    // copy constructor
    public FunctionNVar(FunctionNVar f, Kernel kernel) {   
        expression = f.expression.getCopy(kernel);
        fVars = f.fVars; // no deep copy of function variable             
        isBooleanFunction = f.isBooleanFunction;
        isConstantFunction = f.isConstantFunction;
       
        app = kernel.getApplication();
        this.kernel = kernel;
    }
    
    public boolean isFunctionVariable(String var) {
    	if (fVars == null) 
    		return false;
    	else{
    		for (int i=0; i<fVars.length; i++)
    			if (fVars[i].toString().equals(var))
    				return true;
    		return false; //if none of function vars equals var
    	}
    }
       
    
    public Kernel getKernel() {
    	return kernel;
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new FunctionNVar(this, kernel);        
    }
    
    final public ExpressionNode getExpression() {
        return expression;
    }    
    
    public void resolveVariables() {
       expression.resolveVariables();                
    }
    
    /**
     * Replaces geo and all its dependent geos in this function's
     * expression by copies of their values.
     */
    public void replaceChildrenByValues(GeoElement geo) {     	
    	if (expression != null) {
    		expression.replaceChildrenByValues(geo);
    	}
    }
    
    /**
     * Use this method only if you really know
     * what you are doing.
     */
    public void setExpression(ExpressionNode exp) {
        expression = exp;
    }
    
    /**
     * Use this methode only if you really know
     * what you are doing.
     */
    public void setExpression(ExpressionNode exp, FunctionVariable[] vars) {
        expression = exp;
        fVars = vars;
    }
    
    public FunctionNVar getFunction() {
        return this;
    }
    
    public FunctionVariable[] getFunctionVariables() {
        return fVars;
    }
    
    final public String getVarString(int i) {
    	return fVars[i].toString();
    }
    
    /**
     * Number of arguments of this function, e.g. 2 for f(x,y)
     * @return
     */
    final public int getVarNumber() {
    	return fVars.length;
    }
    
    public String getVarString() {
    	StringBuilder sb = new StringBuilder();
    	for (int i=0; i < fVars.length-1; i++) {
    		sb.append(fVars[i].toString());
    		sb.append(", ");
    	}
    	sb.append(fVars[fVars.length-1].toString());
    	return sb.toString();
    }

    /**
     * Call this function to resolve variables and init the function.
     * May throw MyError (InvalidFunction).
     */
    public void initFunction() {    	
    	// replace function variables in tree
    	for (int i=0; i < fVars.length; i++) {
    		FunctionVariable fVar = fVars[i];
	   
        	// look for Variable objects with name of function variable and replace them
        	int replacements = expression.replaceVariables(fVar.toString(), fVar);
        	isConstantFunction = isConstantFunction && replacements == 0;
        
        	if (replacements == 0) {
	        	// x, y got polynomials while parsing 
	        	replacements = expression.replacePolynomials(fVar);
	        	isConstantFunction = isConstantFunction && replacements == 0;
        	}
    	}
     
    	
        // replace variable names by objects
        expression.resolveVariables();
        
        // the idea here was to allow something like: Derivative[f] + 3x
        // but wrapping the GeoFunction objects as ExpressionNodes of type FUNCTION
        // leads to Derivative[f](x) + 3x
        // expression.wrapGeoFunctionsAsExpressionNode();

        // replace all polynomials in expression (they are all equal to "1x" if we got this far)
        // by an instance of MyDouble
        
        
        //  simplify constant parts in expression
        expression.simplifyConstantIntegers();

        initType();
    }               
    
    private void initType() {
    	// check type of function
        ExpressionValue ev = expression.evaluate();        
        if (ev.isBooleanValue()) {
        	isBooleanFunction = true;
        }        
        else if (ev.isNumberValue()) {
        	isBooleanFunction = false;
        } 
        else {
			throw new MyError(app, "InvalidFunction");  
        }
    }

    /**
     * Returns whether this function always evaluates to BooleanValue.
     */
    final public boolean isBooleanFunction() {
    	return isBooleanFunction;
    }
    
    /**
     * Returns whether this function always evaluates to the same
     * numerical value, i.e. it is of the form f(x1,...,xn) = c.
     */
    final public boolean isConstantFunction() {
    	if (isConstantFunction)
    		return true;
    	for (int i=0;i<fVars.length; i++)
    		if (expression.contains(fVars[i]))
    			return false;
    	return true; //none of the vars appears in the expression
    }       
    
    public boolean isConstant() {
        return false;
    }

    public boolean isLeaf() {
        return true;
    }

    public ExpressionValue evaluate() {
        return this;
    }
    
    /**
     * Returns this function's value at position.    
     * @param vals
     * @return f(vals)
     */
    final public double evaluate(double[] vals) {
    	if (isBooleanFunction) {
    		// BooleanValue
    		return evaluateBoolean(vals) ? 1 : 0;
    	}
    	else {
    		// NumberValue
    		for (int i=0;i<fVars.length; i++){
    			//Application.debug(fVars[i].toString()+" <= "+vals[i]);
    			fVars[i].set(vals[i]);
    		}
    		return ((NumberValue) expression.evaluate()).getDouble();
    	}     
    }
    
    /**
     * Returns this function's value at position vals.
     * (Note: use this method if isBooleanFunction() returns true.
     * @param vals
     * @return f(vals)
     */
    final public boolean evaluateBoolean(double[] vals) {       
    	for (int i=0;i<fVars.length; i++)
			fVars[i].set(vals[i]);
        return ((BooleanValue) expression.evaluate()).getBoolean();       
    }
    
    public HashSet getVariables() {
        return expression.getVariables();
    }

    public GeoElement[] getGeoElementVariables() {
        return expression.getGeoElementVariables();
    }
    
    public String toString() {
        return expression.toString();
    }

    final public String toValueString() {
        return expression.toValueString();
    }
    
	final public String toLaTeXString(boolean symbolic) {
		return expression.toLaTeXString(symbolic);		
	}
    

	/* ***************
	 * CAS Stuff
	 * ***************/
      
	
      /**
       * Evaluates this function using the given CAS command. 
       * Caching is used for symbolic CAS evaluations.
       * 
       * @param ggbCasCmd the GeoGebraCAS command needs to include % in all places
       * where the function f should be substituted, e.g. "Derivative(%,x)"
       * @param symbolic true for symbolic evaluation, false to use values of GeoElement variables
       * @return resulting function
       */
      final public FunctionNVar evalCasCommand(String ggbCasCmd, boolean symbolic) {
    	  // remember expression and its CAS string 
    	  boolean useCaching = false;
    	  if (casEvalExpression != expression) {	 
    		  casEvalExpression = expression;
    		  if (symbolic)
    			  casEvalStringSymbolic = expression.getCASstring(kernel.getCurrentCAS(), true);
    		  
    		  // chaching should only be done if the expression doesn't contain other functions
    		  // e.g. this is important for f(x) = x^2, g(x,y) = f(x) + y, Derivative(g(x,y), x)
    		  // where we cannot cache the derivative fo g because f may have changed
    		  useCaching = symbolic && !expression.containsObjectType(CasEvaluableFunction.class);
    	  }
    	  
    	  // build command string for CAS
    	  String expString = symbolic ? casEvalStringSymbolic : expression.getCASstring(kernel.getCurrentCAS(), false);
    	  
    	  // substitute % by expString in ggbCasCmd
    	  String casString = ggbCasCmd.replaceAll("%", expString);
          
    	  FunctionNVar resultFun = null;
    	  if (useCaching) {
    		  // check if result is in cache
	          resultFun = getCasEvalMap().get(casString);
	          if (resultFun != null) {
	        	  //System.out.println("caching worked: " + casString + " -> " + resultFun);
	        	  return resultFun;
	          }
    	  }
          
          // eval with CAS
          try {                   	            
              // evaluate expression by CAS 
              String result = kernel.evaluateGeoGebraCAS(casString);  
            
              // parse CAS result back into GeoGebra        			
              sb.setLength(0);
              sb.append("f("); // this name is never used, just needed for parsing
              sb.append(getVarString());
              sb.append(") = ");
              sb.append(result);
      
              // parse result
	  		  if (getVarNumber() == 1) {
	  			resultFun = kernel.getParser().parseFunction(sb.toString());
	  		  } else {
	  			resultFun = kernel.getParser().parseFunctionNVar(sb.toString()); 
	  		  }
	  		   
	  		  resultFun.initFunction();
           } 
          catch (Error err) {       
              err.printStackTrace();
              resultFun = null;
           } catch (Exception e) {
          	 e.printStackTrace();
          	resultFun = null;
           } catch (Throwable e) {
  			resultFun = null;
  		} 
           
        // cache result
        if (useCaching && resultFun != null)
        	getCasEvalMap().put(casString, resultFun);
        
  	   //System.out.println("NO caching: " + casString + " -> " + resultFun);
        
        return resultFun;
      }
      private ExpressionNode casEvalExpression;
      private String casEvalStringSymbolic;
   
      private static HashMap<String, FunctionNVar> getCasEvalMap() {
	   	 if (casEvalMap == null) {
	   		casEvalMap = new HashMap<String, FunctionNVar>();
	   	 }
	   	 return casEvalMap;
	  }
	  private static HashMap<String, FunctionNVar> casEvalMap;
    
    
    public boolean isNumberValue() {
        return false;
    }

    public boolean isVectorValue() {
        return false;
    }
    
    public boolean isBooleanValue() {
        return false;
    }
    
    public boolean isListValue() {
        return false;
    }

    public boolean isPolynomialInstance() {
        return false;
    }   
    
    public boolean isTextValue() {
        return false;
    }
    
    final public boolean isExpressionNode() {
        return false;
    }
    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }       
    
	

	public boolean isVector3DValue() {
		return false;
	}
	
	 public String getLabelForAssignment() {
		 StringBuilder sb = new StringBuilder();
			// function, e.g. f(x) := 2*x
			sb.append(getLabel());
			sb.append("(");
			sb.append(getVarString());
			sb.append(")");	
			return sb.toString();
	 }
}
