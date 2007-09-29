package jasymca;

import geogebra.util.MyMath;

/*------------------------------------------------------------*/

///////////////////// Trigonometric and other conversions /////////////////////////////////////

/* User Function trigrat
Convert trigs to expoentials, normalize, convert back, and collect roots
*/
class LambdaTRIGRAT extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic f = (Algebraic)Jasymca.evalPrefix(car(x),true,env); 
		f = f.rat();
		p("Rational: "+f);
		f = new ExpandUser().f_exakt( (Algebraic) f); 
		p("User Function expand: "+f);
		f = new TrigExpand().f_exakt( (Algebraic) f);
		p("Trigexpand: "+f);
		f = new NormExp().f_exakt( (Algebraic) f);
		p("Norm: "+f);
		f = new TrigInverseExpand().f_exakt( (Algebraic) f);
		p("Triginverse: "+f);
		f = new SqrtExpand().f_exakt( (Algebraic) f);
		p("Sqrtexpand: "+f);
		return f;
	}
}

/* User Function trigexp
Convert trigs to expoentials
*/
class LambdaTRIGEXP extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{
		Algebraic f = (Algebraic)Jasymca.evalPrefix(car(x),true,env); 
		f = f.rat();
		p("Rational: "+f);
		f = new ExpandUser().f_exakt( (Algebraic) f); 
		p("User Function expand: "+f);
		f = new TrigExpand().f_exakt( (Algebraic) f);
		p("Trigexpand: "+f);
		f = new NormExp().f_exakt( (Algebraic) f);
		f = new SqrtExpand().f_exakt((Algebraic) f);
		return f;
	}
}


/* Internal function trigexpand
expand trigs to exponentials
*/
class TrigExpand extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x instanceof Polynomial && ((Polynomial)x).var instanceof FunctionVariable){
			Polynomial xp = (Polynomial)x;
			FunctionVariable f = (FunctionVariable)xp.var;
			Object la = env.getValue(f.fname);
			if(la != null && la instanceof LambdaAlgebraic &&
				((LambdaAlgebraic)la).trigrule != null){
				try{
					String trigrule = ((LambdaAlgebraic)la).trigrule;
					Object prefix = change(compile_rule(trigrule),
						list(cons("x", f.arg)) );
					Algebraic fexp = (Algebraic)Jasymca.evalPrefix(prefix,true,env);
					Algebraic r=Zahl.ZERO;
					for(int i=xp.coef.length-1; i>0; i--){
						r=r.add(f_exakt(xp.coef[i])).mult(fexp);
					}
					if(xp.coef.length>0)
					r=r.add(f_exakt(xp.coef[0]));
					return r;
				}catch(Exception e){
					throw new JasymcaException(e.toString());
				}
			}
		}
		return x.map(this);
	}
}

