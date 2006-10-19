package jscl.math;

import java.util.Iterator;
import java.util.Map;

import jscl.math.function.Frac;
import jscl.math.function.Function;
import jscl.math.function.Inv;
import jscl.math.function.Pow;
import jscl.math.operator.Factorial;
import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;
import jscl.util.MyMap;
import jscl.util.MyTreeMap;

public class Expression extends Generic {
	public static final Parser parser=ExpressionParser.parser;
	public static final Parser commaAndExpression=CommaAndExpression.parser;
	final MyMap content=new MyTreeMap();

	Expression() {}

	public Expression add(Expression expression) {
		Expression ex=(Expression)valueof(this);
		ex.put(expression);
		return ex;
	}

	public Generic add(Generic generic) {
		if(generic instanceof Expression) {
			return add((Expression)generic);
		} else if(generic instanceof JSCLInteger) {
			return add(valueof(generic));
		} else {
			return generic.valueof(this).add(generic);
		}
	}

	public Expression subtract(Expression expression) {
		Expression ex=(Expression)valueof(this);
		Iterator it=expression.content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			ex.put(
				(Literal)e.getKey(),
				(JSCLInteger)((JSCLInteger)e.getValue()).negate()
			);
		}
		return ex;
	}

	public Generic subtract(Generic generic) {
		if(generic instanceof Expression) {
			return subtract((Expression)generic);
		} else if(generic instanceof JSCLInteger) {
			return subtract(valueof(generic));
		} else {
			return generic.valueof(this).subtract(generic);
		}
	}

	public Expression multiply(Expression expression) {
		Expression ex=(Expression)newinstance();
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			Iterator it2=expression.content.entrySet().iterator();
			while(it2.hasNext()) {
				Map.Entry e2=(Map.Entry)it2.next();
				ex.put(
					(Literal)l.multiply((Literal)e2.getKey()),
					(JSCLInteger)en.multiply((JSCLInteger)e2.getValue())
				);
			}
		}
		return ex;
	}

	public Generic multiply(Generic generic) {
		if(generic instanceof Expression) {
			return multiply((Expression)generic);
		} else if(generic instanceof JSCLInteger) {
			Expression ex=(Expression)newinstance();
			Iterator it=content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				ex.put(
					(Literal)e.getKey(),
					(JSCLInteger)((JSCLInteger)e.getValue()).multiply(generic)
				);
			}
			return ex;
		} else {
			return generic.multiply(this);
		}
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		Generic a[]=divideAndRemainder(generic);
		if(a[1].signum()==0) return a[0];
		else throw new NotDivisibleException();
	}

	public Generic[] divideAndRemainder(Generic generic) throws ArithmeticException {
		if(generic instanceof Expression) {
			Expression ex=(Expression)generic;
			Literal l1=literalScm();
			Literal l2=ex.literalScm();
			Literal l=(Literal)l1.gcd(l2);
			Variable va[]=l.variables();
			if(va.length==0) {
				if(signum()==0 && ex.signum()!=0) return new Generic[] {this,JSCLInteger.valueOf(0)};
				else try {
					return divideAndRemainder(ex.integerValue());
				} catch (NotIntegerException e) {
					return new Generic[] {JSCLInteger.valueOf(0),this};
				}
			} else {
				Polynomial p[]=UnivariatePolynomial.valueOf(this,va[0]).divideAndRemainder(UnivariatePolynomial.valueOf(ex,va[0]));
				return new Generic[] {p[0].genericValue(),p[1].genericValue()};
			}
		} else if(generic instanceof JSCLInteger) {
			try {
				Expression ex=(Expression)newinstance();
				Iterator it=content.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry e=(Map.Entry)it.next();
					ex.put(
						(Literal)e.getKey(),
						(JSCLInteger)((JSCLInteger)e.getValue()).divide(generic)
					);
				}
				return new Generic[] {ex,JSCLInteger.valueOf(0)};
			} catch (NotDivisibleException e) {
				return new Generic[] {JSCLInteger.valueOf(0),this};
			}
		} else {
			return generic.valueof(this).divideAndRemainder(generic);
		}
	}

	public Generic gcd(Generic generic) {
		if(generic instanceof Expression) {
			Expression ex=(Expression)generic;
			Literal l1=literalScm();
			Literal l2=ex.literalScm();
			Literal l=(Literal)l1.gcd(l2);
			Variable va[]=l.variables();
			if(va.length==0) {
				if(signum()==0) return ex;
				else return gcd(ex.gcd());
			} else return UnivariatePolynomial.valueOf(this,va[0]).gcd(UnivariatePolynomial.valueOf(ex,va[0])).genericValue();
		} else if(generic instanceof JSCLInteger) {
			if(generic.signum()==0) return this;
			else return gcd().gcd(generic);
		} else {
			return generic.valueof(this).gcd(generic);
		}
	}

	public Generic gcd() {
		Generic a=JSCLInteger.valueOf(0);
		for(Iterator it=content.values().iterator();it.hasNext();) {
			a=a.gcd((JSCLInteger)it.next());
		}
		return a;
	}

	Literal literalScm() {
		Literal l=new Literal();
		for(Iterator it=content.keySet().iterator();it.hasNext();) {
			l=l.scm((Literal)it.next());
		}
		return l;
	}

	public Generic negate() {
		Expression ex=(Expression)newinstance();
		return ex.subtract(this);
	}

	public int signum() {
		if(content.isEmpty()) return 0;
		else return ((Generic)content.values().iterator().next()).signum();
	}

	public int degree() {
		return 0;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		if(isPolynomial(variable)) {
			return UnivariatePolynomial.valueOf(this,variable).antiderivative().genericValue();
		} else {
			try {
				Variable v=variableValue();
				try {
					return v.antiderivative(variable);
				} catch (NotIntegrableException e) {
					if(v instanceof Frac) {
						Function f=(Function)v;
						if(f.parameter[1].isConstant(variable)) {
							return new Inv(f.parameter[1]).evaluate().multiply(f.parameter[0].antiderivative(variable));
						}
					}
				}
			} catch (NotVariableException e) {
				Generic a[]=sumValue();
				if(a.length>1) {
					Generic s=JSCLInteger.valueOf(0);
					for(int i=0;i<a.length;i++) {
						s=s.add(a[i].antiderivative(variable));
					}
					return s;
				} else {
					Generic p[]=a[0].productValue();
					Generic s=JSCLInteger.valueOf(1);
					Generic t=JSCLInteger.valueOf(1);
					for(int i=0;i<p.length;i++) {
						if(p[i].isConstant(variable)) s=s.multiply(p[i]);
						else t=t.multiply(p[i]);
					}
					if(s.compareTo(JSCLInteger.valueOf(1))==0);
					else return s.multiply(t.antiderivative(variable));
				}
			}
		}
		throw new NotIntegrableException();
	}

	public Generic derivative(Variable variable) {
		Generic s=JSCLInteger.valueOf(0);
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			Generic a=UnivariatePolynomial.valueOf(this,v).derivative(variable).genericValue();
			s=s.add(a);
		}
		return s;
	}

	public Generic substitute(Variable variable, Generic generic) {
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			l.content.put(v,v.substitute(variable,generic));
		}
		return substitute(l.content);
	}

	Generic substitute(MyMap map) {
		Generic s=JSCLInteger.valueOf(0);
		Iterator it=content.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			Generic a=en;
			Iterator it2=l.content.entrySet().iterator();
			while(it2.hasNext()) {
				Map.Entry e2=(Map.Entry)it2.next();
				Variable v=(Variable)e2.getKey();
				int c=((Integer)e2.getValue()).intValue();
				Generic a2=(Generic)map.get(v);
				a2=a2.pow(c);
				if(Matrix.product(a,a2)) throw new ArithmeticException();
				a=a.multiply(a2);
			}
			s=s.add(a);
		}
		return s;
	}

	public Generic expand() {
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			l.content.put(v,v.expand());
		}
		return substitute(l.content);
	}

	public Generic factorize() {
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			l.content.put(v,v.factorize());
		}
		Generic a=substitute(l.content);
		Factorization s=new Factorization();
		s.compute(a);
		return s.getValue();
	}

	public Generic elementary() {
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			l.content.put(v,v.elementary());
		}
		return substitute(l.content);
	}

	public Generic simplify() {
		Simplification s=new Simplification();
		s.compute(this);
		return s.getValue();
	}

	public Generic numeric() {
		try {
			return integerValue().numeric();
		} catch (NotIntegerException e) {
			Literal l=literalScm();
			Iterator it=l.content.keySet().iterator();
			while(it.hasNext()) {
				Variable v=(Variable)it.next();
				l.content.put(v,v.numeric());
			}
			return substitute(l.content);
		}
	}

	public Generic valueof(Generic generic) {
		Expression ex=(Expression)newinstance();
		ex.put(generic);
		return ex;
	}

	public Generic[] sumValue() {
		Generic a[]=new Generic[content.size()];
		Iterator it=content.entrySet().iterator();
		for(int i=0;i<a.length;i++) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			Expression ex=(Expression)newinstance();
			ex.put(l,en);
			a[i]=ex;
		}
		return a;
	}

	public Generic[] productValue() throws NotProductException {
		int n=content.size();
		if(n==0) return new Generic[] {JSCLInteger.valueOf(0)};
		else if(n==1) {
			Map.Entry e=(Map.Entry)content.entrySet().iterator().next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			Generic p[]=l.productValue();
			if(en.compareTo(JSCLInteger.valueOf(1))==0) return p;
			else {
				Generic a[]=new Generic[p.length+1];
				for(int i=0;i<p.length;i++) a[i+1]=p[i];
				a[0]=en;
				return a;
			}
		} else throw new NotProductException();
	}

	public Object[] powerValue() throws NotPowerException {
		int n=content.size();
		if(n==0) return new Object[] {JSCLInteger.valueOf(0),new Integer(1)};
		else if(n==1) {
			Map.Entry e=(Map.Entry)content.entrySet().iterator().next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.powerValue();
			else if(l.degree()==0) return en.powerValue();
			else throw new NotPowerException();
		} else throw new NotPowerException();
	}

	public Expression expressionValue() throws NotExpressionException {
		return this;
	}

	public JSCLInteger integerValue() throws NotIntegerException {
		int n=content.size();
		if(n==0) return JSCLInteger.valueOf(0);
		else if(n==1) {
			Map.Entry e=(Map.Entry)content.entrySet().iterator().next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(l.degree()==0) return en;
			else throw new NotIntegerException();
		} else throw new NotIntegerException();
	}

	public Variable variableValue() throws NotVariableException {
		int n=content.size();
		if(n==0) throw new NotVariableException();
		else if(n==1) {
			Map.Entry e=(Map.Entry)content.entrySet().iterator().next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(en.compareTo(JSCLInteger.valueOf(1))==0) return l.variableValue();
			else throw new NotVariableException();
		} else throw new NotVariableException();
	}

	public Variable[] variables() {
		return literalScm().variables();
	}

	public boolean isPolynomial(Variable variable) {
		boolean s=true;
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			s=s && (v.isConstant(variable) || v.isIdentity(variable));
		}
		return s;
	}

	public boolean isConstant(Variable variable) {
		boolean s=true;
		Literal l=literalScm();
		Iterator it=l.content.keySet().iterator();
		while(it.hasNext()) {
			Variable v=(Variable)it.next();
			s=s && v.isConstant(variable);
		}
		return s;
	}

	public JSCLVector grad(Variable variable[]) {
		Generic v[]=new Generic[variable.length];
		for(int i=0;i<variable.length;i++) v[i]=derivative(variable[i]);
		return new JSCLVector(v);
	}

	public Generic laplacian(Variable variable[]) {
		return grad(variable).divergence(variable);
	}

	public int compareTo(Expression expression) {
		Iterator it1=content.entrySet().iterator(true);
		Iterator it2=expression.content.entrySet().iterator(true);
		while(true) {
			boolean b1=!it1.hasNext();
			boolean b2=!it2.hasNext();
			if(b1 && b2) return 0;
			else if(b1) return -1;
			else if(b2) return 1;
			else {
				Map.Entry e1=(Map.Entry)it1.next();
				Map.Entry e2=(Map.Entry)it2.next();
				Literal l1=(Literal)e1.getKey();
				Literal l2=(Literal)e2.getKey();
				int c=l1.compareTo(l2);
				if(c<0) return -1;
				else if(c>0) return 1;
				else {
					JSCLInteger en1=(JSCLInteger)e1.getValue();
					JSCLInteger en2=(JSCLInteger)e2.getValue();
					c=en1.compareTo(en2);
					if(c<0) return -1;
					else if(c>0) return 1;
				}
			}
		}
	}

	public int compareTo(Generic generic) {
		if(generic instanceof Expression) {
			return compareTo((Expression)generic);
		} else if(generic instanceof JSCLInteger) {
			return compareTo(valueof(generic));
		} else {
			return generic.valueof(this).compareTo(generic);
		}
	}

	public static Expression valueOf(Variable variable) {
		Literal l=new Literal();
		l.put(variable,new Integer(1));
		return valueOf(l);
	}

	public static Expression valueOf(Literal literal) {
		Expression ex=new Expression();
		ex.put(literal,JSCLInteger.valueOf(1));
		return ex;
	}

	public static Expression valueOf(JSCLInteger integer) {
		Expression ex=new Expression();
		ex.put(integer);
		return ex;
	}

	public static Expression valueOf(String str) throws ParseException {
		int pos[]=new int[1];
		Generic a;
		try {
			a=(Generic)ExpressionParser.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		Parser.skipWhitespaces(str,pos);
		if(pos[0]<str.length()) {
			throw new ParseException();
		}
		Expression ex=new Expression();
		ex.put(a);
		return ex;
	}

	void put(Generic generic) {
		if(generic instanceof Expression) {
			Expression ex=(Expression)generic;
			Iterator it=ex.content.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry e=(Map.Entry)it.next();
				put(
					(Literal)e.getKey(),
					(JSCLInteger)e.getValue()
				);
			}
		} else if(generic instanceof JSCLInteger) {
			put(new Literal(),(JSCLInteger)generic);
		} else throw new ArithmeticException();
	}

	void put(Literal literal, JSCLInteger integer) {
		Object o=content.get(literal);
		if(o!=null) {
			integer=(JSCLInteger)integer.add((JSCLInteger)o);
			if(integer.signum()==0) content.remove(literal);
			else content.put(literal,integer);
		} else {
			if(integer.signum()==0);
			else content.put(literal,integer);
		}
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if(signum()==0) buffer.append("0");
		Iterator it=content.entrySet().iterator();
		for(int i=0;it.hasNext();i++) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(en.signum()>0 && i>0) buffer.append("+");
			if(l.degree()==0) buffer.append(en);
			else {
				if(en.compareTo(JSCLInteger.valueOf(1))==0);
				else if(en.compareTo(JSCLInteger.valueOf(-1))==0) buffer.append("-");
				else buffer.append(en).append("*");
				buffer.append(l);
			}
		}
		return buffer.toString();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		if(signum()==0) buffer.append("JSCLDouble.valueOf(0)");
		Iterator it=content.entrySet().iterator();
		for(int i=0;it.hasNext();i++) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(i>0) {
				if(en.signum()<0) {
					buffer.append(".subtract(");
					en=(JSCLInteger)en.negate();
				} else buffer.append(".add(");
			}
			if(l.degree()==0) buffer.append(en.toJava());
			else {
				if(en.compareTo(JSCLInteger.valueOf(1))==0) buffer.append(l.toJava());
				else if(en.compareTo(JSCLInteger.valueOf(-1))==0) buffer.append(l.toJava()).append(".negate()");
				else buffer.append(en.toJava()).append(".multiply(").append(l.toJava()).append(")");
			}
			if(i>0) buffer.append(")");
		}
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<mrow>\n");
		if(signum()==0) buffer.append(1,"<mn>0</mn>\n");
		Iterator it=content.entrySet().iterator();
		for(int i=0;it.hasNext();i++) {
			Map.Entry e=(Map.Entry)it.next();
			Literal l=(Literal)e.getKey();
			JSCLInteger en=(JSCLInteger)e.getValue();
			if(en.signum()>0 && i>0) buffer.append(1,"<mo>+</mo>\n");
			if(l.degree()==0) buffer.append(1,separateSign(en));
			else {
				if(en.compareTo(JSCLInteger.valueOf(1))==0);
				else if(en.compareTo(JSCLInteger.valueOf(-1))==0) buffer.append(1,"<mo>-</mo>\n");
				else buffer.append(1,separateSign(en));
				buffer.append(1,l.toMathML(null));
			}
		}
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	static String separateSign(Generic generic) {
		IndentedBuffer buffer=new IndentedBuffer();
		if(generic.signum()<0) {
			buffer.append("<mo>-</mo>\n");
			buffer.append(generic.negate().toMathML(null));
		} else buffer.append(generic.toMathML(null));
		return buffer.toString();
	}

	protected Generic newinstance() {
		return new Expression();
	}
}

