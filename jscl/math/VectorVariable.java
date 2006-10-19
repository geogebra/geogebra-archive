package jscl.math;

class VectorVariable extends GenericVariable {
	VectorVariable(Generic generic) {
		super(generic);
	}

	protected Variable newinstance() {
		return new VectorVariable(null);
	}
}
