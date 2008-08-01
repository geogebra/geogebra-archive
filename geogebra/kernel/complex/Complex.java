/*
*   Class   Complex
*
*   Defines a complex number as an object and includes
*   the methods needed for standard complex arithmetic
*
*   See class ComplexMatrix for complex matrix manipulations
*   See class ComplexPoly for complex polynomial manipulations
*   See class ComplexErrorProp for the error propogation in complex arithmetic
*
*   WRITTEN BY: Michael Thomas Flanagan
*
*   DATE:    February 2002
*   UPDATED: 13 April 2004
*
*   DOCUMENTATION:
*   See Michael T Flanagan's JAVA library on-line web page:
*   Complex.html
*
*   Copyright (c) April 2004   Michael Thomas Flanagan
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


final public class Complex{

        // DATA VARIABLES
        private double real = 0.0D;         // Real part of a complex number
        private double imag = 0.0D;         // Imaginary part of a complex number

	// SOME USEFUL NUMBERS
	public static final Complex plusJay = new Complex(0, 1);
	public static final Complex minusJay = new Complex(0, -1);
	// Added for Intergeo File Format (Yves Kreis) -->
	public static final Complex NaN = new Complex(Double.NaN, Double.NaN);
	// <-- Added for Intergeo File Format (Yves Kreis)

/*********************************************************/

        // CONSTRUCTORS
        // default constructor - real and imag = zero
        public Complex()
        {
                real = 0.0D;
                imag = 0.0D;
        }

        // constructor - initialises both real and imag
        public Complex(double real, double imag)
        {
                this.real = real;
                this.imag = imag;
        }

        // constructor - initialises  real, imag = 0.0
        public Complex(double real)
        {
                this.real = real;
                imag = 0.0D;
        }

        // constructor - initialises both real and imag to the values of an existing Complex
        public Complex(Complex c)
        {
                real = c.real;
                imag = c.imag;
        }

