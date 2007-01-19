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

public class Polynomial extends Algebraic{
	public Algebraic[] 	coef 	=	null;
	public Variable 	var		=	null;
	
	// Polynomial with main-est Variable
	public static Polynomial top = new Polynomial(SimpleVariable.top); 
		
	public Polynomial(){}
	
	public Polynomial(Variable var, Algebraic[] coef){
		this.var 	= var;
		this.coef 	= norm(coef);
	}

	public Polynomial(Variable var){
		coef 	= new Zahl[2];
		coef[0] = Zahl.ZERO;
		coef[1] = Zahl.ONE;
		this.var= var;
	}
		

	// Remove leading Zero coefficients,, leave coef[0]	
	public Algebraic[] norm(Algebraic[] x){
		int len = x.length;
		while(len>0 && (x[len-1]==null || x[len-1].equals(Zahl.ZERO))){
			len--;
		}
		if(len == 0) len = 1;
		if(len!=x.length){
			Algebraic[] na= new Algebraic[len];
			for(int i=0; i<len; i++)
				na[i] = x[i];
			return na;
		}
		return x;
	}
		
	
	public Algebraic coefficient(int i){
		if(i<coef.length)
			return coef[i];
		return Zahl.ZERO;
	}
	
	public int degree(){ return coef.length-1; }
	
	
	public Algebraic add( Algebraic p) throws JasymcaException{
		if(p instanceof Rational)
			return p.add(this);
		if(p instanceof Polynomial){
			if( var.equals(((Polynomial)p).var)){
				int len = Math.max(coef.length, ((Polynomial)p).coef.length);
				Algebraic[] csum = new Algebraic[len];
				for(int i=0; i<len; i++)
					csum[i] = coefficient(i).add(((Polynomial)p).coefficient(i));
				return (new Polynomial(var, csum)).reduce();
			}else if( var.smaller(((Polynomial)p).var)){
				return p.add( this );
			}
		}
		Algebraic[] csum = clone( coef );
		csum[0] = coef[0].add(p);
		return (new Polynomial(var, csum)).reduce();
	}
		
	public Algebraic mult( Algebraic p) throws JasymcaException{
		if(p instanceof Rational)
			return p.mult(this);
		if(p instanceof Polynomial){
			if( var.equals(((Polynomial)p).var)){
				int len = coef.length + ((Polynomial)p).coef.length-1;
				Algebraic[] cprod = new Algebraic[len];
				for(int i=0; i<len; i++) cprod[i] = Zahl.ZERO;
				for(int i=0; i<coef.length; i++)
					for(int k=0; k<((Polynomial)p).coef.length; k++)
						cprod[i+k] = cprod[i+k].add( coef[i].mult(((Polynomial)p).coef[k]) );
				return new Polynomial(var, cprod).reduce();
			}else if( var.smaller( ((Polynomial)p).var )){
				return p.mult( this );
			}
		}
		

		Algebraic[] cprod = new Algebraic[coef.length];
		for(int i=0; i<coef.length; i++)
			cprod[i] = coef[i].mult(p);		
		return new Polynomial(var, cprod).reduce();
	}
	
	public Algebraic div( Algebraic q) throws JasymcaException{
		if(q instanceof Zahl)
			return div(q,null)[0];
		if(q instanceof Polynomial)
			return (new Rational(this,(Polynomial)q)).reduce();
		if(q instanceof Rational)
			return mult(((Rational)q).den).div(((Rational)q).nom).reduce();
		throw new JasymcaException("Cannot divide through "+q);
	}
	

