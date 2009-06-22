package jasymca;

import geogebra.main.Application;

/*------------------------------------------------------------*/


// A little bit of Lisp
public class Lisp{
	static final boolean debug = false;
	static void p(String s) { if (debug) Application.debug(s); }

	// Basic Lisp/Scheme functions, mostly adapted from Norvigs JScheme (www.norvig.org)
	// Amazingly, it is possible to code Scheme programs in Java with few changes
	// The Java code does not conform to any standard.
	
		
  	public static Pair list(Object a) {
    	return new Pair(a, null);
  	}

	public static Object list(Object a, Object b){
		if(listq(b)) return cons(a,b);
		return cons(a,list(b));
	}
	
	static boolean listq(Object x){
		if(x==null) return true; // empty list
		if(!(x instanceof Pair)) return false;
		return listq(cdr(x));
	}
	

  	public static Object car(Object a){
  		if(a instanceof Pair)
			return ((Pair)a).car;
		return null;
	}
  	public static Object cdr(Object a){
  		if(a instanceof Pair)
			return ((Pair)a).cdr;
		return null;
	}
	
	public static Pair cons(Object a, Object b){
		return new Pair(a,b);
	}
	
	
	public static boolean equal(Object a, Object b){
		return a==null? b==null:a.equals(b);
	}
	
  	public static int length(Object x) {
    	int len = 0;
    	while (x instanceof Pair) {
      		len++;
      		x = cdr(x);
    	}
    	return len;
  	}

	static boolean member(Object x, Object args){
		while(args instanceof Pair){
			if(car(args).equals(x))
				return true;
			args = cdr(args);
		}
		return false;
	}
	
	static Object remove( Object x, Object args ){
		if(args instanceof Pair){
			Object result = null;
			while(args instanceof Pair){
				if(!car(args).equals(x))
					result = cons(car(args),result);
				args = cdr(args);
			}
			return reverse(result);
		}
		return args;
	}

				

  	static Object append(Object x, Object y) {
    	if (x instanceof Pair) 
			return cons(car(x), append(cdr(x), y));
    	else return y;
  	}

	static Object list2atom(Object x){
		return length(x)==1?car(x):x;
	}
	
	
	public static Object reverse(Object x) {
    	Object result = null;
    	while (x instanceof Pair) {
      		result = cons(car(x), result);
      		x = cdr(x);
    	}
    	return result;
  	}
	
	// Also reverse inner lists
	public static Object mapreverse(Object x){
		if(!(x instanceof Pair))
			return x;
		Object r = null;
		while(x instanceof Pair){
			r = cons(mapreverse(car(x)), r);
			x = cdr(x);
		}
		return r;
	}

	
	// is this a list of Objects of type c
	public static boolean kindof( Object x, Class c){
		if(c.isInstance(x)) return true;
		if(!(x instanceof Pair))
			return false;
		while(x instanceof Pair){
			if(!(c.isInstance(car(x))))
				return false;
			x = cdr(x);
		}
		return true;
	}
	
	// is this a list of Algebraics
	public static boolean algebraicq(Object x){
		try{
			//Class a = Class.forName("jasymca.Algebraic");
			Class a = jasymca.Algebraic.class;
			return kindof(x,a);
		}catch(Exception e){
			throw new RuntimeException("Installation Error.");
		}
	}
		
			
	// is this a list of Numbers
	public static boolean numbers( Object x){
		if( x instanceof Zahl || (x instanceof Vektor && ((Vektor)x).number()))
			return true;
		if(!(x instanceof Pair))
			return false;
		while(x instanceof Pair){
			if(!numbers(car(x)))
				return false;
			x = cdr(x);
		}
		return true;
	}
			
			

	// Convert Object from Lisp-String 	
	public static Object read( String s) throws ParseException{
		return nextObject(new StringBuffer("("+s+")"));
	}
	

	static boolean whitespace(char c){
		return oneof(c," \t\n\r");
	}
	
	static boolean oneof(char c, String s){
		for(int i=0; i<s.length(); i++)
			if(c==s.charAt(i)) return true;
		return false;
	}
			

