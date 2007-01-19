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

public abstract class Zahl extends Algebraic{

	public static Zahl ZERO 	= new Unexakt(0.);
	public static Zahl ONE  	= new Unexakt(1.);
	public static Zahl TWO  	= new Unexakt(2.);
	public static Zahl MINUS	= new Unexakt(-1.);
	public static Zahl IONE   	= new Unexakt(0.,1.);
	public static Zahl IMINUS 	= new Unexakt(0.,-1.);
	public static Polynomial PI = new Polynomial(new Constant("pi", Math.PI));	

	// These must be created after program initialization
	public static Algebraic SQRT2; 
	public static Algebraic SQRT3; 

	static void init(){
		try{
			SQRT2 = FunctionVariable.create("sqrt", new Exakt(2.));	
			SQRT3 = FunctionVariable.create("sqrt", new Exakt(3.));
		}catch(Exception e){
		}
	}
		
	public Algebraic div (Algebraic x) throws JasymcaException{
		if(x instanceof Vektor)
			throw new JasymcaException("Can not divide through Vector.");
		if(x instanceof Rational)
			return ((Rational)x).den.mult(this).div(((Rational)x).nom).reduce();
		if(x instanceof Polynomial)
			return (new Rational(this, (Polynomial)x)).reduce();
		throw new JasymcaException("Internal Error.");
	}
		
	
	public Algebraic deriv( Variable var ) { return ZERO; }
	public Algebraic integrate( Variable var ) throws JasymcaException { 
		if(this.equals(Zahl.ZERO))
			return this;
		return new Polynomial(var).mult(this); 
	}
		
	
	public abstract int intval();				
	public abstract boolean komplexq();

	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		return this;
	}

	public Algebraic cc() throws JasymcaException{
		return realpart().add(imagpart().mult(Zahl.IMINUS));
	}
	
	public abstract Zahl abs();
	
	public Exakt exakt(){ 
		return this instanceof Exakt? (Exakt)this : 
		       new Exakt(((Unexakt)this).real, ((Unexakt)this).imag);
	}
	
	public Unexakt unexakt(){ 
		return this instanceof Unexakt? (Unexakt)this : 
			   ((Exakt)this).tofloat();
	}
	
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		return f.f(this);
	}		
	
	public Zahl gcd(Zahl x) throws JasymcaException{
		return exakt().gcd(x.exakt());
	}
	
	public abstract boolean smaller( Zahl x) throws JasymcaException;


	public Algebraic[] div( Algebraic q1, Algebraic[] result) throws JasymcaException{ 
		return exakt().div(q1,result);
	}
	
	public abstract boolean integerq();	
}
