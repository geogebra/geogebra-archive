package jasymca;

import geogebra.main.Application;



public class GeoGebraJasymca extends Jasymca {
	
	private StringBuffer sb = new StringBuffer(100);
	
	public GeoGebraJasymca() {
		try {
			// init user functions for GeoGebra
			// everything else is defined
			eval("abs(x):=sqrt(x^2)");
			eval("floor(x):=round(x-1/2)");
			eval("ceil(x):=round(x+1/2)");						
		} catch(Exception e) {
			Application.debug("GeoGebraJasymca: " + e.getMessage());
			
			//e.printStackTrace();
		}		
	}
	
	final public String evaluate(String exp) {					
		try {
			Object result = eval(exp);		
	
			return formatExpression(result);
		} catch(Exception e) {
			Application.debug("GeoGebraJasymca: " + e.getMessage());

			//e.printStackTrace();
			return null;
		}				
	}
	
	/**
     * Expands the given JASYMCA expression and tries to 
     * get its polynomial coefficients.
     * The coefficients are returned in ascending order. 
     * If exp is not a polynomial null is returned.
     * 
     * example: getPolynomialCoeffs("3*a*x^2 + b"); returns
     * ["0", "b", "3*a"]
     */
	final public String [] getPolynomialCoeffs(String exp, String variable) {
		try {
			sb.setLength(0);
			sb.append("expand(");
			sb.append(exp);
			sb.append(')');		
	    	
			//Application.debug("exp for JASYMCA: " + sb.toString());  			

			// expand expression
			Object result = eval(sb.toString());
	 
			// number
			if (result instanceof Zahl) {
				String [] coeffs = new String[1];
				coeffs[0] = result.toString();
				return coeffs;
			}	
			
            // polynomial  
			else if (result instanceof Polynomial) {
	            Polynomial poly = (Polynomial) result;
	            if (variable != poly.var.toString())
	            	return null;            
	            
	            // get coefficients
	            int deg = poly.degree();
	            String [] coeffs = new String[deg+1];
	            for (int i=0; i <= deg; i++) {         	
	            	coeffs[i] = poly.coef[i].toString();         	
	            	//Application.debug("   coeff " + i + ": " + coeffs[i] + ", class: " + poly.coef[i].getClass());    	                                                                         
	            }	            
	            return coeffs;  
			}
			
		} catch(Exception e) {
			Application.debug("GeoGebraJasymca: " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}		
	/*
	  public static void main(String [] args) {
		  
	    	GeoGebraJasymca cas = new GeoGebraJasymca();
	    	String [] commands = {"x^2", "x^(1/3)",
			"3+2", "myfun(9)"};
			
			for (int i=0; i < commands.length; i++) {
				String result = cas.evaluate(commands[i]);
				//Application.debug("command: " + commands[i]);
				//Application.debug("result: " + result);        	        
			}
		    	
	  } 
	  */  
 }