class CommaAndExpression extends Parser {
	public static final Parser parser=new CommaAndExpression();

	private CommaAndExpression() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])==',') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			a=(Generic)ExpressionParser.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return a;
	}
}

class ExpressionParser extends Parser {
	public static final Parser parser=new ExpressionParser();

	private ExpressionParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Generic a;
		boolean sign=false;
		try {
			MinusParser.parser.parse(str,pos);
			sign=true;
		} catch (ParseException e) {}
		try {
			a=(Generic)Term.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		if(sign) a=a.negate();
		while(true) {
			try {
				Generic a2=(Generic)PlusOrMinusTerm.parser.parse(str,pos);
				a=a.add(a2);
			} catch (ParseException e) {
				break;
			}
		}
		return a;
	}
}

class MinusParser extends Parser {
	public static final Parser parser=new MinusParser();

	private MinusParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='-') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return null;
	}
}

class PlusOrMinusTerm extends Parser {
	public static final Parser parser=new PlusOrMinusTerm();

	private PlusOrMinusTerm() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		boolean sign;
		Generic a;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && (str.charAt(pos[0])=='+' || str.charAt(pos[0])=='-')) {
			sign=str.charAt(pos[0]++)=='-';
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			a=(Generic)Term.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return sign?a.negate():a;
	}
}

