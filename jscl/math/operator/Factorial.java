package jscl.math.operator;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotVariableException;
import jscl.math.Variable;
import jscl.math.function.Frac;
import jscl.math.function.Pow;
import jscl.text.IndentedBuffer;
import jscl.util.ArrayComparator;

public class Factorial extends Operator {
	public Factorial(Generic expression) {
		super("",new Generic[] {expression});
	}

	public Generic compute() {
		try {
			int n=parameter[0].integerValue().intValue();
			Generic a=JSCLInteger.valueOf(1);
			for(int i=0;i<n;i++) {
				a=a.multiply(JSCLInteger.valueOf(i+1));
			}
			return a;
		} catch (NotIntegerException e) {}
		return expressionValue();
	}

	public int compareTo(Variable variable) {
		if(this==variable) return 0;
		int c=comparator.compare(this,variable);
		if(c<0) return -1;
		else if(c>0) return 1;
		else {
			Factorial v=(Factorial)variable;
			return ArrayComparator.comparator.compare(parameter,v.parameter);
		}
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		try {
			JSCLInteger en=parameter[0].integerValue();
			buffer.append(en);
		} catch (NotIntegerException e) {
			try {
				Variable v=parameter[0].variableValue();
				if(v instanceof Frac || v instanceof Pow) {
					buffer.append(GenericVariable.valueOf(parameter[0]));
				} else buffer.append(v);
			} catch (NotVariableException e2) {
				buffer.append(GenericVariable.valueOf(parameter[0]));
			}
		}
		buffer.append("!");
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
		buffer.append("<mrow>\n");
		try {
			JSCLInteger en=parameter[0].integerValue();
			buffer.append(1,en.toMathML(null));
		} catch (NotIntegerException e) {
			try {
				Variable v=parameter[0].variableValue();
				if(v instanceof Pow) {
					buffer.append(1,GenericVariable.valueOf(parameter[0]).toMathML(null));
				} else buffer.append(1,v.toMathML(null));
			} catch (NotVariableException e2) {
				buffer.append(1,GenericVariable.valueOf(parameter[0]).toMathML(null));
			}
		}
		buffer.append(1,"<mo>!</mo>\n");
		buffer.append("</mrow>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Factorial(null);
	}
}
