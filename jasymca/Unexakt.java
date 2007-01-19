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

public class Unexakt extends Zahl{
	public double real, imag; 
	
	public Unexakt(){}
	
	public Unexakt(double real, double imag){
		this.real = real;
		this.imag = imag;
	}
	
	public Unexakt(double real){
		this(real,0.);
	}
		
	
	public double norm(){
		return Math.sqrt(real*real+imag*imag);
	}
	
	public Unexakt arg(){
		return new Unexakt(Math.atan2(imag, real));
	}
	
	public Algebraic add(Algebraic x) throws JasymcaException{
		if(x instanceof Unexakt)
			return new Unexakt(real+((Unexakt)x).real, imag+((Unexakt)x).imag);
		return x.add(this);
	}
	
	public Algebraic mult(Algebraic x) throws JasymcaException{
		if(x instanceof Unexakt)
			return new Unexakt(real*((Unexakt)x).real - imag*((Unexakt)x).imag,
							real*((Unexakt)x).imag + imag*((Unexakt)x).real);
		return x.mult(this);
	}
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		if(x instanceof Unexakt){
			// (a+ib)/(x+iy) = (1/(x^2+y^2))*((ax+bx)+i(bx-ay))
			double den = ((Unexakt)x).real*((Unexakt)x).real+((Unexakt)x).imag*((Unexakt)x).imag;
			if(den==0.)
				throw new JasymcaException("Division by Zero.");
			return new Unexakt((real*((Unexakt)x).real + imag*((Unexakt)x).imag)/den,
							(imag*((Unexakt)x).real - real*((Unexakt)x).imag)/den).reduce();
		}
		if(x instanceof Exakt){
			return new Exakt(real,imag).div(x);
		}
		return super.div(x);
	}
	
	public String toString(){
		if(imag==0.)
			return ""+real;
		if(real==0.)
			return imag+"*i";
		return "("+real+(imag>0?"+":"")+imag+"*i)";
	}
	
	public boolean integerq(){
		return imag==0. && Math.round(real) == real;
	}	

	public boolean komplexq(){
		return imag!=0;
	}	

	public Algebraic realpart() throws JasymcaException{
		return new Unexakt(real);
	}

	public Algebraic imagpart() throws JasymcaException{
		return new Unexakt(imag);
	}


	
	public boolean equals(Object x){
		if(x instanceof Unexakt)
			return ((Unexakt)x).real == real && ((Unexakt)x).imag == imag;
		if(x instanceof Exakt){
			return ((Exakt)x).tofloat().equals(this);
		}		
		return false;
	}
	
	public boolean smaller( Zahl x) throws JasymcaException{
		Unexakt xu = x.unexakt();
		if( real == xu.real )
			return imag < xu.imag;
		else
			return real < xu.real;
	}

	public int intval(){ return (int)real; }				

	
	public Zahl abs(){
		if(imag==0.)
			return new Unexakt(Math.abs(real));
		return new Unexakt(Math.sqrt(real*real+imag*imag));
	}

		// Rationalize
	public Algebraic rat(){
		return new Exakt(real,imag);
	}
	
}
		
		
