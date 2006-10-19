package jscl.math;

import jscl.text.IndentedBuffer;

class ExpressionVariable extends GenericVariable {
	ExpressionVariable(Generic generic) {
		super(generic);
	}

	public Generic substitute(Variable variable, Generic generic) {
		if(isIdentity(variable)) return generic;
		else return content.substitute(variable,generic);
	}

	public Generic elementary() {
		return content.elementary();
	}

	public Generic simplify() {
		return content.simplify();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("(").append(content).append(")");
		return buffer.toString();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("(").append(content.toJava()).append(")");
		return buffer.toString();
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
		buffer.append("<mfenced>\n");
		buffer.append(1,content.toMathML(null));
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new ExpressionVariable(null);
	}
}
