package jscl.math;

class DoubleVariable extends GenericVariable {
	DoubleVariable(Generic generic) {
		super(generic);
	}

	public Generic antiderivative(Variable variable) throws NotIntegrableException {
		return expressionValue().multiply(variable.expressionValue());
	}

	public Generic derivative(Variable variable) {
		return JSCLInteger.valueOf(0);
	}

	public Generic substitute(Variable variable, Generic generic) {
		if(isIdentity(variable)) return generic;
		else return expressionValue();
	}

	public Generic expand() {
		return expressionValue();
	}

	public Generic factorize() {
		return expressionValue();
	}

	public Generic elementary() {
		return expressionValue();
	}

	public Generic simplify() {
		return expressionValue();
	}

	protected Variable newinstance() {
		return new DoubleVariable(null);
	}
}