/*********************************************************/

        // PUBLIC METHODS

        // SET VALUES
        // Set the value of real
        final public void setReal(double real){
        	this.real = real;
        }
        
        // Set the value of imag
		final public void setImag(double imag){
             this.imag = imag;
        }

        // Set the values of real and imag
        final public void set(double real, double imag){
                this.real = real;
                this.imag = imag;
        }
        
	final public void set(Complex c){
			real = c.real;
			imag = c.imag;
	}

        // GET VALUES
        // Get the value of real
        public double getReal(){
                return real;
        }

        // Get the value of imag
        public double getImag(){
                return imag;
        }

        public String toString(){
                char ch='+';
                if(imag<0.0)ch='-';
                return real+" "+ch+" i "+Math.abs(imag);
        }  


        // ARRAYS

        // Create a one dimensional array of Complex objects of length n
        // all real = 0 and all imag = 0
        public static Complex[] oneDarray(int n){
                Complex[] a =new Complex[n];
                for(int i=0; i<n; i++){
                      a[i] = new Complex();
                }
                return a;
        }


        // COPY
        // Copy a single complex number [static method]
        public static Complex copy(Complex a){
			return new Complex(a);	
        }
   
        // ADDITION
        // Add two Complex numbers [static method]
        public static Complex plus(Complex a, Complex b, Complex c){
				c.real =a.real+b.real;
				c.imag=a.imag+b.imag;
				return c;
        }

        //  SUBTRACTION
        //Subtract two Complex numbers [static method]
        public static Complex minus (Complex a, Complex b, Complex c){
                c.real=a.real-b.real;
                c.imag=a.imag-b.imag;
			return c;
        }

        //Subtract a double from a Complex number [static method]
        public static Complex minus(Complex a, double b, Complex c){
                c.real=a.real-b;
                c.imag=a.imag;
			return c;
        }

        //Subtract a Complex number from a double [static method]
        public static Complex minus(double a, Complex b, Complex c){
                c.real=a-b.real;
                c.imag=-b.imag;
			return c;
        }

        //Multiply two Complex numbers [static method]
        public static Complex times(Complex a, Complex b, Complex c){
               	double real =a.real*b.real-a.imag*b.imag;
                double imag =a.real*b.imag+a.imag*b.real;
                c.real = real;
                c.imag =  imag;
			return c;
        }

        //Multiply a Complex number by a double [static method]
        public static Complex times(Complex a, double b, Complex c){
                c.real=a.real*b;
                c.imag=a.imag*b;
			return c;
        }

        // DIVISION
        //Division of two Complex numbers a/b [static method]
        public static Complex over(Complex a, Complex b, Complex c){
        	double real, imag;
            if(a.isZero()){
                    if(b.isZero()){
                            real=Double.NaN;
                            imag=Double.NaN;
                    }
                    else{
                            real=0.0;
                            imag=0.0;
                    }
            }
            else{
                    if(Math.abs(b.real)>=Math.abs(b.imag)){
							double ratio=b.imag/b.real;
							double denom=b.real+b.imag*ratio;
                            real=(a.real+a.imag*ratio)/denom;
                            imag=(a.imag-a.real*ratio)/denom;
                    }
                    else{
							double ratio=b.real/b.imag;
							double denom=b.real*ratio+b.imag;
                            real=(a.real*ratio+a.imag)/denom;
                            imag=(a.imag*ratio-a.real)/denom;
                    }
            }
           
			c.real = real;
			c.imag = imag;
			return c;
        }

        //Division of a Complex number, a, by a double, b [static method]
        public static Complex over(Complex a, double b, Complex c){
                c.real=a.real/b;
                c.imag=a.imag/b;
			return c;
        }

        //Division of a double, a, by a Complex number, b  [static method]
        public static Complex over(double a, Complex b, Complex c){
				double real, imag;

                if(a==0.0){
                        if(b.isZero()){
                                real=Double.NaN;
                                imag=Double.NaN;
                        }
                        else{
                                real=0.0;
                                imag=0.0;
                        }
                }
                else{
                        if(Math.abs(b.real)>=Math.abs(b.imag)){
                                double ratio=b.imag/b.real;
								double denom=b.real+b.imag*ratio;
                                real=a/denom;
                                imag=-a*ratio/denom;
                        }
                        else{
								double ratio=b.real/b.imag;
								double denom=b.real*ratio+b.imag;
                                real=a*ratio/denom;
                                imag=-a/denom;
                        }
                }
				c.real = real;
				c.imag = imag;
			return c;
        }

        //FURTHER MATHEMATICAL FUNCTIONS

        // Negates a Complex number [static method]
        public static Complex negate(Complex a, Complex c){
                c.real=-a.real;
                c.imag=-a.imag;
                return c;
        }

        //Absolute value (modulus) of a complex number [static method]
        public static double abs(Complex a){
                double rmod = Math.abs(a.real);
                double imod = Math.abs(a.imag);
                double ratio = 0.0D;
                double res = 0.0D;

                if(rmod==0.0){
                res=imod;
                }
                else{
                if(imod==0.0){
                        res=rmod;
                }
                        if(rmod>=imod){
                                ratio=a.imag/a.real;
                                res=rmod*Math.sqrt(1.0 + ratio*ratio);
                        }
                        else{
                                ratio=a.real/a.imag;
                                res=imod*Math.sqrt(1.0 + ratio*ratio);
                        }
                }
                return res;
        }

        //Square of the absolute value (modulus) of a complex number [static method]
        public static double squareAbs(Complex a){
                return a.real*a.real + a.imag*a.imag;
        }

        //Argument of a complex number [static method]
        public static double arg(Complex a){
                return Math.atan2(a.imag, a.real);
        }

        //Complex conjugate of a complex number [static method]
        public static Complex conjugate(Complex a, Complex c){
                c.real=a.real;
                c.imag=-a.imag;
                return c;
        }

      

        //Exponential of a complex number
        public static Complex exp(Complex aa, Complex z){
                double a = aa.real;
                double b = aa.imag;

                if(b==0.0){
                        z.real=Math.exp(a);
                        z.imag=0.0;
                }
                else{
                        if(a==0){
                                z.real=Math.cos(b);
                                z.imag=Math.sin(b);
                        }
                        else{
                                double c=Math.exp(a);
                                z.real=c*Math.cos(b);
                                z.imag=c*Math.sin(b);
                        }
                }
                return z;
        }

        //Principal value of the natural log of an Complex number
        public static Complex log(Complex aa, Complex c){
                double a=aa.real;
                double b=aa.imag;

                if(b==0.0){
                        c.set(Math.log(a),0.0);
                }
                else{
                        c.real=Math.log(Complex.abs(aa));
                        c.imag=Math.atan2(b,a);
                }
                return c;
        }

        //Roots
        //Principal value of the square root of a complex number
        public static Complex sqrt(Complex aa, Complex c  ){
                double a=aa.real;
                double b=aa.imag;

                if(b==0.0){
                        if(a>=0.0){
                                c.real=Math.sqrt(a);
                                c.imag=0.0;
                        }
                        else{
                                c.real=0.0;
                                c.imag= Math.sqrt(-a);
                        }
                }
                else{
                        double w, ratio;
                        double amod=Math.abs(a);
                        double bmod=Math.abs(b);
                        if(amod>=bmod){
                                ratio=b/a;
                                w=Math.sqrt(amod)*Math.sqrt(0.5*(1.0 + Math.sqrt(1.0 + ratio*ratio)));
                        }
                        else{
                                ratio=a/b;
                                w=Math.sqrt(bmod)*Math.sqrt(0.5*(ratio + Math.sqrt(1.0 + ratio*ratio)));
                        }
                        if(a>=0.0){
                                c.real=w;
                                c.imag=b/(2.0*w);
                        }
                        else{
                                if(b>=0.0){
                                        c.imag=w;
                                        c.real=bmod/(2.0*c.imag);
                                }
                                else{
                                        c.imag=-w;
                                        c.real=bmod/(2.0*c.imag);
                                }
                        }
                }
                return c;
        }

        //Powers
        // Square of a complex number
        public static Complex square(Complex aa, Complex c){
                 double real = aa.real*aa.real-aa.imag*aa.imag;
                 double imag = 2.0*aa.real*aa.imag;
                c.real= real;
                c.imag= imag;
                return c;
        }

        // returns a Complex number raised to a Complex power
        public static Complex pow(Complex a, Complex b, Complex c){
                Complex.exp(Complex.times(b, Complex.log(a, c), c), c);
                return c;
        }

        // Complex trigonometric functions

        //Inverse cosine of a Complex number
        public static Complex acos(Complex a, Complex c ){
                sqrt(minus(square(a, c),1.0, c), c);
                plus(a, c, c);
                times(minusJay, log(c, c), c);
                return c;
        }

        // LOGICAL FUNCTIONS
        // Returns true if the Complex number has a zero imaginary part, i.e. is a real number
        public boolean isReal(){
                return imag==0.0; 
        }

        // Returns true if the Complex number has a zero real and a zero imaginary part
        // i.e. has a zero modulus
        public boolean isZero(){
              return real == 0.0 && imag == 0.0;
        }

        public static boolean isEqual(Complex a, Complex b){
              return  (a.real == b.real && a.imag == b.imag);					      
        }
        
        // returns new zero complex
        public static Complex zero() {
        	return new Complex();
        }
        
        // Returns true if the Complex number is set to Double.NaN (i.e. its real OR imaginary part)
    	// Added for Intergeo File Format (Yves Kreis) -->
        public static boolean isNaN(Complex c) {
        	return (Double.isNaN(c.real) || Double.isNaN(c.imag));
        }
    	// <-- Added for Intergeo File Format (Yves Kreis)
      
}
