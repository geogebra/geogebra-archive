/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
/**
 * Find asymptotes of ImplicitCurves
 * 
 * @author Darko Drakulic
 */
public class AlgoAsymptoteImplicitPoly extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoImplicitPoly ip; // input
    private GeoList g; // output
    private EquationSolver solver;
    
    private StringBuilder sb = new StringBuilder();
   
    public AlgoAsymptoteImplicitPoly(Construction cons, String label, GeoImplicitPoly ip) {
    	super(cons);
        this.ip = ip;            
        solver = getKernel().getEquationSolver();
    	
        g = new GeoList(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        
        g.setLabel(label);
    }
    
    public String getClassName() {
        return "AlgoAsymptoteImplicitPoly";
    }
    
    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = ip;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoList getResult() {
        return g;
    }

    protected final void compute() {       
        if (!ip.isDefined()) {
        	g.setUndefined();
        	return;
        }   
        
        if(ip.getDegX() == ip.getDegY())
        {
        	int degX = ip.getCoeff().length;
        	

        	sb.setLength(0);
    	    sb.append("{");
    		
        	double [] coeffk = new double[degX];
        	double [] coeffn = new double[degX-1];
        	
        	for(int i=degX-1, k=0; i>=0; i--)
        		for(int j=degX-1; j>=0; j--)
        			if(i+j == degX-1)
        				coeffk[k++] = ip.getCoeff()[i][j];
        				
        	for(int j=1; j<coeffk.length; j++)
				coeffn[j-1] = j*coeffk[j];
			
        	int numk = solver.polynomialRoots(coeffk);
			
			for(int k=0; k<numk; k++)
			{
				double div = 0;
				for(int i=0; i<coeffn.length; i++)
					div += coeffn[i]*Math.pow(coeffk[k], i);
				
				sb.append("y = " + 	coeffk[k] + " *x + " + ip.getCoeff()[degX-2][0]/div + ",");
			}
			sb.deleteCharAt(sb.length()-1);
	        sb.append("}");

        }
        
		g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
		g.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
    }
    
    
    final public String toString() {
    	return getCommandDescription();
    }
 
}
