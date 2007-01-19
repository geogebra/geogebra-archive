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

import java.util.Hashtable;

// Environment for variables and functions
// Stored by name, case-insensitive

public class Environment extends Hashtable{
	
	static Polynomial NULL=new Polynomial(new SimpleVariable("null"));
		
	// Store Variable	
	public void putValue(String var, Object x){
		var 		= var.toUpperCase();
		// Way to cancel variables
		if(x.equals(NULL)){
			remove(var);
		}else{
			put( var, x);
		}
	}


	// Value of Variable var	
	public Object getValue(String var){
		var 		= var.toUpperCase();
		Object r 	= get(var);

		// If this is an uninstantiated Operator, create an instance
		// Let the Java Classloader do the work
		if(r == null){ 
			try{
				Class c 	= Class.forName("jasymca.Lambda" + var);
				Lambda f 	= (Lambda)c.newInstance();
				putValue(var, f);
				r 			= f;
			}catch(Exception e){
			}
		}
		return r;
	}
	
	// Get numeric constant
	public Zahl getnum(String var){
		var 		= var.toUpperCase();
		Object r 	= get(var);
		if(r instanceof Zahl)
			return (Zahl)r;
		return null;
	}

}
