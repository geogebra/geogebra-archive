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

public class Matrix extends Algebraic{
	Algebraic a[][];
	
	public Matrix(Algebraic[][] a){
		this.a = a;
	}
	
	public Matrix(Vektor x) throws JasymcaException{
		for(int i=0; i<x.coord.length; i++)
			if(!(x.coord[i] instanceof Vektor) || 
					((Vektor)x.coord[i]).coord.length !=((Vektor)x.coord[0]).coord.length)
						throw new JasymcaException("Wrong Input for Matrix.");
		a = new Algebraic[x.coord.length][((Vektor)x.coord[0]).coord.length];
		for(int i=0; i<x.coord.length; i++){
			a[i] = ((Vektor)x).coord;
		}
	}
		
	
	
	public Algebraic add (Algebraic x) throws JasymcaException{
		if(!(x instanceof Matrix) || ((Matrix)x).a.length != a.length 
			|| ((Matrix)x).a[0].length != a[0].length)
			throw new JasymcaException("Matrix addition requires equal dimensions.");
		throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}

	public Algebraic mult(Algebraic x) throws JasymcaException{
		throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}
	
	public Algebraic deriv(Variable var) throws JasymcaException{
		throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}
	
	public Algebraic integrate(Variable var) throws JasymcaException{
		throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}
	
	public double norm(){
		return 0;//throw new JasymcaException("Matrix arithmetic not yet implemented.");
	}
	
	public boolean equals(Object x){
		if(!(x instanceof Matrix) || ((Matrix)x).a.length != a.length 
			|| ((Matrix)x).a[0].length != a[0].length)
			return false;
		for(int i=0; i<a.length; i++)
			for(int k=0; k<a[0].length; k++)
				if(!a[i][k].equals(((Matrix)x).a[i][k]))
					return false;
		return true;
	}	
	
	public String toString(){ 
		String r = "[";
		for(int i=0; i<a.length; i++){
			r+="[";
			for(int k=0; k<a[0].length; k++){
				r += a[i][k].toString();
				if(k<a[i].length-1)
					r+=", ";
			}
			r+="]";
			if(i<a.length-1) r+="\n";
		}
		return r+"]";
	}
	
	boolean number(){
		for(int i=0; i<a.length; i++)
			for(int k=0; k<a[0].length; k++)
				if(!(a[i][k] instanceof Zahl))
					return false;
		return true;
	}	

	// Map f to components
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		Algebraic cn[][] = new Algebraic[a.length][a[0].length];
		for(int i=0; i<a.length; i++)
			for(int k=0; k<a[0].length; k++)
				cn[i][k] = f.f_exakt(a[i][k]);
		return new Matrix(cn);
	}

}
