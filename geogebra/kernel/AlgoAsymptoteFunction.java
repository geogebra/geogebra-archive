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
		String limitPlusInfinity = kernel.evaluateMathPiper(sb.toString());
		
		Application.debug("input:"+sb.toString());
		Application.debug("limitPlusInfinity: "+limitPlusInfinity);

		sb.setLength(0);
        sb.append("Limit(x,-Infinity)");
        sb.append(functionIn);
		String limitMinusInfinity = kernel.evaluateMathPiper(sb.toString());
		
		Application.debug("input:"+sb.toString());
		Application.debug("limitMinusInfinity: "+limitMinusInfinity);

		// solve 1/f(x) == 0 to find vertical asymptotes
	    sb.setLength(0);
        sb.append("Solve(Simplify(1/(");
        sb.append(functionIn);
        sb.append("))==0,x)");
		String verticalAsymptotes = kernel.evaluateMathPiper(sb.toString());
		
		Application.debug("input:"+sb.toString());
		Application.debug("verticalAsymptotes: "+verticalAsymptotes);
		
	    sb.setLength(0);
        sb.append("Differentiate(x)");
        sb.append(functionIn);
		String firstDerivative = kernel.evaluateMathPiper(sb.toString());

	    sb.setLength(0);
        sb.append("Differentiate(x)");
        sb.append(firstDerivative);
		String secondDerivative = kernel.evaluateMathPiper(sb.toString());
		
		StringBuilder diagonalAsymptotes = new StringBuilder();
		String gradientStrMinus="";
		String interceptStrMinus="";
		
		sb.setLength(0);
        sb.append("Limit(x,-Infinity)");
        sb.append(secondDerivative);
		String limitMinusInfinity2d = kernel.evaluateMathPiper(sb.toString());
		if (limitMinusInfinity2d.equals("0")) {
			// look for diagonal asymptote
			
			sb.setLength(0);
	        sb.append("Limit(x,-Infinity)");
	        sb.append(firstDerivative);
			gradientStrMinus = kernel.evaluateMathPiper(sb.toString());
			
			if (!mathPiperError(gradientStrMinus)) {
				sb.setLength(0);
		        sb.append("Limit(x,-Infinity)Simplify(");
		        sb.append(functionIn);
		        sb.append("-");
		        sb.append(gradientStrMinus);
		        sb.append("*x)");
				interceptStrMinus = kernel.evaluateMathPiper(sb.toString());
				
				if (!mathPiperError(interceptStrMinus)) {
					diagonalAsymptotes.append("y=");
					diagonalAsymptotes.append(gradientStrMinus);
					diagonalAsymptotes.append("*x+");
					diagonalAsymptotes.append(interceptStrMinus);
					Application.debug("diagonal asymptote minus: y = "+gradientStrMinus+"x + "+interceptStrMinus);			
				}
			}		
		}
		

		sb.setLength(0);
        sb.append("Limit(x,Infinity)");
        sb.append(secondDerivative);
		String limitPlusInfinity2d = kernel.evaluateMathPiper(sb.toString());
		if (limitPlusInfinity2d.equals("0")) {
			// look for diagonal asymptote
			
			sb.setLength(0);
	        sb.append("Limit(x,Infinity)");
	        sb.append(firstDerivative);
			String gradientStrPlus = kernel.evaluateMathPiper(sb.toString());
			
			if (!mathPiperError(gradientStrPlus)) {
				sb.setLength(0);
		        sb.append("Limit(x,Infinity)Simplify(");
		        sb.append(functionIn);
		        sb.append("-");
		        sb.append(gradientStrPlus);
		        sb.append("*x)");
				String interceptStrPlus = kernel.evaluateMathPiper(sb.toString());
				
				if (!mathPiperError(interceptStrPlus) && !gradientStrPlus.equals(gradientStrMinus) && !interceptStrPlus.equals(interceptStrMinus)) {
					if (diagonalAsymptotes.length() > 0) diagonalAsymptotes.append(",");
					diagonalAsymptotes.append("y=");
					diagonalAsymptotes.append("gradientStr");
					diagonalAsymptotes.append("*x+");
					diagonalAsymptotes.append("interceptStr");
					Application.debug("diagonal asymptote plus: y = "+gradientStrMinus+"x + "+interceptStrMinus);			
				}
			}		
		}
		

		
		boolean addComma = false;
		
	    sb.setLength(0);
	    sb.append("{");
	    if (!mathPiperError(limitPlusInfinity) && !limitPlusInfinity.endsWith("Infinity")) {
	    	sb.append("y=");
	    	sb.append(limitPlusInfinity);
	    	addComma = true;
	    }
	    if (!limitMinusInfinity.equals(limitPlusInfinity) && !mathPiperError(limitMinusInfinity) && !limitMinusInfinity.endsWith("Infinity")) {
	    	if (addComma) sb.append(",");
	    	sb.append("y=");
	    	sb.append(limitMinusInfinity);
	    	addComma = true;
	    }
	    if (!mathPiperError(verticalAsymptotes) && verticalAsymptotes.length() > 2) {
	    	if (addComma) sb.append(",");
	    	verticalAsymptotes = verticalAsymptotes.replace('{',' ');
	    	verticalAsymptotes = verticalAsymptotes.replace('}',' ');
	    	verticalAsymptotes = verticalAsymptotes.replaceAll("==", "=");
	    	sb.append(verticalAsymptotes);
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
		
    }
    
    final private boolean mathPiperError(String str) {
		if (str == null || str.length()==0) return true;
		if (str.length() > 6) {
			if (str.startsWith("Limit")) return true;
			if (str.startsWith("Solve")) return true;
			if (str.startsWith("Undefined")) return true;
			if (str.indexOf("Infinity") > -1) return true;
		}
		return false;    	
    }
    
    final public String toString() {
    	return getCommandDescription();
    }

}
