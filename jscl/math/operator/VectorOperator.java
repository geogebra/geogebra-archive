package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.text.IndentedBuffer;

public abstract class VectorOperator extends Operator {
	public VectorOperator(String name, Generic parameter[]) {
		super(name,parameter);
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

	protected abstract String bodyToMathML();

	protected String operator(String name) {
		IndentedBuffer buffer=new IndentedBuffer();
		Variable variable[]=variables(GenericVariable.content(parameter[1]));
		buffer.append("<msub>\n");
		buffer.append(1,new Constant(name).toMathML(null));
		buffer.append(1,"<mrow>\n");
		for(int i=0;i<variable.length;i++) {
			buffer.append(2,variable[i].expressionValue().toMathML(null));
		}
		buffer.append(1,"</mrow>\n");
		buffer.append("</msub>\n");
		return buffer.toString();
	}
}
