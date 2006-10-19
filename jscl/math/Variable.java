package jscl.math;

import java.util.Comparator;

import jscl.math.function.Constant;
import jscl.math.function.Frac;
import jscl.math.function.Function;
import jscl.math.function.ImplicitFunction;
import jscl.math.function.Pow;
import jscl.math.function.Root;
import jscl.math.operator.Factorial;
import jscl.math.operator.Operator;
import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;

public abstract class Variable implements Comparable {
	public static final Comparator comparator=VariableComparator.comparator;
	public static final Parser parser=VariableParser.parser;
	public String name;

	public Variable(String name) {
		this.name=name;
	}

	public abstract Generic antiderivative(Variable variable) throws NotIntegrableException;
	public abstract Generic derivative(Variable variable);
	public abstract Generic substitute(Variable variable, Generic generic);
	public abstract Generic expand();
	public abstract Generic factorize();
	public abstract Generic elementary();
	public abstract Generic simplify();
	public abstract Generic numeric();

	public Expression expressionValue() {
		return Expression.valueOf(this);
	}

	public abstract boolean isConstant(Variable variable);

	public boolean isIdentity(Variable variable) {
		return compareTo(variable)==0;
	}

	public abstract int compareTo(Variable variable);

	public int compareTo(Object o) {
		return compareTo((Variable)o);
	}

	public boolean equals(Object obj) {
		if(obj instanceof Variable) {
			return compareTo((Variable)obj)==0;
		} else return false;
	}

	public static Variable valueOf(String str) throws ParseException, NotVariableException {
		return Expression.valueOf(str).variableValue();
	}

	public String toString() {
		return name;
	}

	public String toJava() {
		return name;
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(nameToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,nameToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	protected String nameToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<mi>");
		if(special(name)) buffer.append("&").append(name).append(";");
		else buffer.append(name);
		buffer.append("</mi>\n");
		return buffer.toString();
	}

	static boolean special(String name) {
		for(int i=0;i<na.length;i++) if(name.compareTo(na[i])==0) return true;
		return false;
	}

	protected abstract Variable newinstance();

	private static String na[]={"Alpha","Beta","Gamma","Delta","Epsilon","Zeta","Eta","Theta","Iota","Kappa","Lambda","Mu","Nu","Xi","Omicron","Pi","Rho","Sigma","Tau","Upsilon","Phi","Chi","Psi","Omega","alpha","beta","gamma","delta","epsilon","zeta","eta","theta","iota","kappa","lambda","mu","nu","xi","omicron","pi","rho","sigma","tau","upsilon","phi","chi","psi","omega","infin","nabla","aleph"};
}

class VariableComparator implements Comparator {
	public static final Comparator comparator=new VariableComparator();

	private VariableComparator() {}

	public int compare(Object o1, Object o2) {
		return value((Variable)o1)-value((Variable)o2);
	}

	static int value(Variable v) {
		int n;
		if(v instanceof TechnicalVariable) n=0;
		else if(v instanceof Constant) n=3;
		else if(v instanceof Frac) n=4;
		else if(v instanceof Pow) n=5;
		else if(v instanceof Root) n=6;
		else if(v instanceof ImplicitFunction) n=7;
		else if(v instanceof Function) n=8;
		else if(v instanceof Factorial) n=9;
		else if(v instanceof Operator) n=10;
		else if(v instanceof IntegerVariable) n=1;
		else if(v instanceof DoubleVariable) n=2;
		else if(v instanceof ExpressionVariable) n=11;
		else if(v instanceof VectorVariable) n=12;
		else if(v instanceof MatrixVariable) n=13;
		else throw new ArithmeticException();
		return n;
	}
}

class VariableParser extends Parser {
	public static final Parser parser=new VariableParser();

	private VariableParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		Variable v;
		try {
			v=(Variable)Operator.parser.parse(str,pos);
		} catch (ParseException e) {
			try {
				v=(Variable)Function.parser.parse(str,pos);
			} catch (ParseException e2) {
				try {
					v=(Variable)Constant.parser.parse(str,pos);
				} catch (ParseException e3) {
					throw e3;
				}
			}
		}
		return v;
	}
}
