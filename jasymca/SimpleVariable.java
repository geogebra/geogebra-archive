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

public class SimpleVariable extends Variable{
	String name;
	
	// The most main variable
	static SimpleVariable top = new SimpleVariable("top");
	
	public SimpleVariable(String name){
		this.name = name.intern();
	}
	public Algebraic deriv( Variable x ){
		if( equals(x) )
			return Zahl.ONE;
		else
			return Zahl.ZERO;
	}
	
	public boolean equals( Object x ){
		return x instanceof SimpleVariable &&
		((SimpleVariable)x).name.equals(name);
	}
	
	public String toString(){
		return name;
	}
	
	public Object toPrefix(){ return name; };
	
	public boolean smaller(Variable v){
		if(v==top)
			return true;
		if(this==top)
			return false;
		if( v instanceof Constant)
			return false;
		if(!(v instanceof SimpleVariable))
			return true; // All Function-Variables are larger
		return name.compareTo(((SimpleVariable)v).name) < 0;
	}
	
	public Algebraic value(Variable var, Algebraic x) throws JasymcaException{
		if(var.equals(this))
			return x;
		else
			return new Polynomial(this);
	}
}
