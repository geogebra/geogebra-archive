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

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;


// Definitions for function pointers


// Lambda is a general function pointer
public abstract class Lambda extends Lisp{
	// This will be non-static some day...
	static Environment env;
	public Object lambda(Object x) throws ParseException, JasymcaException{ return x; }
	
	// Return comma-separated list as java.util.Vector
	// used for Vektors: [a,b,c,d] and multivaraiate operators diff(f(x),x) etc.
	static Vector getArgs(Object args) throws ParseException{
		Vector v = new Vector();
		while(args instanceof Pair && car(args).equals(",")){
			if(!car(args).equals(","))
				throw new ParseException("Variables must be separated by \",\" .");
			v.insertElementAt(car(cdr(cdr(args))), 0);
			args = car(cdr(args));
		}
		v.insertElementAt(args,0);
		return v;
	}

}

// LambdaAlgebraic has functions for Algebraic variables
// This class is used for numeric functions (sin,exp, etc)
// for operators (+,-,..) and for many internal functions
abstract class LambdaAlgebraic extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic arg = ((Algebraic)car(x)).reduce();
		if(arg instanceof Unexakt) return f((Zahl)arg);
		return FunctionVariable.create(
				getClass().getName().substring("jasymca.Lambda".length()).toLowerCase(Locale.US), arg);
	}	
	Zahl f(Zahl x) throws JasymcaException{ return x;}
	
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ return null;}

	String diffrule=null, intrule=null, trigrule = null;
	
	public Algebraic integrate(Algebraic arg, Variable x) throws JasymcaException{ 
		if(!(arg.depends(x)))
			throw new JasymcaException("Expression in function does not depend on Variable.");
		if( !(arg instanceof Polynomial) || ((Polynomial)arg).degree()!=1 ||
			!((Polynomial)arg).constcoef(x) || intrule==null)
			throw new JasymcaException("Can not integrate function ");
		try{
			Object prefix = change(compile_rule(intrule), list(cons("x", arg)) );
			// * dx/dz <----> /coef[1]
			return ((Algebraic)Jasymca.evalPrefix(prefix,true,env)).div(((Polynomial)arg).coef[1]);
		}catch(Exception e){
				throw new JasymcaException("Error integrating function");
		}
	}	
}

// Assignment     x:3; --> assign value 3 to variable x
// assigning "null" deletes the variable
class Assign extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		// Variable definition, store in Prefix format and return success
		if(car(x) instanceof String && cdr(x) != null){
			Object val = Jasymca.evalPrefix(car(cdr(x)), true,env);
			env.putValue((String)car(x), val);
            return val;
		}else
			throw new ParseException("Wrong usage of Assignment operator :");
		}
}

// Define Function. Example f(x):=3*x^2+2;
// Functions are stored as evaluated (compiled) Algebraics 
// Functions can be differentiated, integrated or used in symbolic equations
class FunctionDefine extends Lambda{
	public Object lambda(Object x) throws ParseException,JasymcaException{ 
		// Function definition
		// car is name and Variable
		Object fname = car(car(x));
		Object vars  = car(cdr(car(x)));
		Object code  = car(cdr(x));	
		if(fname==null || vars==null  || code==null || !(fname instanceof String))
			throw new ParseException("Wrong function definition.");
		env.putValue((String)fname, new UserFunction((String)fname, vars, code, env));
		return new Polynomial( new SimpleVariable((String)fname));
	}
}

