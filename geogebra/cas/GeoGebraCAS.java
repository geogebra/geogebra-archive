/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.cas;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.UnivariatePolynomial;
import jscl.math.Variable;
import yacas.YacasInterpreter;

/**
 * This class provides an interface for GeoGebra to use JSCL.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS {
    
	private static final String UNICODE_PREFIX = "uNiCoDe";
    private static final String UNICODE_DELIMITER = "U";
    
	private YacasInterpreter yacas;
	private Variable xVar;
    private StringBuffer sbInsertSpecial, sbRemoveSpecial;
    
    
    
    
    public GeoGebraCAS() {    	    	
    	yacas = new YacasInterpreter();    	 
    	
    	// JSCL
    	try { 
    	 	xVar = Variable.valueOf("x"); 
    	} 
    	catch(Exception e) {
    		e.printStackTrace();
    	} 
    	
    	
    	sbInsertSpecial = new StringBuffer(80);
    	sbRemoveSpecial = new StringBuffer(80);    	     	
    }        
    
    /** 
     * Evaluates a YACAS expression and returns the result as a string.
     * e.g. exp = "D(x) (x^2)" returns "2*x"
     * @param expression string
     * @return result string (null possible)
     */ 
    final public String evaluateYACAS(String exp) {
    	//TODO: remove
    	//System.out.println("exp for YACAS: " + exp);
        
        try {
        	String result;
        	
        	// YACAS has problems with special characters:
        	// get rid of them        	
        	String myExp = removeSpecialChars(exp);          	        
           
        	result = yacas.Evaluate(myExp);
        	
        	//System.out.println("   result: " + result);
        	//System.out.println("   result (special chars): " + insertSpecialChars(result));
        	//System.out.println("  result: " + result);
            return insertSpecialChars(result);
            
        } catch (Error err) {
        	yacas.Evaluate("restart;");
            err.printStackTrace();
            return null;    
        } catch (Exception e) {
        	yacas.Evaluate("restart;");
            e.printStackTrace();
            return null;
        }       
    }
    
    /** 
     * Evaluates an JSCL expression and returns the result as a string.
     * e.g. exp = "d(x^2, x)" returns "2*x"
     * @param expression string
     * @return result string (null possible)
     *
    final public String evaluateJSCL(String exp) {
    	//System.out.println("exp for JSCL: " + exp);
        
        try {
        	String result;
        	
        	// JSCL has problems with special characters:
        	// get rid of them        	
        	String myExp = removeSpecialChars(exp);          	        
            Generic in=Expression.valueOf(myExp);
            
            // Strings for expand, simplify and factorize
            // we want the shortest string to be returned
            //String [] str = new String[2];
                
            Generic out = in.expand();
                        
        	//System.out.println("   expand: " + out);
          
            if (out.isPolynomial(xVar)) {
                // build polynomial
                UnivariatePolynomial p = UnivariatePolynomial.valueOf(out, xVar);
                result = toReverseString(p);
            } else {
                out = out.simplify();                                             
                result =  out.toString();
            }  
                       
        	//System.out.println("   result: " + result);
        	//System.out.println("   result (special chars): " + insertSpecialChars(result));
        	
            result = out.toString();
            return insertSpecialChars(result);
            
        } catch (Error err) {
            err.printStackTrace();
            return null;    
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
    } */
    
    /*
    private static String toReverseString(UnivariatePolynomial p) {
    	sbReverse.setLength(0);     
        if(p.signum()==0) sbReverse.append("0");
        int n=0;
        int d=p.degree();
        for(int i=d;i>=0;i--) {
            Generic a=p.get(i);
            if(a.signum()==0) continue;
            if(a instanceof Expression) a=a.signum()>0?GenericVariable.valueOf(a).expressionValue():GenericVariable.valueOf(a.negate()).expressionValue().negate();
            if(a.signum()>0 && n>0) sbReverse.append("+");
            if(i==0) sbReverse.append(a);
            else {
                if(a.compareTo(JSCLInteger.valueOf(1))==0);
                else if(a.compareTo(JSCLInteger.valueOf(-1))==0) sbReverse.append("-");
                else sbReverse.append(a).append("*");
                
                switch (i) {
                    case 1: 
                    	sbReverse.append("x"); break;
                    default: 
                    	sbReverse.append("x^");
                    	sbReverse.append(i);
                }
            }
            n++;
        }
        return sbReverse.toString();
    }*/
    
    /**
     * Finds the polynomial coefficients of
     * the given expression and returns it in ascending order. 
     * If exp is not a polynomial null is returned.
     * 
     * example: getPolynomialCoeffs("3*a*x^2 + b"); returns
     * ["0", "b", "3*a"]
     */
    final public String [] getPolynomialCoeffs(String exp) {
// TODO:  getPolynomialCoeffs() adapt for Yacas
    	// Coef(exp, x, 0 .. Degree(exp,x)), how to check for polynomial?
    	
        try {                     	
            // JSCL does not recognize x^2 / 4 as a polynomial
            // but it does recognize x^2 * 1/4, so we replace every "/" by "*1/"
            String noDivExp = removeSpecialChars(exp.replaceAll("/", "*1/"));         
            
            //System.out.println("getPolynomialCoeffs for " + exp);
            //System.out.println("noDivExp " + noDivExp);   
          
            Generic jsclExp = Expression.valueOf(noDivExp).expand();                                         
           
            //System.out.println("   expand: " + jsclExp);
            //System.out.println("   isPolynomial(x): " + jsclExp.isPolynomial(xVar));  
            
            // check if we have a polynomial
            if (!jsclExp.isPolynomial(xVar)) {
            	// try to simplify
            	jsclExp =  jsclExp.simplify();
            	 //System.out.println("   simplify: " + jsclExp);
            	if (!jsclExp.isPolynomial(xVar)) 
            		return null;
            }
            
            // build polynomial
            UnivariatePolynomial p = UnivariatePolynomial.valueOf(jsclExp, xVar);
            
            int deg = p.degree();
            String [] coeffs = new String[deg+1];
            for (int i=0; i <= deg; i++) {
            	Generic coeff = p.get(i);
            	// the coefficient must not include the variable
            	if (coeff.isConstant(xVar)) 
            		coeffs[i] = insertSpecialChars(coeff.toString());
            	else
            		return null;                                               
              
                //System.out.println("   coeff " + i + ": " + coeffs[i]);
                //System.out.println("   is constant: " + p.get(i).isConstant(xVar));                 
            }
            
            return coeffs;                      
        } catch (Error err) {
            err.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       
    }   
    
    /**
     * Converts all special characters (like greek letters) in the given String
     * to "unicode" + charactercode + DELIMITER Strings. This is neede because
     * JSCL cannot handle all unicode characters.     
     */
    private String removeSpecialChars(String str) {
    	int len = str.length();
    	sbRemoveSpecial.setLength(0);
    	
    	// convert every single character and append it to sb        
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            int code = (int) c;

            //  standard characters
            if ((code >= 32 && code <= 122)) {
                switch (code) {
                    case 95: // replace _
                    //case 39: // replace '
                    	sbRemoveSpecial.append(UNICODE_PREFIX);
                    	sbRemoveSpecial.append(code);
                    	sbRemoveSpecial.append(UNICODE_DELIMITER);
                        break;                                            

                    default :
                        //do not convert                
                    	sbRemoveSpecial.append(c);
                }
            }
            // special characters
            else {
            	switch (code) {
            		case 176: // replace degree sign by " * unicode_string_of_degree_sign"
            			sbRemoveSpecial.append("*");
            			
            		default:
            			sbRemoveSpecial.append(UNICODE_PREFIX);
            			sbRemoveSpecial.append(code);          
            			sbRemoveSpecial.append(UNICODE_DELIMITER);
            	}
            	
            }
        }
        return sbRemoveSpecial.toString();   	
    }        
    
    /**
     * Reverse operation of removeSpecialChars().
     */
    private String insertSpecialChars(String str) {  
    	int len = str.length();    	
    	sbInsertSpecial.setLength(0);    	
    	
    	// convert every single character and append it to sb    	
    	char prefixStart = UNICODE_PREFIX.charAt(0);
    	int prefixLen = UNICODE_PREFIX.length();
    	boolean prefixFound;
    	for (int i = 0; i < len; i++) {
           char c = str.charAt(i);           
           prefixFound = false;
           
           // first character of prefix found
           if (c == prefixStart) {
           		prefixFound = true;            		
           		// check prefix
           		int j = i;
                for (int k = 0; k < prefixLen; k++, j++) {
                	if (UNICODE_PREFIX.charAt(k) != str.charAt(j)) {
                		prefixFound = false;
                		break;
                	}
                }
                
                if (prefixFound) {            	
            		// try to get the unicode 
            		int code = 0;
            		char digit;
            		while (j < len && Character.isDigit(digit = str.charAt(j))) {
            			code = 10*code + (digit-48);
            			j++;
            		}
            		
            		if (code > 0 && code < 65536) { // valid unicode
            			sbInsertSpecial.append((char) code);
            			i = j;
            		} else { // invalid
            			sbInsertSpecial.append(UNICODE_PREFIX);
            			i += prefixLen;
            		}            		                	
                } else {
                	sbInsertSpecial.append(c); 
                }
           }           
           else {
           	sbInsertSpecial.append(c);               
           }
    	}
    	return sbInsertSpecial.toString();    	 	
    }
    
    
    /*
    public static void main(String [] args) {
    	YacasInterpreter yacas = new YacasInterpreter();
    	
    	String [] commands = {"mysin(x):=Sin(x);", "D(x) Sin(x);",
    			"D(ö) mysin(ö);"};
    	
    	for (int i=0; i < commands.length; i++) {
    		String result = yacas.Evaluate(removeSpecialChars(commands[i]));
    		System.out.println("command: " + commands[i]);
        	System.out.println("result: " + insertSpecialChars(result));        	        
    	}    	
    	
    }*/
 
}