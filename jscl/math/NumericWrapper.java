package jscl.math;

import jscl.math.function.Constant;
import jscl.math.numeric.JSCLDouble;
import jscl.math.numeric.Numeric;
import jscl.math.numeric.NumericMatrix;
import jscl.math.numeric.NumericVector;
import jscl.text.IndentedBuffer;
import jscl.text.ParseException;
import jscl.text.Parser;

public final class NumericWrapper extends Generic {
	public static final Parser parser=DoubleParser.parser;
	final Numeric content;

	public NumericWrapper(JSCLInteger integer) {
		content=JSCLDouble.valueOf(integer.content.doubleValue());
	}

	public NumericWrapper(Rational rational) {
		content=JSCLDouble.valueOf(rational.numerator.doubleValue()/rational.denominator.doubleValue());
	}

	public NumericWrapper(JSCLVector vector) {
		Numeric v[]=new Numeric[vector.n];
		for(int i=0;i<vector.n;i++) v[i]=((NumericWrapper)vector.element[i].numeric()).content();
		content=new NumericVector(v);
	}

	public NumericWrapper(Matrix matrix) {
		Numeric m[][]=new Numeric[matrix.n][matrix.p];
		for(int i=0;i<matrix.n;i++) {
			for(int j=0;j<matrix.p;j++) {
				m[i][j]=((NumericWrapper)matrix.element[i][j].numeric()).content();
			}
		}
		content=new NumericMatrix(m);
	}

	public NumericWrapper(Constant constant) {
		if(constant.compareTo(new Constant("pi"))==0) content=JSCLDouble.valueOf(Math.PI);
		else if(constant.compareTo(new Constant("infin"))==0) content=JSCLDouble.valueOf(Double.POSITIVE_INFINITY);
		else throw new ArithmeticException();
	}

	NumericWrapper(Numeric numeric) {
		content=numeric;
	}

	public Numeric content() {
		return content;
	}

	public NumericWrapper add(NumericWrapper wrapper) {
		return new NumericWrapper(content.add(wrapper.content));
	}

	public Generic add(Generic generic) {
		if(generic instanceof NumericWrapper) {
			return add((NumericWrapper)generic);
		} else {
			return add(valueof(generic));
		}
	}

	public NumericWrapper subtract(NumericWrapper wrapper) {
		return new NumericWrapper(content.subtract(wrapper.content));
	}

	public Generic subtract(Generic generic) {
		if(generic instanceof NumericWrapper) {
			return subtract((NumericWrapper)generic);
		} else {
			return subtract(valueof(generic));
		}
	}

	public NumericWrapper multiply(NumericWrapper wrapper) {
		return new NumericWrapper(content.multiply(wrapper.content));
	}

	public Generic multiply(Generic generic) {
		if(generic instanceof NumericWrapper) {
			return multiply((NumericWrapper)generic);
		} else {
			return multiply(valueof(generic));
		}
	}

	public NumericWrapper divide(NumericWrapper wrapper) throws ArithmeticException {
		return new NumericWrapper(content.divide(wrapper.content));
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		if(generic instanceof NumericWrapper) {
			return divide((NumericWrapper)generic);
		} else {
			return divide(valueof(generic));
		}
	}

	public Generic gcd(Generic generic) {
		return null;
	}

	public Generic gcd() {
		return null;
	}

	public Generic abs() {
		return new NumericWrapper(content.abs());
	}

	public Generic negate() {
		return new NumericWrapper(content.negate());
	}

	public int signum() {
		return content.signum();
	}

	public int degree() {
		return 0;
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		return null;
	}

	public Generic derivative(Variable variable) {
		return null;
	}

	public Generic substitute(Variable variable, Generic generic) {
		return null;
	}

	public Generic expand() {
		return null;
	}

	public Generic factorize() {
		return null;
	}

	public Generic elementary() {
		return null;
	}

	public Generic simplify() {
		return null;
	}

	public Generic numeric() {
		return this;
	}

	public NumericWrapper valueof(NumericWrapper wrapper) {
		return new NumericWrapper(content.valueof(wrapper.content));
	}

	public Generic valueof(Generic generic) {
		if(generic instanceof NumericWrapper) {
			return valueof((NumericWrapper)generic);
		} else if(generic instanceof JSCLInteger) { 
			return new NumericWrapper((JSCLInteger)generic);
		} else if(generic instanceof Rational) { 
			return new NumericWrapper((Rational)generic);
		} else throw new ArithmeticException();
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
		throw new NotExpressionException();
	}

	public JSCLInteger integerValue() throws NotIntegerException {
		throw new NotIntegerException();
	}

	public Variable variableValue() throws NotVariableException {
		throw new NotVariableException();
	}

	public Variable[] variables() {
		return new Variable[0];
	}

	public boolean isPolynomial(Variable variable) {
		return true;
	}

	public boolean isConstant(Variable variable) {
		return true;
	}

	public Generic sgn() {
		return new NumericWrapper(content.sgn());
	}

	public Generic log() {
		return new NumericWrapper(content.log());
	}

	public Generic exp() {
		return new NumericWrapper(content.exp());
	}

	public Generic pow(Generic generic) {
		return new NumericWrapper(content.pow(((NumericWrapper)generic).content));
	}

	public Generic sqrt() {
		return new NumericWrapper(content.sqrt());
	}

