package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;

public class Substitute extends Operator {
	public Substitute(Generic expression, Generic variable, Generic value) {
		super("subst",new Generic[] {expression,variable,value});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		return parameter[0].substitute(variable,parameter[2]);
	}

	public Generic expand() {
		return compute();
	}

	protected Variable newinstance() {
		return new Substitute(null,null,null);
	}
}