	// Break up String s into Objects
	// Objects are Numbers, Strings separated by meaning, lists of Objects (x1 x2 x3)
	//and vektors [ x1 x2 x3 ]
	public static Object nextObject(StringBuffer s) throws ParseException{
		int i=0;
		while(i<s.length() && whitespace(s.charAt(i))) i++;
		if(i==s.length()) return null;
		char c = s.charAt(i);
		switch(c){
			case '"':// Read string until next "
				s.delete(0,i+1);
				String st = s.toString();
				i = st.indexOf('"');
				if(i<0)
					throw new ParseException("Unclosed Quote.");
				st = st.substring(0,i); 	
				s.delete(0,i+1);
				return st;
			case '(':			
				// Read list, recurse until ')'
				Object result = null, next, prev="+";
				s.delete(0,i+1);
				while( !")".equals(next = nextObject(s)) ){
					if(next==null && s.length()==0)
						throw new ParseException("Unclosed Parentheses.");	
					result = cons(next, result);
					prev = next;
				}
				return reverse(result);
			case '[':
				// Read vektor, recurse until ']'
				result = null;// Use # to mark vectors
				s.delete(0,i+1);
				while( !"]".equals(next = nextObject(s)) ){
					if(next==null && s.length()==0)
						throw new ParseException("Unclosed Parentheses.");				
					result = cons(next, result);
				}
				return cons("#", list(reverse(result))); // Mark vektors with #
			case ')':case ']':case '+':case '-':case ',':case '=':
			case '*':case '/':case '^':case '!':case ':':
				s.delete(0,i+1); return String.valueOf(c);
			default: // Try to read number
				for(int k=s.length(); k>i; k--){
					try{
						double x = Double.parseDouble(s.toString().substring(i,k));
						s.delete(0,k);
						
						// BEGIN Markus Hohenwarter:
						// keep Integers als Exakt numbers
						//long xround = Math.round(x);
						//if (xround == x)
						//	return new  Exakt( BigInteger.valueOf(xround));
						// END Markus Hohenwarter
						
						return new Unexakt(x);
					}catch(Exception e){
					}
				}
				// Ok, no number and no operator: must be variable
				// Read until next symbol
				int k=i+1;
				while(k<s.length() && !oneof(s.charAt(k), "()[]\n\t\r +-*/^!,:=")) k++;
				String t = s.toString().substring(i,k);
				s.delete(0,k);
				return expConstant(t);						
		}	
	}

				
	public static Object expConstant(String s){
			// Numeric constants
			if(s.equalsIgnoreCase("i"))
				return Zahl.IONE;
			else
				return s;
	}
	
	// Intersperse "*":   a b  ---> a * b
	// Not used any more
	public static Object expandMult( Object x){
		if(!(x instanceof Pair))
			return x;
		Object lhs = car(x), rhs = car(cdr(x));
		if(lhs!=null && rhs!=null && noop(lhs) && noinfixop(rhs)){
			return cons(expandMult(lhs),cons("*",expandMult(cdr(x))));
		}else
			return cons(expandMult(lhs), expandMult(cdr(x)));
	}

	// Expand      "**":  a**b ---> a ^ b
	public static Object expandExp( Object x){
		if(!(x instanceof Pair)) return x;
		Object r = null,prev="+";
		while(x instanceof Pair){
			Object next = car(x);
//			if(next.equals("*") && car(cdr(x)).equals("*")){
			if("*".equals(next) && "*".equals(car(cdr(x)))){
				r = cons("^",r);
				x = cdr(x);
			}else{
				r = cons(expandExp(next),r);
			}
			x = cdr(x);
		}
		return reverse(r);
	}
		
	// Expand      ":=":  a:=b ---> a & b
	// We use single character operators
	public static Object expandFundef( Object x){
		if(!(x instanceof Pair)) return x;
		Object r = null,prev="+";
		while(x instanceof Pair){
			Object next = car(x);
			if(":".equals(next) && "=".equals(car(cdr(x)))){
				r = cons("&",r);
				x = cdr(x);
			}else{
				r = cons(expandExp(next),r);
			}
			x = cdr(x);
		}
		return reverse(r);
	}
		
	
	public static boolean noop(Object x){
		if(!(x instanceof String))
			return false;
		Object a = Lambda.env.getValue((String)x);
		return !(a instanceof Lambda);
	}
	
	public static boolean noinfixop(Object x){
		return !(x instanceof String) || ((String)x).length()!=1 || 
				!oneof(((String)x).charAt(0),"^:+-*/!,=");
	}
	


