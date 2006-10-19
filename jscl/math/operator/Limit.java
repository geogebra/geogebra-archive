package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.Variable;
import jscl.text.IndentedBuffer;

public class Limit extends Operator {
	public Limit(Generic expression, Generic variable, Generic limit, Generic direction) {
		super("lim",new Generic[] {expression,variable,limit,direction});
	}

	public Generic compute() {
		return expressionValue();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		int n=4;
		if(parameter[3].signum()==0) n=3;
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
		int c=parameter[3].signum();
		buffer.append("<mrow>\n");
		buffer.append(1,"<munder>\n");
		buffer.append(2,"<mo>lim</mo>\n");
		buffer.append(2,"<mrow>\n");
		buffer.append(3,parameter[1].toMathML(null));
		buffer.append(3,"<mo>&rarr;</mo>\n");
		if(c==0) {
			buffer.append(3,parameter[2].toMathML(null));
		} else {
			buffer.append(3,"<msup>\n");
			buffer.append(4,parameter[2].toMathML(null));
			if(c<0) buffer.append(4,"<mo>-</mo>\n");
			else if(c>0) buffer.append(4,"<mo>+</mo>\n");
			buffer.append(3,"</msup>\n");
		}
		buffer.append(2,"</mrow>\n");
		buffer.append(1,"</munder>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Limit(null,null,null,null);
	}
}
