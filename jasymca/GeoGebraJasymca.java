package jasymca;

import java.io.InputStream;




public class GeoGebraJasymca extends Jasymca {
	
	final public String evaluate(String exp) {					
		try {
			Object result = eval(exp);
			return formatExpression(result);
		} catch(Exception e) {
			System.err.println("Jasymca: " + e.getMessage());
			return null;
		}				
	}
	
	/*
	final public String evaluate2(String exp) {					
		try {
			InputStream is = new InputStream("test"):
		} catch(Exception e) {
			System.err.println("Jasymca: " + e.getMessage());
			return null;
		}				
	}*/
	
	  public static void main(String [] args) {
		  
	    	GeoGebraJasymca cas = new GeoGebraJasymca();
	    	String [] commands = {"sqrt(2)", "diff(sin(x),x)",
			"3+2"};
			
			for (int i=0; i < commands.length; i++) {
				String result = cas.evaluate(commands[i]);
				System.out.println("command: " + commands[i]);
				System.out.println("result: " + result);        	        
			}    	
	  } 
 }

