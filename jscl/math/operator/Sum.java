package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Sum extends Operator {
	public Sum(Generic expression, Generic variable, Generic n1, Generic n2) {
		super("sum",new Generic[] {expression,variable,n1,n2});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			int n1=parameter[2].integerValue().intValue();
			int n2=parameter[3].integerValue().intValue();
			Generic a=JSCLInteger.valueOf(0);
			for(int i=n1;i<=n2;i++) {
				a=a.add(parameter[0].substitute(variable,JSCLInteger.valueOf(i)));
			}
			return a;
		} catch (NotIntegerException e) {}
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
		buffer.append("<mrow>\n");
		buffer.append(1,"<munderover>\n");
		buffer.append(2,"<mo>&sum;</mo>\n");
		buffer.append(2,"<mrow>\n");
		buffer.append(3,parameter[1].toMathML(null));
		buffer.append(3,"<mo>=</mo>\n");
		buffer.append(3,parameter[2].toMathML(null));
		buffer.append(2,"</mrow>\n");
		buffer.append(2,parameter[3].toMathML(null));
		buffer.append(1,"</munderover>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Sum(null,null,null,null);
	}
}
