package jscl.math;

import java.math.BigInteger;

import jscl.text.IndentedBuffer;

public class Rational extends Generic {
	BigInteger numerator;
	BigInteger denominator;

	Rational() {}

	public Rational add(Rational rational) {
		Rational r=(Rational)newinstance();
		BigInteger gcd=denominator.gcd(rational.denominator);
		BigInteger c=denominator.divide(gcd);
		BigInteger c2=rational.denominator.divide(gcd);
		r.put(numerator.multiply(c2).add(rational.numerator.multiply(c)),denominator.multiply(c2));
		r.reduce();
		return r;
	}

	public Generic add(Generic generic) {
		if(generic instanceof Rational) {
			return add((Rational)generic);
		} else if(generic instanceof JSCLInteger) {
			return add(valueof(generic));
		} else {
			return generic.valueof(this).add(generic);
		}
	}

	void reduce() {
		BigInteger gcd=numerator.gcd(denominator);
		if(gcd.signum()==0) return;
		if(gcd.signum()!=denominator.signum()) gcd=gcd.negate();
		numerator=numerator.divide(gcd);
		denominator=denominator.divide(gcd);
	}

	public Rational multiply(Rational rational) {
		Rational r=(Rational)newinstance();
		BigInteger gcd=numerator.gcd(rational.denominator);
		BigInteger gcd2=denominator.gcd(rational.numerator);
		r.put(numerator.divide(gcd).multiply(rational.numerator.divide(gcd2)),denominator.divide(gcd2).multiply(rational.denominator.divide(gcd)));
		return r;
	}

	public Generic multiply(Generic generic) {
		if(generic instanceof Rational) {
			return multiply((Rational)generic);
		} else if(generic instanceof JSCLInteger) {
			return multiply(valueof(generic));
		} else {
			return generic.multiply(this);
		}
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		if(generic instanceof Rational) {
			return multiply(((Rational)generic).inverse());
		} else if(generic instanceof JSCLInteger) {
			return divide(valueof(generic));
		} else {
			return generic.valueof(this).divide(generic);
		}
	}

	public Generic inverse() {
		Rational r=(Rational)newinstance();
		if(signum()<0) r.put(denominator.negate(),numerator.negate());
		else r.put(denominator,numerator);
		return r;
	}

	public Rational gcd(Rational rational) {
		Rational r=(Rational)newinstance();
		r.put(numerator.gcd(rational.numerator),scm(denominator,rational.denominator));
		return r;
	}

	public Generic gcd(Generic generic) {
		if(generic instanceof Rational) {
			return gcd((Rational)generic);
		} else if(generic instanceof JSCLInteger) {
			return gcd(valueof(generic));
		} else {
			return generic.valueof(this).gcd(generic);
		}
	}

	static BigInteger scm(BigInteger b1, BigInteger b2) {
		return b1.multiply(b2).divide(b1.gcd(b2));
	}

	public Generic gcd() {
		return null;
	}

	public Generic pow(int exponent) {
		return null;
	}

	public Generic negate() {
		Rational r=(Rational)newinstance();
		r.put(numerator.negate(),denominator);
		return r;
	}

	public int signum() {
		return numerator.signum();
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
		return new NumericWrapper(this);
	}

	public Generic valueof(Generic generic) {
		Rational r=(Rational)newinstance();
		r.put(generic);
		return r;
	}

	public Generic[] sumValue() {
		try {
			if(integerValue().signum()==0) return new Generic[0];
			else return new Generic[] {this};
		} catch (NotIntegerException e) {
			return new Generic[] {this};
		}
	}

	public Generic[] productValue() throws NotProductException {
		try {
			if(integerValue().compareTo(JSCLInteger.valueOf(1))==0) return new Generic[0];
			else return new Generic[] {this};
		} catch (NotIntegerException e) {
			return new Generic[] {this};
		}
	}

	public Object[] powerValue() throws NotPowerException {
		return new Object[] {this,new Integer(1)};
	}

	public Expression expressionValue() throws NotExpressionException {
		throw new NotExpressionException();
	}

	public JSCLInteger integerValue() throws NotIntegerException {
		if(denominator.compareTo(BigInteger.valueOf(1))==0) {
			JSCLInteger e=new JSCLInteger();
			e.put(numerator);
			return e;
		} else throw new NotIntegerException();
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

	public int compareTo(Rational rational) {
		int c=denominator.compareTo(rational.denominator);
		if(c<0) return -1;
		else if(c>0) return 1;
		else return numerator.compareTo(rational.numerator);
	}

	public int compareTo(Generic generic) {
		if(generic instanceof Rational) {
			return compareTo((Rational)generic);
		} else if(generic instanceof JSCLInteger) {
			return compareTo(valueof(generic));
		} else {
			return generic.valueof(this).compareTo(generic);
		}
	}

	public static Rational valueOf(JSCLInteger integer) {
		Rational r=new Rational();
		r.put(integer);
		return r;
	}

	void put(Generic generic) {
		if(generic instanceof Rational) {
			Rational r=(Rational)generic;
			put(r.numerator,r.denominator);
		} else if(generic instanceof JSCLInteger) { 
			JSCLInteger en=(JSCLInteger)generic;
			put(en.content,BigInteger.valueOf(1));
		} else throw new ArithmeticException();
	}

	void put(BigInteger numerator, BigInteger denominator) {
		this.numerator=numerator;
		this.denominator=denominator;
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		try {
			buffer.append(integerValue());
		} catch (NotIntegerException e) {
			buffer.append(numerator);
			buffer.append("/");
			buffer.append(denominator);
		}
		return buffer.toString();
	}

	public String toJava() {
		return "JSCLDouble.valueOf("+numerator+"/"+denominator+")";
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
		try {
			buffer.append("<mn>").append(integerValue()).append("</mn>\n");
		} catch (NotIntegerException e) {
			buffer.append("<mfrac>\n");
			buffer.append(1,"<mn>").append(numerator).append("</mn>\n");
			buffer.append(1,"<mn>").append(denominator).append("</mn>\n");
			buffer.append("</mfrac>\n");
		}
		return buffer.toString();
	}

	protected Generic newinstance() {
		return new Rational();
	}
}
