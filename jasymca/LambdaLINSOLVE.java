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

// Gauss Elimination
// Stöcker, S.392
import java.util.Vector;

public class LambdaLINSOLVE extends Lambda{

	public Object lambda(Object x) throws ParseException, JasymcaException{
		Vector args = getArgs(car(x));
		if(args.size()!=2)
			throw new ParseException("linsolve requires 2 arguments.");
		Vektor expr = (Vektor)((Algebraic)Jasymca.evalPrefix(args.elementAt(0), true, env)).rat();
		Vektor vars =  (Vektor)Jasymca.evalPrefix(args.elementAt(1), true, env);
		if(expr.coord.length != vars.coord.length)
			throw new ParseException("Number of vars != Number of equations");
		Algebraic an[][] = new Algebraic[expr.coord.length][vars.coord.length];
		Algebraic cn[]   = new Algebraic[vars.coord.length];
		for(int i=0; i<expr.coord.length; i++){
			Algebraic y = expr.coord[i];
			for(int k=0; k<vars.coord.length; k++){
				an[i][k] = y.div(vars.coord[k],null)[0];
				y = y.sub(an[i][k].mult(vars.coord[k]));
			}
			cn[i] = y.mult(Zahl.MINUS);
		}
		return Gauss(new Matrix(an), new Vektor(cn));
	}	
				
	private static void pivot(Matrix a, Vektor c, int k) throws JasymcaException{
		int pivot = k, n=c.coord.length;
		double maxa = a.a[k][k].norm();
		for(int i=k+1; i<n; i++){
			double dummy = a.a[i][k].norm();
			if(dummy>maxa){
				maxa=dummy;
				pivot=i;
			}
		}
		if(pivot!=k){
			for(int j=k;j<n;j++){
				Algebraic dummy = a.a[pivot][j];
				a.a[pivot][j] = a.a[k][j];
				a.a[k][j] = dummy;
			}
			Algebraic dummy = c.coord[pivot];
			c.coord[pivot] = c.coord[k];
			c.coord[k] = dummy;
		}			
	}
		
	public static Vektor Gauss(Matrix a, Vektor c) throws JasymcaException{
		int n = c.coord.length;
		Algebraic x[]=new Algebraic[n];
		// Vorwärtseliminierung
		for(int k=0; k<n-1; k++){
			pivot(a,c,k);
			for(int i=k+1; i<n; i++){
				Algebraic factor = a.a[i][k].div(a.a[k][k]);
				for(int j=k+1; j<n; j++){
					a.a[i][j] = a.a[i][j].sub(factor.mult(a.a[k][j]));
				}
				c.coord[i] = c.coord[i].sub(factor.mult(c.coord[k]));
			}
		}
		// Rückwärtssubstitution
		x[n-1] = c.coord[n-1].div(a.a[n-1][n-1]);
		for(int i=n-2; i>=0; i--){
			Algebraic sum = Zahl.ZERO;
			for(int j=i+1; j<n; j++){
				sum = sum.add(a.a[i][j].mult(x[j]));
			}
			x[i] = c.coord[i].sub(sum).div(a.a[i][i]);
		}
		return new Vektor(x);
	}
}				
		