	// Divide 2 Polynomials with rest	
	public Algebraic[] div( Algebraic q1, Algebraic[] result) throws JasymcaException{
		if(q1 instanceof Rational){
			Algebraic den = ((Rational)q1).den;
			result = mult(den).div(((Rational)q1).nom, result);
			result[1] = result[1].div(den);
			return result;
		}

		if(result == null)
			result = new Algebraic[2];

		if(q1 instanceof Polynomial){
			Polynomial q = (Polynomial)q1;
			if( var.equals(q.var) ){
				int len = coef.length - q.coef.length;
				if(len<0){
					result[0] = Zahl.ZERO;
					result[1] = this;//q1;
					return result;
				}
				Algebraic[] cdiv = new Algebraic[len+1];
				Algebraic[] nom	 = clone( coef );
				Algebraic 	den	 = q.coef[q.coef.length-1];
				for(int i=len, k=nom.length-1; i>=0; i--,k--){
					cdiv[i] = nom[k].div( den);
					nom[k] = Zahl.ZERO;					
					for(int j=k-1,l=q.coef.length-2; j>k-q.coef.length; j--,l--)
						nom[j] = nom[j].sub( cdiv[i].mult(q.coef[l]));
				}
				// Sowohl r[0] als auch r[1] können Rational sein, allerdings
				// ohne von var abhängigem Nenner
				result[0] = horner(var,cdiv);
				result[1] = horner(var,nom,nom.length-1 -len);
				return result;
			} // The larger of the two variables is assumed to be main
			else if( q.var.smaller(var) ){ // var is main, q is independ of var
				Algebraic[] cn = new Algebraic[coef.length];
				for(int i=0; i<coef.length; i++)
					cn[i] = coef[i].div(q1);				
				result[0] = horner(var,cn);
				result[1] = Zahl.ZERO;
				return result;
			}else{ //q.var is main
				result[0] = Zahl.ZERO;
				result[1] = this;	
				return result;			
			}
		}
		if( !(q1 instanceof Zahl) ){
			throw new RuntimeException("div routine not implemented for"+this+"/"+q1);
		}
		Algebraic[] csum = clone( coef );
		for(int i=0; i<csum.length; i++)
			csum[i] = coef[i].div(q1);		
		result[0] = (new Polynomial(var, csum)).reduce();
		result[1] = Zahl.ZERO;
		
		return result;
	}
	
	public Algebraic reduce() throws JasymcaException{
		if( coef.length == 0 )
			return Zahl.ZERO;
		if( coef.length == 1 )
			return coef[0].reduce();
		return this;
		// Rebuild Polynomial
/*		Algebraic x = var.reduce();
		Algebraic cn[] = new Algebraic[coef.length];
		Algebraic r = Zahl.ZERO;
		for(int i=0; i<cn.length; i++)
			r = r.add(coef[i].reduce()).mult(x);
		if(coef.lenth>0)
			r = r.add(coef[0].reduce());
		return r;*/
	}
	
	public Algebraic[] clone(Algebraic[] x){
		Algebraic[] c = new Algebraic[x.length];
		for(int i=0; i<x.length;i++)
			c[i] = x[i];
		return c;
	}
		
					
	public String toString(){
		Vector x = new Vector();	
		for(int i=coef.length-1; i>0;i--){
			if( coef[i].equals(Zahl.ZERO) )
				continue;	
			String s = "";		
			if(coef[i].equals(Zahl.MINUS))
				s+="-";
			else if( !coef[i].equals(Zahl.ONE) )
				s+=coef[i].toString()+"*";
			s+=var.toString();
			if(i>1)
				s+="^"+i;
			x.addElement(s);
		}
		if( !coef[0].equals(Zahl.ZERO) )
			x.addElement(coef[0].toString());	
		String s = "";
		if(x.size()>1)
			s+="(";
		for(int i=0; i<x.size(); i++){
			s += (String)x.elementAt(i);
			if( i< x.size()-1 && !(((String)x.elementAt(i+1)).charAt(0)=='-'))
				s+="+";
		}
		if(x.size()>1)
			s+=")";
		return s;
	}
	
	public boolean equals(Object x){
		if (! (x instanceof Polynomial) )
			return false;
		if(!(var.equals(((Polynomial)x).var)) || 
			coef.length != ((Polynomial)x).coef.length)
			return false;
		for(int i=0; i<coef.length; i++)
			if(!coef[i].equals(((Polynomial)x).coef[i]))
				return false;
		return true;
	}
	
