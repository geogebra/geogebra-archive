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

import java.util.Vector;


public abstract class Algebraic{
	public abstract Algebraic add (Algebraic x) throws JasymcaException;	
	public abstract Algebraic mult(Algebraic x) throws JasymcaException;	
	public abstract Algebraic div (Algebraic x) throws JasymcaException;	
	public abstract Algebraic deriv( Variable var ) throws JasymcaException;
	public abstract Algebraic integrate( Variable var ) throws JasymcaException;
	public abstract double norm();

	public Algebraic rat() throws JasymcaException{
		return new LambdaRAT().f_exakt(this);
	}

	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		throw new JasymcaException("Map not implemented for "+this);
	}	
	
	// Divide 2 Polynomials with rest, overwritten in Polynomial && Zahl	
	public Algebraic[] div( Algebraic q1, Algebraic[] result) throws JasymcaException{ return result; }

	public Algebraic divrest(Algebraic q) throws JasymcaException{
		return div(q,null)[1];
	}
	
	public boolean depends(Variable var) { return false; }
	// Loke depends, but does not recurse into functions
	public boolean depdir(Variable var){  return false; }
	
	// Build polynomial expression using Horner's method
	public static Algebraic horner(Variable x, Algebraic[] c, int n) throws JasymcaException{
		if(n==0) return Zahl.ZERO;
		Polynomial X = new Polynomial(x);
		Algebraic p = c[n-1];
		for(int i=n-2; i>=0; i--){
			p = p.mult(X).add(c[i]);
		}
		return p;
	}
	// Build polynomial expression using Horner's method
	public static Algebraic horner(Variable x, Algebraic[] c) throws JasymcaException{
		return horner(x,c,c.length);
	}


	public Algebraic sub (Algebraic x) throws JasymcaException{
		return add(x.mult(Zahl.MINUS));
	}
	public Algebraic reduce()  throws JasymcaException{ return this; }
	
	public abstract boolean equals(Object x);	

	public Object toPrefix(){ return this; };
	
	public Algebraic pow_n(int n) throws JasymcaException{
		if(n==0)
			return Zahl.ONE;
		if(n<0)
			return Zahl.ONE.div(pow_n(-n));
		Algebraic result = this;
		for(int i=1; i<n; i++)
			result = mult(result);
		return result;
	}
	
	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		return this;
	}

	public Algebraic realpart() throws JasymcaException{
		return add(cc()).div(Zahl.TWO);
	}

	public Algebraic imagpart() throws JasymcaException{
		return sub(cc()).div(Zahl.TWO).div(Zahl.IONE);
	}

	public Algebraic cc() throws JasymcaException{
		throw new JasymcaException("cc not implemented for "+this);
	}
	
	public boolean constantq() throws JasymcaException{
		CountVars c = new CountVars();
		c.f_exakt(this);
		return c.v.size()==0;
	}
			

	////////////////// GCD routines adapted from Davenports book /////////////////

	int degree( Variable r){
		if(this instanceof Polynomial && ((Polynomial)this).var.equals(r))
			return ((Polynomial)this).degree();
		return 0;
	}
	
	Algebraic coefficient( Variable r, int i){
		if(this instanceof Polynomial && ((Polynomial)this).var.equals(r))
			return ((Polynomial)this).coefficient(i);
		if(i==0)
			return this;
		else
			return Zahl.ZERO;
	}


	// this div q (polynomial division)
	public Algebraic polydiv( Algebraic q1) throws JasymcaException{
		if(q1 instanceof Zahl)
			return div(q1);
		if(equals(Zahl.ZERO))
			return Zahl.ZERO;
		if(!(this instanceof Polynomial) || !(q1 instanceof Polynomial))
			throw new JasymcaException
			("Polydiv is implemented for polynomials only.Got "+this+" / "+q1);
		Polynomial p = (Polynomial)this;
		Polynomial q = (Polynomial)q1;
		if(p.var.equals(q.var)){
			int len = p.coef.length - q.coef.length;
			if(len<0){
				throw new JasymcaException("Polydiv requires zero rest.");
			}
			Algebraic[] cdiv = new Algebraic[len+1];
			Algebraic[] nom	 = p.clone( p.coef );
			Algebraic 	den	 = q.coef[q.coef.length-1];
			for(int i=len, k=nom.length-1; i>=0; i--,k--){
				cdiv[i] = nom[k].polydiv( den);
				nom[k] = Zahl.ZERO;					
				for(int j=k-1,l=q.coef.length-2; j>k-q.coef.length; j--,l--)
						nom[j] = nom[j].sub( cdiv[i].mult(q.coef[l]));
			}
			return horner(p.var,cdiv);
		}else{
			Algebraic[] cn = new Algebraic[p.coef.length];
			for(int i=0; i<p.coef.length; i++)
				cn[i] = p.coef[i].polydiv(q1);				
			return horner(p.var,cn);
		}
	}
	
	// this modulo q (polynomial division)
	public Algebraic mod( Algebraic q, Variable r) throws JasymcaException{
		int len = degree(r) - q.degree(r);
		if(len<0)
			return this;
		Algebraic[] cdiv = new Algebraic[len+1];
		Algebraic[] nom	 = new Algebraic[degree(r)+1];
		for(int i=0; i<nom.length; i++)
			nom[i] = coefficient(r,i);
		Algebraic 	den	 =  q.coefficient(r,q.degree(r));
		for(int i=len, k=nom.length-1; i>=0; i--,k--){
			cdiv[i] = nom[k].polydiv( den);
			nom[k] = Zahl.ZERO;					
			for(int j=k-1,l=(q.degree(r)+1)-2; j>k-  (q.degree(r)+1); j--,l--)
						nom[j] = nom[j].sub( cdiv[i].mult(q.coefficient(r,l)));   
		}
		return horner(r,nom,nom.length-1 -len);
	}
	
	public Algebraic euclid( Algebraic q, Variable r) throws JasymcaException{
		// p,q are numbers or polynomials
		// Alles bezogen auf Variable r !
		int dp = degree(r);
		int dq = q.degree(r);
		Algebraic a = dp<dq ? this : mult( q.coefficient(r,dq).pow_n(dp-dq+1));
		Algebraic b = q;
		Algebraic c = a.mod(b,r);
		return c.equals(Zahl.ZERO) ? b : b.euclid(c,r);
	}

	// Davenport, p133	
	public Algebraic poly_gcd( Algebraic q) throws JasymcaException{
		// poly_gcd does not work with Exponential simplifications
		if( Exponential.containsexp(this) || Exponential.containsexp(q) ){
			DeExp	dx = new DeExp();
			return new NormExp().f_exakt( dx.f_exakt(this).poly_gcd( dx.f_exakt(q) ));
		}
		if( this.equals(Zahl.ZERO )) return q;
		if( q.equals(Zahl.ZERO )) 	 return this;
		if( this instanceof Zahl || q instanceof Zahl ) return Zahl.ONE;
		// r is the mainest of the two variables
		Variable r = ((Polynomial)q).var.smaller(((Polynomial)this).var) ?
						((Polynomial)this).var : ((Polynomial)q).var;
		Algebraic pc = content(r), qc = q.content(r);
		Algebraic eu = polydiv(pc).euclid( q.polydiv(qc), r);
		Algebraic re = eu.polydiv(eu.content(r)).mult(pc.poly_gcd(qc));
//		return re;
		if(re instanceof Zahl) return Zahl.ONE;
		Polynomial rp = (Polynomial)re;
		if(rp.coef[rp.degree()] instanceof Zahl)
			return rp.div(rp.coef[rp.degree()]); // Normalize Polynomial
		return rp;
	}
	
	// Davenport, p133	
	public Algebraic content(Variable r) throws JasymcaException{
		if( this instanceof Zahl ) return this;
		Algebraic result = coefficient(r,0);
		for(int i=0; i<=degree(r) && !result.equals(Zahl.ONE); i++)
			result = result.poly_gcd(coefficient(r,i));
		return result;
	}


////////////////////////////////////////////////////////////////////////////////////////
	
	public static void p(String s) { Lisp.p(s); }
			
	

}


// Count number of Simple Variables (no Constants!)
class CountVars extends LambdaAlgebraic{
	Vector v;
	public CountVars(){
		v = new Vector();
	}
	
	Algebraic f_exakt(Algebraic f) throws JasymcaException{ 
		if(f instanceof Polynomial){
			Polynomial x = (Polynomial)f;
			if(x.var instanceof SimpleVariable && !(x.var instanceof Constant)){
				if(!v.contains(x.var))
					v.addElement(x.var);
			}
		}
		f.map(this);
		return Zahl.ZERO;
	}
}


