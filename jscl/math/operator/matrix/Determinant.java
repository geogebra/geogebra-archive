package jscl.math.operator.matrix;

import jscl.math.Generic;
import jscl.math.GenericVariable;
import jscl.math.Matrix;
import jscl.math.Variable;
import jscl.math.operator.Operator;
import jscl.text.IndentedBuffer;

public class Determinant extends Operator {
	public Determinant(Generic matrix) {
		super("det",new Generic[] {matrix});
	}

	public Generic compute() {
		if(parameter[0] instanceof Matrix) {
			Matrix matrix=(Matrix)parameter[0];
			return matrix.determinant();
		}
		return expressionValue();
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
		Generic m=GenericVariable.content(parameter[0]);
		buffer.append("<mfenced open=\"|\" close=\"|\">\n");
		if(m instanceof Matrix) {
			Generic element[][]=((Matrix)m).elements();
			buffer.append(1,"<mtable>\n");
			for(int i=0;i<element.length;i++) {
				buffer.append(2,"<mtr>\n");
				for(int j=0;j<element.length;j++) {
					buffer.append(3,"<mtd>\n");
					buffer.append(4,element[i][j].toMathML(null));
					buffer.append(3,"</mtd>\n");
				}
				buffer.append(2,"</mtr>\n");
			}
			buffer.append(1,"</mtable>\n");
		} else buffer.append(1,m.toMathML(null));
		buffer.append("</mfenced>\n");
		return buffer.toString();
	}

	protected Variable newinstance() {
		return new Determinant(null);
	}
}
