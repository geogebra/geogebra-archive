package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class Laplacian extends VectorOperator {
	public Laplacian(Generic vector, Generic variable) {
		super("laplacian",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.laplacian(variable);
		} else {
			Expression expression=parameter[0].expressionValue();
			return expression.laplacian(variable);
		}
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(operator("Delta"));
		buffer.append(parameter[0].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Laplacian(null,null);
	}
}
