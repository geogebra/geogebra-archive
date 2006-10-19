package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class TensorProduct extends VectorOperator {
	public TensorProduct(Generic matrix1, Generic matrix2) {
		super("tensor",new Generic[] {matrix1,matrix2});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix && parameter[1] instanceof Matrix) {
			Matrix m1=(Matrix)parameter[0];
			Matrix m2=(Matrix)parameter[1];
			return m1.tensorProduct(m2);
		}
		return expressionValue();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(parameter[0].toMathML(null));
		buffer.append("<mo>&Cross;</mo>\n");
		buffer.append(parameter[1].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new TensorProduct(null,null);
	}
}
