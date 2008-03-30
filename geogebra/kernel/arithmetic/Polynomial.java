/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.Kernel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
  * An Polynomial is a list of Terms
  */  
public class Polynomial extends ValidExpression implements Serializable, ExpressionValue {        
	
	private static final long serialVersionUID = 1L;
    
    private ArrayList terms = new ArrayList();
    private Kernel kernel;

    public Polynomial(Kernel kernel) { this.kernel = kernel;}        
            
    public Polynomial(Kernel kernel, Term t) {
        this(kernel);
        terms.add( t );        
    }
    
    public Polynomial(Kernel kernel, String vars) {
        this(kernel);
        terms.add( new Term(kernel, 1.0d, vars) );        
    }
       
    public Polynomial(Kernel kernel, Polynomial poly) {   
        this(kernel);
        //System.out.println("poly copy constructor input: " + poly);        
        for (int i=0; i < poly.length(); i++) {
            append(new Term( (Term) poly.terms.get(i) ));
        }
        //System.out.println("poly copy constructor output: " + this);        
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new Polynomial(kernel, this);
    }
    
    public Term getTerm(int i) {
        return (Term) terms.get(i);
    }
        
    int length() {
        return terms.size();
    }
    
    boolean isEmpty() {
        return (terms.size() == 0);
    }
    
