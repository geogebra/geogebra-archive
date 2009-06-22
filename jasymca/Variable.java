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

public abstract class Variable{
	public abstract Algebraic deriv( Variable x ) throws JasymcaException; 
	public abstract Object toPrefix();
	public abstract boolean equals(Object x);
	public abstract boolean smaller(Variable v) throws JasymcaException; // For ordering
	public abstract Algebraic value(Variable var, Algebraic x) throws JasymcaException; 
	public Variable cc() throws JasymcaException{ return this; }
}
