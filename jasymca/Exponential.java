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

import geogebra.Application;

import java.util.Vector;

public class Exponential extends Polynomial{
	public Variable 	expvar;
	public Algebraic     exp_b; 
	
	public Exponential(Algebraic a,Algebraic c, Variable x, Algebraic b){
		this.coef = new Algebraic[2];
		this.coef[0] = c;
		this.coef[1] = a;
		Algebraic[] z  = new Algebraic[2];
		z[0] = Zahl.ZERO;
		z[1] = b;
		Object la = Lambda.env.getValue("exp");
		if(!(la instanceof LambdaEXP))
			la = new LambdaEXP();
		this.var = new FunctionVariable("exp", new Polynomial(x, z),(LambdaAlgebraic)la);
		this.expvar = x;
		this.exp_b  = b;
	}
	
	// Only for casting
	public Exponential(Polynomial x){
		super(x.var, x.coef);
		this.expvar = ((Polynomial)((FunctionVariable)this.var).arg).var;
		this.exp_b  = ((Polynomial)((FunctionVariable)this.var).arg).coef[1];
		
		// TODO: remove
		Application.debug("poly: " + x + " to exponential: " + this);
	}
	
	// return x as exponential if it fits
	public static Algebraic poly2exp(Algebraic x){
		if(x instanceof Exponential) return x;
		if(x instanceof Polynomial && ((Polynomial)x).degree()==1 
			&& ((Polynomial)x).var instanceof FunctionVariable
			&& ((FunctionVariable)(((Polynomial)x).var)).fname.equals("exp")){
			Algebraic arg = ((FunctionVariable)(((Polynomial)x).var)).arg;
			if(arg instanceof Polynomial && ((Polynomial)arg).degree()==1 
			&& ((Polynomial)arg).coef[0].equals(Zahl.ZERO)){
				return new Exponential((Polynomial)x);
			}
		}
		return x;
	}

	
//	public String toString(){ return "$"+super.toString()+"$"; }
	
	public Algebraic cc() throws JasymcaException{
		return new Exponential( coef[1].cc(), coef[0].cc(), expvar, exp_b.mult(Zahl.MINUS) );
	}

				
	// True if expression contains exponentials
	static boolean containsexp(Algebraic x) throws JasymcaException{
		if(x instanceof Zahl) return false;
		if(x instanceof Exponential) return true;
		if(x instanceof Polynomial){
			for(int i=0; i<((Polynomial)x).coef.length; i++)
				if( containsexp( ((Polynomial)x).coef[i] ))
					return true;
			if( ((Polynomial)x).var instanceof FunctionVariable ){
				return containsexp( ((FunctionVariable)((Polynomial)x).var).arg );
			}
			return false;
		}
		if(x instanceof Rational)
			return containsexp(((Rational)x).nom) || containsexp(((Rational)x).den);
		if(x instanceof Vektor){
			for(int i=0; i<((Vektor)x).coord.length; i++)
				if( containsexp( ((Vektor)x).coord[i] ))
					return true;
			return false;
		}
		throw new JasymcaException("containsexp not suitable for x");
	}
						
			
	// Exponentials a*exp(b*x)+c
	public Algebraic add(Algebraic x) throws JasymcaException{
		if(x instanceof Zahl)
			return new Exponential( coef[1], x.add(coef[0]), expvar, exp_b );
		if(x instanceof Exponential){
			if(var.smaller(((Exponential)x).var))
				return x.add(this);
			return new Exponential( coef[1], x.add(coef[0]), expvar, exp_b );
		}
		return poly2exp(super.add(x));
	}
	
	public Algebraic mult(Algebraic x) throws JasymcaException{
		if(x.equals(Zahl.ZERO)) return x;
		if(x instanceof Zahl){			
			return new Exponential( coef[1].mult(x), coef[0].mult(x), expvar, exp_b );
		}
		if(x instanceof Exponential && expvar.equals(((Exponential)x).expvar) ){
			// (a*exp(bx)+c)*(d*exp(ex)+f) --> ad*exp((b+d)x) + cd*exp(ex) + af*exp(bx) +cf
			Exponential xp = (Exponential)x;
			Algebraic r = Zahl.ZERO;			
			// ad*exp((b+d)x)
			Algebraic nex = exp_b.add(xp.exp_b);
			if(nex.equals(Zahl.ZERO))
				r = coef[1].mult(xp.coef[1]);
			else{
				r = new Exponential( coef[1].mult(xp.coef[1]),Zahl.ZERO,expvar, nex); 
			}
			// c * (d*exp(ex)+f)
			r = r.add( coef[0].mult(xp) );
			// f * (a*exp(bx)+c)
			r = r.add( mult(xp.coef[0] ));
			r = r.reduce();
			return r;
		}
		return poly2exp(super.mult(x));
	}
	
