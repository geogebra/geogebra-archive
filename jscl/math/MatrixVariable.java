package jscl.math;

class MatrixVariable extends GenericVariable {
	MatrixVariable(Generic generic) {
		super(generic);
	}

	protected Variable newinstance() {
		return new MatrixVariable(null);
	}
}