/* Internal function sqrtexpand
	 sqrt(x^2)->x, (sqrt(x))^2-->x
*/
class SqrtExpand extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(!(x instanceof Polynomial))
			return x.map(this);
		Polynomial xp = (Polynomial)x;
		Variable var  = xp.var;
		Algebraic xf=null;
		if(var instanceof FunctionVariable && ((FunctionVariable)var).fname.equals("sqrt") 
			&& ((FunctionVariable)var).arg instanceof Polynomial){ // sqrt(x^2) --> x
			Polynomial arg = (Polynomial)((FunctionVariable)var).arg;
			Algebraic[] sqfr = arg.square_free_dec(arg.var);
			boolean issquare = true;
			if(sqfr.length>0 && !sqfr[0].equals(arg.coef[arg.coef.length-1]))
				issquare = false;
			for(int i=2; i<sqfr.length && issquare; i++){
				if( (i+1)%2==1 && !sqfr[i].equals(Zahl.ONE))
					issquare = false;
			}
			if(issquare){
				xf = Zahl.ONE;
				for(int i=1; i<sqfr.length; i+=2){
					if(!sqfr[i].equals(Zahl.ZERO))
						xf = xf.mult(sqfr[i].pow_n((i+1)/2));
				}
				Algebraic r = Zahl.ZERO;
				for(int i=xp.coef.length-1; i>0; i--){
					r = r.add( f_exakt(xp.coef[i]) ).mult(xf);
				}
				if(xp.coef.length>0)
					r = r.add(f_exakt(xp.coef[0]) );
				return r;
			}
		}
		if(var instanceof FunctionVariable && ((FunctionVariable)var).fname.equals("sqrt") ){
			boolean issquare = true;				// (sqrt(x))^2 --> x
			for(int i=1; i<xp.coef.length; i++){
				if( i%2==1 && !xp.coef[i].equals(Zahl.ZERO))
					issquare = false;
			}
			if(issquare){
				xf = ((FunctionVariable)var).arg ;
				Algebraic r = Zahl.ZERO;
				for(int i=(xp.coef.length+1)/2-1; i>0; i--){
					r = r.add( f_exakt(xp.coef[2*i]) ).mult(xf);
				}
				if(xp.coef.length>0)
					r = r.add(f_exakt(xp.coef[0]) );
				return r;				
			}
		}
		return x.map(this);
	}
}
				

/* Internal function triginverseexpand
collects exp to trigs
*/
class TrigInverseExpand extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
	  if(x instanceof Polynomial && ((Polynomial)x).var instanceof FunctionVariable){
		Polynomial xp = (Polynomial)x;
		Algebraic xf=null;
		FunctionVariable var  = (FunctionVariable)xp.var;
		//  exp(x+/-iy) = exp(x)*(cos(y)+/-i*sin(y)) 
		if( var.fname.equals("exp") ){
			Algebraic re = var.arg.realpart();
			Algebraic im = var.arg.imagpart();
			if(!im.equals(Zahl.ZERO)){
				boolean minus= minus(im);
				if(minus) im = im.mult(Zahl.MINUS);
				Algebraic a = FunctionVariable.create("exp",re);
				Algebraic b=  FunctionVariable.create("cos", im); 
				Algebraic c=  FunctionVariable.create("sin", im).mult(Zahl.IONE);
				xf = a.mult(minus?(b.sub(c)):b.add(c));
			}
		}
		// log(x+/-i*y) =  1/2*log(x^2+y^2)-/+i*atan(x/y)+i*pi/2*sign(x)
		if( var.fname.equals("log") ){
		// First: log(a*sqrt(x)) = log(a)+1/2*log(x)
			Algebraic arg = var.arg;
			Algebraic factor = Zahl.ONE, sum = Zahl.ZERO;
			if(arg instanceof Polynomial && ((Polynomial)arg).degree()==1 &&
				((Polynomial)arg).var instanceof FunctionVariable &&
				((Polynomial)arg).coef[0].equals(Zahl.ZERO)       &&
				((FunctionVariable)((Polynomial)arg).var).fname.equals("sqrt")){
				sum = FunctionVariable.create("log",((Polynomial)arg).coef[1]);
				factor = new Unexakt(0.5);
				arg = ((FunctionVariable)((Polynomial)arg).var).arg;
				xf = FunctionVariable.create("log", arg);					
			}	
			Algebraic re = arg.realpart();
			Algebraic im = arg.imagpart();
			if(!im.equals(Zahl.ZERO)){
				boolean min_im= minus(im), min_re=minus(re);
				if(min_im) im = im.mult(Zahl.MINUS);
				Algebraic a1 = new SqrtExpand().f_exakt(arg.mult(arg.cc()));
				Algebraic a = FunctionVariable.create("log",a1).div(Zahl.TWO);
				Algebraic b1 = f_exakt( re.div(im) );
				Algebraic b = FunctionVariable.create("atan", b1).mult(Zahl.IONE); 
				xf = min_im? a.add(b) :a.sub(b);
				Algebraic pi2 = Zahl.PI.mult(Zahl.IONE).div(Zahl.TWO);
				xf = min_re? xf.sub(pi2):xf.add(pi2);
			}
			if(xf!=null)
				xf = xf.mult(factor).add(sum);
		}

		// Rebuild to order variables
		if(xf==null){
			return x.map(this);
		}
		Algebraic r = Zahl.ZERO;
		for(int i=xp.coef.length-1; i>0; i--)
			r = r.add(f_exakt(xp.coef[i])).mult(xf);
		if( xp.coef.length>0)
			r = r.add(f_exakt(xp.coef[0]));
		return r;
	  }
	  return x.map(this);
	}
	
	// Determine arbitrary canonical sign
	static boolean minus(Algebraic x) throws JasymcaException{
		if(x instanceof Zahl)
			return ((Zahl)x).smaller(Zahl.ZERO);
		if(x instanceof Polynomial)
			return minus(((Polynomial)x).coef[((Polynomial)x).degree()]);
		if(x instanceof Rational){
			boolean a = minus(((Rational)x).nom);
			boolean b = minus(((Rational)x).den);
			return (a && !b) || (!a && b);
		}
		throw new JasymcaException("minus not implemented for "+x);
	}
}


