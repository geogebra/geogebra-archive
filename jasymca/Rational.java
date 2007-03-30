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

public class Rational extends Algebraic{

	Algebraic  nom; // Zahl oder Polynomial
	Polynomial den;
	
	public Rational(Algebraic nom, Polynomial den) throws JasymcaException{
		Algebraic norm = den.coef[den.degree()];
		if(norm instanceof Zahl){
			this.nom = nom.div(norm);
			this.den = (Polynomial)den.div(norm);
		}else{
			this.nom = nom;
			this.den = den;
		}
	}
	
	public Algebraic reduce() throws JasymcaException{
		if(nom instanceof Zahl)
			return this;
		Algebraic gcd= den.poly_gcd(nom);

		if(!gcd.equals(Zahl.ONE)){
			Algebraic n = ((Polynomial)nom).polydiv(gcd);
			Algebraic d = den.polydiv(gcd);
			if(d.equals(Zahl.ONE))
				return n;
			else if(d instanceof Zahl)
				return n.div(d);
			else
				return new Rational(n,(Polynomial)d);
		}

		return this;
	}
	
			
	
	public Algebraic add(Algebraic x) throws JasymcaException{
		if(x instanceof Rational)
			return nom.mult(((Rational)x).den).add(((Rational)x).
					nom.mult(den)).div(den.mult(((Rational)x).den)).reduce();
		else{
			return nom.add(x.mult(den)).div(den).reduce();
		}
	}
	
	public Algebraic sub(Algebraic x) throws JasymcaException{
		if(x instanceof Rational)
			return nom.mult(((Rational)x).den).sub(((Rational)x).
					nom.mult(den)).div(den.mult(((Rational)x).den)).reduce();
		else
			return nom.sub(x.mult(den)).div(den).reduce();
	}
	