	public Algebraic reduce() throws JasymcaException{
		if( coef[1].reduce().equals(Zahl.ZERO) )
			return coef[0].reduce();
		if( exp_b.equals(Zahl.ZERO) ){
			return coef[0].add(coef[1]).reduce();
		}
		return this;
	}	
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		if(x instanceof Zahl){
			return new Exponential((Polynomial)super.div(x));
		}
		return super.div(x);
	}
	
		// Map f to coefficients and arg
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		return poly2exp(super.map(f));
	}

}
	
////////////////// Hyperbolic Conversions ////////////////////////////


	// Try to convert to normalized exponentials
class NormExp extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic f) throws JasymcaException{ 
		if(f instanceof Rational){
			Algebraic nom = f_exakt(((Rational)f).nom);
			Algebraic den = f_exakt(((Rational)f).den);
			if( den instanceof Zahl)
				return f_exakt(nom.div(den));
			if(den instanceof Exponential  &&  
					((Polynomial)den).coef[0].equals(Zahl.ZERO) &&
					((Polynomial)den).coef[1] instanceof Zahl ){
			// Convert p/(exp(x)) to p*exp(-x)
				if(nom instanceof Zahl || nom instanceof Exponential){
					Exponential denx = (Exponential)den;
					Exponential den_inv = new Exponential(Zahl.ONE.div(denx.coef[1]),Zahl.ZERO, 
											denx.expvar, denx.exp_b.mult(Zahl.MINUS));
					return nom.mult(den_inv);
				}
			}
			return nom.div(den);
		}
		if(f instanceof Exponential)
			return f.map(this);
		if(!(f instanceof Polynomial))
			return f.map(this);
		Polynomial fp = (Polynomial)f;
		if(!(fp.var instanceof FunctionVariable) || !((FunctionVariable)fp.var).fname.equals("exp"))
			return f.map(this);
		Algebraic arg = ((FunctionVariable)fp.var).arg.reduce();
		if(arg instanceof Zahl)
			return  fp.value( FunctionVariable.create("exp",arg) ).map(this);
		if(!(arg instanceof Polynomial) || !(((Polynomial)arg).degree()==1) )
			return f.map(this);
		// Can be normalized 
		// Convert coef[i]*(exp(ax+b))^i ----> (coef[i]*exp(bi)) * exp(aix)
		Algebraic r = Zahl.ZERO;
		Algebraic a = ((Polynomial)arg).coef[1];
		for(int i=1; i<fp.coef.length; i++){
			Algebraic b = ((Polynomial)arg).coef[0];
			Zahl I = new Unexakt((double)i);
			// Try to further reduce b = dy+e etc
			// exp(bi) = exp(diy)*exp(id)
			Algebraic ebi = Zahl.ONE;
			while(b instanceof Polynomial && ((Polynomial)b).degree()==1){
				Algebraic f1 = FunctionVariable.create( "exp",
									new Polynomial( ((Polynomial)b).var).mult(
										((Polynomial)b).coef[1].mult(I)));
				f1 = Exponential.poly2exp(f1);
				ebi = ebi.mult(f1);
				b = ((Polynomial)b).coef[0];
			}
			ebi = ebi.mult( FunctionVariable.create("exp", b.mult(I)));						
			Algebraic cf = f_exakt(fp.coef[i].mult( ebi ));
			Algebraic f2 = FunctionVariable.create( "exp", 
									new Polynomial( ((Polynomial)arg).var).mult(
										a.mult(I)));
			f2 = Exponential.poly2exp(f2);
			r = r.add(cf.mult(f2));
		}
		if(fp.coef.length>0)
			r = r.add(f_exakt(fp.coef[0]));
		return Exponential.poly2exp(r); 
	}
}



// Collect exponentials: exp(2ax)+exp(ax) --> (exp(ax)^2+exp(ax))
class CollectExp extends LambdaAlgebraic{
	Vector v;
	public CollectExp(Algebraic f) throws JasymcaException{
		v = new Vector();
		// Get a list of variables
		new GetExpVars(v).f_exakt(f); 
	}