	public Object toPrefix(){
		Object r = Lisp.list(coef[coef.length-1].toPrefix());
		Object x = var.toPrefix();
		for(int i=coef.length-1; i>0; i--){
			r = Lisp.list(Lisp.cons("*", Lisp.cons(x, r)));
			if(!coef[i-1].equals(Zahl.ZERO)){
				r = Lisp.list(Lisp.cons("+", Lisp.cons(coef[i-1].toPrefix(), r)));
			}
		}
		return Lisp.car(r);
	}

	public Algebraic deriv( Variable var )  throws JasymcaException{
		Algebraic r1 = Zahl.ZERO, r2 = Zahl.ZERO;
		Polynomial x = new Polynomial(this.var);
		for(int i=coef.length-1; i>1; i--){					// Horner
			r1 = r1.add(coef[i].mult(new Unexakt(i))).mult(x); // diff Variable
		}
		if(coef.length>1)
			r1 = r1.add(coef[1]);

		for(int i=coef.length-1; i>0; i--){					// Horner
			r2 = r2.add(coef[i].deriv(var)).mult(x);		// diff coef
		}
		if(coef.length>0)
			r2 = r2.add(coef[0].deriv(var));

		return r1.mult(this.var.deriv(var)).add(r2).reduce();
	}


	// Coefficients do not depend on var	
	public boolean constcoef(Variable var){
		if(!var.equals(this.var)){
			if((this.var instanceof FunctionVariable) &&
				((FunctionVariable)this.var).arg.depends(var))
				return false;
		}
		for(int i=0; i<coef.length; i++)
			if(coef[i].depends(var))
				return false;
		return true;
	}
	
	// Coefficients are numbers
	public boolean constcoef(){
		for(int i=0; i<coef.length; i++)
			if(!(coef[i] instanceof Zahl))
				return false;
		return true;
	}
	
	// Coefficients depend on var
	public boolean coefdepend(Variable var){
		for(int i=0; i<coef.length; i++)
			if(coef[i].depends(var))
				return true;
		return false;
	}
	
	// This polynomial does not contain irrational functions of var
	public boolean rational(Variable var){
		// Check Variable
		if(this.var instanceof FunctionVariable && ((FunctionVariable)this.var).arg.depends(var))
			return false;
		for(int i=0; i<coef.length; i++)
			if(coef[i] instanceof Polynomial && !(((Polynomial)coef[i]).rational(var)))
				return false;
		return true;
	}
	
	// List of variables, which depend on var
	public Vector depvars(Variable var) throws JasymcaException{
		Vector r = new Vector();
		if( ! this.var.deriv(var).equals(Zahl.ZERO) )
			r.addElement(this.var);
		for(int i=0; i<coef.length; i++){
			if(coef[i] instanceof Polynomial){
				Vector c = ((Polynomial)coef[i]).depvars(var);
				if(c.size()>0){
					for(int k=0; k<c.size(); k++){
						Object v = c.elementAt(k);
						if(!r.contains(v)) r.addElement(v);
					}
				}
			}
		}
		return r;
	}		
	
	
	
	public boolean depends(Variable var){ 
		if(coef.length==0) return false;
		if( this.var.equals(var) )
			return true;
		if( this.var instanceof FunctionVariable && ((FunctionVariable)this.var).arg.depends(var))
			return true;
		for(int i=0; i<coef.length; i++)
			if(coef[i].depends(var))
				return true;
		return false;
	}

	// Loke depends, but does not recurse into functions
	public boolean depdir(Variable var){ 
		if(coef.length==0) return false;
		if( this.var.equals(var) )
			return true;
		for(int i=0; i<coef.length; i++)
			if(coef[i].depdir(var))
				return true;
		return false;
	}


	// Flag to stop infinite partial integrations
	static boolean loopPartial = false;
	