	public static Generic root(int subscript, Generic parameter[]) {
		Numeric param[]=new Numeric[parameter.length];
		for(int i=0;i<param.length;i++) param[i]=((NumericWrapper)parameter[i]).content;
		return new NumericWrapper(Numeric.root(subscript,param));
	}

	public Generic conjugate() {
		return new NumericWrapper(content.conjugate());
	}

	public Generic acos() {
		return new NumericWrapper(content.acos());
	}

	public Generic asin() {
		return new NumericWrapper(content.asin());
	}

	public Generic atan() {
		return new NumericWrapper(content.atan());
	}

	public Generic cos() {
		return new NumericWrapper(content.cos());
	}

	public Generic sin() {
		return new NumericWrapper(content.sin());
	}

	public Generic tan() {
		return new NumericWrapper(content.tan());
	}

	public Generic acosh() {
		return new NumericWrapper(content.acosh());
	}

	public Generic asinh() {
		return new NumericWrapper(content.asinh());
	}

	public Generic atanh() {
		return new NumericWrapper(content.atanh());
	}

	public Generic cosh() {
		return new NumericWrapper(content.cosh());
	}

	public Generic sinh() {
		return new NumericWrapper(content.sinh());
	}

	public Generic tanh() {
		return new NumericWrapper(content.tanh());
	}

	public int compareTo(NumericWrapper wrapper) {
		return content.compareTo(wrapper.content);
	}

	public int compareTo(Generic generic) {
		if(generic instanceof NumericWrapper) {
			return compareTo((NumericWrapper)generic);
		} else {
			return compareTo(valueof(generic));
		}
	}

	public String toString() {
		return content.toString();
	}

	public String toJava() {
		return "JSCLDouble.valueOf("+new Double(((JSCLDouble)content).doubleValue())+")";
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(bodyToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,bodyToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<mn>").append(new Double(((JSCLDouble)content).doubleValue())).append("</mn>\n");
		return buffer.toString();
	}

	protected Generic newinstance() {
		return null;
	}
}

class DoubleParser extends Parser {
	public static final Parser parser=new DoubleParser();

	private DoubleParser() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		double d;
		try {
			d=((Double)Singularity.parser.parse(str,pos)).doubleValue();
		} catch (ParseException e) {
			try {
				d=((Double)FloatingPointLiteral.parser.parse(str,pos)).doubleValue();
			} catch (ParseException e2) {
				throw e2;
			}
		}
		return new NumericWrapper(JSCLDouble.valueOf(d));
	}
}

class Singularity extends Parser {
	public static final Parser parser=new Singularity();

	private Singularity() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		double d;
		try {
			String s=(String)Constant.identifier.parse(str,pos);
			if(s.compareTo("NaN")==0) d=Double.NaN;
			else if(s.compareTo("Infinity")==0) d=Double.POSITIVE_INFINITY;
			else {
				pos[0]=pos0;
				throw new ParseException();
			}
		} catch (ParseException e) {
			throw e;
		}
		return new Double(d);
	}
}

class FloatingPointLiteral extends Parser {
	public static final Parser parser=new FloatingPointLiteral();

	private FloatingPointLiteral() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		StringBuffer buffer=new StringBuffer();
		boolean digits=false;
		boolean point=false;
		try {
			String s=(String)JSCLInteger.digits.parse(str,pos);
			buffer.append(s);
			digits=true;
		} catch (ParseException e) {}
		try {
			DecimalPoint.parser.parse(str,pos);
			buffer.append(".");
			point=true;
		} catch (ParseException e) {
			if(!digits) {
				pos[0]=pos0;
				throw e;
			}
		}
		try {
			String s=(String)JSCLInteger.digits.parse(str,pos);
			buffer.append(s);
		} catch (ParseException e) {
			if(!digits) {
				pos[0]=pos0;
				throw e;
			}
		}
		try {
			String s=(String)ExponentPart.parser.parse(str,pos);
			buffer.append(s);
		} catch (ParseException e) {
			if(!point) {
				pos[0]=pos0;
				throw e;
			}
		}
		return new Double(buffer.toString());
	}
}

class DecimalPoint extends Parser {
	public static final Parser parser=new DecimalPoint();

	private DecimalPoint() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && str.charAt(pos[0])=='.') {
			str.charAt(pos[0]++);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		return null;
	}
}

class ExponentPart extends Parser {
	public static final Parser parser=new ExponentPart();

	private ExponentPart() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		StringBuffer buffer=new StringBuffer();
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && (str.charAt(pos[0])=='e' || str.charAt(pos[0])=='E')) {
			char c=str.charAt(pos[0]++);
			buffer.append(c);
		} else {
			pos[0]=pos0;
			throw new ParseException();
		}
		try {
			String s=(String)SignedInteger.parser.parse(str,pos);
			buffer.append(s);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return buffer.toString();
	}
}

class SignedInteger extends Parser {
	public static final Parser parser=new SignedInteger();

	private SignedInteger() {}

	public Object parse(String str, int pos[]) throws ParseException {
		int pos0=pos[0];
		StringBuffer buffer=new StringBuffer();
		skipWhitespaces(str,pos);
		if(pos[0]<str.length() && (str.charAt(pos[0])=='+' || str.charAt(pos[0])=='-')) {
			char c=str.charAt(pos[0]++);
			buffer.append(c);
		}
		try {
			int n=((Integer)Constant.integer.parse(str,pos)).intValue();
			buffer.append(n);
		} catch (ParseException e) {
			pos[0]=pos0;
			throw e;
		}
		return buffer.toString();
	}
}