	Algebraic f_exakt(Algebraic x1) throws JasymcaException{ 
		if(v.size()==0) return x1;
		if( !(x1 instanceof Exponential) )
			return x1.map(this);
		Exponential e = (Exponential)x1;
		// Find largest multiple
		int exp = 1; 
		Algebraic exp_b = e.exp_b;
		if(exp_b instanceof Zahl && ((Zahl)exp_b).smaller(Zahl.ZERO)){
			exp *= -1;
			exp_b = exp_b.mult(Zahl.MINUS);
		}
		Variable x = e.expvar;
		for(int i=0; i<v.size(); i++){
			Polynomial y = (Polynomial)v.elementAt(i);
			if(y.var.equals(x)){
				Algebraic rat = exp_b.div(y.coef[1]);
				if(rat instanceof Zahl && !((Zahl)rat).komplexq() ){
					int cfs = cfs( ((Zahl)rat).unexakt().real );
					if(cfs != 0 && cfs!=1){
						exp *= cfs;
						exp_b = exp_b.div(new Unexakt((double)cfs));
					}
				}
			}
		}
		Algebraic p = new Polynomial(x).mult(exp_b);
		p = FunctionVariable.create("exp",p).pow_n(exp);
		return p.mult(f_exakt(e.coef[1])).add(f_exakt(e.coef[0]));
		
	}
	
	//  x = a/b, return a
	// calculates cfs first approximation
	// result is non-zero only if approximation better than tol
	int cfs(double x){
		if(x<0) return cfs(-x);
		int a0 = (int)Math.floor(x);
		if(x==(double)a0)
			return a0;
		int a1 = (int)Math.floor(1./(x-a0));
		int z = a0*a1+1;
		if(Math.abs((double)z/(double)a1-x) < 1.e-6)
			return z;
		return 0;
	}
	
}

// Find all  exponentials: exp(ax), exp(bx) ...., return in Vector
class GetExpVars extends LambdaAlgebraic{
	Vector v;
	public GetExpVars(Vector v){
		this.v = v;
	}

	Algebraic f_exakt(Algebraic f) throws JasymcaException{ 
		if(f instanceof Exponential){
			Algebraic x = new Polynomial(((Exponential)f).expvar);
			x = x.mult(((Exponential)f).exp_b);
			v.addElement(x);
			f_exakt(((Exponential)f).coef[1]);
			f_exakt(((Exponential)f).coef[0]);
			return Zahl.ONE; // dummy
		}
		return f.map(this);
	}
}


// Eliminate all exponentials
class DeExp extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic f) throws JasymcaException{ 
		if(f instanceof Exponential){
			Exponential x = (Exponential)f;
			Algebraic cn[] = new Algebraic[2];
			cn[0] = f_exakt(x.coef[0]);
			cn[1] = f_exakt(x.coef[1]);
			return new Polynomial(x.var, cn);
		}
		return f.map(this);
	}
}
		


//////// Numeric Hyperbolic Functions ////////////////////////////////////


class LambdaEXP extends LambdaAlgebraic{
	public LambdaEXP(){ diffrule = "exp(x)"; intrule = "exp(x)"; }

