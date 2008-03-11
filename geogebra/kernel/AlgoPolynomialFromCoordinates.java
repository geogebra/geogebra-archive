/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License v2 as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import java.math.BigDecimal;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;

/**
 * Fit a Polynomial exactly to a set of coordinates. Unstable above about 12 coords
 * adapted from AlgoPolynomialFromFunction
 * @author Michael Borcherds
 */
public class AlgoPolynomialFromCoordinates extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoList inputList; // input
    private GeoFunction g; // output         
   
    public AlgoPolynomialFromCoordinates(Construction cons, String label, GeoList inputList) {
    	super(cons);
        this.inputList = inputList;            	
    	      
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    protected String getClassName() {
        return "AlgoPolynomialFromCoordinates";
    }
    
    // for AlgoElement
    void setInputOutput() {
        input = new GeoElement[1];
        input[0] = inputList;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getPolynomial() {
        return g;
    }

//  ON CHANGE: similar code is in AlgoTaylorSeries
    final void compute() {       
        if (!inputList.isDefined()) {
        	g.setUndefined();
        	return;
        }    
        
        int n=inputList.size();
        
        if (n<2) { // can't draw a unique polynomial through 0 or 1 points!
        	g.setUndefined();
        	return;
        }    
        
        double x[] = new double[n];
        double y[] = new double[n];
        double xy[] = new double[2];
        
        // copy inputList into two arrays
        for (int i=0 ; i<n ; i++)
        {
      	  GeoElement geo = inputList.get(i); 
   		  if (geo.isGeoPoint()) {
       		GeoPoint listElement = (GeoPoint) inputList.getCached(i); 
       		listElement.getInhomCoords(xy);
       		x[i]=xy[0];
   			y[i]=xy[1];
  		  }
   		  else
   		  {
   		    g.setUndefined();
       	    return;			
   		  }
        }
        
        // check all the x-coordinates are different
        for (int i=0 ; i<n-1 ; i++)
        for (int j=i+1 ; j<n ; j++)
        {
        	if (x[i]==x[j])
        	{
       		    g.setUndefined();
           	    return;			        		
        	}
        }
        
   		// calculate the coefficients
        double cof[] = new double[n];
   		try {
   		  if (n<15) polcoe(x,y,n,cof);
			 // Michael Borcherds 2008-03-09 added polcoeBig
   		  else polcoeBig(x,y,n,cof);
   		}
   		catch (Exception e)
   		{
   		    g.setUndefined();
       	    return;			   			
   		}

        //  build polynomial 
   		Function polyFun = buildPolyFunctionExpression(kernel,cof);
   		
   		if (polyFun==null)
   		{
   		    g.setUndefined();
       	    return;			   			   			
   		}
   			
		g.setFunction(polyFun);			
		g.setDefined(true);	
		
		
    } 
    
    public static Function buildPolyFunctionExpression(Kernel kernel,double [] cof)
        {
    	int n=cof.length;
        ExpressionNode poly = null; // expression for the expanded polynomial		
		FunctionVariable fVar = new FunctionVariable(kernel);	
		double coeff;
  	    for (int k = n-1; k >= 0 ; k--) {
  	        coeff = cof[k]; 	 
			 if (Double.isNaN(coeff) || Double.isInfinite(coeff)) {
					 return null;
			 }
			 // Michael Borcherds 2008-03-09
			 // removed these two lines to make Polynomial[] work better
			 // leading coefficient can be eg E-30
			 //			 else if (kernel.isZero(coeff)) 
			 //			 	continue; // this part vanished
			 				
			boolean negativeCoeff = coeff < 0; 					
			
			// build the expression x^k
			ExpressionValue powerExp; 
			switch (k) {
			    case 0: 
			    	powerExp = null;
			    	break;
			    	
				case 1: 
					powerExp = fVar; 
					break;
					
				default: powerExp = 				
					 new ExpressionNode(kernel, 
							fVar,
							ExpressionNode.POWER, 
							new MyDouble(kernel, k));						
			}
					
			// build the expression 
			// (coeff) * x^k
			ExpressionValue partExp;
			MyDouble coeffMyDouble = null;
			if (kernel.isEqual(coeff, 1.0)) {
				if (powerExp == null)
					partExp = new MyDouble(kernel, 1.0);
				else
					partExp = powerExp;
			} else {
				coeffMyDouble = new MyDouble(kernel, coeff);
				if (powerExp == null)
					partExp = coeffMyDouble;
				else 
					partExp = new ExpressionNode(kernel, 
									coeffMyDouble, 
									ExpressionNode.MULTIPLY, 
									powerExp);
			}								
	
			 // add part to series
			if (poly == null) {
				poly = new ExpressionNode(kernel, partExp);
			} else {
				if (negativeCoeff) {
					if (coeffMyDouble != null)
						coeffMyDouble.set(-coeff); // change sign
					poly = new ExpressionNode(kernel, 
											poly, 
											ExpressionNode.MINUS, 
											partExp);
				} else {
					poly = new ExpressionNode(kernel, 
											poly, 
											ExpressionNode.PLUS, 
											partExp);
				}					
			}		
  	    }     
  	    
  	    // all coefficients were 0, we've got f(x) = 0
  	    if (poly == null) {
  	    	poly = new ExpressionNode(kernel, new MyDouble(kernel, 0));
  	    }  	       
    	//  polynomial Function
		Function polyFun = new Function(poly, fVar);	
    	return polyFun;
    	
    	
    }
    
    final public String toString() {
    	return getCommandDescription();
    }
    private void polcoe(double x[], double y[], int n, double cof[])
//  Given arrays x[0..n-1] and y[0..n-1] containing a tabulated function yi = f(xi), this routine
//  returns an array of coefficients cof[0..n], such that yi = Sigma cofj.xj
// adapted from Numerical Recipes chap 3.5
    {
    int k,j,i;
    double phi,ff,b;
    double s[] = new double[n];
    for (i=0;i<n;i++) s[i]=cof[i]=0.0;
    s[n-1] = -x[0];
    for (i=1 ; i<n ; i++) { 
	    for (j=n-1-i ; j<n-1 ; j++) s[j] -= x[i]*s[j+1];
      s[n-1] -= x[i];
    }
    for (j=0 ; j<n ; j++) {
      phi=n;
      for (k=n-1 ; k>0 ; k--) 
        phi=k*s[k]+x[j]*phi; 
      ff=y[j]/phi;
      b=1.0; 
      for (k=n-1 ; k>=0 ; k--) {
        cof[k] += b*ff;
        b=s[k]+x[j]*b;
      }
    }
  
  }
    
    private void polcoeBig(double xx[], double yy[], int n, double coff[])
//  Given arrays x[0..n-1] and y[0..n-1] containing a tabulated function yi
// = f(xi), this routine
//  returns an array of coefficients cof[0..n], such that yi = Sigma cofj.xj
// adapted from Numerical Recipes chap 3.5
    {
        BigDecimal x[] = new BigDecimal[n];    	
        BigDecimal y[] = new BigDecimal[n];    	
        BigDecimal cof[] = new BigDecimal[n];    	
        BigDecimal s[] = new BigDecimal[n];    	
        int k,j,i;
        BigDecimal minusone = new BigDecimal(-1.0);
        BigDecimal phi,ff,b;
        for (i=0;i<n;i++)
        {
        	x[i]=new BigDecimal(xx[i]);
        	y[i]=new BigDecimal(yy[i]);
        }
        //double s[] = new double[n];
    for (i=0;i<n;i++) s[i]=cof[i]=new BigDecimal(0.0);
    s[n-1] = x[0].multiply(minusone);
    for (i=1 ; i<n ; i++) { 
	    for (j=n-1-i ; j<n-1 ; j++) s[j] = s[j].subtract(x[i].multiply(s[j+1]));
      s[n-1] = s[n-1].subtract(x[i]);
    }
    for (j=0 ; j<n ; j++) {
      phi=new BigDecimal(n);
      for (k=n-1 ; k>0 ; k--) 
      {
        BigDecimal kk = new BigDecimal(k);
    	  phi=(kk.multiply(s[k])).add(x[j].multiply(phi)); 
      }
      ff=y[j].divide(phi,BigDecimal.ROUND_HALF_UP);
      b= new BigDecimal(1.0); 
      for (k=n-1 ; k>=0 ; k--) {
        cof[k] = cof[k].add(b.multiply(ff));
        b=s[k].add(x[j].multiply(b));
      }
    }
    
    for (i=0;i<n;i++) coff[i]=cof[i].doubleValue();
  
  }

}