// User defined functions 
class UserFunction extends LambdaAlgebraic{
	String fname;
	Algebraic body;
	Variable[] var;

	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic arg = (Algebraic)car(x);
		if(var.length==1 && arg instanceof Zahl) 
			return f((Zahl)arg);
		if(arg instanceof Vektor && ((Vektor)arg).dim()==var.length && ((Vektor)arg).number()) 
			return fv((Vektor)arg);
		return FunctionVariable.create(fname, arg);
	}	
	
	public UserFunction() throws ParseException,JasymcaException {}
	
	public UserFunction(String fname, Object var, Object code, Environment env) 
				throws ParseException,JasymcaException{
		body = (Algebraic)Jasymca.evalPrefix(code, true,env);
		// Transform variable names
		if(var instanceof String){
			this.var 	= new Variable[1];
			this.var[0] = new SimpleVariable((String)var+this.toString());
			body = body.value(new SimpleVariable((String)var),
						new Polynomial(this.var[0])); 
		}else{ // Multivariate function
			Vector v = getArgs(var);
			if(v.size()==0)
				throw new ParseException("Function without variable.");
			this.var = new Variable[v.size()];
			for(int i=0; i<this.var.length; i++){
				Object s = v.elementAt(i);
				if(!(s instanceof String))
					throw new ParseException("Wrong format for Functiondefinition.");					
				this.var[i] = new SimpleVariable((String)s+this.toString());
				body = body.value(new SimpleVariable((String)s),
						new Polynomial(this.var[i])); 
			}
		}
		this.fname = fname;
		Lambda.env = env; // changed to static reference Michael Borcherds 2008-04-06
	}

	Zahl f(Zahl x) throws JasymcaException{ 
		Algebraic y = body.value(var[0], x);
		if(y instanceof Zahl)
			return (Zahl)y;
		throw new JasymcaException("Can not evaluate Function "+fname+" to number, got "+y+" for "+x);
	}

	Algebraic fv(Vektor x) throws JasymcaException{
		Algebraic r = body;
		for(int i=0; i<var.length; i++){
			r = r.value(var[i], x.coord[i]);
		}
		return r;			 
	}

	public Algebraic integrate(Algebraic arg, Variable x) throws JasymcaException{ 
		if(!(arg.depends(x)))
			throw new JasymcaException("Expression in function does not depend on Variable.");
		if(var.length == 1)
			return body.value(var[0], arg).integrate(x);
		if(arg instanceof Vektor && ((Vektor)arg).dim()==var.length)
			return fv((Vektor)arg).integrate(x);
		throw new JasymcaException("Wrong argument to function "+fname);
	}
}
		

// Evaluate list of expressions
class Comma extends LambdaAlgebraic{
	public Object lambda(Object x)  throws ParseException, JasymcaException{ 
		switch(length(x)){
			case 1: return car(x);
			case 2: 
				Algebraic x1 = (Algebraic)car(x);
				Algebraic x2 = (Algebraic)car(cdr(x));
				if(x1 instanceof Vektor){
					Algebraic cn[] = new Algebraic[((Vektor)x1).coord.length+1];
					cn[cn.length-1] = x2;
					for(int i=0; i<((Vektor)x1).coord.length; i++)
						cn[i] = ((Vektor)x1).coord[i];
					return new Vektor(cn);
				}
				Algebraic cn[] = new Algebraic[2];
				cn[0] = x1; cn[1] = x2;
				return new Vektor(cn);
		}
		throw new ParseException("Wrong number of Arguments to \",\".");
	}
}




// Convert to floating point number as much as 
// possible, use ALGEPSILON to reduce numbers
class LambdaFLOAT extends LambdaAlgebraic{
	double eps = 1.e-8;
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Algebraic exp = (Algebraic)Jasymca.evalPrefix(car(x), true,env);
		Zahl a = env.getnum("algepsilon");
		if(a !=null){
			double epstry = a.unexakt().real;
			if(epstry>0) eps = epstry;
		}
		// Eval constants to numbers (pi etc)
		exp = new ExpandConstants().f_exakt(exp);
		return exp.map(this);
	}
	Zahl f(Zahl x) throws JasymcaException{ 
		Unexakt f = x.unexakt();
		if(f.equals(Zahl.ZERO))
			return f;
		double abs = ((Unexakt)f.abs()).real;
		double r = f.real;
		if( Math.abs(r/abs)<eps ) r = 0.;
		double i = f.imag;
		if( Math.abs(i/abs)<eps ) i = 0.;
		return new Unexakt(r,i);
	}

	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		return x.map(this);
	}
}


// Operator for '[' symbol
class CreateVector extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		// Variable definition, store in Prefix format and return success
		Vector args = getArgs(car(x));
		Algebraic[] coord = new Algebraic[args.size()];
		for(int i=0; i<coord.length; i++)
			coord[i] = (Algebraic)Jasymca.evalPrefix(args.elementAt(i), true, env); 
		return new Vektor(coord);
	}
}