	Zahl f( Zahl x){ 
		Unexakt z = x.unexakt();
		double  r = Math.exp(z.real);
		if(z.imag!=0.)
			return new Unexakt( r*Math.cos(z.imag) , r*Math.sin(z.imag) );
		return new Unexakt(r);
	}

	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ONE;
		if(x instanceof Polynomial && ((Polynomial)x).degree()==1 &&
		((Polynomial)x).coef[0].equals(Zahl.ZERO) ){ 
			Polynomial xp = (Polynomial)x;
			if(xp.var instanceof SimpleVariable &&
				((SimpleVariable)xp.var).name.equals("pi")){ // exp(n*i*pi)
				Algebraic q = xp.coef[1].div(Zahl.IONE);
				if(q instanceof Zahl){
					return fzexakt((Zahl)q);
				}
			}		
			if(xp.coef[1] instanceof Zahl &&
				((Zahl)xp.coef[1]).integerq() &&
				xp.var instanceof FunctionVariable &&
				((FunctionVariable)xp.var).fname.equals("log")){ // exp(n*log(x)) --> x^n
					int n = ((Zahl)xp.coef[1]).intval();
					return ((FunctionVariable)xp.var).arg.pow_n(n);
			}
		}
		return null;
	}
	
	// Calculate exp(x*i*pi)
	Algebraic fzexakt(Zahl x) throws JasymcaException{
		if(x.smaller(Zahl.ZERO)){
			Algebraic r = fzexakt((Zahl)x.mult(Zahl.MINUS));
			if(r!=null) return r.cc();
			return r;
		}
		if( x.integerq() ){
			if( x.intval() % 2 == 0 ){ // n even
				return Zahl.ONE;
			}else	// odd
				return Zahl.MINUS;
		}		
		Algebraic qs = x.add(new Unexakt(.5));
		if( ((Zahl)qs).integerq() ){
			if( ((Zahl)qs).intval() % 2 == 0 ){ // n even
				return Zahl.IMINUS;
			}else{	// odd
				return Zahl.IONE;
				}
		}
		qs = x.mult(new Unexakt(4));
		if( ((Zahl)qs).integerq() ){
			Algebraic sq2 = FunctionVariable.create("sqrt",new Unexakt(0.5));
			switch( ((Zahl)qs).intval() % 8 ){
				case 1: return Zahl.ONE.add(Zahl.IONE).div(Zahl.SQRT2);
				case 3: return Zahl.MINUS.add(Zahl.IONE).div(Zahl.SQRT2);
				case 5: return Zahl.MINUS.add(Zahl.IMINUS).div(Zahl.SQRT2);
				case 7: return Zahl.ONE.add(Zahl.IMINUS).div(Zahl.SQRT2);
			}
		}
		qs = x.mult(new Unexakt(6));
		if( ((Zahl)qs).integerq() ){
			switch( ((Zahl)qs).intval() % 12 ){
				case 1: return Zahl.SQRT3.add(Zahl.IONE).div(Zahl.TWO);
				case 2: return Zahl.ONE.add(Zahl.SQRT3.mult(Zahl.IONE)).div(Zahl.TWO);
				case 4: return Zahl.SQRT3.mult(Zahl.IONE).add(Zahl.MINUS).div(Zahl.TWO);
				case 5: return Zahl.IONE.sub(Zahl.SQRT3).div(Zahl.TWO);
				case 7: return Zahl.IMINUS.sub(Zahl.SQRT3).div(Zahl.TWO);
				case 8: return Zahl.SQRT3.mult(Zahl.IMINUS).sub(Zahl.ONE).div(Zahl.TWO);
				case 10:return Zahl.SQRT3.mult(Zahl.IMINUS).add(Zahl.ONE).div(Zahl.TWO);
				case 11:return Zahl.IMINUS.add(Zahl.SQRT3).div(Zahl.TWO);
			}
		}
		return null;
	}
}

class LambdaLOG extends LambdaAlgebraic{
	public LambdaLOG(){ diffrule = "1/x"; intrule = "x*log(x)-x"; }
	Zahl f( Zahl x){ 
		Unexakt z = x.unexakt();
		if(z.real<0 || z.imag != 0.)
			return new Unexakt( StrictMath.log( z.real*z.real+z.imag*z.imag )/2, StrictMath.atan2(z.imag,z.real));
		return new Unexakt(StrictMath.log(z.real)); 
	}
	
	
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ONE))
			return Zahl.ZERO;
		if(x.equals(Zahl.MINUS))
			return Zahl.PI.mult(Zahl.IONE);
		// Trigonometric conversions

		if(x instanceof Polynomial && ((Polynomial)x).degree()==1 &&
		((Polynomial)x).coef[0].equals(Zahl.ZERO) &&
//		((Polynomial)x).coef[1].equals(Zahl.ONE) &&
		((Polynomial)x).var instanceof FunctionVariable &&
			((FunctionVariable)((Polynomial)x).var).fname.equals("exp"))
			return ((FunctionVariable)((Polynomial)x).var).arg.add
				(FunctionVariable.create("log",((Polynomial)x).coef[1]));
		return null;
	}
	
			

}

class Pow extends LambdaAlgebraic{
	public Object lambda(Object x) throws JasymcaException{
		Object base = car(x);
		Object exp  = car(cdr(x));
		if(base==null || exp==null || !(base instanceof Algebraic)
			|| !(exp instanceof Algebraic))
			throw new JasymcaException("Wrong arguments to function pow.");
		if(exp instanceof Zahl && ((Zahl)exp).integerq())
			return ((Algebraic)base).pow_n((int) ((Zahl)exp).intval());
		return FunctionVariable.create("exp",FunctionVariable.create
									("log",(Algebraic)base).mult((Algebraic)exp));
	}
}