///////////////////// Various numeric trig functions /////////////////////////////////

class LambdaSIN extends LambdaAlgebraic{
	public LambdaSIN(){ 
		diffrule = "cos(x)"; 
		intrule  = "-cos(x)"; 
		trigrule = "1/(2*i)*(exp(i*x)-exp(-i*x))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.sin(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;
		return null;
	}
		
}

class LambdaCOS extends LambdaAlgebraic{
	public LambdaCOS(){ 
		diffrule = "-sin(x)"; 
		intrule = "sin(x)"; 
		trigrule = "1/2 *(exp(i*x)+exp(-i*x))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.cos(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ONE;
		return null;
	}
}

class LambdaTAN extends LambdaAlgebraic{
	public LambdaTAN(){ 
		diffrule = "1/(cos(x))^2"; 
		intrule  = "-log(cos(x))"; // To do : add abs
		trigrule = "-i*(exp(i*x)-exp(-i*x))/(exp(i*x)+exp(-i*x))";
	} 
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.tan(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;
		return null;
	}	
}



class LambdaATAN extends LambdaAlgebraic{
	public LambdaATAN(){ 
		diffrule = "1/(1+x^2)"; 
		intrule  = "x*atan(x)-1/2*log(1+x^2)"; 
		trigrule = "-i/2*log((1+i*x)/(1-i*x))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.atan(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}

/* 
 * BEGIN Markus Hohenwarter
 */
class LambdaACOS extends LambdaAlgebraic{
	public LambdaACOS(){ 
		diffrule = "-1/sqrt(1-x^2)"; 
		intrule  = "-sqrt(1-x^2)+x*acos(x)"; 		
		trigrule = "pi/2+i*log(i*x+sqrt(1-x^2))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.acos(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ONE))
			return Zahl.ZERO;	
		if(x.equals(Zahl.MINUS))
			return Zahl.PI;
		return null;
	}	
}

class LambdaASIN extends LambdaAlgebraic{
	public LambdaASIN(){ 
		diffrule = "1/sqrt(1-x^2)"; 
		intrule  = "sqrt(1-x^2)+x*asin(x)"; 		
		trigrule = "-i*log(i*x+sqrt(1-x^2))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.asin(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}


class LambdaCOSH extends LambdaAlgebraic{
	public LambdaCOSH(){ 
		diffrule = "sinh(x)"; 
		intrule  = "sinh(x)"; 		
		trigrule = "1/2*exp(x)+1/2*exp(-x)";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.cosh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ONE;			
		return null;
	}	
}

class LambdaSINH extends LambdaAlgebraic{
	public LambdaSINH(){ 
		diffrule = "cosh(x)"; 
		intrule  = "cosh(x)"; 		
		trigrule = "1/2*exp(x)-1/2*exp(-x)";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.sinh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}

class LambdaTANH extends LambdaAlgebraic{
	public LambdaTANH(){ 
		diffrule = "1/cosh(x)^2"; 
		intrule  = "log(cosh(x))"; 		
		trigrule = "(exp(x)-exp(-x))/(exp(x)+exp(-x))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.tanh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}


class LambdaACOSH extends LambdaAlgebraic{
	public LambdaACOSH(){ 
		diffrule = "1/(sqrt(x-1)*sqrt(x+1))"; 
		intrule  = "-sqrt(x-1)*sqrt(x+1) + x*acosh(x)"; 		
		trigrule = "log(x+sqrt(x-1)*sqrt(x+1))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.acosh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ONE))
			return Zahl.ZERO;			
		return null;
	}	
}

class LambdaASINH extends LambdaAlgebraic{
	public LambdaASINH(){ 
		diffrule = "1/sqrt(x^2+1)"; 
		intrule  = "-sqrt(x^2+1) + x*asinh(x)"; 		
		trigrule = "log(x+sqrt(x^2+1))";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.asinh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}

class LambdaATANH extends LambdaAlgebraic{
	public LambdaATANH(){ 
		diffrule = "-1/(x^2-1)"; 
		intrule  = "x*atanh(x)+1/2*log(x^2-1)"; 		
		trigrule = "1/2*log(1+x)-1/2*log(1-x)";
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.atanh(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		return null;
	}	
}

class LambdaROUND extends LambdaAlgebraic{
	public LambdaROUND(){ 
		diffrule = null; 
		intrule  = null; 		
		trigrule = null;
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.round(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;
		if(x.equals(Zahl.ONE))
			return Zahl.ONE;
		if(x.equals(Zahl.MINUS))
			return Zahl.MINUS;
		return null;
	}	
}


class LambdaCBRT extends LambdaAlgebraic{
	public LambdaCBRT(){ 
		diffrule = "1/(3*cbrt(x)^2)";		
		intrule  = "3/4*cbrt(x)^4";	
		trigrule = null;
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(MyMath.cbrt(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;			
		if(x.equals(Zahl.ONE))
			return Zahl.ONE;
		if(x.equals(Zahl.MINUS))
			return Zahl.MINUS;
		return null;
	}	
}

class LambdaROOT extends LambdaAlgebraic{
	public LambdaROOT(){ 
		diffrule = null; 
		intrule  = null; 		
		trigrule = null;
	}
	Zahl f( Zahl x) throws JasymcaException{
		Unexakt z = x.unexakt();
		if(z.imag == 0.)
			return new Unexakt(Math.round(z.real));
		return (Zahl)Jasymca.evalx(trigrule, z, env);
	}
	Algebraic f_exakt(Algebraic x) throws JasymcaException{ 
		if(x.equals(Zahl.ZERO))
			return Zahl.ZERO;
		if(x.equals(Zahl.ONE))
			return Zahl.ONE;
		if(x.equals(Zahl.MINUS))
			return Zahl.MINUS;
		return null;
	}	
}


/* 
 * END Markus Hohenwarter
 */



/* Let the user define it if he needs it
class LambdaATAN2 extends UserFunction{
	public LambdaATAN2() throws JasymcaException, ParseException{ 
		var = new Variable[2];
		var[0] = new SimpleVariable("y_@ATAN2");
		var[1] = new SimpleVariable("x_@ATAN2");
		fname = "atan2";
		body = FunctionVariable.create("atan", new Polynomial(var[0]).div
														  (new Polynomial(var[1])));
		// x<0; y>0: +pi
		// x<0; y<0: -pi
		// (sign(y)*(1-sign(x))/2 * pi
		Algebraic signx = FunctionVariable.create("sign", new Polynomial(var[1]));
		Algebraic signy = FunctionVariable.create("sign", new Polynomial(var[0]));
		body = body.add( signy.mult(Zahl.ONE.sub(signx)).div(Zahl.TWO).mult(Zahl.PI));
	}

}
*/
