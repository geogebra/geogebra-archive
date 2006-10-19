package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class Divergence extends VectorOperator {
	public Divergence(Generic vector, Generic variable) {
		super("diverg",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.divergence(variable);
		}
		return expressionValue();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(operator("nabla"));
		buffer.append(parameter[0].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Divergence(null,null);
	}
}