	public Algebraic integrate( Variable var ) throws JasymcaException{
		Algebraic in = Zahl.ZERO;
		for(int i=1; i<coef.length; i++){
			if(!coef[i].depends(var))
				if(var.equals(this.var))
					// c*x^n -->1/(n+1)*x^(n+1)
					in=in.add(coef[i].mult(new Polynomial(var).pow_n(i+1).div(new Unexakt(i+1))));
				else if(this.var instanceof FunctionVariable && 
						((FunctionVariable)this.var).arg.depends(var))
					// f(x)
						if(i==1)
							in=in.add( ((FunctionVariable)this.var).integrate(var).mult(coef[1]));
					// (f(x))^2, (f(x))^3 etc
					// give up here but try again after exponential normalization
						else
							throw new JasymcaException("Integral not supported.");
				else
					// Constant:  c --> c*x
					in=in.add(coef[i].mult(new Polynomial(var).mult(new
													Polynomial(this.var).pow_n(i))));
			else
				if(var.equals(this.var))
					// c(x)*x^n , should not happen if this is canonical
					throw new JasymcaException("Integral not supported.");
				else if(this.var instanceof FunctionVariable && 
					((FunctionVariable)this.var).arg.depends(var)){
					if(i==1 && coef[i] instanceof Polynomial && ((Polynomial)coef[i]).var.equals(var)){
						// poly(x)*f(x)						
						// First attempt: try to isolate inner derivative
						// poly(x)*f(w(x)) --> check poly(x)/w' == q : const?
						//           yes   --> Int f dw * q
						p("Trying to isolate inner derivative "+this);
						try{
							FunctionVariable f = (FunctionVariable)this.var;
							Algebraic w = f.arg; 		// Innere Funktion
							Algebraic q = coef[i].div(w.deriv(var));
							if(q.deriv(var).equals(Zahl.ZERO)){ // q - constant
								SimpleVariable v = new SimpleVariable("v");
								Algebraic p = FunctionVariable.create(f.fname, new Polynomial(v));
								Algebraic  r = p.integrate(v).value(v,w).mult(q);
								in=in.add(r);
								continue;
							}
						}catch(JasymcaException je){
							// Didn't work, try more methods
						}
						p("Failed.");
						
						// Some partial integrations follow. To 
						// avoid endless loops, we flag this section

						// Coefficients of coef[i] must not depend on var
						for(int k=0;k<((Polynomial)coef[i]).coef.length;k++)
							if(((Polynomial)coef[i]).coef[k].depends(var))
								throw new JasymcaException("Function not supported by this method");


						if(loopPartial){
							loopPartial = false;
							p("Partial Integration Loop detected.");
							throw new JasymcaException("Partial Integration Loop: "+this);
						}

						// First attempt: x^n*f(x) , n-times diff!
						// works for exp,sin,cos
						p("Trying partial integration: x^n*f(x) , n-times diff "+ this);
						try{
							loopPartial=true;
							Algebraic  p = coef[i];
							Algebraic  f = ((FunctionVariable)this.var).integrate(var);
							Algebraic  r = f.mult(p);
							while(!(p=p.deriv(var)).equals(Zahl.ZERO)){
								f = f.integrate(var).mult(Zahl.MINUS);
								r = r.add(f.mult(p));
							}
							loopPartial=false;
							in=in.add(r);
							continue;
						}catch (JasymcaException je){
							loopPartial=false;
						}
						p("Failed.");
						// Second attempt: x^n*f(x) , 1-times int!
						// works for log, atan	
						p("Trying partial integration: x^n*f(x) , 1-times int "+ this);
						try{
							loopPartial=true;
							Algebraic  p = coef[i].integrate(var);
							Algebraic  f = new Polynomial((FunctionVariable)this.var);
							Algebraic  r = p.mult(f).sub(p.mult(f.deriv(var)).integrate(var));
							loopPartial=false;
							in=in.add(r);
							continue;
						}catch(JasymcaException je3){
							loopPartial=false;
						}
						p("Failed");
						// Add more attempts....
						throw new JasymcaException("Function not supported by this method");
					}else
						throw new JasymcaException("Integral not supported.");
				}else // mainvar independend of var, treat as constant and integrate coef
					in=in.add(coef[i].integrate(var).mult(new
									Polynomial(this.var).pow_n(i)));
		}
		if(coef.length>0)
			in=in.add(coef[0].integrate(var));
		return in;
	}
	
