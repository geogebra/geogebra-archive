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

public class Constant extends SimpleVariable{
	Unexakt value;

	public Constant(String name, double value){
		super(name);
		this.value = new Unexakt(value);			
	}
	
	public Constant(String name, Unexakt value){
		super(name);
		this.value = value;
	}
	
	public boolean smaller(Variable v){
		if(v instanceof Constant)
			return name.compareTo(((Constant)v).name) < 0;
		return true;
	}
	
}

// Insert Constants
class ExpandConstants extends LambdaAlgebraic{
	Algebraic f_exakt(Algebraic f) throws JasymcaException{ 
		if(f instanceof Polynomial && ((Polynomial)f).var instanceof Constant){
			return f.value( ((Polynomial)f).var,
					((Constant)((Polynomial)f).var).value ).map(this);
		}
		return f.map(this);	
	}
}
		


