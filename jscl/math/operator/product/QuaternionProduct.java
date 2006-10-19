package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class QuaternionProduct extends VectorOperator {
	public QuaternionProduct(Generic vector1, Generic vector2) {
		super("quaternion",new Generic[] {vector1,vector2});
	}

	public Generic compute() {
		if(parameter[0] instanceof JSCLVector && parameter[1] instanceof JSCLVector) {
			JSCLVector v1=(JSCLVector)parameter[0];
			JSCLVector v2=(JSCLVector)parameter[1];
			return v1.quaternionProduct(v2);
		}
		return expressionValue();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(parameter[0].toMathML(null));
		buffer.append(parameter[1].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new QuaternionProduct(null,null);
	}
}
