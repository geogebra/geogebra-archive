package jasymca;



public class GeoGebraJasymca extends Jasymca {
	
	public GeoGebraJasymca() {
		try {
			// init user functions for GeoGebra
			// everything else is defined
			eval("abs(x):=sqrt(x^2)");
			eval("floor(x):=round(x-1/2)");
			eval("ceil(x):=round(x+1/2)");						
		} catch(Exception e) {
			System.err.println("GeoGebraJasymca: " + e.getMessage());
		}		
	}
	
	final public String evaluate(String exp) {					
		try {
			Object result = eval(exp);
			return formatExpression(result);
		} catch(Exception e) {
			System.err.println("GeoGebraJasymca: " + e.getMessage());
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
			StringBuffer sb = new StringBuffer("expand(");
			sb.append(exp);
			sb.append(')');						
			
			// expand expression
			Object result = eval(sb.toString());
					 
            // try to convert to polynomial                        
            Polynomial poly = (Polynomial) result;
            if (variable != poly.var.toString())
            	return null;            
            
            // get coefficients
            int deg = poly.degree();
            String [] coeffs = new String[deg+1];
            for (int i=0; i <= deg; i++) {         	
            	coeffs[i] = poly.coef[i].toString();         	
            	//System.out.println("   coeff " + i + ": " + coeffs[i] + ", class: " + poly.coef[i].getClass());    	                                                                         
            }
            
            return coeffs;  
		} catch(Exception e) {
			System.err.println("GeoGebraJasymca: " + e.getMessage());
			return null;
		}
	}
	
	
	/*
	  public static void main(String [] args) {
		  
	    	GeoGebraJasymca cas = new GeoGebraJasymca();
	    	String [] commands = {"sqrt(2)", "diff(sin(\ud554),\ud554)",
			"3+2", "myfun(9)"};
			
			for (int i=0; i < commands.length; i++) {
				String result = cas.evaluate(commands[i]);
				System.out.println("command: " + commands[i]);
				System.out.println("result: " + result);        	        
			}    	
	  } 
	  */
 }

