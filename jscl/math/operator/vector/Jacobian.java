package jscl.math.operator.vector;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLVector;
import jscl.math.Variable;
import jscl.math.function.Constant;
import jscl.math.operator.VectorOperator;
import jscl.text.IndentedBuffer;

public class Jacobian extends VectorOperator {
	public Jacobian(Generic vector, Generic variable) {
		super("jacobian",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		if(parameter[0] instanceof JSCLVector) {
			JSCLVector vector=(JSCLVector)parameter[0];
			return vector.jacobian(variable);
		}
		return expressionValue();
	}

	protected String bodyToMathML() {
		IndentedBuffer buffer=new IndentedBuffer();
		buffer.append(operator("nabla"));
		buffer.append("<msup>\n");
		buffer.append(1,parameter[0].toMathML(null));
		buffer.append(1,"<mo>T</mo>\n");
		buffer.append("</msup>\n");
		return buffer.toString();
	}

	protected String operator(String name) {
		IndentedBuffer buffer=new IndentedBuffer();
		Variable variable[]=variables(GenericVariable.content(parameter[1]));
		buffer.append("<msubsup>\n");
		buffer.append(1,new Constant(name).toMathML(null));
		buffer.append(1,"<mrow>\n");
		for(int i=0;i<variable.length;i++) {
			buffer.append(2,variable[i].expressionValue().toMathML(null));
		}
		buffer.append(1,"</mrow>\n");
		buffer.append(1,"<mo>T</mo>\n");
		buffer.append("</msubsup>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Jacobian(null,null);
	}
}