class Term extends Parser {
	public static final Parser parser=new Term();

	private Term() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Generic a=JSCLInteger.valueOf(1);
		Generic s;
		try {
			s=(Generic)UnsignedFactor.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		while(true) {
			try {
				Generic a2=(Generic)MultiplyOrDivideFactor.multiply.parse(str,pos);
				a=a.multiply(s);
				s=a2;
			} catch (ParseException e) {
				try {
					Generic a2=(Generic)MultiplyOrDivideFactor.divide.parse(str,pos);
					if(s.compareTo(JSCLInteger.valueOf(1))==0) s=new Inv(GenericVariable.content(a2,true)).expressionValue();
					else s=new Frac(GenericVariable.content(s,true),GenericVariable.content(a2,true)).expressionValue();
				} catch (ParseException e2) {
					break;
				}
			}
		}
		a=a.multiply(s);
		return a;
	}
}

class MultiplyOrDivideFactor extends Parser {
	public static final Parser multiply=new MultiplyOrDivideFactor(true);
	public static final Parser divide=new MultiplyOrDivideFactor(false);
	boolean option;

	private MultiplyOrDivideFactor(boolean option) {
		this.option=option;
	}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])==(option?'*':'/')) {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			a=(Generic)Factor.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return a;
	}
}

class Factor extends Parser {
	public static final Parser parser=new Factor();

