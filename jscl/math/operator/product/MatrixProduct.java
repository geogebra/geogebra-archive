package jscl.math.operator.product;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class MatrixProduct extends VectorOperator {
	public MatrixProduct(Generic matrix1, Generic matrix2) {
		super("matrix",new Generic[] {matrix1,matrix2});
	}

	public Generic compute() {
		if(Matrix.product(parameter[0],parameter[1])) {
			return parameter[0].multiply(parameter[1]);
		}
		return expressionValue();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		buffer.append(parameter[0].toJava());
		buffer.append(".multiply(");
		buffer.append(parameter[1].toJava());
		buffer.append(")");
		return buffer.toString();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(parameter[0].toMathML(null));
		buffer.append(parameter[1].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new MatrixProduct(null,null);
	}
}