// GCD for numbers and polynomials
class LambdaGCD extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		// Variable definition, store in Prefix format and return success
		Vector args = getArgs(car(x));
		if(args.size()<2)
			throw new ParseException("GCD requires at least 2 arguments.");
		Algebraic gcd = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		for(int i=1; i<args.size(); i++){
			gcd = gcd(gcd,  (Algebraic)Jasymca.evalPrefix(args.elementAt(i), true, env));
		}
		return gcd;
	}
	
	Algebraic gcd(Algebraic x, Algebraic y) throws JasymcaException{ 
		if(x instanceof Zahl && y instanceof Zahl){
			return  ((Zahl)x).gcd((Zahl)y);
		}
		if(x instanceof Polynomial){
			if(y instanceof Polynomial){ // poly_gcd * coef_gcd
				Algebraic gcd=((Polynomial)x).coef[0];
				for(int i=1; i<((Polynomial)x).coef.length; i++)
					gcd = gcd(gcd, ((Polynomial)x).coef[i]);
				for(int i=0; i<((Polynomial)y).coef.length; i++)
					gcd = gcd(gcd, ((Polynomial)y).coef[i]);
				return x.poly_gcd(y).mult(gcd);
			}if(y instanceof Zahl){
				Algebraic gcd=y;
				for(int i=0; i<((Polynomial)x).coef.length; i++)
					gcd = gcd(gcd, ((Polynomial)x).coef[i]);
				return gcd;
			}
		}
		if(y instanceof Polynomial && x instanceof Zahl)
			return gcd(y,x);
		throw new JasymcaException("Not implemented.");
	}
}

class LambdaEXIT extends Lambda{
	public Object lambda(Object x){ return Jasymca.EXIT; }
}
	
class LambdaEXPAND extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		return new SqrtExpand().f_exakt((Algebraic)Jasymca.evalPrefix(car(x), true, env)); 
	}
}
	
class LambdaREALPART extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		return ((Algebraic)Jasymca.evalPrefix(car(x), true,env)).realpart(); 
	}
}
	
class LambdaIMAGPART extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		return ((Algebraic)Jasymca.evalPrefix(car(x), true,env)).imagpart(); 
	}
}
	

// Continued fraction expansion
class LambdaCFS extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		Algebraic y = ((Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env)).rat(); 
		if(!(y instanceof Exakt))
			throw new ParseException("Argument must be Number");
		double eps = 1.e-5;
		if(args.size()>1)
			eps = ((Zahl)Jasymca.evalPrefix(args.elementAt(1), true, env)).unexakt().real;
		return ((Exakt)y).cfs(eps);		
	}
}
	
class LambdaDIFF extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()==0)
			throw new ParseException("Argument to diff missing.");
		Object f = Jasymca.evalPrefix(args.elementAt(0), true, env); // Function to differentiate
		if(!(f instanceof Algebraic))
			throw new ParseException("First argument to diff must be Algebraic.");			
		Object v;
		if(args.size()>1){
			v = args.elementAt(1); // Variable
			v = Jasymca.evalPrefix(v, true, env);
			if(! (v instanceof Polynomial) )
				throw new ParseException("Second argument to diff must be Polynomial.");
			v = ((Polynomial)v).var;
		}else{
			if( f instanceof Polynomial )
				v = ((Polynomial)f).var;
			else if(f instanceof Rational )
				v = ((Rational)f).den.var;
			else
				throw new ParseException("Could not determine Variable.");
		}
		return ((Algebraic)f).deriv((Variable)v);
	}
}

// SUBST (a, b, c), substitutes a for b in c
// if c evaluates to a prefix expressions, substitute using parser
// if c evaluates to Algebraic and b is SimpleVariable, substitute using value()
// if c evaluates to Algebraic and b is not SimpleVariable, 
// convert c and b to prefix and substitute using parser
class LambdaSUBST extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=3)
			throw new ParseException("Usage: SUBST (a, b, c), substitutes a for b in c");
		Object c = Jasymca.evalPrefix(args.elementAt(2), false, env);
		Object b = Jasymca.evalPrefix(args.elementAt(1), false, env);
		if(c instanceof Algebraic){
			Algebraic a = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
			if(b instanceof String){
				SimpleVariable bv = new SimpleVariable((String)b);
				return ((Algebraic)c).value(bv,a);
			}
			c = ((Algebraic)c).toPrefix();
			// b should be converted to "normalized prefix"
			b = Jasymca.evalPrefix(args.elementAt(1), true, env);
			b = ((Algebraic)b).toPrefix();
		}
		Object a = Jasymca.evalPrefix(args.elementAt(0), false, env);
		Object r = change( c, list(cons(b,a)) );
		return Jasymca.evalPrefix(r,true,env);
	}
}