	private Factor() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Generic a;
		boolean sign=false;
		try {
			MinusParser.parser.parse(str,pos);
			sign=true;
		} catch (ParseException e) {}
		try {
			a=(Generic)UnsignedFactor.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		return sign?a.negate():a;
	}
}

class UnsignedFactor extends Parser {
	public static final Parser parser=new UnsignedFactor();

	private UnsignedFactor() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Generic a;
		try {
			a=(Generic)UnsignedExponent.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		while(true) {
			try {
				Generic a2=(Generic)PowerExponent.parser.parse(str,pos);
				try {
					int c=a2.integerValue().intValue();
					if(c<0) a=new Pow(GenericVariable.content(a,true),JSCLInteger.valueOf(c)).expressionValue();
					else a=a.pow(c);
				} catch (NotIntegerException e) {
					a=new Pow(GenericVariable.content(a,true),GenericVariable.content(a2,true)).expressionValue();
				}
			} catch (ParseException e) {
				break;
			}
		}
		return a;
	}
}

class PowerExponent extends Parser {
	public static final Parser parser=new PowerExponent();

	private PowerExponent() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		try {
			PowerParser.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		try {
			a=(Generic)Exponent.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return a;
	}
}

class PowerParser extends Parser {
	public static final Parser parser=new PowerParser();

