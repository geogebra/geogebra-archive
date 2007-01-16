package jscl.math.operator.vector;

import jscl.math.Expression;
import jscl.math.Generic;
import jscl.math.Variable;
import jscl.math.operator.VectorOperator;

public class Dalembertian extends VectorOperator {
	public Dalembertian(Generic vector, Generic variable) {
		super("dalembertian",new Generic[] {vector,variable});
	}

	public Generic compute() {
		Variable variable[]=variables(parameter[1]);
		Expression expression=parameter[0].expressionValue();
		return expression.dalembertian(variable);
	}

//    protected void bodyToMathML(Element element) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        operator(element,"square");
//        parameter[0].toMathML(element,null);
//    }

	protected Variable newinstance() {
		return new Dalembertian(null,null);
	}
}
