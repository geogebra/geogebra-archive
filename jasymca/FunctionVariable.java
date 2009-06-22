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

public class FunctionVariable extends Variable{
	public String fname;
	public Algebraic arg;
	public LambdaAlgebraic la;
	
	public FunctionVariable( String fname, Algebraic arg, LambdaAlgebraic la){
		this.fname 	= fname;
		this.arg 	= arg;
		this.la 	= la;
	}
	public Algebraic deriv( Variable x ) throws JasymcaException{ 
		if(equals(x))
			return Zahl.ONE;
		if(!arg.depends(x))
			return Zahl.ZERO;
		if(la==null)
			throw new JasymcaException("Can not differentiate "+fname);
		if(la instanceof UserFunction){
			UserFunction uf = (UserFunction)la;
			// Einsetzen von arg in body, dann ableiten
			if(uf.var.length == 1)
				return uf.body.value(uf.var[0], arg).deriv(x);
			if(arg instanceof Vektor && ((Vektor)arg).dim()==uf.var.length){
				return uf.fv((Vektor)arg).deriv(x);
			}
			throw new JasymcaException("Wrong argument to function "+uf.fname);
		}

		// Apply diffrule to arg
		String diffrule = la.diffrule;
		if(diffrule==null)
			throw new JasymcaException("Can not differentiate "+fname);
		try{
			Object prefix = Lisp.change( Lisp.compile_rule(diffrule),
						Lisp.list(Lisp.cons("x", arg)) );
			// Multiply with diff(arg)
			return ((Algebraic)Jasymca.evalPrefix(prefix,true,Lambda.env)).mult(arg.deriv(x)); // changed to static reference Michael Borcherds 2008-04-06
		}catch(ParseException p){
			throw new JasymcaException(p.toString());
		}
	}
	
	public Algebraic integrate( Variable x ) throws JasymcaException{
		arg = arg.reduce();
		if(la==null)
			throw new JasymcaException("Can not integrate "+fname);
		return la.integrate(arg, x);
	}
	
	// return f(arg), evaluate as much as possible
	public static Algebraic create(String f, Algebraic arg) throws JasymcaException{
		arg = arg.reduce();
		Object fl = Lambda.env.getValue(f);
		if(fl!=null && fl instanceof LambdaAlgebraic ){
			Algebraic r = ((LambdaAlgebraic)fl).f_exakt(arg);
			if(r != null)
				return r;
			if(arg instanceof Unexakt){ // Evaluate function
				return ((LambdaAlgebraic)fl).f((Zahl)arg);
			}
		}else
			fl = null;
		return new Polynomial(new FunctionVariable(f,arg,(LambdaAlgebraic)fl));
	}

		
	public boolean equals( Object x ){ 
		return x instanceof FunctionVariable &&
				fname.equals(((FunctionVariable)x).fname) &&
				 arg.equals(((FunctionVariable)x).arg);
	}
	
	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		if(equals(var))
			return x;
		else
			return create(fname, arg.value(var, x));
	}

	
	public Object toPrefix(){
		return Lisp.cons( fname, Lisp.list(arg.toPrefix()) );
	}

	public boolean smaller(Variable v)  throws JasymcaException{
		if(v==SimpleVariable.top)
			return true;
		if(v instanceof SimpleVariable)
			return false; // All Function-Variables are larger
		if(!((FunctionVariable)v).fname.equals(fname))
			return fname.compareTo(((FunctionVariable)v).fname) < 0;
		if(arg.equals(((FunctionVariable)v).arg))
			return false;
		if(arg instanceof Polynomial && ((FunctionVariable)v).arg instanceof Polynomial){
			Polynomial a = (Polynomial)arg;
			Polynomial b = (Polynomial)((FunctionVariable)v).arg;
			if(!a.var.equals(b.var))
				return a.var.smaller(b.var);
			if(a.degree() != b.degree())
				return a.degree() < b.degree();
			for(int i= a.coef.length-1; i>=0; i--){
				if(!a.coef[i].equals(b.coef[i])){
					if(a.coef[i] instanceof Zahl && b.coef[i] instanceof Zahl)
						return ((Zahl)a.coef[i]).smaller((Zahl)b.coef[i]);
					return a.coef[i].norm()<b.coef[i].norm();
				}
			}
		}
		return false;		
	}
	
	public Variable cc() throws JasymcaException{
		if(fname.equals("exp") || fname.equals("log") || fname.equals("sqrt"))
			return new FunctionVariable(fname, arg.cc(),la);
		throw new  JasymcaException("Can't calculate cc for Function "+fname);
	}
		
	public String toString(){
		String a = arg.toString();
		// Michael Borcherds 2008-11-25
		// removed these lines, as they don't work
		// eg differentiate sin( (x-1) log(x) )
		// eg differentiate x^(x-1)
		//if(a.startsWith("(") && a.endsWith(")"))
		//	return fname+a;
		//else
			return fname+"("+a+")";
	}

}