	private PowerParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='^') {
			str.charAt(pos[0]++);
		} else {
			if(pos[0]+1<str.length() && str.charAt(pos[0])=='*' && str.charAt(pos[0]+1)=='*') {
				str.charAt(pos[0]++);
				str.charAt(pos[0]++);
			} else {
				pos[0]=pos0;
				throw new ParseException();
			}
		}
		return null;
	}
}

class Exponent extends Parser {
	public static final Parser parser=new Exponent();

	private Exponent() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		boolean sign=false;
		try {
			MinusParser.parser.parse(str,pos);
			sign=true;
		} catch (ParseException e) {}
		try {
			a=(Generic)UnsignedExponent.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return sign?a.negate():a;
	}
}

class UnsignedExponent extends Parser {
	public static final Parser parser=new UnsignedExponent();

	private UnsignedExponent() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		boolean factorial=false;
		try {
			a=(Generic)PrimaryExpression.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		try {
			FactorialParser.parser.parse(str,pos);
			factorial=true;
		} catch (ParseException e) {}
		return factorial?new Factorial(GenericVariable.content(a,true)).expressionValue():a;
	}
}

class FactorialParser extends Parser {
	public static final Parser parser=new FactorialParser();

	private FactorialParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='!') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return null;
	}
}

class PrimaryExpression extends Parser {
	public static final Parser parser=new PrimaryExpression();

	private PrimaryExpression() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Generic a;
		try {
			a=((Variable)GenericVariable.doubleParser.parse(str,pos)).expressionValue();
		} catch (ParseException e) {
			try {
				a=(Generic)JSCLInteger.parser.parse(str,pos);
			} catch (ParseException e2) {
				try {
					a=((Variable)Variable.parser.parse(str,pos)).expressionValue();
				} catch (ParseException e3) {
					try {
						a=((Variable)GenericVariable.matrix.parse(str,pos)).expressionValue();
					} catch (ParseException e4) {
						try {
							a=((Variable)GenericVariable.vector.parse(str,pos)).expressionValue();
						} catch (ParseException e5) {
							try {
								a=((Variable)GenericVariable.expression.parse(str,pos)).expressionValue();
							} catch (ParseException e6) {
								throw e6;
							}
						}
					}
				}
			}
		}
		return a;
	}
}