	// This simple parser is adapted from Norvig's
	// book. It saves more than 50% space compared
	// to a solution based on javacc and jjtree
	// and is sufficient for our purpose
	
	// Variables are some Strings
	static boolean variableq(Object x){
		return x instanceof String && ((String)x).length()==1 && 
				oneof(((String)x).charAt(0),"xyz");
	}
	
	// Different Match conditions 
	// x,y,z match anything
	// a,b,c match anything except some operators; not used
	static boolean matches(Object var, Object x){
		switch(((String)var).charAt(0)){
			case 'x':case 'y': case 'z': return true;
			case 'a':case 'b': case 'c': return noop(x);
		}
		return false;
	}
	
	

	// like pat_match_rl, but search starting right
	public static Object pat_match( Object pat, Object input ){
		Object p = pat_match_rl(mapreverse(pat), mapreverse(input));
		Object r = null;
		while(p instanceof Pair){
			Pair b = (Pair)car(p);
			if(listq(cdr(b)))
				r = cons( cons(car(b), mapreverse(cdr(b))), r);
			else
				r = cons(b, r);
			p=cdr(p);
		}
		return r;
	}


	//return list of bindings, or "MATCH", or null (no match)
	//  pat = (x + 4 y); input = (2 + 3 + 4 2) -->  ((x . 2.0 + 3.0) (y . 2.0))
	static final String MATCH="#!MATCH";
	
	public static Object pat_match_rl( Object pat, Object input ){
		if(input==null && pat==null) return MATCH;
		if(pat == null) return null;
		if(length(input) < length(pat)) return null;
		Object p = car(pat);
		if(length(pat) == 1){
			input = list2atom(input);
			if( variableq(p) )
				if( matches(p,input)) 
					return list( cons(p, input) );
				else
					return null; 
			else if(p.equals(input)) return MATCH;
			else return null;
		}
									
		if(variableq(p)){ 
			Object pr = cdr(pat), r = list(car(input)), q;
			while( (q=pat_match_rl( pr, cdr(input)))==null
					|| !matches(p, list2atom(reverse(r))) ){
				input = cdr(input);
				if(car(input)==null) 
					return null;
				r = cons(car(input), r);
			}
			r = list2atom(reverse(r));
			if(q == MATCH) // No further Variables, literal match
				return list( cons(p, r) );
			else
				return cons( cons(p, r), q );
		}else if(p.equals(car(input)))
			return pat_match_rl(cdr(pat), cdr(input));
		else
			return null;
	}
	
	// Example: exp = (sin x + z)
	//          match_pairs = ((x . 3) (z . 4)) 
	//          result = (sin 3 + 4)
	public static Object change(Object exp, Object match_pairs){
		if(! (match_pairs instanceof Pair))
			return exp;
		Object r = null;
		while(exp instanceof Pair){
		// Experimental: Allows substitutions of complex expressions,e.g
		// x^2+2, x-1, y+x-1 ---> y+x^2-1
			Object sp = car(exp);
			Object mp = match_pairs;
			while(mp instanceof Pair){
				Pair m = (Pair)car(mp);
				if(sp.equals(m.car)){
					r = cons(m.cdr, r);
					break;
				}else
					mp = cdr(mp);
			}
			if(listq(sp) && !(mp instanceof Pair) ) {
				r = cons(change(sp,match_pairs),r);
				exp = cdr(exp);
				continue;
			}

			if(!(mp instanceof Pair)) // No match found
				r = cons(sp, r);
			exp = cdr(exp);
/*
			Object sp = car(exp);
			if(listq(sp)){
				r = cons(change(sp,match_pairs),r);
				exp = cdr(exp);
				continue;
			}
			Object mp = match_pairs;
			while(mp instanceof Pair){
				Pair m = (Pair)car(mp);
				if(sp.equals(m.car)){
					r = cons(m.cdr, r);
					break;
				}else
					mp = cdr(mp);
			}
			if(!(mp instanceof Pair)) // No match found
				r = cons(sp, r);
			exp = cdr(exp);
*/
		}		
		return reverse(r);
	}
	
