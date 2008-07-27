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
import java.util.Locale;

// Environment for variables and functions
// Stored by name, case-insensitive

public class Environment extends Hashtable{
	
	static Polynomial NULL=new Polynomial(new SimpleVariable("null"));
		
	// BEGIN: added by Markus Hohenwarter, July 23, 2007
	public Environment() {
		initLambdaFunctions();
	}
	// END: added by Markus Hohenwarter, July 23, 2007
	
	// Store Variable	
	public void putValue(String var, Object x){
		var 		= var.toUpperCase(Locale.US);
		// Way to cancel variables
		if(x.equals(NULL)){
			remove(var);
		}else{
			put( var, x);
		}
	}

	// Value of Variable var	
	public Object getValue(String var){
		var 		= var.toUpperCase(Locale.US);
		Object r 	= get(var);

		/* COMMENTED by Markus Hohenwarter, July 23, 2007:
		 * don't use reflection!
		 * 
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
		}*/
	
		return r;	
	}
	

	// Get numeric constant
	public Zahl getnum(String var){
		var 		= var.toUpperCase(Locale.US);
		Object r 	= get(var);
		if(r instanceof Zahl)
			return (Zahl)r;
		return null;
	}

	
//	 BEGIN: added by Markus Hohenwarter, July 23, 2007
	private void initLambdaFunctions() {
		putValue("EXP", new LambdaEXP()); 
		putValue("LOG", new LambdaLOG());
		putValue("ALGSYS", new LambdaALGSYS());
		putValue("INTEGRATE", new LambdaINTEGRATE());
		putValue("ROMBERG", new LambdaROMBERG());
		putValue("FLOAT", new LambdaFLOAT());
		putValue("GCD", new LambdaGCD());
		putValue("EXIT", new LambdaEXIT());
		putValue("EXPAND", new LambdaEXPAND());
		putValue("REALPART", new LambdaREALPART());
		putValue("IMAGPART", new LambdaIMAGPART());
		putValue("CFS", new LambdaCFS());
		putValue("DIFF", new LambdaDIFF());
		putValue("SUBST", new LambdaSUBST());
		putValue("SUM", new LambdaSUM());
		putValue("LSUM", new LambdaLSUM());
		putValue("DIVIDE", new LambdaDIVIDE());
		putValue("TAYLOR", new LambdaTAYLOR());
		putValue("SAVE", new LambdaSAVE());
		putValue("LOADFILE", new LambdaLOADFILE());
		putValue("RAT", new LambdaRAT());
		putValue("SQFR", new LambdaSQFR());
		putValue("ALLROOTS", new LambdaALLROOTS());
		putValue("SQRT", new LambdaSQRT());
		putValue("SIGN", new LambdaSIGN());
		putValue("LINSOLVE", new LambdaLINSOLVE());
		putValue("SOLVE", new LambdaSOLVE());
		putValue("TRIGRAT", new LambdaTRIGRAT());
		putValue("TRIGEXP", new LambdaTRIGEXP());
		putValue("SIN", new LambdaSIN());
		putValue("COS", new LambdaCOS());
		putValue("TAN", new LambdaTAN());
		putValue("ATAN", new LambdaATAN());
		putValue("ACOS", new LambdaACOS());
		putValue("ASIN", new LambdaASIN());
		putValue("COSH", new LambdaCOSH());
		putValue("SINH", new LambdaSINH());
		putValue("TANH", new LambdaTANH());
		putValue("ACOSH", new LambdaACOSH());
		putValue("ASINH", new LambdaASINH());
		putValue("ATANH", new LambdaATANH());
		putValue("ROUND", new LambdaROUND());
		putValue("CBRT", new LambdaCBRT());
		putValue("ROOT", new LambdaROOT());
		//putValue("ATAN2", new LambdaATAN2());
	}
//	 END: added by Markus Hohenwarter, July 23, 2007
	
}
