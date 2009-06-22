package jasymca;
/* Jasymca	-	- Symbolic Calculator for Mobile Devices
   This version is written for J2ME, CLDC 1.1,  MIDP 2, JSR 75
   or J2SE


   Copyright (C) 2006 - Helmut Dersch  der@hs-furtwangen.de
   
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */

/*------------------------------------------------------------*/

// Rational arbitrary precision constant

import java.math.BigInteger;
import java.util.Vector;

public class Exakt extends Zahl{
	BigInteger real[], imag[]=null;	


	public Exakt(BigInteger[] real){
		this(real, null);
	}
	
	public Exakt(BigInteger r){
		real = new BigInteger[2];
		real[0] = r;
		real[1] = BigInteger.ONE;
	}
	
	public Exakt(BigInteger[] real,  BigInteger[] imag){
		this.real = reducev(real);
		if(imag!=null && !imag[0].equals(BigInteger.ZERO))
			this.imag = reducev(imag);
	}
	
	public Exakt(double x){
		this(x,0.);
	}
	
	public Exakt(double x, double y){
		real = reducev(double2rat(x));
		if(y!=0.){
			imag = reducev(double2rat(y));
		}
	}

	// Convert Integer to BigInteger	
	BigInteger double2big(double x){
		int exp = 0;
		while(x>1e15){ x/=10.;exp++; }
		BigInteger y = BigInteger.valueOf(Math.round(x));
		if(exp>0){
			BigInteger ten = BigInteger.valueOf(10L);
			y = y.multiply(ten.pow(exp));
		}
		return y;
	}
	
	private BigInteger[] double2rat(double x){
		BigInteger[] br;
		if(x==0){
			br 	  = new BigInteger[2];
			br[0] = BigInteger.ZERO;
			br[1] = BigInteger.ONE ;
			return br;
		}
		if(x<0.){
			br = double2rat(-x);
			br[0] = br[0].negate();
			return br;
		}
		double eps = 1.e-8;
		Zahl a = Lambda.env.getnum("ratepsilon");
		if(a !=null){
			double epstry = a.unexakt().real;
			if(epstry>0) eps = epstry;
		}
		if(x<1/eps ){ // Use cfs
			double[] y = cfs(x, eps);
			br 	  = new BigInteger[2];
			// Todo: 
			br[0] = double2big(y[0]);
			br[1] = double2big(y[1]);
			return br;
		}			
		br 	  = new BigInteger[2];
		br[0] = double2big(x);
		br[1] = BigInteger.ONE;
		return br;
	}
	
	
	// Create continous fraction expansion of double x
	private double[] cfs( double x, double tol ){
		Vector a = new Vector();
		double error, y[] = new double[2];
		tol = Math.abs(x*tol);
		double aa = Math.floor(x);
		a.addElement(new Double(aa));
		double ra = x;
		cfsd(a,y);
		while((error=Math.abs(x-y[0]/y[1])) > tol){
			ra = 1./(ra-aa);
			aa = Math.floor(ra);
			a.addElement(new Double(aa));
			cfsd(a,y);
		}
		return y;
	}
			
	private void cfsd(Vector a, double[] y){
		int i=a.size()-1;
		double N=((Double)a.elementAt(i)).doubleValue(), Z=1., N1;
		i--;
		while(i>=0){
			N1 = ((Double)a.elementAt(i)).doubleValue()*N+Z;
			Z = N;
			N = N1;
			i--;
		}
		y[0] = N;
		y[1] = Z;
	}

		
		
	

	
	Exakt cfs( double tol1 ) throws JasymcaException{
		Vector a=new Vector();
		Exakt error,y,ra,tol;
		BigInteger aa;
		tol = (Exakt)mult(new Exakt(tol1));
		aa = real[0].divide(real[1]);
		a.addElement( aa );
		y = new Exakt(cfs(a));
		error = (Exakt)((Exakt)(sub(y))).abs();
		ra=this; 
		while(tol.smaller(error)){
			ra = (Exakt)Zahl.ONE.div(ra.sub(new Exakt(aa)));
			aa = ra.real[0].divide(ra.real[1]);
			a.addElement(aa);
			y = new Exakt(cfs(a));
			error=(Exakt)((Exakt)sub(y)).abs();
		}
		return y;
	} 