    /** 
     * Returns true if this polynomial equals "1x" 
     */
    boolean isX() {
        if (length() != 1) return false;
        try {
            Term t = (Term) terms.get(0);
            return (t.getVars().equals("x") && 
                        t.getCoefficient().isConstant() &&
                        ((NumberValue) t.getCoefficient().evaluate()).getDouble() == 1.0);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * append a single term
     */
    private void append(Term t) {
        terms.add( t );        
    }
    
    /**
     * add another Polynomial
     */
    public void add(Polynomial e) {       	     
        for (int i=0; i < e.length(); i++) {
            append( e.getTerm(i) );
        }
        simplify();
    }        
    
    /**
     * subtract another Polynomial
     */
    public void sub(Polynomial e) {         
        Polynomial temp = new Polynomial(kernel, e);         
        temp.multiply(-1.0d);
        add(temp);          // append -e
    }
    
    /**
     * add a Number
     */
    public void add(ExpressionValue number) {        
        append( new Term(kernel, number, "") );        
        simplify();         // add up parts with same variables       
    }
    
    /**
     * subtract a Number
     */
    public void sub(ExpressionValue number) {        
        Term subTerm = new Term(kernel, number, "");
        subTerm.multiply(-1.0d);        
        append(subTerm);
        simplify();         // add up parts with same variables        
    }
    
    /**
     * multiply with another Polynomial
     * store result in this Polynomial
     */ 
    public void multiply(Polynomial e) {        
        ArrayList temp = new ArrayList();
        int i, j;
        Term ti, newTerm;
        
        // multiply every term of this Polynomial
        // with every term of Polynomial e        
        for (i=0; i < length(); i++) {
            ti = getTerm(i);             
            for (j=0; j < e.length(); j++) {
                newTerm = new Term(ti);
                newTerm.multiply( e.getTerm(j) );
                temp.add( newTerm );
            }
        }
        terms = temp;
        simplify();        
    }
    
    /**
     * multiply every term with a double
     * store result in this Polynomial
     */
    public void multiply(ExpressionValue number) {       
       for (int i=0; i < length(); i++) {       
            ((Term) terms.get(i)).multiply(number);
       }       
    }
    
    /**
     * divide every term with a ExpressionValue
     * store result in this Polynomial
     */
    public void divide(ExpressionValue number) {       
        for (int i=0; i < length(); i++) {
            getTerm(i).divide(number);
        }        
    }
    
    /**
     * divides through a polynomial's constant coefficient
     */
    public void divide(Polynomial poly) {                               
        divide(poly.getConstantCoefficient());      
    }
    
    /**
     * multiply every term with a double
     * store result in this Polynomial
     */
    public void multiply(double d) {
        multiply(new MyDouble(kernel, d));
    }
    
    /**
     * compute Polynomial^power
     * store result in this Polynomial
     */
    public void power(int p) {         
        if (p == 0) {
            terms.clear(); // drop everything
            append( new Term(kernel, new MyDouble(kernel, 1), "") );
            return;
        }
        
        Polynomial exp = new Polynomial(kernel, this);            
        for (int i = 0; i < p-1; i++) {
            multiply(exp);
        }
    }   
    
    /**
     * multiply with another Polynomial
     * return result as Polynomial
     * 
    public Polynomial mult(Polynomial e) {
        Polynomial exp = (Polynomial) Util.copy(this);
        exp.multiply(e);
        return exp;
    }
     **/
    
    /**
     * multiply every term with a double
     * store result in this Polynomial
     *
    public Polynomial mult(double d) {
        Polynomial exp = new Polynomial(this);
        exp.multiply(d);
        return exp;
    }
     **/
    
    /**
     * returns true if all terms of this epression are constant
     */
    public boolean hasOnlyConstantCoeffs() {
        simplify();
        boolean isConst = true;      
        Iterator i = terms.iterator();
       
        while( i.hasNext() ) {
            isConst = isConst && ((Term)i.next()).hasNoVars();
        }
        return isConst;
    }
    
    public boolean isInteger() {
        boolean isInt = true;
        Term t;
        Iterator i = terms.iterator();       
        while( i.hasNext() ) {
            t = (Term)i.next();
            isInt = isInt && 
                    t.hasNoVars() &&
                    kernel.isInteger( ((NumberValue)t.getCoefficient().evaluate()).getDouble() );
        }
        return isInt;        
    }
       
    
    /**
     * returns the sum of constant numbers in this Polynomial <BR>
     * returns 0 if there is no constant number
     */
    public ExpressionValue getConstantCoefficient() {       
        // Constants are coefficients without variables
        return getCoefficient("");
    }

    /**
     * returns the sum of coefficients of variables in this Polynomial <BR>
     * returns 0 if variable does not occur <BR>
     * example: 3x -5y    getCoefficient("y") returns -5.0 <BR>
     * 3x -72zz +5y +3zz  getCoefficient("zz") returns -69.0 <BR>
     */
    public ExpressionValue getCoefficient(String variables) {            
        Term t, newTerm = new Term(kernel, new MyDouble(kernel, 0.0), variables);
                
        // add all coefficients of the wanted variables               
        for (int i=0; i < length(); i++) {
            t = getTerm(i);
            if (t.getVars().equals(variables))
                newTerm.addToCoefficient(t.coefficient);                
        }
        return newTerm.coefficient;
    }
    
    public double getCoeffValue(String variables) {
        return ((NumberValue) getCoefficient(variables).evaluate()).getDouble();
    }
    
    public double getConstantCoeffValue() {
        return getCoeffValue("");
    }     
   
    /**
     * simplifies this Polynomial so that every variable only occurs once.
     * example: simplify() on { (4,"xxy"), (7,"xy"), (-84.0,"xx"), (3,"xy") })
     * changes the Polynomial to { (4,"xxy"), (10,"xy"), (-84.0,"xx") }
     */
    void simplify() {        
        //System.out.println("simplify " + this);
        ArrayList list;        
        Object [] t;
        Term ti, tj;
        String vars;
        int i, j, len;                
                        
        list = new ArrayList(); // for the simplified terms
        t = terms.toArray(); // copy term references to array
        len = t.length;
        
        // terms may contain terms with same variables
        // example: {3 x, 5 y, -7 x} should be simplified to {-4 x, 5 y}        
        for (i = 0; i < len; i++) {                                    
            ti = (Term) t[i];
            if (ti != null) {
                vars = ti.getVars();
                // search for terms with same variable part        
                for (j = i + 1; j < len; j++) {
                    tj = (Term) t[j];
                    if ( tj !=null && vars.equals(tj.getVars()) ) 
                    {
                        ti.addToCoefficient( tj.coefficient );                      
                        t[j] = null;
                    }
                }
                // add simplified term to list
                if (!ti.coefficient.isConstant() ||
                    ((NumberValue)ti.coefficient.evaluate()).getDouble() != 0.0) { 
                    list.add( ti );                
                }
            }
        }
        
        // if nothing is left, keep a term with 0
        if (list.size() == 0) {
            list.add( new Term(kernel, new MyDouble(kernel, 0.0), "") );
        }        
        
        // sort the list
        //java.util.Collections.sort( list );
        terms = list;
        //System.out.println("simplified to " + this);
    }   

    boolean contains(String var) {        
        Iterator i = terms.iterator();        
        while (i.hasNext()) {        	
            if (((Term) i.next()).contains(var)) return true;
        }
        return false;
    }
    
    /**
     *  returns the degree of the Polynomial
     *  (max length of variables in a Term)
     */   
    public int degree() {
        // a quadratic Polynomial may only have terms with one or two variables or constant terms
        int deg = 0;
        int varLen; 
        
        if (terms.size() == 0) return -1;
        
        Iterator i = terms.iterator();                              
        while (i.hasNext()) {
            varLen = ((Term)i.next()).degree();
            if (varLen > deg) 
                deg = varLen;
        }
        return deg;
    }
    
     public String toString() {        
        int size = terms.size();
        if (size == 0) return null;
        
        StringBuffer sb = new StringBuffer();        
        String termStr;        
        boolean first = true;
                                
        for (int i=0; i < size; i++) {        
            termStr = ((Term)terms.get(i)).toString();   
            if (termStr != null) {            
                if (first) {
                    sb.append(termStr);
                    first = false;
                } else {                
                    if (termStr.charAt(0) == '-') {
                        sb.append(" - ");
                        sb.append(termStr.substring(1));
                    } else {
                        sb.append(" + ");
                        sb.append(termStr);
                    }            
                }
            }
        }             
        
        return sb.toString();        
    }
    
    public String toValueString() {
        return toString();
    }
    
	
	final public String toLaTeXString(boolean symbolic) {
		return toString();
	}
 
           
    public HashSet getVariables() {
        HashSet temp, vars = new HashSet();
        Iterator i = terms.iterator();        
        while (i.hasNext()) {
            temp = ((Term) i.next()).getCoefficient().getVariables();
            if (temp != null) vars.addAll(temp);
        }
        return vars;        
    }
    
    public void resolveVariables() {    
    	Iterator i = terms.iterator();        
        while (i.hasNext()) {
            ((Term) i.next()).getCoefficient().resolveVariables();            
        }      
    }

    
    public boolean isConstant() {
        HashSet vars = getVariables();
        return (vars == null || vars.size() == 0);
    }
        
    public boolean isLeaf() {
        return true;
    }
    
    public ExpressionValue evaluate() {
        return this;
    }


    final public boolean isNumberValue() {   
        return false;
    }
    	
	public boolean isBooleanValue() {
		return false;
	}

    final public boolean isVectorValue() {
        return false;
    }

    final public boolean isPolynomialInstance() {
        return true;
    }
    
    final public boolean isTextValue() {
        return false;
    }
    
    final public boolean isExpressionNode() {
        return false;
    }       
    
    public boolean isListValue() {
        return false;
    }

    
    final public boolean contains(ExpressionValue ev) {
        return ev == this;
    }    
    
} // end of class Polynomial
