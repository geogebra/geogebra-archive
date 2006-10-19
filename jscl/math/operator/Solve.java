package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.UnivariatePolynomial;
import jscl.math.Variable;
import jscl.math.function.Root;
import jscl.text.IndentedBuffer;

public class Solve extends Operator {
	public Solve(Generic expression, Generic variable, Generic subscript) {
		super("solve",new Generic[] {expression,variable,subscript});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		int subscript=parameter[2].integerValue().intValue();
		if(parameter[0].isPolynomial(variable)) {
			return new Root(UnivariatePolynomial.valueOf(parameter[0],variable),subscript).evaluate();
		}
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=3;
		if(parameter[2].signum()==0) n=2;
		buffer.append(name);
		buffer.append("(");
		for(int i=0;i<n;i++) {
			buffer.append(parameter[i]).append(i<n-1?", ":"");
		}
		buffer.append(")");
		return buffer.toString();
	}

	public String toMathML(Object data) {
		IndentedBuffer buffer=new IndentedBuffer();
		int exponent=data instanceof Integer?((Integer)data).intValue():1;
		int n=3;
		if(parameter[2].signum()==0) n=2;
		if(exponent==1) {
			buffer.append(nameToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,nameToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		buffer.append("<mfenced>\n");
		for(int i=0;i<n;i++) {
			buffer.append(1,parameter[i].toMathML(null));
		}
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Solve(null,null,null);
	}
}
