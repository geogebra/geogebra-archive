package jscl.math.function;

import java.util.Vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegrableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.ArrayComparator;

public class Constant extends Variable {
	public static final Generic e=new Exp(JSCLInteger.valueOf(1)).expressionValue();
	public static final Generic pi=new Constant("pi").expressionValue();
	public static final Generic i=new Sqrt(JSCLInteger.valueOf(-1)).expressionValue();
	public static final Generic half=new Inv(JSCLInteger.valueOf(2)).expressionValue();
	public static final Generic infinity=new Constant("infin").expressionValue();
	public static final Parser parser=ConstantParser.parser;
	public static final Parser identifier=Identifier.parser;
	public static final Parser primeCharacters=PrimeCharacters.parser;
	public static final Parser integer=IntegerParser.parser;
	public static final Parser subscriptParser=Subscript.parser;
	static final int PRIMECHARS=2;
	public int prime;
	public Generic subscript[];

	public Constant(String name) {
		this(name,0,new Generic[0]);
	}

	public Constant(String name, int prime, Generic subscript[]) {
		super(name);
		this.prime=prime;
		this.subscript=subscript;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		return null;
	}

	public Generic derivative(Variable variable) {
		if(isIdentity(variable)) return JSCLInteger.valueOf(1);
		else return JSCLInteger.valueOf(0);
	}

	public Generic substitute(Variable variable, Generic generic) {
		Constant v=(Constant)newinstance();
		for(int i=0;i<subscript.length;i++) {
			v.subscript[i]=subscript[i].substitute(variable,generic);
		}
		if(v.isIdentity(variable)) return generic;
		else return v.expressionValue();
	}

	public Generic expand() {
		Constant v=(Constant)newinstance();
		for(int i=0;i<subscript.length;i++) {
			v.subscript[i]=subscript[i].expand();
		}
		return v.expressionValue();
	}

	public Generic factorize() {
		Constant v=(Constant)newinstance();
		for(int i=0;i<subscript.length;i++) {
			v.subscript[i]=subscript[i].factorize();
		}
		return v.expressionValue();
	}

	public Generic elementary() {
		Constant v=(Constant)newinstance();
		for(int i=0;i<subscript.length;i++) {
			v.subscript[i]=subscript[i].elementary();
		}
		return v.expressionValue();
	}

	public Generic simplify() {
		Constant v=(Constant)newinstance();
		for(int i=0;i<subscript.length;i++) {
			v.subscript[i]=subscript[i].simplify();
		}
		return v.expressionValue();
	}

	public Generic numeric() {
		return new NumericWrapper(this);
	}

	public boolean isConstant(Variable variable) {
		return !isIdentity(variable);
	}

	public int compareTo(Variable variable) {
		if(this==variable) return 0;
		int c=comparator.compare(this,variable);
		if(c<0) return -1;
		else if(c>0) return 1;
		else {
			Constant v=(Constant)variable;
			c=name.compareTo(v.name);
			if(c<0) return -1;
			else if(c>0) return 1;
			else {
				if(prime<v.prime) return -1;
				else if(prime>v.prime) return 1;
				else return ArrayComparator.comparator.compare(subscript,v.subscript);
			}
		}
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(name);
		if(prime==0);
		else if(prime<=PRIMECHARS) buffer.append(primechars(prime));
		else buffer.append("{").append(prime).append("}");
		for(int i=0;i<subscript.length;i++) {
			buffer.append("[").append(subscript[i]).append("]");
		}
		return buffer.toString();
	}

	static String primechars(int n) {
		StringBuffer buffer=new StringBuffer();
		for(int i=0;i<n;i++) buffer.append("'");
		return buffer.toString();
	}

	public String toJava() {
		if(compareTo(new Constant("pi"))==0) return "JSCLDouble.valueOf(Math.PI)";
		else if(compareTo(new Constant("infin"))==0) return "JSCLDouble.valueOf(Double.POSITIVE_INFINITY)";
		StringBuffer buffer=new StringBuffer();
		buffer.append(name);
		if(prime==0);
		else if(prime<=PRIMECHARS) buffer.append(underscores(prime));
		else buffer.append("_").append(prime);
		for(int i=0;i<subscript.length;i++) {
			buffer.append("[").append(subscript[i].integerValue().intValue()).append("]");
		}
		return buffer.toString();
	}

