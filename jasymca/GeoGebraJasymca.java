package jasymca;


public class GeoGebraJasymca extends Jasymca {
	
	public GeoGebraJasymca() {
		try {
			//TODO:init GeoGebra functions
			eval("abs(x):=sqrt(x^2)");
			// acos, asin, cosh, sinh, tanh, acosh, asinh, atanh
			// TODO: make sure GeoGebra can read Jasymca expressions too (like sign(x))
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
	  
 }

