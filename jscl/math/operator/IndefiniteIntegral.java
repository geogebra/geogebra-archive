package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class IndefiniteIntegral extends Operator {
	public IndefiniteIntegral(Generic expression, Generic variable) {
		super("integral",new Generic[] {expression,variable});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			return parameter[0].antiderivative(variable);
		} catch (NotIntegrableException e) {}
		return expressionValue();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		if(exponent==1) {
			buffer.append(bodyToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,"<mfenced>\n");
			buffer.append(2,bodyToMathML());
			buffer.append(1,"</mfenced>\n");
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		return buffer.toString();
	}

	String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		Variable v=parameter[1].variableValue();
		buffer.append("<mrow>\n");
		buffer.append(1,"<mo>&int;</mo>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append(1,"<mo>d</mo>\n");
		buffer.append(1,v.toMathML(null));
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new IndefiniteIntegral(null,null);
	}
}
