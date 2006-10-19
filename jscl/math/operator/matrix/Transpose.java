package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.text.IndentedBuffer;

public class Transpose extends Operator {
	public Transpose(Generic matrix) {
		super("tran",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.transpose();
		}
		return expressionValue();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(bodyToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,bodyToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append("<msup>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append(1,"<mo>T</mo>\n");
		buffer.append("</msup>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Transpose(null);
	}
}
