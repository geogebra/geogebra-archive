package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.NotIntegrableException;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Integral extends Operator {
	public Integral(Generic expression, Generic variable, Generic n1, Generic n2) {
		super("integral",new Generic[] {expression,variable,n1,n2});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			Generic a=parameter[0].antiderivative(variable);
			return a.substitute(variable,parameter[3]).subtract(a.substitute(variable,parameter[2]));
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
		buffer.append(1,"<msubsup>\n");
		buffer.append(2,"<mo>&int;</mo>\n");
		buffer.append(2,parameter[2].toMathML(null));
		buffer.append(2,parameter[3].toMathML(null));
		buffer.append(1,"</msubsup>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append(1,"<mo>d</mo>\n");
		buffer.append(1,v.toMathML(null));
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Integral(null,null,null,null);
	}
}
