/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Find asymptotes
 * 
 * @author Michael Borcherds
 */
public class AlgoAsymptoteFunction extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private GeoList g; // output        
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoAsymptoteFunction(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoAsymptoteFunction";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
                
        
	    String functionIn = f.getFormulaString(ExpressionNode.STRING_TYPE_MATH_PIPER, true);

	    	    
	    sb.setLength(0);
        sb.append("Limit(x,Infinity)");
        sb.append(functionIn);
		String limitPlusInfinity = evaluateMathPiper(sb.toString());
		
		Application.debug("input:"+sb.toString());
		Application.debug("limitPlusInfinity: "+limitPlusInfinity);

		sb.setLength(0);
        sb.append("Limit(x,-Infinity)");
        sb.append(functionIn);
		String limitMinusInfinity = evaluateMathPiper(sb.toString());
		
		Application.debug("input:"+sb.toString());
		Application.debug("limitMinusInfinity: "+limitMinusInfinity);

		// solve 1/f(x) == 0 to find vertical asymptotes
	    sb.setLength(0);
	    
	    // TODO: doesn't work with GeoFunctionConditionals
        sb.append("Solve(Simplify(1/(");
        
        sb.append(functionIn);
        sb.append("))==0,x)");
		String verticalAsymptotes = evaluateMathPiper(sb.toString());
		
		Application.debug("solutions: "+verticalAsymptotes);
		
    	boolean addComma = false;
    	StringBuilder verticalSB = new StringBuilder();
    	
    	if (!mathPiperError(verticalAsymptotes, false) && verticalAsymptotes.length() > 2) {
		
	    	verticalAsymptotes = verticalAsymptotes.replace('{',' ');
	    	verticalAsymptotes = verticalAsymptotes.replace('}',' ');
	    	verticalAsymptotes = verticalAsymptotes.replaceAll("x==", "");
	    	String[] verticalAsymptotesArray = verticalAsymptotes.split(",");
	    	
	    	// check they are really asymptotes
	    	for (int i = 0 ; i < verticalAsymptotesArray.length ; i++) {
	    		
	    		boolean repeat = false;
	    		if (i > 0) { // check for repeats
	    			for (int j = i ; j < verticalAsymptotesArray.length ; i++) {
	    				if (verticalAsymptotesArray[i].equals(verticalAsymptotesArray[j])) {
	    					repeat = true;
	    					break;
	    				}
	    			}
	    		}
	    		
	    		if (!repeat) {
	    		
		    		sb.setLength(0);
		            sb.append("Limit(x,");
		            sb.append(verticalAsymptotesArray[i]);
		            sb.append(",Left)");
		            sb.append(functionIn);
		     		String limit = evaluateMathPiper(sb.toString());
		            Application.debug("checking for vertical asymptote: "+sb.toString()+" = "+limit);
		            if (!mathPiperError(limit, true)) {
		            	if (addComma) verticalSB.append(',');
		            	addComma = true;
		            	verticalSB.append("x=");
		            	verticalSB.append(verticalAsymptotesArray[i]);
		            }
	    		}
	   		
	    	}
	
			Application.debug("verticalAsymptotes: "+verticalSB);
		}
		
	    sb.setLength(0);
        sb.append("Simplify(Differentiate(x)");
        sb.append(functionIn);
        sb.append(')');
		String firstDerivative = evaluateMathPiper(sb.toString());
		
		StringBuilder diagonalAsymptotes = new StringBuilder();
		if (!mathPiperError(firstDerivative, false)) {
	
		    //sb.setLength(0);
	        //sb.append("Simplify(Differentiate(x)");
	        //sb.append(firstDerivative);
	        //sb.append(')');
			//String secondDerivative = evaluateMathPiper(sb.toString());
			
			String gradientStrMinus="";
			String interceptStrMinus="";
			
			//sb.setLength(0);
	        // sb.append("Limit(x,-Infinity)");
	        //sb.append(secondDerivative);
			//String limitMinusInfinity2d = evaluateMathPiper(sb.toString());
			//if (limitMinusInfinity2d.equals("0"))
			{
				// look for diagonal asymptote
				
				sb.setLength(0);
		        sb.append("Limit(x,-Infinity)");
		        sb.append(firstDerivative);
				gradientStrMinus = evaluateMathPiper(sb.toString());
				
				if (!mathPiperError(gradientStrMinus, false) && !gradientStrMinus.equals("0")) {
					sb.setLength(0);
			        sb.append("Limit(x,-Infinity)Simplify(");
			        sb.append(functionIn);
			        sb.append("-");
			        sb.append(gradientStrMinus);
			        sb.append("*x)");
					interceptStrMinus = evaluateMathPiper(sb.toString());
					
					if (!mathPiperError(interceptStrMinus, false)) {
						diagonalAsymptotes.append("y=");
						diagonalAsymptotes.append(gradientStrMinus);
						diagonalAsymptotes.append("*x+");
						diagonalAsymptotes.append(interceptStrMinus);
						Application.debug("diagonal asymptote minus: y = "+gradientStrMinus+"x + "+interceptStrMinus);			
					}
				}		
			}
			
	
			//sb.setLength(0);
	        //sb.append("Limit(x,Infinity)");
	        //sb.append(secondDerivative);
			//String limitPlusInfinity2d = evaluateMathPiper(sb.toString());
			//if (limitPlusInfinity2d.equals("0"))
			{
				// look for diagonal asymptote
				
				sb.setLength(0);
		        sb.append("Limit(x,Infinity)");
		        sb.append(firstDerivative);
				String gradientStrPlus = evaluateMathPiper(sb.toString());
				
				if (!mathPiperError(gradientStrPlus, false) && !gradientStrPlus.equals("0")) {
					sb.setLength(0);
			        sb.append("Limit(x,Infinity)Simplify(");
			        sb.append(functionIn);
			        sb.append("-");
			        sb.append(gradientStrPlus);
			        sb.append("*x)");
					String interceptStrPlus = evaluateMathPiper(sb.toString());
					
					if (!mathPiperError(interceptStrPlus, false) && !gradientStrPlus.equals(gradientStrMinus) && !interceptStrPlus.equals(interceptStrMinus)) {
						if (diagonalAsymptotes.length() > 0) diagonalAsymptotes.append(",");
						diagonalAsymptotes.append("y=");
						diagonalAsymptotes.append("gradientStr");
						diagonalAsymptotes.append("*x+");
						diagonalAsymptotes.append("interceptStr");
						Application.debug("diagonal asymptote plus: y = "+gradientStrMinus+"x + "+interceptStrMinus);			
					}
				}		
			}
		}

		
		addComma = false;
		
	    sb.setLength(0);
	    sb.append("{");
	    if (!mathPiperError(limitPlusInfinity, false)) {
	    	sb.append("y=");
	    	sb.append(limitPlusInfinity);
	    	addComma = true;
	    }
	    if (!mathPiperError(limitMinusInfinity, false) && !limitMinusInfinity.equals(limitPlusInfinity)) {
	    	if (addComma) sb.append(",");
	    	sb.append("y=");
	    	sb.append(limitMinusInfinity);
	    	addComma = true;
	    }
	    
	    // vertical asymptotes
	    if (verticalSB.length() > 0) {
	    	if (addComma) sb.append(",");
	    	sb.append(verticalSB);	    	
	    	addComma = true;
	    }
	    if (diagonalAsymptotes.length() > 0) {
	    	if (addComma) sb.append(",");
	    	sb.append(diagonalAsymptotes);
	    }
	    sb.append("}");
		
		g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
		
		g.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		
		g.setDefined(true);	
		
		kernel.evaluateMathPiper("Limit(x,Infinity)(if(x-1<0)1 else -1)");
		
    }
    
    final private boolean mathPiperError(String str, boolean allowInfinity) {
		if (str == null || str.length()==0) return true;
		if (str.length() > 6) {
			if (str.startsWith("Limit")) return true;
			if (str.startsWith("Solve")) return true;
			if (str.startsWith("Undefined")) return true;
			if (!allowInfinity && str.indexOf("Infinity") > -1) return true;
		}
		return false;    	
    }
    
    final public String toString() {
    	return getCommandDescription();
    }
    
    private String evaluateMathPiper(String exp) {
    	String ret = kernel.evaluateMathPiper(exp);
    	
    	// workaround for http://code.google.com/p/mathpiper/issues/detail?id=35
    	// TODO remove when fixed
    	if (ret != null) ret = ret.replaceAll("else", " else ");
    	
    	// *partial* workaround for MathPiper bug
    	// http://code.google.com/p/mathpiper/issues/detail?id=31
    	if (ret == null || ret.indexOf("else") == -1) return ret;
    	
    	String str[] = ret.split("else");
    	if (str.length != 2) return "Undefined";
    	
    	if (str[0].equals(str[1])) {
    		//System.err.println("Manually simplifying "+ret+" to "+str[1]);
    		return str[0]; // eg 0else0 returned
    	}
    	
    	return ret; // might be error or valid expression
    	
    }

}
