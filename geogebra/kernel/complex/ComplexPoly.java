/*
*   Class   ComplexPoly
*
*   Defines a complex polynomial
*   y = a[0] + a[1].x + a[2].x^2 + a[3].3 + . . . + a[n].x^n
*   where x and all a[i] may be real or complex
*   and deg is the degree of the polynomial, i.e. n,
*   and includes the methods associated with polynomials,
*   e.g. complex root searches
*
*   WRITTEN BY: Michael Thomas Flanagan
* 	 changed by Markus Hohenwarter for GeoGebra (12. August 2004)
*
*   See class Complex for standard complex arithmetic
*
*   DATE:    February 2002
*   UPDATED: 22 June 2003
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's JAVA library on-line web page:
*   ComplexPoly.html
*
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

package geogebra.kernel.complex;

public class ComplexPoly{

        // DATA MEMBERS
        private int deg = 0;            // Degree of the polynomial
        private int degwz = 0;          // Degree of the polynomial with zero roots removed
        private Complex[] coeff;        // Array of polynomial coefficients
        private Complex[] coeffwz;      // Array of polynomial coefficients with zero roots removed



        // Coefficients are real
        public ComplexPoly(double[] aa){
                deg =aa.length-1;
                coeff = Complex.oneDarray(deg+1);
                for(int i=0; i<=deg; i++){
                        coeff[i].set(aa[i], 0.0);
                }
        }

        // Single constant -  complex
        // y = aa
        // needed in class Loop
        public ComplexPoly(Complex aa){
                deg = 0;
                coeff = Complex.oneDarray(1);
                coeff[0]=Complex.copy(aa);
        }

        // Single constant -  double
        // y = aa
        // needed in class Loop
        public ComplexPoly(double aa){
                deg = 0;
                coeff = Complex.oneDarray(1);
                coeff[0].set(aa, 0.0);
        }

        // Straight line - coefficients are complex
        // y = aa + bb.x
        public ComplexPoly(Complex aa, Complex bb){
                deg = 1;
                coeff = Complex.oneDarray(2);
                coeff[0]=Complex.copy(aa);
                coeff[1]=Complex.copy(bb);
        }

        // Straight line - coefficients are real
        // y = aa + bb.x
        public ComplexPoly(double aa, double bb){
                deg = 1;
                coeff = Complex.oneDarray(2);
                coeff[0].set(aa, 0.0);
                coeff[1].set(bb, 0.0);
        }

        // Quadratic - coefficients are complex
        // y = aa + bb.x + cc.x^2
        public ComplexPoly(Complex aa, Complex bb, Complex cc){
                deg = 2;
                coeff = Complex.oneDarray(3);
                coeff[0]=Complex.copy(aa);
                coeff[1]=Complex.copy(bb);
                coeff[2]=Complex.copy(cc);
        }

        // Quadratic - coefficients are real
        // y = aa + bb.x + cc.x^2
        public ComplexPoly(double aa, double bb, double cc){
                deg = 2;
                coeff = Complex.oneDarray(3);
                coeff[0].set(aa, 0.0);
                coeff[1].set(bb, 0.0);
                coeff[2].set(cc, 0.0);
        }

        // Cubic - coefficients are complex
        // y = aa + bb.x + cc.x^2 + dd.x^3
        public ComplexPoly(Complex aa, Complex bb, Complex cc, Complex dd){
                deg = 3;
                coeff = Complex.oneDarray(4);
                coeff[0]=Complex.copy(aa);
                coeff[1]=Complex.copy(bb);
                coeff[2]=Complex.copy(cc);
                coeff[3]=Complex.copy(dd);
        }

        // Cubic - coefficients are real
        // y = aa + bb.x + cc.x^2 + dd.x^3
        public ComplexPoly(double aa, double bb, double cc, double dd){
                deg = 3;
                coeff = Complex.oneDarray(4);
                coeff[0].set(aa, 0.0);
                coeff[1].set(bb, 0.0);
                coeff[2].set(cc, 0.0);
                coeff[3].set(dd, 0.0);
        }

        // METHODS

        // Return a copy of a coefficient
        public Complex coeffCopy(int i){
                return Complex.copy(coeff[i]);
        }

        // Return the degree
        public int getDeg(){
                return deg;
        }
      
        // Convert to a String of the form a + jb, c + jd, etc.
        public String toString(){
                String ss = "";
                for(int i=0; i<=deg; i++){
                        ss =  ss + coeffCopy(i).toString();
                        if(i<deg)ss = ss + ",  ";
                }
                return ss;
        }

        // ROOTS OF POLYNOMIALS
        // For general details of root searching and a discussion of the rounding errors
        // see Numerical Recipes, The Art of Scientific Computing
        // by W H Press, S A Teukolsky, W T Vetterling & B P Flannery
        // Cambridge University Press,   http://www.nr.com/

        // Calculate the roots (real or complex) of a polynomial (real or complex)
        // polish = true ([for deg>3 see laguerreAll(...)]
        // initial root estimates are all zero [for deg>3 see laguerreAll(...)]
        public Complex[] roots(){
                boolean polish=true;
                Complex estx = new Complex(0.0, 0.0);
                return roots(polish, estx);
        }

        // Calculate the roots (real or complex) of a polynomial (real or complex)
        // initial root estimates are all zero [for deg>3 see laguerreAll(...)]
        // for polish  see laguerreAll(...)[for deg>3]
        public Complex[] roots(boolean polish){
                Complex estx = new Complex(0.0, 0.0);
                return roots(polish, estx);
        }

        // Calculate the roots (real or complex) of a polynomial (real or complex)
        // for estx  see laguerreAll(...)[for deg>3]
        // polish = true  see laguerreAll(...)[for deg>3]
        public Complex[] roots(Complex estx){
                boolean polish=true;
                return roots(polish, estx);
        }

        // Calculate the roots (real or complex) of a polynomial (real or complex)
        public Complex[] roots(boolean polish, Complex estx){
                if(deg==0)
					// Application.debug("degree of the polynomial is zero in the method ComplexPoly.roots");
                   // Application.debug("null returned");
                    return null;

                // check for zero roots
                boolean testzero=true;
                int ii=0, nzeros=0;
                while(testzero){
                    if(coeff[ii].isZero()){
                        nzeros++;
                        ii++;
                    }
                    else{
                        testzero=false;
                    }
                }
                if(nzeros>0){
                    degwz = deg - nzeros;
                    coeffwz = Complex.oneDarray(degwz+1);
                    for(int i=0; i<=degwz; i++)
                    	coeffwz[i].set(coeff[i+nzeros]);
                }
                else{
                    degwz = deg;
                    coeffwz = Complex.oneDarray(degwz+1);
                    for(int i=0; i<=degwz; i++)coeffwz[i].set(coeff[i]);
                }

                // calculate non-zero roots
                Complex[] roots = Complex.oneDarray(deg);
                
                // don't use any special cases for degrees 1, 2 or 3 (this is done by GeoGebra)
                Complex[] root =laguerreAll(polish, estx);

                for(int i=0; i<degwz; i++){
                       Complex.conjugate(root[i], roots[i]);
                }
                if(nzeros>0){
                    for(int i=degwz; i<deg; i++){
                        roots[i].set(0,0);
                    }
                }

                return roots;
        }

      

        // LAGUERRE'S METHOD FOR COMPLEX ROOTS OF A COMPLEX POLYNOMIAL
        
        	// maximum iteration = 8 * MAX_STEPS
			public int  MAX_STEPS = 100;      // number of steps in breaking a limit cycle
              
				private Complex b   = new Complex();
				private Complex d   = new Complex();
				private  Complex f   = new Complex();
				private  Complex g   = new Complex();
				private  Complex g2  = new Complex();
				private  Complex h   = new Complex();
				private  Complex sq  = new Complex();
				private  Complex gp  = new Complex();
				private  Complex gm  = new Complex();
				private  Complex dx  = new Complex();
				private  Complex x1  = new Complex();
				private  Complex temp1  = new Complex();
				private  Complex temp2  = new Complex();
				
				// fractions used to break a limit cycle
				 private double  frac[]={0.5, 0.25, 0.75, 0.13, 0.38, 0.62, 0.88, 1.0};

        // Laguerre method for one of the roots
        // Following the procedure in Numerical Recipes for C [Reference above]
        // estx     estimate of the root
        // coeff[]  coefficients of the polynomial
        // m        degree of the polynomial
        private Complex laguerre(Complex estx, Complex[] pcoeff, int m){
                double  eps = 1e-7;     // estimated fractional round-off error
                int     mr = 8;         // number of fractional values in Adam's method of breaking a limit cycle      
                int     maxit = mr*MAX_STEPS;  // maximum number of iterations allowed
                Complex root = new Complex();    // root

                double  abp = 0.0D, abm = 0.0D;
                double  err = 0.0D, abx = 0.0D;

                for(int i=1; i<=maxit; i++){
                        b.set(pcoeff[m]);
                        err=Complex.abs(b);
                        d.set(0,0);
                        f.set(0,0);
                        abx=Complex.abs(estx);
                        for(int j=m-1; j>=0;j--)
                        {
                                // Efficient computation of the polynomial and its first two derivatives
                                f=Complex.plus(Complex.times(estx, f, f),  d, f);
                                d=Complex.plus(Complex.times(estx, d, d),  b, d);
                                b=Complex.plus(Complex.times(estx, b, b),  pcoeff[j], b);
                                err=Complex.abs(b)+abx*err;
                        }
                        err*=eps;

                        // Estimate of round-off error in evaluating polynomial
                        if(Complex.abs(b)<=err)
                        {
                                root.set(estx);
                                return root;
                        }
                        // Laguerre formula
                        g=Complex.over(d, b, g);
                        g2=Complex.square(g, g2);                       
                        temp1 = Complex.times(Complex.over(f, b, temp1), 2.0, temp1);
                        h=Complex.minus(g2, temp1, h);
                        sq=Complex.sqrt(
                        		Complex.times(
                        			Complex.times(
                        				Complex.times(h, m, sq), 
                        				g2, sq
                        			), m-1, sq
                        		), sq);                     		
                        gp=Complex.plus(g, sq, gp);
                        gm=Complex.minus(g, sq, gm);
                        abp=Complex.abs(gp);
                        abm=Complex.abs(gm);
                        if( abp < abm ) gp = gm;
                        temp1.set(m, 0);
                        temp2.set(Math.cos(i), Math.sin(i));
                        dx= Math.max(abp, abm) > 0.0 ? 
                        	Complex.over(temp1, gp, dx) :
                        	Complex.times(temp2, Math.exp(1.0+abx), dx);
                        x1=Complex.minus(estx, dx, x1);                        
                        if(Complex.isEqual(estx, x1))
                        {
                                root.set(estx);
                                return root;     // converged
                        }
                        if ((i % MAX_STEPS) == 0){
                                estx.set(x1);
                        }
                        else{
                                // Every so often we take a fractional step to break any limit cycle
                                // (rare occurence)
                                estx=Complex.minus(estx, Complex.times(dx, frac[i/MAX_STEPS], temp1), estx);
                        }
                }
                // exceeded maximum allowed iterations
                root.set(estx);
               
               // Application.debug("Maximum number of iterations exceeded in laguerre: " + niter);
               // Application.debug("root returned at this point");
               return root;
        }

        // Finds all roots of a complex polynomial by successive calls to laguerre
        // Following the procedure in Numerical Recipes for C [Reference above]
        // Initial estimates are all zero, polish=true
        public Complex[] laguerreAll(){
                Complex estx = new Complex(0.0, 0.0);
                boolean polish = true;
                return laguerreAll(polish, estx);
        }

        //  Initial estimates estx, polish=true
        public Complex[] laguerreAll(Complex estx){
                boolean polish = true;
                return laguerreAll(polish, estx);
        }

        //  Initial estimates are all zero.
        public Complex[] laguerreAll(boolean polish){
                Complex estx = new Complex(0.0, 0.0);
                return laguerreAll(polish, estx);
        }

        // Finds all roots of a complex polynomial by successive calls to laguerre
        //  Initial estimates are estx
        public Complex[] laguerreAll(boolean polish, Complex estx){
                // polish boolean variable
                // if true roots polished also by Laguerre
                // if false roots returned to be polished by another method elsewhere.
                // estx estimate of root - Preferred default value is zero to favour convergence
                //   to smallest remaining root

                int     m = degwz;
                double  eps = 2.0e-6;  // tolerance in determining round off in imaginary part

                Complex x = new Complex();
                Complex b = new Complex();
                Complex c = new Complex();
                Complex[] ad = Complex.oneDarray(m+1);
                Complex[] roots = Complex.oneDarray(m+1);

                // Copy polynomial for successive deflation
                for(int j=0; j<=m; j++) ad[j].set(coeffwz[j]);

                // Loop over each root found
                for(int j=m; j>=1; j--){
                        x.set(estx);   // Preferred default value is zero to favour convergence to smallest remaining root
                                                // and find the root
                        x=laguerre(x, ad, j);
                        if(Math.abs(x.getImag())<=2.0*eps*Math.abs(x.getReal())) x.setImag(0.0);
                        roots[j].set(x);
                        b.set(ad[j]);
                        for(int jj=j-1; jj>=0; jj--){
                                c.set(ad[jj]);
                                ad[jj].set(b);
                                b=Complex.plus(Complex.times(x, b, b), c, b);
                        }
                }

                if(polish){
                        // polish roots using the undeflated coefficients
                        for(int j=1; j<=m; j++){
                                roots[j]=laguerre(roots[j], coeffwz, m);
                       }
                }

////                // Sort roots by their real parts by straight insertion
//                for(int j=2; j<=m; j++){
//                        x.set(roots[j]);
//                        int i=0;
//                        for(i=j-1; i>=1; i--){
//                                if(roots[i].getReal() <= x.getReal()) break;
//                                roots[i+1].set(roots[i]);
//                        }
//                        roots[i+1].set(x);
//                }

                // shift roots to zero initial index
                for(int i=0; i<m; i++)
                	roots[i].set(roots[i+1]);
                return roots;
        }
}

