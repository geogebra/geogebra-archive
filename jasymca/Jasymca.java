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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

///////// Start J2SE-only +

  public class Jasymca{

	  /*
	// Start interactive session
	public static void main(String args[]){
		// get name of startupfile
		String s = System.getProperty("JASYMCA_RC");
		if(s!=null) JasymcaRC=s;
		// Start interactive session
		new Jasymca().start(System.in, System.out);
	}
	*/

	static InputStream getFileInputStream(String fname) throws IOException{
		return new FileInputStream( fname );
	}

	static OutputStream getFileOutputStream(String fname, boolean append) throws IOException{
		return new FileOutputStream( fname, append );
	}
	
	static String JasymcaRC = "Jasymca.rc";
	

//////// End J2SE-only    -


//////// Start J2ME-only   +
/*
import kjava.io.*;
import kjava.util.*;
import jsystem.*;
import jsystem.JConsole.*;


import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class Jasymca extends javax.microedition.midlet.MIDlet{

	static InputStream getFileInputStream( String fname) throws IOException{
		return new kjava.io.FileInputStream( fname );
	}

	static OutputStream getFileOutputStream( String fname, boolean append) throws IOException{
		return new kjava.io.FileOutputStream( fname, append );
	}

	static String JasymcaRC = "vfs/Jasymca.rc";


    public void startApp(){
		JSystem.initConsole(Display.getDisplay(this));
		FileHandler[] 	fh 	= { new TextEdit() };
		JSystem.browser	= new FileBrowser( JSystem.display, JSystem.console, fh);
		
		JSystem.console.setTitle("Jasymca");
        JSystem.showConsole();
        JSystem.console.stdout.println("Jasymca v. 1.01");

  		JSystem.out = JSystem.console.stdout;  
  		JSystem.err = JSystem.console.stdout;  
   		JSystem.in  = JSystem.console.stdin;
		
		start(JSystem.in, JSystem.out);
    }
	*/	
//////// End J2ME-only     -       

   public void destroyApp(boolean a){}
   public void pauseApp(){}
        
///


	// Jasymca = Java Symbolic Calculator
	// Syntax loosely related to GNU-Maxima
	
	// Internal Design:
	// Expressions are Lisp-lists in prefix notation
	// Elements are either parseable Lists or Algebraics
	// Variables are one of
	// --- SimpleVariable 			 = name (String) 
	// --- FunctionVariable          = function (LambdaAlgebraic) + Argument (Algebraic)
	// Algebraics are one of
	// --- Zahl.Unexakt ---> double complex
	// --- Zahl.Exakt	---> BigInteger rational complex
	// --- Polynomial 	---> Coefficients (Algebraic) + Variable
	// --- Polynomial.Constant 	---> Coefficients (Algebraic) + immutable Variable
	// --- Rational     ---> Nominator (Algebraic) + Denominator (Polynomial)
	// --- Vektor       ---> Components (Algebraic)
	// --- Matrix       ---> Vektor of Components
	
	// Environment for variables, functions
	// and operators
	// Stored by name, case-insensitive
	private Environment env; 
	
	// All input/output uses these channels
	PrintStream ps;
	InputStream is;
	



	// Parse constant, indicates end of input
	static String EXIT = "#!exit";


	public Jasymca(){
		// Setup environment
		env = new Environment();
		// Export environment: This should be removed one day
		Lambda.env = env;
		env.putValue("pi", Zahl.PI);
		env.putValue("ratepsilon", new Unexakt(2.0e-8));
		env.putValue("algepsilon", new Unexakt(1.0e-8));
		env.putValue("rombergit",  new Unexakt(11));
		env.putValue("rombergtol", new Unexakt(1.0e-4));
		env.putValue("+", new Add());
		env.putValue("-", new Sub());
		env.putValue("*", new Mult());
		env.putValue("/", new Div());
		env.putValue("^", new Pow());
		env.putValue(":", new Assign());
		env.putValue("&", new FunctionDefine());
		env.putValue(",", new Comma());
		env.putValue("#", new CreateVector()); 
		env.putValue("!", new Fact());
		
		// More initialization
		Zahl.init();
		
	}

	public void start(InputStream is, PrintStream ps){
		this.is = is;
		this.ps = ps;
		
		// Read startup file
		try{
			new LambdaLOADFILE().lambda(Lisp.list(JasymcaRC));
		}catch(Exception e){
		}

		// Counter for Line numbers
		int i = 1;
		
		// Read/eval/print loop
		while(true){
			ps.print( "(In"+i+") ");				// Prompt
			try{
				String s 		= readLine(is);
				Object expr 	= eval(s);
				if(expr==EXIT){
					ps.println("\nGoodbye.");
					break;
				}
				String ans = formatExpression(expr);
				ps.println( "(Out"+i+")     "+ans );
				env.putValue("Out"+i, expr);				// Save expression
				i++;
			}catch(ParseException e){
				ps.println("\n"+e);
			}
		}
	}

	
	// Read everything until ';' or EOF
	String readLine( InputStream in ){
		StringBuffer s = new StringBuffer();
		try{
			int c;
			while((c=in.read()) != -1 && c!=';')
				s.append((char)c);
		}catch(Exception e){
		}
		return s.toString();
	}
	
	String formatExpression(Object expr){
		if(expr instanceof Algebraic) {		
			return expr.toString();
		}
		return infix(expr);
	}

	// is x binary operand	
	boolean binaryq(String op){
		switch(op.charAt(0)){
			case '+': case '-': case '*': case '^':case '/':case ',': return true;
			default: return false;
		}
	}
		
	// Convert prefix expression to infix string
	String infix(Object x){
		if(Lisp.length(x)==0)
			return x.toString();
		if(Lisp.length(x)==1)
			return (Lisp.car(x)).toString();
		String op = (String)Lisp.car(x);
		Object args = Lisp.cdr(x);
		if( binaryq(op) ){	
			if(Lisp.length(args)==1)
				return "("+op+" "+infix(Lisp.car(args))+")";
			else
				return "("+infix(Lisp.car(args)) + op + infix(Lisp.car(Lisp.cdr(args)))+")";
		}
		return
			op + "("+infix(Lisp.list2atom(args))+")";
	}
					
					

	// Parsing proceeds in these steps:
	// (1) Parse Tokens to Objects       -------- read() 
	// (2) Convert Infix to Prefix       -------- in_pr()
	// (3) Evaluate prefix expression    -------- evalPrefix()
	Object eval(String s) throws ParseException{
		Object o;
		try{
			o = Lisp.expandExp(Lisp.expandFundef(Lisp.read(s)));
			return evalPrefix(Lisp.in_pr(o), false,env);
		}catch(Exception e){
			throw new ParseException(e.toString());
		}
	}
	
	// Evaluate prefix expression
	// if canon is set: eval to canonical expression (Algebraic)
	// Otherwise evaluate only numbers, result may be list
	public static Object evalPrefix(Object x, boolean canon, Environment env) 
			throws ParseException, JasymcaException{
		if(x==null) return "";
		if(x instanceof Algebraic)
			return (Algebraic)x;
		if(x instanceof String){ 
			Object val = (env==null?null:env.getValue((String)x));
			if(val!=null)
				return evalPrefix(val,canon,env);
			return canon ? new Polynomial( new SimpleVariable((String)x) ) : x;
		}
		if(x instanceof Pair && Lisp.car(x) instanceof String ){
			String id 	= (String)Lisp.car(x);
			x           = Lisp.cdr(x);
			// id might be Vektor reference 
			Object val = (env==null?null:env.getValue(id));

			if(val!=null && val instanceof Vektor){
				Object idv = evalPrefix( Lisp.car(x), canon , env);
				if(idv instanceof Vektor && ((Vektor)idv).coord.length==1){
					Algebraic idx = ((Vektor)idv).coord[0];
					if(idx instanceof Zahl)
						return ((Vektor)val).komp(((Zahl)idx).intval());
				}
			}
			// or id is Lambda operator
			if(! (val instanceof Lambda) ) {// Unknown function
				if(!canon)
					return Lisp.list(id, evalPrefix(Lisp.car(x),canon, env));
				else
					throw new JasymcaException("Can not evaluate to algebraic.");
			}
			Lambda f = (Lambda)val;
			
			// If f is a general command, supply args unevaluated
			if(!(f instanceof LambdaAlgebraic))
				return f.lambda(x);
			// Otherwise, evaluate args depending on canon	
			// Algebraic function f(x,y,z,...)
			// Supplied argument to lambda must be Algebraic
//			if( !",".equals(Lisp.car(x))){ // Single argument function		
				Object args = null;
				while(x instanceof Pair){
					args = Lisp.cons( evalPrefix( Lisp.car(x), canon, env ), args );
					x = Lisp.cdr(x);
				}
				args = Lisp.reverse(args);
				// at this point args is a prefix expression
				if( Lisp.algebraicq(args) )
					return f.lambda(args);
				if(canon)
					throw new JasymcaException("Can not evaluate to algebraic.");
				return Lisp.list(id, args);
				
		}
		throw new ParseException("Not a legal expression:" + x);
	}
	
	
	// Insert x in rule "f(x)" and evaluate expression to Algebraic
	public static Algebraic evalx(String rule, Algebraic x, Environment env) throws JasymcaException{
		try{
			Object prefix = Lisp.change( Lisp.compile_rule(rule), Lisp.list(Lisp.cons("x", x)) );
				return ((Algebraic)evalPrefix(prefix,true,env));
		}catch(Exception e){
			throw new JasymcaException("Could not evaluate expression "+rule+": "+e.toString());
		}
	}
	
}
				
