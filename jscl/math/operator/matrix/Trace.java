package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.text.IndentedBuffer;

public class Trace extends Operator {
	public Trace(Generic matrix) {
		super("trace",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.trace();
		}
		return expressionValue();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append("<mo>tr</mo>\n");
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,"<mo>tr</mo>\n");
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		buffer.append(parameter[0].toMathML(null));
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Trace(null);
	}
}