	public Algebraic cc() throws JasymcaException{
		Polynomial xn = new Polynomial(var.cc());
		Algebraic r = Zahl.ZERO;
		for(int i=coef.length-1; i>0; i--)
			r = r.add( coef[i].cc() ).mult(xn);
		if(coef.length>0)
			r = r.add(coef[0].cc());
		return r;
	}
		
					
	// Evaluate Polynomial for var = x
	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		Algebraic r  = Zahl.ZERO;
		Algebraic v  = this.var.value(var,x);
		for(int i=coef.length-1; i>0; i--){		// Horner
			r = r.add(coef[i].value(var,x)).mult(v); 
		}
		if(coef.length>0)
			r = r.add(coef[0].value(var,x));
		return r;
	}

	public Algebraic value(Algebraic x) throws JasymcaException{
		return value(this.var, x);
	}

	
	public double norm(){
		double norm=0.;
		for(int i=0; i<coef.length; i++)
			norm+=coef[i].norm();
		return norm;
	}
	
	// Divide through main coefficient; works only for numbers
	public Polynomial monic() throws JasymcaException{
		Algebraic cm = coef[coef.length-1];
		if(cm.equals(Zahl.ONE))
			return this;
		if(cm.equals(Zahl.ZERO) || !(cm instanceof Zahl) )
			throw new JasymcaException("Ill conditioned polynomial: main coefficient Zero or not number");

		return (Polynomial)div(cm);			
	}
	
	// Map f to coefficients and arg
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		Algebraic x = var instanceof SimpleVariable ? new Polynomial(var):
			FunctionVariable.create(((FunctionVariable)var).fname,
					f.f_exakt(((FunctionVariable)var).arg));
		Algebraic r=Zahl.ZERO;
		for(int i=coef.length-1; i>0; i--){
			r=r.add(f.f_exakt(coef[i])).mult(x);
		}
		if(coef.length>0)
			r=r.add(f.f_exakt(coef[0]));
		return r;
	}

		
	
	////////////////////////// Roots /////////////////////////////////////////////////////

	// is var multiplicative variable (i.e. not contained inside
	// Functionvariable
	boolean dependsmult(Variable var) throws JasymcaException{
		if(var.equals(this.var))
			return true;
		for(int i=0; i<coef.length; i++){
			if(coef[i] instanceof Polynomial &&
				((Polynomial)coef[i]).dependsmult(var))
				return true;
		}
		return false;
	}	

    // square -free-decomposition of p=p1*p2^2*p3^3*...returns [p1,p2,p3..]
	public Algebraic[] square_free_dec(Variable var) throws JasymcaException{
		if( !dependsmult(var) )
			return null;
		Algebraic dp = deriv(var);
		Algebraic gcd_pdp = poly_gcd(dp);
		Algebraic q = polydiv(gcd_pdp); 
		Algebraic p1 = q.polydiv(q.poly_gcd(gcd_pdp));
		if(gcd_pdp instanceof Polynomial &&
			 ((Polynomial)gcd_pdp).dependsmult(var)){
			Algebraic sq[] = ((Polynomial)gcd_pdp).square_free_dec(var);
			Algebraic result[] = new Algebraic[sq.length+1];
			result[0] = p1;
			for(int i=0; i<sq.length;i++)
				result[i+1]=sq[i];
			return result;
		}else{
			Algebraic result[] = { p1 };
			return result;
		}
	}
			

	public Vektor solve(Variable var) throws JasymcaException{
		if(!var.equals(this.var)) // substitute var <--> top
			return ((Polynomial)value(var, top)).solve(SimpleVariable.top);			
		Algebraic[] factors = square_free_dec(var);
		Vector s = new Vector();
		int n = factors==null?0:factors.length;
		for(int i=0; i<n; i++){
			if(factors[i] instanceof Polynomial){
				Vektor sol = null;
				Algebraic equ = factors[i];
				try{ // (1) Symbolic solution 
					sol = ((Polynomial)equ).solvepoly();
				}catch(JasymcaException je){ // (2)  Numeric solution
					sol = ((Polynomial)equ).monic().bairstow();
				}
				for(int k=0; k<sol.coord.length; k++){
					s.addElement(sol.coord[k]);
				}
			}
		}
		Algebraic cn[] = new Algebraic[s.size()];
		for(int i=0; i<cn.length; i++){
			cn[i] = (Algebraic)s.elementAt(i);
		}
		return new Vektor(cn);
	}

	// return a vektor of all different solutions	
	public Vektor solvepoly() throws JasymcaException{
		Vector s = new Vector();
		switch(degree()){
			case 0: break;
			case 1: 
				s.addElement(Zahl.MINUS.mult(coef[0].div(coef[1]) ));
				break;
			case 2: 
				Algebraic p = coef[1].div(coef[2]);
				Algebraic q = coef[0].div(coef[2]);
				p = Zahl.MINUS.mult(p).div(Zahl.TWO);
				q = p.mult(p).sub(q);
				if(q.equals(Zahl.ZERO)){
					s.addElement(p); 
					break;
				}
				q = FunctionVariable.create("sqrt", q);
				s.addElement(p.add(q));
				s.addElement(p.sub(q));
				break;
	/*		case 3:
				Algebraic a = r.coef[2];
				Algebraic b = r.coef[1];
				Algebraic c = r.coef[0];
				Zahl drei = new Exakt(3.0);
				// p=(3b-a^2)/3
				p = drei.mult(b).sub(a.mult(a)).div(drei);
				// q = c + 2a^3/27 -ab/3
				q = c.add(Zahl.TWO.mult(a.pow_n(3)).div(new Exakt(27.))).
					  					sub(a.mult(b).div(drei));
				// D^2 = (p/3)^3 + (q/2)^2
				Algebraic D = new Polynomial(new FunctionVariable("sqrt",
					  					p.div(drei).pow_n(3).add(q.div(Zahl.TWO).pow_n(2))));
				Algebraic u = new Polynomial(new FunctionVariable("csqrt",
					  					Zahl.MINUS.mult(q).div(Zahl.TWO).add(D)));
				Algebraic v = new Polynomial(new FunctionVariable("csqrt",
					  					Zahl.MINUS.mult(q).div(Zahl.TWO).sub(D)));
				Algebraic r0   = Zahl.MINUS.mult(a).div(drei).add(u).add(v);
				Algebraic r1   = Zahl.MINUS.mult(a).div(drei).sub(u.add(v).div(Zahl.TWO));
				Algebraic w    = new Unexakt(Math.sqrt(3.)).mult(u.sub(v)).
					  					div(Zahl.TWO).mult(Zahl.IONE);
				Algebraic r2	   = r1.sub(w);
				r1	   			   = r1.add(w);
				s.addElement(r0);
				s.addElement(r1);
				s.addElement(r2);
				break;*/
			default: 
				// Maybe biquadratic/bicubic etc
				// Calculate gcd of exponents for nonzero coefficients
				int gcd = -1;
				for(int i=1; i<coef.length; i++){
					if(!coef[i].equals(Zahl.ZERO)){
						if(gcd<0) gcd=i;
						else gcd=gcd(i,gcd);
					}
				}
				int deg = degree()/gcd;
				if(deg <3){ // Solveable
					Algebraic cn[] = new Algebraic[deg+1];
					for(int i=0; i<cn.length; i++)
						cn[i] = coef[i*gcd];
					Polynomial pr = new Polynomial(var, cn);
					Vektor sn = pr.solvepoly();
					if(gcd==2){ // sol = +/-sqrt(sn)
						cn = new Algebraic[sn.coord.length*2];
						for(int i=0; i<sn.coord.length; i++){
							cn[2*i] = FunctionVariable.create("sqrt", sn.coord[i]);
							cn[2*i+1] = cn[2*i].mult(Zahl.MINUS);
						}
					}else{ // sol = sn^(1/gcd);
						cn = new Algebraic[sn.coord.length];
						Zahl wx = new Unexakt(1./gcd);
						for(int i=0; i<sn.coord.length; i++){
							Algebraic exp = FunctionVariable.create("log",sn.coord[i]);
							cn[i] = FunctionVariable.create("exp", exp.mult(wx));
						}
					}
					return new Vektor(cn);
				}
				throw new JasymcaException("Can't solve expression "+this);
		}
		return Vektor.create(s);
	}

	// Euklid; a>b!=0!
	static int gcd(int a, int b){
		int c = 1;
		while(c!=0){
			c = a % b;
			a = b;
			b = c;
		}
		return a;  
	}
	


	// Use only for (a) constant coefficients and (b) squarefree function (c) monic polynomial
	// Adapted from c-code by  C. Bond (1991)
	public Vektor bairstow() throws JasymcaException{
		double[] a = new double[coef.length];
		for(int i=0; i<coef.length; i++){
			Algebraic cf = coef[coef.length-i-1]; // Reverse order
			if(!(cf instanceof Zahl) || ((Zahl)cf).komplexq() )
				throw new JasymcaException("Bairstow requires constant real coefficients.");
			a[i] = ((Zahl)cf).unexakt().real;
			a[i] /= a[0];							  // and Normalize
		}
		// Handle trivial cases
		if(coef.length==2){
			Algebraic[] result = { new Unexakt(-a[1]) };
			return new Vektor(result);
		}else if(coef.length==3){
			return new Vektor(pqsolve(a[1],a[2]));
		}
		int n = coef.length-1;
		double b[] =new double[n+1], c[] =new double[n+1];
		b[0]=c[0]=1.0;
		
		while (n > 2) {
			double r,s,dn,dr,ds,drn,dsn,eps;
			int i,iter;
			r = s = 0;
			dr = 1.0;
			ds = 0;
			eps = 1e-14;
			iter = 1;

			while ((Math.abs(dr)+Math.abs(ds)) > eps) {
				if ((iter % 200) == 0) {
					r=Math.random() * 1000;
				}
				if ((iter % 500) == 0) {
					eps*=10.0;
				}
				b[1] = a[1] - r;
				c[1] = b[1] - r;

				for (i=2;i<=n;i++){
					b[i] = a[i] - r * b[i-1] - s * b[i-2];
					c[i] = b[i] - r * c[i-1] - s * c[i-2];
				}
				dn=c[n-1] * c[n-3] - c[n-2] * c[n-2];
				drn=b[n] * c[n-3] - b[n-1] * c[n-2];
				dsn=b[n-1] * c[n-1] - b[n] * c[n-2];

				if (Math.abs(dn) < 1e-16) {
					dn = 1;
					drn = 1;
					dsn = 1;
				}
				dr = drn / dn;
				ds = dsn / dn;

				r += dr;
				s += ds;
				iter++;
			}
			for (i=0;i<n-1;i++) 
				a[i] = b[i];
			a[n] = s;
			a[n-1] = r;
			n-=2;
		}
		Algebraic result[] = new Algebraic[coef.length-1];
		int k=0;
		for (int i=coef.length-1;i>=2;i-=2) {/* quadratics */
			Algebraic[] lsg = pqsolve(a[i-1],a[i]);
			result[k++] = lsg[0];
			result[k++] = lsg[1];
		}
		if ((n % 2) == 1)
			result[k] = new Unexakt(-a[1]);
		return new Vektor(result);
	}

	
	// solve x^2+px+q=0
	static Zahl[] pqsolve(double p, double q){
		Zahl r[] = new Zahl[2];
		p = -p/2.;
		q = p*p-q;
		if(q>0){
			q = Math.sqrt(q);
			r[0] = new Unexakt(p + q);
			r[1] = new Unexakt(p - q);
		}else if(q<0){
			q = Math.sqrt(-q);
			r[0] = new Unexakt(p, q);
			r[1] = new Unexakt(p, -q);
		}else{
			r[0] = new Unexakt(p);
			r[1] = r[0];
		}
		return r;
	}
				
		
	
		
}			