	private BigInteger[] cfs(Vector a) throws JasymcaException{
		int i=a.size()-1;
		BigInteger N=(BigInteger)a.elementAt(i), Z=BigInteger.ONE, N1;
		i--;
		while(i>=0){
			N1 = ((BigInteger)a.elementAt(i)).multiply(N).add(Z);
			Z = N;
			N = N1;
			i--;
		}
		BigInteger[] r = {N,Z};
		return r;
	}


	// Copy and simplify	
	private BigInteger[] reducev(BigInteger[] y){
		BigInteger[] x = new BigInteger[2];
		x[0] = y[0]; x[1] = y[1];
		BigInteger gcd = x[0].gcd(x[1]);
		if(!gcd.equals(BigInteger.ONE)){
			x[0] = x[0].divide(gcd);
			x[1] = x[1].divide(gcd);
		}
		if(x[1].compareTo(BigInteger.ZERO) < 0){
			x[0]=x[0].negate();
			x[1]=x[1].negate();
		}
		return x;
	}
	
	public Algebraic realpart() throws JasymcaException{
		return new Exakt(real);
	}

	public Algebraic imagpart() throws JasymcaException{
		if(imag!=null)
			return new Exakt(imag);
		return new Exakt(BigInteger.ZERO);
	}
	
	
	private double floatValue(BigInteger[] x){
		BigInteger[] q = x[0].divideAndRemainder(x[1]);
		return q[0].doubleValue() + q[1].doubleValue()/x[1].doubleValue();
	}
		
	
	public Unexakt tofloat(){
		if(imag == null)
			return new Unexakt(floatValue(real));
		else
			return new Unexakt(floatValue(real), floatValue(imag));
	}
	
	private BigInteger[] add(BigInteger[] x, BigInteger[] y){
		if(x == null) return y;
		if(y == null) return x;
		BigInteger[] r = new BigInteger[2];
		r[0] = x[0].multiply(y[1]).add(y[0].multiply(x[1]));
		r[1] = x[1].multiply(y[1]);
		return r;
	}
	
	private BigInteger[] sub(BigInteger[] x, BigInteger[] y){
		if(y == null) return x;
		BigInteger[] r = new BigInteger[2];
		r[0] = y[0].negate();
		r[1] = y[1];
		return add(x,r);
	}
				
	private BigInteger[] mult(BigInteger[] x, BigInteger[] y){
		if(x==null || y==null) return null;
		BigInteger[] r = new BigInteger[2];
		r[0] = x[0].multiply(y[0]);
		r[1] = x[1].multiply(y[1]);
		return r;
	}
	
	private BigInteger[] div(BigInteger[] x, BigInteger[] y) throws JasymcaException{
		if(x==null) return null;
		if(y==null) throw new JasymcaException("Division by Zero.");
		BigInteger[] r = new BigInteger[2];
		r[0] = x[0].multiply(y[1]);
		r[1] = x[1].multiply(y[0]);
		return r;
	}
	
	private boolean equals(BigInteger[] x, BigInteger[] y){
		if(x==null && y==null) return true;
		if(x==null || y==null) return false;
		return x[0].equals(y[0]) && x[1].equals(y[1]);
	}
	
	
	public Algebraic add(Algebraic x) throws JasymcaException{
		if(!(x instanceof Zahl))
			return x.add( this );
		Exakt X = ((Zahl)x).exakt();
		return new Exakt( add(real, X.real ), add(imag, X.imag ));
	}
		
			