// SUM (exp, ind, lo, hi)
class LambdaSUM extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=4)
			throw new ParseException("Usage: SUM (exp, ind, lo, hi)");
		Algebraic exp = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		Algebraic ind = (Algebraic)Jasymca.evalPrefix(args.elementAt(1), true, env);
		Algebraic lo = (Algebraic)Jasymca.evalPrefix(args.elementAt(2), true, env);
		Algebraic hi = (Algebraic)Jasymca.evalPrefix(args.elementAt(3), true, env);
		if(!(ind instanceof Polynomial) || !(lo instanceof Zahl) || !(hi instanceof Zahl))
			throw new ParseException("Usage: SUM (exp, ind, lo, hi)");
		Variable v = ((Polynomial)ind).var;
		Algebraic sum = Zahl.ZERO;
		for( ; !((Zahl)hi).smaller((Zahl)lo); lo= lo.add(Zahl.ONE))		
			sum = sum.add(exp.value(v,lo));
		return sum;	
	}
}

// LSUM (exp, ind, list)
class LambdaLSUM extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=3)
			throw new ParseException("Usage: LSUM (exp, ind, list)");
		Algebraic exp = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		Algebraic ind = (Algebraic)Jasymca.evalPrefix(args.elementAt(1), true, env);
		Algebraic list= (Algebraic)Jasymca.evalPrefix(args.elementAt(2), true, env);
		if(!(ind instanceof Polynomial) || !(list instanceof Vektor))
			throw new ParseException("Usage: LSUM (exp, ind, list)");
		Variable v = ((Polynomial)ind).var;
		Algebraic sum = Zahl.ZERO;
		for( int i=0; i<((Vektor)list).coord.length; i++)		
			sum = sum.add(((Vektor)list).coord[i]);
		return sum;	
	}
}

// Divide and remainder of two polynomials
class LambdaDIVIDE extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=3 && args.size()!=2)
			throw new ParseException("Usage: DIVIDE (p1, p2, var)");
		Algebraic p1 = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		Algebraic p2 = (Algebraic)Jasymca.evalPrefix(args.elementAt(1), true, env);
		Algebraic var = null;
		if(args.size()==3){
			var= (Algebraic)Jasymca.evalPrefix(args.elementAt(2), true, env);
			if(!(var instanceof Polynomial))
				throw new ParseException("Usage: DIVIDE (p1, p2, var)");
			Variable v = ((Polynomial)var).var;
			p1 = p1.value(v,Polynomial.top);
			p2 = p2.value(v,Polynomial.top);
		}
		Algebraic[] r = p1.div(p2,null);
		if(args.size()==3){
			r[0] = r[0].value(SimpleVariable.top, var);
			r[1] = r[1].value(SimpleVariable.top, var);
		}
		return new Vektor(r);
	}
}


// TAYLOR (exp, var, pt, pow)
class LambdaTAYLOR extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=4)
			throw new ParseException("Usage: TAYLOR (exp, var, pt, pow)");
		Algebraic exp = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		Algebraic var = (Algebraic)Jasymca.evalPrefix(args.elementAt(1), true, env);
		Algebraic pt  = (Algebraic)Jasymca.evalPrefix(args.elementAt(2), true, env);
		Algebraic pow = (Algebraic)Jasymca.evalPrefix(args.elementAt(3), true, env);
		if(!(var instanceof Polynomial) || !(pow instanceof Zahl) || !((Zahl)pow).integerq() )
			throw new ParseException("Usage: TAYLOR (exp, var, pt, pow)");
		Variable v = ((Polynomial)var).var;
		int n = ((Zahl)pow).intval();
		Algebraic r = exp.value(v, pt);
		Algebraic t = new Polynomial(v).sub(pt);
		double nf = 1;
		for(int i=1; i<=n; i++){
			exp = exp.deriv(v);
			nf *= i; // Fakultät
			r = r.add(exp.value(v,pt).mult(t.pow_n(i)).div(new Unexakt(nf)));
		}
		return r;
	}
}


