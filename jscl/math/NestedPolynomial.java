package jscl.math;

public class NestedPolynomial extends UnivariatePolynomial {
	final Variable variable[];

	NestedPolynomial(Variable variable[]) {
		super(variable[0]);
		this.variable=variable;
	}

	protected Generic uncoefficient(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return ((PolynomialWrapper)generic).content().genericValue();
		} else {
			return generic;
		}
	}

	protected Generic coefficient(Generic generic) {
		if(variable.length>1) {
			Variable var[]=new Variable[variable.length-1];
			for(int i=0;i<var.length;i++) var[i]=variable[i+1];
			return new PolynomialWrapper(valueOf(generic,var));
		} else {
			return generic;
		}
	}

	public static NestedPolynomial valueOf(Generic generic, Variable variable[]) {
		NestedPolynomial p=new NestedPolynomial(variable);
		p.put(generic);
		return p;
	}

	protected Polynomial newinstance() {
		return new NestedPolynomial(variable);
	}
}

final class PolynomialWrapper extends Generic {
	final Polynomial content;

	PolynomialWrapper(Polynomial polynomial) {
		content=polynomial;
	}

	Polynomial content() {
		return content;
	}

	public PolynomialWrapper add(PolynomialWrapper wrapper) {
		return new PolynomialWrapper(content.add(wrapper.content));
	}

	public Generic add(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return add((PolynomialWrapper)generic);
		} else {
			return add(valueof(generic));
		}
	}

	public PolynomialWrapper subtract(PolynomialWrapper wrapper) {
		return new PolynomialWrapper(content.subtract(wrapper.content));
	}

	public Generic subtract(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return subtract((PolynomialWrapper)generic);
		} else {
			return subtract(valueof(generic));
		}
	}

	public PolynomialWrapper multiply(PolynomialWrapper wrapper) {
		return new PolynomialWrapper(content.multiply(wrapper.content));
	}

	public Generic multiply(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return multiply((PolynomialWrapper)generic);
		} else {
			return multiply(valueof(generic));
		}
	}

	public PolynomialWrapper divide(PolynomialWrapper wrapper) throws ArithmeticException {
		return new PolynomialWrapper(content.divide(wrapper.content));
	}

	public Generic divide(Generic generic) throws ArithmeticException {
		if(generic instanceof PolynomialWrapper) {
			return divide((PolynomialWrapper)generic);
		} else {
			return divide(valueof(generic));
		}
	}

	public PolynomialWrapper gcd(PolynomialWrapper wrapper) {
		return new PolynomialWrapper(content.gcd(wrapper.content));
	}

	public Generic gcd(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return gcd((PolynomialWrapper)generic);
		} else {
			return gcd(valueof(generic));
		}
	}

	public Generic gcd() {
		return content.gcd();
	}

	public Generic negate() {
		return new PolynomialWrapper(content.negate());
	}

	public int signum() {
		return content.signum();
	}

	public int degree() {
		return content.degree();
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
		return null;
	}

	public PolynomialWrapper valueof(PolynomialWrapper wrapper) {
		return new PolynomialWrapper(content.valueof(wrapper.content));
	}

	public Generic valueof(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return valueof((PolynomialWrapper)generic);
		} else {
			return new PolynomialWrapper(content.valueof(generic));
		}
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
		return false;
	}

	public boolean isConstant(Variable variable) {
		return false;
	}

	public int compareTo(PolynomialWrapper wrapper) {
		return content.compareTo(wrapper.content);
	}

	public int compareTo(Generic generic) {
		if(generic instanceof PolynomialWrapper) {
			return compareTo((PolynomialWrapper)generic);
		} else {
			return compareTo(valueof(generic));
		}
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		if(signum()<0) buffer.append("-").append(negate());
		else buffer.append("(").append(content).append(")");
		return buffer.toString();
	}

	public String toJava() {
		return null;
	}

	public String toMathML(Object data) {
		return null;
	}

	protected Generic newinstance() {
		return null;
	}
}
