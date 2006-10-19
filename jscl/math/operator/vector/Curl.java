package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class Curl extends VectorOperator {
	public Curl(Generic vector, Generic variable) {
		super("curl",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.curl(variable);
		}
		return expressionValue();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(operator("nabla"));
		buffer.append("<mo>&wedge;</mo>\n");
		buffer.append(parameter[0].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Curl(null,null);
	}
}