	public Algebraic mult(Algebraic x) throws JasymcaException{
		if(x instanceof Rational)
			return nom.mult(((Rational)x).nom).div(den.mult(((Rational)x).den)).reduce();
		else
			return nom.mult(x).div(den).reduce();
	}
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		if(x instanceof Rational)
			return nom.mult(((Rational)x).den).div(den.mult(((Rational)x).nom)).reduce();
		else
			return nom.div(den.mult(x)).reduce();
	}

	// Divide 2 Polynomials with rest	
	public Algebraic[] div( Algebraic q1, Algebraic[] result) throws JasymcaException{
		result = nom.div(q1,result);
		result[0] = result[0].div(den);
		result[1] = result[1].div(den);
		return result;
	}

	// toString() changed by Markus Hohenwarter, March 30, 2007
	// old code missed parentheses around denominator
	//	public String toString(){
	//		return nom + "/" + den;			
	//	}
	
	public String toString(){
		return nom + "/(" + den + ")";			
	}
	
	public boolean equals(Object x){
		return x instanceof Rational && ((Rational)x).nom.equals(nom) 
									 && ((Rational)x).den.equals(den);
	}
	
	public Algebraic deriv( Variable var ) throws JasymcaException{
		return nom.deriv(var).mult(den).sub(den.deriv(var).mult(nom)).div(den.mult(den)).reduce();
	}


	public Algebraic integrate( Variable var ) throws JasymcaException{
		if(!den.depends(var))
			return nom.integrate(var).div(den);
		// Try f'/f
		Algebraic quot = den.deriv(var).div(nom);
		if(quot.deriv(var).equals(Zahl.ZERO)){
			// J.Puettschneider sei Dank
			return FunctionVariable.create("log",den).div(quot);
//			return FunctionVariable.create("log",den).mult(quot);
		}
		Algebraic q[] = nom.div(den,null);
		if(!q[0].equals(Zahl.ZERO))
			return q[0].integrate(var).add(q[1].div(den).integrate(var));
		// Constant coefficients
		if((nom instanceof Zahl || ((Polynomial)nom).constcoef(var)) && den.constcoef(var)){
			Algebraic r = Zahl.ZERO;
			Vektor h = horowitz(nom,den,var);
			if(h.coord[0] instanceof Rational)  // Square part
				r = r.add(h.coord[0]);
			if(h.coord[1] instanceof Rational)  // Squarefree part
				r = r.add( new TrigInverseExpand().f_exakt(((Rational)h.coord[1]).intrat(var) ));
			return r;
		}
		throw new JasymcaException("Could not integrate Function "+this);
	}

	
	public double norm(){
		return nom.norm()/den.norm();
	}
	
	public Object toPrefix(){ 
		Object r = null;
		r = Lisp.cons(den.toPrefix(),r);
		r = Lisp.cons(nom.toPrefix(),r);
		r = Lisp.cons("/",r);
		return r;		
	}

	public Algebraic cc() throws JasymcaException{
		return nom.cc().div(den.cc());
	}


	
	public boolean depends(Variable var){
		return nom.depends(var) || den.depends(var);
	}
	// Loke depends, but does not recurse into functions
	public boolean depdir(Variable var){ 
		return nom.depdir(var) || den.depdir(var);
	}

	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		return nom.value(var,x).div(den.value(var,x));
	}

		
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		return f.f_exakt(nom).div(f.f_exakt(den));
	}


	// Int p/q = c/d + Int a/b
	// return Vektor[ c/d, a/b ]
	public static Vektor horowitz(Algebraic p, Polynomial q, Variable x) throws JasymcaException{
		if(p.degree(x)>=q.degree(x))
			throw new JasymcaException("Degree of p must be smaller than degree of q");
		p= p.rat(); q = (Polynomial)q.rat();
		Algebraic d = q.poly_gcd(q.deriv(x));
		Algebraic b = q.div(d,null)[0];
		int m = b instanceof Polynomial? ((Polynomial)b).degree():0;
		int n = d instanceof Polynomial? ((Polynomial)d).degree():0;
		SimpleVariable a[] = new SimpleVariable[m];
		Polynomial X = new Polynomial(x);
		Algebraic A = Zahl.ZERO;
		for(int i=a.length-1; i>=0; i--){
			a[i] = new SimpleVariable("a"+i);
			A=A.add(new Polynomial(a[i]));
			if(i>0) A = A.mult(X);
		}
			
		SimpleVariable c[] = new SimpleVariable[n];
		Algebraic C = Zahl.ZERO;
		for(int i=c.length-1; i>=0; i--){
			c[i] = new SimpleVariable("c"+i);
			C=C.add(new Polynomial(c[i]));
			if(i>0) C = C.mult(X);
		}		
		Algebraic r = b.mult(C.deriv(x)).sub(C.mult(b).mult(d.deriv(x)).div(d,null)[0]).add(d.mult(A));		
		Algebraic aik[][] = new Algebraic[m+n][m+n];
		Algebraic cf, co[] = new Algebraic[m+n];
		for(int i=0; i<m+n; i++){
			co[i] 	= p.coefficient(x,i);
			cf 		= r.coefficient(x,i);
			for(int k=0; k<m; k++){
				aik[i][k] =  cf.deriv(a[k]);
			}
			for(int k=0; k<n; k++){
				aik[i][k+m]=  cf.deriv(c[k]);
			}
		}
		Vektor s = LambdaLINSOLVE.Gauss(new Matrix(aik), new Vektor(co));
		// s = [ a(0)...a(m-1) c(0 ... c(n-1) ]
		A = Zahl.ZERO;
		for(int i=m-1; i>=0; i--){
			A=A.add(s.coord[i]);
			if(i>0) A = A.mult(X);
		}
		C = Zahl.ZERO;
		for(int i=n-1; i>=0; i--){
			C=C.add(s.coord[i+m]);
			if(i>0) C = C.mult(X);
		}
	  	co = new Algebraic[2];
		co[0] = C.div(d); 
		co[1] = A.div(b);
		return new Vektor(co);
	}	

	// this muﬂ echt gebrochen rational mit Nenner squarefree sein
	Algebraic intrat(Variable x) throws JasymcaException{
		// Wir benutzen: Residue(f/g (a)) = (x-a)*(f/g)(a) = f(a)/g'(a)
		Algebraic de = den.deriv(x);
		if(de instanceof Zahl){ // trivial case
			return makelog(nom.div(de), x, den.coef[0].mult(Zahl.MINUS).div(de));
		}
		Algebraic  r  = nom.div(de);
		Vektor     xi = den.monic().bairstow();
		Algebraic   rs = Zahl.ZERO;

		for(int i=0; i<xi.coord.length; i++){
			Algebraic c = r.value(x,xi.coord[i]);
			rs = rs.add(makelog(c,x,xi.coord[i]));
		}
		return rs;
	}
	
	// Create the function c*log(x-a)
	Algebraic makelog(Algebraic c, Variable x, Algebraic a) throws JasymcaException{
		Algebraic arg = new Polynomial(x).sub(a);
		return FunctionVariable.create("log", arg).mult(c);
	}
				
				

}	
	
