package jscl.math;

public abstract class Polynomial implements Arithmetic, Comparable {
	public abstract Polynomial add(Polynomial polynomial);

	public Polynomial subtract(Polynomial polynomial) {
		return add(polynomial.negate());
	}

	public abstract Polynomial multiply(Polynomial polynomial);
	public abstract Polynomial multiply(Generic generic);
	public abstract Polynomial multiply(Monomial monomial, Generic generic);
	public abstract Polynomial multiply(Monomial monomial);

	public boolean multiple(Polynomial polynomial) throws ArithmeticException {
		return remainder(polynomial).signum()==0;
	}

	public Polynomial divide(Polynomial polynomial) throws ArithmeticException {
		Polynomial p[]=divideAndRemainder(polynomial);
		if(p[1].signum()==0) return p[0];
		else throw new NotDivisibleException();
	}

	public abstract Polynomial divide(Generic generic) throws ArithmeticException;
	public abstract Polynomial divide(Monomial monomial) throws ArithmeticException;

	public Arithmetic add(Arithmetic arithmetic) {
		return add((Polynomial)arithmetic);
	}

	public Arithmetic subtract(Arithmetic arithmetic) {
		return subtract((Polynomial)arithmetic);
	}

	public Arithmetic multiply(Arithmetic arithmetic) {
		return multiply((Polynomial)arithmetic);
	}

	public Arithmetic divide(Arithmetic arithmetic) throws ArithmeticException {
		return divide((Polynomial)arithmetic);
	}

	public abstract Polynomial[] divideAndRemainder(Polynomial polynomial) throws ArithmeticException;

	public Polynomial remainder(Polynomial polynomial) throws ArithmeticException {
		return divideAndRemainder(polynomial)[1];
	}

	public abstract Polynomial remainderUpToCoefficient(Polynomial polynomial) throws ArithmeticException;
	public abstract Polynomial gcd(Polynomial polynomial);

	public Polynomial scm(Polynomial polynomial) {
		return divide(gcd(polynomial)).multiply(polynomial);
	}

	public abstract Generic gcd();

	public Polynomial[] gcdAndNormalize() {
		Generic gcd=gcd();
		if(gcd.signum()==0) return new Polynomial[] {valueof(gcd),this};
		if(gcd.signum()!=signum()) gcd=gcd.negate();
		return new Polynomial[] {valueof(gcd),divide(gcd)};
	}

	public Polynomial normalize() {
		return gcdAndNormalize()[1];
	}

	public abstract Monomial monomialGcd();

	public Polynomial pow(int exponent) {
		Polynomial a=valueof(JSCLInteger.valueOf(1));
		for(int i=0;i<exponent;i++) a=a.multiply(this);
		return a;
	}

	public Polynomial abs() {
		return signum()<0?negate():this;
	}

	public abstract Polynomial negate();
	public abstract int signum();
	public abstract int degree();
	public abstract int sugar();
	public abstract Polynomial valueof(Polynomial polynomial);
	public abstract Polynomial valueof(Generic generic);
	public abstract Polynomial valueof(Monomial monomial);
	public abstract Monomial headMonomial();
	public abstract Monomial tailMonomial();
	public abstract Generic headCoefficient();
	public abstract Generic tailCoefficient();
	public abstract Polynomial s_polynomial(Polynomial polynomial);
	public abstract Polynomial reduce(Basis basis, boolean completely, boolean tail);
	public abstract Generic genericValue();
	public abstract Generic[] elements();

	public abstract int compareTo(Polynomial polynomial);

	public int compareTo(Object o) {
		return compareTo((Polynomial)o);
	}

	public boolean equals(Object obj) {
		if(obj instanceof Polynomial) {
			return compareTo((Polynomial)obj)==0;
		} else return false;
	}

	public abstract String toMathML(Object data);
	protected abstract Polynomial newinstance();
}
