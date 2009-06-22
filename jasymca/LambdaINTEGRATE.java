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

class LambdaINTEGRATE extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()==0)
			throw new ParseException("Argument to integrate missing.");
		Object f = Jasymca.evalPrefix(args.elementAt(0), true, env); // Function to integrate
		if(!(f instanceof Algebraic))
			throw new ParseException("First argument to integrate must be Algebraic.");
		Object v; // Variable
		if(args.size()>1){
			v = args.elementAt(1); // Variable
			v = Jasymca.evalPrefix(v, true, env);
			if(! (v instanceof Polynomial) )
				throw new ParseException("Second argument to Algebraic must be Polynomial.");
			v = ((Polynomial)v).var;
		}else{
			if( f instanceof Polynomial )
				v = ((Polynomial)f).var;
			else if(f instanceof Rational )
				v = ((Rational)f).den.var;
			else
				throw new ParseException("Could not determine Variable.");
		}
		f = new ExpandUser().f_exakt((Algebraic)f);
		try{
			return new TrigInverseExpand().f_exakt(((Algebraic)f).integrate((Variable)v));
		}catch (JasymcaException j){
		}
		Algebraic expr = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		// Second attempt: Use trigonometric/exponential Normalization
		expr = new ExpandUser().f_exakt(expr);
		expr = new TrigExpand().f_exakt( expr );
		p("Expand User Functions: "+expr);
		expr = new NormExp().f_exakt(expr);
		p("Norm Functions: "+expr);
		expr = expr.integrate((Variable)v);
		expr = new TrigInverseExpand().f_exakt(expr);
		return expr;
	}		
		
}


class LambdaROMBERG extends Lambda{
	public Object lambda(Object x) throws ParseException, JasymcaException{ 
		Vector args = getArgs(car(x));
		if(args.size()!=4)
			throw new ParseException("Usage: ROMBERG (exp,var,ll,ul)");
		Algebraic exp = (Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env);
		Algebraic var = (Algebraic)Jasymca.evalPrefix(args.elementAt(1), true, env);
		Algebraic ll  = (Algebraic)Jasymca.evalPrefix(args.elementAt(2), true, env);
		Algebraic ul  = (Algebraic)Jasymca.evalPrefix(args.elementAt(3), true, env);
		// Expand constants like pi
		LambdaAlgebraic xc = new ExpandConstants(); // evaluate pi etc.
		exp = xc.f_exakt(exp);
		ll  = xc.f_exakt(ll);
		ul  = xc.f_exakt(ul);

		// Check arguments
		if(!(var instanceof Polynomial) || !(ll instanceof Zahl) || !(ul instanceof Zahl) )
			throw new ParseException("Usage: ROMBERG (exp,var,ll,ul)");
		Variable v = ((Polynomial)var).var;
		
		double rombergtol = 1.0e-4;
		int    rombergit  = 11;
		Zahl a1 = env.getnum("rombergit");
		if(a1 !=null){
			rombergit = a1.intval();
		}
		a1 = env.getnum("rombergtol");
		if(a1 !=null){
			rombergtol = a1.unexakt().real;
		}
		double a = ((Zahl)ll).unexakt().real;
		double b = ((Zahl)ul).unexakt().real;
		double I[][] = new double[rombergit][rombergit];
		int i=0,n=1;
		Algebraic t = trapez( exp, v, n, a, b);
		if(!(t instanceof Zahl))
			throw new ParseException("Expression must evaluate to number");
		I[0][0] = ((Zahl)t).unexakt().real;
		double epsa = 1.1*rombergtol;
		while(epsa>rombergtol && i<rombergit-1){
			i++;
			n *= 2;
			t = trapez( exp, v, n, a, b);
			I[0][i] = ((Zahl)t).unexakt().real;
			double f = 1.;
			for(int k= 1; k<=i; k++){
				f *= 4; // 4^k
				I[k][i] = I[k-1][i]+(I[k-1][i]-I[k-1][i-1])/(f-1.);
			}
/*
			for(int k= 0; k<=i; k++){
				System.out.print(I[k][i]+" ");
			}
			p("");
*/			

			epsa = Math.abs(( I[i][i]- I[i-1][i-1]) /I[i][i]);
		}
		return new Unexakt(I[i][i]);		
	}
	
	Algebraic trapez( Algebraic exp, Variable v, int n, double a, double b) throws JasymcaException{
		Algebraic sum = Zahl.ZERO;
		double step = (b-a)/n;
		for(int i=1; i<n; i++){
			Algebraic x = exp.value(v, new Unexakt(a+step*i));
			sum = sum.add(x);
		}
		sum = exp.value(v, new Unexakt(a)).add(sum.mult(Zahl.TWO)).add(exp.value(v, new Unexakt(b)));
		return new Unexakt(b-a).mult(sum).div(new Unexakt(2.*n));
	}
}

			