// SAVE (filename,arg1, arg2,...,argi)
// arg = all saves everything except functions
class LambdaSAVE extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()<2)
			throw new ParseException("Usage: SAVE (filename,arg1, arg2,...,argi)");
		Object filename = args.elementAt(0);
		try{
			OutputStream f = Jasymca.getFileOutputStream( (String)filename, true);
			for(int i=1; i<args.size(); i++){
				String var = (String)args.elementAt(i);
				if("ALL".equalsIgnoreCase(var)){
					Enumeration en = env.keys();
					while(en.hasMoreElements()){
						Object key = en.nextElement();
						if(!"pi".equalsIgnoreCase((String)key)){ // Would be reread as variable
							Object val = env.getValue((String)key);
							if(!(val instanceof Lambda)){
								String line = key.toString()+":"+val.toString()+";\n";
								f.write(line.getBytes());
							}
						}
					}
				}else{
					Object val  = env.getValue(var);
					String line = var.toString()+":"+val.toString()+";\n";
					f.write(line.getBytes());
				}
			}f.close();
			return "Wrote variables to "+filename;
		}catch(Exception e){
			throw new JasymcaException("Could not write to "+filename+" :"+e.toString());
		}
	}
}

//  LOADFILE (filename) 
class LambdaLOADFILE extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=1)
			throw new ParseException("Usage: LOADFILE (filename)");
		Object filename = args.elementAt(0);
		try{
			InputStream f = Jasymca.getFileInputStream( (String)filename );
			String s;
			while( (s = readLine(f)) != null ){
				p(s);
				Object o = expandExp(expandFundef(read(s)));
				Jasymca.evalPrefix(in_pr(o), false,env);
			}
			f.close();
			return "Loaded Variables from "+filename;
		}catch(Exception e){
			throw new JasymcaException("Could not read from "+filename+" :"+e.toString());
		}
	}	
	// Read everything until ';' or EOF
	String readLine( InputStream in ){
		StringBuffer s = new StringBuffer();
		int c=0;
		try{
			while((c=in.read()) != -1 && c!=';')
				if(c!='\n' && c!='\r')
					s.append((char)c);
		}catch(Exception e){
			p(e.toString());
		}
		if(s.length()==0 && c==-1)
			return null;
		return s.toString();
	}
}


// Rationalize all numbers, user function
class LambdaRAT extends LambdaAlgebraic{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic arg = ((Algebraic)car(x)).reduce();
		if(arg instanceof Unexakt) return f((Zahl)arg);
		if(arg instanceof Exakt)   return arg;
		return FunctionVariable.create(
				getClass().getName().substring("jasymca.Lambda".length()).toLowerCase(Locale.US), arg);
	}	
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x instanceof Zahl) return (Zahl)x.rat();		
		return x.map(this);
	}
	Zahl f( Zahl x) throws JasymcaException{ 
		return (Zahl)x.rat();
	}
}
				
				

// Square free decomposition of polynomial
class LambdaSQFR extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Object f = Jasymca.evalPrefix(car(x), true, env); 
		if(f instanceof Zahl) return f;
		if(!(f instanceof Polynomial))
			throw new ParseException("Argument to sqfr() must be polynomial.");
		f = ((Polynomial)f).rat();
		Algebraic[] fs = ((Polynomial)f).square_free_dec(((Polynomial)f).var);
		if(fs==null) return f;
		Object result = null;
		for(int i=0; i<fs.length; i++){
			Object factor=null;
			if(fs[i]!=null){
				if(i==0) factor = fs[i];
				else	 factor = list(cons("^",cons(fs[i], list(new Unexakt(i+1)))));
			}
			if(factor!=null){
				if(result==null)
					result=factor;
				else
					result = cons("*", cons(result, factor));
			}
		}
		if(result==null)
			return f;
		return result;
	}
}


// Find roots of univariate real polynomial
class LambdaALLROOTS extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		x = Jasymca.evalPrefix(car(x), true, env); // Evaluate to canonical form
		if(!(x instanceof Polynomial))
			throw new JasymcaException("Argument to allroots must be polynomial.");
		Polynomial p = (Polynomial)((Polynomial)x).rat();
		Algebraic ps[] = p.square_free_dec(p.var);
		Vektor r;
		Vector v = new Vector();
		for(int i=0; i<ps.length; i++){
			if(ps[i] instanceof Polynomial){
				r= ((Polynomial)ps[i]).monic().bairstow();
				for(int k=0; r != null && k<r.coord.length ; k++){
					for(int j=0; j<=i; j++)
						v.addElement(r.coord[k]);
				}
			}
		}
		return Vektor.create(v);
	}
}
			

			
	
