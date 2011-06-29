/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.util.ArrayList;

import edu.jas.util.ArrayUtil;
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
        
        ArrayList<double[]> asymptotes = new ArrayList<double[]>();
        
        
    	int deg = ip.getDeg();
    	
    	sb.setLength(0);
	    sb.append("{");
		
	    double [][] coeff = new double[deg+1][deg+1];
	    for(int i=0; i<ip.getCoeff().length; i++)
	    	for(int j=0; j<ip.getCoeff()[0].length; j++)
	    	    	coeff[i][j] = ip.getCoeff()[i][j];
	    
	    
    	double [] coeffk = new double[deg+1];
    	double [] diag = new double[deg+1];
    	double [] upDiag = new double[deg];
    	
    	
    	for(int i=deg, k=0, m=0; i>=0; i--)
    		for(int j=deg; j>=0; j--)
    			if(i+j == deg)
    			{
    				coeffk[k] = coeff[i][j];
    				diag[k++] = coeff[i][j];
    			}
    			else if(i+j == deg-1)
    				upDiag[m++] = coeff[i][j];
    	
		
	
		/**
		 * Asymptotes parallel to x-axe and y-axe
		 */
		
    	double[] parallelCoeff = new double[deg+1];
    	
		// parallel with x-axe
		if(coeff[0][deg] == 0)
		{
			for(int i=deg-1; i>=0; i--)
				if(sumRow(coeff, i, 2) != 0)
				{
					for(int j=0; j<deg; j++)
						parallelCoeff[j] = coeff[j][i];
					int numx = solver.polynomialRoots(parallelCoeff);
					for(int k=0; k<numx; k++)
					{
						double [] asy = {1.0, 0.0, -parallelCoeff[k]};
						asymptotes.add(asy);
					}
					break;
				}
		}
		
		// parallel with y-axe
		if(coeff[deg][0] == 0)
		{
			for(int i=deg; i>=0; i--)
				if(sumRow(coeff, i, 1) != 0)
				{
					for(int j=0; j<deg; j++)
						parallelCoeff[j] = coeff[i][j];
					int numx = solver.polynomialRoots(parallelCoeff);
					for(int k=0; k<numx; k++)
					{
						double [] asy = {0.0, 1.0, -parallelCoeff[k]};
						asymptotes.add(asy);
					}
					break;
				}
		}
		
		
		/**
		 * Other asymptotes
		 */
	
    	int numk = solver.polynomialRoots(coeffk);
    	
    	for(int i=0; i<numk; i++)
		{
			double down = 0, up = 0;
			for(int j=0; j<upDiag.length; j++)
			{	
				up += upDiag[j]*Math.pow(coeffk[i], j);
				down += (j+1)*diag[j+1]*Math.pow(coeffk[i], j);
			}
			if(down == 0)
				continue;
			
			double [] asy = {-coeffk[i], 1, up/down};
			asymptotes.add(asy);
		}
    	
    	for(int i=0; i<asymptotes.size(); i++)
    		for(int j=i+1; j<asymptotes.size(); j++)
    	    	if(Math.abs(Math.abs(asymptotes.get(i)[0]) - Math.abs(asymptotes.get(j)[0])) < 1E-2 &&
    	    			Math.abs(Math.abs(asymptotes.get(i)[1]) - Math.abs(asymptotes.get(j)[1])) < 1E-2 &&
    	    			Math.abs(Math.abs(asymptotes.get(i)[2]) - Math.abs(asymptotes.get(j)[2])) < 1E-2 )
    	    		asymptotes.remove(j--);
    		
    	for(int i=0; i<asymptotes.size(); i++)
        	sb.append(asymptotes.get(i)[0] + "*x + " + asymptotes.get(i)[1] + "*y + " + asymptotes.get(i)[2] + "=0,");
    		
    	if(sb.length() > 1)
			sb.deleteCharAt(sb.length()-1);
        
    	sb.append("}");

        
		g.set(kernel.getAlgebraProcessor().evaluateToList(sb.toString()));	
		g.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
    }
    
    /**
     * compute a sum of elements from i-th row or column if matrix mat
     * rc = 1 for rows, rc = 2 for columns
     */
    private double sumRow(double [][] mat, int i, int rc)
    {
    	double sum = 0;
    	for(int j=0; j<mat.length; j++)
    		if(rc == 1)
    			sum += mat[i][j];
    		else
    			sum += mat[j][i];
    	return sum;
    }
    
    
    final public String toString() {
    	return getCommandDescription();
    }
 
}
