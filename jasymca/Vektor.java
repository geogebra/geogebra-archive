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

public class Vektor extends Algebraic{
	Algebraic coord[];
	
	public Vektor(Algebraic[] coord){
		this.coord = coord;
	}

	public static Vektor create(Vector v) throws JasymcaException{
		Algebraic[] coord = new Algebraic[v.size()];
		for(int i=0; i<coord.length; i++){
			Object x = v.elementAt(i);
			if(!(x instanceof Algebraic))
				throw new JasymcaException("Error creating Vektor.");
			coord[i] = (Algebraic)x;
		}
		return new Vektor(coord);
	}
	
	// Get coordinate in range 1...dim
	public Algebraic komp(int i) throws JasymcaException{ 
		if(i<0 || i>coord.length)
			throw new JasymcaException("Index out of bounds.");
		return coord[i-1];
	}
	
	public int dim(){ 
		return coord.length;
	}
	
	public Vector vector(){
		Vector r = new Vector(coord.length);	
		for(int i=0; i<coord.length; i++)
			r.addElement(coord[i]);
		return r;
	}
	
	public Algebraic reduce(){
		if(coord.length==1)
			return coord[0];
		return this;
	}
	
	
	// Map f to coordinates
	public Algebraic map( LambdaAlgebraic f ) throws JasymcaException{
		Algebraic cn[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			cn[i] = f.f_exakt(coord[i]);
		return new Vektor(cn); 
	}

	
	public Algebraic add (Algebraic x) throws JasymcaException{
		if(!(x instanceof Vektor) || ((Vektor)x).coord.length != coord.length)
			throw new JasymcaException("Vektor addition requires equal dimensions.");
		Algebraic nc[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			nc[i] = coord[i].add(((Vektor)x).coord[i]);
		return new Vektor(nc);
	}

	public Algebraic mult(Algebraic x) throws JasymcaException{
		if( x instanceof Vektor )
			throw new JasymcaException("Vektor multiplication requires scalar.");
		Algebraic nc[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			nc[i] = coord[i].mult(x);
		return new Vektor(nc);
	}
	
	public Algebraic div(Algebraic x) throws JasymcaException{
		if( x instanceof Vektor )
			throw new JasymcaException("Vektor division requires scalar.");
		Algebraic nc[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			nc[i] = coord[i].div(x);
		return new Vektor(nc);
	}
	
	public Algebraic deriv(Variable var) throws JasymcaException{
		Algebraic nc[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			nc[i] = coord[i].deriv(var);
		return new Vektor(nc);
	}
	
	public Algebraic integrate(Variable var) throws JasymcaException{
		Algebraic nc[] = new Algebraic[coord.length];
		for(int i=0; i<coord.length; i++)
			nc[i] = coord[i].integrate(var);
		return new Vektor(nc);
	}
	
	public double norm(){
		double r = 0.;
		for(int i=0; i<coord.length; i++)
			r += coord[i].norm();
		return r;
	}
	
	public boolean equals(Object x){
		if(!(x instanceof Vektor) || ((Vektor)x).coord.length!=coord.length)
			return false;
		for(int i=0; i<coord.length; i++)
			if(!coord[i].equals(((Vektor)x).coord[i]))
				return false;
		return true;
	}	
	
	public String toString(){ 
		String r = "[";
		for(int i=0; i<coord.length; i++){
			r += coord[i].toString();
			if(i<coord.length-1)
				r+=", ";
		}
		return r+"]";
	}
	
	boolean number(){
		for(int i=0; i<coord.length;i++)
			if(!(coord[i] instanceof Zahl))
				return false;
		return true;
	} 
	
	public boolean depends(Variable var) {
		for(int i=0; i<coord.length; i++)
			if(coord[i].depends(var))
				return true;
		return false; 
	}

	
}