class Add extends LambdaAlgebraic{
	public Object lambda(Object x) throws JasymcaException,ParseException{
		switch(length(x)){
			case 1: return car(x);
			case 2: return ((Algebraic)car(x)).add((Algebraic)car(cdr(x)));
		}
		throw new ParseException("Wrong number of arguments for \"+\".");
	}
}
	
class Sub extends LambdaAlgebraic{
	public Object lambda(Object x) throws JasymcaException,ParseException{
		switch(length(x)){
			case 1: return ((Algebraic)car(x)).mult(Zahl.MINUS);
			case 2: return ((Algebraic)car(x)).sub((Algebraic)car(cdr(x)));
		}
		throw new ParseException("Wrong number of arguments for \"-\".");
	}
}
	
class Mult extends LambdaAlgebraic{
	public Object lambda(Object x) throws JasymcaException,ParseException{
		switch(length(x)){
			case 1: return car(x);
			case 2: return ((Algebraic)car(x)).mult((Algebraic)car(cdr(x)));
		}
		throw new ParseException("Wrong number of arguments for \"*\".");
	}
}
	
class Div extends LambdaAlgebraic{
	public Object lambda(Object x) throws JasymcaException,ParseException{
		switch(length(x)){
			case 1: return car(x);
			case 2: return ((Algebraic)car(x)).div((Algebraic)car(cdr(x)));
		}
		throw new ParseException("Wrong number of arguments for \"/\".");
	}
}
	
class Fact extends LambdaAlgebraic{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic arg = (Algebraic)car(x);
		if(arg instanceof Zahl) return f((Zahl)arg);
		return FunctionVariable.create("!", arg);
	}	
	
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x instanceof Zahl) return f((Zahl)x);;		
		return null;
	}
	
	
	Zahl f( Zahl x) throws JasymcaException{ 
		if(!x.integerq() || x.smaller(Zahl.ZERO))
			throw new JasymcaException("Argument to factorial must be a positive integer.");
		Algebraic r = Zahl.ONE;
		while(Zahl.ONE.smaller(x)){
			r=r.mult(x);
			x=(Zahl)x.sub(Zahl.ONE);
		}
		return (Zahl)r;
	}
}

class LambdaSQRT extends LambdaAlgebraic{
	public LambdaSQRT(){ diffrule = "1/(2*sqrt(x))"; intrule = "2/3*x*sqrt(x)"; }

	// Integrate root of squares
	static String intrule2 = 
		"(2*a*x+b)*sqrt(X)/(4*a)+(4*a*c-b*b)/(8*a*sqrt(a))*log(2*sqrt(a*X)+2*a*x+b)";
		