	//Find the first rule in rules that matches input, and apply rule.
	public static Object translator(Object rules, Object input){
		if(length(rules)==0) return null;
		Object 	rule 	= car(rules), 
				match 	= pat_match( car(rule), input );	
		if(match!=null){	
			return change( cdr(rule), match );
		}else
			return translator( cdr(rules), input );
	}


	public static Object exp_lhs(Object expr){
		return car(cdr(expr));
	}
	
	public static Object exp_rhs(Object expr){
		return list2atom(cdr (cdr(expr)));
	}
		
	public static Object in_pr(	Object expr ){
		if(inpr_rules == null)
			inpr_rules = makeRules( inpr_rules_in );
		switch(length(expr)){
			case 0: return expr;
			case 1: return in_pr(car(expr));
			default: 
				Object r = translator(inpr_rules, expr);
				if(r!=null){
					int len = length(r);
					if(len==2)
						return cons(car(r), list(in_pr(exp_lhs(r))));
					else
						return cons(car(r), list((in_pr(exp_lhs(r))),
											list(in_pr(exp_rhs(r)))));
				}
				if(car(expr) instanceof String){ // Function?
					return list(car(expr), list(in_pr(cdr(expr))));
				}
				
				// TODO: remove
				try {
					throw new Exception("Illegal expression: "+expr);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Application.debug("Illegal expression: "+expr);
				return null;
		}
	}
	
	static Object makeRules(String[][] rules_in){
		Object r = null;
		for(int i=0; i<rules_in.length; i++){
			try{
			Object pat = read( rules_in[i][0]);
			Object exp = read( rules_in[i][1]);
			r = cons(cons(pat,exp),r);
			}catch(Exception e){}
		}
		return reverse(r);
	}
	
	static Object inpr_rules = null;	

	//A list of rules, ordered by precedence.
	static String[][] inpr_rules_in=
		{{ "x , y" , ", x y"},
		 { "x : y" , ": x y"},
         { "x & y" , "& x y"},
         { "x = y" , "- x y"},
         { "x + y" , "+ x y"},
         { "x - y" , "- x y"},
         { "- x"   , "- x"  },
         { "+ x"   , "+ x"  },
         { "d y / d x" , "d y x"},
         { "Int y d x" , "int y x"},
         { "x * y" , "* x y"},
         { "x / y" , "/ x y"},
         { "x !" , "! x"},
         { "x ^ y" , "^ x y"}};

	
	
	// Conversions from and to lists ("prefix")

	// convert canonical base^exponent to prefix form.
	public static Object exp_pr(Object base, int exponent){
		switch(exponent){
			case 0: return Zahl.ONE;
			case 1: return base;
			default: return cons( "^", cons(base,list( new Unexakt(exponent))));
		}
	}
	
  	// convert arg1 op arg2 op arg3 .... to prefix form
	public static Object args_prefix(String op, Zahl identity, Object args){
		args = remove( identity, args );
		if(args == null)
			return identity;
		if( op.equals("*") && member(Zahl.ZERO, args) )
			return Zahl.ZERO;
		if( length(args) == 1){
			return car(args);
		}
		Object result = list(op);
		for(Object arg = car(args); arg != null; args = cdr(args), arg = car(args)){
			if( op.equals(car(arg)) )
				result = append(result, cdr(arg));
			else{
				result = append(result, list(arg));
			}
		}
		return result;
	}
	
	
	public static Object compile_rules( String[][] rules ){
		Object r = null;
		for(int i=0; i<rules.length; i++){
			try{
				Object pat = compile_rule(rules[i][0]);
				Object exp = compile_rule(rules[i][1]);
				r = cons(cons(pat,exp),r);
			}catch(Exception e){}
		}
		return reverse(r);
	}
	
	public static Object compile_rule(String s) throws ParseException{
		return in_pr( expandMult(read( s)));
	}

	// Input and Output are Prefix format	
	public static Object expand( Object expr, Object rules){
		switch(length(expr)){
			case 0: return expr;
			case 1: return expand(car(expr), rules);
			default:Object r = translator(rules, expr);
				if(r != null) 
				// Applied rule, try more 
					return expand(r,rules);
				// No rule applied:
				// Break up list and apply rules to each car
				while(expr instanceof Pair){
					r = cons( expand(car(expr),rules), r);
					expr=cdr(expr);
				}
				return reverse(r);
		}
	}

}


