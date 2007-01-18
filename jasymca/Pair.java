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

public class Pair{
    public Object car;
    public Object cdr;

    public Pair(Object car, Object cdr) { 
		this.car = car; this.cdr = cdr; 
    }
	
	public String toString(){
		if(Lisp.listq(this)){
			String s = "(";
			Object x = this;
			while(x instanceof Pair){
				s += Lisp.car(x);
				x = Lisp.cdr(x);
				if(x instanceof Pair)
					s += " ";
			}
			return s+")";
		}
		return "(" + car + " . " + cdr +")";
	}

    public boolean equals(Object x) {
		if (x == this) 
			return true;
		else if (!(x instanceof Pair)) 
			return false;
		else return Lisp.equal(car,((Pair)x).car) &&
					Lisp.equal(cdr,((Pair)x).cdr);
	}
}