	public Algebraic integrate(Algebraic arg, Variable x) throws JasymcaException{ 
		try{
			return super.integrate(arg,x);
		}catch(JasymcaException je){
			
		if(!(arg.depends(x)))
			throw new JasymcaException("Expression in function does not depend on Variable.");
		if( !(arg instanceof Polynomial) || ((Polynomial)arg).degree()!=2 ||
			!((Polynomial)arg).constcoef(x) )
			throw new JasymcaException("Can not integrate function ");
		try{
			// Create substitution list
			Algebraic xp = new Polynomial(x);
			Polynomial X = (Polynomial)arg;
			Object s = list(cons("X", X));
			s = cons(cons("x",xp),s);
			s = cons(cons("a",X.coef[2]),s);
			s = cons(cons("b",X.coef[1]),s);
			s = cons(cons("c",X.coef[0]),s);
			Object prefix = change( compile_rule(intrule2), s);
			Algebraic r = (Algebraic)Jasymca.evalPrefix(prefix,true, env);
			// BEGIN Markus Hohenwarter 2008-03-14
			// removed sqrt expand: don't want simplification of sqrt(x^2) -> x here
			// because abs(x) := sqrt(x^2)
			//return new SqrtExpand().f_exakt(r);
			return r;
			// END Markus Hohenwarter 2008-03014

		}catch(ParseException e){
				throw new JasymcaException("Error integrating function");
		}
		}
	}

	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.){
			if(z.real<0.)
				return new Unexakt(0, Math.sqrt(-z.real));
			return new Unexakt(Math.sqrt(z.real));
		}
		double sr  = Math.sqrt(z.real*z.real+z.imag*z.imag);
		double phi = Math.atan2(z.imag,z.real)/2.;
		return new Unexakt( sr*Math.cos(phi), sr*Math.sin(phi));
	}

	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ONE) || x.equals(Zahl.ZERO))
			return x;
		if(x.equals(Zahl.MINUS))
			return Zahl.IONE;
		if(x instanceof Zahl){ 
			return fzexakt((Zahl)x);
		}
	
		if(x instanceof Polynomial && ((Polynomial)x).degree()==1 &&
		((Polynomial)x).coef[0].equals(Zahl.ZERO) &&
		((Polynomial)x).coef[1].equals(Zahl.ONE) &&
		((Polynomial)x).var instanceof FunctionVariable &&
			((FunctionVariable)((Polynomial)x).var).fname.equals("exp"))
			return FunctionVariable.create("exp",
				((FunctionVariable)((Polynomial)x).var).arg.div(Zahl.TWO));
		return null;
	}
	
	
	Algebraic fzexakt(Zahl x) throws JasymcaException{
		if(x instanceof Exakt && !x.komplexq()){
			if(x.smaller(Zahl.ZERO)){
				Algebraic r = fzexakt((Zahl)x.mult(Zahl.MINUS));
				if(r!=null) return Zahl.IONE.mult(r);
				return r;
			}
			long nom = ((Exakt)x).real[0].longValue();
			long den = ((Exakt)x).real[1].longValue();
			// x = sqrt(a/b) = sqrt((a0^2*a1)/(b0^2*b1)) = (a0/(b0*b1))*sqrt(a1*b1);
			long a0 = introot(nom), a1 = nom/(a0*a0);
			long b0 = introot(den), b1 = den/(b0*b0);
			BigInteger br[] = { BigInteger.valueOf(a0),
								BigInteger.valueOf(b0*b1) };
			Exakt r = new Exakt(br);
			a0 = a1*b1;
			if(a0 == 1L) return r;
			return r.mult( new Polynomial(new FunctionVariable("sqrt",
									new Exakt(  BigInteger.valueOf(a0) ), this)));
		}
		return null;
	}
	
	// Find and return largest squared factor in x:
	// x = a^2*b ---> a
	long introot(long x){
		long s = 1L, f, g, t[] = { 2L, 3L, 5L};

		for(int i=0; i<t.length; i++){
			g = t[i]; f = g*g;
			while(x % f == 0L && x != 1L){
				s *= g; x /= f;
			}
		}
		for(long i= 6L; x!=1L ; i+=6L){	
			g = i+1; f = g*g;
			while(x % f == 0L && x != 1L){
				s *= g; x /= f;
			}
			g = i+5; f = g*g;
			while(x % f == 0L && x != 1L){
				s *= g; x /= f;
			}
			if(f>x) break;
		}
		return s;
	}		
}

class LambdaSIGN extends LambdaAlgebraic{
	public LambdaSIGN(){ 
		diffrule = "x-x";  // 0 geht nicht wegen parser: korrigieren!
		intrule  = "x*sign(x)"; 
	}

	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x instanceof Zahl)
			return f((Zahl)x);
		return null;
	}

	Zahl f( Zahl x) throws JasymcaException{
		return x.smaller(Zahl.ZERO)?Zahl.MINUS:Zahl.ONE;
	}
}



// Expand all user functions
class ExpandUser extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic x1) throws JasymcaException{ 
		if( !(x1 instanceof Polynomial) )
			return x1.map(this);
		Polynomial p = (Polynomial)x1;
		if(p.var instanceof SimpleVariable)
			return p.map(this);
		FunctionVariable f = (FunctionVariable)p.var;
		Object lx = env.getValue(f.fname);
		if(!(lx instanceof UserFunction))
			return p.map(this);		
		UserFunction la = (UserFunction)lx;
		Algebraic x;
		if(la.var.length==1)
			x = la.body.value(la.var[0], f.arg);
		else if(f.arg instanceof Vektor && ((Vektor)f.arg).dim()==la.var.length)
			x = la.fv((Vektor)f.arg);
		else 
			throw new JasymcaException("Wrong argument to function "+la.fname);
		
		Algebraic r=Zahl.ZERO;
		for(int i=p.coef.length-1; i>0; i--){
			r=r.add(f_exakt(p.coef[i])).mult(x);
		}
		if(p.coef.length>0)
			r=r.add(f_exakt(p.coef[0]));
		return r;
	}
}


	

