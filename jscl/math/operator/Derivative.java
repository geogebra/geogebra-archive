package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Derivative extends Operator {
	public Derivative(Generic expression, Generic variable, Generic value, Generic order) {
		super("d",new Generic[] {expression,variable,value,order});
	}

	public Generic compute() {
		Variable variable=parameter[1].variableValue();
		try {
			int n=parameter[3].integerValue().intValue();
			Generic a=parameter[0];
			for(int i=0;i<n;i++) {
				a=a.derivative(variable);
			}
			return a.substitute(variable,parameter[2]);
		} catch (NotIntegerException e) {}
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=4;
		if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
			n=3;
			if(parameter[2].compareTo(parameter[1])==0) n=2;
		}
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
		int n=4;
		if(parameter[3].compareTo(JSCLInteger.valueOf(1))==0) {
			n=3;
			if(parameter[2].compareTo(parameter[1])==0) n=2;
		}
		if(exponent==1) {
			buffer.append(bodyToMathML());
		} else {
			buffer.append("<msup>\n");
			buffer.append(1,bodyToMathML());
			buffer.append(1,"<mn>").append(exponent).append("</mn>\n");
			buffer.append("</msup>\n");
		}
		if(n>2) {
			buffer.append("<mfenced>\n");
			buffer.append(1,parameter[2].toMathML(null));
			buffer.append("</mfenced>\n");
		}
		return buffer.toString();
	}

	String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		Variable v=parameter[1].variableValue();
		int n=0;
		try {
			n=parameter[3].integerValue().intValue();
		} catch (NotIntegerException e) {}
		if(n==1) {
			buffer.append("<mfrac>\n");
			buffer.append(1,"<mrow>\n");
			buffer.append(2,"<mo>d</mo>\n");
			buffer.append(2,parameter[0].toMathML(null));
			buffer.append(1,"</mrow>\n");
			buffer.append(1,"<mrow>\n");
			buffer.append(2,"<mo>d</mo>\n");
			buffer.append(2,v.toMathML(null));
			buffer.append(1,"</mrow>\n");
			buffer.append("</mfrac>\n");
		} else {
			buffer.append("<mfrac>\n");
			buffer.append(1,"<mrow>\n");
			buffer.append(2,"<msup>\n");
			buffer.append(3,"<mo>d</mo>\n");
			buffer.append(3,parameter[3].toMathML(null));
			buffer.append(2,"</msup>\n");
			buffer.append(2,parameter[0].toMathML(null));
			buffer.append(1,"</mrow>\n");
			buffer.append(1,"<mrow>\n");
			buffer.append(2,"<mo>d</mo>\n");
			buffer.append(2,"<msup>\n");
			buffer.append(3,v.toMathML(null));
			buffer.append(3,parameter[3].toMathML(null));
			buffer.append(2,"</msup>\n");
			buffer.append(1,"</mrow>\n");
			buffer.append("</mfrac>\n");
		}
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Derivative(null,null,null,null);
	}
}
