package jscl.math;

import java.util.Vector;

import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;

public class Equation extends Generic {
	final Generic element[];
	final int equality[];
	final int n;

	Equation(Generic element[], int equality[]) {
		this.element=element;
		this.equality=equality;
		n=element.length;
	}

	public Generic add(Generic generic) {
		return null;
	}

	public Generic subtract(Generic generic) {
		return null;
	}

	public Generic multiply(Generic generic) {
		return null;
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		return null;
	}

	public Generic gcd(Generic generic) {
		return null;
	}

	public Generic gcd() {
		return null;
	}

	public Generic negate() {
		return null;
	}

	public int signum() {
		return 0;
	}

	public int degree() {
		return 0;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].antiderivative(variable);
		return v;
	}

	public Generic derivative(Variable variable) {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].derivative(variable);
		return v;
	}

	public Generic substitute(Variable variable, Generic generic) {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].substitute(variable,generic);
		return v;
	}

	public Generic expand() {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].expand();
		return v;
	}

	public Generic factorize() {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].factorize();
		return v;
	}

	public Generic elementary() {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].elementary();
		return v;
	}

	public Generic simplify() {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].simplify();
		return v;
	}

	public Generic numeric() {
		Equation v=(Equation)newinstance();
		for(int i=0;i<n;i++) v.element[i]=element[i].numeric();
		return v;
	}

	public Generic valueof(Generic generic) {
		return null;
	}

	public Generic[] sumValue() {
		return null;
	}

	public Generic[] productValue() throws NotProductException {
		return null;
	}

	public Object[] powerValue() throws NotPowerException {
		return null;
	}

	public Expression expressionValue() throws NotExpressionException {
		return null;
	}

	public JSCLInteger integerValue() throws NotIntegerException {
		return null;
	}

	public Variable variableValue() throws NotVariableException {
		return null;
	}

	public Variable[] variables() {
		return null;
	}

	public boolean isPolynomial(Variable variable) {
		return false;
	}

	public boolean isConstant(Variable variable) {
		return false;
	}

	public int compareTo(Generic generic) {
		return 0;
	}

	public static Equation valueOf(String str) throws ParseException {
		int pos[]=new int[1];
		Equation v;
		try {
			v=(Equation)EquationOrExpression.parser.parse(str,pos);
		} catch (ParseException e) {
			throw e;
		}
		Parser.skipWhitespaces(str,pos);
		if(pos[0]<str.length()) {
			throw new ParseException();
		}
		return v;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		for(int i=0;i<n;i++) {
			buffer.append(element[i]).append(i<n-1?eass[equality[i]]:"");
		}
		return buffer.toString();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		for(int i=0;i<n;i++) {
			buffer.append(element[i].toJava()).append(i<n-1?easj[equality[i]]:"");
		}
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		for(int i=0;i<n;i++) {
			buffer.append(element[i].toMathML(null));
			if(i<n-1) buffer.append("<mo>").append(easm[equality[i]]).append("</mo>\n");
		}
		return buffer.toString();
	}

	protected Generic newinstance() {
		return newinstance(n);
	}

	protected Generic newinstance(int n) {
		return new Equation(new Generic[n],equality);
	}

	private static final String eass[]={"=","<=",">=","<>","<",">","~"};
	private static final String easj[]={"==","<=",">=","!=","<",">","=="};
	private static final String easm[]={"=","&leq;","&geq;","&ne;","&lt;","&gt;","&ap;"};
}

class EquationOrExpression extends Parser {
	public static final Parser parser=new EquationOrExpression();

	private EquationOrExpression() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Vector v=new Vector();
		Vector w=new Vector();
		try {
			Generic a=(Generic)Expression.parser.parse(str,pos);
			v.addElement(a);
		} catch (ParseException e) {
			throw e;
		}
		while(true) {
			try {
				Object o[]=(Object[])RightHandSide.parser.parse(str,pos);
				v.addElement((Generic)o[0]);
				w.addElement((Integer)o[1]);
			} catch (ParseException e) {
				break;
			}
		}
		Generic element[]=new Generic[v.size()];
		v.copyInto(element);
		int equality[]=new int[w.size()];
		for(int i=0;i<equality.length;i++) equality[i]=((Integer)w.elementAt(i)).intValue();
		return new Equation(element,equality);
	}
}

class RightHandSide extends Parser {
	public static final Parser parser=new RightHandSide();

	private RightHandSide() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		Generic a;
		int c;
		try {
			c=((Integer)Equality.parser.parse(str,pos)).intValue();
		} catch (ParseException e) {
			throw e;
		}
		try {
			a=(Generic)Expression.parser.parse(str,pos);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return new Object[] {a,new Integer(c)};
	}
}

class Equality extends Parser {
	public static final Parser parser=new Equality();

	private Equality() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		int c;
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='=') {
			str.charAt(pos[0]++);
			c=0;
		} else if(pos[0]+1<str.length() && str.charAt(pos[0])=='<' && str.charAt(pos[0]+1)=='=') {
			str.charAt(pos[0]++);
			str.charAt(pos[0]++);
			c=1;
		} else if(pos[0]+1<str.length() && str.charAt(pos[0])=='>' && str.charAt(pos[0]+1)=='=') {
			str.charAt(pos[0]++);
			str.charAt(pos[0]++);
			c=2;
		} else if(pos[0]+1<str.length() && str.charAt(pos[0])=='<' && str.charAt(pos[0]+1)=='>') {
			str.charAt(pos[0]++);
			str.charAt(pos[0]++);
			c=3;
		} else if(pos[0]<str.length() && str.charAt(pos[0])=='<') {
			str.charAt(pos[0]++);
			c=4;
		} else if(pos[0]<str.length() && str.charAt(pos[0])=='>') {
			str.charAt(pos[0]++);
			c=5;
		} else if(pos[0]<str.length() && str.charAt(pos[0])=='~') {
			str.charAt(pos[0]++);
			c=6;
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return new Integer(c);
	}
}