	static String underscores(int n) {
		StringBuffer buffer=new StringBuffer();
		for(int i=0;i<n;i++) buffer.append("_");
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			if(prime==0) {
				if(subscript.length==0) {
					buffer.append(nameToMathML());
				} else {
					buffer.append("<msub>\n");
					buffer.append(1,nameToMathML());
					buffer.append(1,"<mrow>\n");
					for(int i=0;i<subscript.length;i++) {
						buffer.append(2,subscript[i].toMathML(null));
					}
					buffer.append(1,"</mrow>\n");
					buffer.append("</msub>\n");
				}
			} else {
				if(subscript.length==0) {
					buffer.append("<msup>\n");
					buffer.append(1,nameToMathML());
					buffer.append(1,primeToMathML());
					buffer.append("</msup>\n");
				} else {
					buffer.append("<msubsup>\n");
					buffer.append(1,nameToMathML());
					buffer.append(1,"<mrow>\n");
					for(int i=0;i<subscript.length;i++) {
						buffer.append(2,subscript[i].toMathML(null));
					}
					buffer.append(1,"</mrow>\n");
					buffer.append(1,primeToMathML());
					buffer.append("</msubsup>\n");
				}
			}
		} else {
			if(prime==0) {
				if(subscript.length==0) {
					buffer.append("<msup>\n");
					buffer.append(1,nameToMathML());
					buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
					buffer.append("</msup>\n");
				} else {
					buffer.append("<msubsup>\n");
					buffer.append(1,nameToMathML());
					buffer.append(1,"<mrow>\n");
					for(int i=0;i<subscript.length;i++) {
						buffer.append(2,subscript[i].toMathML(null));
					}
					buffer.append(1,"</mrow>\n");
					buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
					buffer.append("</msubsup>\n");
				}
			} else {
				if(subscript.length==0) {
					buffer.append("<msup>\n");
					buffer.append(1,"<msup>\n");
					buffer.append(2,nameToMathML());
					buffer.append(2,primeToMathML());
					buffer.append(1,"</msup>\n");
					buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
					buffer.append("</msup>\n");
				} else {
					buffer.append("<msup>\n");
					buffer.append(1,"<msubsup>\n");
					buffer.append(2,nameToMathML());
					buffer.append(2,"<mrow>\n");
					for(int i=0;i<subscript.length;i++) {
						buffer.append(3,subscript[i].toMathML(null));
					}
					buffer.append(2,"</mrow>\n");
					buffer.append(2,primeToMathML());
					buffer.append(1,"</msubsup>\n");
					buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
					buffer.append("</msup>\n");
				}
			}
		}
		return buffer.toString();
	}

	String primeToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		if(prime<=PRIMECHARS) {
			buffer.append("<mo>"+primechars(prime)+"</mo>\n");
		} else {
			buffer.append("<mfenced>\n");
			buffer.append(1,"<mn>").append(prime).append("</mn>\n");
			buffer.append("</mfenced>\n");
		}
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Constant(name,prime,new Generic[subscript.length]);
	}
}

class ConstantParser extends Parser {
	public static final Parser parser=new ConstantParser();

	private ConstantParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		String name;
		int prime=0;
		Vector vector=new Vector();
		try {
			name=(String)Constant.identifier.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		try {
			prime=((Integer)Prime.parser.parse(str,pos)).intValue();
		} catch (ParseException e) {}
		while(true) {
			try {
				Generic a=(Generic)Subscript.parser.parse(str,pos);
				vector.addElement(a);
			} catch (ParseException e) {
				break;
			}
		}
		Generic a[]=new Generic[vector.size()];
		vector.copyInto(a);
		Constant v=new Constant(name,prime,a);
		return v;
	}
}

class Identifier extends Parser {
	public static final Parser parser=new Identifier();

	private Identifier() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		StringBuffer buffer=new StringBuffer();
		skipWhitespaces(str,pos);
//		if(pos[0]<str.length() && Character.isLetter(str.charAt(pos[0]))) {
		if(pos[0]<str.length() && isLetter(str.charAt(pos[0]))) {
			char c=str.charAt(pos[0]++);
			buffer.append(c);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
//		while(pos[0]<str.length() && Character.isLetterOrDigit(str.charAt(pos[0]))) {
		while(pos[0]<str.length() && (isLetter(str.charAt(pos[0])) || Character.isDigit(str.charAt(pos[0])))) {
			char c=str.charAt(pos[0]++);
			buffer.append(c);
		}
		return buffer.toString();
	}

	static boolean isLetter(char c) {
		return (c>='A' && c<='Z') || (c>='a' && c<='z');
	}
}

class Prime extends Parser {
	public static final Parser parser=new Prime();

	private Prime() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		int c;
		try {
			c=((Integer)PrimeCharacters.parser.parse(str,pos)).intValue();
		} catch (ParseException e) {
			try {
				c=((Integer)Superscript.parser.parse(str,pos)).intValue();
			} catch (ParseException e2) {
				throw e2;
			}
		}
		return new Integer(c);
	}
}

class PrimeCharacters extends Parser {
	public static final Parser parser=new PrimeCharacters();

	private PrimeCharacters() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		int c;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='\'') {
			str.charAt(pos[0]++);
			c=1;
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		while(pos[0]<str.length() && str.charAt(pos[0])=='\'') {
			str.charAt(pos[0]++);
			c++;
		}
		return new Integer(c);
	}
}

class Superscript extends Parser {
	public static final Parser parser=new Superscript();

	private Superscript() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		int c;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='{') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			c=((Integer)IntegerParser.parser.parse(str,pos)).intValue();
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='}') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return new Integer(c);
	}
}

class IntegerParser extends Parser {
	public static final Parser parser=new IntegerParser();

	private IntegerParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
//		StringBuffer buffer=new StringBuffer();
		int n;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
			char c=str.charAt(pos[0]++);
//			buffer.append(c);
			n=c-'0';
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		while(pos[0]<str.length() && Character.isDigit(str.charAt(pos[0]))) {
			char c=str.charAt(pos[0]++);
//			buffer.append(c);
			n=10*n+(c-'0');
		}
//		return new Integer(buffer.toString());
		return new Integer(n);
	}
}

class Subscript extends Parser {
	public static final Parser parser=new Subscript();

	private Subscript() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='[') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			a=(Generic)Expression.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])==']') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return a;
	}
}
