/*
 *   changed by: Markus Hohenwarter (10. August 2004)
 * base on the following 
 *
*   Class Integration
*       interface RealRootFunction also required
*
*   Contains the methods for Gaussian-Legendre quadrature
*
*
*   WRITTEN BY: Michael Thomas Flanagan
*
*   DATE:	 February 2002
*   UPDATE:  22 June 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's JAVA library on-line web page:
*   Integration.html
*
*   Copyright (c) April 2004
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package geogebra.kernel.integration;

import geogebra.kernel.roots.RealRootFunction;

// Numerical integration class
public class GaussQuadIntegration{

		private int nPoints;
		private double[] gaussQuadDist;
		private double[] gaussQuadWeight;
		
        public GaussQuadIntegration(int points){
			nPoints = points;
			gaussQuadDist = new double[nPoints];
			gaussQuadWeight = new double[nPoints];
			
			// Calculate coefficients
			gaussQuadCoeff(gaussQuadDist, gaussQuadWeight, nPoints);
        }

    	// Numerical integration using n point Gaussian-Legendre quadrature
    	final public double integrate(RealRootFunction g, double lowerLimit, double upperLimit){ 
        	double sum=0.0D;
        	double xplus = 0.5D*(upperLimit + lowerLimit);
        	double xminus = 0.5D*(upperLimit - lowerLimit);
        	double dx = 0.0D;

        	// Perform summation
        	for(int i=0; i<nPoints; i++){
            		dx = xminus*gaussQuadDist[i];
            		sum += gaussQuadWeight[i]*g.evaluate(xplus+dx);
        	}
        	return sum*xminus;   // rescale and return
    	}

    	// Returns the distance (guassQuadDist) and weight coefficients (gaussQuadCoeff)
    	// for an n point Gauss-Legendre Quadrature.
    	// The Gauss-Legendre distances, gaussQuadDist, are scaled to -1 to 1
    	// See Numerical Recipes for details
    	final public void gaussQuadCoeff(double[] guassQuadDist, double[] guassQuadWeight, int n){

	    	double	z=0.0D, z1=0.0D;
		    double  pp=0.0D, p1=0.0D, p2=0.0D, p3=0.0D;

	    	double 	eps = 3e-11;	// set required precision
	    	double	x1 = -1.0D;		// lower limit
	    	double	x2 = 1.0D;		// upper limit

	    	//  Calculate roots
	    	// Roots are symmetrical - only half calculated
	    	int m  = (n+1)/2;
	    	double	xm = 0.5D*(x2+x1);
	    	double	xl = 0.5D*(x2-x1);

	    	// Loop for  each root
	    	for(int i=1; i<=m; i++){
			// Approximation of ith root
		    	z = Math.cos(Math.PI*(i-0.25D)/(n+0.5D));

		    	// Refinement on above using Newton's method
		    	do{
			    	p1 = 1.0D;
			    	p2 = 0.0D;

			    	// Legendre polynomial (p1, evaluated at z, p2 is polynomial of
			    	//  one order lower) recurrence relationsip
			    	for(int j=1; j<=n; j++){
				    	p3 = p2;
				    	p2 = p1;
				    	p1= ((2.0D*j - 1.0D)*z*p2 - (j - 1.0D)*p3)/j;
			    	}
			    	pp = n*(z*p1 - p2)/(z*z - 1.0D);    // Derivative of p1
			    	z1 = z;
			    	z = z1 - p1/pp;			            // Newton's method
		    	} while(Math.abs(z - z1) > eps);

		    	guassQuadDist[i-1] = xm - xl*z;		    // Scale root to desired interval
		    	guassQuadDist[n-i] = xm + xl*z;		    // Symmetric counterpart
		    	guassQuadWeight[i-1] = 2.0*xl/((1.0 - z*z)*pp*pp);	// Compute weight
		    	guassQuadWeight[n-i] = guassQuadWeight[i-1];		// Symmetric counterpart
	    	}
    	}
}
