/*
*   Class RealRoot
*
*   Contains methods for finding a real root
*
*   The function whose root is to be determined is supplied
*   by means of an interface, RealRootFunction,
*   if no derivative required
*
*   The function whose root is to be determined is supplied
*   by means of an interface, RealRootDerivFunction,
*   as is the first derivative if a derivative is required
*
*   WRITTEN BY: Michael Thomas Flanagan
*
*   DATE:   18 May 2003
*   UPDATE: 22 June 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's JAVA library on-line web page:
*   html
*
*   Copyright (c) June 2003    Michael Thomas Flanagan
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

package geogebra.kernel.roots;

// RealRoot class
public class RealRoot{

    private double tol = 1e-8;           // tolerance in determining convergence upon a root
    private double tolClass = 1e-8;      // class default value for tol
    private int iterMax = 300;           // maximum number of iterations allowed in root search
    private int iterN = 0;               // number of iterations taken in root search

    // Reset the default tolerance
    public void setTolerance(double tolerance){
        tolClass=tolerance;
    }

    // Get the default tolerance
    public double getTolerance(){
        return tolClass;
    }

    // Reset the maximum iterations allowed
    public void setIterMax(int imax){
        iterMax=imax;
    }

    // Get the maximum iterations allowed
    public int getIterMax(){
        return iterMax;
    }

    // Get the number of iterations taken
    public int getIterN(){
        return iterN;
    }

    // Combined bisection and Newton Raphson method
    // required accuracy supplied
   	public double brent(RealRootFunction g, double lower, double upper, double acc){
        tol=acc;
        double root = brent(g, lower, upper);
        tol=tolClass;
        return root;
    }

    // Combined bisection and Newton Raphson method
    // default accuracy used
   	final public double brent(RealRootFunction g, double lower, double upper){
	    // check upper>lower
	    if(upper==lower)throw new IllegalArgumentException("upper cannot equal lower");

	    double root = -1.235e-200;  // variable to hold the returned root
        boolean testConv = true;    // convergence test: becomes false on convergence
        iterN = 0;
        double temp = 0.0D;

        if(upper<lower){
 	        temp = upper;
	        upper = lower;
	        lower = temp;
	    }

	    // calculate the function value at the estimate of the higher bound to x
	    double fu = g.evaluate(upper);
	    // calculate the function value at the estimate of the lower bound of x
	    double fl = g.evaluate(lower);
	    if(Double.isNaN(fl))throw new IllegalArgumentException("lower bound returned NaN as the function value");
	    if(Double.isNaN(fu))throw new IllegalArgumentException("upper bound returned NaN as the function value");

        // check that the root has been bounded
        if(fu*fl>0.0D)throw new IllegalArgumentException("root not bounded");

	    // check initial values for true root value
	    if(fl==0.0D){
	        root=lower;
	        testConv = false;
	    }
	    if(fu==0.0D){
	        root=upper;
	        testConv = false;
	    }

	    // Function at mid-point of initial estimates
        double mid=(lower+upper)/2.0D;   // mid point (bisect) or new x estimate (Newton-Raphson)
        double lastMidB = mid;           // last succesful mid point
        double fm = g.evaluate(mid);
        double diff = mid-lower; // difference between successive estimates of the root
        double fmB = fm;        // last succesful mid value function value
        double lastMid=mid;
        boolean lastMethod = true; // true; last method = Newton Raphson, false; last method = bisection method
        boolean nextMethod = true; // true; next method = Newton Raphson, false; next method = bisection method

	    // search
	    double rr=0.0D, ss=0.0D, tt=0.0D, pp=0.0D, qq=0.0D; // interpolation variables
	    while(testConv){
	        // test for convergence
	        if(fm==0.0D || Math.abs(diff)<tol){
	            testConv=false;
	            if(fm==0.0D){
	                root=lastMid;
	            }
	            else{
	                if(Math.abs(diff)<tol)root=mid;
	            }
	        }
	        else{
	            lastMethod=nextMethod;
	            // test for succesfull inverse quadratic interpolation
	            if(lastMethod){
	                if(mid<lower || mid>upper){
	                    // inverse quadratic interpolation failed
	                    nextMethod=false;
	                }
	                else{
	                    fmB=fm;
	                    lastMidB=mid;
	                }
	            }
	            else{
	                nextMethod=true;
	            }
		        if(nextMethod){
		            // inverse quadratic interpolation
		            fl=g.evaluate(lower);
	                fm=g.evaluate(mid);
	                fu=g.evaluate(upper);
	                rr=fm/fu;
	                ss=fm/fl;
	                tt=fl/fu;
	                pp=ss*(tt*(rr-tt)*(upper-mid)-(1.0D-rr)*(mid-lower));
	                qq=(tt-1.0D)*(rr-1.0D)*(ss-1.0D);
	                lastMid=mid;
	                diff=pp/qq;
	                mid=mid+diff;
	            }
	            else{
	                // Bisection procedure
	                fm=fmB;
	                mid=lastMidB;
	                if(fm*fl>0.0D){
	                    lower=mid;
	                    fl=fm;
	                }
	                else{
	                    upper=mid;
	                    fu=fm;
	                }
	                lastMid=mid;
	                mid=(lower+upper)/2.0D;
	                fm=g.evaluate(mid);
	                diff=mid-lastMid;
	                fmB=fm;
	                lastMidB=mid;
	            }
	        }
            iterN++;
            if(iterN>iterMax){
                //Application.debug("brent: maximum number of iterations exceeded - root at this point returned");
                //Application.debug("Last mid-point difference = "+diff+", tolerance = " + tol);
                root = mid;
                testConv = false;
            }
        }
        return root;
    }

/* 	
    // bisection method
    // accuracy supplied
	final public double bisect(RealRootFunction g, double lower, double upper, double acc){
	    tol=acc;
	    double root = bisect(g, lower, upper);
        tol=tolClass;
	    return root;
	}   
   	
    // bisection method
    // default accuracy used
	final public double bisect(RealRootFunction g, double lower, double upper){
	    // check upper>lower
	    if(upper < lower){
            double temp = upper;
	        upper = lower;
	        lower = temp;
	    }
	    
	    
	    
	    
	    if(upper == lower)throw 
	     	new IllegalArgumentException("upper cannot equal lower");
		  
	    double root = -1.235e-200;  // variable to hold the returned root
        boolean testConv = true;    // convergence test: becomes false on convergence
        iterN = 0;         // number of iterations
        double diff = 1e250;        // abs(difference between the last two successive mid-pint x values)
               
	    // calculate the function value at the estimate of the higher bound to x
	    double fu = g.evaluate(upper);
	    // calculate the function value at the estimate of the lower bound of x
	    double fl = g.evaluate(lower);	    
	    if (Double.isNaN(fl)) throw new IllegalArgumentException("lower bound returned NaN as the function value"); 
	    if (Double.isNaN(fu))throw new IllegalArgumentException("upper bound returned NaN as the function value");	    		    

        // check that the root has been bounded
        if(fu*fl>0.0D)throw new IllegalArgumentException("root not bounded");

	    // check initial values for true root value
	    if(fl==0.0D){
	        root=lower;
	        testConv = false;
	    }
	    if(fu==0.0D){
	        root=upper;
	        testConv = false;
	    }

	    // start search
        double mid = (lower+upper)/2.0D;    // mid-point
        double lastMid = 1e300;             // previous mid-point
        double fm = g.evaluate(mid);
        while(testConv){
            if(fm==0.0D || diff<tol){
                testConv=false;
                root=mid;
            }
            if(fm*fl>0.0D){
                lower = mid;
                fl=fm;
            }
            else{
                upper = mid;
                fu=fm;
            }
            lastMid = mid;
            mid = (lower+upper)/2.0D;
            fm = g.evaluate(mid);
            diff = Math.abs(mid-lastMid);
            iterN++;
            if(iterN>iterMax){
                //Application.debug("bisect: maximum number of iterations exceeded - root at this point returned");
                //Application.debug("Last mid-point difference = "+diff+", tolerance = " + tol);
                root = mid;
                testConv = false;
            }
        }
        return root;
    }*/

    // false position  method
    // accuracy suppled
	final public double falsePosition(RealRootFunction g, double lower, double upper, double acc){
        tol=acc;
	    double root = falsePosition(g, lower, upper);
        tol=tolClass;
	    return root;
	}

    // false position  method
    // default accuracy used
	final public double falsePosition(RealRootFunction g, double lower, double upper){
	    // check upper>lower
	    if(upper<lower){
 	        double temp = upper;
	        upper = lower;
	        lower = temp;
	    } else 
		  if(upper==lower)throw new IllegalArgumentException("upper cannot equal lower");
	    

	    double root = -1.235e-200;  // variable to hold the returned root
        boolean testConv = true;    // convergence test: becomes false on convergence
        iterN = 0;         // number of iterations
        double diff = 1e250;        // abs(difference between the last two successive mid-pint x values)

	    // calculate the function value at the estimate of the higher bound to x
	    double fu = g.evaluate(upper);
	    // calculate the function value at the estimate of the lower bound of x
	    double fl = g.evaluate(lower);
	    if(Double.isNaN(fl))throw new IllegalArgumentException("lower bound returned NaN as the function value");
	    if(Double.isNaN(fu))throw new IllegalArgumentException("upper bound returned NaN as the function value");

        // check that the root has been bounded
        if(fu*fl>0.0D)throw new IllegalArgumentException("root not bounded");

	    // check initial values for true root value
	    if(fl==0.0D){
	        root=lower;
	        testConv = false;
	    }
	    if(fu==0.0D){
	        root=upper;
	        testConv = false;
	    }

	    // start search
        double mid = lower+(upper-lower)*Math.abs(fl)/(Math.abs(fl)+Math.abs(fu));    // mid-point
        double lastMid = 1e300;             // previous mid-point
        double fm = g.evaluate(mid);
        while(testConv){
            if(fm==0.0D || diff<tol){
                testConv=false;
                root=mid;
            }
            if(fm*fl>0.0D){
                lower = mid;
                fl=fm;
            }
            else{
                upper = mid;
                fu=fm;
            }
            lastMid = mid;
            mid = lower+(upper-lower)*Math.abs(fl)/(Math.abs(fl)+Math.abs(fu));    // mid-point
            fm = g.evaluate(mid);
            diff = Math.abs(mid-lastMid);
            iterN++;
            if(iterN>iterMax){
                //Application.debug("falsePosition: maximum number of iterations exceeded - root at this point returned");
                //Application.debug("Last mid-point difference = "+diff+", tolerance = " + tol);
                root = mid;
                testConv = false;
            }
        }
        return root;
    }


    // Combined bisection and Newton Raphson method
    // accuracy supplied
   	final public double bisectNewtonRaphson(RealRootDerivFunction g, double lower, double upper, double acc){
        tol=acc;
   	    double root = bisectNewtonRaphson(g, lower, upper);
   	    tol=tolClass;
   	    return root;
   	}

    // Combined bisection and Newton Raphson method
    // default accuracy used
    final public double bisectNewtonRaphson(RealRootDerivFunction g, double lower, double upper){
	
	    double root = -1.235e-200;  // variable to hold the returned root
        boolean testConv = true;    // convergence test: becomes false on convergence
        iterN = 0;         // number of iterations
        double temp = 0.0D;

        if(upper<lower){
 	        temp = upper;
	        upper = lower;
	        lower = temp;
	    }              
        
        // check upper>lower
	    if(upper==lower)throw new IllegalArgumentException("upper cannot equal lower");
        
	    // calculate the function value at the estimate of the higher bound to x
	    double[] f = g.evaluateDerivFunc(upper);
	    double fu=f[0];
	    // calculate the function value at the estimate of the lower bound of x
	    f = g.evaluateDerivFunc(lower);
	    double fl=f[0];
	    if(Double.isNaN(fl))throw new IllegalArgumentException("lower bound returned NaN as the function value");
	    if(Double.isNaN(fu))throw new IllegalArgumentException("upper bound returned NaN as the function value");

        // check that the root has been bounded
        if(fu*fl>0.0D)throw new IllegalArgumentException("root not bounded");

	    // check initial values for true root value
	    if(fl==0.0D){
	        root=lower;
	        testConv = false;
	    }
	    if(fu==0.0D){
	        root=upper;
	        testConv = false;
	    }

	    // Function at mid-point of initial estimates
        double mid=(lower+upper)/2.0D;   // mid point (bisect) or new x estimate (Newton-Raphson)
        double lastMidB = mid;           // last succesful mid point
        f = g.evaluateDerivFunc(mid);
        double diff = f[0]/f[1]; // difference between successive estimates of the root
        double fm = f[0];
        double fmB = fm;        // last succesful mid value function value
        double lastMid=mid;
        mid = mid-diff;
        boolean lastMethod = true; // true; last method = Newton Raphson, false; last method = bisection method
        boolean nextMethod = true; // true; next method = Newton Raphson, false; next method = bisection method

	    // search
	    while(testConv){
	        // test for convergence
	        if(fm==0.0D || Math.abs(diff)<tol){
	            testConv=false;
	            if(fm==0.0D){
	                root=lastMid;
	            }
	            else{
	                if(Math.abs(diff)<tol)root=mid;
	            }
	        }
	        else{
	            lastMethod=nextMethod;
	            // test for succesfull Newton-Raphson
	            if(lastMethod){
	                if(mid<lower || mid>upper){
	                    // Newton Raphson failed
	                    nextMethod=false;
	                }
	                else{
	                    fmB=fm;
	                    lastMidB=mid;
	                }
	            }
	            else{
	                nextMethod=true;
	            }
		        if(nextMethod){
		            // Newton-Raphson procedure
	                f=g.evaluateDerivFunc(mid);
	                fm=f[0];
	                diff=f[0]/f[1];
	                lastMid=mid;
	                mid=mid-diff;
	            }
	            else{
	                // Bisection procedure
	                fm=fmB;
	                mid=lastMidB;
	                if(fm*fl>0.0D){
	                    lower=mid;
	                    fl=fm;
	                }
	                else{
	                    upper=mid;
	                    fu=fm;
	                }
	                lastMid=mid;
	                mid=(lower+upper)/2.0D;
	                f=g.evaluateDerivFunc(mid);
	                fm=f[0];
	                diff=mid-lastMid;
	                fmB=fm;
	                lastMidB=mid;
	            }
	        }
            iterN++;
            if(iterN>iterMax){
                //Application.debug("bisectNetonRaphson: maximum number of iterations exceeded - root at this point returned");
                //Application.debug("Last mid-point difference = "+diff+", tolerance = " + tol);
                root = mid;
                testConv = false;
            }
        }
        return root;
    }

    // Newton Raphson method
    // accuracy supplied
	final public double newtonRaphson(RealRootDerivFunction g, double x, double acc){
        tol=acc;
	    double root = newtonRaphson(g, x);
   	    tol=tolClass;
   	    return root;
	}

    // Newton Raphson method
    // default accuracy used
	final public double newtonRaphson(RealRootDerivFunction g, double x){
	    double root = -1.235e-200;  // variable to hold the returned root
        boolean testConv = true;    // convergence test: becomes false on convergence
        iterN = 0;         // number of iterations
        double diff = 1e250;        // difference between the last two successive mid-pint x values

	    // calculate the function and derivative value at the initial estimate  x
	    double[] f = g.evaluateDerivFunc(x);
	    if(Double.isNaN(f[0]))throw new IllegalArgumentException("NaN returned as the function value");
	    if(Double.isNaN(f[1]))throw new IllegalArgumentException("NaN returned as the derivative function value");


	    // search
        while(testConv){
            diff = f[0]/f[1];
            if(f[0]==0.0D || Math.abs(diff)<tol){
                root = x;
                testConv=false;
            }
            else{
                x -= diff;
                f = g.evaluateDerivFunc(x);
                if(Double.isNaN(f[0]))throw new IllegalArgumentException("NaN returned as the function value");
	            if(Double.isNaN(f[1]))throw new IllegalArgumentException("NaN returned as the derivative function value");
            }
            iterN++;
            if(iterN>iterMax){
                //Application.debug("newtonRaphson: maximum number of iterations exceeded - root at this point returned");
                //Application.debug("Last mid-point difference = "+diff+", tolerance = " + tol);
                root = x;
                testConv = false;
            }
        }
        return root;
    }

}