	public Algebraic mult(Algebraic x) throws JasymcaException{
		if(!(x instanceof Zahl) ) 
			return x.mult( this );
		Exakt X = ((Zahl)x).exakt();
		return new Exakt( sub(mult(real,X.real), mult(imag,X.imag)), 
						  add(mult(imag,X.real), mult(real,X.imag)) );
	}
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		if(!(x instanceof Zahl) ) 
			return super.div(x);
		Exakt X = ((Zahl)x).exakt();
		BigInteger[] N = add(mult(X.real,X.real),mult(X.imag,X.imag));
		if(N==null || N[0].equals(BigInteger.ZERO))
			throw new JasymcaException("Division by Zero.");
		return new Exakt( div( add(mult(real,X.real), mult(imag,X.imag)), N), 
						  div( sub(mult(imag,X.real), mult(real,X.imag)), N ));
	}
	
	private BigInteger lsm(BigInteger x, BigInteger y){
		return x.multiply(y).divide(x.gcd(y));
	}
	
	public Algebraic[] div( Algebraic q1, Algebraic[] result) throws JasymcaException{ 
		if(result==null)
			result=new Algebraic[2];
		if(!(q1 instanceof Zahl)){
			result[0] = Zahl.ZERO;
			result[1] = this;
			return result;
		}
		Exakt q = ((Zahl)q1).exakt();
		if(!komplexq() && q.komplexq()){
			result[0] = Zahl.ZERO;
			result[1] = this;
			return result;
		}
		if(komplexq() && !q.komplexq()){
			result[0] = div(q);
			result[1] = Zahl.ZERO;
			return result;
		}
		if(komplexq() && q.komplexq()){
			result[0] = imagpart().div(q.imagpart());
			result[1] = sub(result[0].mult(q));
			return result;
		}
		// Both real
		if( integerq() && q.integerq() ){
			BigInteger d[] = real[0].divideAndRemainder(q.real[0]);
			result[0] = new Exakt(d[0]);
			result[1] = new Exakt(d[1]);
			return result;
		}
		result[0] = div(q);
		result[1] = Zahl.ZERO;
		return result;
	}		
		
			
			
	private String b2string(BigInteger[] x){
		if(x[1].equals(BigInteger.ONE))
			return x[0].toString();
		return x[0].toString()+"/"+x[1].toString();
	}
	
	public String toString(){
		if(imag==null || imag[0].equals(BigInteger.ZERO))
			return ""+b2string(real);
		if(real[0].equals(BigInteger.ZERO))
			return b2string(imag)+"*i";
		return "("+b2string(real)+(imag[0].compareTo(BigInteger.ZERO)>0?"+":"")+b2string(imag)+"*i)";
	}
	
	
	public boolean integerq(){
		return real[1].equals(BigInteger.ONE) && imag==null;
	}	
	
	public boolean smaller( Zahl x) throws JasymcaException{
		return unexakt().smaller(x);
	}

	public boolean komplexq(){
		return imag!=null && !imag[0].equals(BigInteger.ZERO);
	}	
			
	
	public boolean equals(Object x){
		if(x instanceof Exakt)
			return equals(real,((Exakt)x).real) && equals(imag,((Exakt)x).imag);
		return tofloat().equals(x);
	}	
	
	public double norm(){ return tofloat().norm(); }

	public Algebraic rat(){ return this; }
	
	public Zahl abs(){
		if(komplexq())
			return tofloat().abs();
		BigInteger[] r = new BigInteger[2];
		r[0] = real[0].compareTo(BigInteger.ZERO)<0?real[0].negate():real[0];
		r[1] = real[1];
		return new Exakt(r);
	}
	
	
	public Exakt gcd(Exakt x) throws JasymcaException{
		if(equals(Zahl.ZERO))
			return x;
		else if(x.equals(Zahl.ZERO))
			return this;
		if(komplexq() && x.komplexq()){
			 Exakt r = ((Exakt)realpart()).gcd((Exakt)x.realpart());
			 Exakt i = ((Exakt)imagpart()).gcd((Exakt)x.imagpart());
			 if(r.equals(Zahl.ZERO))
			 	return (Exakt)i.mult(Zahl.IONE);
			 if(realpart().div(r).equals(imagpart().div(i)))
			 	return (Exakt)r.add(i.mult(Zahl.IONE));
			else
				return Zahl.ONE.exakt();
		}else if(komplexq() || x.komplexq())
			return Zahl.ONE.exakt();
		else{ // Neither complex
			return (Exakt)new Exakt(real[0].multiply(x.real[1]).gcd(real[1].multiply(x.real[0]))).div
				  (new Exakt(real[1].multiply(x.real[1])));
		}
	}	
	
	public int intval() { return real[0].intValue();}
	
}
